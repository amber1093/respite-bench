package amber1093.respite_bench.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;

public class BenchEntityRenderer extends DisplayEntityRenderer<BenchEntity, BenchEntityModel> {

    protected BenchEntityRenderer(Context context) {
        super(context);
    }

    @Override
    protected BenchEntityModel getData(BenchEntity var1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getData'");
    }

    @Override
    protected void render(BenchEntity var1, BenchEntityModel var2, MatrixStack var3, VertexConsumerProvider var4, int var5, float var6) {
    }
}
