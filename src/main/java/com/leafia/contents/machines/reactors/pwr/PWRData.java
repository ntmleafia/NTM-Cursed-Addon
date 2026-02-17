package com.leafia.contents.machines.reactors.pwr;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.fluid.CoriumFinite;
import com.hbm.config.MobConfig;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingStep;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Gaseous;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Liquid;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.AdvancementManager;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.Tuple.Triplet;
import com.leafia.CommandLeaf;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentEntity;
import com.leafia.contents.machines.reactors.pwr.blocks.components.channel.PWRChannelBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.channel.PWRConductorBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementTE;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck.Erosion;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.AddonAdvancements;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.exceptions.messages.TextWarningLeafia;
import com.llib.group.LeafiaMap;
import com.llib.group.LeafiaSet;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

public class PWRData implements ITickable, LeafiaPacketReceiver {
	public BlockPos corePos;
	public FluidTankNTM[] tanks;
	public FluidType[] tankTypes;
	public int coolantId = Fluids.COOLANT.getID();
	public int compression = 0;
	//public double heat = 20;
	public int coriums = 0;
	public double masterControl = 0.25;
	public final Map<String, Double> controlDemand = new HashMap<>();
	public int toughness = 16_000;

	PWRComponentBlock getPWRBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof PWRComponentBlock)
			return (PWRComponentBlock) block;
		return null;
	}

	PWRComponentBlock getPWRComponent(World world, BlockPos pos) {
		PWRComponentBlock block = getPWRBlock(world, pos);
		if (block == null) return null;
		return block.tileEntityShouldCreate(world, pos) ? block : null;
	}

	PWRComponentEntity getPWREntity(World world, BlockPos pos) {
		PWRComponentBlock pwr = getPWRBlock(world, pos);
		if (pwr != null)
			return pwr.getPWR(world, pos);
		return null;
	}

	public Set<BlockPos> members = new HashSet<>();
	public Set<BlockPos> controls = new HashSet<>();
	public Set<BlockPos> fuels = new HashSet<>();
	public LeafiaSet<BlockPos> projection = new LeafiaSet<>();

	public void onDiagnosis(World world) {
		if (world.isRemote) {

		} else {
			int slots = 0;
			for (BlockPos pos : projection) {
				PWRComponentBlock block = getPWRComponent(world, pos);
				if (block != null) {
					if (block instanceof PWRElementBlock) {
						slots++;
					}
				}
			}
			if (remoteSize != slots) {
				remoteContainer = new ItemStackHandler(slots);
				remoteSize = slots;
			}
			for (FluidTankNTM tank : tanks) {
				if (tank.getFill() > tank.getMaxFill())// whoops
					tank.setFill(tank.getMaxFill());
			}
			addDataToPacket(LeafiaPacket._start(companion), this).__sendToAffectedClients();
		}
	}

	public Pair<LeafiaSet<BlockPos>, LeafiaSet<BlockPos>> getProjectionFuelAndControlPositions() {
		Pair<LeafiaSet<BlockPos>, LeafiaSet<BlockPos>> output = new Pair<>(new LeafiaSet<>(), new LeafiaSet<>());
		for (BlockPos pos : projection) {
			Block block = getWorld().getBlockState(pos).getBlock();
			if (block instanceof PWRElementBlock)
				output.getKey().add(pos);
			else if (block instanceof PWRControlBlock)
				output.getValue().add(pos);
		}
		return output;
	}

	public BlockPos terminal_toGlobal(IBlockState terminal, BlockPos terminalPos, BlockPos pos) {
		EnumFacing face = terminal.getValue(BlockHorizontal.FACING).getOpposite();
		Vec3d lookVector = new Vec3d(face.getDirectionVec()); // amazingly, Vec3i does not has .scale() method LMAO
		Vec3d rightVector = lookVector.crossProduct(new Vec3d(0, 1, 0));
		return new BlockPos(new Vec3d(terminalPos).add(0.5, 0.5 + pos.getY(), 0.5).add(lookVector.scale(-pos.getZ())).add(rightVector.scale(pos.getX())));
	}

	public BlockPos terminal_toLocal(IBlockState terminal, BlockPos terminalPos, BlockPos pos) {
		EnumFacing face = terminal.getValue(BlockHorizontal.FACING).getOpposite();
		BlockPos relative = pos.subtract(terminalPos);
		switch (face) { // IM STUPID OK??
			case NORTH:
				return relative;
			case SOUTH:
				return new BlockPos(-relative.getX(), relative.getY(), -relative.getZ());
			case WEST:
				return new BlockPos(-relative.getZ(), relative.getY(), relative.getX());
			case EAST:
				return new BlockPos(relative.getZ(), relative.getY(), -relative.getX());
			default:
				throw new LeafiaDevFlaw("PWR Terminals should only face sideways, got " + face.getName() + " how the fuck does this even happen");
		}
	}

	public TileEntity companion;

	public ItemStackHandler resourceContainer = new ItemStackHandler(3) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			companion.markDirty();
			if (slot == 0)
				updateCoolantTypeFromItem(getStackInSlot(slot));
		}
	};
	void updateCoolantTypeFromItem(ItemStack stack) {
		if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
			FluidType type = identifier.getType(getWorld(),corePos.getX(),corePos.getY(),corePos.getZ(),stack);
			if (updateCoolantType(type.getID()))
				coolantId = type.getID();
		}
	}
	public double multiplier = 1;
	boolean updateCoolantType(int coolantId) {
		FluidType coolant = Fluids.fromID(coolantId);
		if (!coolant.equals(Fluids.NONE)) {
			this.coolantId = coolantId;
			Triplet<FluidType,FluidType,Double> hots = getBoiledCoolant(coolant);
			if (hots != null) {
				if (tankTypes[0] != coolant)
					tanks[2].setFill(0);
				if (tankTypes[1] != hots.getX())
					tanks[2].setFill(0);
				if (tankTypes[2] != hots.getX())
					tanks[2].setFill(0);
				tankTypes[0] = coolant;
				tankTypes[1] = hots.getX();
				tankTypes[2] = hots.getY();
				for (int i = 0; i < 3; i++)
					tanks[i].setTankType(tankTypes[i]);
				multiplier = hots.getZ();
				resizeTanks(lastChannels,lastConductors);
					/*
					Fluid hotter = hot;
					while (true) {
						Fluid hottest = HeatRecipes.getBoilFluid(hotter);
						if (hottest == null) break;
						else {
							hotter = hottest;
							if (hottest.isGaseous()) {
								if (tankTypes[2] != hottest)
									tanks[2].drain(tanks[2].getMaxFill(), true);
								tankTypes[2] = hottest;
							}
						}
					}*/
				return true;
			}
		}
		return false;
	}
	public ItemStackHandler remoteContainer = new ItemStackHandler(0);
	int remoteSize = 0;

	public void invalidate(World world) {
		if (!(world instanceof WorldServer)) return;
		BlockPos checkPos = companion.getPos();
		//((PWRComponentEntity) companion).assignCore(null); how many hours was I smoking my tail when I was coding this?
		world.getMinecraftServer().addScheduledTask(() -> {
			if (world.getBlockState(checkPos).getBlock() instanceof CoriumFinite)
				this.explode(world, null,null,1);
		});
	}

	public PWRData(TileEntity entity) {
		tanks = new FluidTankNTM[]{
				new FluidTankNTM(Fluids.COOLANT,128_000),
				new FluidTankNTM(Fluids.COOLANT_HOT,128_000),
				new FluidTankNTM(AddonFluids.COOLANT_MAL,16_000),

				new FluidTankNTM(Fluids.WATER,512_000),
				new FluidTankNTM(Fluids.STEAM,256_000)
		};
		tankTypes = new FluidType[]{
				Fluids.COOLANT,
				Fluids.COOLANT_HOT,
				AddonFluids.COOLANT_MAL,

				Fluids.WATER,
				Fluids.STEAM
		};
		this.companion = entity;
		this.corePos = companion.getPos();
		//valid = !entity.isInvalid(); im fucking stupid
	}

	public World getWorld() {
		return this.companion.getWorld();
	}

	public void onUpdateCompression() {
		if (compression == 0) {
			tanks[4].setTankType(Fluids.STEAM);
			tankTypes[4] = Fluids.STEAM;
		} else if (compression == 1) {
			tanks[4].setTankType(Fluids.HOTSTEAM);
			tankTypes[4] = Fluids.HOTSTEAM;
		} else if (compression == 2) {
			tanks[4].setTankType(Fluids.SUPERHOTSTEAM);
			tankTypes[4] = Fluids.SUPERHOTSTEAM;
		}
	}

	protected FluidTankNTM resizeTank(FluidTankNTM tank,int newCapacity) {
		if (newCapacity == tank.getMaxFill()) return tank;
		FluidTankNTM resized = new FluidTankNTM(tank.getTankType(),newCapacity);
		resized.setFill(Math.min(newCapacity,tank.getFill()));
		return resized;
	}

	public int lastChannels = 0;
	public int lastConductors = 0;
	public void resizeTanks(int channels, int conductors) {
		tanks[0] = resizeTank(tanks[0], 3200 * channels); // coolant
		tanks[1] = resizeTank(tanks[1], 3200 * (int)Math.ceil(channels/(tankTypes[1].hasTrait(FT_Gaseous.class) ? 1.5 : 1)*multiplier)); // hot coolant
		tanks[2] = resizeTank(tanks[2], (int)Math.ceil(toughness/(float)(tankTypes[1].hasTrait(FT_Gaseous.class) ? 10 : 1)*multiplier)); // emergency buffer
		tanks[3] = resizeTank(tanks[3], 25600 * conductors); // water
		tanks[4] = resizeTank(tanks[4], 12800 * conductors); // steam
	}

	FluidType getBoilFluid(FluidType f) {
		if (f.hasTrait(FT_Heatable.class))
			return f.getTrait(FT_Heatable.class).getFirstStep().typeProduced;
		return null;
	}

	double getBoilRatio(FluidType f) {
		if (f.hasTrait(FT_Heatable.class)) {
			HeatingStep step = f.getTrait(FT_Heatable.class).getFirstStep();
			return step.amountProduced/(double)step.amountReq;
		}
		return 0;
	}

	Triplet<FluidType,FluidType,Double> getBoiledCoolant(FluidType f) {
		FluidType hot = getBoilFluid(f);
		double mul = 1;
		if (hot == null) return null;
		FluidType secondhot = getBoilFluid(f);
		mul *= getBoilRatio(f);
		double mul2 = mul;
		while (true) {
			FluidType hotter = getBoilFluid(hot);
			if (hotter != null) {
				mul = mul2;
				mul2 *= getBoilRatio(hot);
				secondhot = hot;
				hot = hotter;
			} else break;
		}
		return new Triplet<>(secondhot,hot,mul);
	};

	public PWRData readFromNBT(NBTTagCompound nbt) {
		nbt = nbt.getCompoundTag("data");
		if (nbt.hasKey("compression"))
			compression = nbt.getInteger("compression");
		onUpdateCompression();
		tankTypes[0] = Fluids.COOLANT;
		tankTypes[1] = Fluids.COOLANT_HOT;
		tankTypes[2] = AddonFluids.COOLANT_MAL;
		if (nbt.hasKey("coolantId")) {
			coolantId = nbt.getInteger("coolantId");
			updateCoolantType(coolantId);
		}
		//if (nbt.hasKey("heat"))
		//	heat = nbt.getDouble("heat");
		if (nbt.hasKey("tank0"))
			tanks[0].readFromNBT(nbt,"tank0");
		if (nbt.hasKey("tank1"))
			tanks[1].readFromNBT(nbt,"tank1");
		if (nbt.hasKey("tank2"))
			tanks[2].readFromNBT(nbt,"tank2");
		if (nbt.hasKey("tank3"))
			tanks[3].readFromNBT(nbt,"tank3");
		if (nbt.hasKey("tank4"))
			tanks[4].readFromNBT(nbt,"tank4");
		if (nbt.hasKey("remoteContainerSize"))
			remoteSize = nbt.getInteger("remoteContainerSize");
		if (nbt.hasKey("resourceContainer"))
			resourceContainer.deserializeNBT(nbt.getCompoundTag("resourceContainer"));
		if (nbt.hasKey("projectionMap")) {
			projection.clear();
			NBTTagList nbtjection = nbt.getTagList("projectionMap", 11/*INT[], refer to NBTBase*/);
			for (NBTBase item : nbtjection) {
				NBTTagIntArray array = (NBTTagIntArray) item;
				int[] coords = array.getIntArray();
				projection.add(new BlockPos(coords[0], coords[1], coords[2]));
			}
		}
		if (nbt.hasKey("controlMaster"))
			masterControl = nbt.getDouble("controlMaster");
		if (nbt.hasKey("controlDemand"))
			readControlPositions(nbt.getCompoundTag("controlDemand"));
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound mainCompound) {
		NBTTagCompound nbt = new NBTTagCompound();
		//nbt.setDouble("heat", heat);
		if (Fluids.COOLANT.getID() != coolantId)
			nbt.setInteger("coolantId",coolantId);
		nbt.setInteger("compression", compression);
		tanks[0].writeToNBT(nbt,"tank0");
		tanks[1].writeToNBT(nbt,"tank1");
		tanks[2].writeToNBT(nbt,"tank2");
		tanks[3].writeToNBT(nbt,"tank3");
		tanks[4].writeToNBT(nbt,"tank4");
		nbt.setInteger("remoteContainerSize", remoteSize);
		nbt.setTag("resourceContainer", resourceContainer.serializeNBT());
		NBTTagList nbtjection = new NBTTagList();
		for (BlockPos pos : projection) {
			nbtjection.appendTag(new NBTTagIntArray(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
		}
		nbt.setTag("projectionMap", nbtjection);

		nbt.setDouble("controlMaster", masterControl);
		nbt.setTag("controlDemand", writeControlPositions());

		mainCompound.setTag("data", nbt);
		return mainCompound;
	}

	public void readControlPositions(NBTTagCompound nbt) {
		controlDemand.clear();
		for (String key : nbt.getKeySet())
			controlDemand.put(key, nbt.getDouble(key));
	}

	public void resyncControls() {
		LeafiaPacket._start(companion).__write(27, writeFullControls()).__sendToAffectedClients();
	}

	public NBTTagCompound writeFullControls() {
		NBTTagList nbt = new NBTTagList();
		for (BlockPos pos : controls) {
			TileEntity entity = getWorld().getTileEntity(pos);
			if (entity instanceof PWRControlTE control)
				nbt.appendTag(control.writeControlDateToNBT());
		}
		NBTTagCompound result = new NBTTagCompound();
		result.setTag("controls", nbt);
		return result;
	}

	public NBTTagCompound writeControlPositions() {
		Set<String> existingNames = new LeafiaSet<>();
		for (BlockPos pos : controls) {
			TileEntity entity = getWorld().getTileEntity(pos);
			if (entity instanceof PWRControlTE control)
				existingNames.add(control.name);
		}
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<String, Double> entry : controlDemand.entrySet()) {
			if (existingNames.contains(entry.getKey()))
				nbt.setDouble(entry.getKey(), entry.getValue());
		}
		return nbt;
	}

	boolean valid = false;
	float timeToDrainMalcoolant = 15;
	public int warnTicks = 0;
	public double stressTimer = 300;

	/*protected boolean inputValidForTank(int tank, int slot) {
		if (!resourceContainer.getStackInSlot(slot).isEmpty() && tanks[tank] != null) {
			if (resourceContainer.getStackInSlot(slot).getItem() == ModItems.fluid_barrel_infinite || isValidFluidForTank(tank, FluidUtil.getFluidContained(resourceContainer.getStackInSlot(slot)))) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidFluidForTank(int tank, FluidStack stack) {
		if (stack == null || tanks[tank] == null)
			return false;
		return stack.getFluid() == tankTypes[tank];
	}*/

	public double spendAccum = 0;

	@Override
	public void update() {
		if (!valid) {
			if (!companion.isInvalid()) {
				valid = true;
				Block block = companion.getBlockType();
				if (block instanceof PWRComponentBlock) {
					this.corePos = companion.getPos();
					((PWRComponentBlock) block).beginDiagnosis(getWorld(), companion.getPos(), companion.getPos());
				} else
					companion.invalidate(); // you're coming with me
			}
			return;
		}
		if (getWorld().isRemote) {
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("hello... here at "+companion.getPos()+".. ,,uwu,,"));
			// The debug code above is a serious sign of mental illness.
			warnTicks = Math.floorMod(warnTicks + 1, 8);
		} else {
			//for (EntityPlayer player : getWorld().playerEntities) {
			//    player.sendMessage(new TextComponentString("" + getWorld().isRemote + "! Im at " + companion.getPos()));
			//}
			tanks[3].loadTank(1,2,resourceContainer);

			if (coriums > 0)
				spendCoolant(Math.pow(coriums * 2727, 0.414), null);
			if (tanks[2].getFill() > 0) {
				int decr = Math.min(tanks[2].getFill(), tanks[2].getMaxFill() / Math.round(timeToDrainMalcoolant * 20));
				if (tanks[1].getMaxFill() - tanks[1].getFill() >= decr * 4) {
					tanks[1].setFill(Math.min(tanks[1].getFill()+decr*4,tanks[1].getMaxFill()));
					tanks[2].setFill(Math.max(tanks[2].getFill()-decr,0));
				}
				// directly copied from zirnox port lmfao
				double stress = tanks[2].getFill() / (double) tanks[2].getMaxFill();
				stressTimer -= Math.pow(stress, 0.9) * 64;
				if (stressTimer <= 0) {
					BlockPos pos = (BlockPos) members.toArray()[getWorld().rand.nextInt(members.size())];
					getWorld().playSound(null, pos.getX() + 0.5, pos.getY() + 2.5, pos.getZ() + 0.5, LeafiaSoundEvents.stressSounds[getWorld().rand.nextInt(7)], SoundCategory.BLOCKS, (float) MathHelper.clampedLerp(0.25, 14, Math.pow(stress, 4)), 1.0F);
				}
			}
			if (tanks[3].getMaxFill() > 0) {
				int conversionRate = 8;
				int consumption = (int) Math.round(Math.pow(tanks[1].getFill() / (double) Math.max(tanks[3].getMaxFill(), 1) * 4, 0.4) * 80 * conversionRate);
				FluidStack stack = tanks[1].drain(consumption / conversionRate, false);
				FluidStack stack2 = tanks[3].drain(consumption, false);
				if (stack != null && stack2 != null) {
					int boilAmt = Math.min(stack.amount * conversionRate, stack2.amount);
					int division = (int) Math.pow(10, compression);
					int filled = tanks[4].fill(tankTypes[4], boilAmt * 100 / division, true);
					int drained = (Math.min(boilAmt, filled * division / 100) / conversionRate);
					tanks[1].setFill(Math.max(tanks[1].getFill()-drained,0));
					tanks[0].fill(tankTypes[0],(int)(drained/multiplier),true);
					tanks[3].setFill(Math.max(tanks[3].getFill()-(Math.min(boilAmt, filled * division / 100)),0));
				}
			}
			LeafiaPacket._start(companion).__write(30, new int[]{
					compression,
					tanks[0].getMaxFill(),
					tanks[1].getMaxFill(),
					tanks[2].getMaxFill(),
					tanks[3].getMaxFill(),
					tanks[4].getMaxFill(),
					tanks[0].getFill(),
					tanks[1].getFill(),
					tanks[2].getFill(),
					tanks[3].getFill(),
					tanks[4].getFill()
			}).__sendToAffectedClients();
		}
	}

	int boilingAccum = 0;
	public static final double transferMultiplier = 12;
	public static final int boilingDivision = 48;

	public void spendCoolant(double cooled, @Nullable ItemStack stack) {
		double drainD = cooled * transferMultiplier/multiplier;
		int drain = (int) Math.floor(drainD);///12500*tanks[0].getMaxFill());

		double accum = drainD - drain;
		spendAccum += accum;
		int add = (int) Math.floor(spendAccum);
		spendAccum -= add;
		drain += add;

		drain = (int)((int)(drain*multiplier)/multiplier);

		FluidStack fs = tanks[0].drain(drain, true);
		if (fs != null) {
			int drained = (int)(fs.amount*multiplier);
			for (int tank = 1; drained > 0; tank++) {
				switch (tank) {
					case 1: {
						int filled = tanks[tank].fill(tankTypes[tank], drained, true);
						drained -= filled;
					}
					break;
					case 2: {
						int fill = drained / boilingDivision;
						boilingAccum += Math.floorMod(drained, boilingDivision);
						int fillAdd = boilingAccum / boilingDivision;
						boilingAccum -= fillAdd;
						int filled = tanks[tank].fill(tankTypes[tank], fill + fillAdd, true);
						drained -= filled * boilingDivision;
					}
					break;
					default:
						if (tanks[2].getFill() >= tanks[2].getMaxFill())
							explode(getWorld(), stack, null,0); // Blowout
						return;
				}
			}
		}
		/*
		if (tanks[0].getFill() > 0) {
			if ()
				hotType = 2;
			tanks[hotType].fill(new FluidStack(tankTypes[hotType],drain),true);
			//TODO: make it so both hot coolant buffer and emergency buffer are scaled by blast resistances of surrounding blocks
			//TODO: lower blast res. would result in easier failure but less destructive
			//TODO: resistance would be determined by average resistance of surrounding blocks with little more distribution to the stronger blocks
			//TODO: weak blocks surrounded by strong blocks wouldn't explode this way as individual resistances are not taken into account
			if (tanks[2].getFill() >= tanks[2].getMaxFill())
		}*/
	}

	boolean exploded = false;
	World explodeWorld = null;

	Set<BlockPos> growMembers(Set<BlockPos> set) { // have this highly laggy retarded Solution
		Set<BlockPos> offsets = new HashSet<>();
		for (BlockPos member : set) {
			for (EnumFacing facing : EnumFacing.values()) {
				BlockPos offset = member.add(facing.getXOffset(), facing.getYOffset(), facing.getZOffset());
				//if (!members.contains(offset)) {
				//if (explodeWorld.getBlockState(offset).getBlock().getExplosionResistance(null) >= 50)
				offsets.add(offset);
				//}
			}
		}
		for (BlockPos offset : offsets) {
			members.add(offset); // fuck you ConcurrentModificationException
		}
		return offsets;
	}

	double signedPow(double x, double y) {
		return Math.pow(Math.abs(x), y) * Math.signum(x);
	}

	protected class PWRExplosion {
		final World world;
		final Vec3d centerPoint;
		final int minX;
		final int minY;
		final int minZ;
		final int maxX;
		final int maxY;
		final int maxZ;

		public PWRExplosion(World world, Vec3d centerPoint, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.world = world;
			this.centerPoint = centerPoint;
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		protected void explodeLv1() {
			for (BlockPos member : members) {
				Block block = world.getBlockState(member).getBlock();
				if (block instanceof PWRChannelBlock || block instanceof PWRConductorBlock) {
					world.setBlockState(member,ModBlocks.gas_radon_dense.getDefaultState());
					for (EnumFacing face : EnumFacing.values()) {
						BlockPos offs = member.offset(face);
						if (world.isValid(offs) && world.getBlockState(offs).getBlock() instanceof PWRComponentBlock) {
							if (world.rand.nextInt(5) == 0)
								world.setBlockState(offs,ModBlocks.gas_radon.getDefaultState());
						}
					}
				}
			}
			world.createExplosion(null, centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, 8.0F, true);
		}

		protected void explodeLv2() {
			for (BlockPos member : members) {
				if (world.getBlockState(member).getBlock() instanceof PWRComponentBlock)
					world.setBlockState(member,ModBlocks.gas_radon_dense.getDefaultState());
			}
			world.newExplosion(null, centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, 24.0F, true, true);
		}

		protected IBlockState getBlockLv3(BlockPos pos, LeafiaMap<BlockPos, IBlockState> map, World world) {
			if (map.containsKey(pos))
				return map.get(pos);
			else
				return world.getBlockState(pos);
		}

		protected void explodeLv3() {
			double reactorSize = (maxX - minX + 1 + 2 + 4 + maxY - minY + 1 + 2 + 4 + maxZ - minZ + 1 + 2 + 4) / 3d;
			for (EntityPlayer plr : world.playerEntities) {
				if (plr.getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.wand_d) {
					plr.sendMessage(new TextComponentString("PWR Exploded"));
					plr.sendMessage(new TextComponentString("  Size: " + reactorSize));
					plr.sendMessage(new TextComponentString("  x: " + minX + " : " + maxX));
					plr.sendMessage(new TextComponentString("  y: " + minY + " : " + maxY));
					plr.sendMessage(new TextComponentString("  z: " + minZ + " : " + maxZ));
				}
			}
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(centerPoint)).grow(100));
			if (tankTypes[1].equals(Fluids.ULTRAHOTSTEAM))for (EntityPlayer player : players)
				AdvancementManager.grantAchievement(player,AddonAdvancements.nukebwr);
			else if (tankTypes[1].hasTrait(FT_Liquid.class))for (EntityPlayer player : players)
				AdvancementManager.grantAchievement(player,AddonAdvancements.nukepwr);

			// mmm this is gonna be crispy computer
			growMembers(growMembers(growMembers(growMembers(members))));

			// HashSet is one giant dick. It randomly doesn't detect its OWN element by contains(). You just wasted my precious time a LOT
			// Don't believe me? Try replacing all "motherfucker" occurrences to original "members" (which is a Set) and boom IT BREAKS SOMEHOW
			List<BlockPos> motherfucker = new ArrayList<>();
			motherfucker.addAll(members);
			//world.newExplosion(null,centerPoint.x+0.5,centerPoint.y+0.5,centerPoint.z+0.5,24*(toughness/15_000f),true,true);
			List<BlockPos> placeWrecks = new ArrayList<>();
			List<BlockPos> vaporized = new ArrayList<>();
			List<BlockPos> remains = new ArrayList<>();
			List<BlockPos> allDebris = new ArrayList<>();
			Vec3d pressure = new Vec3d(0, 0, 0);
			for (BlockPos member : motherfucker) {
				Vec3d ray = new Vec3d(member).add(0.5, 0.5, 0.5).subtract(centerPoint);
				if (!world.getBlockState(member).getMaterial().isSolid()) {
					pressure = pressure.add(ray.scale(2d / motherfucker.size()));
					continue;
				}
				IBlockState state = world.getBlockState(member);
				Block block = state.getBlock();
				if (block instanceof BlockFire) continue;
				if (block instanceof CoriumFinite) continue;
				if (block instanceof PWRElementBlock) {
					//world.newExplosion(null,member.getX()+0.5,member.getY()+0.5,member.getZ()+0.5,11,true,true);
					//world.setBlockState(member,ModBlocks.corium_block.getDefaultState());
					world.setBlockState(member,ModBlocks.gas_meltdown.getDefaultState());
					continue;
				}
				//Block fuckyou = Blocks.BLACK_GLAZED_TERRACOTTA;

				boolean destroyed = false;
				int counter = 0;
				int threshold = 7 + world.rand.nextInt(3);
				for (double s = 0; s <= 2; s += 0.2) {
					BlockPos rayHit = new BlockPos(centerPoint.add(ray.scale(Math.pow(s, 1.5) + 1))/*.add(ray.normalize().scale(1.732/1.5))*/);
					//member;//new BlockPos(centerPoint.add(ray.scale(s) ));
					if (!world.isValid(rayHit) || world.isAirBlock(rayHit) || motherfucker.contains(rayHit)) {
					/*
					switch (counter+2) {
						case 0: fuckyou = Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA; break;
						case 1: fuckyou = Blocks.BLUE_GLAZED_TERRACOTTA; break;
						case 2: fuckyou = Blocks.GREEN_GLAZED_TERRACOTTA; break;
						case 3: fuckyou = Blocks.LIME_GLAZED_TERRACOTTA; break;
						case 4: fuckyou = Blocks.YELLOW_GLAZED_TERRACOTTA; break;
						case 5: fuckyou = Blocks.ORANGE_GLAZED_TERRACOTTA; break;
						case 6: fuckyou = Blocks.RED_GLAZED_TERRACOTTA; break;
					}*/
						if (++counter >= threshold) {
							//world.setBlockToAir(member);
							vaporized.add(member);
							destroyed = true;
							pressure = pressure.add(ray.scale(1d / motherfucker.size()));
							break;
						}
					}
				}
				//if (motherfucker.contains(member)) { The line that proves the fact HashSet sometimes doesn't detect its own elements
				// This looks like always "true" if branch, except it is not for HashSet. Bruh.
				//	fuckyou = Blocks.LIME_GLAZED_TERRACOTTA;
				//}
				if (!destroyed) {
					// myaaaaa
					if (LeafiaUtil.isSolidVisibleCube(world.getBlockState(member)) || block instanceof PWRComponentBlock)
						remains.add(member);
					pressure = pressure.subtract(ray.scale(1d / motherfucker.size()));
				/*
				if (block instanceof PWRComponentBlock) {
					world.setBlockState(member,((world.rand.nextInt(3) == 0) ? ModBlocks.pribris_burning : ModBlocks.pribris).getDefaultState());
				} else if (block.isFullBlock(state) && !block.isPassable(world,member)) {
					// almost broken
					if (counter >= threshold-2) {
						//world.setBlockState(member,ModBlocks.pribris_radiating.getDefaultState()); // Test
						placeWrecks.add(member);
					}
				}*/
					//world.setBlockState(member,fuckyou.getDefaultState());
				}
			}
			LeafiaMap<BlockPos, IBlockState> placeMap = new LeafiaMap<>();
			LeafiaSet<BlockPos> antiPlaceSet = new LeafiaSet<>();
			List<PWRDebrisEntity> entitiesToSpawn = new ArrayList<>();
			for (BlockPos pos : vaporized) {
				if (placeWrecks.contains(pos)) continue; // Somehow
				boolean converted = false;
				for (EnumFacing face : EnumFacing.values()) {
					if (remains.contains(pos.offset(face))) {
						if (LeafiaUtil.isSolidVisibleCube(world.getBlockState(pos)) || world.getBlockState(pos).getBlock() instanceof PWRComponentBlock) {
							placeWrecks.add(pos);
							allDebris.add(pos);
							placeMap.put(pos, world.getBlockState(pos));
							converted = true;
						}
						break;
					}
				}
				if (!converted) {
					Block block = world.getBlockState(pos).getBlock();
					if (!(block instanceof IFluidBlock) && LeafiaUtil.isSolidVisibleCube(world.getBlockState(pos))) {
						if (world.getBlockState(pos).getBlockHardness(world, pos) >= 1) {
							Vec3d ray = new Vec3d(pos).add(0.5, 0.5, 0.5).subtract(centerPoint);
							PWRDebrisEntity debris = new PWRDebrisEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5, pos.getZ() + 0.5D, world.getBlockState(pos));
							debris.motionX = signedPow(ray.x, 1) / reactorSize * (1 + world.rand.nextDouble() * 4) + signedPow(pressure.x, 0.8) / 2;
							debris.motionY = signedPow(ray.y, 1) / reactorSize * (1 + world.rand.nextDouble() * 4) + signedPow(pressure.y, 0.8) / 2;
							debris.motionZ = signedPow(ray.z, 1) / reactorSize * (1 + world.rand.nextDouble() * 4) + signedPow(pressure.z, 0.8) / 2;
							entitiesToSpawn.add(debris);
						}
					}
					world.setBlockState(pos,ModBlocks.gas_radon_dense.getDefaultState());
				}
			}
			// TODO: add cam shake

			for (BlockPos pos : remains) {
				placeMap.put(pos, world.getBlockState(pos));
				world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());
			}
			ExplosionNT nt = new ExplosionNT(world, null, centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, 24 * (toughness / 15_000f));
			nt.addAttrib(ExAttrib.FIRE);
			nt.addAttrib(ExAttrib.NODROP);
			nt.overrideResolution(32);
			nt.explode();
			double shakeIntensity = toughness / 10_000d;
			PacketThreading.createSendToAllTrackingThreadedPacket(
					new CommandLeaf.ShakecamPacket(new String[]{
							"type=smooth",
							"preset=PWR_NEAR",
							"bloomDulling*" + (0.8125 - Math.pow(shakeIntensity, 0.5) / 20) / 0.75,
							"duration*" + Math.pow(shakeIntensity, 0.5),
							"range=" + reactorSize * 4
					}).setPos(new BlockPos(centerPoint)),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, reactorSize * 4.25)
			);
			PacketThreading.createSendToAllTrackingThreadedPacket(
					new CommandLeaf.ShakecamPacket(new String[]{
							"type=smooth",
							"preset=PWR_FAR",
							"duration*" + Math.pow(shakeIntensity, 0.5),
							"range=" + reactorSize * 9
					}).setPos(new BlockPos(centerPoint)),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(), centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, reactorSize * 9.25)
			);

			for (BlockPos pos : remains) {
				boolean buried = true;
				for (EnumFacing face : EnumFacing.values()) {
					if (vaporized.contains(pos.offset(face)) || !LeafiaUtil.isSolidVisibleCube(getBlockLv3(pos.offset(face), placeMap, world))) {
						buried = false;
						break;
					}
				}
				if (!buried)
					allDebris.add(pos);
			}
			for (BlockPos member : allDebris) {
				IBlockState state = getBlockLv3(member, placeMap, world);
				Block block = state.getBlock();
				Vec3d ray = new Vec3d(member).add(0.5, 0.5, 0.5).subtract(centerPoint);

				if (block instanceof PWRControlBlock) {
					world.setBlockState(member, ModBlocks.block_electrical_scrap.getDefaultState());
					antiPlaceSet.add(member);
					state = ModBlocks.block_electrical_scrap.getDefaultState();
					block = state.getBlock();
					//continue;
				}
				SoundType soundType = block.getSoundType();
				Material material = block.getMaterial(state);
				double heatBase = MathHelper.clamp(Math.pow(MathHelper.clamp(1 - ray.length() / (reactorSize / 2), 0, 1), 0.45) * 8, 0, 7);
				int heat = (int) heatBase;
				int heatRand = world.rand.nextInt(3);
				if (heatRand == 1)
					heat = (int) Math.round(heatBase);
				else if (heatRand == 2)
					heat = (int) Math.ceil(heatBase);
				boolean noErosion = true;
				if (placeWrecks.contains(member)) {
					noErosion = false;
					EnumFacing face = EnumFacing.UP;
					double absX = Math.abs(ray.x);
					double absY = Math.abs(ray.y);
					double absZ = Math.abs(ray.z);
					if ((absX > absY) && (absX > absZ))
						face = (ray.x > 0) ? EnumFacing.WEST : EnumFacing.EAST;
					else if ((absY > absX) && (absY > absZ))
						face = (ray.y > 0) ? EnumFacing.DOWN : EnumFacing.UP;
					else if ((absZ > absX) && (absZ > absY))
						face = (ray.z > 0) ? EnumFacing.NORTH : EnumFacing.SOUTH;
					EnumFacing most = null;
					int spaces = -1;
					int reliableSurround = 0;
					boolean surrounded = false;
					for (int i = 0; i < 7; i++) {
						EnumFacing curFace = (i > 0) ? EnumFacing.values()[i - 1] : face;
						if (i > 0 && curFace.equals(face)) continue; // skip if duplicate
						if (!getBlockLv3(member.offset(curFace, -1), placeMap, world).isFullBlock()) continue;
						if (placeWrecks.contains(member.offset(curFace, -1))) continue;
						if (!getBlockLv3(member.offset(curFace), placeMap, world).getBlock().isPassable(world, member.offset(curFace)))
							continue;
						if (placeWrecks.contains(member.offset(curFace))) continue;
						int mySpaces = 0;
						EnumFacing[] sides = null;
						switch (curFace.getAxis()) {
							case X:
								sides = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH};
								break;
							case Y:
								sides = EnumFacing.HORIZONTALS;
								break;
							case Z:
								sides = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST};
								break;
						}
						if (sides == null) continue;
						int surround = 0;
						int reliable = 0;
						for (EnumFacing side : sides) {
							if (placeWrecks.contains(member.offset(side)))
								surround++;
							else if (getBlockLv3(member.offset(side), placeMap, world).isFullBlock()) {
								surround++;
								reliable++;
							}
							if (LeafiaUtil.isSolidVisibleCube(getBlockLv3(member.offset(curFace).offset(side), placeMap, world)))
								continue;
							if (placeWrecks.contains(member.offset(curFace).offset(side))) continue;
							mySpaces++;
						}
						if (mySpaces > spaces) {
							spaces = mySpaces;
							most = curFace;
							surrounded = surround >= 4;
							reliableSurround = reliable;
						}
					}
					if (most != null) {
						// TODO: make RUBBLE model
						// RUBBLE should be used when (most != face)
						// Actually use it when (!surrounded)
						// TODO: use SLIGHT if verysurrounded
						Erosion erosion = Erosion.NORMAL;
						if (reliableSurround >= 2) erosion = Erosion.SLIGHT;
						else if (!surrounded) erosion = Erosion.RUBBLE;
						if (material.equals(Material.IRON)) {
							if (soundType.equals(SoundType.STONE))
								AddonBlocks.PWR.wreck_stone.create(world, member, most, state, erosion, heat);
							else
								AddonBlocks.PWR.wreck_metal.create(world, member, most, state, erosion, heat);
							antiPlaceSet.add(member);
						} else
							noErosion = true;
					}
				}
				if (noErosion) {
					/*Block[] sellafieldLevels = new Block[]{
							ModBlocks.sellafield_slaked,
							ModBlocks.sellafield_0,
							ModBlocks.sellafield_1,
							ModBlocks.sellafield_2,
							ModBlocks.sellafield_3,
							ModBlocks.sellafield_4,
							ModBlocks.sellafield_core
					};*/
					if (heat > 0) {
						if (block instanceof BlockGrass)
							world.setBlockState(member, ModBlocks.waste_earth.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockGravel)
							world.setBlockState(member, LegacyBlocks.waste_gravel.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockDirt || block == Blocks.FARMLAND)
							world.setBlockState(member, LegacyBlocks.waste_dirt.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockSnow)
							world.setBlockState(member, LegacyBlocks.waste_snow.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockSnowBlock)
							world.setBlockState(member, LegacyBlocks.waste_snow_block.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockMycelium)
							world.setBlockState(member, ModBlocks.waste_mycelium.getStateFromMeta(Math.min(heat, 6)));
						//else if (block instanceof BlockRedSandstone)
						//	world.setBlockState(member, ModBlocks.waste_sandstone_red.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockSandStone)
							world.setBlockState(member, LegacyBlocks.waste_sandstone.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockHardenedClay || block instanceof BlockStainedHardenedClay)
							world.setBlockState(member, LegacyBlocks.waste_terracotta.getStateFromMeta(Math.min(heat, 6)));
						else if (block instanceof BlockSand) {
							BlockSand.EnumType meta = state.getValue(BlockSand.VARIANT);
							world.setBlockState(member, ((meta == BlockSand.EnumType.SAND) ? LegacyBlocks.waste_sand : LegacyBlocks.waste_sand_red).getStateFromMeta(Math.min(heat, 6)));
						} else {
							int level = -1;
							if (block == Blocks.COBBLESTONE || block == Blocks.STONE || block instanceof BlockStone)
								level = 0;
							else if (block == ModBlocks.sellafield_slaked) {
								level = block.getMetaFromState(state);
								/*for (int i = 1; i < sellafieldLevels.length; i++) {
									if (block == sellafieldLevels[i]) {
										level = i;
										break;
									}
								}*/
							}
							if ((level >= 0) && (heat >= level))
								world.setBlockState(member, ModBlocks.sellafield_slaked.getStateFromMeta(Math.min(heat, 6)));
							else
								AddonBlocks.PWR.wreck_stone.create(world, member, EnumFacing.UP, state, Erosion.NONE, heat);
						}
						antiPlaceSet.add(member);
					}
				}
			}
			for (Entry<BlockPos, IBlockState> entry : placeMap.entrySet()) {
				if (!antiPlaceSet.contains(entry.getKey()))
					world.setBlockState(entry.getKey(), entry.getValue());
			}
			for (PWRDebrisEntity debris : entitiesToSpawn)
				world.spawnEntity(debris);
			NBTTagCompound data = new NBTTagCompound();
			data.setString("type", "rbmkmush");
			data.setFloat("scale", (float) reactorSize);
			PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5), new NetworkRegistry.TargetPoint(world.provider.getDimension(), centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, 250));

			world.playSound(null, centerPoint.x + 0.5, centerPoint.y + 0.5, centerPoint.z + 0.5, HBMSoundHandler.rbmk_explosion, SoundCategory.BLOCKS, 50.0F, 1.0F);
		}
	}

	public void explode(World world, @Nullable ItemStack prevStack,@Nullable LeafiaRodItem doNuke,int forceLevel) {
		if (exploded) return;
		exploded = true;
		explodeWorld = world;
		if (members.size() <= 0) return;
		Vec3d centerPoint = new Vec3d(0, 0, 0);
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		for (BlockPos member : members) {
			minX = Math.min(minX, member.getX());
			minY = Math.min(minY, member.getY());
			minZ = Math.min(minZ, member.getZ());
			maxX = Math.max(maxX, member.getX());
			maxY = Math.max(maxY, member.getY());
			maxZ = Math.max(maxZ, member.getZ());
			centerPoint = centerPoint.add(new Vec3d(member.getX(), member.getY(), member.getZ()).scale(1d / members.size()));
			Block block = world.getBlockState(member).getBlock();
			if (block instanceof PWRComponentBlock) {
				if (block instanceof PWRElementBlock) {
					if (((PWRElementBlock) block).tileEntityShouldCreate(world, member)) {
						TileEntity entity = world.getTileEntity(member);
						if (entity != null) {
							if (entity instanceof PWRElementTE) {
								PWRElementTE element = (PWRElementTE) entity;
								if (element.inventory != null) {
									prevStack = LeafiaRodItem.comparePriority(element.inventory.getStackInSlot(0), prevStack);
									element.inventory.setStackInSlot(0,ItemStack.EMPTY);
								}
							}
						}
					}
				}
				//world.setBlockToAir(member);
			}
		}

		PWRExplosion boom = new PWRExplosion(world, centerPoint, minX, minY, minZ, maxX, maxY, maxZ);
		if (forceLevel == 0) {
			if (toughness >= 15_000)
				boom.explodeLv3();
			else if (toughness >= 10_000)
				boom.explodeLv2();
			else
				boom.explodeLv1();
		} else {
			if (forceLevel == 1)
				boom.explodeLv1();
			if (forceLevel == 2)
				boom.explodeLv2();
			if (forceLevel == 3)
				boom.explodeLv3();
		}

		boolean nope = true;
        /*
        if (prevStack != null) {
            if (prevStack.getItem() instanceof LeafiaRodItem) {
                nope = false;
                LeafiaRodItem rod = (LeafiaRodItem) (prevStack.getItem());
                rod.resetDetonate();
                rod.detonateRadius = 18;
                rod.detonateVisualsOnly = true;
                rod.detonate(world, new BlockPos(centerPoint));
            }
        }*/
		if (doNuke != null)
			doNuke.nuke(world,new BlockPos(centerPoint));
		ExplosionNukeGeneric.waste(world, (int) centerPoint.x, (int) centerPoint.y, (int) centerPoint.z, 35);
		ChunkRadiationManager.proxy.incrementRad(world, new BlockPos(centerPoint), 3000F, 4000F);

		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(centerPoint)).grow(100));

		if(MobConfig.enableElementals) {
			for(EntityPlayer player : players) {
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean("radMark", true);
			}
		}
	}

	public static LeafiaPacket addDataToPacket(LeafiaPacket packet, @Nullable PWRData self) {
		return packet.__write(31, (self != null) ? self.writeToNBT(new NBTTagCompound()) : false);
	}

	@Nullable
	public static PWRData tryLoadFromPacket(TileEntity entity, Object value) {
		if (value.equals(false)) return null;
		else if (value instanceof NBTTagCompound)
			return new PWRData(entity).readFromNBT((NBTTagCompound) value);
		else
			return null;
	}

	public void sendControlPositions() {
		LeafiaPacket._start(companion)
				.__write(29, writeControlPositions())
				.__write(28, masterControl)
				.__sendToAffectedClients();
	}

	@Override
	public String getPacketIdentifier() {
		throw new LeafiaDevFlaw("Method PWRData.getPacketIdentifier() is not supposed to be used! Spaghetti coding moment.");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onReceivePacketLocal(byte key, Object value) {
		if (key == 30) { // Tank packets
			if (!value.getClass().isArray()) {
				Minecraft.getMinecraft().player.sendMessage(new TextWarningLeafia("Malformed PWR tank packet! (Given value wasn't Array)"));
				return;
			}
			if (Array.getLength(value) != 11) {
				Minecraft.getMinecraft().player.sendMessage(new TextWarningLeafia("Malformed PWR tank packet! (Array length must be 5, got " + Array.getLength(value) + ")"));
				return;
			}
			int readIndex = 0;
			int prevCompression = compression;
			compression = (int) Array.get(value, readIndex++);
			if (prevCompression != compression)
				onUpdateCompression();
			for (int i = 0; i < 5; i++)
				tanks[i] = resizeTank(tanks[i],(int) Array.get(value, readIndex++));
			for (int i = 0; i < 5; i++)
				tanks[i].setFill((int) Array.get(value, readIndex++));
			// if we somehow got non-int values in the array, well... #ripbozo
		} else if (key == 29) { // Rod sync packets
			if (value instanceof NBTTagCompound) {
				readControlPositions((NBTTagCompound) value);
			}
		} else if (key == 31) {
			new PWRDiagnosis(companion.getWorld(), companion.getPos()).addPosition(companion.getPos());
		} else if (key == 28) { // Master rod sync
			masterControl = (double) value;
		} else if (key == 27) { // Server Rod sync
			if (value instanceof NBTTagCompound) {
				NBTTagList list = ((NBTTagCompound) value).getTagList("controls", ((NBTTagCompound) value).getId());
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound nbt = list.getCompoundTagAt(i);
					if (nbt.hasKey("x") && nbt.hasKey("y") && nbt.hasKey("z")) {
						BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
						if (controls.contains(pos)) {
							TileEntity entity = getWorld().getTileEntity(pos);
							if (entity instanceof PWRControlTE) {
								PWRControlTE control = (PWRControlTE) entity;
								control.readControlDataFromNBT(nbt);
							}
						}
					}
				}
			} else {
				Minecraft.getMinecraft().player.sendMessage(new TextWarningLeafia("Malformed PWR control rod packet! (Given value wasn't NBTTagList)"));
			}
		}
	}

	@Override
	public void onReceivePacketServer(byte key, Object value, EntityPlayer plr) {
		if (key == 30) { // control rods request
			if (value instanceof NBTTagCompound) {
				NBTTagCompound nbt = (NBTTagCompound) value;
				if (nbt.hasKey("name")) {
					controlDemand.put(nbt.getString("name"), nbt.getDouble("level"));
					manipulateRod(nbt.getString("name"));
				} else {
					masterControl = nbt.getDouble("level");
					manipulateRod(null);
				}
				sendControlPositions();
			}
		} else if (key == 29) {
			if (value instanceof Integer) {
				compression = Math.floorMod((int) value, 3);
				onUpdateCompression();
			}
		}
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		addDataToPacket(LeafiaPacket._start(companion), this).__sendToClient(plr);
	}

	public void manipulateRod(String name) {
		for (BlockPos pos : controls) {
			TileEntity entity = getWorld().getTileEntity(pos);
			if (entity instanceof PWRControlTE) {
				PWRControlTE control = (PWRControlTE) entity;
				double newTarget = 0;
				if (controlDemand.containsKey(control.name))
					newTarget = controlDemand.get(control.name) * masterControl;
				if (name != null) {
					if (!control.name.equals(name)) continue;
				}
				control.targetPosition = newTarget;
			}
		}
	}
}
