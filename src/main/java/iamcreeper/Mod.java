package iamcreeper;

import iamcreeper.mixin.AddPotionRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

import java.util.*;

public class Mod implements ModInitializer {

    public static final String ID = "iamcreeper";
    public static final StatusEffect CREEPER = new CreeperEffect(StatusEffectCategory.HARMFUL, 5797459);
    public static final Item CREEPER_ESSENCE_ITEM = new CreeperEssenceItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(16));
    public static final Potion CREEPER_ESSENCE_WEAK_POTION = new Potion(new StatusEffectInstance(CREEPER, 20 * 60, 0, true, false, true));
    public static final Potion CREEPER_ESSENCE_LONG_POTION = new Potion(new StatusEffectInstance(CREEPER, 20 * 60 * 4, 0, true, false, true));
    public static final Potion CREEPER_ESSENCE_STRONG_POTION = new Potion(new StatusEffectInstance(CREEPER, 20 * 30, 1, true, false, true));

    @Override
    public void onInitialize() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(ID, "creeper"), CREEPER);
        Registry.register(Registry.ITEM, new Identifier(ID, "creeper_essence"), CREEPER_ESSENCE_ITEM);
        Registry.register(Registry.POTION, new Identifier(ID, "creeper_essence_weak"), CREEPER_ESSENCE_WEAK_POTION);
        Registry.register(Registry.POTION, new Identifier(ID, "creeper_essence_long"), CREEPER_ESSENCE_LONG_POTION);
        Registry.register(Registry.POTION, new Identifier(ID, "creeper_essence_strong"), CREEPER_ESSENCE_STRONG_POTION);
        AddPotionRecipes.invokeRegisterPotionRecipe(Potions.MUNDANE, Mod.CREEPER_ESSENCE_ITEM, Mod.CREEPER_ESSENCE_WEAK_POTION);
        AddPotionRecipes.invokeRegisterPotionRecipe(Mod.CREEPER_ESSENCE_WEAK_POTION, Items.GLOWSTONE_DUST, Mod.CREEPER_ESSENCE_LONG_POTION);
        AddPotionRecipes.invokeRegisterPotionRecipe(Potions.THICK, Mod.CREEPER_ESSENCE_ITEM, Mod.CREEPER_ESSENCE_STRONG_POTION);
        FuelRegistry.INSTANCE.add(CREEPER_ESSENCE_ITEM, 300);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                if (((PlayerInterface) serverPlayerEntity).isCreeper()) {
                    Data data = Mod.get(serverPlayerEntity);
                    data.tick(serverPlayerEntity);
                }
            });
        });
    }

    public static Map<UUID, Data> map = new HashMap<>();
    public static Map<UUID, Data> mapLocal = new HashMap<>();

    public static float getClientFuseTime(LivingEntity entity, float timeDelta) {
        Mod.Data data = Mod.get(entity);
        return MathHelper.lerp(timeDelta, data.lastFuseTime, data.currentFuseTime) / (float) (data.fuseTime - 2);
    }

    public static Data get(Entity entity) {
        if (entity.world.isClient()) {
            if (!mapLocal.containsKey(entity.getUuid())) {
                mapLocal.put(entity.getUuid(), new Data());
            }
            return mapLocal.get(entity.getUuid());
        } else {
            if (!map.containsKey(entity.getUuid())) {
                map.put(entity.getUuid(), new Data());
            }
            return map.get(entity.getUuid());
        }
    }

    public static class Data {

        public float explosionRadius = 3F;
        public float lastFuseTime;
        public float currentFuseTime;
        public int fuseTime = 30;

        public void tick(PlayerEntity player) {
            if (player.isAlive()) {
                PlayerInterface playerInterface = (PlayerInterface) player;

                int i;
                this.lastFuseTime = this.currentFuseTime;
                if (playerInterface.isIgnited()) {
                    playerInterface.setFuseSpeed(1);
                }
                if ((i = playerInterface.getFuseSpeed()) > 0 && this.currentFuseTime == 0) {
                    player.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
                    player.emitGameEvent(GameEvent.PRIME_FUSE);
                }
                this.currentFuseTime += i;
                if (this.currentFuseTime < 0) {
                    this.currentFuseTime = 0;
                }
                if (this.currentFuseTime >= this.fuseTime) {
                    this.currentFuseTime = this.fuseTime;
                    this.explode(player, playerInterface);
                    this.lastFuseTime = 0;
                    this.currentFuseTime = 0;
                }

                if (!player.world.isClient()) {
                    this.friendly(player, playerInterface);
                }
            }
        }

        private void friendly(PlayerEntity player, PlayerInterface playerInterface) {
            if(playerInterface.isCreeper()) {
                List<MobEntity> otherEntities = player
                        .getEntityWorld()
                        .getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), player.getBoundingBox().expand(10), e -> e instanceof Monster);
                for (MobEntity otherEntity : otherEntities) {
                    if(otherEntity.getTarget() == player) {
                        otherEntity.setTarget(null);
                    }
                }
            }

            if (player.isSneaking() && player.isAlive()) {
                playerInterface.setFuseSpeed(1);
            } else {
                if (player.isOnFire() && player.isAlive()) {
                    playerInterface.ignite();
                } else {
                    playerInterface.setFuseSpeed(-1);
                }
            }
        }

        private void explode(PlayerEntity player, PlayerInterface playerInterface) {
            if (!player.world.isClient) {
                Explosion.DestructionType destructionType = player.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
                float f = playerInterface.isCharged() ? 2.0f : 1.0f;
                player.world.createExplosion(player, player.getX(), player.getY(), player.getZ(), this.explosionRadius * f, destructionType);
                player.kill();
                this.spawnEffectsCloud(player);
            }
        }

        private void spawnEffectsCloud(PlayerEntity player) {
            Collection<StatusEffectInstance> collection = player.getStatusEffects().stream()
                    .filter(statusEffectInstance -> !statusEffectInstance.isAmbient()).toList();
            if (!collection.isEmpty()) {
                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(player.world, player.getX(), player.getY(), player.getZ());
                areaEffectCloudEntity.setRadius(2.5f);
                areaEffectCloudEntity.setRadiusOnUse(-0.5f);
                areaEffectCloudEntity.setWaitTime(10);
                areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
                areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
                for (StatusEffectInstance statusEffectInstance : collection) {
                    areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
                }
                player.world.spawnEntity(areaEffectCloudEntity);
            }
        }
    }
}
