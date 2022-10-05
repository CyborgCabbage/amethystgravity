package cyborgcabbage.amethystgravity.client.render.block.entity;

import com.fusionflux.gravity_api.util.RotationUtil;
import cyborgcabbage.amethystgravity.block.entity.FieldGeneratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;

@Environment(value= EnvType.CLIENT)
public class FieldGeneratorBlockEntityRenderer extends AbstractFieldGeneratorBlockEntityRenderer<FieldGeneratorBlockEntity>{

    public FieldGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderForceField(FieldGeneratorBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animation) {
        Direction direction = entity.getDirection();
        double height = entity.getHeight();
        double width = entity.getWidth();
        double depth = entity.getDepth();
        ArrayList<Vec3f> p = new ArrayList<>();
        p.add(new Vec3f(-.5f*(float)width+SMIDGE, 1*(float)height-SMIDGE, -.5f*(float)depth+SMIDGE));
        p.add(new Vec3f(-.5f*(float)width+SMIDGE, 1*(float)height-SMIDGE, .5f*(float)depth-SMIDGE));
        p.add(new Vec3f(.5f*(float)width-SMIDGE, 1*(float)height-SMIDGE, .5f*(float)depth-SMIDGE));
        p.add(new Vec3f(.5f*(float)width-SMIDGE, 1*(float)height-SMIDGE, -.5f*(float)depth+SMIDGE));
        p.add(new Vec3f(-.5f*(float)width+SMIDGE, 0*(float)height+SMIDGE, -.5f*(float)depth+SMIDGE));
        p.add(new Vec3f(-.5f*(float)width+SMIDGE, 0*(float)height+SMIDGE, .5f*(float)depth-SMIDGE));
        p.add(new Vec3f(.5f*(float)width-SMIDGE, 0*(float)height+SMIDGE, .5f*(float)depth-SMIDGE));
        p.add(new Vec3f(.5f*(float)width-SMIDGE, 0*(float)height+SMIDGE, -.5f*(float)depth+SMIDGE));
        for (Vec3f vertex : p) {
            Vec3f rotated = RotationUtil.vecPlayerToWorld(vertex, direction);
            Vec3f shift = entity.getDirection().getUnitVector();
            shift.multiplyComponentwise(-.5f,-.5f,-.5f);
            rotated.add(shift);
            vertex.set(rotated);
        }

        VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(ARROW_TEXTURE));
        Matrix4f m = matrixStack.peek().getModel();
        Matrix3f n = matrixStack.peek().getNormal();
        float h0 = -animation;
        float h1 = -animation;
        if(entity.getPolarity() == 1) {
            h0 += (float)height;
        }else {
            h1 += (float)height;
        }
        //
        addVertex(m, n, buffer, p.get(0), -.5f*(float)depth, h0);
        addVertex(m, n, buffer, p.get(1), .5f*(float)depth, h0);
        addVertex(m, n, buffer, p.get(5), .5f*(float)depth, h1);
        addVertex(m, n, buffer, p.get(4), -.5f*(float)depth, h1);
        //
        addVertex(m, n, buffer, p.get(1), -.5f*(float)width, h0);
        addVertex(m, n, buffer, p.get(2), .5f*(float)width, h0);
        addVertex(m, n, buffer, p.get(6), .5f*(float)width, h1);
        addVertex(m, n, buffer, p.get(5), -.5f*(float)width, h1);
        //
        addVertex(m, n, buffer, p.get(2), .5f*(float)depth, h0);
        addVertex(m, n, buffer, p.get(3), -.5f*(float)depth, h0);
        addVertex(m, n, buffer, p.get(7), -.5f*(float)depth, h1);
        addVertex(m, n, buffer, p.get(6), .5f*(float)depth, h1);
        //
        addVertex(m, n, buffer, p.get(3), .5f*(float)width, h0);
        addVertex(m, n, buffer, p.get(0), -.5f*(float)width, h0);
        addVertex(m, n, buffer, p.get(4), -.5f*(float)width, h1);
        addVertex(m, n, buffer, p.get(7), .5f*(float)width, h1);

        VertexConsumer buffer2 = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(WALL_TEXTURE));
        renderFaceEdges(m, n, buffer2, p.get(0), p.get(1), p.get(2), p.get(3));
        renderFaceEdges(m, n, buffer2, p.get(4), p.get(5), p.get(6), p.get(7));
    }
}
