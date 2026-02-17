package com.leafia.contents.cannery.actions;

import com.hbm.wiaj.JarScene;
import com.hbm.wiaj.WorldInAJar;
import com.hbm.wiaj.actions.IJarAction;

/**
 * Shit that does ActionOffsetBy and ActionSetZoom at same time
 * @author ntmleafia
 */
public class ActionOffsetAndZoom implements IJarAction {

	int time;
	double motionX;
	double motionY;
	double motionZ;
	double zoom;

	public ActionOffsetAndZoom(double x,double y,double z,double zoom,int time) {
		this.motionX = x / (time + 1);
		this.motionY = y / (time + 1);
		this.motionZ = z / (time + 1);
		this.zoom = zoom / (time + 1);
		this.time = time;
	}

	@Override
	public int getDuration() {
		return this.time;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) {
		scene.script.offsetX += this.motionX;
		scene.script.offsetY += this.motionY;
		scene.script.offsetZ += this.motionZ;
		
		if(this.time == 0) {
			scene.script.lastOffsetX = scene.script.offsetX;
			scene.script.lastOffsetY = scene.script.offsetY;
			scene.script.lastOffsetZ = scene.script.offsetZ;
		}

		if(this.getDuration() == 0) {
			scene.script.lastZoom = scene.script.zoom = this.zoom;
		} else {
			scene.script.zoom += this.zoom;
		}
	}
}
