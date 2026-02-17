package com.leafia.init.proxy;

import com.custom_hbm.contents.torex.LCETorex;
import com.custom_hbm.contents.torex.LCETorexRender;
import com.custom_hbm.render.tileentity.LCERenderSpinnyLight;
import com.custom_hbm.sound.LCEAudioWrapper;
import com.custom_hbm.sound.LCEAudioWrapperClient;
import com.custom_hbm.sound.LCEAudioWrapperClientStartStop;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.tileentity.deco.TileEntitySpinnyLight;
import com.hbm.tileentity.machine.*;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonItems;
import com.leafia.contents.bomb.missile.customnuke.entity.CustomNukeMissileEntity;
import com.leafia.contents.bomb.missile.customnuke.entity.CustomNukeMissileEntityRender;
import com.leafia.contents.building.broof.BroofRender;
import com.leafia.contents.building.broof.BroofTE;
import com.leafia.contents.building.light.LightRender;
import com.leafia.contents.building.light.LightTE;
import com.leafia.contents.building.sign.SignRender;
import com.leafia.contents.building.sign.SignTE;
import com.leafia.contents.cannery.AddonJars;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodRender;
import com.leafia.contents.effects.folkvangr.visual.LCERenderCloudFleija;
import com.leafia.contents.effects.folkvangr.visual.LCERenderCloudRainbow;
import com.leafia.contents.gear.utility.FuzzyIdentifierRender;
import com.leafia.contents.machines.elevators.*;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.contents.machines.elevators.floors.EvFloorRender;
import com.leafia.contents.machines.elevators.floors.EvFloorTE;
import com.leafia.contents.machines.elevators.weight.EvWeightEntity;
import com.leafia.contents.machines.elevators.weight.EvWeightRender;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexRender;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexTE;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseRender;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterRender;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterTE;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerRender;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerTE;
import com.leafia.contents.machines.powercores.dfc.components.cemitter.CoreCEmitterTE;
import com.leafia.contents.machines.powercores.dfc.components.exchanger.CoreExchangerTE;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCCoreRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRenderNeo;
import com.leafia.contents.machines.processing.mixingvat.MixingVatTE;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryRender;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryTE;
import com.leafia.contents.machines.reactors.lftr.components.control.MSRControlTE;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorRender;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlRender;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlTE;
import com.leafia.contents.machines.reactors.pwr.blocks.components.element.PWRElementBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreckEntity;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.RenderPWRMeshedWreck;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisEntity;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisItemRender;
import com.leafia.contents.machines.reactors.pwr.debris.RenderPWRDebris;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityRender;
import com.leafia.contents.network.ff_duct.utility.converter.FFConverterTE;
import com.leafia.contents.network.ff_duct.utility.pump.FFPumpTE;
import com.leafia.contents.network.fluid.FluidDuctEquipmentRender;
import com.leafia.contents.network.fluid.gauges.FluidDuctGaugeTE;
import com.leafia.contents.network.fluid.valves.FluidDuctValveTE;
import com.leafia.contents.network.spk_cable.SPKCableRender;
import com.leafia.contents.network.spk_cable.SPKCableTE;
import com.leafia.contents.nonmachines.fftank.FFTankRender;
import com.leafia.contents.nonmachines.fftank.FFTankTE;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.init.AddonAdvancements;
import com.leafia.init.ItemRendererInit;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class LeafiaClientProxy extends LeafiaServerProxy {
	@Override
	public void registerRenderInfo() {
		for (Class<?> cl : LeafiaClientListener.class.getClasses()) {
			try {
				MinecraftForge.EVENT_BUS.register(cl.newInstance());
			} catch (InstantiationException|IllegalAccessException e) {
				LeafiaDevFlaw flaw = new LeafiaDevFlaw(e.getMessage());
				flaw.setStackTrace(e.getStackTrace());
				throw flaw;
			}
		}
		{
			ModelLoader.setCustomStateMapper(AddonBlocks.door_fuckoff,new StateMap.Builder().ignore(BlockDoor.POWERED).build());
			ModelLoader.setCustomStateMapper(AddonBlocks.fluid_fluoride,new StateMap.Builder().ignore(BlockFluidClassic.LEVEL).build());
			ModelLoader.setCustomStateMapper(PWR.element,new StateMap.Builder().ignore(PWRElementBlock.stacked).build());
			ModelLoader.setCustomStateMapper(PWR.element_old,new StateMap.Builder().ignore(PWRElementBlock.stacked).build());
			ModelLoader.setCustomStateMapper(PWR.element_old_blank,new StateMap.Builder().ignore(PWRElementBlock.stacked).build());
		}
		{
			RenderingRegistry.registerEntityRenderingHandler(EntityCloudFleija.class,LCERenderCloudFleija.FACTORY);
			RenderingRegistry.registerEntityRenderingHandler(EntityCloudFleijaRainbow.class,LCERenderCloudRainbow.FACTORY);
			RenderingRegistry.registerEntityRenderingHandler(LCETorex.class,LCETorexRender.FACTORY);

			RenderingRegistry.registerEntityRenderingHandler(AbsorberShrapnelEntity.class,AbsorberShrapnelRender.FACTORY);
			RenderingRegistry.registerEntityRenderingHandler(PWRDebrisEntity.class,RenderPWRDebris.FACTORY);
			RenderingRegistry.registerEntityRenderingHandler(CustomNukeMissileEntity.class,CustomNukeMissileEntityRender.FACTORY);

			RenderingRegistry.registerEntityRenderingHandler(ElevatorEntity.class,ElevatorRender.FACTORY);
			RenderingRegistry.registerEntityRenderingHandler(EvWeightEntity.class,EvWeightRender.FACTORY);
		}
		{
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpinnyLight.class,new LCERenderSpinnyLight());

			ClientRegistry.bindTileEntitySpecialRenderer(SPKCableTE.class,new SPKCableRender());

			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCore.class,new DFCCoreRender());
			DFCComponentRender dfcComponentRender = new DFCComponentRender();
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreEmitter.class,dfcComponentRender);
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreReceiver.class,dfcComponentRender);
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreStabilizer.class,dfcComponentRender);
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreInjector.class,dfcComponentRender);
			ClientRegistry.bindTileEntitySpecialRenderer(CoreCEmitterTE.class,dfcComponentRender);
			ClientRegistry.bindTileEntitySpecialRenderer(CoreExchangerTE.class,dfcComponentRender);

			ClientRegistry.bindTileEntitySpecialRenderer(SignTE.class,new SignRender());
			FFDuctUtilityRender ffUtilityRender = new FFDuctUtilityRender();
			ClientRegistry.bindTileEntitySpecialRenderer(FFPumpTE.class,ffUtilityRender);
			ClientRegistry.bindTileEntitySpecialRenderer(FFConverterTE.class,ffUtilityRender);

			ClientRegistry.bindTileEntitySpecialRenderer(SaltSeparatorTE.class,new SaltSeparatorRender());
			ClientRegistry.bindTileEntitySpecialRenderer(MSRArbitraryTE.class,new MSRArbitraryRender());
			ClientRegistry.bindTileEntitySpecialRenderer(MixingVatTE.class,new MixingVatRenderNeo());
			ClientRegistry.bindTileEntitySpecialRenderer(CoolantHeatexTE.class,new CoolantHeatexRender());

			ClientRegistry.bindTileEntitySpecialRenderer(PWRControlTE.class,new PWRControlRender());
			ClientRegistry.bindTileEntitySpecialRenderer(PWRMeshedWreckEntity.class,new RenderPWRMeshedWreck());

			ClientRegistry.bindTileEntitySpecialRenderer(LightTE.class,new LightRender());
			FluidDuctEquipmentRender equipment = new FluidDuctEquipmentRender();
			ClientRegistry.bindTileEntitySpecialRenderer(FluidDuctGaugeTE.class,equipment);
			ClientRegistry.bindTileEntitySpecialRenderer(FluidDuctValveTE.class,equipment);

			ClientRegistry.bindTileEntitySpecialRenderer(AMSBaseTE.class,new AMSBaseRender());
			ClientRegistry.bindTileEntitySpecialRenderer(AMSStabilizerTE.class,new AMSStabilizerRender());
			ClientRegistry.bindTileEntitySpecialRenderer(AMSEmitterTE.class,new AMSEmitterRender());

			ClientRegistry.bindTileEntitySpecialRenderer(BroofTE.class,new BroofRender());
			ClientRegistry.bindTileEntitySpecialRenderer(FFTankTE.class,new FFTankRender());

			ClientRegistry.bindTileEntitySpecialRenderer(EvFloorTE.class, new EvFloorRender());
			ClientRegistry.bindTileEntitySpecialRenderer(EvPulleyTE.class, new EvPulleyRender());
			ClientRegistry.bindTileEntitySpecialRenderer(EvShaftTE.class, new EvShaftRender());
			ClientRegistry.bindTileEntitySpecialRenderer(EvBufferTE.class, new EvBufferRender());
		}
		AddonJars.initJars();
	}
	@Override
	public File getDataDir() {
		return Minecraft.getMinecraft().gameDir;
	}

	@Override
	public LCEAudioWrapper getLoopedSound(SoundEvent sound,SoundCategory cat,float x,float y,float z,float volume,float pitch) {
		LCEAudioWrapperClient audio = new LCEAudioWrapperClient(sound, cat);
		audio.updatePosition(x, y, z);
		return audio;
	}

	@Override
	public LCEAudioWrapper getLoopedSoundStartStop(World world,SoundEvent sound,SoundEvent start,SoundEvent stop,SoundCategory cat,float x,float y,float z,float volume,float pitch) {
		LCEAudioWrapperClientStartStop audio = new LCEAudioWrapperClientStartStop(world, sound, start, stop, volume, cat);
		audio.updatePosition(x, y, z);
		if (pitch != 1)
			audio.updatePitch(pitch);
		return audio;
	}

	@Override
	public void onLoadComplete(FMLLoadCompleteEvent event){
		if (!Loader.isModLoaded("backups")) LeafiaClientListener.HandlerClient.backupsWarning = true;
	}

	@Override
	public void preInit(FMLPreInitializationEvent evt) {
		ItemRendererInit.preInit();
		ItemRendererInit.apply();

		for (LeafiaRodItem rod : LeafiaRodItem.fromResourceMap.values()) {
			rod.setTileEntityItemStackRenderer(LeafiaRodRender.INSTANCE);
			//ItemRendererInit.fixFuckingLocations.add(rod);
		}

		/*{
			AddonItems.pwr_piece.setTileEntityItemStackRenderer(new PWRDebrisItemRender());
			AddonItems.pwr_shrapnel.setTileEntityItemStackRenderer(new PWRDebrisItemRender());
			AddonItems.pwr_shard.setTileEntityItemStackRenderer(new PWRDebrisItemRender());
		}*/

		for (Item item : AddonItems.ALL_ITEMS) {

		}
		// thanks TheSlize
		/*for (Item toFix : ItemRendererInit.fixFuckingLocations) {
			ModelLoader.setCustomModelResourceLocation(
					toFix,
					0,
					new ModelResourceLocation(toFix.getRegistryName(), "inventory")
			);
		}*/
		AddonItems.fuzzy_identifier.setTileEntityItemStackRenderer(FuzzyIdentifierRender.INSTANCE);
	}
}
