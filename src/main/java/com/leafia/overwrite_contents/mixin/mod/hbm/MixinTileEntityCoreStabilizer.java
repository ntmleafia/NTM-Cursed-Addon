package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.DataValue;
import com.hbm.inventory.control_panel.DataValueFloat;
import com.hbm.items.machine.ItemLens;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreStabilizer;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.contents.machines.powercores.dfc.LCEItemLens;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreStabilizer;
import com.leafia.settings.AddonConfig;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})
@Mixin(TileEntityCoreStabilizer.class)
public abstract class MixinTileEntityCoreStabilizer extends TileEntityMachineBase implements ITickable, IEnergyReceiverMK2, SimpleComponent, IMixinTileEntityCoreStabilizer {
	public MixinTileEntityCoreStabilizer(int scount) {
		super(scount);
	}

	@Shadow(remap = false)
	public long power;
	@Shadow(remap = false)
	public int watts;
	@Shadow(remap = false)
	public boolean isOn;

	@Unique
	public LensType lens = LensType.STANDARD;
	@Unique
	public boolean cl_hasLens = false;
	@Unique
	public TileEntityCore lastGetCore;
	@Unique
	public BlockPos targetPosition = new BlockPos(0,0,0);

	@SideOnly(Side.CLIENT)
	@Override
	public void onReceivePacketLocal(byte key, Object value) {
		if (key == 0) {
			cl_hasLens = (int) value >= 0;
			if (cl_hasLens)
				this.lens = LensType.values()[(int)value];
		}
		if (key == 1)
			this.isOn = (boolean) value;
		//if (key == 2)
		//this.innerColor = (int)value;
		IMixinTileEntityCoreStabilizer.super.onReceivePacketLocal(key, value);
	}

	@Shadow(remap = false)
	protected abstract void updateConnections();

	/**
	 * @author mlbv
	 * @reason a
	 */
	@Override
	@Overwrite
	public void update() {
		if (!world.isRemote) {
			LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();

			this.updateConnections();

			watts = MathHelper.clamp(watts, 1, 100);
			long demand = (long) Math.pow(watts, 6);
			isOn = false;

			//beam = 0;

			LCEItemLens lens = null;
			if (inventory.getStackInSlot(0).getItem() instanceof LCEItemLens leafiaItemLens) {
				lens = leafiaItemLens;
				for (LensType type : LensType.values()) {
					if (type.item == lens) {
						this.lens = type;
						break;
					}
				}
//				if (lens == ModItems.ams_focus_blank) wtf is this stupid shit
//					this.lens = LensType.BLANK;
//				else if (lens == ModItems.ams_lens)
//					this.lens = LensType.STANDARD;
//				else if (lens == ModItems.ams_focus_limiter)
//					this.lens = LensType.LIMITER;
//				else if (lens == ModItems.ams_focus_booster)
//					this.lens = LensType.BOOSTER;
//				else if (lens == ModItems.ams_focus_omega)
//					this.lens = LensType.OMEGA;
			}

			if (lens != null && power >= demand * lens.drainMod) {
				isOn = true;
				TileEntityCore core = getCore();
				if (core != null) {
					IMixinTileEntityCore mixinTileEntityCore = (IMixinTileEntityCore) core;
					//core.field += (int)(watts * lens.fieldMod);
					mixinTileEntityCore.setDFCStabilization(mixinTileEntityCore.getDFCStabilization() + lens.fieldMod * (watts / 100d));
					mixinTileEntityCore.setDFCStabilizers(mixinTileEntityCore.getDFCStabilizers() + 1);
					mixinTileEntityCore.setDFCEnergyMod(mixinTileEntityCore.getDFCEnergyMod() * lens.energyMod);
					this.power -= (long) (demand * lens.drainMod);

					long dmg = ItemLens.getLensDamage(inventory.getStackInSlot(0));
					dmg += watts;

					if (dmg >= lens.maxDamage)
						inventory.setStackInSlot(0, ItemStack.EMPTY);
					else
						ItemLens.setLensDamage(inventory.getStackInSlot(0), dmg);
				}
			}
			//PacketDispatcher.wrapper.sendToAllTracking(new AuxGaugePacket(pos, beam, 0), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 250));
			LeafiaPacket._start(this)
					.__write((byte) 0, lens != null ? this.lens.ordinal() : -1)
					.__write(1, isOn)
					//.__write((byte)1,this.lens.outerColor)
					//.__write((byte)2,this.lens.innerColor)
					.__sendToClients(250);
		} else if (isOn)
			lastGetCore = getCore();
	}

	@Unique
	private TileEntityCore getCore() {
		return IMixinTileEntityCoreStabilizer.super.getCore(AddonConfig.dfcComponentRange);
	}

	@Override
	public BlockPos getControlPos() {
		return getPos();
	}

	@Override
	public World getControlWorld() {
		return getWorld();
	}

