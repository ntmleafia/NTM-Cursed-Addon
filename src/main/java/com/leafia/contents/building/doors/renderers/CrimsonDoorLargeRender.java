package com.leafia.contents.building.doors.renderers;

import com.hbm.interfaces.IDoor.DoorState;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.tileentity.door.IRenderDoors;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.leafia.AddonBase;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import java.nio.DoubleBuffer;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class CrimsonDoorLargeRender implements IRenderDoors {
	public static final CrimsonDoorLargeRender INSTANCE = new CrimsonDoorLargeRender();
	public static final ResourceLocation tex = getIntegrated("decoration/doors/crimdoorlarge/texture.png");
	public static final WaveFrontObjectVAO vao = getVAO(getIntegrated("decoration/doors/crimdoorlarge/crimdoorlarge.obj"));
	@Override
	public void render(TileEntityDoorGeneric te,DoubleBuffer doubleBuffer) {
		LeafiaGls.translate(0,0,0);
		double doorRatio = 0;
		if (te.state == DoorState.OPEN)
			doorRatio = 1;
		if (te.currentAnimation != null)
			doorRatio = IRenderDoors.getRelevantTransformation("DOOR",te.currentAnimation)[1];
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		{
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0,0,3*doorRatio);
			vao.renderPart("DoorLeft");
			LeafiaGls.popMatrix();
		}
		{
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0,0,-3*doorRatio);
			vao.renderPart("DoorRight");
			LeafiaGls.popMatrix();
		}
		vao.renderPart("Track");
		vao.renderPart("OuterFrame");
		vao.renderPart("InnerFrame");
		Minecraft.getMinecraft().getTextureManager().bindTexture(AddonBase.solid);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
		LeafiaGls.disableLighting();
		switch(te.getState()) {
			case OPEN -> LeafiaGls.color(0,1,0);
			case CLOSED -> LeafiaGls.color(1,0,0);
			default -> LeafiaGls.color(1,0.9f,0);
		}
		vao.renderPart("Light");
		LeafiaGls.enableLighting();
		LeafiaGls.color(1,1,1);
	}
}
