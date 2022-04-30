package iamcreeper;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

public class CreeperEssenceItem extends Item {

    private static final FoodComponent FOOD = new FoodComponent.Builder()
            .hunger(2)
            .saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(Mod.CREEPER, 20 * 30, 0, true, false, true), 1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20, 0, true, false, true), 0.2f)
            .alwaysEdible()
            .build();

    public CreeperEssenceItem(Settings settings) {
        super(settings.food(FOOD));
    }
}
