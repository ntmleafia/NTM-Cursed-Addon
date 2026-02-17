package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.machine.ItemBatteryPack;
import com.leafia.contents.control.battery.AddonEnumBatteryPack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(value = ItemBatteryPack.EnumBatteryPack.class, remap = false)
public abstract class MixinEnumBatteryPack {

    @Shadow
    @Final
    @Mutable
    public static ItemBatteryPack.EnumBatteryPack[] VALUES;
    @Shadow
    @Final
    @Mutable
    private static ItemBatteryPack.EnumBatteryPack[] $VALUES;

    @Invoker("<init>")
    private static ItemBatteryPack.EnumBatteryPack leafia$ctor_bool(String enumName, int enumOrdinal, String tex, long dischargeRate, boolean capacitor) {
        throw new AssertionError();
    }

    @Invoker("<init>")
    private static ItemBatteryPack.EnumBatteryPack leafia$ctor_duration(String enumName, int enumOrdinal, String tex, long dischargeRate, long duration) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/hbm/items/machine/ItemBatteryPack$EnumBatteryPack;values()[Lcom/hbm/items/machine/ItemBatteryPack$EnumBatteryPack;", shift = At.Shift.BEFORE))
    private static void leafia$extendEnum(CallbackInfo ci) {
        int base = $VALUES.length;
        var desh = leafia$ctor_bool("BATTERY_DESH", base, "battery_desh", 125_000L, false);
        var euphemium = leafia$ctor_bool("BATTERY_EUPHEMIUM", base + 1, "battery_euphemium", 500_000L, false);
        var slop = leafia$ctor_duration("BATTERY_SLOP", base + 2, "battery_slop", 833_333_333L, 20L * 60L * 10L);
        var spk = leafia$ctor_duration("BATTERY_SPK", base + 3, "battery_spk", 16_666_666_667L, 20L * 60L * 5L);
        var electro = leafia$ctor_duration("BATTERY_ELECTRO", base + 4, "battery_electro", 166_666_666_667L, 20L * 60L * 5L);
        var ext = Arrays.copyOf($VALUES, base + 5);
        ext[base] = desh;
        ext[base + 1] = euphemium;
        ext[base + 2] = slop;
        ext[base + 3] = spk;
        ext[base + 4] = electro;
        $VALUES = VALUES = ext;
        AddonEnumBatteryPack.BATTERY_DESH = desh;
        AddonEnumBatteryPack.BATTERY_EUPHEMIUM = euphemium;
        AddonEnumBatteryPack.BATTERY_SLOP = slop;
        AddonEnumBatteryPack.BATTERY_SPK = spk;
        AddonEnumBatteryPack.BATTERY_ELECTRO = electro;
    }

    @Inject(method = "valueOf", at = @At("HEAD"), cancellable = true)
    private static void leafia$valueOf(String name, CallbackInfoReturnable<ItemBatteryPack.EnumBatteryPack> cir) {
        switch (name) {
            case "BATTERY_DESH":
                cir.setReturnValue(AddonEnumBatteryPack.BATTERY_DESH);
                return;
            case "BATTERY_EUPHEMIUM":
                cir.setReturnValue(AddonEnumBatteryPack.BATTERY_EUPHEMIUM);
                return;
            case "BATTERY_SLOP":
                cir.setReturnValue(AddonEnumBatteryPack.BATTERY_SLOP);
                return;
            case "BATTERY_SPK":
                cir.setReturnValue(AddonEnumBatteryPack.BATTERY_SPK);
                return;
            case "BATTERY_ELECTRO":
                cir.setReturnValue(AddonEnumBatteryPack.BATTERY_ELECTRO);
                return;
            default:
        }
    }

    @Inject(method = "isCapacitor", at = @At("HEAD"), cancellable = true)
    private void leafia$isCapacitor(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((Enum) (Object) this).name().startsWith("CAPACITOR_"));
    }
}

