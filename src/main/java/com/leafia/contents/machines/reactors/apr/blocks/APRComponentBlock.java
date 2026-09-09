package com.leafia.contents.machines.reactors.apr.blocks;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.handler.NTMToolHandler;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.material.Mats;
import com.hbm.items.ModItems;
import com.hbm.util.EnumUtil;
import com.hbm.util.InventoryUtil;
import com.hbm.util.Tuple;
import com.leafia.contents.AddonItems;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.dev.blocks.blockbase.meta.IMetaPlacable;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APRComponentBlock extends AddonBlockBase implements IMetaPlacable, IToolable, ILookOverlay {
	@Override
	public String getTranslationKey(int meta) {
		return getTranslationKey()+"."+EnumUtil.grabEnumSafely(APRComponentType.values(),meta).getName();
	}
	@Override
	public void registerCustomMRL() {
		for (APRComponentType value : APRComponentType.values())
			ModelLoader.setCustomModelResourceLocation(
					Item.getItemFromBlock(this),
					value.ordinal(),
					new ModelResourceLocation(
							this.getRegistryName(),
							"variant="+value.getName()
					)
			);
	}
	public enum APRComponentType implements IStringSerializable {
		SUPPORT,
		HULL,
		HULL_ASSEMBLED,
		PLATING,
		PLATING_DARK,
		BLADES,
		MECHANICAL,
		COIL,
		;
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	public static final PropertyEnum<APRComponentType> VARIANT = PropertyEnum.create("variant",APRComponentType.class);
	public APRComponentBlock(String s) {
		super(Material.IRON,s);
		setSoundType(SoundType.METAL);
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> list,ITooltipFlag advanced) {
		MachineTooltip.addMultiblock(list);
		super.addInformation(stack,player,list,advanced);
	}
	@Override
	public boolean canCreatureSpawn(IBlockState state,IBlockAccess world,BlockPos pos,SpawnPlacementType type) {
		return false;
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,VARIANT);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT,EnumUtil.grabEnumSafely(APRComponentType.values(),meta));
	}
	@Override
	public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer,EnumHand hand) {
		return getDefaultState().withProperty(VARIANT,EnumUtil.grabEnumSafely(APRComponentType.values(),meta));
	}
	@Override
	public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,EntityPlayer player) {
		return new ItemStack(state.getBlock(),1,state.getValue(VARIANT).ordinal());
	}
	public List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) {
		APRComponentType variant = state.getValue(VARIANT);
		if (variant == APRComponentType.HULL_ASSEMBLED) {
			return Arrays.asList(
					new ItemStack(Item.getItemFromBlock(this),1,APRComponentType.HULL.ordinal()),
					new ItemStack(ModItems.bolt,1,Mats.MAT_STEEL.id),
					//new ItemStack(ModItems.pipe,1,Mats.MAT_COPPER.id)
					new ItemStack(AddonItems.blanket_mysticite)
			);
		}
		return Arrays.asList(new ItemStack(Item.getItemFromBlock(this),1,variant.ordinal()));
	}
	@Override
	public boolean onScrew(World world,EntityPlayer entityPlayer,int i,int i1,int i2,EnumFacing enumFacing,float v,float v1,float v2,EnumHand enumHand,ToolType toolType) {
		if (toolType == ToolType.TORCH) {
			BlockPos pos = new BlockPos(i,i1,i2);
			if (world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(VARIANT) == APRComponentType.HULL) {
				if (InventoryUtil.doesPlayerHaveAStacks(entityPlayer,Arrays.asList(new OreDictStack(OreDictManager.STEEL.bolt()),new ComparableStack(AddonItems.blanket_mysticite)),true)) {
					world.setBlockState(pos,getDefaultState().withProperty(VARIANT,APRComponentType.HULL_ASSEMBLED),3);
					return true;
				}
			}
		}
		return false;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
		ToolType tool = ToolType.getType(held);
		if (tool != ToolType.TORCH) return;

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this || state.getValue(VARIANT) != APRComponentType.HULL) return;
		int meta = getMetaFromState(state);

		List<String> text = new ArrayList<>();
		text.add(TextFormatting.GOLD + "Requires:");

		ItemStack bolt = new OreDictStack(OreDictManager.STEEL.bolt()).extractForCyclingDisplay(20);
		text.add("- " + bolt.getDisplayName() + " x1");
		text.add("- " + new ItemStack(AddonItems.blanket_mysticite).getDisplayName() + " x1");

		String blockName = new ItemStack(this, 1, meta).getDisplayName();
		ILookOverlay.printGeneric(event, blockName, 0xffff00, 0x404000, text);
	}
	@Override
	public void getSubBlocks(CreativeTabs itemIn,NonNullList<ItemStack> items) {
		for (APRComponentType value : APRComponentType.values())
			items.add(new ItemStack(this,1,value.ordinal()));
	}
}
