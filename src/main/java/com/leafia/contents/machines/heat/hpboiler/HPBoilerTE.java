package com.leafia.contents.machines.heat.hpboiler;

import com.google.common.math.IntMath;
import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.saveddata.TomSaveData;
import com.hbm.sound.AudioWrapper;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.leafia.contents.machines.heat.hpboiler.container.HPBoilerContainer;
import com.leafia.contents.machines.heat.hpboiler.container.HPBoilerGUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class HPBoilerTE extends TileEntityMachineBase implements ITickable, LeafiaPacketReceiver, IFluidStandardReceiverMK2, IFluidStandardSenderMK2, IGUIProvider {
	public static int maxHeat = 128_000_000;
	public static double diffusion = 0.1D;
	public int heat;
	private int lastHeat;

	AxisAlignedBB bb = null;
	private AudioWrapper audio;
	private int audioTime;

	public FluidTankNTM input;
	public FluidTankNTM output;
	public int compression = 4;
	public boolean isOn = false;

	int inAmt;
	int outAmt;
	double efficiency;
	public int heatReq;

	public HPBoilerTE() {
		super(5,true,false);
		setFluid(Fluids.WATER,compression);
	}
	public void setFluid(FluidType fluid,int compression) {
		BoilData dat = getBoiledFluid(fluid,compression);
		if (dat.outType != Fluids.NONE) {
			if (input == null || output == null || input.getTankType() != fluid || output.getTankType() != dat.outType) {
				input = new FluidTankNTM(fluid,64000*dat.inAmt);
				output = new FluidTankNTM(dat.outType,64000*dat.outAmt);
				this.compression = dat.compression;
				inAmt = dat.inAmt;
				outAmt = dat.outAmt;
				efficiency = dat.efficiency;
				heatReq = dat.heatReq;
			}
		}
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			isOn = false;
			input.loadTank(0,1,inventory);
			output.unloadTank(2,3,inventory);
			ItemStack stack = inventory.getStackInSlot(4);
			if (stack.getItem() instanceof IItemFluidIdentifier identifier)
				setFluid(identifier.getType(world,pos.getX(),pos.getY(),pos.getZ(),stack),compression);
			int light = this.world.getLightFor(EnumSkyBlock.SKY, pos);
			if (light > 7 && TomSaveData.forWorld(world).fire > 1e-5) {
				this.heat += (int) ((maxHeat - heat) * 0.000005D); // constantly heat up 0.0005% of the remaining heat buffer for
				// rampant but diminishing heating
			}
			tryPullHeat();
			tryConvert();
			for (DirPos conPos : getConPos()) {
				trySubscribe(input.getTankType(),world,conPos);
				tryProvide(output,world,conPos);
			}
			NBTTagCompound tankData = new NBTTagCompound();
			input.writeToNBT(tankData,"input");
			output.writeToNBT(tankData,"output");
			LeafiaPacket._start(this)
					.__write(0,tankData)
					.__write(1,compression)
					.__write(2,heat)
					.__write(3,isOn)
					.__write(4,heatReq)
					.__sendToAffectedClients();
		} else {
			if (this.isOn) audioTime = 20;

			if (audioTime > 0) {

				audioTime--;

				if (audio == null) {
					audio = createAudioLoop();
					audio.startSound();
				} else if (!audio.isPlaying()) {
					audio = rebootAudio(audio);
				}

				audio.updateVolume(getVolume(1F));
				audio.keepAlive();

			} else {

				if (audio != null) {
					audio.stopSound();
					audio = null;
				}
			}
		}
	}
	protected DirPos[] getConPos() {
		ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset).getRotation(ForgeDirection.UP);
		return new DirPos[] {
				new DirPos(pos.getX() + dir.offsetX * 2, pos.getY()+1, pos.getZ() + dir.offsetZ * 2, dir),
				new DirPos(pos.getX() - dir.offsetX * 2, pos.getY()+1, pos.getZ() - dir.offsetZ * 2, dir.getOpposite())
		};
	}
	public void tryConvert() {
		int inputOps = input.getFill() / inAmt;
		int outputOps = (output.getMaxFill() - output.getFill()) / outAmt;
		int heatOps = (int)(this.heat * efficiency) / heatReq;

		int ops = Math.min(inputOps, Math.min(outputOps, heatOps));

		input.setFill(input.getFill() - inAmt * ops);
		output.setFill(output.getFill() + outAmt * ops);
		this.heat -= heatReq * ops;

		if (ops > 0 && world.rand.nextInt(400) == 0) {
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5,
					HBMSoundHandler.boilerGroanSounds[world.rand.nextInt(3)], SoundCategory.BLOCKS, 0.5F, 1.0F);
		}

		if (ops > 0)
			this.isOn = true;
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new HPBoilerContainer(entityPlayer.inventory,this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new HPBoilerGUI(entityPlayer.inventory,this);
	}

	public static class BoilData {
		final FluidType outType;
		final int compression;
		final int inAmt;
		final int outAmt;
		final double efficiency;
		final int heatReq;
		public BoilData(FluidType outType,int comp,int inAmt,int outAmt,double efficiency,int heatReq) {
			this.outType = outType;
			this.compression = comp;
			this.inAmt = inAmt;
			this.outAmt = outAmt;
			this.efficiency = efficiency;
			this.heatReq = heatReq;
		}
	}

    public BoilData getBoiledFluid(FluidType fluid, int compression) {
        FluidType cur = fluid;
        FluidType outType = Fluids.NONE;
        int comp = 0;
        int inAmt = 0;
        int outAmt = 0;
        double efficiency = 1.0;
        int heatReq = 0;

        for (int i = 0; i < compression; i++) {
            FT_Heatable trait = cur.getTrait(FT_Heatable.class);
            if (trait == null) break;

            double stepEfficiency = trait.getEfficiency(HeatingType.BOILER);
            if (stepEfficiency <= 0.0) break;

            FT_Heatable.HeatingStep step = trait.getFirstStep();
            if (step == null || step.typeProduced == null) break;

            if (comp == 0) {
                inAmt = step.amountReq;
                outAmt = step.amountProduced;
                heatReq = step.heatReq;
            } else {
                int prevOut = outAmt;
                // mlbv: guava gcd is faster than hand-rolled one; use LongMath for long gcd.
                int g = IntMath.gcd(prevOut, step.amountReq);
                int prefixMul = step.amountReq / g; // how many times the existing fused cycles must run
                int stepMul = prevOut / g; // how many times this new step must run
                inAmt *= prefixMul;
                outAmt = step.amountProduced * stepMul;
                heatReq = heatReq * prefixMul + step.heatReq * stepMul;
            }

            comp++;
            efficiency *= stepEfficiency;
            outType = cur = step.typeProduced;
        }

        if (comp <= 0) {
            return new BoilData(outType, 0, 0, 0, 1, 0);
        }

	    heatReq = (int)(heatReq/Math.pow(1.5,comp-1)); // apply bonus

        // mlbv: this part shall be removed if the desired behavior is to simulate three chained boilers with each
        // using gcd internally
        int div = IntMath.gcd(IntMath.gcd(inAmt, outAmt), heatReq);
        if (div > 1) {
            inAmt /= div;
            outAmt /= div;
            heatReq /= div;
        }

        return new BoilData(outType, comp, inAmt, outAmt, 1, heatReq);
    }

	protected void tryPullHeat() {
		TileEntity con = world.getTileEntity(pos.down(1));
		if (con instanceof IHeatSource source) {
			int diff = source.getHeatStored()-this.heat;
			if (diff == 0)
				return;
			if (diff > 0) {
				diff = (int) Math.ceil(diff*diffusion);
				diff = Math.min(diff,maxHeat-this.heat);
				source.useUpHeat(diff);
				this.heat += diff;
				if (this.heat > maxHeat) this.heat = maxHeat;
				return;
			}
		}
		this.heat = Math.max(this.heat-Math.max(this.heat/1000,1),0);
	}
	@Override
	public AudioWrapper createAudioLoop() {
		return MainRegistry.proxy.getLoopedSound(HBMSoundHandler.boiler, SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 0.125F, 10F, 1.0F, 20);
	}
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (audio != null) {
			audio.stopSound();
			audio = null;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (audio != null) {
			audio.stopSound();
			audio = null;
		}
	}
	@Override
	public String getDefaultName() {
		return "tile.hp_boiler.name";
	}
	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{input};
	}
	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		return new FluidTankNTM[]{output};
	}
	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{input,output};
	}
	@Override
	public String getPacketIdentifier() {
		return "HP_BOILER";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0) {
			NBTTagCompound tag = (NBTTagCompound)value;
			input.readFromNBT(tag,"input");
			output.readFromNBT(tag,"output");
		} else if (key == 1)
			compression = (int)value;
		else if (key == 2)
			heat = (int)value;
		else if (key == 3)
			isOn = (boolean)value;
		else if (key == 4)
			heatReq = (int)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0)
			setFluid(input.getTankType(),(int)value);
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("fluid",input.getTankType().getName());
		compound.setInteger("compression",compression);
		input.writeToNBT(compound,"input");
		output.writeToNBT(compound,"output");
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		compression = compound.getInteger("compression");
		setFluid(Fluids.fromName(compound.getString("fluid")),compression);
		input.readFromNBT(compound,"input");
		output.readFromNBT(compound,"output");
	}
}
