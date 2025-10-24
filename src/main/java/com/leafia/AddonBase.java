package com.leafia;

import com.leafia.contents.AddonBlocks;
import com.leafia.init.PacketInit;
import com.leafia.init.proxy.ServerProxy;
import com.llib.exceptions.LeafiaDevFlaw;
import com.myname.mymodid.Tags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:hbm")
public class AddonBase {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    @SidedProxy(clientSide = "com.leafia.init.proxy.ClientProxy", serverSide = "com.leafia.init.proxy.ServerProxy")
    public static ServerProxy proxy;

    public static void _initMemberClasses(Class<?> c) {
        for (Class<?> cl : c.getClasses()) { // stupid solution to initialize the stupid fields
            try {
                Class.forName(cl.getName());
                System.out.println("Initialized member class "+cl.getSimpleName());
            } catch (ClassNotFoundException exception) {
                LeafiaDevFlaw flaw = new LeafiaDevFlaw("ModItems failed to initialize member class "+cl.getSimpleName());
                flaw.setStackTrace(exception.getStackTrace());
                throw flaw;
            }
        }
    }

    @EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc. (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // register to the event bus so that we can listen to events
        MinecraftForge.EVENT_BUS.register(this);
        AddonBlocks.preInit();
        PacketInit.registerPackets();
        LOGGER.info("I am " + Tags.MODNAME + " + at version " + Tags.VERSION);
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
    }

    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
    }

    @EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
    }
}
