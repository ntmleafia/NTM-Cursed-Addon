package com.leafia.shit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class AssHooks {
	/// accessed via ASM
	public static boolean loadAdvancements(Map<ResourceLocation, Builder> map) {
		ModContainer mod = null;
		for (ModContainer container : Loader.instance().getModList()) {
			if (container.getModId().equals("leafia")) {
				mod = container;
				break;
			}
		}
		if (mod == null)
			throw new LeafiaDevFlaw("????????????");
		JsonContext ctx = new JsonContext(mod.getModId());

		return CraftingHelper.findFiles(mod, "assets/hbm/advancements", null,
				(root, file) ->
				{

					String relative = root.relativize(file).toString();
					if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;

					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
					ResourceLocation key = new ResourceLocation("hbm", name);

					if (/*!map.containsKey(key)*/true)
					{
						BufferedReader reader = null;

						try
						{
							reader = Files.newBufferedReader(file);
							String contents = IOUtils.toString(reader);
							JsonObject json = JsonUtils.gsonDeserialize(CraftingHelper.GSON, contents, JsonObject.class);
							if (!CraftingHelper.processConditions(json, "conditions", ctx))
								return true;
							Advancement.Builder builder = JsonUtils.gsonDeserialize(AdvancementManager.GSON, contents, Advancement.Builder.class);
							map.put(key, builder);
						}
						catch (JsonParseException jsonparseexception)
						{
							FMLLog.log.error("Parsing error loading built-in advancement " + key, (Throwable)jsonparseexception);
							return false;
						}
						catch (IOException ioexception)
						{
							FMLLog.log.error("Couldn't read advancement " + key + " from " + file, (Throwable)ioexception);
							return false;
						}
						finally
						{
							IOUtils.closeQuietly(reader);
						}
					}

					return true;
				},
				true, true
		);
	}
}
