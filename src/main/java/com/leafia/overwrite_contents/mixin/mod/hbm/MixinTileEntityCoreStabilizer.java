package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.machine.ItemLens;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreStabilizer;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreStabilizer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityCoreStabilizer.class)
public abstract class MixinTileEntityCoreStabilizer extends TileEntityMachineBase implements ITickable, IMixinTileEntityCoreStabilizer {
    public MixinTileEntityCoreStabilizer(int scount) {
        super(scount);
    }

    @Shadow
    public long power;
    @Shadow
    public int watts;
    @Shadow
    public boolean isOn;

    @Unique
    public LensType lens = LensType.STANDARD;
    @Unique
    public boolean cl_hasLens = false;
    @Unique
    public TileEntityCore lastGetCore;
    @Unique
    public BlockPos targetPosition;

    @SideOnly(Side.CLIENT)
    @Override
    public void onReceivePacketLocal(byte key, Object value) {
        if (key == 0) {
            cl_hasLens = (int) value >= 0;
            if (cl_hasLens)
                this.lens = LensType.values()[(int) value];
        }
        if (key == 1)
            this.isOn = (boolean) value;
        //if (key == 2)
        //this.innerColor = (int)value;
        IMixinTileEntityCoreStabilizer.super.onReceivePacketLocal(key, value);
    }

    @Shadow
    protected abstract void updateConnections();

    /**
     * @author
     * @reason
     */
    @Override
    @Overwrite
    public void update() {
        if (!world.isRemote) {
            LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();

            this.updateStandardConnections(world, pos);

            watts = MathHelper.clamp(watts, 1, 100);
            long demand = (long) Math.pow(watts, 6);
            isOn = false;

            //beam = 0;

            ItemLens lens = null;
            if (inventory.getStackInSlot(0).getItem() instanceof ItemLens) {
                lens = (ItemLens) inventory.getStackInSlot(0).getItem();
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
            /*
            if (lens != null && power >= demand * lens.drainMod) {
                isOn = true;
                TileEntityCore core = getCore();
                if (core != null) {
                    //core.field += (int)(watts * lens.fieldMod);
                    core.stabilization += lens.fieldMod * (watts / 100d);
                    core.stabilizers++;
                    core.energyMod *= lens.energyMod;
                    this.power -= (long) (demand * lens.drainMod);

                    long dmg = ItemLens.getLensDamage(inventory.getStackInSlot(0));
                    dmg += watts;

                    if (dmg >= lens.maxDamage)
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    else
                        ItemLens.setLensDamage(inventory.getStackInSlot(0), dmg);
                }
            }
             */
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
}
