package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;

public class PlanetFieldGeneratorScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final PropertyDelegate propertyDelegate;
    //Client
    public PlanetFieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, new ArrayPropertyDelegate(1), ScreenHandlerContext.EMPTY);
    }

    //Server
    public PlanetFieldGeneratorScreenHandler(int syncId, PropertyDelegate _propertyDelegate, ScreenHandlerContext _context) {
        super(AmethystGravity.PLANET_FIELD_GENERATOR_SCREEN_HANDLER, syncId);
        context = _context;
        propertyDelegate = _propertyDelegate;
        addProperties(propertyDelegate);
    }

    //Server
    public void pressButton(PlanetFieldGeneratorBlockEntity.Button button, boolean shift){
        int magnitude = shift ? 1 : 10;
        int index = 0;
        int sign = switch(button){
            case RADIUS_UP -> 1;
            case RADIUS_DOWN -> -1;
        };
        int newValue = propertyDelegate.get(index)+magnitude*sign;
        if(newValue <= 10) newValue = 10;
        propertyDelegate.set(index, newValue);
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
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(context, player, AmethystGravity.PLANET_FIELD_GENERATOR_BLOCK);
    }
}
