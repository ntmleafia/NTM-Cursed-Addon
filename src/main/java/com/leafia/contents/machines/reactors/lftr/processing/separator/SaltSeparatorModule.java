package com.leafia.contents.machines.reactors.lftr.processing.separator;

import com.hbm.api.energymk2.IEnergyHandlerMK2;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.modules.machine.ModuleMachineBase;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.SaltSeparatorRecipe;
import com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.SaltSeparatorRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class SaltSeparatorModule extends ModuleMachineBase {
	FluidTank saltTank;
	public SaltSeparatorModule(int index,IEnergyHandlerMK2 battery,ItemStackHandler inventory) {
		super(index,battery,inventory);
		this.inputSlots = new int[0];
		this.outputSlots = new int[0];
		this.inputTanks = new FluidTankNTM[2];
		this.outputTanks = new FluidTankNTM[3];
	}
	@Override
	public SaltSeparatorRecipes getRecipeSet() {
		return SaltSeparatorRecipes.INSTANCE;
	}
	public SaltSeparatorModule fluidInput(FluidTankNTM a,FluidTankNTM b) { inputTanks[0] = a; inputTanks[1] = b; return this; }
	public SaltSeparatorModule fluidOutput(FluidTankNTM a, FluidTankNTM b, FluidTankNTM c) { outputTanks[0] = a; outputTanks[1] = b; outputTanks[2] = c; return this; }
	public SaltSeparatorModule saltTank(FluidTank tank) { saltTank = tank; return this; }

	@Override
	public SaltSeparatorRecipe getRecipe() {
		return this.getRecipeSet().recipeNameMap.get(this.recipe);
	}

	@Override
	public void update(double speed,double power,boolean extraCondition,ItemStack blueprint) {
		SaltSeparatorRecipe recipe = this.getRecipe();
		if (recipe != null && recipe.isPooled() && !recipe.isPartOfPool(ItemBlueprints.grabPool(blueprint))) {
			this.didProcess = false;
			this.progress = (double)0.0F;
			this.recipe = "null";
		} else {
			this.setupTanks(recipe);
			this.didProcess = false;
			this.markDirty = false;
			if (extraCondition && this.canProcess(recipe, speed, power)) {
				this.process(recipe, speed, power);
				this.didProcess = true;
			} else {
				this.progress = (double)0.0F;
			}

		}
	}

	public boolean isMixtureIncompatible(SaltSeparatorRecipe recipe,Map<String,Double> mixture) {
		boolean isIncompatible = false;
		for (Entry<MSRFuel,Double> entry : recipe.mixture.entrySet()) {
			if (!mixture.containsKey(entry.getKey().name())) {
				isIncompatible = true;
				break;
			}
			if (!Objects.equals(mixture.get(entry.getKey().name()),entry.getValue())) {
				isIncompatible = true;
				break;
			}
		}
		for (Entry<String,Double> entry : mixture.entrySet()) {
			try {
				if (!recipe.mixture.containsKey(MSRFuel.valueOf(entry.getKey()))) {
					isIncompatible = true;
					break;
				}
			} catch (IllegalArgumentException e) {
				isIncompatible = true;
				break;
			}
		}
		return isIncompatible;
	}

	public void setupTanks(SaltSeparatorRecipe recipe) {
		super.setupTanks(recipe);
		if (saltTank.getFluid() != null) {
			if (!AddonFluids.fromFF(saltTank.getFluid().getFluid()).equals(recipe.saltType)) {
				saltTank.drain(saltTank.getCapacity()*2,true);
				return;
			}
			NBTTagCompound tag = MSRElementTE.nbtProtocol(saltTank.getFluid().tag);
			Map<String,Double> mixture = MSRElementTE.readMixture(tag);
			saltTank.getFluid().tag = tag;
			/*boolean shouldEmpty = isMixtureIncompatible(recipe,mixture);
			if (shouldEmpty)
				saltTank.drain(saltTank.getCapacity()*2,true);*/
		}
	}

	public boolean canProcess(SaltSeparatorRecipe recipe,double speed,double power) {
		if (saltTank.getFluid() != null) {
			if (saltTank.getFluidAmount() < recipe.saltAmount) return false;
			NBTTagCompound tag = MSRElementTE.nbtProtocol(saltTank.getFluid().tag);
			Map<String,Double> mixture = MSRElementTE.readMixture(tag);
			if (isMixtureIncompatible(recipe,mixture)) return false;
			return super.canProcess(recipe,speed,power);
		}
		return false;
	}

	public void process(SaltSeparatorRecipe recipe,double speed,double power) {
		this.battery.setPower(this.battery.getPower() - (power == (double)1.0F ? recipe.power : (long)((double)recipe.power * power)));
		double step = Math.min(speed / (double)recipe.duration, (double)1.0F);
		this.progress += step;
		if (this.progress >= (double)1.0F) {
			this.consumeInput(recipe);
			this.produceItem(recipe);
			saltTank.drain(recipe.saltAmount,true);
			if (this.canProcess(recipe, speed, power)) {
				--this.progress;
			} else {
				this.progress = (double)0.0F;
			}
		}
	}
}
