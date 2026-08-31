package com.hbm.render.model;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RadGlassesModel extends ModelArmorBase {
	public RadGlassesModel(int type) {
		super(type);
		// please fuck off
		head = new ModelRendererObj(ResourceManager.armor_goggles);
		body = new ModelRendererObj(ResourceManager.armor_bj, "Body");
		leftArm = new ModelRendererObj(ResourceManager.armor_bj, "LeftArm").setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightArm = new ModelRendererObj(ResourceManager.armor_bj, "RightArm").setRotationPoint(5.0F, 2.0F, 0.0F);
		leftLeg = new ModelRendererObj(ResourceManager.armor_bj, "LeftLeg").setRotationPoint(1.9F, 12.0F, 0.0F);
		rightLeg = new ModelRendererObj(ResourceManager.armor_bj, "RightLeg").setRotationPoint(-1.9F, 12.0F, 0.0F);
		leftFoot = new ModelRendererObj(ResourceManager.armor_bj, "LeftFoot").setRotationPoint(1.9F, 12.0F, 0.0F);
		rightFoot = new ModelRendererObj(ResourceManager.armor_bj, "RightFoot").setRotationPoint(-1.9F, 12.0F, 0.0F);
	}
	public static ResourceLocation radgoggles = new ResourceLocation("leafia","textures/armor/goggle_rad.png");
	@Override
	public void renderArmor(Entity par1Entity,float par7) {
		if(type == 0) {
			Minecraft.getMinecraft().renderEngine.bindTexture(radgoggles);
			head.render(par7*1.001F);
		}
	}
}
