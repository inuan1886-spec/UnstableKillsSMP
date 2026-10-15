package com.yourname.unstablekills.mixin;

import com.yourname.unstablekills.UnstableKills;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Перехватываем пакет статуса сущности.
 * Статус 3 = смерть. Ловим его ДО того, как ванильный код начнёт обрабатывать.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class EntityStatusS2CPacketMixin {

    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void unstablekills$onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        // Нас интересует только статус 3 (death)
        if (packet.getStatus() != 3) return;

        // Получаем сущность, к которой относится пакет
        Entity entity = packet.getEntity(
                net.minecraft.client.MinecraftClient.getInstance().world
        );
        if (entity == null) return;

        // Регистрируем смерть — записываем UUID и текущее время
        UnstableKills.registerDeath(entity.getUuid());
    }
}
