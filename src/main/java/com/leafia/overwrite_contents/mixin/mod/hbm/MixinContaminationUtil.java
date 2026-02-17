package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.capability.HbmLivingProps;
import com.hbm.handler.HazmatRegistry;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.util.ContaminationUtil;
import com.leafia.settings.AddonConfig;
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
		double division = 100;
		String unit = "Sv";
		if (!AddonConfig.enableHealthMod) {
			division = 1;
			unit = "RAD";
		}
		String eRadS, radsS, envS, recS, resS, resKoeffS;
		ar = Math.abs(eRad);
		eRadS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", eRad/division) : String.format("%.3f", eRad/division);
		ar = Math.abs(rads);
		radsS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", rads) : String.format("%.3f", rads);
		ar = Math.abs(env);
		envS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", env/division) : String.format("%.3f", env/division);
		ar = Math.abs(rec);
		recS = (ar >= 1.0e6 || (ar > 0.0 && ar < 1.0e-3)) ? String.format("%.3e", rec/division) : String.format("%.3f", rec/division);
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
				.appendSibling(new TextComponentString(" " + chunkPrefix + radsS + " RAD/s"))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.envRad")
				.appendSibling(new TextComponentString(" " + envPrefix + envS + " "+unit+"/s"))
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		player.sendMessage(new TextComponentTranslation("geiger.recievedRad")
				.appendSibling(new TextComponentString(" " + recPrefix + recS + " "+unit+"/s"))
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
