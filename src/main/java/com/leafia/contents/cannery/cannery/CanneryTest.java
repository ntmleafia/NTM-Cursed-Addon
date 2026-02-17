package com.leafia.contents.cannery.cannery;

import com.hbm.blocks.ModBlocks;
import com.hbm.wiaj.JarScene;
import com.hbm.wiaj.JarScript;
import com.hbm.wiaj.WorldInAJar;
import com.hbm.wiaj.actions.ActionCreateActor;
import com.hbm.wiaj.actions.ActionRotateBy;
import com.hbm.wiaj.actions.ActionSetBlock;
import com.hbm.wiaj.actions.ActionWait;
import com.hbm.wiaj.cannery.CanneryBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.cannery.actors.ActorVisFrame;
import com.leafia.contents.cannery.actors.ActorVisText;
import net.minecraft.item.ItemStack;

public class CanneryTest extends CanneryBase {
	@Override
	public ItemStack getIcon() {
		return new ItemStack(PWR.element);
	}
	@Override
	public String getName() {
		return "cannery.test";
	}
	@Override
	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(5, 5, 5);
		JarScript script = new JarScript(world);
		{
			JarScene scene = new JarScene(script);
			for (int x = world.sizeX-1; x >= 0; x--) {
				for (int z = 0; z < world.sizeZ; z++)
					scene.add(new ActionSetBlock(x,0,z,ModBlocks.concrete_smooth));

				scene.add(new ActionWait(2));
			}
			scene.add(new ActionCreateActor(0,new ActorVisFrame(script,1,1,1,0x00FF00)));
			scene.add(new ActionCreateActor(1,new ActorVisText(script,1,1,1,0x00FF00,1,"Test")));
			scene.add(new ActionWait(20));
			scene.add(new ActionRotateBy(45,-60,20));

			script.addScene(scene);
		}

		return script;
	}
}
