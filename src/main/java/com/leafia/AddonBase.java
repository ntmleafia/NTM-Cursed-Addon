package com.leafia;

import com.hbm.handler.GuiHandler;
import com.hbm.items.machine.ItemBatteryPack.EnumBatteryPack;
import com.hbm.packet.PacketDispatcher;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonFluids.AddonFF;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import com.leafia.contents.machines.controlpanel.instruments.AddonInstrumentRegister;
import com.leafia.contents.potion.LeafiaPotion;
import com.leafia.contents.worldgen.AddonBiomes;
import com.leafia.contents.worldgen.AddonBiomesGenerator;
import com.leafia.contents.worldgen.AddonWorldGen;
import com.leafia.contents.worldgen.NTMStructBuffer.StructLoader;
import com.leafia.database.AirDetonationMissiles;
import com.leafia.database.ReactorTiers;
import com.leafia.dev.optimization.LeafiaParticlePacket;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.eventbuses.LeafiaServerListener;
import com.leafia.init.*;
import com.leafia.init.proxy.LeafiaServerProxy;
import com.leafia.init.recipes.*;
import com.leafia.overwrite_contents.other.LCEItemCatalyst;
import com.leafia.settings.AddonConfig;
import com.leafia.unsorted.AddonGuiHandler;
import com.leafia.unsorted.LeafiaBlockReplacer;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Mod(modid = Tags.MODID, version = "Unknown", name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
		dependencies = "required-after:hbm@[2.5.0.2,);required:mixinbooter;after:ntmspace")
public class AddonBase {
	public enum AddonLoadingStage {
		BLOCKS,
		ITEMS
	}
	public static AddonLoadingStage staticLoadingStage = null;

	public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
	@SidedProxy(clientSide = "com.leafia.init.proxy.LeafiaClientProxy", serverSide = "com.leafia.init.proxy.LeafiaServerProxy")
	public static LeafiaServerProxy proxy;
	@Mod.Instance(Tags.MODID)
	public static AddonBase instance;

	public static final String MODID = Tags.MODID;

	public static final ResourceLocation solid = new ResourceLocation("leafia", "textures/solid.png");
	public static final ResourceLocation solid_e = new ResourceLocation("leafia", "textures/solid_emissive.png");
	public static final ResourceLocation invisible = new ResourceLocation("leafia", "textures/invisible.png");

	static {
		//LeafiaSoundEvents.init();
	}

	public static void _initMemberClasses(Class<?> c) {
		for (Class<?> cl : c.getClasses()) // stupid solution to initialize the stupid fields
			_initClass(cl);
	}
	public static void _initClass(Class<?> c) {
		try {
			Class.forName(c.getName());
			System.out.println("Initialized member class "+c.getSimpleName());
		} catch (ClassNotFoundException exception) {
			LeafiaDevFlaw flaw = new LeafiaDevFlaw("ModItems failed to initialize member class "+c.getSimpleName());
			flaw.setStackTrace(exception.getStackTrace());
			throw flaw;
		}
	}

	public static ResourceLocation getIntegrated(String s) {
		return new ResourceLocation("leafia","textures/_integrated/"+s);
	}

