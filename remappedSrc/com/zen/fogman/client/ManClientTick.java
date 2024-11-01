package com.zen.the_fog.client;

import com.zen.the_fog.common.entity.ModEntities;
import com.zen.the_fog.common.entity.the_man.TheManEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;

import java.util.List;

public class ManClientTick implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.world == null) {
            return;
        }

        if (client.player == null) {
            return;
        }

        List<TheManEntity> theManEntities = client.world.getEntitiesByType(
                ModEntities.THE_MAN,
                Box.of(
                        client.player.getPos(),
                        TheManEntity.MAN_CHASE_DISTANCE * 1.5,
                        TheManEntity.MAN_CHASE_DISTANCE * 1.5,
                        TheManEntity.MAN_CHASE_DISTANCE * 1.5
                ),
                EntityPredicates.VALID_ENTITY
        );

        theManEntities.forEach(theManEntity -> {
            theManEntity.clientTick(client);
        });
    }
}
