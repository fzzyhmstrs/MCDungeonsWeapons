package chronosacaria.mcdw.statuseffects;

import chronosacaria.mcdw.Mcdw;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PainCycleStatusEffect extends StatusEffect {
    public PainCycleStatusEffect(StatusEffectCategory statusEffectCategory, int color, String id) {
        super(statusEffectCategory, color);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Mcdw.MOD_ID, id), this);
    }

    private int painCounter = 0;

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        painCounter++;
        if (painCounter == 300) {
            entity.damage(entity.getWorld().getDamageSources().magic(), 2);
            painCounter = 0;
        }
    }
}