	@Override
	public void receiveEvent(BlockPos from, ControlEvent e) {
		if (e.name.equals("set_stabilizer_level")) {
			watts = Math.round(e.vars.get("level").getNumber());
		}
	}
	@Override
	public Map<String, DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("active",new DataValueFloat(isOn ? 1 : 0));
		map.put("level",new DataValueFloat(watts));
		map.put("lens_health",new DataValueFloat(0));
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.getItem() instanceof ItemLens) {
			ItemLens lens = (ItemLens) inventory.getStackInSlot(0).getItem();
			map.put("lens_health",new DataValueFloat(100-ItemLens.getLensDamage(stack)*100/(float)lens.maxDamage));
		}
		map.put("core_temp",new DataValueFloat(0));
		map.put("core_energy",new DataValueFloat(0));
		map.put("core_expel",new DataValueFloat(0));
		map.put("core_potent",new DataValueFloat(0));
		map.put("core_collapse",new DataValueFloat(0));
		TileEntityCore core = getCore();
		if (isOn && core != null) {
			IMixinTileEntityCore mixinTileEntityCore = (IMixinTileEntityCore) core;
			map.put("core_temp",new DataValueFloat((float)mixinTileEntityCore.getDFCTemperature()));
			map.put("core_energy",new DataValueFloat((float)mixinTileEntityCore.getDFCContainedEnergy()*1000_000));
			map.put("core_expel",new DataValueFloat((float)mixinTileEntityCore.getDFCExpellingEnergy()*1000_000));
			map.put("core_potent",new DataValueFloat((float)mixinTileEntityCore.getDFCPotentialGain()*100));
			map.put("core_collapse",new DataValueFloat((float)Math.pow(mixinTileEntityCore.getDFCCollapsing(),4)*100));
		}
		return map;
	}

	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("set_stabilizer_level");
	}

	@Override
	public void validate(){
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}

	@Override
	public void invalidate(){
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}

	/**
	 * @author ntmleafia
	 * @reason need more juice
	 */
	@Override
	@Overwrite(remap = false)
	public long getMaxPower() {
		return maxPower;
	}

	@Inject(method = {"readFromNBT","func_145839_a"},at = @At("HEAD"))
	public void onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		readTargetPos(compound);
	}

	@Inject(method = {"writeToNBT","func_189515_b"},at = @At("HEAD"))
	public void onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		writeTargetPos(compound);
	}

	@Override
	public TileEntityCore lastGetCore() {
		return lastGetCore;
	}

	@Override
	public void lastGetCore(TileEntityCore core) {
		this.lastGetCore = core;
	}

	@Override
	public BlockPos getTargetPosition() {
		return targetPosition;
	}

	@Override
	public void targetPosition(BlockPos pos) {
		this.targetPosition = pos;
	}

	@Override
	public String getPacketIdentifier() {
		return "dfc_stabilizer";
	}

	@Override
	public boolean hasLens() {
		return cl_hasLens;
	}

	@Override
	public LensType getLens() {
		return lens;
	}


	@Inject(method = "getComponentName",at = @At(value = "HEAD"),cancellable = true,remap = false)
	public void onGetComponentName(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue("dfc_communicator");
		cir.cancel();
	}

	@Optional.Method(modid = "opencomputers")
	@Callback
	public Object[] analyze(Context context, Arguments args) {
		TileEntityCore core = getCore();
		if (isOn && core != null) {
			LinkedHashMap<String, Object> mop = new LinkedHashMap<>();
			IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
			mop.put("temperature", mixin.getDFCTemperature());
			mop.put("stabilization", mixin.getDFCStabilization());
			mop.put("containedEnergy", mixin.getDFCContainedEnergy()*1000_000);
			mop.put("expellingEnergy", mixin.getDFCExpellingEnergy()*1000_000);
			mop.put("potentialRelease", mixin.getDFCPotentialGain()*100);
			mop.put("collapse", Math.pow(mixin.getDFCCollapsing(),4)*100);
			mop.put("fuelA", core.tanks[0].getFill());
			mop.put("fuelB", core.tanks[1].getFill());
			return new Object[]{mop};
		}
		return new Object[]{"COULDN'T CONNECT TO THE CORE"};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "durability()->(lensDurability: int, maximum: int) - Returns currently installed lens' durability, or 0 if missing.")
	public Object[] durability(Context context,Arguments args) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.getItem() instanceof ItemLens) {
			ItemLens lens = (ItemLens) inventory.getStackInSlot(0).getItem();
			return new Object[]{ItemLens.getLensDamage(stack), lens.maxDamage};
		}
		return new Object[]{0, 0};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "setLevel(newLevel: number)->(previousLevel: number)")
	public Object[] setLevel(Context context, Arguments args) {
		Object[] prev = new Object[]{watts};
		watts = MathHelper.clamp(args.checkInteger(0), 1, 100);
		return prev;
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "getLevel()->(level: number)")
	public Object[] getLevel(Context context, Arguments args) {
		return new Object[]{watts};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "validate()->(success: boolean) - Whether the stabilizer is working or not")
	public Object[] validate(Context context, Arguments args) {
		return new Object[]{isOn};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "getPower(); returns the current power level - long")
	public Object[] getPower(Context context, Arguments args) {
		return new Object[]{power};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "getMaxPower(); returns the maximum power level - long")
	public Object[] getMaxPower(Context context, Arguments args) {
		return new Object[]{getMaxPower()};
	}

	@Optional.Method(modid = "opencomputers")
	@Callback(doc = "getChargePercent(); returns the charge in percent - double")
	public Object[] getChargePercent(Context context, Arguments args) {
		return new Object[]{100D * getPower() / (double) getMaxPower()};
	}
}
