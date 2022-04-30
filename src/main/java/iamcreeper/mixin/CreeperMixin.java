package iamcreeper.mixin;

import iamcreeper.Mod;
import iamcreeper.PlayerInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends MobEntity {

    @Shadow public abstract void setTarget(@Nullable LivingEntity target);

    protected CreeperMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "setTarget", at = @At(value = "TAIL"))
    public void setTargetYep(CallbackInfo ci) {
        if(getTarget() instanceof PlayerInterface && ((PlayerInterface) getTarget()).isCreeper()) {
            setTarget(null);
        }
    }

    @Inject(method = "explode", at = @At(value = "TAIL"))
    public void explode(CallbackInfo ci) {
        if (!this.world.isClient && random.nextBoolean()) {
            ItemEntity entity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(Mod.CREEPER_ESSENCE_ITEM));
            getWorld().spawnEntity(entity);
        }
    }
}
