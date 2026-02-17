package com.leafia.contents.machines.elevators.weight;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nullable;

import static com.leafia.contents.machines.elevators.car.ElevatorRender.model;
import static com.leafia.contents.machines.elevators.car.ElevatorRender.support;

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
		LeafiaGls.popMatrix();
	}
	@Override
	protected @Nullable ResourceLocation getEntityTexture(EvWeightEntity entity) {
		return support;
	}
}
