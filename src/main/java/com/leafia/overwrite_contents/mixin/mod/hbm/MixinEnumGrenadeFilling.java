// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityCloudSolinium;
import com.hbm.entity.grenade.EntityGrenadeUniversal;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.items.weapon.grenade.ItemGrenadeFilling.EnumGrenadeFilling;
import com.hbm.items.weapon.grenade.ItemGrenadeShell.EnumGrenadeShell;
import com.leafia.contents.miscellanous.slop.SlopTE;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.function.Consumer;

@Mixin(value = EnumGrenadeFilling.class,remap = false)
public class MixinEnumGrenadeFilling {
	@Shadow @Final @Mutable public static EnumGrenadeFilling[] VALUES;
	@Shadow @Final @Mutable private static EnumGrenadeFilling[] $VALUES;
	@Invoker("<init>")
	private static EnumGrenadeFilling leafia$constructor(String enumName,int enumOrdinal,Consumer<EntityGrenadeUniversal> explode,int bodyColor,int labelColor,EnumGrenadeShell... compatibleShells) {
		throw new AssertionError();
	}
	@Unique private static EnumGrenadeFilling leafia$NULL;
	@Unique private static EnumGrenadeFilling leafia$SOL;
	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/hbm/items/weapon/grenade/ItemGrenadeFilling$EnumGrenadeFilling;values()[Lcom/hbm/items/weapon/grenade/ItemGrenadeFilling$EnumGrenadeFilling;", shift = At.Shift.BEFORE))
	private static void leafia$extendEnum(CallbackInfo ci) {
		int base = $VALUES.length;
		var if_null = leafia$constructor("NULL", base, (grenade)->{
			BlockPos pos = new BlockPos(grenade.posX,grenade.posY,grenade.posZ);
			for (int x = -3; x <= 3; x++) {
				for (int y = -3; y <= 3; y++) {
					for (int z = -3; z <= 3; z++)
						grenade.world.setBlockState(pos.add(x,y,z),Blocks.AIR.getDefaultState(),18);
				}
			}
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX()-3,pos.getY()-3,pos.getZ()-3,pos.getX()+4,pos.getY()+4,pos.getZ()+4);
			for (Entity entity : grenade.world.getEntitiesWithinAABBExcludingEntity(null,aabb))
				SlopTE.tryKill(entity);
		},0xc2c2c2,0x383838,EnumGrenadeShell.FRAG);
		var sol = leafia$constructor("SOL", base+1, (grenade)->{
			World world = grenade.world;
			world.playSound(null, grenade.posX, grenade.posY, grenade.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 10.0f, world.rand.nextFloat() * 0.1F + 0.9F);

			EntityNukeExplosionMK3 entity = new EntityNukeExplosionMK3(world);
			entity.posX = grenade.posX;
			entity.posY = grenade.posY;
			entity.posZ = grenade.posZ;
			entity.destructionRange = 25;
			entity.speed = BombConfig.blastSpeed;
			entity.coefficient = 1.0F;
			entity.waste = false;
			entity.extType = 1;

			world.spawnEntity(entity);

			EntityCloudSolinium cloud = new EntityCloudSolinium(world, 25);
			cloud.posX = grenade.posX;
			cloud.posY = grenade.posY;
			cloud.posZ = grenade.posZ;
			world.spawnEntity(cloud);
		},0x114eaf,0x000000,EnumGrenadeShell.NUKE);
		var ext = Arrays.copyOf($VALUES, base + 2);
		ext[base] = if_null;
		ext[base+1] = sol;
		$VALUES = VALUES = ext;
		leafia$NULL = if_null;
		leafia$SOL = sol;
	}
	@Inject(method = "valueOf", at = @At("HEAD"), cancellable = true)
	private static void leafia$valueOf(String name, CallbackInfoReturnable<EnumGrenadeFilling> cir) {
		switch (name) {
			case "NULL":
				cir.setReturnValue(leafia$NULL);
				return;
			case "SOL":
				cir.setReturnValue(leafia$SOL);
				return;
			default:
		}
	}
}
