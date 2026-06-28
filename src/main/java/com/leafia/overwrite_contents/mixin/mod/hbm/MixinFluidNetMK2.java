package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.FluidNetMK2;
import com.hbm.api.fluidmk2.FluidNode;
import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Amat;
import com.hbm.uninos.NodeNet;
import com.hbm.util.Tuple;
import com.hbm.util.Tuple.ObjectLongPair;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = FluidNetMK2.class,remap = false)
public abstract class MixinFluidNetMK2 extends NodeNet<IFluidReceiverMK2, IFluidProviderMK2,FluidNode, FluidNetMK2> {
	@Shadow protected long currentTime;
	@Shadow protected static int timeout;
	@Shadow protected FluidType type;
	@Shadow public List<ObjectLongPair<IFluidProviderMK2>>[] providers;
	@Shadow public long[] fluidAvailable;

	@Unique
	private boolean isConnectedToAnywhere(IFluidProviderMK2 provider) {
		ObjectIterator<Entry<IFluidReceiverMK2>> iterator = receiverEntries.object2LongEntrySet().fastIterator();
		while (iterator.hasNext()) {
			Object2LongMap.Entry<IFluidReceiverMK2> entry = iterator.next();
			if (entry.getKey() != provider)
				return true;
		}
		return false;
	}

	@Unique
	public boolean checkExplode(IFluidProviderMK2 provider) {
		if (!type.hasTrait(FT_Amat.class)) return false;
		if (!isConnectedToAnywhere(provider)) return false;
		if (provider instanceof TileEntity te) {
			te.getWorld().newExplosion(null,te.getPos().getX(),te.getPos().getY(),te.getPos().getZ(),5,true,true);
			return true;
		}
		return false;
	}

	/**
	 * @author ntmleafia
	 * @reason not enough budget to inject
	 */
	@Overwrite
	public void setupFluidProviders() {
		ObjectIterator<Entry<IFluidProviderMK2>> iterator = providerEntries.object2LongEntrySet().fastIterator();

		while(iterator.hasNext()) {
			Object2LongMap.Entry<IFluidProviderMK2> entry = iterator.next();
			if(currentTime - entry.getLongValue() > timeout || isBadLink(entry.getKey()) || checkExplode(entry.getKey())) { iterator.remove(); continue; }
			IFluidProviderMK2 provider = entry.getKey();
			int[] pressureRange = provider.getProvidingPressureRange(type);
			for(int p = pressureRange[0]; p <= pressureRange[1]; p++) {
				long available = Math.min(provider.getFluidAvailable(type, p), provider.getProviderSpeed(type, p));
				providers[p].add(new Tuple.ObjectLongPair<>(provider, available));
				fluidAvailable[p] += available;
			}
		}
	}
}
