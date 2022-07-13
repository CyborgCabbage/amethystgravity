package cyborgcabbage.amethystgravity.block.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class FieldGeneratorScreen extends AbstractFieldGeneratorScreen<FieldGeneratorScreenHandler>{

    public FieldGeneratorScreen(FieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
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
        //Height
        addDrawableChild(new ButtonWidget(bX-50, bY - 40, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> sh.setHeight(sh.height + (Screen.hasShiftDown() ? 1 : 10))));
        addDrawableChild(new ButtonWidget(bX-50, bY, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> sh.setHeight(sh.height - (Screen.hasShiftDown() ? 1 : 10))));
        //Width
        addDrawableChild(new ButtonWidget(bX, bY - 40, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> sh.setWidth(sh.width + (Screen.hasShiftDown() ? 1 : 10))));
        addDrawableChild(new ButtonWidget(bX, bY, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> sh.setWidth(sh.width - (Screen.hasShiftDown() ? 1 : 10))));
        //Depth
        addDrawableChild(new ButtonWidget(bX+50, bY - 40, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> sh.setDepth(sh.depth + (Screen.hasShiftDown() ? 1 : 10))));
        addDrawableChild(new ButtonWidget(bX+50, bY, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> sh.setDepth(sh.depth - (Screen.hasShiftDown() ? 1 : 10))));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        //Draw values
        drawValue(matrices, sh.height/10.0, -50);
        drawValue(matrices, sh.width/10.0, 0);
        drawValue(matrices, sh.depth/10.0, 50);
        //Draw labels
        drawLabel(matrices, "Height", -50);
        drawLabel(matrices, "Width", 0);
        drawLabel(matrices, "Depth", 50);
    }
}
