package cyborgcabbage.amethystgravity.client.render.block.entity;

import cyborgcabbage.amethystgravity.block.entity.PlanetFieldGeneratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;

@Environment(value= EnvType.CLIENT)
public class PlanetFieldGeneratorBlockEntityRenderer extends AbstractFieldGeneratorBlockEntityRenderer<PlanetFieldGeneratorBlockEntity> {
    public PlanetFieldGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderForceField(PlanetFieldGeneratorBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animation) {
        float radius = (float)entity.getRadius();

        ArrayList<Vec3f> inner = new ArrayList<>();
        inner.add(new Vec3f( .5f, .5f, .5f));
        inner.add(new Vec3f( .5f, .5f,-.5f));
        inner.add(new Vec3f( .5f,-.5f, .5f));
        inner.add(new Vec3f( .5f,-.5f,-.5f));
        inner.add(new Vec3f(-.5f, .5f, .5f));
        inner.add(new Vec3f(-.5f, .5f,-.5f));
        inner.add(new Vec3f(-.5f,-.5f, .5f));
        inner.add(new Vec3f(-.5f,-.5f,-.5f));

        float r = radius+.5f-SMIDGE;
        ArrayList<Vec3f> outer = new ArrayList<>();
        outer.add(new Vec3f( r, r, r));
        outer.add(new Vec3f( r, r,-r));
        outer.add(new Vec3f( r,-r, r));
        outer.add(new Vec3f( r,-r,-r));
        outer.add(new Vec3f(-r, r, r));
        outer.add(new Vec3f(-r, r,-r));
        outer.add(new Vec3f(-r,-r, r));
        outer.add(new Vec3f(-r,-r,-r));

        ArrayList<IntPair> edges = new ArrayList<>();
        edges.add(new IntPair(0, 1));
        edges.add(new IntPair(3, 2));
        edges.add(new IntPair(5, 4));
        edges.add(new IntPair(6, 7));
        edges.add(new IntPair(0, 2));
        edges.add(new IntPair(3, 1));
        edges.add(new IntPair(6, 4));
        edges.add(new IntPair(5, 7));
        edges.add(new IntPair(0, 4));
        edges.add(new IntPair(5, 1));
        edges.add(new IntPair(6, 2));
        edges.add(new IntPair(3, 7));

        ArrayList<IntFour> faces = new ArrayList<>();
        faces.add(new IntFour(2, 3, 7, 6));
        faces.add(new IntFour(0, 1, 5, 4));
        faces.add(new IntFour(1, 3, 7, 5));
        faces.add(new IntFour(0, 2, 6, 4));
        faces.add(new IntFour(4, 5, 7, 6));
        faces.add(new IntFour(0, 1, 3, 2));

        VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(ARROW_TEXTURE));
        Matrix4f m = matrixStack.peek().getPositionMatrix();
        Matrix3f n = matrixStack.peek().getNormalMatrix();

        float diagonal = (float)Math.sqrt(2)*radius;

        for (IntPair e : edges) {
            if(entity.getPolarity() == 1){
                addVertex(m, n, buffer, outer.get(e.a()), r,diagonal - animation);
                addVertex(m, n, buffer, outer.get(e.b()), -r,diagonal - animation);
                addVertex(m, n, buffer, inner.get(e.b()), -.5f, -animation);
                addVertex(m, n, buffer, inner.get(e.a()), .5f, -animation);
            }else {
                addVertex(m, n, buffer, outer.get(e.a()), r, -animation);
                addVertex(m, n, buffer, outer.get(e.b()), -r, -animation);
                addVertex(m, n, buffer, inner.get(e.b()), -.5f, diagonal - animation);
                addVertex(m, n, buffer, inner.get(e.a()), .5f, diagonal - animation);
            }
        }
        VertexConsumer buffer2 = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(WALL_TEXTURE));
        for (IntFour f : faces) {
            renderFaceEdges(m, n, buffer2, outer.get(f.a()), outer.get(f.b()), outer.get(f.c()), outer.get(f.d()));
        }
    }
}
