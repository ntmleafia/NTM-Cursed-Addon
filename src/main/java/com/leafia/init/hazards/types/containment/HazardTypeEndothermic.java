package com.leafia.init.hazards.types.containment;

import com.custom_hbm.contents.torex.LCETorex;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.util.I18nUtil;
import com.leafia.contents.effects.nuke.NukeLCA;
import com.leafia.contents.effects.nuke.NukeLCA.ExplosionType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class HazardTypeEndothermic extends HazardTypeContainmentBase {
	@Override
	public void addHazardInformation(EntityPlayer entityPlayer,List<String> list,double v,ItemStack itemStack,List<IHazardModifier> list1) {
		super.addHazardInformation(entityPlayer,list,v,itemStack,list1);
		list.addAll(Arrays.asList(I18nUtil.resolveKey("trait._hazarditem.containment.endo").split("\\$")));
	}
	@Override
	public void onDestroyed(DamageSource source,World world,Vec3d pos,double level) {
		if (source == DamageSource.OUT_OF_WORLD) return;
		LCETorex.statFacEndo(world,pos.x,pos.y,pos.z,(float)level);
		NukeLCA nuke = NukeLCA.statFac(world,(int)level,pos.x,pos.y,pos.z).setExplosionType(ExplosionType.ENDOTHERMIC);
		world.spawnEntity(nuke);
	}
}
