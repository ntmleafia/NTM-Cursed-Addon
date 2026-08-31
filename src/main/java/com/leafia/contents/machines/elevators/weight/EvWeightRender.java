package com.leafia.contents.machines.elevators.weight;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;

import static com.leafia.contents.machines.elevators.car.ElevatorRender.*;

public class EvWeightRender extends Render<EvWeightEntity> {
	public static final IRenderFactory<EvWeightEntity> FACTORY = EvWeightRender::new;
	public static final WaveFrontObjectVAO mdl = model("weight");
	protected EvWeightRender(RenderManager renderManager) {
		super(renderManager);
	}
	@Override
	public void doRender(EvWeightEntity entity,double x,double y,double z,float entityYaw,float partialTicks) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x,y,z);
		LeafiaGls.rotate(entityYaw,0,-1,0);
		bindTexture(support);
		mdl.renderAll();
		if (entity.pulley != null) {
			bindTexture(cable);
			LeafiaGls.pushMatrix();
			LeafiaBrush brush = LeafiaBrush.instance;
			Vec3d pos = new Vec3d(
					entity.prevPosX+(entity.posX-entity.prevPosX)*partialTicks,
					entity.prevPosY+(entity.posY-entity.prevPosY)*partialTicks+3.0625,
					entity.prevPosZ+(entity.posZ-entity.prevPosZ)*partialTicks
			);
			Vec3d pulleyPos = new Vec3d(entity.pulley.getPos()).add(0.5,0,0.5).add(new Vec3d(EnumFacing.byIndex(entity.pulley.getBlockMetadata()-10).rotateY().getDirectionVec()).scale(1.4062));
			Vec3d difference = pulleyPos.subtract(pos);
			LeafiaGls.translate(0,3.0625,0);
			brush.startDrawingQuads();
			for (int i = -2; i <= 2; i++)
				ElevatorRender.renderCable(new Vec3d(0,0,i*0.0625*2),difference.add(0,0,i*0.0625*2),0.0625);
			brush.draw();
			LeafiaGls.popMatrix();
		}
		LeafiaGls.popMatrix();
	}
	@Override
	protected @Nullable ResourceLocation getEntityTexture(EvWeightEntity entity) {
		return support;
	}
}
