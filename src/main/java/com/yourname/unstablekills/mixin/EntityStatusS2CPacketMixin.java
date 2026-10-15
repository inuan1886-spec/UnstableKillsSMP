package com.yourname.unstablekills.mixin;

import com.yourname.unstablekills.UnstableKills;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class EntityStatusS2CPacketMixin {
    // Сигнатура метода onEntityStatus могла измениться
    // Открой ClientPacketListener в 26.2 и найди метод, который принимает EntityStatusS2CPacket
}
