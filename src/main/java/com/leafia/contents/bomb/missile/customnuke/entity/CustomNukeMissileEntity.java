package com.leafia.contents.bomb.missile.customnuke.entity;

import com.hbm.blocks.bomb.NukeCustom;
import com.hbm.entity.missile.EntityMissileTier0;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import com.leafia.overwrite_contents.interfaces.IMixinEntityMissileBaseNT;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CustomNukeMissileEntity extends EntityMissileTier0 implements IMixinEntityMissileBaseNT {
	float tnt;
	float nuke;
	float hydro;
	float bale;
	float dirty;
	float schrab;
	float sol;
	float euph;

	@Override
	public boolean shouldDetonateInAir() {
		return (nuke > 0 || tnt >= 75) && schrab <= 0 && sol <= 0 && euph <= 0;
	}

	@Override
	public boolean interactABMissile() {
		if (tnt >= 75 || nuke > 0)
			return true;
		explodeAB();
		return false;
	}

	// not really unused, required for loading save
	public CustomNukeMissileEntity(World world) {
		super(world);
	}

	public CustomNukeMissileEntity(World world,float x,float y,float z,int tx,int tz,float tnt,float nuke,float hydro,float bale,float dirty,float schrab,float sol,float euph) {
		super(world,x,y,z,tx,tz);
		this.tnt = tnt;
		this.nuke = nuke;
		this.hydro = hydro;
		this.bale = bale;
		this.dirty = dirty;
		this.schrab = schrab;
		this.sol = sol;
		this.euph = euph;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.tnt = nbt.getFloat("custom_tnt");
		this.nuke = nbt.getFloat("custom_nuke");
		this.hydro = nbt.getFloat("custom_hydro");
		this.bale = nbt.getFloat("custom_bale");
		this.dirty = nbt.getFloat("custom_dirty");
		this.schrab = nbt.getFloat("custom_schrab");
		this.sol = nbt.getFloat("custom_sol");
		this.euph = nbt.getFloat("custom_euph");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("custom_tnt",this.tnt);
		nbt.setFloat("custom_nuke",this.nuke);
		nbt.setFloat("custom_hydro",this.hydro);
		nbt.setFloat("custom_bale",this.bale);
		nbt.setFloat("custom_dirty",this.dirty);
		nbt.setFloat("custom_schrab",this.schrab);
		nbt.setFloat("custom_sol",this.sol);
		nbt.setFloat("custom_euph",this.euph);
	}

	@Override
	public ItemStack getMissileItemForInfo() {
		return new ItemStack(AddonItems.missile_customnuke);
	}
	@Override
	public void onMissileImpact(RayTraceResult rayTraceResult) {
		NukeCustom.explodeCustom(this.world,null,this.posX,this.posY,this.posZ,
				this.tnt,
				this.nuke,
				this.hydro,
				this.bale,
				this.dirty,
				this.schrab,
				this.sol,
				this.euph
		);
	}
	@Override
	public ItemStack getDebrisRareDrop() {
		return new ItemStack(ModItems.circuit,3,EnumCircuitType.CONTROLLER_ADVANCED.ordinal());
	}
}