	@EventHandler
	// preInit "Run before anything else. Read your config, create blocks, items, etc. (Remove if not needed)
	public void preInit(FMLPreInitializationEvent event) {
		// register to the event bus so that we can listen to events
		MinecraftForge.EVENT_BUS.register(this);

		LeafiaSoundEvents.init();

		for (EnumBatteryPack value : EnumBatteryPack.values()) {
			System.out.println("ENUM: "+value.name()+", ORDINAL: "+value.ordinal());
		}

		_initClass(AddonConfig.class);

		PacketDispatcher.LISTENERS.add(new AddonPacketRegister());

		for (Class<?> cl : LeafiaServerListener.class.getClasses()) {
			try {
				MinecraftForge.EVENT_BUS.register(cl.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LeafiaDevFlaw flaw = new LeafiaDevFlaw(e.getMessage());
				flaw.setStackTrace(e.getStackTrace());
				throw flaw;
			}
		}
		AddonFluids.init();
		AddonFF.init();
		staticLoadingStage = AddonLoadingStage.BLOCKS;
		AddonBlocks.preInit();
		staticLoadingStage = AddonLoadingStage.ITEMS;
		AddonItems.preInit();
		if (AddonConfig.enableSellacity) {
			AddonBiomesGenerator gen = new AddonBiomesGenerator();
			MinecraftForge.EVENT_BUS.register(gen);
			MinecraftForge.TERRAIN_GEN_BUS.register(gen);
		}
		LeafiaPotion.init();
		proxy.registerRenderInfo();

		TEInit.preInit();
		EntityInit.preInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		GameRegistry.registerWorldGenerator(new AddonWorldGen(),1);
		proxy.preInit(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance,new AddonGuiHandler());

		LCEItemCatalyst.registerMeltingPoints();

		AddonFluidTraits.preInit();

		AddonInstrumentRegister.register();
	}

	@SubscribeEvent
	// Register recipes here (Remove if not needed)
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

	}

	@SubscribeEvent
	// Register items here (Remove if not needed)
	public void registerItems(RegistryEvent.Register<Item> event) {

	}

	@SubscribeEvent
	// Register blocks here (Remove if not needed)
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	}

	@EventHandler
	// load "Do your mod setup. Build whatever data structures you care about." (Remove if not needed)
	public void init(FMLInitializationEvent event) {
		AddonHazards.register();
		AddonNodesRegister.register();
		AirDetonationMissiles.init();
		ReactorTiers.register();
		AddonBiomes.init();
	}

	@EventHandler
	// postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
	public void postInit(FMLPostInitializationEvent event) {
		AddonFF.setFromRegistry();
		ArmorInit.postInit();
		LeafiaBlockReplacer.addReplacementMap();
		StructLoader.init();
	}

	public static void registerSerializable() {
		AddonChemplantRecipes.register();
		AddonAssemblerRecipes.register();
		AddonGasCentRecipes.register();
		AddonElectrolyzerRecipes.register();
		AddonPyroOvenRecipes.register();
		AddonAnvilRecipes.registerSmithingRecipes();
		AddonAnvilRecipes.registerConstructionRecipes();
		AddonPUREXRecipes.register();
		AddonWasteDrumRecipes.register();
		AddonCentrifugeRecipes.register();
		AddonArcWelderRecipes.register();
		AddonDFCRecipes.register();
		AddonSmeltingRecipes.register();
		AddonSolderingRecipes.register();
		AddonMixerRecipes.register();
		AddonCompressorRecipes.register();
		AddonPlasmaForgeRecipes.register();
		AddonShredderRecipes.register();
		AddonPARecipes.register();
		AddonExposureChamberRecipes.register();
	}

	@EventHandler
	public void fMLLoadCompleteEvent(FMLLoadCompleteEvent evt){
		proxy.onLoadComplete(evt);
		FalloutConfigInit.onInit();
		_initClass(LeafiaParticlePacket.class); // make it crash here if it's missing SideOnly annotation

        /*
        FluidTankNTM tankNTM = new FluidTankNTM(Fluids.CRYOGEL,1000);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Hello","World!");
        NTMFNBT.setNBT(tankNTM,nbt);

        NBTTagCompound compound = NTMFNBT.getNBT(tankNTM);
        System.out.println(compound.getString("Hello"));*/
	}

	@EventHandler
	// register server commands in this event handler (Remove if not needed)
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandLeaf());
		AddonAdvancements.init(event.getServer());
		if (event.getServer().isDedicatedServer()) {
			/*
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
				//String s = r.readLine();
				//System.out.println(s+" INPUTTED");
				r.close();
			} catch (IOException e) {
				throw new LeafiaDevFlaw(e);
			}*/ // forget it
		}
	}

	@EventHandler
	public void fMLServerStoppedEvent(FMLServerStoppedEvent evt) {
		if (evt.getSide() == Side.CLIENT)
			LeafiaClientListener.HandlerClient.iQuit(evt);
	}
}
