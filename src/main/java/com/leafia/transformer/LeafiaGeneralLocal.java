package com.leafia.transformer;

import com.hbm.lib.RefStrings;
import com.hbm.util.I18nUtil;
import com.leafia.contents.worldgen.biomes.effects.HasAcidicRain;
import com.leafia.contents.worldgen.biomes.effects.ParticleCloudSmall;
import com.leafia.dev.optimization.diagnosis.RecordablePacket;
import com.llib.math.SIPfx;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeafiaGeneralLocal {
	public static void injectDebugInfoLeft(List<String> list) {
		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals("")) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			list.add(index,"LCE packet network: "+SIPfx.format("%01.2f",RecordablePacket.previousByteUsageSec,true).toLowerCase()+"bytes/sec");
			list.add(index+1,"("+SIPfx.format("%01.2f",RecordablePacket.previousByteUsageMin,true).toLowerCase()+"bytes/min, "+SIPfx.format("%01.2f",RecordablePacket.previousByteUsage,true).toLowerCase()+"bytes/tick)");
		}
	}
	public static final ResourceLocation acidRain = new ResourceLocation("leafia", "textures/acidicrain.png");
	public static boolean acidRainParticles(Entity entity,Biome biome,IBlockState state,BlockPos down,double rx,double rz,AxisAlignedBB bb) {
		double x = (double)down.getX()+rx;
		double y = (double)((float)down.getY()+0.1F)+bb.maxY;
		double z = (double)down.getZ()+rz;
		World world = entity.world;
		if (biome instanceof HasAcidicRain) {
			if (world.rand.nextInt(10) == 0) {
				int rand = world.rand.nextInt(2)+1;
				for (int i = 0; i < rand; i++)
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCloudSmall(world,x,y,z,0.25f));
			}
			if (world.rand.nextInt(900) == 0)
				world.playSound(Minecraft.getMinecraft().player,down,SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,SoundCategory.AMBIENT,0.1f,world.rand.nextFloat()*0.2f+0.6f);
			return false;
		}
		return true;
	}
	static Map<String,String> splash = new HashMap<>();
	static {
		//https://www.ibm.com/docs/en/wamt?topic=binaries-jvm-command-line-properties-options
		splash.put("en","I know where you live, %s.");
		splash.put("es","Sé dónde vives, %s.");
		splash.put("ja","お前の住処は知っている、%s。");
		splash.put("ru","Я знаю, где ты живёшь, %s.");
		splash.put("zh","我知道你的住处，%s。");
	}
	public static void injectWackySplashes(List<String> splashes) {
		splashes.add("Floppenheimer!");
		splashes.add("i should dip my balls in sulfuric acid");
		splashes.add("All answers are popbob!");
		splashes.add("None may enter The Orb!");
		splashes.add("Wacarb was here");
		splashes.add("SpongeBoy me Bob I am overdosing on keramine agagagagaga");
		String lang = System.getProperty("user.language");
		String spl = splash.get(lang);
		if (spl == null) spl = splash.get("en");
		splashes.add(TextFormatting.RED+spl.replace("%s",System.getProperty("user.name")));
		splashes.add("Nice toes, now hand them over.");
		splashes.add("I smell burnt toast!");
		splashes.add("There are bugs under your skin!");
		splashes.add("Fentanyl!");
		splashes.add("Don't do drugs!"); // for legal reasons :)
		splashes.add("Imagine being scared by splash texts!");
		splashes.add("Redditors aren't people!");
		splashes.add("Can someone tell me what corrosive fumes the people on Reddit are huffing so I can avoid those more effectively?");
		// VV lce shit
		//splashes.add("Extra information on F3 debug screen!");
		splashes.add("This addon sends string characters in just 5 bits!");
		splashes.add("Core game community nowadays is really toxic!");
	}
}
