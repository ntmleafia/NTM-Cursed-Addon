package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Gaseous;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Liquid;
import com.hbm.modules.machine.ModuleMachineChemplant;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityMachineChemicalPlant;
import com.hbm.util.ParticleUtil;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.firestorm.IFirestormTE;
import com.leafia.settings.AddonConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(value = TileEntityMachineChemicalPlant.class)
public abstract class MixinTileEntityMachineChemicalPlant extends TileEntityMachineBase implements IFirestormTE {
	@Shadow(remap = false)
	public abstract FluidTankNTM[] getAllTanks();

	@Unique boolean onFire = false;
	@Unique boolean destroyed = false;
	public MixinTileEntityMachineChemicalPlant(int scount) {
		super(scount);
	}

	@Override
	public void catchFire() {
		LeafiaDebug.debugLog(world,"catchFire");
		for (FluidTankNTM tank : getAllTanks()) {
			if (tank.getTankType().hasTrait(FT_Flammable.class) || tank.getTankType().hasTrait(FT_Combustible.class)) {
				LeafiaDebug.debugLog(world,"Flammable");
				if (tank.getFill() > 0) {
					int chance = 200;
					if (tank.getTankType().hasTrait(FT_Gaseous.class))
						chance = 15;
					boolean willSetOnFire = false;
					if (tank.getTankType().hasTrait(FT_Liquid.class))
						willSetOnFire = true;
					LeafiaDebug.debugLog(world,"Chance: "+chance+", willSetOnFire: "+willSetOnFire);
					if (world.rand.nextInt(chance) == 0) {
						destroyed = true;
						onFire = willSetOnFire;
						world.createExplosion(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,5,false);
						break;
					}
				}
			}
		}
	}
	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Inject(method = "serialize",at = @At(value = "TAIL"),remap = false,require = 1)
	public void onSerialize(ByteBuf buf,CallbackInfo ci) {
		buf.writeBoolean(onFire);
		buf.writeBoolean(destroyed);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),remap = false,require = 1)
	public void onDeserialize(ByteBuf buf,CallbackInfo ci) {
		onFire = buf.readBoolean();
		destroyed = buf.readBoolean();
	}
	@Inject(method = "readFromNBT",at = @At(value = "TAIL"),require = 1)
	public void onReadFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		if (AddonConfig.enableFirestorm) {
			destroyed = nbt.getBoolean("firestorm_destroyed");
			onFire = nbt.getBoolean("firestorm_onFire");
		}
	}
	@Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
	public void onWriteToNBT(NBTTagCompound nbt,CallbackInfoReturnable<NBTTagCompound> cir) {
		nbt.setBoolean("firestorm_destroyed",destroyed);
		nbt.setBoolean("firestorm_onFire",onFire);
	}
	@Redirect(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/modules/machine/ModuleMachineChemplant;update(DDZLnet/minecraft/item/ItemStack;)V",remap = false),require = 1)
	public void onUpdate(ModuleMachineChemplant instance,double speed,double pow,boolean extraCondition,ItemStack blueprint) {
		if (!destroyed)
			instance.update(speed,pow,extraCondition,blueprint);
		else {
			instance.markDirty = false;
			instance.didProcess = false;
			instance.progress = 0;
			if (onFire) {
				Random rand = world.rand;
				ParticleUtil.spawnGasFlame(world,pos.getX()+rand.nextDouble(),pos.getY()+0.5+rand.nextDouble(),pos.getZ()+rand.nextDouble(),rand.nextGaussian()*0.2,0.1,rand.nextGaussian()*0.2);
				List<Entity> affected = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - 1.5, pos.getY(), pos.getZ() - 1.5, pos.getX() + 2.5, pos.getY() + 5, pos.getZ() + 2.5));
				for(Entity e : affected) e.setFire(5);
			}
		}
	}
}
