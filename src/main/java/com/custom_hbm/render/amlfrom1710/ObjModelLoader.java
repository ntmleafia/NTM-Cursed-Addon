package com.custom_hbm.render.amlfrom1710;

import com.hbm.render.amlfrom1710.IModelCustom;
import com.hbm.render.amlfrom1710.ModelFormatException;
import com.hbm.render.amlfrom1710.WavefrontObject;
import net.minecraft.util.ResourceLocation;

public class ObjModelLoader implements IModelCustomLoader
{

    @Override
    public String getType()
    {
        return "OBJ model";
    }

    private static final String[] types = { "obj" };
    @Override
    public String[] getSuffixes()
    {
        return types;
    }

    @Override
    public IModelCustom loadInstance(ResourceLocation resource) throws ModelFormatException
    {
        return new WavefrontObject(resource);
    }
}
