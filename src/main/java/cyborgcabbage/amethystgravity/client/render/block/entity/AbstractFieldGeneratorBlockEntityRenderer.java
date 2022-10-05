package cyborgcabbage.amethystgravity.client.render.block.entity;

import cyborgcabbage.amethystgravity.AmethystGravity;
import cyborgcabbage.amethystgravity.block.entity.AbstractFieldGeneratorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;

public abstract class AbstractFieldGeneratorBlockEntityRenderer<BE extends AbstractFieldGeneratorBlockEntity> implements BlockEntityRenderer<BE> {
    protected static final Identifier ARROW_TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/misc/arrow_forcefield.png");
    protected static final Identifier WALL_TEXTURE = new Identifier(AmethystGravity.MOD_ID, "textures/misc/wall_forcefield.png");
    protected static final float SMIDGE = 0.01f;

    public AbstractFieldGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BE entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        boolean show = false;
        switch(entity.getVisibility()){
            case 0 -> {//With glasses
                Entity ce = MinecraftClient.getInstance().getCameraEntity();
                if(ce instanceof LivingEntity le) {
                    ItemStack equippedStack = le.getEquippedStack(EquipmentSlot.HEAD);
                    show = equippedStack.getItem() == AmethystGravity.GRAVITY_GLASSES;
                }
            }
            case 1 -> {//Always
                show = true;
            }
            case 2 -> {//Never
                show = false;
            }
        }
        if(show){
            double time = tickDelta;
            if (entity.getWorld() != null) {
                time += entity.getWorld().getTime();
                time /= 20;
            }
            //Animation
            float animation = (float) (time % 1);
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);
            renderForceField(entity, matrixStack, vertexConsumerProvider, animation);
            matrixStack.pop();
        }
    }

    protected abstract void renderForceField(BE entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animation);

    protected void addVertex(Matrix4f m, Matrix3f n, VertexConsumer buffer, Vec3f vec, float u, float v){
        addVertex(m, n, buffer, vec.getX(), vec.getY(), vec.getZ(), u, v);
    }

    protected void addVertex(Matrix4f m, Matrix3f n, VertexConsumer buffer, float x, float y, float z, float u, float v){
        buffer.vertex(m, x, y, z).color(.7f, .5f, .9f, .4f).uv(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(n, 0, 1,0).next();
    }

    //Each point should be next to the previous going round the quad
    protected void renderFaceEdges(Matrix4f m, Matrix3f n, VertexConsumer buffer, Vec3f p0, Vec3f p1, Vec3f p2, Vec3f p3){
        Vec3f faceCentre = p0.copy();
        faceCentre.lerp(p2, .5f);
        ArrayList<VecPair> faceEdges = new ArrayList<>();
        faceEdges.add(new VecPair(p0, p1));
        faceEdges.add(new VecPair(p1, p2));
        faceEdges.add(new VecPair(p2, p3));
        faceEdges.add(new VecPair(p3, p0));
        for (VecPair fe : faceEdges) {
            Vec3f edgeCentre = fe.a().copy();
            edgeCentre.lerp(fe.b(), 0.5f);
            Vec3f towardsFaceCentre = faceCentre.copy();
            towardsFaceCentre.subtract(edgeCentre);
            towardsFaceCentre.normalize();
            towardsFaceCentre.multiplyComponentwise(.25f,.25f,.25f);
            Vec3f towardsEdgeCentre = edgeCentre.copy();
            towardsEdgeCentre.subtract(fe.b());
            towardsEdgeCentre.normalize();
            towardsEdgeCentre.multiplyComponentwise(.25f,.25f,.25f);
            Vec3f i0 = fe.b().copy();
            Vec3f i1 = fe.a().copy();
            i0.add(towardsFaceCentre);
            i0.add(towardsEdgeCentre);
            i1.add(towardsFaceCentre);
            i1.subtract(towardsEdgeCentre);
            addVertex(m, n, buffer, fe.a(), 0, 0);
            addVertex(m, n, buffer, fe.b(), 0, 0);
            addVertex(m, n, buffer, i0, 0, 0);
            addVertex(m, n, buffer, i1, 0, 0);
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(BE blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    public record VecPair(Vec3f a, Vec3f b){}
    public record IntPair(int a, int b){}
    public record IntFour(int a, int b, int c, int d){}
}
