package cyborgcabbage.amethystgravity.gravity;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public record GravityEffect(Direction direction, double volume, BlockPos source) {

    public static Box getGravityEffectCollider(PlayerEntity player){
        var d = player.getDimensions(player.getPose());
        double hw = d.width / 2.0;
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(hw, 0.6, hw, GravityChangerAPI.getGravityDirection(player));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-hw, 0.0, -hw, GravityChangerAPI.getGravityDirection(player));
        return new Box(pos1, pos2).offset(player.getPos());
    }

    public static Box getLowerGravityEffectCollider(PlayerEntity player){
        var d = player.getDimensions(player.getPose());
        double hw = d.width / 2.0;
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(hw, -0.1, hw, GravityChangerAPI.getGravityDirection(player));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-hw, 0.0, -hw, GravityChangerAPI.getGravityDirection(player));
        return new Box(pos1, pos2).offset(player.getPos());
    }

    public static void applyGravityEffectToPlayers(GravityEffect gravityEffect, Box box, World world, boolean opposite, List<Direction> directions, boolean lower){
        List<ClientPlayerEntity> playerEntities = world.getEntitiesByClass(ClientPlayerEntity.class, box.expand(0.5), e -> true);
        for (ClientPlayerEntity player : playerEntities) {
            Vec3d boxCentre = box.getCenter();
            Vec3d playerCentre = getGravityOrigin(player);
            Optional<Direction> optionalEffectiveDirection = directions.stream()
                    .max(Comparator.comparingDouble(d -> boxCentre.add(new Vec3d(d.getUnitVector())).distanceTo(playerCentre)));
            if(optionalEffectiveDirection.isEmpty()) return;
            Direction effectiveDirection = optionalEffectiveDirection.get();
            if(opposite) effectiveDirection = effectiveDirection.getOpposite();
            gravityEffect = new GravityEffect(effectiveDirection, gravityEffect.volume(), gravityEffect.source());
            //Get player collider for gravity effects
            Box gravityEffectCollider = (gravityEffect.direction().getOpposite() == GravityChangerAPI.getGravityDirection(player)) ? player.getBoundingBox() : GravityEffect.getGravityEffectCollider(player);
            Box lowerGravityEffectCollider = GravityEffect.getLowerGravityEffectCollider(player);
            //Check if the player's rotation box is colliding with this gravity plates area of effect
            if (box.intersects(gravityEffectCollider))
                ((GravityData) player).getFieldList().add(gravityEffect);
            if (lower && box.intersects(lowerGravityEffectCollider))
                ((GravityData) player).getLowerFieldList().add(gravityEffect);
        }
    }

    public static Vec3d getGravityOrigin(PlayerEntity player){
        var dim = player.getDimensions(player.getPose());
        return player.getPos().add(RotationUtil.vecPlayerToWorld(0.0, dim.width / 2.0, 0.0, GravityChangerAPI.getGravityDirection(player)));
    }
}
