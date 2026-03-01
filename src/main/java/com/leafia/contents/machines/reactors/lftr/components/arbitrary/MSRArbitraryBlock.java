package com.leafia.contents.machines.reactors.lftr.components.arbitrary;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockMachineBase;
import com.hbm.items.tool.ItemTooling;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.InventoryHelper;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MSRArbitraryBlock extends BlockMachineBase implements ITooltipProvider, ILookOverlay {
	public MSRArbitraryBlock(Material mat,String s) {
		super(mat,-1,s);
		this.setTranslationKey(s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new MSRArbitraryTE();
	}

	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof MSRArbitraryTE element))
			return false;

		if (element.inventory == null) return false;

		ItemStack held = player.getHeldItem(hand);
		ItemStack stack = element.inventory.getStackInSlot(0);
		if (held.isEmpty()) return false;
		if (stack.isEmpty()) {
			if (held.getItem() instanceof ItemBlock block) {
				if (held.getItem() instanceof ItemTooling)
					return false;
				if (block.getBlock() instanceof IBlockProperties properties) {
					if (!properties.getRenderType().equals(EnumBlockRenderType.MODEL))
						return false;
				}
				ItemStack stack1 = held.copy();
				stack1.setCount(1);
				element.inventory.setStackInSlot(0,stack1);
				if (world.isRemote) return true;
				held.shrink(1);
				player.inventoryContainer.detectAndSendChanges();
				world.playSound(null,pos,HBMSoundHandler.upgradePlug,SoundCategory.BLOCKS,1,1);
				entity.markDirty();
				return true;
			}
			return false;
		} else {
			if (held.getItem() instanceof ItemTooling) {
				if (((ItemTooling)(held.getItem())).getType().equals(IToolable.ToolType.SCREWDRIVER)) {
					element.inventory.setStackInSlot(0,ItemStack.EMPTY);
					if (world.isRemote) return true;
					InventoryHelper.spawnItemStack(world,player.posX,player.posY,player.posZ,stack);
					held.damageItem(1,player);
					world.playSound(null,pos,HBMSoundHandler.lockHang,SoundCategory.BLOCKS,0.85f,1);
					world.playSound(null,pos,HBMSoundHandler.pipePlaced,SoundCategory.BLOCKS,0.65f,0.8f);
					entity.markDirty();
					return true;
				}
			}
		}

		return false; //super.onBlockActivated(world,pos,state,player,hand,facing,hitX,hitY,hitZ);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL; // grrrrwl
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof MSRArbitraryTE))
			return;
		List<String> texts = new ArrayList<>();
		MSRArbitraryTE element = (MSRArbitraryTE)entity;

		if (element.inventory != null) {
			ItemStack stack = element.inventory.getStackInSlot(0);
			if (stack.isEmpty())
				texts.add("Empty");
			else {
				texts.add(stack.getDisplayName());
				stack.getItem().addInformation(stack,world,texts,ITooltipFlag.TooltipFlags.NORMAL);
			}
		}

		MSRTEBase.appendPrintHook(texts,world,pos);

		LeafiaGls.pushMatrix();
		LeafiaGls.scale(0.5);
		ScaledResolution resolution = event.getResolution();
		LeafiaGls.translate(resolution.getScaledHeight_double(),resolution.getScaledHeight_double()/2,0);
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xFF55FF, 0x3F153F, texts);
		LeafiaGls.popMatrix();
	}
}
