package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class PlanetFieldGeneratorScreen extends AbstractFieldGeneratorScreen<PlanetFieldGeneratorScreenHandler> {

    public PlanetFieldGeneratorScreen(PlanetFieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        //Draw values
        drawValue(matrices, sh.getRadius()*.1, 0);
        //Draw labels
        drawLabel(matrices, "Radius", 0);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 20;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Radius
        addDrawableChild(new ButtonWidget(bX, bY - 40, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(PlanetFieldGeneratorBlockEntity.Button.RADIUS_UP)));
        addDrawableChild(new ButtonWidget(bX, bY, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(PlanetFieldGeneratorBlockEntity.Button.RADIUS_DOWN)));
   }
}
