package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.DataValue;
import com.hbm.inventory.control_panel.DataValueFloat;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemLens;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreStabilizer;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreStabilizer;
import com.leafia.settings.AddonConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                this.lens = LensType.VALUES[(int) value];
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

            this.updateConnections();

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

            if (lens != null && power >= demand * lens.drainMod) {
                isOn = true;
                TileEntityCore core = getCore();
                if (core != null) {
                    IMixinTileEntityCore mixinTileEntityCore = (IMixinTileEntityCore) core;
                    //core.field += (int)(watts * lens.fieldMod);
                    mixinTileEntityCore.setDFCStabilization(mixinTileEntityCore.getDFCStabilization() + lens.fieldMod * (watts / 100d));
                    mixinTileEntityCore.setDFCStabilizers(mixinTileEntityCore.getDFCStabilizers() + 1);
                    mixinTileEntityCore.setDFCEnergyMod(mixinTileEntityCore.getDFCEnergyMod() * getLensEnergyMod(lens));
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
    private float getLensEnergyMod(ItemLens lens) {
        if (lens == ModItems.ams_focus_blank || lens == ModItems.ams_lens)
            return 1.0f;
        if (lens == ModItems.ams_focus_limiter)
            return 0.5f;
        if (lens == ModItems.ams_focus_booster)
            return 1.35f;
        if (lens == ModItems.ams_focus_omega)
            return 3.5f;
        throw new IllegalArgumentException("Unknown lens type: " + lens);
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
