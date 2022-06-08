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
    public void setData(int height, int width, int depth){
        propertyDelegate.set(0, propertyDelegate.get(0)+height);
        propertyDelegate.set(1, propertyDelegate.get(1)+width);
        propertyDelegate.set(2, propertyDelegate.get(2)+depth);
        context.run((world, pos) -> {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.markDirty(pos);
            serverWorld.getChunkManager().markForUpdate(pos);
        });
        /*if(context != null){
            context.run((world, blockPos) -> {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if(blockEntity instanceof FieldGeneratorBlockEntity fieldGenerator){
                    fieldGenerator.setParameters(height, width, depth);
                }
            });
        }*/
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
