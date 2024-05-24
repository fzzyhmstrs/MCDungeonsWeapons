/*
 * Timefall Development License 1.2
 * Copyright (c) 2020-2024. Chronosacaria, Kluzzio, Timefall Development. All Rights Reserved.
 *
 * This software's content is licensed under the Timefall Development License 1.2. You can find this license information here: https://github.com/Timefall-Development/Timefall-Development-Licence/blob/main/TimefallDevelopmentLicense1.2.txt
 */
package dev.timefall.mcdw.bases;

import dev.timefall.mcdw.api.interfaces.IOffhandAttack;
import dev.timefall.mcdw.api.util.CleanlinessHelper;
import dev.timefall.mcdw.configs.stats.IMcdwWeaponStats;
import dev.timefall.mcdw.registries.ItemGroupRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class McdwGauntletItem extends SwordItem implements IOffhandAttack {
    private final ToolMaterial material;
    IMcdwWeaponStats.MeleeStats itemStats;

    public McdwGauntletItem(IMcdwWeaponStats.MeleeStats itemStats, ToolMaterial material, Item.Settings settings) {
        super(material, settings);
        this.itemStats = itemStats;
        this.material = material;

        ItemGroupEvents.modifyEntriesEvent(ItemGroupRegistry.MELEE).register(entries -> entries.add(this.getDefaultStack()));
    }

    @Override
    public ToolMaterial getMaterial() {
        return this.material;
    }

    @Override
    public int getEnchantability() {
        return this.material.getEnchantability();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return itemStats.repairIngredient.toIngredient().test(ingredient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
        return useOffhand(worldIn, playerIn, handIn);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, tooltipContext, tooltip, type);
        CleanlinessHelper.mcdw$tooltipHelper(stack, tooltip, 19);
    }
}