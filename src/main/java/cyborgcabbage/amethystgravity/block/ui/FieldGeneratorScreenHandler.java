package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class FieldGeneratorScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final PropertyDelegate propertyDelegate;
    //Client
    public FieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, new ArrayPropertyDelegate(3), ScreenHandlerContext.EMPTY);
    }

    //Server
    public FieldGeneratorScreenHandler(int syncId, PropertyDelegate _propertyDelegate, ScreenHandlerContext _context) {
        super(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, syncId);
        context = _context;
        propertyDelegate = _propertyDelegate;
        addProperties(propertyDelegate);
    }

    //Server
    public void pressButton(FieldGeneratorBlockEntity.Button button, boolean shift){
        int magnitude = shift ? 1 : 10;
        int index = switch(button){
            case HEIGHT_UP, HEIGHT_DOWN -> 0;
            case WIDTH_UP, WIDTH_DOWN -> 1;
            case DEPTH_UP, DEPTH_DOWN -> 2;
        };
        int sign = switch(button){
            case HEIGHT_UP, DEPTH_UP, WIDTH_UP -> 1;
            case HEIGHT_DOWN, DEPTH_DOWN, WIDTH_DOWN -> -1;
        };
        int newValue = propertyDelegate.get(index)+magnitude*sign;
        if(newValue <= 0) newValue = magnitude;
        propertyDelegate.set(index, newValue);
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
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(context, player, AmethystGravity.FIELD_GENERATOR_BLOCK);
    }
}
