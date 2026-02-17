package com.leafia.contents.machines.reactors.pwr.blocks.components.computer;

import com.custom_hbm.util.LCETuple.Triplet;
import com.hbm.inventory.control_panel.*;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRAssignableEntity;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementTE;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.*;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})
public class PWRComputerTE extends PWRAssignableEntity implements SimpleComponent, IControllable {
	@Override
	public String getPacketIdentifier() {
		return "PWR_COMPUTER";
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) {

	}
	@Override
	public String getComponentName() {
		return "pwr";
	}

	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getControl(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			String name = args.checkString(0);
			if (core.controlDemand.containsKey(name))
				return new Object[]{core.controlDemand.get(name)*100};
		}
		return new Object[]{null};
	}

	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] setControl(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			String name = args.checkString(0);
			double value = MathHelper.clamp(args.checkDouble(1)/100,0,1);
			if (!core.controlDemand.containsKey(name)) {
				boolean exists = false;
				for (BlockPos pos : core.controls) {
					TileEntity entity = getWorld().getTileEntity(pos);
					if (entity instanceof PWRControlTE control) {
						if (control.name.equals(name)) {
							exists = true;
							break;
						}
					}
				}
				if (!exists)
					return new Object[]{};
			}
			core.controlDemand.put(name,value);
			core.manipulateRod(name);
			core.sendControlPositions();
		}
		return new Object[]{};
	}

	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getMasterControl(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new Object[]{core.masterControl*100};
		}
		return new Object[]{null};
	}

	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] setMasterControl(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			core.masterControl = MathHelper.clamp(args.checkDouble(0)/100,0,1);
			core.manipulateRod(null);
			core.sendControlPositions();
		}
		return new Object[]{};
	}

	@Callback(doc = "Returns lowest fuel temperature, highest fuel temperature, and average fuel temperature in order.")
	@Optional.Method(modid = "opencomputers")
	public Object[] getFuelTemperatures(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			Triplet<Double,Double,Double> temps = getTemperatures(core);
			return new Object[]{temps.getA(),temps.getB(),temps.getC()};
		}
		return new Object[]{20,20,0};
	}

	@Callback(doc = "Returns coolant tank fill, hot coolant tank fill, and emergency buffer fill in order.")
	@Optional.Method(modid = "opencomputers")
	public Object[] getTankFills(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new Object[]{core.tanks[0].getFill(),core.tanks[1].getFill(),core.tanks[2].getFill()};
		}
		return new Object[]{0,0,0};
	}

	@Callback(doc = "Returns coolant tank capacity, hot coolant tank capacity, and emergency buffer capacity in order.")
	@Optional.Method(modid = "opencomputers")
	public Object[] getTankCapacities(Context context,Arguments args) {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new Object[]{core.tanks[0].getFill(),core.tanks[1].getFill(),core.tanks[2].getFill()};
		}
		return new Object[]{0,0,0};
	}

	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		PWRData core = getLinkedCore();
		if (e.name.equals("pwr_computer_set_master") && core != null) {
			core.masterControl = MathHelper.clamp(e.vars.get("level").getNumber()/100,0,1);
			core.manipulateRod(null);
			core.sendControlPositions();
		} else if (e.name.equals("pwr_computer_set_level") && core != null) {
			String name = e.vars.get("name").toString();
			double value = MathHelper.clamp(e.vars.get("level").getNumber()/100,0,1);
			if (!core.controlDemand.containsKey(name)) {
				boolean exists = false;
				for (BlockPos pos : core.controls) {
					TileEntity entity = getWorld().getTileEntity(pos);
					if (entity instanceof PWRControlTE control) {
						if (control.name.equals(name)) {
							exists = true;
							break;
						}
					}
				}
				if (!exists)
					return;
			}
			core.controlDemand.put(name,value);
			core.manipulateRod(name);
			core.sendControlPositions();
		}
	}

	@Override
	public List<String> getInEvents() {
		return Arrays.asList("pwr_computer_set_master","pwr_computer_set_level");
	}

	Triplet<Double,Double,Double> getTemperatures(PWRData core) {
		boolean firstMin = true;
		boolean firstMax = true;
		double min = 20;
		double max = 20;
		double avg = 0;
		double divide = 0;
		for (BlockPos fuel : core.fuels) {
			TileEntity te = world.getTileEntity(fuel);
			if (te instanceof PWRElementTE element) {
				ItemStack rod = element.inventory.getStackInSlot(0);
				if (rod.getItem() instanceof LeafiaRodItem) {
					NBTTagCompound compound = rod.getTagCompound();
					double heat = 20;
					if (compound != null)
						heat = compound.getDouble("heat");
					divide++;
					avg += heat;
					min = Math.min(heat,min);
					max = Math.max(heat,max);
					if (firstMin) {
						min = heat;
						firstMin = false;
					}
					if (firstMax) {
						max = heat;
						firstMax = false;
					}
				}
			}
		}
		if (divide > 0)
			avg /= divide;
		return new Triplet<>(min,max,avg);
	}

	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		PWRData core = getLinkedCore();
		map.put("master_level",new DataValueFloat(0));
		map.put("temp_lowest",new DataValueFloat(20));
		map.put("temp_highest",new DataValueFloat(20));
		map.put("temp_average",new DataValueFloat(0));
		map.put("tank_coolant",new DataValueFloat(0));
		map.put("tank_hotcoolant",new DataValueFloat(0));
		map.put("tank_e_buffer",new DataValueFloat(0));
		map.put("capacity_coolant",new DataValueFloat(0));
		map.put("capacity_hotcoolant",new DataValueFloat(0));
		map.put("capacity_e_buffer",new DataValueFloat(0));
		if (core != null) {
			map.put("master_level",new DataValueFloat((float)(core.masterControl*100)));
			Triplet<Double,Double,Double> temps = getTemperatures(core);
			map.put("temp_lowest",new DataValueFloat(temps.getA().floatValue()));
			map.put("temp_highest",new DataValueFloat(temps.getB().floatValue()));
			map.put("temp_average",new DataValueFloat(temps.getC().floatValue()));
			map.put("tank_coolant",new DataValueFloat(core.tanks[0].getFill()));
			map.put("tank_hotcoolant",new DataValueFloat(core.tanks[1].getFill()));
			map.put("tank_e_buffer",new DataValueFloat(core.tanks[2].getFill()));
			map.put("capacity_coolant",new DataValueFloat(core.tanks[0].getMaxFill()));
			map.put("capacity_hotcoolant",new DataValueFloat(core.tanks[1].getMaxFill()));
			map.put("capacity_e_buffer",new DataValueFloat(core.tanks[2].getMaxFill()));
		}
		return map;
	}

	@Override
	public BlockPos getControlPos() {
		return pos;
	}

	@Override
	public World getControlWorld() {
		return world;
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
}