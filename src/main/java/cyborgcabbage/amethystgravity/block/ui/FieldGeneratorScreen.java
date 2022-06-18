package cyborgcabbage.amethystgravity.block.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;

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
        addDrawableChild(new ButtonWidget(bX-50, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.HEIGHT_UP)));
        addDrawableChild(new ButtonWidget(bX-50, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.HEIGHT_DOWN)));
        //Width
        addDrawableChild(new ButtonWidget(bX, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_UP)));
        addDrawableChild(new ButtonWidget(bX, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.WIDTH_DOWN)));
        //Depth
        addDrawableChild(new ButtonWidget(bX+50, bY - 40, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.increase"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.DEPTH_UP)));
        addDrawableChild(new ButtonWidget(bX+50, bY, bWidth, bHeight, new TranslatableText("amethystgravity.fieldGenerator.decrease"), button -> sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.DEPTH_DOWN)));
    }

    @Override
    protected void renderValuesAndLabels(MatrixStack matrices) {
        //Draw values
        drawValue(matrices, sh.getHeight()*.1, -50);
        drawValue(matrices, sh.getWidth()*.1, 0);
        drawValue(matrices, sh.getDepth()*.1, 50);
        //Draw labels
        drawLabel(matrices, "Height", -50);
        drawLabel(matrices, "Width", 0);
        drawLabel(matrices, "Depth", 50);
    }
}
