package com.leafia.dev.fluids;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Exists for sole purpose of eliminating issue of NBT erasure with NTMF-FF integration.
 * <p>Do NOT integrate NTMF and LF.
 * <p>P.S. What the fuck is this leftover garbage
 */
@Deprecated
public class FluidTankLeafia {
	FluidType type;
	FluidType filter;
	int fill;
	int maxFill;
	NBTTagCompound nbt;

	public FluidTankLeafia(int maxFill) {
		this.maxFill = maxFill;
	}

	public FluidTankLeafia setFilter(FluidType type) {
		filter = type;
		return this;
	}
	public FluidType getFilter() {
		return filter;
	}

	/**
	 * For manually setting fluids. For regular use, <tt>fill</tt> method is preferred.
	 * @param nbt the nbt
	 * @return itself
	 */
	public FluidTankLeafia setNBT(NBTTagCompound nbt) {
		this.nbt = nbt;
		return this;
	}
	/**
	 * For manually setting fluids. For regular use, <tt>fill</tt> method is preferred.
	 * @param type the fluid type
	 * @return itself
	 */
	public FluidTankLeafia setFluid(FluidType type) {
		this.type = type;
		return this;
	}
	/**
	 * For manually setting fluids. For regular use, <tt>fill</tt> method is preferred.
	 * @param fill the fill
	 * @return itself
	 */
	public FluidTankLeafia setFill(int fill) {
		this.fill = fill;
		return this;
	}

	public NBTTagCompound getNBT() {
		return nbt;
	}
	public FluidType getFluid() {
		return type;
	}
	public int getFill() {
		return fill;
	}

	public boolean isStackCompatible(FluidStackLeafia stack) {
		if (!filter.equals(Fluids.NONE))
			if (!stack.type.equals(filter)) return false;
		if (type.equals(Fluids.NONE)) return true;
		return areTagsCompatible(stack,this);
	}

	public FluidStackLeafia getStack() {
		return new FluidStackLeafia(type,fill,nbt);
	}
	public FluidTankLeafia setStack(FluidStackLeafia stack) {
		if (!filter.equals(Fluids.NONE))
			if (!stack.type.equals(filter)) return this;
		type = stack.type;
		fill = stack.amount;
		nbt = stack.nbt;
		return this;
	}

	public int fill(FluidStackLeafia stack,FluidTankLeafia srcTank,boolean doFill) {
		if (isStackCompatible(stack)) {
			int addAmt = Math.min(stack.amount,maxFill-fill);
			if (doFill) {
				fill += addAmt;
				transferTags(srcTank,this);
			}
			return addAmt;
		}
		return 0;
	}

	public FluidStackLeafia drain(int amount,boolean doDrain) {
		int drainAmt = Math.min(amount,fill);
		if (drainAmt == 0) return null;
		fill -= drainAmt;
		FluidStackLeafia stack = new FluidStackLeafia(type,drainAmt,nbt);
		if (fill <= 0) {
			type = Fluids.NONE;
			nbt = null;
		}
		return stack;
	}

	/**
	 * Should be called while calculating fluid distribution in networks to see if tank contents are compatible.
	 * @param sending the sending stack
	 * @param receiving the receiving tank
	 * @return true if compatible
	 */
	public boolean areTagsCompatible(FluidStackLeafia sending,FluidTankLeafia receiving) {
		NBTTagCompound sendingTag = sending.nbt;
		NBTTagCompound receivingTag = receiving.nbt;
		if (receivingTag == null) return true;
		if (receivingTag.equals(sendingTag)) return true;
		//if (sending.getTankType().equals(AddonFluids.FLUORIDE)) return true; for future; MSR
		return false;
	}

	/**
	 * Should be called whilst doing transferFluid.
	 * For example MSR would need this to calculate combined temperature and fuel mixture.
	 * @param sending the sending stack
	 * @param receiving the receiving tank
	 */
	public void transferTags(FluidTankLeafia sending,FluidTankLeafia receiving) {
		NBTTagCompound sendingTag = sending.nbt;
		NBTTagCompound receivingTag = receiving.nbt;
		if (receivingTag == null) {
			receivingTag = new NBTTagCompound();
			for (String s : sendingTag.getKeySet())
				receivingTag.setTag(s,sendingTag.getTag(s));
		}
		receiving.nbt = receivingTag;
	}
}
