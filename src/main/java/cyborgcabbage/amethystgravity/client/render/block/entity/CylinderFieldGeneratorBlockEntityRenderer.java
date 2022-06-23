package cyborgcabbage.amethystgravity.client.render.block.entity;

import cyborgcabbage.amethystgravity.block.entity.CylinderFieldGeneratorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

import java.util.ArrayList;

@Environment(value= EnvType.CLIENT)
public class CylinderFieldGeneratorBlockEntityRenderer extends AbstractFieldGeneratorBlockEntityRenderer<CylinderFieldGeneratorBlockEntity>{
    public CylinderFieldGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderForceField(CylinderFieldGeneratorBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float animation) {
        float radius = (float)entity.getRadius();

        float r = radius+.5f-SMIDGE;
        float w = (float)entity.getWidth()/2.f-SMIDGE;
        ArrayList<Vec3f> outer = new ArrayList<>();
        outer.add(new Vec3f( r, w, r));
        outer.add(new Vec3f( r, w,-r));
        outer.add(new Vec3f( r,-w, r));
        outer.add(new Vec3f( r,-w,-r));
        outer.add(new Vec3f(-r, w, r));
        outer.add(new Vec3f(-r, w,-r));
        outer.add(new Vec3f(-r,-w, r));
        outer.add(new Vec3f(-r,-w,-r));

        ArrayList<Vec3f> inner = new ArrayList<>();
        for (Vec3f v : outer) inner.add(v.copy());

        switch(entity.getAxis()){
            case X -> inner.replaceAll((a) -> {
                a.multiplyComponentwise(1,0,0);
                return a;
            });
            case Y -> inner.replaceAll((a) -> {
                a.multiplyComponentwise(0,1,0);
                return a;
            });
            case Z -> inner.replaceAll((a) -> {
                a.multiplyComponentwise(0,0,1);
                return a;
            });
        }

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
        //faces.add(new IntFour(2, 3, 7, 6));
        //faces.add(new IntFour(0, 1, 5, 4));
        faces.add(new IntFour(1, 3, 7, 5));
        faces.add(new IntFour(0, 2, 6, 4));
        faces.add(new IntFour(4, 5, 7, 6));
        faces.add(new IntFour(0, 1, 3, 2));

        VertexConsumer arrowBuffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(ARROW_TEXTURE));
        Matrix4f m = matrixStack.peek().getPositionMatrix();
        Matrix3f n = matrixStack.peek().getNormalMatrix();

        float diagonal = (float)Math.sqrt(2)*radius;

        for (IntPair e : edges) {
            Vec3d i1 = new Vec3d(inner.get(e.a()));
            Vec3d i2 = new Vec3d(inner.get(e.b()));
            float il = (float)i1.distanceTo(i2)/2.f;
            Vec3d o1 = new Vec3d(outer.get(e.a()));
            Vec3d o2 = new Vec3d(outer.get(e.b()));
            float ol = (float)o1.distanceTo(o2)/2.f;
            if(entity.getPolarity() == 1){
                addVertex(m, n, arrowBuffer, outer.get(e.a()), ol,diagonal - animation);
                addVertex(m, n, arrowBuffer, outer.get(e.b()), -ol,diagonal - animation);
                addVertex(m, n, arrowBuffer, inner.get(e.b()), -il, -animation);
                addVertex(m, n, arrowBuffer, inner.get(e.a()), il, -animation);
            }else {
                addVertex(m, n, arrowBuffer, outer.get(e.a()), ol, -animation);
                addVertex(m, n, arrowBuffer, outer.get(e.b()), -ol, -animation);
                addVertex(m, n, arrowBuffer, inner.get(e.b()), -il, diagonal - animation);
                addVertex(m, n, arrowBuffer, inner.get(e.a()), il, diagonal - animation);
            }
        }
        VertexConsumer wallBuffer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(WALL_TEXTURE));
        for (IntFour f : faces) {
            renderFaceEdges(m, n, wallBuffer, outer.get(f.a()), outer.get(f.b()), outer.get(f.c()), outer.get(f.d()));
        }
    }
}
