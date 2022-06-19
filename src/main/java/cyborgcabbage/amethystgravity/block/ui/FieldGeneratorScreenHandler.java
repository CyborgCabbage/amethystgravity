package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import net.minecraft.server.world.ServerWorld;

public class FieldGeneratorScreenHandler extends AbstractFieldGeneratorScreenHandler<FieldGeneratorScreenHandler> {
    //Client
    public FieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, syncId, playerInventory, 4);
    }

    //Server
    public FieldGeneratorScreenHandler(int syncId, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, syncId, propertyDelegate, context);
    }

    //Server
    public void pressButton(AbstractFieldGeneratorBlockEntity.Button button, boolean shift){
        if(button == AbstractFieldGeneratorBlockEntity.Button.POLARITY){
            int newValue = 1-propertyDelegate.get(3);
            if(newValue != 0 && newValue != 1) newValue = 0;
            propertyDelegate.set(3, newValue);
        }else {
            int magnitude = shift ? 1 : 10;
            int index = switch (button) {
                case HEIGHT_UP, HEIGHT_DOWN -> 0;
                case WIDTH_UP, WIDTH_DOWN -> 1;
                case DEPTH_UP, DEPTH_DOWN -> 2;
                default -> throw new IllegalStateException("Unexpected button: " + button);
            };
            int sign = switch (button) {
                case HEIGHT_UP, DEPTH_UP, WIDTH_UP -> 1;
                case HEIGHT_DOWN, DEPTH_DOWN, WIDTH_DOWN -> -1;
                default -> throw new IllegalStateException("Unexpected button: " + button);
            };
            int newValue = propertyDelegate.get(index) + magnitude * sign;
            int threshold = index == 0 ? 1 : 10;
            if (newValue < threshold) newValue = threshold;
            propertyDelegate.set(index, newValue);
        }
        context.run((world, pos) -> {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.markDirty(pos);
            serverWorld.getChunkManager().markForUpdate(pos);
        });
    }



    //Client
    public int getHeight(){
        return propertyDelegate.get(0);
    }

    public int getWidth(){
        return propertyDelegate.get(1);
    }

    public int getDepth(){
        return propertyDelegate.get(2);
    }

    @Override
    public int getPolarity() {
        return propertyDelegate.get(3);
    }

    @Override
    protected Block getBlock() {
        return AmethystGravity.FIELD_GENERATOR_BLOCK;
    }
}
