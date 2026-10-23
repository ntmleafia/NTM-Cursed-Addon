// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.ntmfbottle;

import com.google.common.collect.ImmutableMap;
import com.hbm.capability.HbmLivingProps;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Corrosive;
import com.hbm.inventory.fluid.trait.FT_Polluting;
import com.hbm.inventory.fluid.trait.FT_Toxin;
import com.hbm.inventory.fluid.trait.FT_Toxin.ToxinEffects;
import com.hbm.inventory.fluid.trait.FT_Toxin.ToxinEntry;
import com.hbm.inventory.fluid.trait.FT_VentRadiation;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Amat;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Delicious;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Gaseous;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.FT_Viscous;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.items.itembase.AddonItemBaked;
import com.leafia.init.LeafiaDamageSource;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemNTMFBottle extends AddonItemBaked {
	private final ResourceLocation baseTextureLocation = new ResourceLocation("minecraft","items/potion_bottle_drinkable");
	protected Map<Integer,ModelResourceLocation> models = new HashMap<>();
	public ItemNTMFBottle(String s) {
		super(s);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		/*
		FluidType[] order = Fluids.getInNiceOrder();
		for (FluidType fluidType : order) {
			if (isValidFluid(fluidType))
				ModelLoader.setCustomModelResourceLocation(this, fluidType.getID(), getModelLocation(fluidType.getID()));
		}*/
	}
	public ModelResourceLocation getModelLocation(int meta) {
		if (!models.containsKey(meta))
			models.put(meta,new ModelResourceLocation(new ResourceLocation("leafia","ntmfbottle_"+meta), "inventory"));
		return models.get(meta);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void bakeModel(ModelBakeEvent event) {
		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft", "item/generated"));

			FluidType[] order = Fluids.getInNiceOrder();
			for (FluidType fluidType : order) {
				if (isValidFluid(fluidType)) {
					ImmutableMap.Builder<String,String> textures = ImmutableMap.builder();
					textures.put("layer0",this.baseTextureLocation.toString());
					textures.put("layer1","leafia:items/"+this.getRegistryName()+"_overlay_"+fluidType.getID());

					IModel retexturedModel = baseModel.retexture(textures.build());
					IBakedModel bakedModel = retexturedModel.bake(ModelRotation.X0_Y0,DefaultVertexFormats.ITEM,ModelLoader.defaultTextureGetter());
					event.getModelRegistry().putObject(getModelLocation(fluidType.getID()),bakedModel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static FluidType getFluidFromStack(ItemStack stack) {
		return Fluids.fromID(stack.getItemDamage());
	}

	public static boolean isValidFluid(FluidType fluid) {
		if (fluid.equals(Fluids.NONE)) return false; // what's the point?
		if (fluid.equals(Fluids.WATER)) return false; // already exists
		if (fluid.equals(Fluids.MERCURY)) return false; // already exists
		if (fluid.hasTrait(FT_Amat.class)) return false;
		return !fluid.hasTrait(FT_Gaseous.class);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}
	public int getMaxItemUseDuration(ItemStack stack) {
		if (getFluidFromStack(stack).hasTrait(FT_Viscous.class))
			return 96;
		return 32;
	}
	public ActionResult<ItemStack> onItemRightClick(World worldIn,EntityPlayer playerIn,EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	public ItemStack onItemUseFinish(ItemStack stack,World worldIn,EntityLivingBase entity) {
		EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		if (!worldIn.isRemote) {
			FluidType fluid = getFluidFromStack(stack);
			if (fluid.temperature > 60) {
				// BURNING
				float damage = (fluid.temperature-60)/400f*20;// maybe it was too weak
				entity.attackEntityFrom(LeafiaDamageSource.drinkhot,damage);
				if (fluid.temperature > 300) {
					float duration = (fluid.temperature-300)/50f+3;
					entity.setFire((int)duration);
				}
			}
			else if (fluid.temperature < -20) {
				// FREEZING
				float damage = -(fluid.temperature+20)/200f*20;
				entity.attackEntityFrom(LeafiaDamageSource.drinkcryo,damage);
			}
			{
				// EFFECTS
				if (fluid.hasTrait(FT_Toxin.class)) {
					FT_Toxin toxin = fluid.getTrait(FT_Toxin.class);
					for (ToxinEntry entry : toxin.entries) {
						if (entry instanceof ToxinEffects eff) {
							for(PotionEffect effect : eff.effects)
								entity.addPotionEffect(new PotionEffect(effect.getPotion(),effect.getDuration(),effect.getAmplifier()));
						}
					}
				}
			}
			if (fluid.hasTrait(FT_Corrosive.class)) {
				// ACID
				entity.attackEntityFrom(LeafiaDamageSource.drinkacid,fluid.getTrait(FT_Corrosive.class).getRating()/3f);
			}
			if (fluid.hasTrait(FT_VentRadiation.class)) {
				// RADIATION
				ContaminationUtil.contaminate(entity,HazardType.RADIATION,ContaminationType.CREATIVE,fluid.getTrait(FT_VentRadiation.class).getRadPerMB()*333);
			}
			if (player != null) {
				// HUNGER
				int fill = 2;
				if (fluid.hasTrait(FT_Delicious.class))
					fill = 5;
				player.getFoodStats().addStats(fill,fill);
			}
			{
				// POISONING
				float damage = fluid.poison*5;
				if (fluid.hasTrait(FT_Polluting.class)) {
					float poisoning = 0;
					float coal = 0;
					float asbestos = 0;
					FT_Polluting pollution = fluid.getTrait(FT_Polluting.class);
					for (Entry<PollutionType,Float> entry : pollution.releaseMap.entrySet()) {
						float value = entry.getValue()*1000;
						switch(entry.getKey()) {
							case POISON:
								poisoning += value*50;
								break;
							case SOOT:
								poisoning += value*10;
								coal += value*20;
								break;
							case FALLOUT:
								poisoning += value*20;
								asbestos += value*20;
								break;
							case HEAVYMETAL:
								poisoning += value*70;
								break;
						}
						LeafiaDebug.debugLog(entity.world,entry.getKey().name+": "+value);
					}
					if (coal >= 1)
						HbmLivingProps.incrementBlackLung(entity,(int)coal);
					if (asbestos >= 1)
						HbmLivingProps.incrementBlackLung(entity,(int)coal);
					damage = Math.max(damage,poisoning);
				}
				damage *= 1.5f; // i felt like it was too weak
				boolean isFatal = entity.getHealth() <= damage/1.5;
				if (damage >= 12.5 && !isFatal) {
					damage /= 1.5f;
					int ix = MathHelper.floor(entity.posX);
					int iy = MathHelper.floor(entity.posY);
					int iz = MathHelper.floor(entity.posZ);
					final int[] counter = {0};
					Runnable vomit = new Runnable() {
						@Override
						public void run() {
							if (counter[0] < 10) {
								NBTTagCompound nbt = new NBTTagCompound();
								nbt.setString("type", "vomit");
								nbt.setString("mode", "normal");
								nbt.setInteger("count", 15);
								nbt.setInteger("entity", entity.getEntityId());
								PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(nbt, 0, 0, 0),  new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 25));
								LeafiaPassiveServer.queueFunction(this);
							}
							counter[0]++;
						}
					};
					LeafiaPassiveServer.queueFunction(vomit);

					worldIn.playSound(null, ix, iy, iz, HBMSoundHandler.vomit, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60, 19));
				}
				if (damage >= 0.05) {
					entity.attackEntityFrom(LeafiaDamageSource.poison,damage);
					entity.addPotionEffect(new PotionEffect(MobEffects.POISON,(int)(damage*20),(int)Math.max(0,damage/5-1)));
				}
			}
		}
		if (player != null) {
			if (!player.isCreative()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}
		return stack;
	}

	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		getFluidFromStack(stack).addInfoItemTanks(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18nUtil.resolveKey("item.ntmfbottle.name",getFluidFromStack(stack).getLocalizedName());
	}

	@Override
	public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			FluidType[] order = Fluids.getInNiceOrder();
			for (FluidType fluidType : order) {
				if (isValidFluid(fluidType))
					items.add(new ItemStack(this,1,fluidType.getID()));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel() {
		FluidType[] order = Fluids.getInNiceOrder();
		for (FluidType fluidType : order) {
			if (isValidFluid(fluidType))
				ModelLoader.setCustomModelResourceLocation(this, fluidType.getID(), getModelLocation(fluidType.getID()));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprite(TextureMap map) {
		map.registerSprite(this.baseTextureLocation);
		FluidType[] order = Fluids.getInNiceOrder();
		for (FluidType fluidType : order) {
			if (isValidFluid(fluidType)) {
				ResourceLocation spriteLoc = new ResourceLocation("leafia","items/"+this.getRegistryName()+"_overlay_"+fluidType.getID());
				TextureAtlasSpriteMask maskedSpr = new TextureAtlasSpriteMask(
						spriteLoc.toString(),
						new ResourceLocation("minecraft","textures/items/potion_overlay.png"),fluidType.getTexture()
				);
				map.setTextureEntry(maskedSpr);
			}
		}
	}
}
