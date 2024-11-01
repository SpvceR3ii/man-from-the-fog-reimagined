package com.zen.the_fog.common.server;

import com.zen.the_fog.common.entity.ModEntities;
import com.zen.the_fog.common.entity.the_man.TheManEntity;
import com.zen.the_fog.common.entity.the_man.TheManUtils;
import com.zen.the_fog.common.gamerules.ModGamerules;
import com.zen.the_fog.common.other.MathUtils;
import com.zen.the_fog.common.sounds.ModSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ManWorldEvents implements ServerWorldEvents.Load, ServerTickEvents.EndWorldTick {

    public static final float MAN_CREEPY_VOLUME = 5f;

    public static long spawnCooldown = MathUtils.secToTick(15.0);

    public static Random random = new Random();

    @Nullable
    public static ServerPlayerEntity getRandomAlivePlayer(ServerWorld serverWorld) {
        List<ServerPlayerEntity> list = serverWorld.getPlayers(entity -> entity.isAlive() && entity.method_48926().getRegistryKey() == World.OVERWORLD && !entity.isSpectator() && !entity.isCreative());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Generates a random position around position
     * @param serverWorld The World
     * @param position Position to generate around
     * @param minRange Minimum range to generate
     * @param maxRange Maximum range to generate
     * @return The generated position
     */
    public static Vec3d getRandomSpawnPositionAtPoint(ServerWorld serverWorld, BlockPos position, int minRange, int maxRange) {
        int xOffset = (random.nextBoolean() ? 1 : -1) * random.nextInt(minRange,maxRange);
        int zOffset = (random.nextBoolean() ? 1 : -1) * random.nextInt(minRange,maxRange);
        return serverWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE,position.add(xOffset,0,zOffset)).toCenterPos();
    }

    /**
     * Generates a random position around position
     * @param serverWorld The World
     * @param position Position to generate around
     * @return The generated position
     */
    public static Vec3d getRandomSpawnPositionAtPoint(ServerWorld serverWorld, BlockPos position) {
        int xOffset = (random.nextBoolean() ? 1 : -1) * random.nextInt(20,60);
        int zOffset = (random.nextBoolean() ? 1 : -1) * random.nextInt(20,60);
        return serverWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE,position.add(xOffset,0,zOffset)).toCenterPos();
    }

    public static void spawnMan(ServerWorld serverWorld) {
        ServerPlayerEntity player = getRandomAlivePlayer(serverWorld);
        if (player == null) {
            return;
        }
        Vec3d spawnPosition = getRandomSpawnPositionAtPoint(serverWorld,player.getBlockPos());
        TheManEntity man = new TheManEntity(ModEntities.THE_MAN,serverWorld);
        man.setPosition(spawnPosition);
        man.setTarget(player);
        serverWorld.spawnEntity(man);
    }

    public static void playCreepySound(ServerWorld serverWorld) {
        ServerPlayerEntity player = getRandomAlivePlayer(serverWorld);
        if (player == null) {
            return;
        }
        Vec3d soundPosition = getRandomSpawnPositionAtPoint(serverWorld,player.getBlockPos());
        serverWorld.playSound(null,soundPosition.getX(),soundPosition.getY(),soundPosition.getZ(), ModSounds.MAN_CREEPY, SoundCategory.AMBIENT,MAN_CREEPY_VOLUME,1f);
    }

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld serverWorld) {
        if (serverWorld.getRegistryKey() != World.OVERWORLD) {
            return;
        }

        spawnCooldown = MathUtils.secToTick(serverWorld.getGameRules().get(ModGamerules.MAN_SPAWN_INTERVAL).get());
    }

    @Override
    public void onEndTick(ServerWorld serverWorld) {
        if (serverWorld.isClient()) {
            return;
        }
        if (serverWorld.isDay()) {
            return;
        }
        if (TheManUtils.manExists(serverWorld) || TheManUtils.hallucinationsExists(serverWorld)) {
            return;
        }

        GameRules gameRules = serverWorld.getGameRules();

        if (--spawnCooldown <= 0L) {

            int spawnChanceMul = gameRules.getBoolean(ModGamerules.MAN_SPAWN_CHANCE_SCALES) ? serverWorld.getPlayers().size() : 1;

            if (Math.random() < gameRules.get(ModGamerules.MAN_SPAWN_CHANCE).get() * spawnChanceMul) {
                if (Math.random() < gameRules.get(ModGamerules.MAN_AMBIENT_SOUND_CHANCE).get()) {
                    playCreepySound(serverWorld);
                } else {
                    spawnMan(serverWorld);
                }
            }

            spawnCooldown = MathUtils.secToTick(gameRules.get(ModGamerules.MAN_SPAWN_INTERVAL).get());
        }
    }
}
