package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;

public class CylinderFieldGeneratorScreenHandler extends AbstractFieldGeneratorScreenHandler<CylinderFieldGeneratorScreenHandler>{
    //Client
    public CylinderFieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER, syncId, playerInventory, 3);
    }

    //Server
    public CylinderFieldGeneratorScreenHandler(int syncId, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(AmethystGravity.CYLINDER_FIELD_GENERATOR_SCREEN_HANDLER, syncId, propertyDelegate, context);
    }

    //Server
    public void pressButton(AbstractFieldGeneratorBlockEntity.Button button, boolean shift){
        if(button == AbstractFieldGeneratorBlockEntity.Button.POLARITY){
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
        }
        context.run((world, pos) -> {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.markDirty(pos);
            serverWorld.getChunkManager().markForUpdate(pos);
        });
    }



    //Client
    public int getRadius(){
        return propertyDelegate.get(0);
    }

    public int getWidth(){
        return propertyDelegate.get(1);
    }


    @Override
    public int getPolarity() {
        return propertyDelegate.get(2);
    }

    @Override
    protected Block getBlock() {
        return AmethystGravity.CYLINDER_FIELD_GENERATOR_BLOCK;
    }
}
