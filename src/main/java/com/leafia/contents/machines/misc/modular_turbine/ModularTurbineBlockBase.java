// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.ForgeDirection;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.util.BobMathUtil;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks.ModularTurbines;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreBlock;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;
import com.leafia.contents.machines.misc.modular_turbine.ports.IMTPortBlock;
import com.leafia.contents.machines.misc.modular_turbine.ports.MTComponentPortTE;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.SIPfx;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.leafia.AddonBase.getIntegrated;

public abstract class ModularTurbineBlockBase extends AddonBlockDummyable implements IToolable, ILookOverlay, IMTStageUpgradeContributor, IMTMachineUpgradeContributor {
	public ModularTurbineBlockBase(String s) {
		super(Material.IRON,s);
		setHardness(5);
		setResistance(50);
		setCreativeTab(MainRegistry.machineTab);
		ModularTurbines.ALL_COMPONENTS_FOR_RENDER.add(this);
	}
	public abstract int shaftHeight();
	public enum TurbineComponentType {
		BLADES(true),
		SEPARATOR,PORT(true),
		INLINE_PORT, // special
		FLYWHEEL,
		SHAFT_ONLY,
		GENERATOR,
		;
		public final boolean isSeparator;
		TurbineComponentType(boolean needsSeparation) {
			this.isSeparator = !needsSeparation;
		}
		TurbineComponentType() {
			isSeparator = true;
		}
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addBeta(tooltip);
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		if (this instanceof MTCoreBlock)
			MachineTooltip.addCore(tooltip);
		if (variant().equals("glass"))
			tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("info.turbine._tooltip.glass").split("\\$")));
		tooltip.add(TextFormatting.AQUA+I18nUtil.resolveKey("info.turbine.weight","+"+weight()+" WU"));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	public abstract TurbineComponentType componentType();
	public String variant() {
		return "default";
	}
	/// Sizes this component can connect to
	/// <p>Empty array means all components.
	public abstract int[] canConnectTo();
	public abstract int size();
	public abstract double weight();
