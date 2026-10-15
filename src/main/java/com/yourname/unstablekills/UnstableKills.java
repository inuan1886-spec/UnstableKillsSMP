package com.yourname.unstablekills;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnstableKills implements ClientModInitializer {
    public static final Map<UUID, Long> RECENT_DEATHS = new HashMap<>();
    private static final long TIMEOUT = 5000L;

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signed, sender, params, timestamp) -> {
            String raw = message.getString();
            // логика проверки смерти — ДОПИШИ САМ, имена методов могли измениться
            return true;
        });
    }

    public static void registerDeath(UUID uuid) {
        RECENT_DEATHS.put(uuid, System.currentTimeMillis());
    }
}
    public static void registerDeath(UUID uuid) {
        RECENT_DEATHS.put(uuid, System.currentTimeMillis());
    }
}
