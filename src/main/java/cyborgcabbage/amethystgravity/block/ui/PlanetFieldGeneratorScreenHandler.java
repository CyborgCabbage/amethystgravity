package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class PlanetFieldGeneratorScreenHandler extends AbstractFieldGeneratorScreenHandler<PlanetFieldGeneratorScreenHandler> {
    //Client
    public PlanetFieldGeneratorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, syncId);
        this.radius = buf.readInt();
        this.polarity = buf.readInt();
    }

    //Server
    public PlanetFieldGeneratorScreenHandler(int syncId, ScreenHandlerContext context, boolean _creative) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, syncId, context, _creative);
    }

    //Server
    @Override
    public void updateSettings(ServerPlayerEntity player, int height, int width, int depth, int radius, int polarity, int visibility) {
        setRadius(radius);
        this.polarity = polarity;
        this.visibility = visibility;
        /*if(button == AbstractFieldGeneratorBlockEntity.Button.POLARITY){
            int newValue = 1-propertyDelegate.get(1);
            if(newValue != 0 && newValue != 1) newValue = 0;
            propertyDelegate.set(1, newValue);
        }else {
            int magnitude = shift ? 1 : 10;
            int sign = switch (button) {
                case RADIUS_UP -> 1;
                case RADIUS_DOWN -> -1;
                default -> throw new IllegalStateException("Unexpected button: " + button);
            };
            int newValue = propertyDelegate.get(0) + magnitude * sign;
            int threshold = 1;
            if (newValue < threshold) newValue = threshold;
            propertyDelegate.set(0, newValue);
        }*/
        context.run((world, pos) -> {
            ServerWorld serverWorld = (ServerWorld)world;
            Optional<PlanetFieldGeneratorBlockEntity> blockEntity = serverWorld.getBlockEntity(pos, AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_ENTITY);
            blockEntity.ifPresent(be -> be.updateSettings(player, this.radius, this.polarity, this.visibility));
            serverWorld.markDirty(pos);
            serverWorld.getChunkManager().markForUpdate(pos);
        });
    }

    @Override
    protected Block getBlock() {
        return creative ? AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK_CREATIVE : AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK;
    }
}
