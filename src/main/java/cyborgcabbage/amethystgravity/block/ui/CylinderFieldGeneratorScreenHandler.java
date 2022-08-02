package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.CylinderFieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class CylinderFieldGeneratorScreenHandler extends AbstractFieldGeneratorScreenHandler<CylinderFieldGeneratorScreenHandler>{
    //Client
    public CylinderFieldGeneratorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER, syncId);
        this.radius = buf.readInt();
        this.width = buf.readInt();
        this.polarity = buf.readInt();
        this.visibility = buf.readInt();
    }

    //Server
    public CylinderFieldGeneratorScreenHandler(int syncId, ScreenHandlerContext context, boolean _creative) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER, syncId, context, _creative);
    }

    //Server

    @Override
    public void updateSettings(ServerPlayerEntity player, int height, int width, int depth, int radius, int polarity, int visibility) {
        setRadius(radius);
        setWidth(width);
        this.polarity = polarity;
        this.visibility = visibility;
        /*if(button == AbstractFieldGeneratorBlockEntity.Button.POLARITY){
            int newValue = 1-propertyDelegate.get(2);
            if(newValue != 0 && newValue != 1) newValue = 0;
            propertyDelegate.set(2, newValue);
        }else {
            int magnitude = shift ? 1 : 10;
            int index = switch (button) {
                case RADIUS_UP , RADIUS_DOWN -> 0;
                case WIDTH_UP, WIDTH_DOWN -> 1;
                default -> throw new IllegalStateException("Unexpected button: " + button);
            };
            int sign = switch (button) {
                case RADIUS_UP, WIDTH_UP -> 1;
                case RADIUS_DOWN, WIDTH_DOWN -> -1;
                default -> throw new IllegalStateException("Unexpected button: " + button);
            };
            int newValue = propertyDelegate.get(index) + magnitude * sign;
            int threshold = index == 0 ? 1 : 10;
            if (newValue < threshold) newValue = threshold;
            propertyDelegate.set(index, newValue);
        }*/
        context.run((world, pos) -> {
            ServerWorld serverWorld = (ServerWorld)world;
            Optional<CylinderFieldGeneratorBlockEntity> blockEntity = serverWorld.getBlockEntity(pos, AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_ENTITY);
            blockEntity.ifPresent(be -> {
                be.updateSettings(player, this.radius, this.width, this.polarity, this.visibility);
            });
            serverWorld.markDirty(pos);
            serverWorld.getChunkManager().markForUpdate(pos);
        });
    }

    @Override
    protected Block getBlock() {
        return creative ? AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK_CREATIVE : AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK;
    }
}
