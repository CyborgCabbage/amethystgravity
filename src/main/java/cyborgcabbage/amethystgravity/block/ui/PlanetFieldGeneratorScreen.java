package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class PlanetFieldGeneratorScreen extends AbstractFieldGeneratorScreen<PlanetFieldGeneratorScreenHandler> {

    public PlanetFieldGeneratorScreen(PlanetFieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
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
        addDrawableChild(new ButtonWidget(bX, bY - 40, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> sh.setRadius(sh.radius + (Screen.hasShiftDown() ? 1 : 10))));
        addDrawableChild(new ButtonWidget(bX, bY, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> sh.setRadius(sh.radius - (Screen.hasShiftDown() ? 1 : 10))));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        //Draw values
        drawValue(matrices, sh.radius/10.0, 0);
        //Draw labels
        drawLabel(matrices, "Radius", 0);
    }
}
