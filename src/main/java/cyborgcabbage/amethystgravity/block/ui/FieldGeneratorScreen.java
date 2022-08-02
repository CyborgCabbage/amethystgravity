package cyborgcabbage.amethystgravity.block.ui;

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
        addDrawableChild(new ButtonWidget(bX-50, bY - 48, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setHeight(handler.height + magnitude)));
        addDrawableChild(new ButtonWidget(bX-50, bY - 8, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setHeight(handler.height - magnitude)));
        //Width
        addDrawableChild(new ButtonWidget(bX, bY - 48, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setWidth(handler.width + magnitude)));
        addDrawableChild(new ButtonWidget(bX, bY - 8, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setWidth(handler.width - magnitude)));
        //Depth
        addDrawableChild(new ButtonWidget(bX+50, bY - 48, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.increase"), button -> handler.setDepth(handler.depth + magnitude)));
        addDrawableChild(new ButtonWidget(bX+50, bY - 8, bWidth, bHeight, Text.translatable("amethystgravity.fieldGenerator.decrease"), button -> handler.setDepth(handler.depth - magnitude)));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        super.renderValuesAndLabels(matrices);
        //Draw values
        drawValue(matrices, handler.height/10.0, -50);
        drawValue(matrices, handler.width/10.0, 0);
        drawValue(matrices, handler.depth/10.0, 50);
        //Draw labels
        drawLabel(matrices, "Height", -50);
        drawLabel(matrices, "Width", 0);
        drawLabel(matrices, "Depth", 50);
    }
}
