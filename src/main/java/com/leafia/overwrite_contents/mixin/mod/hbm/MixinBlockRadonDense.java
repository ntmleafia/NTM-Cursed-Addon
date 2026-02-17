package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.gas.BlockGasBase;
import com.hbm.blocks.gas.BlockGasRadonDense;
import com.hbm.config.GeneralConfig;
import com.hbm.handler.ArmorUtil;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockGasRadonDense.class)
public abstract class MixinBlockRadonDense extends BlockGasBase {
	public MixinBlockRadonDense(float r,float g,float b,String s) {
		super(r,g,b,s);
	}
	/**
	 * @author ntmleafia
	 * @reason radiation protection
	 */
	@Overwrite
	public void onEntityCollision(World world,BlockPos pos,IBlockState state,Entity entity) {
		if (!(entity instanceof EntityLivingBase entityLiving) || !GeneralConfig.enableRadon)
			return;

		if (ArmorRegistry.hasProtection(entityLiving, EntityEquipmentSlot.HEAD, HazardClass.PARTICLE_FINE)) {
			ArmorUtil.damageGasMaskFilter(entityLiving, 2);
			//ContaminationUtil.contaminate(entityLiving, HazardType.RADIATION, ContaminationType.CREATIVE, 0.5F);
		} else {
			ContaminationUtil.contaminate(entityLiving, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 0.5F);
		}
	}
}
