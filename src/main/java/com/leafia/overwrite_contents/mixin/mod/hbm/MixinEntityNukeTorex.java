package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.interfaces.IConstantRenderer;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeTorex;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityNukeTorex.class)
public abstract class MixinEntityNukeTorex extends Entity implements IConstantRenderer, IMixinEntityNukeTorex {
	@Unique double initPosX;
	@Unique double initPosY;
	@Unique double initPosZ;
	@Unique boolean valid = false;
	@Override public double getInitPosX() { return initPosX; }
	@Override public double getInitPosY() { return initPosY; }
	@Override public double getInitPosZ() { return initPosZ; }
	@Override public boolean getValid() { return valid; }
	@Override public void setInitPosX(double value) { initPosX = value; }
	@Override public void setInitPosY(double value) { initPosY = value; }
	@Override public void setInitPosZ(double value) { initPosZ = value; }
	@Override public void setValid(boolean value) { valid = value; }
	public MixinEntityNukeTorex(World worldIn) {
		super(worldIn);
	}
}
