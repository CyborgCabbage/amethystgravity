package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractFieldGeneratorScreenHandler<T extends AbstractFieldGeneratorScreenHandler> extends ScreenHandler {
    protected final ScreenHandlerContext context;

    public int height;
    public int width;
    public int depth;
    public int radius;
    public int polarity;
    public int visibility;

    public boolean creative;

    //Client
    public AbstractFieldGeneratorScreenHandler(ScreenHandlerType<T> sht, int syncId) {
        this(sht, syncId, ScreenHandlerContext.EMPTY, false);
    }

    //Server
    public AbstractFieldGeneratorScreenHandler(ScreenHandlerType<T> sht, int syncId, ScreenHandlerContext _context, boolean _creative) {
        super(sht, syncId);
        context = _context;
        creative = _creative;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(context, player, getBlock());
    }

    protected abstract Block getBlock();

    public abstract void updateSettings(ServerPlayerEntity player, int height, int width, int depth, int radius, int polarity, int visibility);

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return null;
    }

    public void setHeight(int value) {
        height = value;
        if(height < 1) height = 1;
    }

    public void setWidth(int value) {
        width = value;
        if(width < 10) width = 10;
    }

    public void setDepth(int value) {
        depth = value;
        if(depth < 10) depth = 10;
    }

    public void setRadius(int value) {
        radius = value;
        if(radius < 1) radius = 1;
    }
}
