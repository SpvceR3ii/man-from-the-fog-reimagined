package com.zen.fogman.common.item.custom;

import com.zen.fogman.common.other.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class TearOfTheManItem extends Item {

    private boolean hasEffects = false;

    public TearOfTheManItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (entity.isPlayer() && entity instanceof ServerPlayerEntity player) {
                if (selected || player.getOffHandStack() == stack) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Util.secToTick(12)));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Util.secToTick(12)));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}