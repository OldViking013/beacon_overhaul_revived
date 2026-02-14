package com.oldviking.beacon_overhaul_revived.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Invoker("setTooltip")
    void invokeSetTooltip(@Nullable Tooltip tooltip);
}
