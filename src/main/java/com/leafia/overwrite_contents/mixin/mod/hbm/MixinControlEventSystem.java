package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.spongepowered.asm.mixin.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = ControlEventSystem.class,remap = false)
public class MixinControlEventSystem {
	@Shadow
	@Final
	private static Map<World,ControlEventSystem> systems;
	@Unique
	private static final Field leafia$tickablesHandle;
	static {
		try {
			leafia$tickablesHandle = ControlEventSystem.class.getDeclaredField("tickables");
			leafia$tickablesHandle.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	private static Set<TileEntity> leafia$wasValid = new HashSet<>();
	/**
	 * @author ntmleafia
	 * @reason fuck off!
	 */
	@Overwrite
	@SubscribeEvent
	public static void tick(WorldTickEvent evt) {
		try {
			if (evt.phase != Phase.START || evt.world.isRemote)
				return;
			ControlEventSystem s = systems.get(evt.world);
			if (s != null) {
				ObjectOpenHashSet<IControllable> t = (ObjectOpenHashSet<IControllable>)leafia$tickablesHandle.get(s);
				Set<IControllable> controllables = new HashSet<>(t);
				for (IControllable c : controllables) {
					if (c instanceof TileEntity te) {
						if (te.getWorld().getChunkProvider().getLoadedChunk(te.getPos().getX()>>4,te.getPos().getZ()>>4) == null) {
							if (leafia$wasValid.contains(te)) {
								t.remove(te);
								leafia$wasValid.remove(te);
							}
							continue;
						} else
							leafia$wasValid.add(te);
					}
					c.receiveEvent(c.getControlPos(),ControlEvent.newEvent("tick").setVar("time",evt.world.getTotalWorldTime()));
				}
			}
		} catch (IllegalAccessException ignored) {}
	}
}
