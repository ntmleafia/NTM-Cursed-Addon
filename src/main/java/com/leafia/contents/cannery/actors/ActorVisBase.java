package com.leafia.contents.cannery.actors;

import com.hbm.wiaj.JarScene;
import com.hbm.wiaj.JarScript;
import com.hbm.wiaj.WorldInAJar;
import com.hbm.wiaj.actors.ISpecialActor;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ActorVisBase implements ISpecialActor {
	final JarScript script;
	final int x;
	final int y;
	final int z;
	final int color;
	public ActorVisBase(JarScript script,int x,int y,int z,int color) {
		this.script = script;
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
	}
	@Override
	public void drawForegroundComponent(int w, int h, int ticks, float interp) { }
	@Override
	public void drawBackgroundComponent(WorldInAJar world, int ticks, float interp) {
		LeafiaGls.pushMatrix();
		LeafiaGls._push();
		LeafiaGls.resetEffects();
		LeafiaGls.enableDepth();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE);
		LeafiaGls.translate(x,y,z);
		renderContents();
		LeafiaGls._pop();
		LeafiaGls.popMatrix();
	}
	public abstract void renderContents();
	@Override
	public void tick(JarScene jarScene) {
	}
	@Override
	public void setActorData(NBTTagCompound nbtTagCompound) {
	}
	@Override
	public void setDataPoint(String s,Object o) {
	}
}
