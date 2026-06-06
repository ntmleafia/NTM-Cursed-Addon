package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.capability.HbmLivingProps;
import com.hbm.handler.HazmatRegistry;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.util.ContaminationUtil;
import com.leafia.settings.AddonConfig;
import com.leafia.unsorted.BullshitInDavenportIowa;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ContaminationUtil.class,remap = false)
public abstract class MixinContaminationUtil {
	@Shadow
	public static double getPlayerRads(EntityLivingBase entity) {
		return 0;
	}

	@Shadow
	public static String getPreffixFromRad(double rads) {
		return null;
	}
	/**
	 * @author ntmleafia
	 * @reason rad classification
	 */
	@Overwrite
	public static void printGeigerData(EntityPlayer player) {
		double rawRadMod = ContaminationUtil.calculateRadiationMod(player);
		double eRad = HbmLivingProps.getRadiation(player);
		double rads = ChunkRadiationManager.proxy.getRadiation(player.world, player.getPosition());
		double env = getPlayerRads(player);
		double res = (1.0 - rawRadMod) * 100.0;
		double resKoeff = HazmatRegistry.getResistance(player) * 100.0;
		double rec = env * rawRadMod;
		double ar;

		double radsD = rads;
		double eRadD = eRad;
		double envD = env;
		double recD = rec;

		String unit = AddonConfig.bullshitUnits ? "dyatlov" : "Sv";
		String unitRAD = AddonConfig.bullshitUnits ? "expie" : "RAD";
		String unitPS = AddonConfig.bullshitUnits ? "/stevejob" : "/s";
		if (!AddonConfig.enableHealthMod) {
			unit = unitRAD;
			if (AddonConfig.bullshitUnits) {
				radsD = BullshitInDavenportIowa.RADToEx(radsD);
				eRadD = BullshitInDavenportIowa.RADToEx(eRadD);
				envD = BullshitInDavenportIowa.RADToEx(envD);
				recD = BullshitInDavenportIowa.RADToEx(recD);
			}
		} else {
			eRadD /= 100;
			envD /= 100;
			recD /= 100;
			if (AddonConfig.bullshitUnits) {
				radsD = BullshitInDavenportIowa.RADToEx(radsD);
				eRadD = BullshitInDavenportIowa.SvToDy(eRadD);
				envD = BullshitInDavenportIowa.SvToDy(envD);
				recD = BullshitInDavenportIowa.SvToDy(recD);
			}
		}
		if (AddonConfig.bullshitUnits) {
			radsD = BullshitInDavenportIowa.PSToPSj(radsD);
			envD = BullshitInDavenportIowa.PSToPSj(envD);
			recD = BullshitInDavenportIowa.PSToPSj(recD);
		}
		String eRadS, radsS, envS, recS, resS, resKoeffS;
		ar = Math.abs(eRad);
		eRadS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", eRadD) : String.format("%.3f", eRadD);
		ar = Math.abs(rads);
		radsS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", radsD) : String.format("%.3f", radsD);
		ar = Math.abs(env);
		envS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", envD) : String.format("%.3f", envD);
		ar = Math.abs(rec);
		recS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", recD) : String.format("%.3f", recD);
		ar = Math.abs(res);
		resS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-6)) ? String.format("%.6e", res) : String.format("%.6f", res);
		ar = Math.abs(resKoeff);
		resKoeffS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-2)) ? String.format("%.2e", resKoeff) : String.format("%.2f", resKoeff);

		String chunkPrefix = getPreffixFromRad(rads);
		String envPrefix = getPreffixFromRad(env);
		String recPrefix = getPreffixFromRad(rec);
		String radPrefix = "";
		String resPrefix = "" + TextFormatting.WHITE;

		if (eRad < 200) radPrefix += TextFormatting.GREEN;
		else if (eRad < 400) radPrefix += TextFormatting.YELLOW;
		else if (eRad < 600) radPrefix += TextFormatting.GOLD;
		else if (eRad < 800) radPrefix += TextFormatting.RED;
		else if (eRad < 1000) radPrefix += TextFormatting.DARK_RED;
		else radPrefix += TextFormatting.DARK_GRAY;
		if (resKoeff > 0) resPrefix += TextFormatting.GREEN;

		//localization and server-side restrictions have turned this into a painful mess
		//a *functioning* painful mess, nonetheless
		//@formatter:off
		player.sendMessage(new TextComponentString("===== ☢ ")
				.appendSibling(new TextComponentTranslation("geiger.title"))
				.appendSibling(new TextComponentString(" ☢ ====="))
				.setStyle(new Style().setColor(TextFormatting.GOLD)));
		player.sendMessage(new TextComponentTranslation("geiger.chunkRad")
				.appendSibling(new TextComponentString(" " + chunkPrefix + radsS + " "+unitRAD+unitPS))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.envRad")
				.appendSibling(new TextComponentString(" " + envPrefix + envS + " "+unit+unitPS))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.recievedRad")
				.appendSibling(new TextComponentString(" " + recPrefix + recS + " "+unit+unitPS))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.playerRad")
				.appendSibling(new TextComponentString(" " + radPrefix + eRadS + " "+unit))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.playerRes")
				.appendSibling(new TextComponentString(" " + resPrefix + resS + "% (" + resKoeffS + ")"))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		//@formatter:on
	}
}
