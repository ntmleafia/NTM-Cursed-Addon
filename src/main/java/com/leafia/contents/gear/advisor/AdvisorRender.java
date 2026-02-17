package com.leafia.contents.gear.advisor;

import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.dev.items.LeafiaGripOffsetHelper;
import com.leafia.dev.items.LeafiaGripOffsetHelper.LeafiaGripOffset;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static com.leafia.init.ResourceInit.getVAO;

public class AdvisorRender extends TEISRBase {
	static final String basePath = "textures/_integrated/advisor/";
	public static final WaveFrontObjectVAO mdl = getVAO(new ResourceLocation("leafia",basePath+"ugly af.obj"));
	public static final ResourceLocation base = ResourceManager.control_panel_custom_tex;
	public static final ResourceLocation solid = AddonBase.solid;
	public static final ResourceLocation hta = new ResourceLocation("leafia",basePath+"hta.png");
	public static final ResourceLocation solar = new ResourceLocation("leafia",basePath+"solar.png");
	public static final ResourceLocation speaker = new ResourceLocation("leafia",basePath+"speaker.png");
	public static final ResourceLocation logo = new ResourceLocation("leafia",basePath+"logo.png");
	public static final LeafiaColor hinge = new LeafiaColor(0.4,0.4,0.4);
	public static final LeafiaColor instrument = new LeafiaColor(0.75,0.75,0.75);

	public static LeafiaGripOffsetHelper grip = new LeafiaGripOffsetHelper()
			.get(TransformType.GUI)
			.setScale(1.15).setPosition(0.5,0,-0.275).setRotation(0,0,0).getHelper()

			.get(TransformType.GROUND)
			.setScale(0.5).setPosition(0.5,0.7,-2.05).setRotation(85,0,0).getHelper()

			.get(TransformType.FIRST_PERSON_RIGHT_HAND)
			.setScale(1).setPosition(-0.35,0.5,-0.05).setRotation(-10,0,-30).getHelper()

			.get(TransformType.FIRST_PERSON_LEFT_HAND)
			.setPosition(1.15,1.35,0.05).setRotation(0,-5,75).getHelper()

			.get(TransformType.THIRD_PERSON_RIGHT_HAND)
			.setScale(0.5).setPosition(0,0.55,-1.5).setRotation(55,-90,0).getHelper()

			.get(TransformType.FIXED)
			.setScale(1).setPosition(0.5,0.45,-0.4).setRotation(-5,0,0).getHelper();

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(-0.5,0,0.5);
		grip.apply(type);
		{
			// BASE
			bindTexture(base);
			mdl.renderPart("base");
			bindTexture(solid);
			setColor(hinge);
			mdl.renderPart("hingeBase");
			LeafiaGls.color(0,0,0);
			mdl.renderPart("screen");
			LeafiaGls.color(1,1,1);
			bindTexture(logo);
			mdl.renderPart("logo");
			bindTexture(solar);
			mdl.renderPart("solar");
		}
		if (itemStackIn.hasTagCompound() && itemStackIn.getTagCompound().getBoolean("open") && type != TransformType.GUI) {
			LeafiaGls.translate(0.6875,0.0625,0);
			LeafiaGls.rotate(180,0,0,1);
			LeafiaGls.translate(-0.6875,-0.0625,0);
		}
		{
			// COVER
			bindTexture(base);
			mdl.renderPart("cover");
			bindTexture(solid);
			setColor(hinge);
			mdl.renderPart("hingeCover");
			LeafiaGls.color(0,0,0);
			mdl.renderPart("antenna");
			setColor(instrument);
			mdl.renderPart("instrument");
			LeafiaGls.color(1,1,1);
			bindTexture(speaker);
			mdl.renderPart("speakers");
			mdl.renderPart("speakersIn");
			bindTexture(hta);
			mdl.renderPart("hta");
		}
		LeafiaGls.popMatrix();
	}
	public static void setColor(LeafiaColor color) {
		LeafiaGls.color(color.getRed(),color.getGreen(),color.getBlue());
	}
	public static void bindTexture(ResourceLocation loc) {
		NTMRenderHelper.bindTexture(loc);
	}
}
