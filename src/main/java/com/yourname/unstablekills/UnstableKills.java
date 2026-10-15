package com.yourname.unstablekills;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Главный класс мода.
 * Здесь регистрируется слушатель чата и хранится список недавно умерших игроков.
 */
public class UnstableKills implements ClientModInitializer {

    // Карта: UUID игрока -> время смерти (в миллисекундах)
    public static final Map<UUID, Long> RECENT_DEATHS = new HashMap<>();

    // Сколько миллисекунд считаем смерть "свежей" (5 секунд)
    private static final long DEATH_TIMEOUT_MS = 5000L;

    @Override
    public void onInitializeClient() {
        // Регистрируем событие: перехватываем каждое сообщение в чате
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            String raw = message.getString();

            // Проверяем, похоже ли сообщение на ванильное сообщение о смерти
            if (!looksLikeDeathMessage(raw)) {
                return true; // не наше — пропускаем дальше
            }

            // Ищем в списке недавно умерших того, чья смерть была подтверждена пакетом
            UUID confirmed = findConfirmedDeath();

            if (confirmed != null) {
                // Реальная смерть. Показываем жёлтое сообщение "left the game".
                String name = resolveName(confirmed);
                Text fakeLeave = Text.literal("§e" + name + " left the game");
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(fakeLeave);

                // Убираем из списка, чтобы не сработало повторно
                RECENT_DEATHS.remove(confirmed);

                // Отменяем оригинальное ванильное сообщение (чтобы не дублировать)
                return false;
            } else {
                // Смерть не подтверждена пакетом — значит сервер подделал сообщение.
                Text warn = Text.literal("§c[FAKE DEATH] ")
                        .append(message)
                        .append(Text.literal(" §c(не подтверждено!)"));
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(warn);

                // Оригинал тоже отменяем, чтобы игрок видел только наше предупреждение
                return false;
            }
        });
    }

    /**
     * Грубая проверка: похоже ли сообщение на смерть.
     * Ванильные сообщения содержат "slain", "died", "withered", "drowned", "blown up", "burned" и т.д.
     */
    private static boolean looksLikeDeathMessage(String text) {
        String lower = text.toLowerCase();
        return lower.contains("slain")
                || lower.contains("died")
                || lower.contains("withered")
                || lower.contains("drowned")
                || lower.contains("blown up")
                || lower.contains("burned")
                || lower.contains("fell")
                || lower.contains("was killed")
                || lower.contains("hit the ground")
                || lower.contains("starved")
                || lower.contains("suffocated")
                || lower.contains("froze");
    }

    /**
     * Ищем в карте запись, которая ещё "жива" (не старше 5 секунд).
     */
    private static UUID findConfirmedDeath() {
        long now = System.currentTimeMillis();
        UUID found = null;
        for (Map.Entry<UUID, Long> e : RECENT_DEATHS.entrySet()) {
            if (now - e.getValue() <= DEATH_TIMEOUT_MS) {
                found = e.getKey();
                break;
            }
        }
        return found;
    }

    /**
     * Пытаемся получить имя игрока по UUID из списка игроков на сервере.
     * Если не удалось — возвращаем "Unknown".
     */
    private static String resolveName(UUID uuid) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
            if (entry != null && entry.getProfile() != null) {
                return entry.getProfile().getName();
            }
        }
        return "Unknown";
    }

    /**
     * Вызывается из миксина, когда пакет смерти пришёл.
     */
    public static void registerDeath(UUID uuid) {
        RECENT_DEATHS.put(uuid, System.currentTimeMillis());
    }
}
