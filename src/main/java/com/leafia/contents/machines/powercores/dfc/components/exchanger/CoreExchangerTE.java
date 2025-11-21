package com.leafia.contents.machines.powercores.dfc.components.exchanger;

import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.api.fluid.IFluidStandardSender;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.util.Tuple.Quartet;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.settings.AddonConfig;
import com.llib.group.LeafiaSet;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoreExchangerTE extends LCETileEntityMachineBase implements IDFCBase, ITickable, IGUIProvider, IFluidStandardSender, IFluidStandardReceiver {
	public FluidTankNTM input = new FluidTankNTM(Fluids.COOLANT,2560_000);
	public FluidTankNTM output = new FluidTankNTM(Fluids.COOLANT_HOT,2560_000);
	protected BlockPos targetPosition = new BlockPos(0,0,0);
	int inAmt = 1;
	int outAmt = 1;

    private static Quartet<Integer, FluidType, Integer, Integer> getBoiledFluid(FluidType f, int compression) {
        int comp = 0;
        FluidType cur = f;
        long in = 0;
        long out = 0;

        for (int i = 0; i < compression; i++) {
            FT_Heatable trait = cur.getTrait(FT_Heatable.class);
            if (trait == null) break;

            FT_Heatable.HeatingStep step = trait.getFirstStep();
            if (step == null || step.typeProduced == null) break;
            long reqPrev  = step.amountReq;
            long prodCur  = step.amountProduced;
            if (reqPrev <= 0L || prodCur <= 0L) break;

            if (comp == 0) {
                in = reqPrev;
                out = prodCur;
            } else {
                long l = lcm(in, prodCur);
                long scaleExisting = l / in;
                long batches       = l / prodCur;
                in  = batches * reqPrev;
                out = out * scaleExisting;
            }

            cur = step.typeProduced;
            comp++;
        }

        int inI  = in  > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) in;
        int outI = out > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) out;
        return new Quartet<>(comp, cur, inI, outI);
    }

    private static long lcm(long a, long b) {
        if (a == 0L || b == 0L) return 0L;
        long g = gcd(a, b);
        return (a / g) * b;
    }

    private static long gcd(long a, long b) {
        while (b != 0L) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

	public int compression = 1;
	public int amountToHeat = 1;
	public int tickDelay = 1;
	public void setInput(FluidType f,int compression) {
		Quartet<Integer,FluidType,Integer,Integer> quartlet = getBoiledFluid(f,compression);
		if (quartlet.getW() > 0) {
			if (input.getTankType() != f)
				input.setFill(0);
			if (output.getTankType() != f)
				output.setFill(0);
			this.compression = quartlet.getW();
			input.setTankType(f);
			output.setTankType(quartlet.getX());
			inAmt = quartlet.getY();
			outAmt = quartlet.getZ();
		}
	}
	@Override
	public void slotContentsChanged(int slot,ItemStack newStack) {
		super.slotContentsChanged(slot,newStack);
		if (newStack.getItem() instanceof IItemFluidIdentifier identifier)
			setInput(identifier.getType(world,getPos().getX(),getPos().getY(),getPos().getZ(),newStack),compression);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeTargetPos(compound);
		input.writeToNBT(compound,"tankI");
		output.writeToNBT(compound,"tankO");
		compound.setInteger("amount",amountToHeat);
		compound.setInteger("delay",tickDelay);
		compound.setByte("compression",(byte)compression);
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		readTargetPos(compound);
		super.readFromNBT(compound);
		if (compound.hasKey("amount"))
			amountToHeat = compound.getInteger("amount");
		if (compound.hasKey("delay"))
			tickDelay = compound.getInteger("delay");
		if (compound.hasKey("compression"))
			compression = compound.getByte("compression");
		if (compound.hasKey("tankI")) {
			input.readFromNBT(compound,"tankI");
			output.readFromNBT(compound,"tankO");
			setInput(input.getTankType(),compression);
		}
	}

	public CoreExchangerTE() {
		super(1);
	}

	int timer = 0;

    @Override
    public void update() {
        TileEntityCore core = getCore(AddonConfig.dfcComponentRange);
        if (!world.isRemote) {
            LeafiaPacket._start(this).__write(31, targetPosition).__sendToAffectedClients();
            timer++;

            if (timer >= tickDelay) {
                timer = 0;
	            this.subscribeToAllAround(input.getTankType(), this);

                FluidType inType  = input.getTankType();
                FluidType outType = output.getTankType();

                int heatAmt = outType.temperature - inType.temperature;
                if (core != null && heatAmt > 0 && inAmt > 0) {
                    IMixinTileEntityCore mixin = (IMixinTileEntityCore) core;
                    double mbPerCelsius = 1000.0;
                    double difference = mixin.getDFCTemperature() - outType.temperature;
                    if (difference > 0) {
                        int maxDrain = (int) (difference / heatAmt * mbPerCelsius);
                        int drain = Math.min(maxDrain, amountToHeat);
                        drain = (drain / inAmt) * inAmt;
                        if (drain > 0 && maxDrain > 0) {
                            int availableIn = input.getFill();
                            if (availableIn >= drain && inType != Fluids.NONE) {
                                int batches = drain / inAmt;
                                int fill = batches * outAmt;
                                int canFill = output.fill(outType, fill, false);
                                if (canFill == fill) {
                                    input.setFill(availableIn - drain);
                                    output.fill(outType, fill, true);
                                    //mlbv: why did you /20 here but not in maxDrain calculation? probably a bug, removed.
	                                //ntmleafia: because if i divide maxDrain by 20 it just rounds down to 0 and doesn't work well
                                    mixin.setDFCTemperature(Math.max(mixin.getDFCTemperature() - drain * heatAmt / mbPerCelsius /20, 0.0));
                                }
                            }
                        }
                    }
                }
            }

            sendFluidToAll(output, this);
            LeafiaPacket._start(this)
                        .__write(0, input.getTankType().getName())
                        .__write(1, output.getTankType().getName())
                        .__write(2, input.getFill())
                        .__write(3, output.getFill())
                        .__write(4, compression)
                        .__write(5, amountToHeat)
                        .__write(6, tickDelay)
                        .__sendToListeners();
        }
    }


	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		IDFCBase.super.onReceivePacketServer(key,value,plr);
		switch(key) {
			case 0: amountToHeat = Math.max((int)value,1); break;
			case 1: tickDelay = Math.max((int)value,1); break;
			case 2: setInput(input.getTankType(),(int)value); break;
		}
	}
	public TileEntityCore lastGetCore = null;

	@Override
	public TileEntityCore lastGetCore() {
		return lastGetCore;
	}

	@Override
	public void lastGetCore(TileEntityCore core) {
		lastGetCore = core;
	}

	@Override
	public BlockPos getTargetPosition() {
		return targetPosition;
	}

	@Override
	public void targetPosition(BlockPos pos) {
		targetPosition = pos;
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		IDFCBase.super.onReceivePacketLocal(key,value);
		switch(key) {
			case 0: input.setTankType(Fluids.fromName((String)value)); break;
			case 1: output.setTankType(Fluids.fromName((String)value)); break;
			case 2: input.setFill((int)value); break;
			case 3: output.setFill((int)value); break;
			case 4: compression = (int)value; break;
			case 5: amountToHeat = (int)value; break;
			case 6: tickDelay = (int)value; break;
		}
	}

	public LeafiaSet<EntityPlayer> listeners = new LeafiaSet<>();
	@Override
	public List<EntityPlayer> getListeners() {
		return listeners;
	}

	@Override
	public String getName() {
		return "tile.dfc_exchanger.name";
	}

	@Override
	public String getDefaultName() {
		return "tile.dfc_exchanger.name";
	}

	@Override
	public String getPacketIdentifier() {
		return "DFC_EXCHANGER";
	}

	@Override
	public Container provideContainer(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new CoreExchangerContainer(player,this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return new CoreExchangerGUI(player,this);
	}

	@Override
	public FluidTankNTM[] getSendingTanks() {
		return new FluidTankNTM[]{output};
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		return new FluidTankNTM[]{input,output};
	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[]{input};
	}
}