/*
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return getRenderLayer() == layer;
	}*/

	@Override
	public void onBlockPlacedBy(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state,
                                @NotNull EntityLivingBase placer, @NotNull ItemStack itemStack) {
        if (!(placer instanceof EntityPlayer player)) return;
        safeRem = true;
        try {
            world.setBlockToAir(pos);
        } finally {
            safeRem = false;
        }
        var hand = player.getHeldItemMainhand() == itemStack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        double yaw = placer.rotationYaw + (placer.isSneaking() ? 180D : 0D);
        int quarter = MathHelper.floor(yaw * 4F / 360F + 0.5D) & 3;
        var facing = getDirModified(ForgeDirection.getOrientation(EnumFacing.byHorizontalIndex(quarter).getOpposite()));
        int height = getHeightOffset();
        int distance = -getOffset();
        var anchor = pos.up(height);
        if (placer.isSneaking()) anchor = anchor.offset(facing.toEnumFacing(), getDimensions()[2] - distance);
        if (!checkRequirement(world, anchor.getX(), anchor.getY(), anchor.getZ(), facing, distance)) {
            refundPlacement(player, hand, itemStack);
            return;
        }
        if (!world.isRemote) {
            var core = anchor.offset(facing.toEnumFacing(), distance);
            int metadata = getMetaForCore(world, core, player, facing.ordinal() + offset);
            world.setBlockState(core, getDefaultState().withProperty(META, metadata), 3);
            IPersistentNBT.onBlockPlacedBy(world, core, itemStack);
            fillSpace(world, anchor.getX(), anchor.getY(), anchor.getZ(), facing, distance);
        }
        var updatePos = anchor.down(height);
        world.scheduleUpdate(updatePos, this, 1);
        world.scheduleUpdate(updatePos, this, 2);
    }

    private static void refundPlacement(EntityPlayer player, EnumHand hand, ItemStack placementStack) {
        if (player.capabilities.isCreativeMode) return;
        var refund = placementStack.copy();
        refund.setCount(1);
        var held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            player.setHeldItem(hand, refund);
            return;
        }
        // ItemBlock consumes the placement stack after onBlockPlacedBy returns.
        if (ItemHandlerHelper.canItemStacksStack(held, refund)
                && held.getCount() < Math.min(held.getMaxStackSize(), player.inventory.getInventoryStackLimit())) {
            held.grow(1);
            return;
        }
        player.inventory.addItemStackToInventory(refund);
        if (!refund.isEmpty() && !player.world.isRemote) player.dropItem(refund, false, false);
    }
	@SideOnly(Side.CLIENT)
	void addCoreInfo(MTCoreTE c,List<String> texts) {
		if (c.turbulence > 20) {
			texts.add("&[" + (BobMathUtil.getBlink() ? 0xff0000 : 0xffff00) + "&]"+I18nUtil.resolveKey("info.turbine.turbulence.warning"));
			if (c.turbulenceReasonInputSurge || c.turbulenceReasonInverseBlades || c.turbulenceReasonTooManyBlades) {
				texts.add(TextFormatting.GOLD+I18nUtil.resolveKey("info.turbine.turbulence.reasons"));
				if (c.turbulenceReasonInputSurge)
					texts.add(TextFormatting.GOLD+"- "+I18nUtil.resolveKey("info.turbine.turbulence.reason.surge"));
				if (c.turbulenceReasonInverseBlades)
					texts.add(TextFormatting.GOLD+"- "+I18nUtil.resolveKey("info.turbine.turbulence.reason.wrongblades"));
				if (c.turbulenceReasonTooManyBlades)
					texts.add(TextFormatting.GOLD+"- "+I18nUtil.resolveKey("info.turbine.turbulence.reason.toomanyblades"));
			}
		}
		texts.add(I18nUtil.resolveKey("info.turbine.rps",String.format("%01.2f",c.rps)));
		texts.add(TextFormatting.GRAY+"- "+I18nUtil.resolveKey("info.turbine.overdrive","+"+MTCoreTE.getRPSBoost(c.overdrive)));
		if (c.overdrive > 0)
			texts.add(TextFormatting.GRAY+"- "+I18nUtil.resolveKey("info.turbine.downgrade"));
		texts.add(I18nUtil.resolveKey("info.turbine.weight",String.format("%01.2f WU",c.weight)));
		texts.add(I18nUtil.resolveKey("info.turbine.turbulence",String.format("%01.2f%%",c.turbulence)));
		//texts.add(I18nUtil.resolveKey("info.turbine.gear",String.format("%01.2f",c.globalGearScale)));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		List<String> texts = new ArrayList<>();
		BlockPos core = findCore(world,pos);
		if (core != null) {
			if (world.getTileEntity(core) instanceof MTCoreTE te) {
				if (te.components.isEmpty())
					texts.add("&[" + (BobMathUtil.getBlink() ? 0xff0000 : 0xffff00) + "&]"+I18nUtil.resolveKey("info.turbine.assembly.unassembled"));
				else
					addCoreInfo(te,texts);
			} else if (world.getTileEntity(core) instanceof ModularTurbineComponentTE te) {
				MTCoreTE c = te.core;
				if (c == null)
					texts.add("&[" + (BobMathUtil.getBlink() ? 0xff0000 : 0xffff00) + "&]"+I18nUtil.resolveKey("info.turbine.assembly.unassembled"));
				else {
					addCoreInfo(c,texts);
				}
				if (te instanceof MTComponentPortTE port) {
					texts.add(I18nUtil.resolveKey("info.turbine.identifier",port.identifier != null ? port.identifier.getLocalizedName() : "N/A"));
					if (!port.decompress)
						texts.add(I18nUtil.resolveKey("info.turbine.decompression",I18nUtil.resolveKey(port.decompress ? "info.turbine.state.enabled" : "info.turbine.state.disabled")));
					if (te.assembly != null) {
						texts.add(TextFormatting.GREEN+"-> "+TextFormatting.RESET+te.assembly.input.getTankType().getLocalizedName()+": "+te.assembly.input.getFill()+"/"+te.assembly.input.getMaxFill()+"mB");
						texts.add(TextFormatting.RED+"<- "+TextFormatting.RESET+te.assembly.output.getTankType().getLocalizedName()+": "+te.assembly.output.getFill()+"/"+te.assembly.output.getMaxFill()+"mB");
					}
				}
				if (te.core != null)
					texts.add(TextFormatting.RED+"-> "+TextFormatting.RESET+SIPfx.auto(te.core.displayPowerGenerated)+"HE");
			}
			ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xFF55FF, 0x3F153F, texts);
			LeafiaGls.color(1,1,1);
			if (componentType() == TurbineComponentType.BLADES) { // cursor render
				event.setCanceled(true);
				LeafiaGls.enableBlend();
				LeafiaGls.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR,DestFactor.ONE_MINUS_SRC_COLOR,SourceFactor.ONE,DestFactor.ZERO);
				LeafiaGls.pushMatrix();
				ScaledResolution resolution = event.getResolution();
				LeafiaGls.translate(resolution.getScaledWidth_double()/2,resolution.getScaledHeight_double()/2,0);
				Minecraft.getMinecraft().renderEngine.bindTexture(cursor);
				EnumFacing facing = EnumFacing.byIndex(world.getBlockState(core).getValue(META)-10).getOpposite();
				float face = facing.getHorizontalAngle();
				float yaw = Minecraft.getMinecraft().player.rotationYaw;
				boolean inverse = Minecraft.getMinecraft().player.rotationPitch < 0;
				LeafiaGls.rotate((face-yaw)*(inverse ? -1 : 1)+(inverse ? 180 : 0),0,0,1);
				Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(-6,-6,0,0,12,12);
				LeafiaGls.popMatrix();
				LeafiaGls.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA,SourceFactor.ONE,DestFactor.ZERO);
				LeafiaGls.disableBlend();
				Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
			}
		}
	}
	static final ResourceLocation cursor = getIntegrated("machines/modular_turbines/cursor.png");
	@Override
	public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (this instanceof IMTPortBlock) {
			BlockPos core = findCore(world,pos);
			if (core != null && world.getTileEntity(core) instanceof MTComponentPortTE port) {
				ItemStack stack = player.getHeldItem(hand);
				if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
					FluidType type = identifier.getType(world,core.getX(),core.getY(),core.getZ(),stack);
					if (MTCoreTE.steamTypes.contains(type)) {
						if (port.identifier == null || !port.identifier.equals(type)) {
							if (!world.isRemote) {
								port.identifier = type;
								LeafiaPacket._start(port)
										.__write(MTPacketId.PORT_IDENTIFIER.id,port.identifier.getName())
										.__sendToAffectedClients();
							}
							if (!world.isRemote && port.core != null)
								port.core.disassemble();
							return true;
						}
					}
				}
			}
		}
		return super.onBlockActivated(world,pos,state,player,hand,facing,hitX,hitY,hitZ);
	}
	@Override
	public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, EnumFacing side, float fX, float fY, float fZ, EnumHand hand, ToolType tool) {
		return onScrew(world, player, new BlockPos(x, y, z), side, fX, fY, fZ, hand, tool);
	}
	@Override
	public boolean onScrew(World world,EntityPlayer player,BlockPos pos,EnumFacing side,float fX,float fY,float fZ,EnumHand hand,ToolType tool) {
		if (this instanceof IMTPortBlock) {
			BlockPos core = findCore(world,pos);
			if (core != null && world.getTileEntity(core) instanceof MTComponentPortTE port) {
				boolean changed = false;
				if (player.isSneaking()) {
					if (port.identifier != null) {
						changed = true;
						if (!world.isRemote) {
							port.identifier = null;
							LeafiaPacket._start(port)
									.__write(MTPacketId.PORT_IDENTIFIER.id,null)
									.__sendToAffectedClients();
						}
					}
				} else { // why would you want to disable decompression
					/*if (!world.isRemote) {
						port.decompress = !port.decompress;
						LeafiaPacket._start(port)
								.__write(MTPacketId.PORT_DECOMPRESSION.id,port.decompress)
								.__sendToAffectedClients();
					}
					changed = true;*/
				}
				if (changed) {
					if (!world.isRemote && port.core != null)
						port.core.disassemble();
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void neighborChanged(@NotNull IBlockState state,World world,@NotNull BlockPos pos,@NotNull Block blockIn,@NotNull BlockPos fromPos) {
		super.neighborChanged(state,world,pos,blockIn,fromPos);
		BlockPos core = findCore(world,pos);
		if (core != null && world.getTileEntity(core) instanceof ModularTurbineComponentTE te) {
			if (world.isRemote)
				te.local$firstRender = true;
			else {
				if (world.getBlockState(fromPos).getBlock() instanceof ModularTurbineBlockBase) {
					if (te.core != null)
						te.core.disassemble();
				}
			}
		}
	}
}
