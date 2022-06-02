package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.AmethystGravity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public class FieldGeneratorScreenHandler extends ScreenHandler {
    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public FieldGeneratorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(AmethystGravity.FIELD_GENERATOR_SCREEN_HANDLER, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
