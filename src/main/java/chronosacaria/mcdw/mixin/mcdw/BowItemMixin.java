/*
Timefall Development License 1.2
Copyright (c) 2020-2024. Chronosacaria, Kluzzio, Timefall Development. All Rights Reserved.

This software's content is licensed under the Timefall Development License 1.2. You can find this license information here: https://github.com/Timefall-Development/Timefall-Development-Licence/blob/main/TimefallDevelopmentLicense1.2.txt
*/
package chronosacaria.mcdw.mixin.mcdw;

import chronosacaria.mcdw.Mcdw;
import chronosacaria.mcdw.api.interfaces.IMcdwEnchantedArrow;
import chronosacaria.mcdw.api.util.CleanlinessHelper;
import chronosacaria.mcdw.api.util.ProjectileEffectHelper;
import chronosacaria.mcdw.api.util.RangedAttackHelper;
import chronosacaria.mcdw.bases.McdwBow;
import chronosacaria.mcdw.bases.McdwLongbow;
import chronosacaria.mcdw.bases.McdwShortbow;
import chronosacaria.mcdw.enums.EnchantmentsID;
import chronosacaria.mcdw.registries.EnchantsRegistry;
import chronosacaria.mcdw.registries.StatusEffectsRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BowItem.class)
public abstract class BowItemMixin{

    @Unique
    private int overcharge;

    @Unique
    private LivingEntity livingEntity;

    @Unique
    public void setLivingEntity(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    public void mcdw$onStoppedUsingBow(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci){
        if (Mcdw.CONFIG.mcdwEnchantmentsConfig.ENCHANTMENT_CONFIG.get(EnchantmentsID.BONUS_SHOT).mcdw$getIsEnabled()){
            int bonusShotLevel = EnchantmentHelper.getLevel(EnchantsRegistry.enchantments.get(EnchantmentsID.BONUS_SHOT), stack);
            if (bonusShotLevel > 0){
                float damageMultiplier = 0.03F + (bonusShotLevel * 0.07F);
                float arrowVelocity = RangedAttackHelper.getVanillaOrModdedBowArrowVelocity(stack, remainingUseTicks);
                if (arrowVelocity >= 0.1F){
                    ProjectileEffectHelper.mcdw$spawnExtraArrows(user, user, 1, 10, damageMultiplier);
                }
            }
        }
        if (Mcdw.CONFIG.mcdwEnchantmentsConfig.ENCHANTMENT_CONFIG.get(EnchantmentsID.MULTI_SHOT).mcdw$getIsEnabled()) {
            int multiShotLevel = EnchantmentHelper.getLevel(EnchantsRegistry.enchantments.get(EnchantmentsID.MULTI_SHOT), stack);
            if (multiShotLevel > 0) {
                PersistentProjectileEntity projectile = ProjectileEffectHelper.mcdw$createAbstractArrow(user);
                LivingEntity target = user.getAttacking();
                if (target != null) { // \/\/ Taken from AbstractSkeletonEntity
                    double d = target.getX() - user.getX();
                    double e = target.getBodyY(0.3333333333333333D) - projectile.getY();
                    double f = target.getZ() - user.getZ();
                    double g = MathHelper.sqrt((float) (d * d + f * f));
                    projectile.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float) (14 - user.getWorld().getDifficulty().getId() * 4));
                    projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    world.spawnEntity(projectile);
                }
            }
        }
    }

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile" +
            "/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void mcdw$applyBowEnchantmentLevel(ItemStack stack, World world, LivingEntity user, int remainingUseTicks,
                                 CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2,
                                               ArrowItem arrowItem, PersistentProjectileEntity ppe){

        // Not the level of Overcharge
        if (overcharge > 0) {
            ((IMcdwEnchantedArrow)ppe).mcdw$setOvercharge(overcharge);
        }
        CleanlinessHelper.addPPEEnchantments(stack, (IMcdwEnchantedArrow) ppe);
    }

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getMaxUseTime(Lnet/minecraft/item/ItemStack;)I"))
    private void mcdw$livingEntityGetter(ItemStack stack, World world, LivingEntity user,
                                                int remainingUseTicks, CallbackInfo ci){
        this.setLivingEntity(user);
    }

    @SuppressWarnings("lossy-conversions")
    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"), index = 0)
    private int mcdw$acceleratedPullProgress(int value){
        ItemStack bowStack = livingEntity.getActiveItem();

        if (bowStack.getItem() instanceof McdwShortbow mcdwShortBow) {
            value /= (mcdwShortBow.getDrawSpeed() / 20);
        } else if (bowStack.getItem() instanceof McdwLongbow mcdwLongBow) {
            value /= (mcdwLongBow.getDrawSpeed() / 20);
        } else if (bowStack.getItem() instanceof McdwBow mcdwBow) {
            value /= (mcdwBow.getDrawSpeed() / 20);
        }

        if (Mcdw.CONFIG.mcdwEnchantmentsConfig.ENCHANTMENT_CONFIG.get(EnchantmentsID.ACCELERATE).mcdw$getIsEnabled()) {
            int accelerateLevel = EnchantmentHelper.getLevel(EnchantsRegistry.enchantments.get(EnchantmentsID.ACCELERATE), bowStack);
            if (accelerateLevel > 0) {
                StatusEffectInstance accelerateInstance = livingEntity.getStatusEffect(StatusEffectsRegistry.ACCELERATE);
                int consecutiveShots = accelerateInstance != null ? accelerateInstance.getAmplifier() + 1 : 0;
                value = (int) (value * (1f + (MathHelper.clamp(consecutiveShots * (6.0f + 2.0f * accelerateLevel), 0f, 100f) / 100f)));

                if (BowItem.getPullProgress(value) >= 1) {
                    StatusEffectInstance accelerateUpdateInstance =
                            new StatusEffectInstance(StatusEffectsRegistry.ACCELERATE, 60, consecutiveShots, false, false, true);
                    livingEntity.addStatusEffect(accelerateUpdateInstance);
                }
            }
        }

        if (Mcdw.CONFIG.mcdwEnchantmentsConfig.ENCHANTMENT_CONFIG.get(EnchantmentsID.OVERCHARGE).mcdw$getIsEnabled()) {
            int overchargeLevel = EnchantmentHelper.getLevel(EnchantsRegistry.enchantments.get(EnchantmentsID.OVERCHARGE), bowStack);
            if (overchargeLevel > 0) {
                overcharge = Math.min((value / 20) - 1, overchargeLevel);
                value = overcharge == overchargeLevel ? value : value % 20;
            }
        }
        return value;
    }

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private void mcdw$rangeHandler(Args args) {
        float velocity = args.get(4);
        ItemStack bowStack = livingEntity.getActiveItem();

        if (bowStack.getItem() instanceof McdwShortbow mcdwShortBow) {
            velocity *= mcdwShortBow.getRange() / 15f;
        } else if (bowStack.getItem() instanceof McdwLongbow mcdwLongBow) {
            velocity *= mcdwLongBow.getRange() / 15f;
        } else if (bowStack.getItem() instanceof McdwBow mcdwBow) {
            velocity *= mcdwBow.getRange() / 15f;
        }

        args.set(4, velocity);
    }
}