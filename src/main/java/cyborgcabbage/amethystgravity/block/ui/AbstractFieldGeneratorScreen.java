package cyborgcabbage.amethystgravity.block.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
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
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;

public abstract class AbstractFieldGeneratorScreen<T extends AbstractFieldGeneratorScreenHandler> extends HandledScreen<T> {
    private static final Identifier TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/gui/blank.png");
    protected final T sh;
    ButtonWidget polarityButton;

    public AbstractFieldGeneratorScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleY = -100;
        sh = handler;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderValuesAndLabels(matrices);
        //Tooltip
        drawMouseoverTooltip(matrices, mouseX, mouseY);

        if(polarityButton != null) polarityButton.setMessage(getPolarityText());
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int bWidth = 120;
        int bHeight = 20;
        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2 + 5;
        //Polarity Button
        polarityButton = addDrawableChild(new ButtonWidget(bX, bY + 40, bWidth, bHeight, getPolarityText(), button -> {
            sendMenuUpdatePacket(FieldGeneratorBlockEntity.Button.POLARITY);
        }));
    }

    private Text getPolarityText(){
        if(sh.getPolarity() == 0){
            return Text.translatable("amethystgravity.fieldGenerator.attract");
        }else{
            return Text.translatable("amethystgravity.fieldGenerator.repel");
        }
    }

    protected abstract void renderValuesAndLabels(MatrixStack matrices);

    protected void drawValue(MatrixStack matrices, double value, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        DecimalFormat df = new DecimalFormat("0.0");

        String heightValue = df.format(value);
        textRenderer.draw(matrices, heightValue, tX-textRenderer.getWidth(heightValue)/2.f+xOffset, tY-20, Color.DARK_GRAY.getRGB());
    }

    protected void drawLabel(MatrixStack matrices, String label, int xOffset){
        int tX = (width) / 2;
        int tY = (height - textRenderer.fontHeight) / 2 + 6;
        textRenderer.draw(matrices, label, tX-textRenderer.getWidth(label)/2.f+0.5f+xOffset, tY-60, Color.DARK_GRAY.getRGB());
    }

    protected void sendMenuUpdatePacket(AbstractFieldGeneratorBlockEntity.Button button){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(button);
        buf.writeBoolean(hasShiftDown());
        ClientPlayNetworking.send(AmethystGravity.FIELD_GENERATOR_MENU_CHANNEL, buf);
    }
}
