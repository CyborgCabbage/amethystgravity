package cyborgcabbage.amethystgravity.mixin;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.access.GravityData;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
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
        Direction newDir = Direction.DOWN;
        if(!directions.isEmpty() && !player.isSpectator() && !player.getAbilities().flying) newDir = directions.get(0);
        Direction oldDir = GravityChangerAPI.getGravityDirection(player);
        //Set gravity
        if(!oldDir.equals(newDir)) {
            GravityChangerAPI.setGravityDirection(player, newDir);
        }
        //Clear direction pool
        directions.clear();
    }

}
