package cyborgcabbage.amethystgravity.gravity;

import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record GravityEffect(Direction direction, Type type, double volume, BlockPos source) {
    public enum Type{
        BASELINE,
        FIELD,
        PLATE
    }

    public static Box getGravityEffectCollider(PlayerEntity player){
        //Get the centre of rotation
        Vec3d stablePoint = RotationUtil.vecPlayerToWorld(0.0, 0.35, 0.0, GravityChangerAPI.getAppliedGravityDirection(player));
        stablePoint = stablePoint.add(player.getPos());
        //Create 0.6 x 0.6 x 0.6 box around centre of rotation
        Vec3d dim = new Vec3d(0.35,0.35,0.35);
        return new Box(stablePoint.subtract(dim), stablePoint.add(dim));
    }
}
