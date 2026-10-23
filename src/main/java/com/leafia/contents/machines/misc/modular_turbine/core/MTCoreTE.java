// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.core;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.leafia.AddonBase;
import com.leafia.contents.machines.misc.modular_turbine.*;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase.TurbineComponentType;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.AssemblyReturnCode.AssemblyErrorReason;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.AssemblyReturnCode.ReturnCodeError;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.AssemblyReturnCode.ReturnCodeSuccess;
import com.leafia.contents.machines.misc.modular_turbine.ports.MTComponentPortTE;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.passive.LeafiaPassiveServer;
import com.leafia.settings.AddonConfig;
import com.llib.math.SIPfx;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreData.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MTCoreTE extends TileEntity implements LeafiaPacketReceiver, ITickable, IControllable {
	public static double PARTIAL_EXPANSION_FRACTION = 0.2D;
	/** nozzle-to-inlet swirl gain */
	public static double INLET_WHIRL_COEFFICIENT = 32D;
	/** exit-swirl recovery */
	public static double EXIT_WHIRL_RECOVERY_FACTOR = 0.82D;
	public static double ANGULAR_MOMENTUM_TORQUE_COEFFICIENT = 0.02D;
	/** Effective linear electrical load slope */
	public static double GENERATOR_LOAD_COEFFICIENT = 0.000001D;
	/** Saturation cap applied to the effective generator counter-torque. */
	public static double GENERATOR_TORQUE_LIMIT = Double.MAX_VALUE;
	public static double COULOMB_FRICTION_TORQUE = 0.7D;
	public static double FRICTION_RPS_EPSILON = 0.25D;
	public static double VISCOUS_FRICTION_COEFFICIENT = 0.05D;
	public static double WINDAGE_COEFFICIENT = 0.01D;
	public static double INCIDENCE_DESIGN_SPEED_RATIO = 1D;
	public static double INCIDENCE_LOSS_FACTOR = 0.25D; // UNUSED
	public static double INCIDENCE_EFFICIENCY_FLOOR = 0.05D; // UNUSED
	public static double POWER_SCALE = 0.04; //0.00232478632*2;
	public static double ADMISSION_NOMINAL_TAU_TICKS = 4D;
	public static double ADMISSION_STABLE_RELATIVE_ERROR = 0.03D;
	// DEATHSTEAM -> ULTRAHOTSTEAM -> SUPERHOTSTEAM -> HOTSTEAM -> STEAM -> SPENTSTEAM stage-work scalars
	public static double DEATHSTEAM_STAGE_WORK_MULTIPLIER = 5000D;
	public static double ULTRAHOTSTEAM_STAGE_WORK_MULTIPLIER = 1000D;
	public static double SUPERHOTSTEAM_STAGE_WORK_MULTIPLIER = 500D;
	public static double HOTSTEAM_STAGE_WORK_MULTIPLIER = 250D;
	public static double STEAM_STAGE_WORK_MULTIPLIER = 125D;
	public static double BUFFER_CAPACITY_MULTIPLIER = 12D;
	public static double ADMISSION_MAX_BUFFER_TICKS = 2D;
	public static double ADMISSION_FLOW_EPSILON = 1.0E-9D;
	public static double ADMISSION_BUFFER_EPSILON = 1.0E-6D;
	public static double EQUILIBRIUM_RPS_EPSILON = 1.0E-6D;
	public static double EQUILIBRIUM_RPS_LIMIT = 1.0E6D;
	public static int EQUILIBRIUM_SOLVER_ITERATIONS = 48;
	public static double DEFAULT_GLOBAL_GEAR_SCALE = 0.05D;
	public static double GOVERNED_RPS = AddonConfig.governedRPS;
	public static double THROTTLE_GOVERNOR_PROPORTIONAL_GAIN = 0.04D;
	public static double THROTTLE_GOVERNOR_INTEGRAL_GAIN = 0.0025D;
	public static double THROTTLE_GOVERNOR_ADMISSION_RATE = 0.02D;
	public static double ROTOR_INERTIA_SCALE = 0.8D;
	public static double TWO_PI = Math.PI*2D;
	public static double TICK_SECONDS = 1D/20D;

	public int overdrive = 0;
	public static int getRPSBoost(int level) {
		return switch(level) {
			case 1 -> 10;
			case 2 -> 25;
			case 3 -> 40;
			default -> 0;
		};
	}
	public double getGovernedRPS() {
		return GOVERNED_RPS+getRPSBoost(overdrive);
	}

	public double rps;
	public double lastTargetRPS = 0;
	public double lastDriveTorque = 0;
	public double lastGeneratorTorque = 0;
	public double lastFrictionTorque = 0;
	public double lastWindageTorque = 0;
	public double globalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
	public double lastGlobalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
	public double admission = 0;
	public double lastAdmission = 0;
	public final List<ModularTurbineComponentTE> components = new ArrayList<>();
	public final Map<Integer,ModularTurbineComponentTE> positionToComponentMap = new HashMap<>();
	public final List<TurbineAssembly> assemblies = new ArrayList<>();
	public int length = 0;
	// Rotor inertia scalar
	public double weight = 0;
	private double governorIntegral = 0;

	public double local$shaftAngle = 0;
	public double local$shaftAnglePrev = 0;
	public int stressSoundTimer = 0;

	private CompiledMachineStats compiledMachineStats;
	public static final List<FluidType> steamTypes = new ArrayList<>();
	public static FluidType getNextSteam(FluidType type,boolean decompress) {
		int index = steamTypes.indexOf(type);
		if (index == -1) return null;
		if (decompress) {
			if (index+1 < steamTypes.size())
				return steamTypes.get(index+1);
			else
				return null;
		} else
			return type;
	}
	public static FT_Coolable getSteamExpansion(FluidType type) {
		if (type.hasTrait(FT_Coolable.class))
			return type.getTrait(FT_Coolable.class);
		return null;
	}
	public static double getSteamMassEquivalent(FluidType type) {
		if (type.equals(Fluids.SPENTSTEAM))
			return 1;
		FT_Coolable expansion = getSteamExpansion(type);
		if (expansion == null)
			return 0;
		return getSteamMassEquivalent(expansion.coolsTo)*expansion.amountProduced/(double)expansion.amountReq;
	}
	public static double getTurbineStepWork(FluidType type) {
		FT_Coolable expansion = getSteamExpansion(type);
		if (expansion == null)
			return 0;
		double eff = expansion.getEfficiency(FT_Coolable.CoolingType.TURBINE);
		if (eff == 0)
			eff = 1;
		return expansion.heatEnergy*eff/(double)expansion.amountReq;
	}
	public static double getSteamRemainingWork(FluidType type) {
		FT_Coolable expansion = getSteamExpansion(type);
		if (expansion == null)
			return 0;
		return getTurbineStepWork(type)+getSteamRemainingWork(expansion.coolsTo)*expansion.amountProduced/(double)expansion.amountReq;
	}
	public static double getSteamSpecificRemainingWork(FluidType type) {
		return getSteamRemainingWork(type)/getSteamMassEquivalent(type);
	}
	public static double getStageSpecificWork(FluidType type,boolean decompress) {
		return getStageSpecificWork(type,decompress,PARTIAL_EXPANSION_FRACTION);
	}
	private static double getStageSpecificWork(FluidType type,boolean decompress,double partialExpansionFraction) {
		double specificWork = getSteamSpecificRemainingWork(type);
		if (!decompress)
			return specificWork*partialExpansionFraction;
		FluidType nextSteam = getNextSteam(type,true);
		if (nextSteam == null)
			return specificWork;
		return Math.max(specificWork-getSteamSpecificRemainingWork(nextSteam),0);
	}
	private static double getStageSpecificWork(TurbineAssembly assembly,CompiledStageStats compiledStats) {
		return getStageSpecificWork(assembly.typeIn,assembly.decompress,compiledStats.partialExpansionFraction)*getStageWorkMultiplier(assembly.typeIn);
	}
	private static double getStageWorkMultiplier(FluidType type) {
		/*return switch (steamTypes.indexOf(type)) {
			case 0 -> DEATHSTEAM_STAGE_WORK_MULTIPLIER;
			case 1 -> ULTRAHOTSTEAM_STAGE_WORK_MULTIPLIER;
			case 2 -> SUPERHOTSTEAM_STAGE_WORK_MULTIPLIER;
			case 3 -> HOTSTEAM_STAGE_WORK_MULTIPLIER;
			case 4 -> STEAM_STAGE_WORK_MULTIPLIER;
			default -> 1D;
		};*/
		/*if (type.equals(Fluids.STEAM))
			return 125D;
		else if (type.equals(Fluids.HOTSTEAM))
			return 1000D;
		else
			return 500D;*/
		return 125;
	}
	private static int getStageInputAmount(FluidType type,boolean decompress) {
		if (!decompress)
			return 1;
		FT_Coolable expansion = getSteamExpansion(type);
		if (expansion == null)
			return 0;
		return expansion.amountReq;
	}
	private static int getStageOutputAmount(FluidType type,boolean decompress) {
		if (!decompress)
			return 1;
		FT_Coolable expansion = getSteamExpansion(type);
		if (expansion == null)
			return 0;
		return expansion.amountProduced;
	}
	private static double getStageOutputRatio(FluidType type,boolean decompress) {
		int inputAmount = getStageInputAmount(type,decompress);
		if (inputAmount <= 0)
			return 0;
		return getStageOutputAmount(type,decompress)/(double)inputAmount;
	}
	public static double getStageRadius(int size) {
		return Math.max(size,2)*0.35D;
	}
	private static double getStageInletWhirl(FluidType type,boolean decompress) {
		return getStageInletWhirl(getStageSpecificWork(type,decompress),INLET_WHIRL_COEFFICIENT);
	}
	private static double getStageInletWhirl(CompiledStageStats compiledStats) {
		return getStageInletWhirl(compiledStats.stageSpecificWork,compiledStats.inletWhirlCoefficient);
	}
	private static double getStageInletWhirl(double stageSpecificWork,double inletWhirlCoefficient) {
		if (stageSpecificWork <= 0)
			return 0;
		return Math.sqrt(2*Math.max(stageSpecificWork,0))*inletWhirlCoefficient;
	}
	private static void assignBaseGearRatios(List<TurbineAssembly> assemblies) {
		double weightedLogSum = 0;
		double totalWeight = 0;
		for (TurbineAssembly assembly : assemblies) {
			CompiledStageStats compiledStats = assembly.compiledStats;
			double inletWhirl = compiledStats.inletWhirl;
			double stageRadius = compiledStats.stageRadius;
			if (inletWhirl <= 0 || stageRadius <= 0) {
				assembly.baseGearRatio = 1;
				compiledStats.baseGearRatio = 1;
				continue;
			}
			double speedIndex = inletWhirl/stageRadius;
			double authorityWeight = compiledStats.authorityWeight;
			weightedLogSum += authorityWeight*Math.log(speedIndex);
			totalWeight += authorityWeight;
		}
		if (totalWeight <= 0) {
			for (TurbineAssembly assembly : assemblies) {
				assembly.baseGearRatio = 1;
				assembly.compiledStats.baseGearRatio = 1;
			}
			return;
		}
		double referenceSpeedIndex = Math.exp(weightedLogSum/totalWeight);
		for (TurbineAssembly assembly : assemblies) {
			CompiledStageStats compiledStats = assembly.compiledStats;
			double inletWhirl = compiledStats.inletWhirl;
			double stageRadius = compiledStats.stageRadius;
			if (inletWhirl <= 0 || stageRadius <= 0) {
				assembly.baseGearRatio = 1;
				compiledStats.baseGearRatio = 1;
				continue;
			}
			double speedIndex = inletWhirl/stageRadius;
			assembly.baseGearRatio = speedIndex/referenceSpeedIndex;
			compiledStats.baseGearRatio = assembly.baseGearRatio;
		}
	}
	private static double getEffectiveMassFlow(TurbineAssembly assembly,CompiledStageStats compiledStats,double rawMassFlow) {
		double flowCapacity = compiledStats.flowCapacity;
		double cappedRawMassFlow = Math.min(rawMassFlow,flowCapacity);
		if (assembly.nominalMassFlow <= ADMISSION_FLOW_EPSILON && assembly.admissionBufferMass <= ADMISSION_BUFFER_EPSILON) {
			assembly.nominalMassFlow = cappedRawMassFlow;
			assembly.admissionBufferMass = Math.max(rawMassFlow-cappedRawMassFlow,0);
			if (assembly.admissionBufferMass <= ADMISSION_BUFFER_EPSILON)
				assembly.admissionBufferMass = 0;
			return cappedRawMassFlow;
		}

		assembly.nominalMassFlow += (rawMassFlow-assembly.nominalMassFlow)*(1D-Math.exp(-1D/ADMISSION_NOMINAL_TAU_TICKS));
		assembly.nominalMassFlow = Math.min(assembly.nominalMassFlow,flowCapacity);
		double stableBand = Math.max(assembly.nominalMassFlow*ADMISSION_STABLE_RELATIVE_ERROR,ADMISSION_FLOW_EPSILON);
		double maxBufferMass = Math.max(assembly.nominalMassFlow,0)*ADMISSION_MAX_BUFFER_TICKS;
		if (Math.abs(rawMassFlow-assembly.nominalMassFlow) <= stableBand && assembly.admissionBufferMass <= ADMISSION_BUFFER_EPSILON) {
			assembly.admissionBufferMass = 0;
			return cappedRawMassFlow;
		}

		double availableMass = rawMassFlow+assembly.admissionBufferMass;
		double effectiveMassFlow = Math.min(assembly.nominalMassFlow,availableMass);
		assembly.admissionBufferMass = Math.max(availableMass-effectiveMassFlow,0);
		if (assembly.admissionBufferMass > maxBufferMass) {
			double overflowMass = assembly.admissionBufferMass-maxBufferMass;
			effectiveMassFlow += overflowMass;
			assembly.admissionBufferMass = maxBufferMass;
		}
		if (assembly.admissionBufferMass <= ADMISSION_BUFFER_EPSILON && Math.abs(effectiveMassFlow-rawMassFlow) <= stableBand) {
			assembly.admissionBufferMass = 0;
			return cappedRawMassFlow;
		}
		return effectiveMassFlow;
	}
	private static double getGeneratorTorqueAtOmega(CompiledMachineStats machineStats,double omega) {
		double generatorTorque = machineStats.generatorLoadCoefficient*omega;
		return Math.min(Math.max(generatorTorque,-machineStats.generatorTorqueLimit),machineStats.generatorTorqueLimit);
	}
	private static double getFrictionTorqueAtRPS(CompiledMachineStats machineStats,double rps) {
		return machineStats.coulombFrictionTorque*Math.tanh(rps/machineStats.frictionRpsEpsilon)+machineStats.viscousFrictionCoefficient*rps;
	}
	private static double getWindageTorqueAtRPS(CompiledMachineStats machineStats,double rps) {
		return machineStats.windageCoefficient*rps*Math.abs(rps);
	}
	static double getIncidenceEfficiency(double speedRatio) {
		double deviation = (speedRatio-INCIDENCE_DESIGN_SPEED_RATIO)/INCIDENCE_DESIGN_SPEED_RATIO;
		return 1; //Math.max(1-INCIDENCE_LOSS_FACTOR*deviation*deviation,INCIDENCE_EFFICIENCY_FLOOR);
	}
	private static double getNetTorqueAtRPS(CompiledMachineStats machineStats,double rps,double driveTorqueIntercept,double driveTorqueOmegaSlope) {
		double omega = rps*TWO_PI;
		double driveTorque = driveTorqueIntercept-driveTorqueOmegaSlope*omega;
		return driveTorque-getGeneratorTorqueAtOmega(machineStats,omega)-getFrictionTorqueAtRPS(machineStats,rps)-getWindageTorqueAtRPS(machineStats,rps);
	}
	private static double solveEquilibriumRPS(CompiledMachineStats machineStats,double driveTorqueIntercept,double driveTorqueOmegaSlope) {
		double zeroNetTorque = getNetTorqueAtRPS(machineStats,0,driveTorqueIntercept,driveTorqueOmegaSlope);
		if (zeroNetTorque <= 0)
			return 0;

		double linearNoLoadRPS = 0;
		if (driveTorqueOmegaSlope > EQUILIBRIUM_RPS_EPSILON)
			linearNoLoadRPS = driveTorqueIntercept/(driveTorqueOmegaSlope*TWO_PI);
		double low = 0;
		double high = Math.max(linearNoLoadRPS,1);
		double highNetTorque = getNetTorqueAtRPS(machineStats,high,driveTorqueIntercept,driveTorqueOmegaSlope);
		while (highNetTorque > 0 && high < EQUILIBRIUM_RPS_LIMIT) {
			low = high;
			high *= 2;
			highNetTorque = getNetTorqueAtRPS(machineStats,high,driveTorqueIntercept,driveTorqueOmegaSlope);
		}
		if (highNetTorque > 0)
			return high;
		for (int i = 0; i < EQUILIBRIUM_SOLVER_ITERATIONS; i++) {
			double mid = (low+high)/2;
			double midNetTorque = getNetTorqueAtRPS(machineStats,mid,driveTorqueIntercept,driveTorqueOmegaSlope);
			if (midNetTorque > 0)
				low = mid;
			else
				high = mid;
		}
		return (low+high)/2;
	}
	/*
	private static double getNetTorqueAtRPS(CompiledMachineStats machineStats,double rps,List<StageRuntimeData> stageData,double globalGearScale) {
		double omega = rps*TWO_PI;
		double driveTorque = 0;
		for (StageRuntimeData data : stageData)
			driveTorque += data.getDriveTorque(omega,globalGearScale);
		return driveTorque-getGeneratorTorqueAtOmega(machineStats,omega)-getFrictionTorqueAtRPS(machineStats,rps)-getWindageTorqueAtRPS(machineStats,rps);
	}
	private static double solveEquilibriumRPS(CompiledMachineStats machineStats,List<StageRuntimeData> stageData,double globalGearScale) {
		double zeroNetTorque = getNetTorqueAtRPS(machineStats,0,stageData,globalGearScale);
		if (zeroNetTorque <= 0)
			return 0;
		double low = 0;
		double high = 1;
		double highNetTorque = getNetTorqueAtRPS(machineStats,high,stageData,globalGearScale);
		while (highNetTorque > 0 && high < EQUILIBRIUM_RPS_LIMIT) {
			low = high;
			high *= 2;
			highNetTorque = getNetTorqueAtRPS(machineStats,high,stageData,globalGearScale);
		}
		if (highNetTorque > 0)
			return high;
		for (int i = 0; i < EQUILIBRIUM_SOLVER_ITERATIONS; i++) {
			double mid = (low+high)/2;
			double midNetTorque = getNetTorqueAtRPS(machineStats,mid,stageData,globalGearScale);
			if (midNetTorque > 0)
				low = mid;
			else
				high = mid;
		}
		return (low+high)/2;
	}*/
	private static int requireTankCapacity(long bufferSize,FluidType fluid) {
		if (bufferSize > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;//throw new IllegalStateException("Buffer size exceeds FluidTankNTM capacity for "+fluid.getName()+": "+bufferSize);
		return (int)bufferSize;
	}
	@Override
	public String getPacketIdentifier() {
		return "MT_CORE";
	}
	@Override
	public double affectionRange() {
		return 512;
	}
	public boolean assembleFromNBT(NBTTagCompound compound) {
		disassemble();
		stopAllSounds();
		local$audios.clear();
		boolean ret = assembleFromNBT_internal(compound);
		if (!ret)
			disassemble();
		else {
			if (world.isRemote)
				local$createAudios();
		}
		return ret;
	}
	protected boolean assembleFromNBT_internal(NBTTagCompound compound) {
		length = compound.getInteger("length");
		weight = compound.getDouble("weight");
		globalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
		admission = compound.hasKey("admission") ? compound.getDouble("admission") : 0;
		governorIntegral = compound.hasKey("governorIntegral") ? compound.getDouble("governorIntegral") : admission;
		EnumFacing dir = getAssemblyFacing();
		Map<Integer,TurbineAssembly> assemblyMap = new HashMap<>();
		boolean missingGearRatio = false;

		// LOAD ASSEMBLIES
		for (NBTBase nbtBase : compound.getTagList("assemblies",10)) {
			if (nbtBase instanceof NBTTagCompound tag) {
				TurbineAssembly assembly = new TurbineAssembly();
				assemblies.add(assembly);
				for (NBTBase nbtBase1 : tag.getTagList("positions",2)) {
					if (nbtBase1 instanceof NBTTagShort p) {
						assembly.positionsInOrder.add(p.getInt());
						assemblyMap.put(p.getInt(),assembly);
					} else
						return false;
				}
				for (NBTBase nbtBase1 : tag.getTagList("bladeMap",10)) {
					if (nbtBase1 instanceof NBTTagCompound bladeTag)
						assembly.bladeDirections.put((int)bladeTag.getShort("pos"),bladeTag.getBoolean("opposite"));
					else
						return false;
				}
				assembly.input = new FluidTankNTM(Fluids.NONE,0);
				assembly.output = new FluidTankNTM(Fluids.NONE,0);
				assembly.input.readFromNBT(tag,"input");
				assembly.output.readFromNBT(tag,"output");
				assembly.typeIn = Fluids.fromName(tag.getString("typeIn"));
				assembly.decompress = tag.getBoolean("decompress");
				assembly.size = tag.getByte("size");
				if (tag.hasKey("gearRatio"))
					assembly.baseGearRatio = tag.getDouble("gearRatio");
				else
					missingGearRatio = true;
			} else return false;
		}
		// HOOK UP TILE-ENTITIES
		for (int i = 0; i < length; i++) {
			BlockPos offs = new BlockPos(pos).offset(dir,i+1);
			if (world.getBlockState(offs).getBlock() instanceof ModularTurbineBlockBase turbine) {
				BlockPos core = turbine.findCore(world,offs);
				if (core != null) {
					if (world.getTileEntity(core) instanceof ModularTurbineComponentTE te) {
						positionToComponentMap.put(i,te);
						if (!components.contains(te)) {
							components.add(te);
							te.core = this;
							if (assemblyMap.containsKey(i))
								te.assembly = assemblyMap.get(i);
						}
					} else return false;
				} else return false;
			} else
				return false;
		}
		compileAssemblyModel(missingGearRatio);
		return true;
	}
	public NBTTagCompound writeAssemblyToNBT(NBTTagCompound compound) {
		compound.setInteger("length",length);
		compound.setDouble("weight",weight);
		compound.setDouble("admission",admission);
		compound.setDouble("governorIntegral",governorIntegral);
		NBTTagList assemList = new NBTTagList();
		for (TurbineAssembly assembly : assemblies) {
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList posList = new NBTTagList();
			for (Integer p : assembly.positionsInOrder)
				posList.appendTag(new NBTTagShort(p.shortValue()));
			tag.setTag("positions",posList);
			NBTTagList bladeMap = new NBTTagList();
			for (Entry<Integer,Boolean> entry : assembly.bladeDirections.entrySet()) {
				NBTTagCompound e = new NBTTagCompound();
				e.setShort("pos",entry.getKey().shortValue());
				e.setBoolean("opposite",entry.getValue());
				bladeMap.appendTag(e);
			}
			tag.setTag("bladeMap",bladeMap);
			assembly.input.writeToNBT(tag,"input");
			assembly.output.writeToNBT(tag,"output");
			tag.setString("typeIn",assembly.typeIn.getName());
			tag.setBoolean("decompress",assembly.decompress);
			tag.setByte("size",(byte)assembly.size);
			tag.setDouble("gearRatio",assembly.baseGearRatio);
			assemList.appendTag(tag);
		}
		compound.setTag("assemblies",assemList);
		return compound;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this)
				.__write(MTPacketId.CORE_ASSEMBLY_SYNC.id,writeAssemblyToNBT(new NBTTagCompound()))
				.__sendToClient(plr);
	}
	private EnumFacing getAssemblyFacing() {
		return EnumFacing.byIndex(getBlockMetadata()-10).getOpposite();
	}
	private CompiledMachineStats getCompiledMachineStats() {
		if (compiledMachineStats == null)
			compiledMachineStats = compileMachineStats(summarizeMachineUpgrades());
		return compiledMachineStats;
	}
	private void compileAssemblyModel(boolean recomputeGearRatios) {
		compiledMachineStats = compileMachineStats(summarizeMachineUpgrades());
		for (int assemblyIndex = 0; assemblyIndex < assemblies.size(); assemblyIndex++) {
			TurbineAssembly assembly = assemblies.get(assemblyIndex);
			assembly.compiledStats = compileStageStats(assembly,summarizeStageUpgrades(assemblyIndex,assembly));
		}
		if (recomputeGearRatios)
			assignBaseGearRatios(assemblies);
		else
			for (TurbineAssembly assembly : assemblies)
				assembly.compiledStats.baseGearRatio = assembly.baseGearRatio;
	}
	private MachineUpgradeSummary summarizeMachineUpgrades() {
		MachineUpgradeSummary summary = new MachineUpgradeSummary();
		for (ModularTurbineComponentTE component : components)
			collectMachineUpgradeOffsets(summary,component);
		return summary;
	}
	private StageUpgradeSummary summarizeStageUpgrades(int targetAssemblyIndex,TurbineAssembly targetAssembly) {
		StageUpgradeSummary summary = new StageUpgradeSummary();
		for (int componentPosition = 0; componentPosition < components.size(); componentPosition++) {
			ModularTurbineComponentTE component = components.get(componentPosition);
			StageUpgradeContext context = resolveStageUpgradeContext(targetAssemblyIndex,targetAssembly,componentPosition,component);
			if (context == null)
				continue;
			collectStageUpgradeOffsets(summary,context,component);
		}
		return summary;
	}
	private StageUpgradeContext resolveStageUpgradeContext(int targetAssemblyIndex,TurbineAssembly targetAssembly,int componentPosition,ModularTurbineComponentTE component) {
		if (component.assembly != null) {
			if (component.assembly != targetAssembly)
				return null;
			return new StageUpgradeContext(
					targetAssembly,
					targetAssemblyIndex > 0 ? assemblies.get(targetAssemblyIndex-1) : null,
					targetAssemblyIndex+1 < assemblies.size() ? assemblies.get(targetAssemblyIndex+1) : null,
					componentPosition,
					StageUpgradeRelation.MEMBER
			);
		}
		TurbineAssembly previousAssembly = findPreviousAssembly(componentPosition);
		if (previousAssembly == targetAssembly) {
			return new StageUpgradeContext(
					targetAssembly,
					previousAssembly,
					findNextAssembly(componentPosition),
					componentPosition,
					StageUpgradeRelation.AFTER_TARGET
			);
		}
		TurbineAssembly nextAssembly = findNextAssembly(componentPosition);
		if (nextAssembly == targetAssembly) {
			return new StageUpgradeContext(
					targetAssembly,
					findPreviousAssembly(componentPosition),
					nextAssembly,
					componentPosition,
					StageUpgradeRelation.BEFORE_TARGET
			);
		}
		return null;
	}
	private TurbineAssembly findPreviousAssembly(int componentPosition) {
		TurbineAssembly previousAssembly = null;
		for (TurbineAssembly assembly : assemblies) {
			if (assembly.getEndPosition() >= componentPosition)
				break;
			previousAssembly = assembly;
		}
		return previousAssembly;
	}
	private TurbineAssembly findNextAssembly(int componentPosition) {
		for (TurbineAssembly assembly : assemblies) {
			if (assembly.getStartPosition() > componentPosition)
				return assembly;
		}
		return null;
	}
	private void collectMachineUpgradeOffsets(MachineUpgradeSummary summary,ModularTurbineComponentTE component) {
		component.contributeMachineUpgradeOffsets(summary,this,component);
		((IMTMachineUpgradeContributor)world.getBlockState(component.getPos()).getBlock()).contributeMachineUpgradeOffsets(summary,this,component);
	}
	private void collectStageUpgradeOffsets(StageUpgradeSummary summary,StageUpgradeContext context,ModularTurbineComponentTE component) {
		component.contributeStageUpgradeOffsets(summary,this,context,component);
		((IMTStageUpgradeContributor) world.getBlockState(component.getPos()).getBlock()).contributeStageUpgradeOffsets(summary,this,context,component);
	}
	private CompiledMachineStats compileMachineStats(MachineUpgradeSummary upgrades) {
		CompiledMachineStats machineStats = new CompiledMachineStats();
		machineStats.generatorLoadCoefficient = GENERATOR_LOAD_COEFFICIENT+upgrades.getGeneratorLoadCoefficientOffset();
		machineStats.generatorTorqueLimit = GENERATOR_TORQUE_LIMIT*(1D+upgrades.getGeneratorTorqueLimitOffset());
		machineStats.coulombFrictionTorque = COULOMB_FRICTION_TORQUE*(1D+upgrades.getCoulombFrictionTorqueOffset());
		machineStats.frictionRpsEpsilon = FRICTION_RPS_EPSILON*(1D+upgrades.getFrictionRpsEpsilonOffset());
		machineStats.viscousFrictionCoefficient = VISCOUS_FRICTION_COEFFICIENT*(1D+upgrades.getViscousFrictionCoefficientOffset());
		machineStats.windageCoefficient = WINDAGE_COEFFICIENT*(1D+upgrades.getWindageCoefficientOffset());
		machineStats.powerScale = POWER_SCALE*(1D+upgrades.getPowerScaleOffset());
		machineStats.rotorInertiaScale = ROTOR_INERTIA_SCALE*(1D+upgrades.getRotorInertiaScaleOffset());
		return machineStats;
	}
	private CompiledStageStats compileStageStats(TurbineAssembly assembly,StageUpgradeSummary upgrades) {
		CompiledStageStats compiledStats = new CompiledStageStats();
		compiledStats.partialExpansionFraction = PARTIAL_EXPANSION_FRACTION+upgrades.getPartialExpansionFractionOffset();
		compiledStats.inletWhirlCoefficient = INLET_WHIRL_COEFFICIENT*(1D+upgrades.getInletWhirlCoefficientOffset());
		compiledStats.exitWhirlRecoveryFactor = EXIT_WHIRL_RECOVERY_FACTOR*(1D+upgrades.getExitWhirlRecoveryFactorOffset());
		compiledStats.angularMomentumTorqueCoefficient = ANGULAR_MOMENTUM_TORQUE_COEFFICIENT*(1D+upgrades.getAngularMomentumTorqueCoefficientOffset());
		compiledStats.inputAmount = getStageInputAmount(assembly.typeIn,assembly.decompress);
		compiledStats.outputAmount = getStageOutputAmount(assembly.typeIn,assembly.decompress);
		compiledStats.division = getStageOutputRatio(assembly.typeIn,assembly.decompress);
		compiledStats.steamMassEquivalent = getSteamMassEquivalent(assembly.typeIn);
		compiledStats.stageSpecificWork = getStageSpecificWork(assembly,compiledStats);
		compiledStats.stageRadius = getStageRadius(assembly.size);
		compiledStats.inletWhirl = getStageInletWhirl(compiledStats);
		compiledStats.torqueResponseFactor = compiledStats.angularMomentumTorqueCoefficient*(1D+compiledStats.exitWhirlRecoveryFactor);
		compiledStats.bladeCount = assembly.bladeDirections.size();
		compiledStats.bladeArea = Math.max(compiledStats.bladeCount+upgrades.getBladeArea(),ADMISSION_FLOW_EPSILON);
		compiledStats.flowCapacity = upgrades.getFlowCapacity() > ADMISSION_FLOW_EPSILON ? upgrades.getFlowCapacity() : Double.POSITIVE_INFINITY;
		compiledStats.authorityWeight = Math.max(compiledStats.bladeArea*compiledStats.steamMassEquivalent,ADMISSION_FLOW_EPSILON);
		compiledStats.baseGearRatio = assembly.baseGearRatio;
		return compiledStats;
	}
	private List<StageRuntimeData> evaluateStages(double admission) {
		List<StageRuntimeData> stageData = new ArrayList<>();
		for (TurbineAssembly assembly : assemblies)
			stageData.add(evaluateStage(assembly,admission));
		return stageData;
	}
	private StageRuntimeData evaluateStage(TurbineAssembly assembly,double admission) {
		CompiledStageStats compiledStats = assembly.compiledStats;
		int inputConsumed = transferStageFluids(assembly,compiledStats,admission);
		int wrongBlades = countWrongAndStackedBlades(assembly);
		assembly.lastWrongBlades = wrongBlades;
		// applyWrongBladeTurbulence
		double turbAddWrongBlades = wrongBlades*2d/assembly.compiledStats.bladeArea*Math.min(inputConsumed,1);
		if (turbAddWrongBlades > 0.3) turbulenceReasonInverseBlades = true;
		turbulence = Math.min(turbulence+turbAddWrongBlades,100);

		// apply blade count turbulence
		double turbAddBladeCount = Math.pow(assembly.maxStackedBlades/(double)AddonConfig.maxOptimalTurbineLength,5)*0.015*Math.min(inputConsumed,1);
		maxTurbAddBladeCount = Math.max(maxTurbAddBladeCount,turbAddBladeCount);

		//LeafiaDebug.debugLog(world,"WrongBlades: "+turbAddWrongBlades);
		//LeafiaDebug.debugLog(world,"BladeCount: "+turbAddBladeCount);

		double bladeEfficiency = Math.max(compiledStats.bladeCount-wrongBlades,0)/(double)compiledStats.bladeCount;
		assembly.lastBladeEfficiency = bladeEfficiency;

		double rawMassFlow = inputConsumed*compiledStats.steamMassEquivalent;
		double massFlow = getEffectiveMassFlow(assembly,compiledStats,rawMassFlow);
		assembly.lastRawMassFlow = rawMassFlow;
		assembly.lastEffectiveMassFlow = massFlow;
		assembly.lastAdmissionBufferMass = assembly.admissionBufferMass;
		assembly.lastNominalMassFlow = assembly.nominalMassFlow;

		double stageTorqueScale = 0;
		if (massFlow > 0 && compiledStats.stageSpecificWork > 0)
			stageTorqueScale = massFlow*compiledStats.stageRadius*bladeEfficiency*compiledStats.getBladeAreaFactor()*compiledStats.torqueResponseFactor;
		return new StageRuntimeData(assembly,compiledStats,massFlow,stageTorqueScale);
	}
	private int transferStageFluids(TurbineAssembly assembly,CompiledStageStats compiledStats,double admission) {
		int inputOps = compiledStats.inputAmount > 0 ? assembly.input.getFill()/compiledStats.inputAmount : 0;
		int outputOps = compiledStats.outputAmount > 0 ? (assembly.output.getMaxFill()-assembly.output.getFill())/compiledStats.outputAmount : 0;
		int maxOps = Math.min(inputOps,outputOps);
		double admittedOps = maxOps*admission+assembly.admissionOpCarry;
		int ops = (int)Math.floor(admittedOps);
		assembly.admissionOpCarry = admittedOps-ops;
		if (!AddonConfig.enableGovernedRPS)
			ops = maxOps;
		int inputConsumed = ops*compiledStats.inputAmount;
		int outputProduced = ops*compiledStats.outputAmount;
		// ntmleafia: the steams are meant to be processed instantaneously, no matter what
		// the only bottleneck is meant to be the buffer capacity
		assembly.input.setFill(assembly.input.getFill()-/*inputConsumed*/maxOps*compiledStats.inputAmount);
		assembly.output.setFill(assembly.output.getFill()+/*outputProduced*/maxOps*compiledStats.outputAmount);
		assembly.lastOps = inputConsumed;
		assembly.lastDivision = compiledStats.division;
		return inputConsumed;
	}
	private int countWrongAndStackedBlades(TurbineAssembly assembly) {
		int wrongBlades = 0;
		assembly.maxStackedBlades = 0;
		if (!assembly.receivingPositions.isEmpty()) {
			int startIndex = assembly.getStartPosition();
			int endIndex = assembly.getEndPosition();
			int flowCounterStart = assembly.receivingPositions.size();
			int flowCounter = flowCounterStart;
			boolean shouldBeOpposite = true;
			EnumFacing dir = getAssemblyFacing();
			int stackCounter = 0;
			for (int i = startIndex; i <= endIndex; i++) {
				if (assembly.receivingPositions.contains(i)) {
					flowCounter--;
					shouldBeOpposite = false;
					assembly.maxStackedBlades = Math.max(assembly.maxStackedBlades,stackCounter);
					stackCounter = 0;
				}
				if (assembly.bladeDirections.containsKey(i)) {
					stackCounter++;
					boolean isOpposite = assembly.bladeDirections.get(i);
					if (shouldBeOpposite != isOpposite || (flowCounter > 0 && flowCounter < flowCounterStart)) {
						wrongBlades++;
						LeafiaDebug.debugPos(
								world,
								new BlockPos(pos).offset(dir,i+1),
								0.05f,0xFF0000,
								"BAD BLADE",
								"FLOWCOUNTER: "+flowCounter+"/"+flowCounterStart
						);
					}
				}
			}
			assembly.maxStackedBlades = Math.max(assembly.maxStackedBlades,stackCounter);
			assembly.server$lastReceivingPositions.clear();
			assembly.server$lastReceivingPositions.addAll(assembly.receivingPositions);
			assembly.receivingPositions.clear();
		}
		return wrongBlades;
	}
	private void updateThrottleGovernor(TickSummary tickSummary) {
		double error = getGovernedRPS()-rps;
		double proportionalAdmission = error*THROTTLE_GOVERNOR_PROPORTIONAL_GAIN;
		double unclampedAdmission = governorIntegral+proportionalAdmission;
		double targetAdmission = Math.min(Math.max(unclampedAdmission,0D),1D);
		if (targetAdmission == unclampedAdmission || (targetAdmission <= 0D && error > 0) || (targetAdmission >= 1D && error < 0))
			governorIntegral += error*THROTTLE_GOVERNOR_INTEGRAL_GAIN;
		targetAdmission = Math.min(Math.max(governorIntegral+proportionalAdmission,0D),1D);
		double admissionDelta = targetAdmission-admission;
		admissionDelta = Math.min(Math.max(admissionDelta,-THROTTLE_GOVERNOR_ADMISSION_RATE),THROTTLE_GOVERNOR_ADMISSION_RATE);
		admission += admissionDelta;
	}
	private void applyRotationalStep(CompiledMachineStats machineStats,List<StageRuntimeData> stageData,TickSummary tickSummary) {
		//////////////////////////////////
		double driveTorqueIntercept = 0;
		double driveTorqueOmegaSlope = 0;
		for (StageRuntimeData data : stageData) {
			double actualGearRatio = data.compiledStats.getActualGearRatio(globalGearScale);
			driveTorqueIntercept += data.stageTorqueScale*data.compiledStats.inletWhirl*actualGearRatio;
			driveTorqueOmegaSlope += data.stageTorqueScale*data.compiledStats.stageRadius*actualGearRatio*actualGearRatio;
		}
		tickSummary.targetRPS = Math.min(solveEquilibriumRPS(machineStats,driveTorqueIntercept,driveTorqueOmegaSlope),getGovernedRPS());
		//////////////////////////////////
		//tickSummary.targetRPS = Math.min(solveEquilibriumRPS(machineStats,stageData,globalGearScale),GOVERNED_RPS);

		double omega = rps*TWO_PI;
		for (StageRuntimeData data : stageData)
			tickSummary.driveTorque += data.getDriveTorque(omega,globalGearScale);
		tickSummary.generatorTorque = getGeneratorTorqueAtOmega(machineStats,omega);
		tickSummary.frictionTorque = getFrictionTorqueAtRPS(machineStats,rps);
		tickSummary.windageTorque = getWindageTorqueAtRPS(machineStats,rps);

		double netTorque = tickSummary.driveTorque-tickSummary.generatorTorque-tickSummary.frictionTorque-tickSummary.windageTorque;
		double effectiveWeight = weight*machineStats.rotorInertiaScale;
		omega += netTorque/effectiveWeight*TICK_SECONDS;
		rps = omega/TWO_PI;

		double outputOmega = rps*TWO_PI;
		tickSummary.powerGenerated += (long)Math.max(tickSummary.generatorTorque*outputOmega*machineStats.powerScale,0);
	}
	public long powerOutput = 0;
	public long displayPowerGenerated = 0;
	@SideOnly(Side.CLIENT)
	public void local$spawnParticle(World world,BlockPos pos,double diameter,EnumFacing direction,double speed,int travelDistance,int amount) {
		double x = pos.getX()+0.5;
		double y = pos.getY()+0.5;
		double z = pos.getZ()+0.5;
		x -= direction.getXOffset()*0.5;
		z -= direction.getZOffset()*0.5;
		EnumFacing rotate = direction.rotateY();
		diameter -= 0.75;
		for (int i = 0; i < amount; i++) {
			double angle = world.rand.nextDouble()*Math.PI*2;
			double r = diameter/2*world.rand.nextDouble();
			double lx = x;
			double lz = z;
			EnumFacing ldir = direction;
			double lsp = speed;
			int ldst = travelDistance;
			if (turbulence > 0) {
				// randomize speed if we have turbulence
				double turb = turbulence/100;
				lsp = speed*(1-(world.rand.nextDouble()*0.75+0.25)*turb);
				if (world.rand.nextInt(100) < turbulence) {
					// at random chance (higher chance with higher turbulence) we randomize the position of steam
					int offset = world.rand.nextInt(travelDistance+1);
					ldst = travelDistance-offset;
					lx += direction.getXOffset()*offset;
					lz += direction.getZOffset()*offset;
					if (world.rand.nextBoolean()) {
						// at random chance, we reverse the direction
						ldst = travelDistance-ldst;
						ldir = direction.getOpposite();
					}
				}
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleMTSteam(
					world,
					lx+Math.cos(angle)*r*rotate.getXOffset(),
					y+Math.sin(angle)*r,
					lz+Math.cos(angle)*r*rotate.getZOffset(),
					ldir.getXOffset()*lsp,
					0,
					ldir.getZOffset()*lsp,
					ldst
			));
		}
	}
	public double getVisualDeltaAngle() {
		return Math.pow(rps*360,0.9)/20;
	}
	@Override
	public void update() {
		if (world.isRemote) {
			local$shaftAnglePrev = local$shaftAngle;
			local$shaftAngle += getVisualDeltaAngle();
			if (local$shaftAngle >= 360) {
				local$shaftAngle -= 360;
				local$shaftAnglePrev -= 360;
			}
			for (LCEAudioWrapper audio : local$audios) {
				float ratio = (float)(Math.pow(rps/60,0.75));
				audio.updatePitch(0.5f+ratio);
				audio.updateVolume(0.25f*ratio);
			}
			EnumFacing dir = EnumFacing.byIndex(getBlockMetadata()-10).getOpposite();
			for (TurbineAssembly assembly : assemblies) {
				for (Integer p : assembly.receivingPositions) {
					if (assembly.lastOps <= 0) continue;
					BlockPos center = pos.offset(dir,p+1);
					int index = assembly.positionsInOrder.indexOf(p);
					int distForward = 0;
					int distBackward = 0;
					for (int i = index+1; i < assembly.positionsInOrder.size(); i++) {
						if (assembly.receivingPositions.contains(assembly.positionsInOrder.get(i))) break;
						distForward++;
					}
					for (int i = index-1; i >= 0; i--) {
						if (assembly.receivingPositions.contains(assembly.positionsInOrder.get(i))) break;
						distBackward++;
					}
					double speed = 1+Math.pow(rps,0.75)/1.5;
					int amount = (1+(int)speed/4)*assembly.size;
					if (distForward > 0)
						local$spawnParticle(world,center.offset(dir),assembly.size,dir,speed/20,distForward,amount);
					if (distBackward > 0)
						local$spawnParticle(world,center.offset(dir.getOpposite()),assembly.size,dir.getOpposite(),speed/20,distBackward,amount);
				}
			}
			return;
		}
		if (stressSoundTimer <= 0) { // just fuck off
			stressSoundTimer = 70;
			double vol = Math.pow((Math.max(rps-65,0)/35),3)*0.4;
			if (vol > 0) {
				int interval = 5;
				EnumFacing dir = getAssemblyFacing();
				for (int i = 0; i < length; i+=interval) {
					world.playSound(
							null,
							pos.offset(dir,1+i),
							LeafiaSoundEvents.pipestressed,
							SoundCategory.BLOCKS,
							(float)vol,0.65f
					);
				}
			}
		}
		stressSoundTimer--;

		powerOutput = 0;
		displayPowerGenerated = 0;
		TickSummary tickSummary = new TickSummary();
		maxTurbAddBladeCount = 0;
		turbulenceReasonInputSurge = false;
		turbulenceReasonInverseBlades = false;
		turbulenceReasonTooManyBlades = false;

		// Simulate the current shaft state.
		if (weight > 0) {
			updateThrottleGovernor(tickSummary);
			CompiledMachineStats machineStats = getCompiledMachineStats();
			List<StageRuntimeData> stageData = evaluateStages(admission);
			applyRotationalStep(machineStats,stageData,tickSummary);
		}

		// Apply turbulence damping and transient spike accumulation.
		double turbulenceAdd = Math.pow(Math.max(Math.min(tickSummary.targetRPS,AddonConfig.governedRPS)-rps,0)/24,5)/5;//Math.pow(Math.max(tickSummary.targetRPS-rps,0)/110,7.2)/Math.max(8,60-turbulence*2);
		turbulence = Math.max(turbulence-turbulence*0.008-0.008,0);
		turbulence = Math.min(turbulence+maxTurbAddBladeCount,100);
		double turbMul1 = Math.min(1,weight/300);
		double turbMul2 = Math.pow(Math.max(tickSummary.generatorTorque,0)/10000,0.5);
		turbulence = Math.min(turbulence+turbulenceAdd*AddonConfig.surgeTurbulenceMultiplier*Math.pow(turbMul2,2.25),100);
		if (turbulenceAdd > 0.3) turbulenceReasonInputSurge = true;
		if (maxTurbAddBladeCount > 0.3) turbulenceReasonTooManyBlades = true;

		rps -= rps*Math.pow(turbulence/100,1.35)*0.05;
		//turbulence = 0; // removed temporarily because it's annoying to test

		// Commit the visible summary for overlays/debug output.
		lastTargetRPS = tickSummary.targetRPS;
		lastDriveTorque = tickSummary.driveTorque;
		lastGeneratorTorque = tickSummary.generatorTorque;
		lastFrictionTorque = tickSummary.frictionTorque;
		lastWindageTorque = tickSummary.windageTorque;
		lastGlobalGearScale = globalGearScale;
		lastAdmission = admission;

		powerOutput = tickSummary.powerGenerated;
		displayPowerGenerated = powerOutput;

		// Emit debug traces.
		Tracker._startProfile(this,"update");
		Tracker._tracePosition(this,pos.up(),
				"RPS: "+rps,
				"powerGenerated: "+SIPfx.auto(tickSummary.powerGenerated*20)+"HE/s",
				"Drive torque: "+tickSummary.driveTorque,
				"Generator torque: "+tickSummary.generatorTorque,
				"Friction torque: "+tickSummary.frictionTorque,
				"Windage torque: "+tickSummary.windageTorque,
				"TargetRPS-RPS difference: "+(tickSummary.targetRPS-rps),
				"Admission: "+admission,
				"Turbulence: "+turbulence,
				"Global gear scale: "+globalGearScale,
				"Weight: "+weight+" WU"
		);
		Tracker._endProfile(this);

		// Sync steam buffers and incoming-flow markers to clients.
		NBTTagCompound syncCompound = new NBTTagCompound();
		NBTTagList syncList = new NBTTagList();
		for (TurbineAssembly assembly : assemblies) {
			NBTTagCompound tag = new NBTTagCompound();
			assembly.input.writeToNBT(tag,"i");
			assembly.output.writeToNBT(tag,"o");
			tag.setInteger("f",assembly.lastOps);
			NBTTagList flowList = new NBTTagList();
			for (Integer receiving : assembly.server$lastReceivingPositions)
				flowList.appendTag(new NBTTagShort(receiving.shortValue()));
			tag.setTag("r",flowList);
			syncList.appendTag(tag);
		}
		syncCompound.setTag("a",syncList);
		LeafiaPacket._start(this)
				.__write(MTPacketId.CORE_STEAM_SYNC.id,syncCompound)
				.__write(MTPacketId.CORE_TURBULENCE_REASONS.id,new boolean[]{
						turbulenceReasonInputSurge,
						turbulenceReasonInverseBlades,
						turbulenceReasonTooManyBlades
				})
				.__write(MTPacketId.CORE_WEIGHT.id,weight)
				.__write(MTPacketId.CORE_TURBULENCE.id,turbulence)
				.__write(MTPacketId.CORE_GENERATION.id,displayPowerGenerated)
				//.__write(MTPacketId.CORE_GLOBAL_GEAR.id,globalGearScale)
				.__write(MTPacketId.CORE_RPS.id,rps) // forgor
				.__write(MTPacketId.CORE_OVERDRIVE.id,overdrive)
				.__sendToAffectedClients();
		generateds[needle] = displayPowerGenerated;
		needle = (needle+1)%20;
		generatedPerSec = 0;
		for (long gen : generateds)
			generatedPerSec += gen;
	}
	long[] generateds = new long[20];
	int needle = 0;
	long generatedPerSec = 0;
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> mop = new HashMap<>();
		mop.put("rps",new DataValueFloat((float)rps));
		mop.put("turbulence",new DataValueFloat((float)turbulence));
		mop.put("generated",new DataValueFloat((float)generatedPerSec));
		return mop;
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == MTPacketId.CORE_ASSEMBLY_SYNC.id) {
			disassemble();
			if (value != null)
				assembleFromNBT((NBTTagCompound)value);
		} else if (key == MTPacketId.CORE_STEAM_SYNC.id) {
			NBTTagCompound compound = (NBTTagCompound)value;
			NBTTagList list = compound.getTagList("a",10);
			for (int i = 0; i < assemblies.size(); i++) {
				if (i < list.tagCount()) {
					TurbineAssembly assembly = assemblies.get(i);
					NBTTagCompound tag = list.getCompoundTagAt(i);
					assembly.input.readFromNBT(tag,"i");
					assembly.output.readFromNBT(tag,"o");
					assembly.lastOps = tag.getInteger("f");
					NBTTagList receiving = tag.getTagList("r",2);
					assembly.receivingPositions.clear();
					for (NBTBase j : receiving)
						assembly.receivingPositions.add(((NBTTagShort)j).getInt());
				}
			}
		} else if (key == MTPacketId.CORE_TURBULENCE_REASONS.id) {
			boolean[] values = (boolean[])value;
			turbulenceReasonInputSurge = values[0];
			turbulenceReasonInverseBlades = values[1];
			turbulenceReasonTooManyBlades = values[2];
		} else if (key == MTPacketId.CORE_WEIGHT.id)
			weight = (double)value;
		else if (key == MTPacketId.CORE_TURBULENCE.id)
			turbulence = (double)value;
		else if (key == MTPacketId.CORE_GENERATION.id)
			displayPowerGenerated = (long)value;
		else if (key == MTPacketId.CORE_GLOBAL_GEAR.id)
			globalGearScale = (double)value;
		else if (key == MTPacketId.CORE_RPS.id)
			rps = (double)value;
		else if (key == MTPacketId.CORE_OVERDRIVE.id)
			overdrive = (int)value;
	}
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
	public static class TurbineAssembly {
		/// NOTE: The values stored here are actually 1 lower than actual offset
		public List<Integer> positionsInOrder = new ArrayList<>();
		public FluidTankNTM input;
		public FluidTankNTM output;
		public FluidType typeIn;
		public boolean decompress = false;
		public int size = -1;
		public double baseGearRatio = 1;
		private CompiledStageStats compiledStats;
		public Set<Integer> receivingPositions = new HashSet<>();
		public Set<Integer> server$lastReceivingPositions = new HashSet<>();
		public Map<Integer,Boolean> bladeDirections = new HashMap<>(); // false: normal, true: opposite
		public int lastOps = 0;
		public double lastDivision = 1;
		public int lastWrongBlades = 0;
		public double lastBladeEfficiency = 0;
		public double admissionBufferMass = 0;
		public double admissionOpCarry = 0;
		public double nominalMassFlow = 0;
		public double lastRawMassFlow = 0;
		public double lastEffectiveMassFlow = 0;
		public double lastAdmissionBufferMass = 0;
		public double lastNominalMassFlow = 0;
		public int maxStackedBlades = 0;
		int getStartPosition() {
			return positionsInOrder.get(0);
		}
		int getEndPosition() {
			return positionsInOrder.get(positionsInOrder.size()-1);
		}
	}
	public boolean turbulenceReasonInputSurge = false;
	public boolean turbulenceReasonInverseBlades = false;
	public boolean turbulenceReasonTooManyBlades = false;
	public double maxTurbAddBladeCount = 0;
	public double turbulence;
	public void disassemble() {
		for (ModularTurbineComponentTE component : components) {
			component.assembly = null;
			component.core = null;
		}
		assemblies.clear();
		components.clear();
		positionToComponentMap.clear();
		length = 0;
		weight = 0;
		compiledMachineStats = null;
		globalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
		lastGlobalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
		admission = 0;
		lastAdmission = 0;
		governorIntegral = 0;
		if (!world.isRemote) {
			LeafiaPacket._start(this)
					.__write(MTPacketId.CORE_ASSEMBLY_SYNC.id,writeAssemblyToNBT(new NBTTagCompound()))
					.__sendToAffectedClients();
		}
	}
	public static class AssemblyReturnCode {
		public static class ReturnCodeSuccess extends AssemblyReturnCode { }
		public static class ReturnCodeError extends AssemblyReturnCode {
			public final BlockPos errorPosA;
			public final BlockPos errorPosB;
			public final AssemblyErrorReason reason;
			public ReturnCodeError(BlockPos errorPosA,BlockPos errorPosB,AssemblyErrorReason reason) {
				this.errorPosA = errorPosA;
				this.errorPosB = errorPosB;
				this.reason = reason;
			}
		}
		public enum AssemblyErrorReason {
			OPEN_END,
			NOT_ALIGNED,
			INCOMPATIBLE_SIZE,
			BUG,
			NO_IDENTIFIER,
			IDENTIFIER_MISMATCH,
			CANT_DECOMPRESS,
			INVALID_IDENTIFIER,
			NO_BLADES,
			TOO_LONG,
			SIZE_MISMATCH, // Only the same sized components may be used for each types of steam
		}
	}
	@Override
	public void invalidate() {
		disassemble();
		stopAllSounds();
		local$audios.clear();
		ControlEventSystem.get(world).removeControllable(this);
		super.invalidate();
	}
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	public AssemblyReturnCode reassemble() {
		AssemblyReturnCode code = reassemble_internal();
		if (code instanceof ReturnCodeError)
			disassemble();
		LeafiaPacket._start(this)
				.__write(MTPacketId.CORE_ASSEMBLY_SYNC.id,writeAssemblyToNBT(new NBTTagCompound()))
				.__sendToAffectedClients();
		return code;
	}
	protected AssemblyReturnCode reassemble_internal() {
		disassemble();
		int[] connectable = new int[]{};
		boolean prevWasSeparator = false;
		int prevSize = 1;
		EnumFacing dir = getAssemblyFacing();
		BlockPos offs = new BlockPos(pos);
		BlockPos prevPos = offs;
		TurbineAssembly lastAssembly = null;
		BlockPos previousPortPos = null;
		int i = 0;
		int leastCompression = -1;
		int mostCompression = -1;
		while (true) {
			if (i > 300) // Who in the sane mind does this?
				return new ReturnCodeError(pos,offs,AssemblyErrorReason.TOO_LONG);
			offs = offs.offset(dir);
			if (world.getBlockState(offs).getBlock() instanceof ModularTurbineBlockBase turbine) {
				boolean aligned = true;
				BlockPos core = turbine.findCore(world,offs);
				if (core == null)
					return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.BUG);
				else {
					EnumFacing partDir = EnumFacing.byIndex(world.getBlockState(core).getValue(BlockDummyable.META)-10).getOpposite();
					if (world.getTileEntity(core) instanceof ModularTurbineComponentTE te) {
						positionToComponentMap.put(i,te);
						if (!components.contains(te)) {
							weight += turbine.weight();
							components.add(te);
							te.core = this;

							// ALIGNMENT CHECK
							if (dir.getAxis() == Axis.X)
								aligned = core.getZ() == offs.getZ();
							else if (dir.getAxis() == Axis.Z)
								aligned = core.getX() == offs.getX();
							aligned = aligned && (core.getY()+turbine.shaftHeight() == offs.getY());
							if (!aligned)
								return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.NOT_ALIGNED);

							// DIRECTION CHECK
							if (!partDir.getAxis().equals(dir.getAxis()))
								return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.NOT_ALIGNED); // bruh

							// COMPATIBILITY CHECK
							int size = turbine.size();
							boolean compatible = connectable.length == 0 || turbine.canConnectTo().length == 0;
							for (int s : connectable) {
								if (s == size) {
									compatible = true;
									break;
								}
							}
							for (int s : turbine.canConnectTo()) {
								if (s == prevSize) {
									compatible = true;
									break;
								}
							}
							if (!compatible)
								return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.INCOMPATIBLE_SIZE);
							prevSize = size;

							// UPDATING PREVIOUS PARAMETERS & FORMING ASSEMBLY
							connectable = turbine.canConnectTo();
							boolean isSeparator = turbine.componentType().isSeparator;
							if (turbine.componentType().equals(TurbineComponentType.INLINE_PORT)) {
								// IF IT'S INLINE PORT, OVERRIDE isSeparator SO
								// ONE OF THEM WILL FORM AN ASSEMBLY WHEN 2 OF THEM ARE PLACED IN A ROW
								if (prevWasSeparator)
									isSeparator = false;
							}
							boolean joinsSteamAssembly = !isSeparator;

							// IF IT'S INLINE PORT, EXPAND PREVIOUS ASSEMBLY
							// THIS IS THE ONLY WAY WE CAN GET 2 ASSEMBLIES ADJACENT
							// WITHOUT HAVING TO PLACE A SEPARATOR IN-BETWEEN
							if (turbine.componentType().equals(TurbineComponentType.INLINE_PORT)) {
								if (lastAssembly != null)
									joinsSteamAssembly = true;
							}

							if (!isSeparator && !prevWasSeparator && i == 0)
								return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.OPEN_END);

							if (joinsSteamAssembly) {
								// ELSE, CREATE A NEW ASSEMBLY OR EXPAND PREVIOUS ONE
								if (lastAssembly == null) {
									lastAssembly = new TurbineAssembly();
									assemblies.add(lastAssembly);
								}
								lastAssembly.positionsInOrder.add(i);
								te.assembly = lastAssembly;
								if (turbine.componentType().equals(TurbineComponentType.BLADES)) {
									lastAssembly.bladeDirections.put(i,!dir.equals(partDir));
									lastAssembly.size = turbine.size();
								}

								// CHECK FOR IDENTIFIERS
								if (te instanceof MTComponentPortTE port) {
									if (port.identifier != null) {
										// CHECK IF THERE WAS ALREADY A COMPONENT WITH IDENTIFIER
										if (lastAssembly.typeIn != null) {
											// IF SO, CHECK IF THE IDENTIFIER MATCHES
											// previousPortPos IS ALWAYS NOT NULL HERE BECAUSE
											// WHEN typeIn IS SET previousPortPos IS ALSO SET AND THUS
											// IMPOSSIBLE TO REACH HERE WITH IT BEING NULL

											// JUST SAYIN' BECAUSE I'M DUMB
											if (!lastAssembly.typeIn.equals(port.identifier) || lastAssembly.decompress != port.decompress)
												return new ReturnCodeError(previousPortPos,offs,AssemblyErrorReason.IDENTIFIER_MISMATCH);
										}
										lastAssembly.typeIn = port.identifier;
										lastAssembly.decompress = port.decompress;
										previousPortPos = offs;

										// SET LEAST/MOST COMPRESSION FOR LATER USE
										FluidType nextSteam = getNextSteam(port.identifier,port.decompress);
										if (nextSteam == null) {
											if (port.decompress)
												return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.CANT_DECOMPRESS);
											else
												return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.INVALID_IDENTIFIER);
										}
										int steamIndex = steamTypes.indexOf(port.identifier);
										int nextSteamIndex = steamTypes.indexOf(nextSteam);
										if (steamIndex == -1)
											return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.INVALID_IDENTIFIER);
										if (mostCompression == -1 || nextSteamIndex < mostCompression)
											mostCompression = nextSteamIndex;
										if (leastCompression == -1 || steamIndex > leastCompression)
											leastCompression = steamIndex;
									}
								}
							}
							if (isSeparator) {
								// SEPARATE ASSEMBLY IF IT'S A SEPARATOR
								if (lastAssembly != null) {
									if (lastAssembly.typeIn == null)
										return new ReturnCodeError(pos.offset(dir,lastAssembly.getStartPosition()+1),prevPos,AssemblyErrorReason.NO_IDENTIFIER);

									if (lastAssembly.bladeDirections.isEmpty())
										return new ReturnCodeError(pos.offset(dir,lastAssembly.getStartPosition()+1),prevPos,AssemblyErrorReason.NO_BLADES);
								}
								lastAssembly = null;
							}
							// DON'T REPLACE THIS WITH isSeparator VARIABLE AS IT GETS
							// OVERRIDDEN ON INLINE_PORT TYPE
							prevWasSeparator = turbine.componentType().isSeparator;
						}
					} else
						return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.BUG);
				}
			} else {
				if (!prevWasSeparator)
					return new ReturnCodeError(prevPos,offs,AssemblyErrorReason.OPEN_END);
				if (lastAssembly != null) {
					if (lastAssembly.typeIn == null)
						return new ReturnCodeError(pos.offset(dir,lastAssembly.getStartPosition()+1),offs,AssemblyErrorReason.NO_IDENTIFIER);

					if (lastAssembly.bladeDirections.isEmpty())
						return new ReturnCodeError(pos.offset(dir,lastAssembly.getStartPosition()+1),prevPos,AssemblyErrorReason.NO_BLADES);
				}
				break;
			};
			prevPos = offs;
			i++;
		}
		length = i;
		// CALCULATE BUFFERS
		for (TurbineAssembly assembly : assemblies) {
			// temporary values
			FluidType nextSteam = getNextSteam(assembly.typeIn,assembly.decompress);
			long inputCapacity = calculateBufferSize(assembly.typeIn,mostCompression,assembly.bladeDirections.keySet().size(),assembly.size);
			long outputCapacity = calculateBufferSize(nextSteam,mostCompression,assembly.bladeDirections.keySet().size()*4,assembly.size);
			assembly.input = new FluidTankNTM(assembly.typeIn,requireTankCapacity(inputCapacity,assembly.typeIn));
			assembly.output = new FluidTankNTM(nextSteam,requireTankCapacity(outputCapacity,nextSteam));
		}
		compileAssemblyModel(true);
		globalGearScale = DEFAULT_GLOBAL_GEAR_SCALE;
		admission = 0;
		lastAdmission = 0;
		governorIntegral = 0;
		return new ReturnCodeSuccess();
	}
	public long calculateBufferSize(FluidType fluid,int mostCompression,int blades,int size) {
		// mostCompression IS A SMALLER VALUE BECAUSE THE LOWER THE INDEX IS, THE MORE COMPRESSED THE STEAM IS
		int index = steamTypes.indexOf(fluid);
		mostCompression = 0; // ah forget it
		int delta = index-mostCompression;
		double buffer = Math.pow(10,delta)/Math.pow(4,delta)*blades*Math.pow(8,(size-1)/2d)*(4500d/steamTypes.get(mostCompression).temperature)*BUFFER_CAPACITY_MULTIPLIER;
		if (fluid.equals(Fluids.SPENTSTEAM))
			buffer /= 1000;
		return (long)buffer;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		rps = compound.getDouble("rps");
		turbulence = compound.getDouble("turbulence");
		overdrive = compound.getInteger("overdrive");
		if (compound.hasKey("assembly"))
			LeafiaPassiveServer.queueFunction(()->assembleFromNBT(compound.getCompoundTag("assembly")));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("rps",rps);
		compound.setDouble("turbulence",turbulence);
		compound.setTag("assembly",writeAssemblyToNBT(new NBTTagCompound()));
		compound.setInteger("overdrive",overdrive);
		return super.writeToNBT(compound);
	}
	List<LCEAudioWrapper> local$audios = new ArrayList<>();
	public void stopAllSounds() {
		for (LCEAudioWrapper audio : local$audios)
			audio.stopSound();
	}
	BiFunction<Float,Double,Double> local$attenuationFunc = (vol,distance)->{
		double ratio = 1-Math.max(0,distance-5)/30;
		return vol*Math.pow(Math.max(0,ratio),3.5);
	};
	@SideOnly(Side.CLIENT)
	public void local$createAudios() {
		int interval = 10;
		EnumFacing dir = getAssemblyFacing();
		for (int i = 0; i < length; i+=interval) {
			BlockPos p = pos.offset(dir,i+1);
			local$audios.add(AddonBase.proxy.getLoopedSoundStartStop(
					world,
					LeafiaSoundEvents.modular_turbine,
					null,null,
					SoundCategory.BLOCKS,
					p.getX()+0.5f,p.getY()+0.5f,p.getZ()+0.5f,
					0.01f,0.5f
			).setLooped(true).setCustomAttenuation(local$attenuationFunc).startSound());
		}
	}
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		stopAllSounds();
		local$audios.clear();
	}
}
