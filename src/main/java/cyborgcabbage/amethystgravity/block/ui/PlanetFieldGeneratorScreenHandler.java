package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;

public class PlanetFieldGeneratorScreenHandler extends AbstractFieldGeneratorScreenHandler<PlanetFieldGeneratorScreenHandler> {
    //Client
    public PlanetFieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, syncId, playerInventory, 2);
    }

    //Server
    public PlanetFieldGeneratorScreenHandler(int syncId, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, syncId, propertyDelegate, context);
    }

    //Server
    public void pressButton(PlanetFieldGeneratorBlockEntity.Button button, boolean shift){
        if(button == AbstractFieldGeneratorBlockEntity.Button.POLARITY){
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

    @Override
    public int getPolarity() {
        return propertyDelegate.get(1);
    }

    @Override
    protected Block getBlock() {
        return AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK;
    }
}
