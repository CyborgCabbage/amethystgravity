package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;

public abstract class AbstractFieldGeneratorScreenHandler<T extends AbstractFieldGeneratorScreenHandler> extends ScreenHandler {
    protected final ScreenHandlerContext context;
    protected final PropertyDelegate propertyDelegate;

    //Client
    public AbstractFieldGeneratorScreenHandler(ScreenHandlerType<T> sht, int syncId, PlayerInventory playerInventory, int pdSize) {
        this(sht, syncId, new ArrayPropertyDelegate(pdSize), ScreenHandlerContext.EMPTY);
    }

    //Server
    public AbstractFieldGeneratorScreenHandler(ScreenHandlerType<T> sht, int syncId, PropertyDelegate _propertyDelegate, ScreenHandlerContext _context) {
        super(sht, syncId);
        context = _context;
        propertyDelegate = _propertyDelegate;
        addProperties(propertyDelegate);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(context, player, getBlock());
    }

    protected abstract Block getBlock();

    public abstract void pressButton(AbstractFieldGeneratorBlockEntity.Button button, boolean shift);

    public abstract int getPolarity();

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return null;
    }
}
