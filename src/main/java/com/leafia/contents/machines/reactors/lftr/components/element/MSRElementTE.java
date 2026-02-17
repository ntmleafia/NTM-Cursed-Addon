package com.leafia.contents.machines.reactors.lftr.components.element;

import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.inventory.OreDictManager;
import com.hbm.util.Tuple.Pair;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryBlock;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryTE;
import com.leafia.contents.machines.reactors.pwr.blocks.PWRReflectorBlock;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.passive.LeafiaPassiveServer;
import com.llib.group.LeafiaMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class MSRElementTE extends MSRTEBase {
	public enum MSRByproduct {
		th232(
				1,
				new Pair<>("pa233",1d)
		),
		pa233(
				1,
				new Pair<>("pa234",1d)
		),
		pa233_decay(
				1,
				new Pair<>("u233",1d)
		),
		u233(
				1,
				new Pair<>("u235",1d)
		);
		final public double division;
		final public Pair<String,Double>[] byproducts;
		MSRByproduct(double division,Pair<String,Double>... byproducts) {
			this.division = division;
			this.byproducts = byproducts;
		}
	}
	public enum MSRFuel {
		th232(
				new Item[]{},
				new String[]{OreDictManager.TH232.nugget()},
				"x^0.3/B",
				(x)->Math.pow(x,0.3),
				0.3,
				MSRByproduct.th232,
				30000000d
		),
		pa233(
				new Item[]{},
				new String[]{},
				"x^0.3/B",
				(x)->Math.pow(x,0.3),
				1,
				MSRByproduct.pa233,
				1000000d,
				0.0003,
				MSRByproduct.pa233_decay
		),
		u233(
				new Item[]{},
				new String[]{OreDictManager.U233.nugget()},
				"x^0.85/B",
				(x)->Math.pow(x,0.85),
				1,
				MSRByproduct.u233,
				600000000d
		),
		u235(
				new Item[]{},
				new String[]{OreDictManager.U235.nugget()},
				"x^0.75/B",
				(x)->Math.pow(x,0.75),
				1
		),
		pa234(
				new Item[]{},
				new String[]{},
				"x^0.15/B",
				(x)->Math.pow(x,0.15),
				1
		);
		final public Item[] items;
		final public String[] dicts;
		final public String funcString;
		final public Function<Double,Double> function;
		final public double decayRate;
		final public MSRByproduct decayProduct;
		final public double heatPerFlux;
		final public MSRByproduct byproduct;
		final double life;
		MSRFuel(Item[] items,String[] dicts,String funcString,Function<Double,Double> function,double heatPerFlux) {
			this.items = items;
			this.dicts = dicts;
			this.funcString = funcString;
			this.function = function;
			this.heatPerFlux = heatPerFlux;
			byproduct = null;
			this.decayRate = 0;
			this.decayProduct = null;
			this.life = 0;
		}
		MSRFuel(Item[] items,String[] dicts,String funcString,Function<Double,Double> function,double heatPerFlux,MSRByproduct byproduct,double life) {
			this.items = items;
			this.dicts = dicts;
			this.funcString = funcString;
			this.function = function;
			this.byproduct = byproduct;
			this.heatPerFlux = heatPerFlux;
			this.decayRate = 0;
			this.decayProduct = null;
			this.life = life;
		}
		MSRFuel(Item[] items,String[] dicts,String funcString,Function<Double,Double> function,double heatPerFlux,double decayRate,MSRByproduct decayProduct) {
			this.items = items;
			this.dicts = dicts;
			this.funcString = funcString;
			this.function = function;
			this.heatPerFlux = heatPerFlux;
			this.decayRate = decayRate;
			this.decayProduct = decayProduct;
			byproduct = null;
			this.life = 0;
		}
		MSRFuel(Item[] items,String[] dicts,String funcString,Function<Double,Double> function,double heatPerFlux,MSRByproduct byproduct,double life,double decayRate,MSRByproduct decayProduct) {
			this.items = items;
			this.dicts = dicts;
			this.funcString = funcString;
			this.function = function;
			this.byproduct = byproduct;
			this.heatPerFlux = heatPerFlux;
			this.decayRate = decayRate;
			this.decayProduct = decayProduct;
			this.life = life;
		}
	}
	void decay() {
		double B = tank.getFluidAmount()/1000d;
		FluidStack stack = tank.getFluid();
		if (stack != null) {
			NBTTagCompound nbt = nbtProtocol(stack.tag);
			Map<String,Double> mixture = readMixture(nbt);
			for (Entry<String,Double> entry : mixture.entrySet()) {
				// entry.getValue() doesn't work because the mixture changes concurrently
				double mix = mixture.get(entry.getKey());
				if (mix <= 0) continue;
				try {
					MSRFuel type = MSRFuel.valueOf(entry.getKey());
					//if (entry.getKey().equals("u233")) {
					//LeafiaDebug.debugLog(world,"U233: "+tempAdd);
					//}
					if (type.decayProduct != null) {
						double addAmt = type.decayRate*mix*B*0.05;
						;
						for (Pair<String,Double> byproduct : type.decayProduct.byproducts)
							addMixture(mixture,byproduct.getKey(),/*mix* what was my peanut brain cooking while i was coding ts*/addAmt/type.byproduct.division);
						double perc = mix-addAmt;
						mixture.put(entry.getKey(),perc);
						if (perc <= 0)
							mixture.remove(entry.getKey());
					}
				} catch (IllegalArgumentException ignored) {
				}
			}
			nbt.setTag("itemMixture",writeMixture(mixture));
		}
	}

	Block getBlockArbitrary(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof MSRArbitraryBlock) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof MSRArbitraryTE arbitrary) {
				if (arbitrary.inventory.getStackInSlot(0).getItem() instanceof ItemBlock b)
					block = b.getBlock();
			}
		}
		return block;
	}

	BlockPos toBlockPos(FiaMatrix mat) {
		return new BlockPos(mat.position);
	}

	FiaMatrix toFiaMatrix(BlockPos pos) {
		return new FiaMatrix(new Vec3d(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5));
	}

	boolean isModerator(Block block) {
		return ("_"+block.getRegistryName().getPath()+"_").matches(".*[^a-z]graphite[^a-z].*");
	}

	boolean isRadResistant(Block block) {
		return block instanceof IRadResistantBlock;
	}

	MSRElementTE getReactionTarget(BlockPos pos) {
		Block block = getBlockArbitrary(pos);
		if (block instanceof PWRReflectorBlock)
			return this;
		if (world.getTileEntity(pos) instanceof MSRElementTE te)
			return te;
		return null;
	}

	void addMixture(Map<String,Double> mixture,String fuelType,double amt) {
		if (mixture.containsKey(fuelType))
			mixture.put(fuelType,mixture.get(fuelType)+amt);
		else
			mixture.put(fuelType,amt);
	}
	void react(MSRElementTE te,double distance,double multiplier) {
		double B = te.tank.getFluidAmount()/2000d+tank.getFluidAmount()/2000d;
		FluidStack stack0 = tank.getFluid();
		FluidStack stack1 = te.tank.getFluid();
		if (stack0 != null) {
			double y = 0;
			NBTTagCompound nbt = nbtProtocol(stack0.tag);
			double curRestriction = Math.max(restriction,te.restriction);
			multiplier *= (1-curRestriction);
			multiplier /= distance/2;
			if (stack1 != null) {
				Map<String,Double> mixture = readMixture(nbt);
				for (Entry<String,Double> entry : mixture.entrySet()) {
					// entry.getValue() doesn't work because the mixture changes concurrently
					double mix = mixture.get(entry.getKey());
					if (mix <= 0) continue;
					try {
						MSRFuel type = MSRFuel.valueOf(entry.getKey());
						double tempAdd = type.function.apply(nbtProtocol(stack1.tag).getDouble("heat")+getBaseTemperature(AddonFluids.fromFF(stack0.getFluid())))*mix*B*multiplier;
						y += tempAdd;
						if (type.byproduct != null) {
							double addAmt = tempAdd/type.life;
							for (Pair<String,Double> byproduct : type.byproduct.byproducts)
								addMixture(mixture,byproduct.getKey(),addAmt/type.byproduct.division);
							double perc = mix-addAmt;
							mixture.put(entry.getKey(),perc);
							if (perc <= 0)
								mixture.remove(entry.getKey());
						}
					} catch (IllegalArgumentException ignored) {}
				}
				//curRestriction = Math.max(curRestriction,te.restriction);
				nbt.setTag("itemMixture",writeMixture(mixture));
			}
			double heat = nbt.getDouble("heat");
			double heatMg = y-heat;
			heat += Math.pow(Math.abs(heatMg),0.2)*Math.signum(heatMg);
			nbt.setDouble("heat",heat);
			stack0.tag = nbt;
			double rad = Math.pow(heatMg,0.65)/2;
			//ChunkRadiationManager.proxy.incrementRad(world,pos,(float)rad/8,(float)rad);
		}
	}

	void reactCorners2D(FiaMatrix mat,Map<BlockPos,Block> blocks) {
		Tracker._startProfile(this,"reactCorners2D");
		for (int x = -1; x <= 2; x+=2) {
			for (int y = -1; y <= 2; y+=2) {
				BlockPos a = toBlockPos(mat.translate(x,0,0));
				BlockPos b = toBlockPos(mat.translate(0,y,0));
				BlockPos c = toBlockPos(mat.translate(x,y,0));
				Block blockA = blocks.getOrDefault(a,Blocks.AIR);
				Block blockB = blocks.getOrDefault(b,Blocks.AIR);
				boolean moderatedA = isModerator(blockA);
				boolean moderatedB = isModerator(blockB);
				boolean resistantA = isRadResistant(blockA);
				boolean resistantB = isRadResistant(blockA);
				if (!resistantA && !resistantB) {
					MSRElementTE te = getReactionTarget(c);
					if (te != null)
						react(te,2,(moderatedA || moderatedB) ? 2 : 0.5);
				}
				Tracker._tracePosition(this,c,moderatedA,moderatedB);
			}
		}
		Tracker._endProfile(this);
	}

	void reactCorners() {
		Tracker._startProfile(this,"reactCorners");
		Map<BlockPos,Block> mop = new LeafiaMap<>();
		for (EnumFacing value : EnumFacing.values()) {
			BlockPos p = pos.offset(value);
			mop.put(p,getBlockArbitrary(p));
		}
		FiaMatrix mat = toFiaMatrix(pos);
		reactCorners2D(mat,mop);
		reactCorners2D(mat.rotateY(90),mop);
		reactCorners2D(mat.rotateX(90),mop);
		Tracker._endProfile(this);
	}

	void reactLine(EnumFacing facing) {
		Tracker._startProfile(this,"reactLine");
		boolean moderated = false;
		for (int i = 1; i <= 4; i++) {
			BlockPos p = pos.offset(facing,i);
			Tracker._tracePosition(this,p,"moderated: "+moderated);
			Block block = getBlockArbitrary(p);
			if (isModerator(block)) moderated = true;
			MSRElementTE te = getReactionTarget(p);
			if (te != null) {
				react(te,i,moderated ? 2 : 0.5);
				return;
			}
			if (isRadResistant(block)) return;
		}
		Tracker._endProfile(this);
	}

	@Override
	public String getPacketIdentifier() {
		return "MSRElement";
	}
	public BlockPos control = null;
	public double restriction = 0;

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			/*if (control != null && control.isInvalid()) {
				control = null;
				restriction = 0;
			}*/
			decay();
			reactCorners();
			for (EnumFacing face : EnumFacing.values())
				reactLine(face);
			LeafiaPacket._start(this).__write(0,restriction).__sendToAffectedClients();
			markChanged();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		restriction = compound.getDouble("restriction");
		if (compound.hasKey("controlX")) {
			control = new BlockPos(
					compound.getInteger("controlX"),
					compound.getInteger("controlY"),
					compound.getInteger("controlZ")
			);;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("restriction",restriction);
		if (control != null) {
			compound.setInteger("controlX",control.getX());
			compound.setInteger("controlY",control.getY());
			compound.setInteger("controlZ",control.getZ());
		} else {
			compound.removeTag("controlX");
			compound.removeTag("controlY");
			compound.removeTag("controlZ");
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			restriction = (double)value;
	}
}
