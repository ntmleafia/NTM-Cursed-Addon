package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.control_panel.IControllable;
import com.hbm.items.ModItems;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import net.minecraft.item.Item;

public interface IMixinTileEntityCoreStabilizer extends IDFCBase, IControllable {
    long maxPower = 10000000000000L;
    int range = 50;

    enum LensType {
        STANDARD(0x0c222c, 0x7F7F7F, ModItems.ams_lens),
        BLANK(0x121212, 0x646464, ModItems.ams_focus_blank),
        LIMITER(0x001733, 0x7F7F7F, ModItems.ams_focus_limiter),
        BOOSTER(0x4f1600, 0x7F7F7F, ModItems.ams_focus_booster),
        OMEGA(0x64001e, 0x9A9A9A, ModItems.ams_focus_omega);
        public final int outerColor;
        public final int innerColor;
        public final Item item;
        public static final LensType[] VALUES = new LensType[]{STANDARD, BLANK, LIMITER, BOOSTER, OMEGA};

        LensType(int outerColor, int innerColor, Item item) {
            this.outerColor = outerColor;
            this.innerColor = innerColor;
            this.item = item;
        }
    }

    boolean hasLens();

    LensType getLens();
}
