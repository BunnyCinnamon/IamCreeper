package iamcreeper.mixin;

import iamcreeper.PlayerInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends Entity implements PlayerInterface {

    @Unique
    private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final TrackedData<Boolean> CREEPER = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public PlayerMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At(value = "RETURN"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(FUSE_SPEED, -1);
        this.dataTracker.startTracking(CHARGED, false);
        this.dataTracker.startTracking(IGNITED, false);
        this.dataTracker.startTracking(CREEPER, false);
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        super.onStruckByLightning(world, lightning);
        if(isCreeper()) {
            this.dataTracker.set(CHARGED, true);
        }
    }

    public boolean isCreeper() {
        return this.dataTracker.get(CREEPER);
    }

    public void setCreeper(boolean creeper) {
        this.dataTracker.set(CREEPER, creeper);
    }

    public boolean isIgnited() {
        return this.dataTracker.get(IGNITED);
    }

    public int getFuseSpeed() {
        return this.dataTracker.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int fuseSpeed) {
        this.dataTracker.set(FUSE_SPEED, fuseSpeed);
    }

    public void ignite() {
        this.dataTracker.set(IGNITED, true);
    }

    public boolean isCharged() {
        return this.dataTracker.get(CHARGED);
    }

    public void setCharged(boolean shouldRenderOverlay) {
        this.dataTracker.set(CHARGED, shouldRenderOverlay);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "RETURN"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setCreeper(nbt.getBoolean("Creeper"));
        this.setCharged(nbt.getBoolean("Charged"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "RETURN"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("Creeper", this.isCreeper());
        nbt.putBoolean("Charged", this.isCharged());
    }
}
