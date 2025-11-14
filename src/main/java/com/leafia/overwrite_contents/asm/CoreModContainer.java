package com.leafia.overwrite_contents.asm;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Arrays;

public class CoreModContainer extends DummyModContainer {

	public CoreModContainer() {
		super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "leafiacore";
        meta.name = "LeafiaCore";
        meta.description = "Binary class transformers to push the boundaries of the mod\n\nNow with the help of movblock.";
        meta.version = "1.12.2-1.0";
        meta.authorList = Arrays.asList("Leafia","movblock");
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
}
