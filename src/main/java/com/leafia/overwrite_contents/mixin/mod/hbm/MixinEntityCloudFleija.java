package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.items.ModItems;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.effects.folkvangr.visual.EntityCloudFleijaBase;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityCloudFleija.class)
public class MixinEntityCloudFleija extends Entity implements IMixinEntityCloudFleija {
	@Unique public double scale = 0;
	@Unique public float tickrate = 1;
	@Unique public float tickrate1 = 1;
	@Unique public float tickrate2 = 1;
	@Unique public EntityNukeFolkvangr bound = null;
	@Unique public boolean isAntischrab;

	@Override public boolean getIsAntischrab() { return isAntischrab; }
	@Override public void setIsAntischrab(boolean value) { isAntischrab = value; }
	@Override public double getScale() { return scale; }
	@Override public void setScale(double value) { scale = value; }
	@Override public EntityNukeFolkvangr getBound() { return bound; }
	@Override public void setBound(EntityNukeFolkvangr value) { bound = value; }

	public MixinEntityCloudFleija(World worldIn) {
		super(worldIn);
		this.setSize(1, 4);
		this.ignoreFrustumCheck = true;
		this.isImmuneToFire = true;
		this.isAntischrab = false;
		//this.age = 0;
		scale = 0;
		if (!worldIn.isRemote)
			tryBindAuto();
	}

	protected void tryBindAuto() {
		for (EntityNukeFolkvangr folkvangr : EntityNukeFolkvangr.awaitingBind) {
			for (EntityPlayer player : world.playerEntities) {
				if (player.getHeldItemMainhand().getItem() == ModItems.wand_d)
					player.sendMessage(new TextComponentString("Distance "+(folkvangr.getPositionVector().distanceTo(this.getPositionVector()))).setStyle(new Style().setColor(TextFormatting.YELLOW)));
			}
			if (folkvangr.getPositionVector().distanceTo(this.getPositionVector()) <= 1.5) {
				for (EntityPlayer player : world.playerEntities) {
					if (player.getHeldItemMainhand().getItem() == ModItems.wand_d)
						player.sendMessage(new TextComponentString("Bound").setStyle(new Style().setColor(TextFormatting.YELLOW)));
				}
				folkvangr.cloudBound = this;
				EntityNukeFolkvangr.awaitingBind.remove(folkvangr);
				bound = folkvangr;
				break;
			}
		}
	}

	@Override
	protected void entityInit() {

	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.scale = compound.getDouble("scale");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setDouble("scale",scale);
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 25000;
	}
}
