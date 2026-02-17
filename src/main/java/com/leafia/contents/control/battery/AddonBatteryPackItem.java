package com.leafia.contents.control.battery;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBatteryPack;
import com.hbm.util.EnumUtil;
import com.leafia.contents.AddonItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

@Deprecated // suck my butt
public class AddonBatteryPackItem /*extends ItemBatteryPack*/ {
	/*
	public AddonBatteryPackItem(String s) {
		super(s);
		//theEnum = AddonEnumBatteryPack.class;
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}
	@Override
	public long getMaxCharge(ItemStack stack) {
		AddonEnumBatteryPack pack = EnumUtil.grabEnumSafely(AddonEnumBatteryPack.class, stack.getItemDamage());
		return pack.capacity;
	}
	@Override
	public long getChargeRate(ItemStack stack) {
		AddonEnumBatteryPack pack = EnumUtil.grabEnumSafely(AddonEnumBatteryPack.class, stack.getItemDamage());
		return pack.chargeRate;
	}
	@Override
	public long getDischargeRate(ItemStack stack) {
		AddonEnumBatteryPack pack = EnumUtil.grabEnumSafely(AddonEnumBatteryPack.class, stack.getItemDamage());
		return pack.dischargeRate;
	}

	@Override
	public void addInformation(ItemStack stack,World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		super.addInformation(stack,worldIn,tooltip,flagIn);
		tooltip.add("Don't say anything about these batteries.");
		tooltip.add("Everything about these are slop, I KNOW.");
	}

	public enum AddonEnumBatteryPack {
		BATTERY_DESH("battery_desh", 125_000L, false),
		BATTERY_EUPHEMIUM("battery_euphemium", 500_000L, false),
		BATTERY_SLOP("battery_slop", 833_333_333L, 20 * 60 * 10),
		BATTERY_SPK("battery_spk", 16_666_666_667L, 20 * 60 * 5),
		BATTERY_ELECTRO("battery_electro", 166_666_666_667L, 20 * 60 * 5); // this is insanity
/*
		BATTERY_REDSTONE("battery_redstone", 100L, false),
		BATTERY_LEAD("battery_lead", 1_000L, false),
		BATTERY_LITHIUM("battery_lithium", 10_000L, false),
		BATTERY_SODIUM("battery_sodium", 50_000L, false),
		BATTERY_SCHRABIDIUM("battery_schrabidium", 250_000L, false),
		BATTERY_QUANTUM("battery_quantum", 1_000_000L, 20 * 60 * 60);*/
/*
		public final ResourceLocation texture;
		public final long capacity;
		public final long chargeRate;
		public final long dischargeRate;
		public static final AddonEnumBatteryPack[] VALUES = values();

		AddonEnumBatteryPack(String tex,long dischargeRate,boolean capacitor) {
			this(tex,
					capacitor ? (dischargeRate * 20 * 30) : (dischargeRate * 20 * 60 * 15),
					capacitor ? dischargeRate : dischargeRate * 10,
					dischargeRate);
		}

		AddonEnumBatteryPack(String tex,long dischargeRate,long duration) {
			this(tex, dischargeRate * duration, dischargeRate * 10, dischargeRate);
		}

		AddonEnumBatteryPack(String tex,long capacity,long chargeRate,long dischargeRate) {
			this.texture = new ResourceLocation("leafia", "textures/models/batteries/" + tex + ".png");
			this.capacity = capacity;
			this.chargeRate = chargeRate;
			this.dischargeRate = dischargeRate;
		}

		public boolean isCapacitor() {
			return this.ordinal() > BATTERY_ELECTRO.ordinal(); // impossible case for now
		}

		public ItemStack stack() {
			return new ItemStack(AddonItems.addon_battery_pack, 1, this.ordinal());
		}
	}
	/*@Override
	public String getResourceLocationAsString() {
		return ((ItemBatteryPack)ModItems.battery_pack).getResourceLocationAsString();
	}*/
}
