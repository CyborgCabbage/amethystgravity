package cyborgcabbage.amethystgravity.mixin;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin implements GravityData {
    public List<Direction> gravityBlocks = new ArrayList<>();

    @Override
    public List<Direction> getGravityData() {
        return gravityBlocks;
    }

    @Inject(method="tick()V", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci){
        PlayerEntity player = (PlayerEntity)(Object)this;
        //Get direction
        List<Direction> directions = ((GravityData)this).getGravityData();
        Direction dir = Direction.DOWN;
        if(!directions.isEmpty() && !player.isSpectator() && !player.getAbilities().flying) dir = directions.get(0);
        //Set gravity
        if(!GravityChangerAPI.getGravityDirection(player).equals(dir)) {
            GravityChangerAPI.setGravityDirection(player, dir);
            AmethystGravity.LOGGER.info(player);
            AmethystGravity.LOGGER.info(dir);
        }
        //Clear direction pool
        directions.clear();
    }

}
