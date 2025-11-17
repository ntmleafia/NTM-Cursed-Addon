package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.control_panel.IControllable;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import net.minecraft.item.Item;

public interface IMixinTileEntityCoreStabilizer extends IDFCBase, IControllable {
    long maxPower = 10000000000000L;
    int range = 50;

    enum LensType {
        STANDARD(0x0c222c, 0x7F7F7F, ModItems.ams_lens),
        BLANK(0x121212, 0x646464, AddonItems.ams_focus_blank),
        LIMITER(0x001733, 0x7F7F7F, AddonItems.ams_focus_limiter),
        BOOSTER(0x4f1600, 0x7F7F7F, AddonItems.ams_focus_booster),
        OMEGA(0x64001e, 0x9A9A9A, AddonItems.ams_focus_omega),
        SAFE(0x166800,0x73ec51, AddonItems.ams_focus_safe);
        public final int outerColor;
        public final int innerColor;
        public final Item item;
        //public static final LensType[] VALUES = new LensType[]{STANDARD, BLANK, LIMITER, BOOSTER, OMEGA, SAFE};
        // wtf this is redudant ^^
        LensType(int outerColor, int innerColor, Item item) {
            this.outerColor = outerColor;
            this.innerColor = innerColor;
            this.item = item;
        }
    }

    boolean hasLens();

    LensType getLens();
}
