package com.leafia.init.hazards.types;

import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.I18nUtil;
import com.leafia.dev.optimization.LeafiaParticlePacket.AlkaliFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class HazardTypeAlkaline implements IHazardType {
	@Override
	public void onUpdate(EntityLivingBase entity,double v,ItemStack held) {

		if(!entity.world.isRemote && entity.isInWater()) {

			int damage = reactAlkaline(entity.world,held,entity.posX,entity.posY,entity.posZ,(int)v);
			if (damage > 200) {
				if (entity instanceof EntityPlayer player) {
					held.shrink(held.getCount());
					//player.inventory.mainInventory.set(player.inventory.currentItem,held.getItem().getContainerItem(held));
					player.inventoryContainer.detectAndSendChanges();
				}
			}

			//player.world.newExplosion(null, player.posX, player.posY + player.getEyeHeight() - player.getYOffset(), player.posZ, 2F, true, true);
		}
		if (tickAlkaline(entity.world,held,entity.posX,entity.posY,entity.posZ,(int)v))
			entity.setFire(3);
	}
	@Override
	public void updateEntity(EntityItem item,double v) {
		item.setEntityInvulnerable(true); // fuck you
		if((item.isInWater() || item.world.isRainingAt(new BlockPos((int)item.posX, (int)item.posY, (int)item.posZ)) || item.world.getBlockState(new BlockPos((int)item.posX, (int)item.posY, (int)item.posZ)).getMaterial() == Material.WATER)) {
			//item.setDead();
			//item.world.newExplosion(item, item.posX, item.posY, item.posZ, 2F, true, true);
			int damage = reactAlkaline(item.world,item.getItem(),item.posX,item.posY,item.posZ,(int)v);
			if (damage > 200) {
				item.setDead();
				return;
			}
		} else
			getNBT(item.getItem()).removeTag("damage");
		tickAlkaline(item.world,item.getItem(),item.posX,item.posY,item.posZ,(int)v);
	}
	@Override
	public void addHazardInformation(EntityPlayer entityPlayer,List<String> list,double v,ItemStack itemStack,List<IHazardModifier> list1) {
		if (v == (int)v) // retarded way to remove the decimals
			list.add(TextFormatting.RED + "[" + I18nUtil.resolveKey("trait._hazarditem.hydro") + " " + (int)v + "]");
		else
			list.add(TextFormatting.RED + "[" + I18nUtil.resolveKey("trait._hazarditem.hydro") + " " + v + "]");
	}


	/// LCE ZONE ///
	NBTTagCompound getNBT(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		return nbt;
	}
	boolean tickAlkaline(World world,ItemStack stack,double x,double y,double z,int alkaline) {
		NBTTagCompound nbt = getNBT(stack);
		int flaming = nbt.getInteger("flaming");
		if (flaming > 0) {
			AlkaliFire particle = new AlkaliFire(alkaline);
			particle.emit(new Vec3d(x,y,z),new Vec3d(0,0,0),world.provider.getDimension());
			Random prand = new Random(Math.floorDiv((int)(x+y*200+z*40000),100000));
			for (int i = 0; i <= alkaline-1; i++) {
				BlockPos pos = new BlockPos(x,y,z);
				if (i > 0) {
					int range = (int)(i*0.52)+1;
					pos = pos.add(prand.nextInt(range*2)-range,prand.nextInt(range*2)-range,prand.nextInt(range*2)-range);
				}
				if (world.isValid(pos) && world.rand.nextBoolean()) {
					IBlockState state = world.getBlockState(pos);
					if ((state.getMaterial().isReplaceable() || state.getBlock().isReplaceable(world,pos)) && !state.getMaterial().isLiquid())
						world.setBlockState(pos,Blocks.FIRE.getDefaultState());
				}
			}
			if (flaming > 1)
				nbt.setInteger("flaming",Math.max(flaming-hazardRate,0));
			else
				nbt.removeTag("flaming");
			return true;
		}
		return false;
	}
	int reactAlkaline(World world,ItemStack stack,double x,double y,double z,int alkaline) {
		NBTTagCompound nbt = getNBT(stack);
		int damage = nbt.getInteger("damage");
		int flaming = nbt.getInteger("flaming");
		int add = 0;
		switch(alkaline) {
			case 1:
				if (flaming > 0 || world.rand.nextInt(20) == 0) {
					flaming = 240;
					add = 1;
				}
				break;
			case 2:
				flaming = 240;
				add = 5;
				break;
			case 3:
				flaming = 240;
				add = 2;
				break;
			case 4:
				world.newExplosion(null,x,y,z,1,true,false);
				add = 1000;
				break;
			case 5:
				world.newExplosion(null,x,y,z,3,true,true);
				add = 1000;
				break;
			case 6:
				world.newExplosion(null,x,y,z,6,true,true);
				ContaminationUtil.radiate(world,x,y,z,9,20);
				add = 1000;
				break;
		}
		if (damage > 0)
			nbt.setInteger("damage",damage+add);
		else
			nbt.removeTag("damage");
		if (flaming > 1)
			nbt.setInteger("flaming",Math.max(flaming-1,0));
		else
			nbt.removeTag("flaming");
		return damage+add;
	}
}
