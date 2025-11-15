package com.leafia.init;

import com.hbm.lib.MethodHandleHelper;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ResourceInit {
    public static final List<WaveFrontObjectVAO> allVAOs = new ArrayList<>();
    private static final MethodHandle pauseSplash = MethodHandleHelper.findStatic(ResourceManager.class, "pauseSplash", MethodType.methodType(void.class));
    private static final MethodHandle resumeSplash = MethodHandleHelper.findStatic(ResourceManager.class, "resumeSplash", MethodType.methodType(void.class));


    public static final WaveFrontObjectVAO cloudFleija = getVAO(new ResourceLocation(RefStrings.MODID, "models/Sphere.obj"));


    public static void init() {
        try {
            pauseSplash.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        for (WaveFrontObjectVAO obj : allVAOs) {
            obj.generate_vaos();
        }
        try {
            resumeSplash.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static WaveFrontObjectVAO getVAO(ResourceLocation model) {
        WaveFrontObjectVAO vao = new HFRWavefrontObject(model).asVBO();
        WaveFrontObjectVAO.allVBOs.remove(vao);
        allVAOs.add(vao);
        return vao;
    }
}
