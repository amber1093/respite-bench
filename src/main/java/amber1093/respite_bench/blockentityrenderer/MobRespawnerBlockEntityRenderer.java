package amber1093.respite_bench.blockentityrenderer;

import amber1093.respite_bench.blockentity.MobRespawnerBlockEntity;
import amber1093.respite_bench.logic.MobRespawnerLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class MobRespawnerBlockEntityRenderer implements BlockEntityRenderer<MobRespawnerBlockEntity> {
	private final EntityRenderDispatcher entityRenderDispatcher;
	
	public MobRespawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.getEntityRenderDispatcher();
    }

	@Override
    public void render(MobRespawnerBlockEntity mobRespawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        MobRespawnerLogic logic = mobRespawnerBlockEntity.getLogic();
        Entity entity = logic.getRenderedEntity(mobRespawnerBlockEntity.getWorld(), mobRespawnerBlockEntity.getWorld().getRandom(), mobRespawnerBlockEntity.getPos());
        if (entity != null) {
            float g = 0.53125f;
            float h = Math.max(entity.getWidth(), entity.getHeight());
            if ((double)h > 1.0) {
                g /= h;
            }
            matrixStack.translate(0.0f, 0.4f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)MathHelper.lerp((double)f, logic.getLastRotation(), logic.getRotation()) * 5.0f));
            matrixStack.translate(0.0f, -0.2f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0f));
            matrixStack.scale(g, g, g);
            this.entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, f, matrixStack, vertexConsumerProvider, i);
        }
        matrixStack.pop();
    }
}
