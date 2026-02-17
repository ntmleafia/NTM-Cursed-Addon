package com.leafia.contents.machines.elevators.items;

import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.elevators.EvPulleyTE;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EvSpawnItem extends AddonItemBase {
	public EvSpawnItem(String s) {
		super(s);
		setMaxStackSize(1);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addWIP(tooltip);
		tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("item.ev_spawn.desc").split("\\$")));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	public EvPulleyTE findPulley(World world,BlockPos basePos) {
		EvPulleyTE pulley = null;
		for (int i = basePos.getY(); i < 255; i++) {
			TileEntity te = world.getTileEntity(new BlockPos(basePos.getX(),i,basePos.getZ()));
			if (te instanceof EvPulleyTE) {
				pulley = (EvPulleyTE)te;
				return pulley;
			}
		}
		return null;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand paw) {
		ItemStack stack = player.getHeldItem(paw);
		FiaMatrix mat = new FiaMatrix(new Vec3d(player.posX,player.posY,player.posZ)).rotateY(-player.rotationYaw).translate(0,0,1.5);
		FiaMatrix mat2 = new FiaMatrix().rotateY(-player.rotationYaw).rotateX(player.rotationPitch).translate(0,0,1);
		BlockPos pos = new BlockPos(mat.position.add(0,0.5+mat2.position.y,0));
		EvPulleyTE pulley = findPulley(world,pos);
		if (pulley != null) {
			EnumFacing face = player.getHorizontalFacing();
			if (face.equals(EnumFacing.byIndex(pulley.getBlockMetadata()-10))) {
				if (!world.isRemote) {
					ElevatorEntity elevator = new ElevatorEntity(world);
					if (stack.hasTagCompound()) {
						NBTTagCompound compound = stack.getTagCompound();
						if (compound.hasKey("configuration"))
							elevator.loadData = compound.getCompoundTag("configuration");
					}
					elevator.setLocationAndAngles(
							elevator.posX = pos.getX()+0.5,
							elevator.posY = pos.getY()+0.1,
							elevator.posZ = pos.getZ()+0.5,
							face.getHorizontalAngle()+180,
							0
					);
					world.spawnEntity(elevator);
				}
				return new ActionResult<>(EnumActionResult.SUCCESS,ItemStack.EMPTY);
			} else {
				if (world.isRemote)
					player.sendMessage(new TextComponentTranslation("item.ev_spawn.error.direction").setStyle(new Style().setColor(TextFormatting.RED)));
			}
		} else {
			if (world.isRemote)
				player.sendMessage(new TextComponentTranslation("item.ev_spawn.error.pulley").setStyle(new Style().setColor(TextFormatting.RED)));
		}
		return super.onItemRightClick(world,player,paw);
	}
}
