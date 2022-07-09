package cyborgcabbage.amethystgravity.gravity;

import com.fusionflux.gravity_api.api.RotationParameters;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.gravity_api.util.packet.UpdateGravityPacket;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.PlatingBlock;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class FieldGravityVerifier {
    public static Identifier FIELD_GRAVITY_SOURCE = new Identifier(AmethystGravity.MOD_ID, "field");
    public static int FIELD_GRAVITY_PRIORITY = 100;
    public static int FIELD_GRAVITY_MAX_DURATION = 100;

    public static Gravity newFieldGravity(Direction direction, RotationParameters rp){
        return new Gravity(direction, FIELD_GRAVITY_PRIORITY, FIELD_GRAVITY_MAX_DURATION, FIELD_GRAVITY_SOURCE.toString(), rp);
    }

    public static boolean check(ServerPlayerEntity player, PacketByteBuf info, UpdateGravityPacket packet){
        if(packet.gravity.duration() > FIELD_GRAVITY_MAX_DURATION) return false;
        if(packet.gravity.priority() > FIELD_GRAVITY_PRIORITY) return false;
        if(!packet.gravity.source().equals(FIELD_GRAVITY_SOURCE.toString())) return false;
        if(packet.gravity.direction() == null) return true;
        BlockPos blockPos = info.readBlockPos();
        World world = player.getWorld();
        if(world == null) return false;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.getBlock() instanceof PlatingBlock){
            ArrayList<Direction> directions = PlatingBlock.getDirections(blockState);
            if(directions.contains(packet.gravity.direction())){
                double distance = GravityEffect.getGravityOrigin(player).distanceTo(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5));
                return distance < 5;
            }
        }else if(blockEntity instanceof AbstractFieldGeneratorBlockEntity fieldBlockEntity){
            Box gravityEffectBox = fieldBlockEntity.getGravityEffectBox();
            return gravityEffectBox.expand(3.0).intersects(GravityEffect.getGravityEffectCollider(player));
        }
        return false;
    }

    public static PacketByteBuf packInfo(BlockPos block){
        var buf = PacketByteBufs.create();
        buf.writeBlockPos(block);
        return buf;
    }
}
