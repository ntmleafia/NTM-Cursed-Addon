package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.machine.ItemCatalyst;
import com.leafia.overwrite_contents.other.LCEItemCatalyst;
import com.llib.math.LeafiaColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemCatalyst.class)
public class MixinItemCatalyst extends Item {
	@Shadow(remap = false) private float powerMod;
	@Shadow(remap = false) private float heatMod;
	@Shadow(remap = false) private float fuelMod;
	@Shadow(remap = false) private int color;

	/**
	 * @author ntmleafia
	 * @reason im too lazy to get rid of Absolute Energy Bonus with redirections
	 */
	@Overwrite
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.add("§6Melting Point: §8" +LCEItemCatalyst.meltingPoints.get(this)+ "°C");
		//tooltip.add("Absolute Energy Bonus: " + (powerAbs >= 0 ? "§a+" : "§c") + Library.getShortNumber(powerAbs) + "HE");
		tooltip.add("Energy Modifier:           " + (powerMod >= 1 ? "§a+" : "§c") + (Math.round(powerMod * 1000) * .10 - 100) + "%");
		tooltip.add("Heat Modifier:               " + (heatMod > 1 ? "§c+" : "§a") + (Math.round(heatMod * 1000) * .10 - 100) + "%");
		tooltip.add("Fuel Modifier:               " + (fuelMod > 1 ? "§c+" : "§a") + (Math.round(fuelMod * 1000) * .10 - 100) + "%");
		tooltip.add("");
		tooltip.add(TextFormatting.LIGHT_PURPLE + "Required to contain the deadly stellar core of DFC");
	}

	NBTTagCompound getTag(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		return compound;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true; //super.showDurabilityBar(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getTag(stack).getDouble("damage") / 100d; //super.getDurabilityForDisplay(stack);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		LeafiaColor fiac = new LeafiaColor(color);
		double damage = getTag(stack).getDouble("damage") / 100d;
		fiac = fiac.lerp(new LeafiaColor(0.1, 0.1, 0.1), damage);
		if (damage > 0.666 && Math.floorMod(System.currentTimeMillis(), 400) >= 200)
			fiac = new LeafiaColor(1, 0, 0);
		return fiac.toInARGB();
	}
}
