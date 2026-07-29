package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileEntityCoreReceiver.class, remap = false)
public abstract class MixinTileEntityCoreReceiverOC extends TileEntity implements IMixinTileEntityCoreReceiver, SimpleComponent {
	@Shadow(remap = false) public long power;
	@Shadow(remap = false) public FluidTankNTM tank;

	@Callback
	public Object[] incomingEnergy(Context context,Arguments args) {
		return new Object[]{leafia$syncJoules()};
	}

	@Callback
	public Object[] outgoingPower(Context context, Arguments args) {
		return new Object[]{power};
	}

	@Callback
	public Object[] storedCoolant(Context context, Arguments args) {
		return new Object[]{tank.getFill()};
	}

	@Callback
	public Object[] getStress(Context context, Arguments args) {
		return new Object[]{leafia$destructionLevel()*100/300f};
	}

	@Callback(doc = "setLevel(newLevel: number [0~100])->(previousLevel: number)")
	public Object[] setLevel(Context context, Arguments args) {
		double level = args.checkDouble(0);
		level = MathHelper.clamp(level,0,100);
		double prevLevel = level*100;
		leafia$setLevel(level/100d);
		return new Object[]{prevLevel*100};
	}

	@Callback(doc = "getLevel()->(level: number [0-100])")
	public Object[] getLevel(Context context, Arguments args) {
		return new Object[]{leafia$getLevel()};
	}
}
