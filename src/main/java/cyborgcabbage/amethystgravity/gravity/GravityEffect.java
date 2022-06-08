package cyborgcabbage.amethystgravity.gravity;

import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public record GravityEffect(Direction direction, double volume, BlockPos source) {

    public static Box getGravityEffectCollider(PlayerEntity player){
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(0.35, 0.7, 0.35, GravityChangerAPI.getAppliedGravityDirection(player));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-0.35, 0.0, -0.35, GravityChangerAPI.getAppliedGravityDirection(player));
        return new Box(pos1, pos2).offset(player.getPos());
    }

    public static Box getLowerGravityEffectCollider(PlayerEntity player){
        Vec3d pos1 = RotationUtil.vecPlayerToWorld(0.35, -0.1, 0.35, GravityChangerAPI.getAppliedGravityDirection(player));
        Vec3d pos2 = RotationUtil.vecPlayerToWorld(-0.35, 0.0, -0.35, GravityChangerAPI.getAppliedGravityDirection(player));
        return new Box(pos1, pos2).offset(player.getPos());
    }

    public static void applyGravityEffectToPlayers(GravityEffect gravityEffect, Box box, World world){
        List<ClientPlayerEntity> playerEntities = world.getEntitiesByClass(ClientPlayerEntity.class, box.expand(0.2), e -> true);
        for (ClientPlayerEntity player : playerEntities) {
            //Get player collider for gravity effects
            Box gravityEffectCollider = (gravityEffect.direction().getOpposite() == GravityChangerAPI.getGravityDirection(player)) ? player.getBoundingBox() : GravityEffect.getGravityEffectCollider(player);
            Box lowerGravityEffectCollider = GravityEffect.getLowerGravityEffectCollider(player);
            //Check if the player's rotation box is colliding with this gravity plates area of effect
            if (box.intersects(gravityEffectCollider))
                ((GravityData) player).getFieldList().add(gravityEffect);
            if (box.intersects(lowerGravityEffectCollider))
                ((GravityData) player).getLowerFieldList().add(gravityEffect);
        }
    }

}
