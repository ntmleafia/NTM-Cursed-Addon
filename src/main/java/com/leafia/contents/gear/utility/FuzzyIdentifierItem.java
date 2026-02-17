package com.leafia.contents.gear.utility;

import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.interfaces.IHasCustomModel;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.llib.technical.FifthString;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FuzzyIdentifierItem extends AddonItemBase implements IItemFluidIdentifier, IHasCustomModel {
	public static final ModelResourceLocation fuzzyModel = new ModelResourceLocation("leafia:fuzzy_identifier", "inventory");
	public FuzzyIdentifierItem(String s) {
		super(s);
	}
	@Override
	public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
		}
	}
	public static FluidType getType(ItemStack stack) {
		if(!stack.hasTagCompound())
			return Fluids.NONE;

		String type = stack.getTagCompound().getString("fluidtype");
		return Fluids.fromName(type);
	}
	public void addInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn) {
		if (!(stack.getItem() instanceof FuzzyIdentifierItem))
			return;
		FluidType f = getType(stack);
		list.add(TextFormatting.GREEN + I18nUtil.resolveKey("desc.leafia.fuzzy.howto"));
		list.add("");
		if (f != null) {
			list.add(I18nUtil.resolveKey("desc.leafia.fuzzy.set"));
			list.add("   " + f.getLocalizedName());
		} else
			list.add(TextFormatting.RED+I18nUtil.resolveKey("desc.leafia.fuzzy.unset"));
	}
	static int index = 0;
	@Override
	public EnumActionResult onItemUse(EntityPlayer player,World worldIn,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof FuzzyIdentifierItem) {
			ItemStack stack = player.getHeldItem(hand);
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof TileEntityPipeBaseNT duct) {
				FluidType handType = getType(worldIn,pos.getX(),pos.getY(),pos.getZ(),player.getHeldItem(hand));
				if (handType != duct.getType()) {
					if (player.isSneaking()) {
						NBTTagCompound nbt = stack.getTagCompound();
						if (nbt == null) nbt = new NBTTagCompound();
						nbt.setString("fluidtype",duct.getType().getName());
						stack.setTagCompound(nbt);
						if (!worldIn.isRemote)
							worldIn.playSound(null,player.getPosition(),HBMSoundHandler.techBleep,SoundCategory.PLAYERS,1,1);
						else
							Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",duct.getType().getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
					} else
						spreadType(worldIn,pos,handType,duct.getType(),256);
				}
				return EnumActionResult.SUCCESS;
			} else if (te instanceof FFDuctTE duct) {
				FluidType handType = getType(worldIn,pos.getX(),pos.getY(),pos.getZ(),player.getHeldItem(hand));
				if (handType != duct.getType()) {
					if (player.isSneaking()) {
						NBTTagCompound nbt = stack.getTagCompound();
						if (nbt == null) nbt = new NBTTagCompound();
						nbt.setString("fluidtype",duct.getType().getName());
						stack.setTagCompound(nbt);
						if (!worldIn.isRemote)
							worldIn.playSound(null,player.getPosition(),HBMSoundHandler.techBleep,SoundCategory.PLAYERS,1,1);
						else
							Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",duct.getType().getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
					} else
						spreadTypeFF(worldIn,pos,handType,duct.getType(),256);
				}
				return EnumActionResult.SUCCESS;
			} else if (player.isSneaking()) {
				Block block = worldIn.getBlockState(pos).getBlock();
				if (block instanceof BlockDummyable dummyable) {
					BlockPos core = dummyable.findCore(worldIn,pos);
					if (core != null)
						te = worldIn.getTileEntity(core);
				}
				if (te != null) {
					if (te instanceof IFuzzyCompatible fz) {
						FluidType fluid = fz.getOutputType();
						NBTTagCompound nbt = stack.getTagCompound();
						if (nbt == null) nbt = new NBTTagCompound();
						nbt.setString("fluidtype",fluid.getName());
						stack.setTagCompound(nbt);
						if (!worldIn.isRemote)
							worldIn.playSound(null,player.getPosition(),HBMSoundHandler.techBleep,SoundCategory.PLAYERS,1,1);
						else
							Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",fluid.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
					} else if (te instanceof IFluidStandardSenderMK2 mk2) {
						if (!worldIn.isRemote) {
							FluidTankNTM[] sending = mk2.getSendingTanks();
							if (sending.length <= 0) return EnumActionResult.PASS;
							if (index >= sending.length) index = 0;
							FluidType fluid = sending[index].getTankType();
							index++;
							NBTTagCompound nbt = stack.getTagCompound();
							if (nbt == null) nbt = new NBTTagCompound();
							nbt.setString("fluidtype",fluid.getName());
							stack.setTagCompound(nbt);
							player.inventoryContainer.detectAndSendChanges();
							worldIn.playSound(null,player.getPosition(),HBMSoundHandler.techBleep,SoundCategory.PLAYERS,1,1);
							FuzzyIdentifierResponsePacket response = new FuzzyIdentifierResponsePacket();
							response.id = fluid.getID();
							LeafiaCustomPacket.__start(response).__sendToClient(player);
						}
					}
				}
			}
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	public static void spreadType(World worldIn, BlockPos pos, FluidType hand, FluidType pipe, int x){
		if(x > 0){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof TileEntityPipeBaseNT duct){
				if(duct.getType() == pipe){
					duct.setType(hand);
					duct.markDirty();
					spreadType(worldIn, pos.add(1, 0, 0), hand, pipe, x-1);
					spreadType(worldIn, pos.add(0, 1, 0), hand, pipe, x-1);
					spreadType(worldIn, pos.add(0, 0, 1), hand, pipe, x-1);
					spreadType(worldIn, pos.add(-1, 0, 0), hand, pipe, x-1);
					spreadType(worldIn, pos.add(0, -1, 0), hand, pipe, x-1);
					spreadType(worldIn, pos.add(0, 0, -1), hand, pipe, x-1);
				}
			}
		}
	}
	public static void spreadTypeFF(World worldIn, BlockPos pos, FluidType hand, FluidType pipe, int x){
		if(x > 0){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof FFDuctTE duct){
				if(duct.getType() == pipe){
					duct.setType(hand);
					duct.markDirty();
					spreadTypeFF(worldIn, pos.add(1, 0, 0), hand, pipe, x-1);
					spreadTypeFF(worldIn, pos.add(0, 1, 0), hand, pipe, x-1);
					spreadTypeFF(worldIn, pos.add(0, 0, 1), hand, pipe, x-1);
					spreadTypeFF(worldIn, pos.add(-1, 0, 0), hand, pipe, x-1);
					spreadTypeFF(worldIn, pos.add(0, -1, 0), hand, pipe, x-1);
					spreadTypeFF(worldIn, pos.add(0, 0, -1), hand, pipe, x-1);
				}
			}
		}
	}
	@Override
	public ModelResourceLocation getResourceLocation() {
		return fuzzyModel;
	}

	@Override
	public FluidType getType(World world,int i,int i1,int i2,ItemStack itemStack) {
		return getType(itemStack);
	}

	public static class FuzzyIdentifierPacket implements LeafiaCustomPacketEncoder {
		public String fluidRsc;
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeFifthString(new FifthString(fluidRsc));
		}
		@Nullable
		@Override
		public Consumer<MessageContext> decode(LeafiaBuf buf) {
			fluidRsc = buf.readFifthString().toString();
			return (ctx)->{
				ItemStack stack = ctx.getServerHandler().player.inventory.getItemStack();
				if (stack != null && !stack.isEmpty()) {
					if (stack.getItem() instanceof FuzzyIdentifierItem) {
						NBTTagCompound nbt = stack.getTagCompound();
						if (nbt == null) nbt = new NBTTagCompound();
						nbt.setString("fluidtype",fluidRsc);
						stack.setTagCompound(nbt);
						ctx.getServerHandler().player.updateHeldItem();
						ctx.getServerHandler().player.world.playSound(null,ctx.getServerHandler().player.getPosition(),HBMSoundHandler.techBleep,SoundCategory.PLAYERS,1,1);
					}
				}
			};
		}
	}
	public static class FuzzyIdentifierResponsePacket implements LeafiaCustomPacketEncoder {
		int id;
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeInt(id);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			int id = buf.readInt();
			return (ctx)->{
				FluidType fluid = Fluids.fromID(id);
				Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",fluid.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
			};
		}
	}
}
