/*
Timefall Development License 1.2
Copyright (c) 2020-2024. Chronosacaria, Kluzzio, Timefall Development. All Rights Reserved.

This software's content is licensed under the Timefall Development License 1.2. You can find this license information here: https://github.com/Timefall-Development/Timefall-Development-Licence/blob/main/TimefallDevelopmentLicense1.2.txt
*/
package chronosacaria.mcdw.registries;

import chronosacaria.mcdw.Mcdw;
import chronosacaria.mcdw.enchants.summons.entity.SummonedBeeEntity;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Map;

public class SummonedEntityRegistry {
    public static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> ATTRIBUTES =
            Maps.newHashMap();

    public static final EntityType<SummonedBeeEntity> SUMMONED_BEE_ENTITY =
            FabricEntityTypeBuilder
                    .create(SpawnGroup.CREATURE, SummonedBeeEntity::new)
                    .dimensions(EntityDimensions.fixed(1,2))
                    .build();

    public static void register(){
        registerEntity(
                "summoned_bee",
                SUMMONED_BEE_ENTITY,
                SummonedBeeEntity.createSummonedBeeEntityAttributes());
    }

    public static void registerEntity(String name, EntityType<? extends LivingEntity> entity, DefaultAttributeContainer.Builder attributes){
        Registry.register(Registries.ENTITY_TYPE, Mcdw.ID(name), entity);
        ATTRIBUTES.put(entity, attributes.build());
    }

}
