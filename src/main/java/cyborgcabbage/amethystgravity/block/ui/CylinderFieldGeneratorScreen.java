package cyborgcabbage.amethystgravity.block.ui;

import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CylinderFieldGeneratorScreen extends AbstractFieldGeneratorScreen<CylinderFieldGeneratorScreenHandler>{
    public CylinderFieldGeneratorScreen(CylinderFieldGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
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
        addDrawableChild(new ButtonWidget(bX-25, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.RADIUS_UP)));
        addDrawableChild(new ButtonWidget(bX-25, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.RADIUS_DOWN)));
        //Width
        addDrawableChild(new ButtonWidget(bX+25, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_UP)));
        addDrawableChild(new ButtonWidget(bX+25, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_DOWN)));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        //Draw values
        drawValue(matrices, sh.getRadius()*.1, -25);
        drawValue(matrices, sh.getWidth()*.1, 25);
        //Draw labels
        drawLabel(matrices, "Radius", -25);
        drawLabel(matrices, "Width", 25);
    }
}
