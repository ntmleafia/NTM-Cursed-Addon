package com.leafia.contents.miscellanous.diverter;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.settings.AddonConfig;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiverterBlock extends AddonBlockBase implements ITileEntityProvider, ITooltipProvider, ILookOverlay {
	private final boolean isOn;
	protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);
	public DiverterBlock(String s,boolean isOn){
		super(Material.ROCK,s);
		this.isOn = isOn;
		this.setTranslationKey("meteor_diverter");
	}

	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		if (!worldIn.isRemote) {
			if (placer instanceof EntityPlayer plr) {
				TileEntity te = worldIn.getTileEntity(pos);
				if (te instanceof DiverterTE diverter)
					diverter.placerName = plr.getName();
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos) {
		return STANDING_AABB;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn,IBlockState state,BlockPos pos,EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	@Override
	public ItemStack getItem(World worldIn,BlockPos pos,IBlockState state) {
		return new ItemStack(AddonBlocks.diverter_unlit);
	}
	@Override
	public boolean isAssociatedBlock(Block other) {
		return other == AddonBlocks.diverter || other == AddonBlocks.diverter_unlit;
	}

	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new DiverterTE();
	}

	@Override
	public Item getItemDropped(IBlockState state,Random rand,int fortune) {
		return Item.getItemFromBlock(AddonBlocks.diverter_unlit);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void printHook(Pre pre,World world,BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof DiverterTE diverter) {
			List<String> text = new ArrayList<>();
			int color = 0x55FF55;
			if (diverter.isError) {
				color = 0xFF0000;
				text.add("&["+color+"&]Error");
			} else {
				if (diverter.timeRemaining > 0) {
					color = 0xFF0000;
					long secs = diverter.timeRemaining/20;
					String formatted = String.format("%01d:%02d:%02d",secs/3600,(secs/60)%60,secs%60);
					text.add("&["+color+"&]"+I18nUtil.resolveKey("tile.meteor_diverter.message.inactive"));
					text.add(I18nUtil.resolveKey("tile.meteor_diverter.message.time",formatted));
				} else
					text.add("&["+color+"&]"+I18nUtil.resolveKey("tile.meteor_diverter.message.active"));
			}
			text.add(I18nUtil.resolveKey("tile.meteor_diverter.placer",diverter.placerName));
			ILookOverlay.printGeneric(pre,getLocalizedName(),0x00FFFF,0x004040,text);
		}
	}
	public static boolean isProtected(World world,int ccX,int ccZ) {
		int radius = AddonConfig.meteorDiverterProtectionRadius;
		for (int cx = ccX-radius; cx <= ccX+radius; cx++) {
			for (int cz = ccZ-radius; cz <= ccZ+radius; cz++) {
				Chunk chunk = world.getChunk(cx,cz);
				for (TileEntity te : chunk.getTileEntityMap().values()) {
					if (!te.isInvalid() && te instanceof DiverterTE div) {
						if (div.timeRemaining <= 0)
							return true;
					}
				}
			}
		}
		return false;
	}
}
