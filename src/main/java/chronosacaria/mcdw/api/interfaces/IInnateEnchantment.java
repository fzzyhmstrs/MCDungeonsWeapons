/*
Timefall Development License 1.2
Copyright (c) 2020-2024. Chronosacaria, Kluzzio, Timefall Development. All Rights Reserved.

This software's content is licensed under the Timefall Development License 1.2. You can find this license information here: https://github.com/Timefall-Development/Timefall-Development-Licence/blob/main/TimefallDevelopmentLicense1.2.txt
*/
package chronosacaria.mcdw.api.interfaces;

import chronosacaria.mcdw.Mcdw;
import chronosacaria.mcdw.enums.SettingsID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IInnateEnchantment {
    /**
     * Copyright 2023 DaFuqs
     * <br/><br/>
     * Used with Permission
     * <br/><br/>
     * The following code is from Spectrum and can be found here:<br/>
     * <a href = "https://github.com/DaFuqs/Spectrum/blob/1.19-deeper-down/src/main/java/de/dafuqs/spectrum/items/Preenchanted.java#L11">Preenchanted#getDefaultEnchantments</a>
     */

    Map<Enchantment, Integer> getInnateEnchantments();

    /**
     * Copyright 2023 DaFuqs
     * <br/><br/>
     * Used with Permission, modifications made to allow for checking whether innate enchantments are enabled or not.
     * <br/><br/>
     * The following code is from Spectrum and can be found here:<br/>
     * <a href = "https://github.com/DaFuqs/Spectrum/blob/1.19-deeper-down/src/main/java/de/dafuqs/spectrum/items/Preenchanted.java#L13">Preenchanted#getDefaultEnchantedStack</a>
     */
    default ItemStack getInnateEnchantedStack(Item item) {
        ItemStack itemStack = new ItemStack(item);
        if (Mcdw.CONFIG.mcdwEnchantmentSettingsConfig.ENABLE_ENCHANTMENT_SETTINGS.get(SettingsID.ENABLE_INNATE_ENCHANTMENTS)) {
            Map<Enchantment, Integer> map = getInnateEnchantments();
            if (map != null) {
                for (Enchantment enchantment : map.keySet())
                    itemStack.addEnchantment(enchantment, map.get(enchantment));
            }
        }
        return itemStack;
    }
    
    /**
     * Checks a stack if it only has enchantments that are lower or equal its InnateEnchantments,
     * meaning enchantments had been added on top of the innate ones.
     * Copyright 2023 DaFuqs
     * <br/><br/>
     * Used with Permission, modifications made to allow for checking whether innate enchantments are enabled or not.
     * <br/><br/>
     * The following code is from Spectrum and can be found here:<br/>
     * <a href = "https://github.com/DaFuqs/Spectrum/blob/1.19-deeper-down/src/main/java/de/dafuqs/spectrum/items/Preenchanted.java#L13">Preenchanted#onlyHasPreEnchantments</a>
     */
    default boolean onlyHasInnateEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> innateEnchantments = getInnateEnchantments();
        Map<Enchantment, Integer> stackEnchantments = EnchantmentHelper.get(stack);

        if (innateEnchantments == null)
            return false;
        
        for(Map.Entry<Enchantment, Integer> stackEnchantment : stackEnchantments.entrySet()) {
            int innateLevel = innateEnchantments.getOrDefault(stackEnchantment.getKey(), 0);
            if(stackEnchantment.getValue() > innateLevel) {
                return false;
            }
        }
        
        return true;
    }
    
}
