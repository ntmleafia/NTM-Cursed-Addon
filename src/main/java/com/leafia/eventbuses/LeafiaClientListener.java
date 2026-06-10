package com.leafia.eventbuses;

import com.custom_hbm.GuiAssentialWarning;
import com.custom_hbm.GuiBackupsWarning;
import com.custom_hbm.sound.LCEAudioWrapperClientStartStop;
import com.google.gson.JsonSyntaxException;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.generic.BlockControlPanel;
import com.hbm.capability.HbmLivingProps;
import com.hbm.config.ClientConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.config.MobConfig;
import com.hbm.interfaces.IHasCustomModel;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.items.IDynamicModels;
import com.hbm.items.ModItems;
import com.hbm.lib.ModDamageSource;
import com.hbm.main.ResourceManager;
import com.hbm.render.GuiCTMWarning;
import com.custom_hbm.util.LCETuple.*;
import com.hbm.render.item.TEISRBase;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.Tags;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import com.leafia.contents.building.catwalk.railing.CatwalkRailingBase;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.gear.IADSWeapon;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryBlock;
import com.leafia.contents.machines.reactors.lftr.components.ejector.MSREjectorBlock;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementBlock;
import com.leafia.contents.machines.reactors.lftr.components.plug.MSRPlugBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.contents.network.ff_duct.FFDuctStandard;
import com.leafia.contents.network.pipe_amat.AmatDuctStandard;
import com.leafia.contents.worldgen.AddonBiome;
import com.leafia.contents.worldgen.AddonBiomes;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.dev.LeafiaBrush;
import com.leafia.dev.LeafiaBrush.BrushMode;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.init.ResourceInit;
import com.leafia.passive.LeafiaPassiveLocal;
import com.leafia.passive.effects.LeafiaShakecam;
import com.leafia.passive.rendering.AddonRainRender;
import com.leafia.passive.rendering.TopRender;
import com.leafia.settings.AddonConfig;
import com.leafia.shit.leafiashader.BigBruh;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.IEntityCustomCollision;
import com.leafia.unsorted.StructuralIntegrityHandler;
import com.leafia.unsorted.StructuralIntegrityHandler.SimulationData;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class LeafiaClientListener {
	public static class Digamma {
		public static MusicTicker.MusicType silence;
		public static void backstab(EntityPlayer player) {
			float strength = 0.4f;
			float yaw = player.rotationYaw+player.world.rand.nextFloat()*90-45;
			float xRatio = (float)-(-Math.sin(yaw/180*Math.PI));
			float zRatio = (float)-(Math.cos(yaw/180*Math.PI));
			if (true) // nop cuz fuc u player.world.rand.nextDouble() >= player.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue())
			{
				player.isAirBorne = true;
				float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
				player.motionX /= 2.0D;
				player.motionZ /= 2.0D;
				player.motionX -= xRatio / (double)f * (double)strength;
				player.motionZ -= zRatio / (double)f * (double)strength;

				if (player.onGround)
				{
					player.motionY /= 2.0D;
					player.motionY += (double)strength;

					if (player.motionY > 0.4000000059604645D)
					{
						player.motionY = 0.4000000059604645D;
					}
				}
			}
		}

		public Digamma() {
			silence = EnumHelperClient.addMusicType("LEAFIA_SILENCE", LeafiaSoundEvents.literally_nothing, 12000, 24000);
		}

		public static ResourceLocation vg = new ResourceLocation(Tags.MODID,"textures/vignette.png");
		static int vgTicks = 0;
		static final int vgMaxTicks = 10;
		public static void drawVignette(ScaledResolution resolution,float partialTicks) {
			if (vgTicks <= 0) return;
			LeafiaBrush brush = LeafiaBrush.instance;
			Minecraft.getMinecraft().renderEngine.bindTexture(vg);
			float ratio = (vgTicks-partialTicks)/vgMaxTicks;
			LeafiaGls.disableAlpha();
			LeafiaGls.color(1,1,1,(float)Math.sin(ratio*Math.PI)/2.5f);
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
			brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.POSITION_TEX);
			brush.addVertexWithUV(0,resolution.getScaledHeight_double(),0,0,1);
			brush.addVertexWithUV(resolution.getScaledWidth_double(),resolution.getScaledHeight_double(),0,1,1);
			brush.addVertexWithUV(resolution.getScaledWidth_double(),0,0,1,0);
			brush.addVertexWithUV(0,0,0,0,0);
			brush.draw();
			//LeafiaGls.disableBlend();
			LeafiaGls.enableAlpha();
		}

		public static float digammaDose = 1;
		static Random rand = new Random();
		static List<DigammaText> texts = new ArrayList<>();
		/*@SubscribeEvent
		public void fovUpdate(FOVUpdateEvent e){
			float fovMultiplier = 1-digammaDose*0.21428571428f;
			e.setNewfov(e.getFov()*fovMultiplier);
		}*/
		@SubscribeEvent
		public void shake(EntityViewRenderEvent.CameraSetup e) {
			if (digammaDose > 0.25f) {
				float ratio = (digammaDose-0.25f)/0.75f;
				GL11.glTranslated(rand.nextGaussian()*ratio*0.065,0,0);
			}
		}
		@SubscribeEvent
		public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
			if (event.getType() == ElementType.CROSSHAIRS) {
				ScaledResolution resolution = event.getResolution();
				FontRenderer font = Minecraft.getMinecraft().fontRenderer;
				for (DigammaText text : texts) {
					int w = font.getStringWidth(text.message);
					int h = font.FONT_HEIGHT;
					LeafiaGls.pushMatrix();
					float shakex = rand.nextFloat()*4-2;
					float shakey = rand.nextFloat()*4-2;
					LeafiaGls.translate(resolution.getScaledWidth()*text.x+shakex,resolution.getScaledHeight()*text.y+shakey,0);
					LeafiaGls.scale(text.scale);
					LeafiaGls.translate(-w/2f,-h/2f,0);
					/*
					int alphaChannel = 0x01000000;
					float alpha = text.timeElapsed;
					if (text.timeElapsed >= 1) {
						if (text.timeElapsed > 5)
							alpha = (1-(text.timeElapsed-5))*255;
						else
							alpha = 1;
					}
					int value = alphaChannel*(int)Math.ceil(alpha*255);*/ // fuck off
					font.drawString(text.message,0,0,0xFFFFFF);
					LeafiaGls.popMatrix();
				}
			}
		}
		static float timer = 0;
		static float timerMax = 5;
		public static int messageVariants = 10;
		public static LCEAudioWrapperClientStartStop ambience;
		public static double ambienceVolume = 0;
		static BiFunction<Float,Double,Double> nope = (intended,dist)->ambienceVolume;
		static int beatTimer = 0;
		public static void update(World world) {
			if (ambience != null) {
				if (ambience.world != world) {
					ambience.stopSound();
					ambience = null;
				}
			}
			if (ambience != null) {
				if (!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ambience.sound)) {
					ambience.stopSound();
					ambience = null;
				}
			}
			if (vgTicks > 0)
				vgTicks--;
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (DigammaCrater.isDigammaBiome(world.getBiome(new BlockPos(player.posX,player.posY,player.posZ)))) {
				ambienceVolume = Math.min(ambienceVolume+0.01,1);
				beatTimer++;
				if (beatTimer >= 17) {
					beatTimer = 0;
					vgTicks = vgMaxTicks;
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.eversionsong7_cut,1));
				}
				if (ambience == null) {
					ambience = new LCEAudioWrapperClientStartStop(world,LeafiaSoundEvents.sbmoon_surface,null,null,0.01f,SoundCategory.AMBIENT);
					ambience.setCustomAttenuation(nope);
					ambience.setLooped(true);
					Minecraft.getMinecraft().getSoundHandler().playSound(ambience.sound);
				}
				ambience.updateVolume((float)ambienceVolume/1.5f);
			} else {
				ambienceVolume = Math.max(ambienceVolume-0.01,0);
				if (ambience != null) {
					ambience.updateVolume((float)ambienceVolume/1.5f);
					if (ambienceVolume <= 0) {
						Minecraft.getMinecraft().getSoundHandler().stopSound(ambience.sound);
						ambience = null;
					}
				}
			}
			digammaDose = (float)Math.pow(HbmLivingProps.getDigamma(player)/10,0.666);
			int needle = 0;
			while (needle < texts.size()) {
				DigammaText text = texts.get(needle);
				text.timeElapsed += 0.05f;
				if (text.timeElapsed > text.lifetime)
					texts.remove(needle);
				else
					needle++;
			}
			timer = timer + 0.05f;
			if (timer >= timerMax) {
				timer = 0;
				timerMax = rand.nextFloat()*3f+1;
				if (rand.nextFloat()+0.1f < digammaDose) {
					String msg = I18nUtil.resolveKey("gui.digamma_message."+rand.nextInt(messageVariants));
					float offset = rand.nextFloat()-0.5f;
					texts.add(new DigammaText(
							msg,
							0.5f+offset*Math.max(0,1-msg.length()/60f),
							0.1f+rand.nextFloat()*0.8f,
							1f+rand.nextFloat(),
							5+rand.nextFloat()*4
					));
				}
			}
		}
		public static class DigammaText {
			final float x;
			final float y;
			final float scale;
			final String message;
			float timeElapsed = 0;
			final float lifetime;
			public DigammaText(String message,float x,float y,float scale,float lifetime) {
				this.message = message;
				this.x = x;
				this.y = y;
				this.scale = scale;
				this.lifetime = lifetime;
			}
		}
	}
	public static class HandlerClient {
		public static void iQuit(FMLServerStoppedEvent evt) {
			/*
				@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@%%%%%%@@@@@@@@@@@@@@@@@@@@@@%%@@@@@@@@@@@@@@@@@@@@@@@@@%*+++%@@@@@@@@
				@@@@@@@@@@%+%%%%%%%@%%@@@@@@@%%%@@@%%%==%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%##@@@@@@@@
				@@@@@@@@@@%+%%%%%%%%%%%%%%%%%%%%%%-:::::-*%%%%%%%%%%%%%%%%%%%%%%%%%%###*@@@@@@@@
				@@@@@@@@@@#+######%%%%%%%%%%%%%%%#:::::::*#%%%%%%##%###################*@@@@@@@@
				@@@@@@@@@@#+%%%%%%%%%%%%%%%%%%%%%%#=::-:-+%%%%%%%%%%%%%%###############*@@@@@@@@
				@@@@@@@@@@#+#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%####################***%@@@@@@@
				@@@@@@@@@*=--:::::::::-----------:::::::-:-=+++++++*####################@@@@@@@@
				@@@@@@@@@@@@%+-:::::::::::::::::::::::::::::#@@+##%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@%=::::::::::::::::::::::::::::-@@@#*%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@@%-:::::::::::::::::::::::::::-#@@@*#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@@%=:::::::::::::::::::::::::::-#@@@##%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@@%=::::::::::::::::--------::-+####%%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@%=:::::::::::::::--+%@@@@@%#%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@@+::::::::::::::::-#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@@*::::::::::::::::-*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@%=:::::::::::::::-=@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@%=::::::::::::::-=%@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				@@@@@@@@%#-::::::::::::::*%%%%%%%%%%%%%%%%%%@@@@@@@@@%@@@@@@@@@@@@@@@@@@@@@@@@@@
				%%%%%%%%%%%*=-:::::::::::-#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@%@@@@@@@@@@@@@@@@@
				%%%%%%%%%%#**+=--::-==++**#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@%%@%@@@@@@@@@@@@@@@@@
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@@%*%@@@%%%%%@@@@@@@@@@@@#%@@@@@@@@@@
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%#:+%%%--%@%%@@@%%@@%%@%:#@@@@@@
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*:%%:#%%%%%*-%*:%%@*:%@-*%#:*#@@@@@
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*:%%:#%%%%%+=%*:%%%*:%%-*%%:#%%%@@%
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*:%%#--+*-:=%%%:-+=-:%%-*%%--*%%%%%
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%+#%%%%%%%%%%%%%%%%%%%%%%
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
				%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			 */
			LeafiaShakecam.instances.clear();
		}
		Map<AddonBiome,Float> getBiomeRatios(Entity entity) {
			World world = entity.world;
			int mops = 0;
			Map<AddonBiome,Integer> mop = new HashMap<>();
			BlockPos pos = entity.getPosition();
			for (int ox = -3; ox <= 3; ox++) {
				for (int oz = -3; oz <= 3; oz++) {
					Biome biome = world.getBiome(pos.add(ox*5,0,oz*5));
					if (biome instanceof AddonBiome)
						mop.put((AddonBiome)biome,mop.getOrDefault((AddonBiome)biome,0)+1);
					else if (AddonConfig.schizoMode)
						mop.put(AddonBiomes.digamma,mop.getOrDefault(AddonBiomes.digamma,0)+1);
					mops++;
				}
			}
			Map<AddonBiome,Float> map = new HashMap<>();
			for (Entry<AddonBiome,Integer> entry : mop.entrySet())
				map.put(entry.getKey(),entry.getValue().floatValue()/mops);
			return map;
		}
		@SubscribeEvent
		public void overrideFog(EntityViewRenderEvent.RenderFogEvent event) {
			float density = GlStateManager.fogState.density;
			float start = GlStateManager.fogState.start;
			float end = GlStateManager.fogState.end;
			float ogDensity = density;
			float ogStart = start;
			float ogEnd = end;
			for (Entry<AddonBiome,Float> entry : getBiomeRatios(event.getEntity()).entrySet()) {
				{
					float delta = entry.getKey().getFogDensity(ogDensity)-density;
					density += delta*entry.getValue();
				}
				{
					float delta = entry.getKey().getFogStart(ogStart)-start;
					start += delta*entry.getValue();
				}
				{
					float delta = entry.getKey().getFogEnd(ogEnd)-end;
					end += delta*entry.getValue();
				}
			}
			//density = (float)Math.max(Math.pow(IdkWhereThisShitBelongs.darkness*(IdkWhereThisShitBelongs.dustDisplayTicks/30f),0.1)*4,density);
			if (density != ogDensity)
				LeafiaGls.setFogDensity(start);
			if (start != ogStart)
				LeafiaGls.setFogStart(start);
			if (end != ogEnd)
				LeafiaGls.setFogEnd(end);
		}
		@SubscribeEvent
		public void setFogColor(EntityViewRenderEvent.FogColors event) {
			Entity entity = event.getEntity();
			World world = entity.world;
			Vec3d viewport = ActiveRenderInfo.projectViewFromEntity(entity,event.getRenderPartialTicks());
			BlockPos viewportPos = new BlockPos(viewport);
			IBlockState viewportState = world.getBlockState(viewportPos);
			Vec3d inMaterialColor = viewportState.getBlock().getFogColor(world, viewportPos, viewportState, entity, new Vec3d(1,0,0), (float)event.getRenderPartialTicks());

			Vec3d col = new Vec3d(event.getRed(),event.getGreen(),event.getBlue());
			for (Entry<AddonBiome,Float> entry : getBiomeRatios(entity).entrySet()) {
				int code = entry.getKey().getFogColor();
				Vec3d add = new Vec3d(code>>>16&0xFF,code>>>8&0xFF,code&0xFF).scale(1/255d).subtract(col);
				double alpha = (1-(code>>24&0xFF)/255d)*entry.getValue();
				col = col.add(add.scale(alpha));
			}

			event.setRed((float) MathHelper.clamp(col.x/*+(1-IdkWhereThisShitBelongs.darkness)*IdkWhereThisShitBelongs.infernal*0.7/1.5*/,0,1));
			event.setGreen((float)MathHelper.clamp(col.y/*+(1-IdkWhereThisShitBelongs.darkness)*IdkWhereThisShitBelongs.infernal*0.4/1.5*/,0,1));
			event.setBlue((float)MathHelper.clamp(col.z/*+(1-IdkWhereThisShitBelongs.darkness)*IdkWhereThisShitBelongs.infernal*0.1/1.5*/,0,1));

			//event.setRed((float)inMaterialColor.x);
			//event.setGreen((float)inMaterialColor.y);
			//event.setBlue((float)inMaterialColor.z);
		}
		final Long2BooleanOpenHashMap ccpSupported = new Long2BooleanOpenHashMap();
		/// For calls before addInformation, see com.leafia.dev.machine.MachineTooltip.addInfoASM()
		@SubscribeEvent
		public void drawTooltip(ItemTooltipEvent event) {
			List<String> list = event.getToolTip();
			Item item = event.getItemStack().getItem();
			if (item instanceof ItemBlock ib) {
				Block block = ib.getBlock();
				if (block instanceof PWRComponentBlock) {
					list.add(TextFormatting.GRAY+"["+I18nUtil.resolveKey("trait.leafia.component.pwr")+"]");
					list.add(TextFormatting.GRAY+"-::"+TextFormatting.WHITE+I18nUtil.resolveKey("trait.leafia.component.pwr.desc"));
				} else if (block instanceof MSRArbitraryBlock || block instanceof MSRPlugBlock || block instanceof MSREjectorBlock || block instanceof MSRElementBlock) {
					list.add(TextFormatting.GRAY+"["+I18nUtil.resolveKey("trait.leafia.component.lftr")+"]");
					list.add(TextFormatting.GRAY+"-::"+TextFormatting.WHITE+I18nUtil.resolveKey("trait.leafia.component.lftr.desc"));
				}
			}
			if (event.getFlags().isAdvanced() && item.getCreativeTab() != null) {
				list.add(TextFormatting.GREEN+"Creative Tab ID:");
				list.add(TextFormatting.DARK_GREEN+" - "+item.getCreativeTab().tabLabel);
			}
			if (item instanceof ItemBlock ib && StructuralIntegrityHandler.AUTOMATIC) {
				IBlockState state = null;
				try {
					state = ib.getBlock().getStateFromMeta(ib.getMetadata(event.getItemStack()));
				} catch (Exception e) {
					state = ib.getBlock().getDefaultState();
				}
				int mass = StructuralIntegrityHandler.getMass(state,null,null);
				int glue = StructuralIntegrityHandler.getGlue(state,null,null);
				list.add(TextFormatting.RED+"Structural Integrity:");
				list.add(TextFormatting.DARK_RED+" - Mass: "+((mass != -1) ? mass : "?"));
				list.add(TextFormatting.DARK_RED+" - Glue: "+((glue != -1) ? glue : "?"));
			}
			if (item instanceof ItemBlock ib) {
				if (ib.getBlock() instanceof ITileEntityProvider prov) {
					if (!ccpSupported.containsKey(prov.hashCode())) {
						if (!(prov instanceof BlockControlPanel)) {
							TileEntity fuckoff = null;
							try {
								fuckoff = prov.createNewTileEntity(Minecraft.getMinecraft().world,12);
							} catch (Exception ignored) {}
							ccpSupported.put(prov.hashCode(),fuckoff instanceof IControllable);
						}
					}
					if (ccpSupported.get(prov.hashCode()))
						list.add(TextFormatting.GREEN+"[CCP Compatible]");
				}
			}
		}
		@SubscribeEvent
		public void blockColorsEvent(ColorHandlerEvent.Block evt) {
			FFDuctStandard.registerColorHandler(evt);
			AmatDuctStandard.registerColorHandler(evt);
		}

		private void registerModel(Item item,int meta) {
			if (item.getTileEntityItemStackRenderer() instanceof TEISRBase) {
				return;
			}
			if (item instanceof LeafiaRodItem.EmptyLeafiaRod) {
				ModelLoader.setCustomModelResourceLocation(item, 14, new ModelResourceLocation(item.getRegistryName() + "_overlay_bf", "inventory"));
				ModelLoader.setCustomModelResourceLocation(item, 15, new ModelResourceLocation(item.getRegistryName() + "_overlay", "inventory"));
				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName() + "_empty", "inventory"));
			} else if(item instanceof IHasCustomModel) {
				ModelLoader.setCustomModelResourceLocation(item, meta, ((IHasCustomModel) item).getResourceLocation());
			} else {
				ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}

		private void registerBlockModel(Block block, int meta) {
			registerModel(Item.getItemFromBlock(block), meta);
		}
		@SubscribeEvent
		public void registerModels(ModelRegistryEvent evt) {
			for(Item item : AddonItems.ALL_ITEMS) {
				if (!(item instanceof IDynamicModels))
					registerModel(item, 0);
			}
			for(Block block : AddonBlocks.ALL_BLOCKS) {
				registerBlockModel(block, 0);
			}
		}
		void renderBox(double ratio,float alpha) {
			LeafiaBrush brush = LeafiaBrush.instance;
			brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION_COLOR);
			{
				// bottom line
				float gb = ratio <= 0 ? 0 : 1;
				brush.addVertexWithColor(-0.5,-0.5,-0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(0.5,-0.5,-0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(0.5,-0.5,-0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(0.5,-0.5,0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(0.5,-0.5,0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(-0.5,-0.5,0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(-0.5,-0.5,0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(-0.5,-0.5,-0.5,1,gb,gb,alpha);
			}
			{
				// side lines
				for (int xo = -1; xo <= 1; xo+=2) {
					for (int zo = -1; zo <= 1; zo+=2) {
						if (ratio > 0) {
							brush.addVertexWithColor(-xo/2d,-0.5,-zo/2d,1,1,1,alpha);
							brush.addVertexWithColor(-xo/2d,-0.5+ratio,-zo/2d,1,1,1,alpha);

							brush.addVertexWithColor(xo/2d,-0.5,-zo/2d,1,1,1,alpha);
							brush.addVertexWithColor(xo/2d,-0.5+ratio,-zo/2d,1,1,1,alpha);

							brush.addVertexWithColor(-xo/2d,-0.5,zo/2d,1,1,1,alpha);
							brush.addVertexWithColor(-xo/2d,-0.5+ratio,zo/2d,1,1,1,alpha);

							brush.addVertexWithColor(xo/2d,-0.5,zo/2d,1,1,1,alpha);
							brush.addVertexWithColor(xo/2d,-0.5+ratio,zo/2d,1,1,1,alpha);
						}
						if (ratio < 1) {
							brush.addVertexWithColor(-xo/2d,-0.5+ratio,-zo/2d,1,0,0,alpha);
							brush.addVertexWithColor(-xo/2d,0.5,-zo/2d,1,0,0,alpha);

							brush.addVertexWithColor(xo/2d,-0.5+ratio,-zo/2d,1,0,0,alpha);
							brush.addVertexWithColor(xo/2d,0.5,-zo/2d,1,0,0,alpha);

							brush.addVertexWithColor(-xo/2d,-0.5+ratio,zo/2d,1,0,0,alpha);
							brush.addVertexWithColor(-xo/2d,0.5,zo/2d,1,0,0,alpha);

							brush.addVertexWithColor(xo/2d,-0.5+ratio,zo/2d,1,0,0,alpha);
							brush.addVertexWithColor(xo/2d,0.5,zo/2d,1,0,0,alpha);
						}
					}
				}
			}
			{
				// top line
				float gb = ratio >= 1 ? 1 : 0;
				brush.addVertexWithColor(-0.5,0.5,-0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(0.5,0.5,-0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(0.5,0.5,-0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(0.5,0.5,0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(0.5,0.5,0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(-0.5,0.5,0.5,1,gb,gb,alpha);

				brush.addVertexWithColor(-0.5,0.5,0.5,1,gb,gb,alpha);
				brush.addVertexWithColor(-0.5,0.5,-0.5,1,gb,gb,alpha);
			}
			brush.draw();
		}
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void renderWorld(RenderWorldLastEvent evt) {
			if (AddonConfig.schizoMode) { // fuck the sky
				Minecraft.getMinecraft().renderEngine.bindTexture(AddonBase.solid);
				LeafiaGls.color(0,0,0);
				LeafiaGls.pushMatrix();
				LeafiaGls.scale(-1000);
				ResourceManager.sphere_ruv.renderAll();
				LeafiaGls.popMatrix();
				LeafiaGls.color(1,1,1);
			}
			AddonRainRender.INSTANCE.render(evt.getPartialTicks());
			if (StructuralIntegrityHandler.AUTOMATIC) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				ItemStack stack = player.getHeldItemMainhand();
				if (stack.getItem() instanceof ItemBlock ib) {
					Minecraft mc = Minecraft.getMinecraft();
					World world = mc.world;
					RayTraceResult mop = mc.objectMouseOver;
					if (mop != null && mop.typeOfHit == mop.typeOfHit.BLOCK) {
						BlockPos pos = mop.getBlockPos().offset(mop.sideHit);
						SimulationData data = StructuralIntegrityHandler.handleBlockSimulate(world,pos,ib.getBlock().getStateFromMeta(ib.getDamage(stack)));
						if (data != null) {
							Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
							float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
							double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
							double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
							double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
							LeafiaGls.pushMatrix();
							LeafiaGls.translate(pos.getX()+0.5-x,pos.getY()+0.5-y,pos.getZ()+0.5-z);
							LeafiaGls.inStack(()->{
								LeafiaGls.color(1,1,1,1);
								LeafiaGls.enableAlpha();
								LeafiaGls.alphaFunc(GL11.GL_GREATER,0);
								LeafiaGls.disableLighting();
								LeafiaGls.enableBlend();
								LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
								LeafiaGls.disableTexture2D();
								double offset = Math.floorMod(System.currentTimeMillis(),1000L)/1000d;
								double r = 1-data.maxRatio;
								if (data.maxRatio < 1) {
									LeafiaGls.pushMatrix();
									LeafiaGls.scale(0.75);
									renderBox(r,0.75f);
									LeafiaGls.popMatrix();
									double pulsate = offset/0.75;
									if (pulsate <= 1) {
										LeafiaGls.pushMatrix();
										LeafiaGls.scale(0.75+pulsate*0.15);
										renderBox(r,(float)Math.pow(0.5-pulsate/2,1.5));
										LeafiaGls.popMatrix();
									}
								} else {
									LeafiaGls.pushMatrix();
									double psin = Math.max(Math.sin(offset*Math.PI/0.75),0);
									LeafiaGls.scale(0.75+0.1*(float)psin);
									renderBox(r,0.75f);
									LeafiaGls.popMatrix();
								}
							});
							LeafiaGls.popMatrix();
						}
					}
				}
			}
			TopRender.main(evt);
		}
		public static int dfcFlashTicks = 0;
		@SubscribeEvent
		public void onOverlayRenderPost(RenderGameOverlayEvent.Post evt) {
			if(evt.getType() == ElementType.ALL) {
				ScaledResolution resolution = evt.getResolution();
				Digamma.drawVignette(resolution,evt.getPartialTicks());
				if (dfcFlashTicks > 0) { // dfc flash
					LeafiaBrush brush = LeafiaBrush.instance;
					Minecraft.getMinecraft().renderEngine.bindTexture(AddonBase.solid);
					LeafiaGls.disableAlpha();
					LeafiaGls.color(1,1,1,Math.max(0,Math.min(1,(dfcFlashTicks-evt.getPartialTicks())/100f)));
					LeafiaGls.enableBlend();
					LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE);
					brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.POSITION_TEX);
					brush.addVertexWithUV(0,resolution.getScaledHeight_double(),0,0,1);
					brush.addVertexWithUV(resolution.getScaledWidth_double(),resolution.getScaledHeight_double(),0,1,1);
					brush.addVertexWithUV(resolution.getScaledWidth_double(),0,0,1,0);
					brush.addVertexWithUV(0,0,0,0,0);
					brush.draw();
					LeafiaGls.enableAlpha();
					LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
				}
			}
		}
		public static final ResourceLocation addonBadges = new ResourceLocation("leafia","textures/badges.png");
		public static void renderBadges(ScaledResolution res, Gui gui) {

			GL11.glPushMatrix();

			Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableDepth();
			GlStateManager.depthMask(false);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

			GlStateManager.disableAlpha();

			Minecraft.getMinecraft().renderEngine.bindTexture(addonBadges);

			int offsetX = 2;
			int offsetY = 2;
			int width = 26;

			boolean true528 = GeneralConfig.true528();
			boolean trueExp = GeneralConfig.trueExp();
			boolean trueRam = MobConfig.trueRam();
			boolean true328 = true528 && trueExp && trueRam;

			if(true528) { offsetX += width; }
			if(trueExp) { offsetX += width; }
			if(trueRam) { offsetX += width; }
			if(true328) { offsetX += width; }
			boolean schz = AddonConfig.schizoMode;
			boolean grav = StructuralIntegrityHandler.AUTOMATIC;
			boolean is128 = true328 && schz && grav;
			boolean ctcl = GeneralConfig.enableCataclysm;
			boolean isNeg72 = is128 && ctcl;
			if(schz) { gui.drawTexturedModalRect(offsetX, offsetY, 0, 0, 24, 8); offsetX += width; }
			if(grav) { gui.drawTexturedModalRect(offsetX, offsetY, 24, 0, 24, 8); offsetX += width; }
			if(is128) { gui.drawTexturedModalRect(offsetX, offsetY, 0, 8, 24, 8); offsetX += width; }
			if(ctcl) { gui.drawTexturedModalRect(offsetX, offsetY, 48, 0, 24, 8); offsetX += width; }
			if(isNeg72) { gui.drawTexturedModalRect(offsetX, offsetY, 24, 8, 24, 8); offsetX += width; }

			Minecraft.getMinecraft().renderEngine.bindTexture(Gui.ICONS);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.enableDepth();
			GlStateManager.depthMask(true);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.popMatrix();
		}
		@SubscribeEvent
		public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
			if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
				if(ClientConfig.BADGES_HUD.get()) renderBadges(event.getResolution(), Minecraft.getMinecraft().ingameGUI);
			}
			if(event.getType() == ElementType.CROSSHAIRS) {
				Minecraft mc = Minecraft.getMinecraft();
				World world = mc.world;
				RayTraceResult mop = mc.objectMouseOver;

				if (mop != null && mop.typeOfHit == mop.typeOfHit.BLOCK) {
					if (mc.player.getHeldItemMainhand().getItem() == ModItems.detonator_multi) {
						BlockPos core = mop.getBlockPos();
						if (world.getBlockState(mop.getBlockPos()).getBlock() instanceof BlockDummyable dummyable)
							core = dummyable.findCore(world,mop.getBlockPos());
						if (core != null) {
							Chunk chunk = world.getChunk(core);
							TileEntity entity = chunk.getTileEntity(core,Chunk.EnumCreateEntityType.CHECK);
							if (entity instanceof IControllable && !(entity instanceof TileEntityControlPanel)) {
								String s = "Custom Control Panel compatible";
								mc.fontRenderer.drawString(
										s,
										event.getResolution().getScaledWidth()/2f-mc.fontRenderer.getStringWidth(s)/2f,
										event.getResolution().getScaledHeight()/6f*2,
										0x55FF55,
										true
								);
								LeafiaGls.color(1,1,1);
							}
						}
					}
					if (mc.player.getHeldItemOffhand().getItem() == AddonItems.wand_v) {
						Chunk chunk = world.getChunk(mop.getBlockPos());
						TileEntity entity = chunk.getTileEntity(mop.getBlockPos(),Chunk.EnumCreateEntityType.CHECK);
						if (entity != null) {
							NBTTagCompound nbt = new NBTTagCompound();
							entity.writeToNBT(nbt);
							LeafiaGls.pushMatrix();
							LeafiaGls.scale(0.6,0.6,1);
							mc.fontRenderer.drawStringWithShadow("Replicated blockdata",4,4,LeafiaUtil.colorFromTextFormat(TextFormatting.GREEN));
							int textX = 10;
							int textY = 4;
							List<Triplet<Integer,Integer,List<Pair<String,NBTBase>>>> stack = new ArrayList<>();
							stack.add(new Triplet<>(0,0,new ArrayList<>()));
							for (String key : nbt.getKeySet()) {
								stack.get(0).getC().add(new Pair<>(key,nbt.getTag(key)));
							}
							while (stack.size() > 0) {
								Triplet<Integer,Integer,List<Pair<String,NBTBase>>> stackItem = stack.get(stack.size()-1);
								List<Pair<String,NBTBase>> compound = stackItem.getC();
								if (compound.size() > 0) {
									Pair<String,NBTBase> entry = compound.remove(0);
									textY += 10;
									String lineTxt = (entry.getA() != null) ? TextFormatting.YELLOW+"["+entry.getA()+"] " : "["+stackItem.getB()+"] ";
									stackItem.setB(stackItem.getB()+1);
									NBTBase value = entry.getB();
									if (value instanceof NBTTagByte)
										lineTxt += TextFormatting.BLUE+""+((NBTTagByte) value).getByte();
									if (value instanceof NBTTagShort)
										lineTxt += TextFormatting.DARK_AQUA+""+((NBTTagShort) value).getShort();
									if (value instanceof NBTTagInt)
										lineTxt += TextFormatting.AQUA+""+((NBTTagInt) value).getInt();
									if (value instanceof NBTTagLong)
										lineTxt += TextFormatting.GOLD+""+((NBTTagLong) value).getLong();
									if (value instanceof NBTTagFloat)
										lineTxt += TextFormatting.GREEN+""+((NBTTagFloat) value).getFloat()+"f";
									if (value instanceof NBTTagDouble)
										lineTxt += TextFormatting.RED+""+((NBTTagDouble) value).getDouble()+"d";
									if (value instanceof NBTTagByteArray)
										lineTxt += TextFormatting.DARK_GRAY+""+value;
									if (value instanceof NBTTagIntArray)
										lineTxt += TextFormatting.DARK_GRAY+""+value;
									if (value instanceof NBTTagLongArray)
										lineTxt += TextFormatting.GRAY+""+value;
									if (value instanceof NBTTagList) {
										lineTxt += TextFormatting.RESET+"[";
										List<Pair<String,NBTBase>> subCompound = new ArrayList<>();
										for (NBTBase item : ((NBTTagList) value)) {
											subCompound.add(new Pair<>(null,item));
										}
										stack.add(new Triplet<>(1,0,subCompound));
									}
									if (value instanceof NBTTagString)
										lineTxt += TextFormatting.LIGHT_PURPLE+""+((NBTTagString) value).getString();
									if (value instanceof NBTTagCompound) {
										lineTxt += "{";
										List<Pair<String,NBTBase>> subCompound = new ArrayList<>();
										NBTTagCompound nbtValue = (NBTTagCompound) value;
										for (String key : nbtValue.getKeySet()) {
											subCompound.add(new Pair<>(key,nbtValue.getTag(key)));
										}
										stack.add(new Triplet<>(2,0,subCompound));
									}
									mc.fontRenderer.drawStringWithShadow(lineTxt,textX,textY,-1);
									if (value instanceof NBTTagCompound)
										textX += 6;
									if (value instanceof NBTTagList)
										textX += 6;
								}
								if (stack.get(stack.size()-1).getC().size() <= 0) {
									switch(stack.get(stack.size()-1).getA()) {
										case 1:
											textX -= 6;
											textY += 10;
											mc.fontRenderer.drawStringWithShadow("]",textX,textY,LeafiaUtil.colorFromTextFormat(TextFormatting.WHITE));
											break;
										case 2:
											textX -= 6;
											textY += 10;
											mc.fontRenderer.drawStringWithShadow("}",textX,textY,LeafiaUtil.colorFromTextFormat(TextFormatting.YELLOW));
											break;
									}
									stack.remove(stack.size()-1);
								}
							}
							LeafiaGls.popMatrix();
						}
					}
				}
			}
		}
		public static boolean backupsWarning = false;
		public static boolean seenWarning = false;
		public static boolean assentialWarning = false;
		public static boolean seenAssentialWarning = false;
		@SubscribeEvent
		public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiCTMWarning) return;
			if (event.getGui() instanceof GuiCTMWarning) {
				seenWarning = false;
				return;
			}
			if (event.getGui() instanceof net.minecraft.client.gui.GuiMainMenu) {
				if (assentialWarning && !seenAssentialWarning) {
					GuiAssentialWarning.text.add("Essential is NOT compatible with LCA,");
					GuiAssentialWarning.text.add("or literally any addons that specifies Mixin version > 0.8.4");
					GuiAssentialWarning.text.add("due to them using the dumbest solution one can ever think of.");
					GuiAssentialWarning.text.add("");
					GuiAssentialWarning.text.add("Major game corruptions are expected.");
					GuiAssentialWarning.text.add("Do not report any bugs occurred with Essential installed.");
					GuiAssentialWarning.text.add("");
					GuiAssentialWarning.text.add("I suggest you just close the game and get rid of that junker.");
					Minecraft.getMinecraft().displayGuiScreen(new GuiAssentialWarning());
					seenAssentialWarning = true;
					return;
				}
				if (backupsWarning && !seenWarning) {
					GuiBackupsWarning.text.add("Backups is recommended as the addon is highly unstable.");
					GuiBackupsWarning.downloadButtonIndex = GuiBackupsWarning.text.size();
					GuiBackupsWarning.text.add("Click to download Backups");
					GuiBackupsWarning.text.add("");
					GuiBackupsWarning.text.add("Press any key to continue");
					Minecraft.getMinecraft().displayGuiScreen(new GuiBackupsWarning());
					seenWarning = true;
				}
			}
		}
		public static float getViewADS(EntityPlayer player) {
			if (player.isSneaking()) {
				boolean canADS = true;
				ItemStack main = player.getHeldItemMainhand();
				ItemStack sub = player.getHeldItemOffhand();
				if ((!main.isEmpty() || !sub.isEmpty()) && (main.isEmpty() != sub.isEmpty())) {
					Item holding = main.isEmpty() ? sub.getItem() : main.getItem();
					if (holding instanceof IADSWeapon weapon) {
						if (weapon.getADS() != 1f)
							return weapon.getADS()*(main.isEmpty()  ? -1 : 1)*(player.getPrimaryHand().equals(EnumHandSide.RIGHT) ? 1 : -1);
					}
				}
			}
			return 0;
		}
		@SubscribeEvent
		public void fovUpdate(FOVUpdateEvent e){
			EntityPlayer player = e.getEntity();
			float multiplier = 1.0F;
			//if(player.getHeldItemMainhand().getItem() == Armory.gun_supershotgun && ItemGunShotty.hasHookedEntity(player.world, player.getHeldItemMainhand())) {
			//	multiplier *= 1.1F;
			//}
			float viewADS = getViewADS(player);
			if (viewADS != 0)
				multiplier *= Math.abs(viewADS);
			//multiplier *= IdkWhereThisShitBelongs.fovM;
			float fovMultiplier = 1-Digamma.digammaDose*0.21428571428f;

			e.setNewfov(e.getNewfov()*multiplier*fovMultiplier);
		}

		public static final Logger LOGGER = LogManager.getLogger();
		final Set<TileEntity> validatedTEs = new HashSet<>();
		private static final Map<String,BigBruh> shaderGroups = new HashMap<>();
		int lastW = 0;
		int lastH = 0;
		public HandlerClient() {
			LeafiaShakecam.noise = new NoiseGeneratorPerlin(new Random(),1);
			if (!AddonConfig.disableLCAShaders) {
				this.addShader("tom",new ResourceLocation("leafia:shaders/help/tom_desat.json"));
				this.addShader("nuclear",new ResourceLocation("leafia:shaders/help/nuclear.json"));
				this.addShader("drx",new ResourceLocation("leafia:shaders/help/digamma.json"));
			}
		}
		public static float rx = 0;
		public static float ry = 0;
		public static float rz = 0;
		@SubscribeEvent
		public void renderTick(RenderTickEvent e){
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				if (e.phase == Phase.END) {
					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.loadIdentity();
					GL11.glTranslatef(rx,ry,rz);
					rx = 0;
					ry = 0;
					rz = 0;
					LeafiaGls.matrixMode(5888);
					if (!AddonConfig.disableLCAShaders) {
						boolean needsUpdate = false;
						for (BigBruh shaderGroup : shaderGroups.values()) {
							LeafiaGls.matrixMode(5890);
							LeafiaGls.pushMatrix();
							LeafiaGls.loadIdentity();
							Minecraft mc = Minecraft.getMinecraft();
							Framebuffer mainCanvas = mc.getFramebuffer();
							if (shaderGroup != null) {
								if (lastW != mainCanvas.framebufferWidth || lastH != mainCanvas.framebufferHeight || needsUpdate) {
									lastW = mc.getFramebuffer().framebufferWidth;
									lastH = mc.getFramebuffer().framebufferHeight;
									shaderGroup.createBindFramebuffers(mainCanvas.framebufferWidth,mainCanvas.framebufferHeight);
									needsUpdate = true;
								}
								shaderGroup.render(e.renderTickTime);
							}
							LeafiaGls.popMatrix();
						}
						LeafiaGls.matrixMode(5888);
					}
				/*
				//LeafiaGls.color(1.0F, 1.0F, 1.0F, 1.0F);
				LeafiaGls.enableBlend();
				LeafiaGls.enableDepth();
				LeafiaGls.enableAlpha();
				//LeafiaGls.enableFog();
				LeafiaGls.enableLighting();
				LeafiaGls.enableColorMaterial();*/
				}
			}
		}
		void addShader(String key,ResourceLocation resourceLocationIn)
		{
			if (OpenGlHelper.shadersSupported) {
				if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
					ShaderLinkHelper.setNewStaticShaderLinkHelper();
				}
				LOGGER.info("Trying to load shader: {}",resourceLocationIn);
				Minecraft mc = Minecraft.getMinecraft();
				lastW = mc.getFramebuffer().framebufferWidth;
				lastH = mc.getFramebuffer().framebufferHeight;
				try {
					BigBruh shaderGroup = new BigBruh(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), resourceLocationIn);
					shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
					shaderGroups.put(key,shaderGroup);
					LOGGER.warn("Successfully put shader: {}", resourceLocationIn);
				} catch (IOException ioexception) {
					LOGGER.warn("Failed to load shader: {}", resourceLocationIn, ioexception);
				} catch (JsonSyntaxException jsonsyntaxexception) {
					LOGGER.warn("Failed to load shader: {}", resourceLocationIn, jsonsyntaxexception);
				}
			}
		}
		@SubscribeEvent
		public void clientTick(ClientTickEvent e) {

			if(e.phase == Phase.END) {
				if (Minecraft.getMinecraft().world != null)
					LeafiaPassiveLocal.priorTick(Minecraft.getMinecraft().world);
			} else {
				if(Minecraft.getMinecraft().world != null){
					LeafiaPassiveLocal.onTick(Minecraft.getMinecraft().world);
				}
			}
			if(Minecraft.getMinecraft().player != null){
				if (e.phase == Phase.END) {
					if (Minecraft.getMinecraft().world != null) {
						List<TileEntity> entities = Minecraft.getMinecraft().world.loadedTileEntityList;
						BlockPos pos = Minecraft.getMinecraft().player.getPosition();
						if (!validatedTEs.isEmpty()) {
							Set<TileEntity> removalQueue = new HashSet<>();
							for (TileEntity entity : validatedTEs) {
								if (!entities.contains(entity) || !(entity instanceof LeafiaPacketReceiver))
									removalQueue.add(entity);
								else if (!entity.isInvalid()) {
									LeafiaPacketReceiver receiver = (LeafiaPacketReceiver)entity;
									if (entity.getPos().getDistance(pos.getX(),pos.getY(),pos.getZ()) > receiver.affectionRange()*1.25) {
										removalQueue.add(entity);
									}
								}
							}
							for (TileEntity entity : removalQueue) {
								validatedTEs.remove(entity);
							}
						}
						for (int i = 0; i < entities.size(); i++) {
							TileEntity entity = entities.get(i);
							if (!entity.isInvalid() && entity instanceof LeafiaPacketReceiver && !validatedTEs.contains(entity)) {
								LeafiaPacketReceiver receiver = (LeafiaPacketReceiver)entity;
								if (entity.getPos().getDistance(pos.getX(),pos.getY(),pos.getZ()) <= receiver.affectionRange()) {
									validatedTEs.add(entity);
									LeafiaPacket._validate(entity);
								}
							}
						}
					}
					LeafiaShakecam.localTick();
					//IdkWhereThisShitBelongs.localTick();
					EntityNukeFolkvangr.FolkvangrVacuumPacket.Handler.localTick();
					for (String s : shaderGroups.keySet()) {
						BigBruh shader = shaderGroups.get(s);
						switch(s) {
							case "drx":
								shader.accessor.get("intensity").set(Digamma.digammaDose);
								break;
							case "tom":
								//shader.accessor.get("intensity").set((float)(IdkWhereThisShitBelongs.darkness)*(IdkWhereThisShitBelongs.dustDisplayTicks/30f)/2f);
								break;
							case "nuclear":
								shader.accessor.get("blur").set(LeafiaShakecam.blurSum);
								shader.accessor.get("bloom").set(LeafiaShakecam.bloomSum);
								break;
						}
					}
				}
			}
		}
		@SubscribeEvent
		public void cameraSetup(EntityViewRenderEvent.CameraSetup e){
			//IdkWhereThisShitBelongs.shakeCam();
			LeafiaShakecam.shakeCam();
		}
        @SubscribeEvent
        public void onModelBaking(ModelBakeEvent e) {
            ResourceInit.init();
        }
	}
	public static class Unsorted {
        @SubscribeEvent
        public void worldInit(Load evt) {
            if (evt.getWorld().isRemote)
                CatwalkRailingBase.initRenderMaskCache(evt.getWorld());
        }

        @SubscribeEvent
        public void worldUnload(Unload evt) {
            if (evt.getWorld().isRemote)
                CatwalkRailingBase.clearRenderMaskCache(evt.getWorld());
        }

        @SubscribeEvent
        public void chunkUnload(ChunkEvent.Unload evt) {
            if (evt.getWorld().isRemote)
                CatwalkRailingBase.invalidateRenderMaskCacheChunk(evt.getWorld(),evt.getChunk().x,evt.getChunk().z);
        }

		/**
		 * Thank you forge for naming it like this
		 * <p>Yes, {@link RenderGameOverlayEvent.Text} is the event solely for debug screen, despite the radically confusing name just "Text".
		 * <p>Good job, forge. I'll kindly prepare 9800 schrabidium missiles to serve you.
		 */
		@SubscribeEvent
		public void dammit(RenderGameOverlayEvent.Text debug) {
			//LeafiaGeneralLocal.injectDebugInfoLeft(debug.getLeft());
		}

		/*static final Field mapRegisteredSprites;
		static {
			try {
				mapRegisteredSprites = TextureMap.class.getDeclaredField(
						FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(
								"net.minecraft.client.renderer.texture.TextureMap",
								"mapRegisteredSprites",//"field_110574_e",
								"Ljava/util/Map;"
						)
				);
				mapRegisteredSprites.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new LeafiaDevFlaw(e);
			}
		}

		TextureMap manager;
		Map<String,TextureAtlasSprite> map;
		private void redirectNTMSprite(String s) {
			ResourceLocation loc = new ResourceLocation("leafia",s);
			TextureAtlasSprite sprite = manager.registerSprite(loc);
			map.remove(loc.toString());
			map.put(new ResourceLocation("hbm",s).toString(),sprite);
		}

		@SubscribeEvent
		public void textureStitch(TextureStitchEvent.Pre evt) {
			try {
				Map<String,TextureAtlasSprite> map = (Map<String,TextureAtlasSprite>)mapRegisteredSprites.get(evt.getMap());
				this.manager = evt.getMap();
				this.map = map;
				{
					for (int z = 0; z <= 6; z++) {
						redirectNTMSprite("blocks/contamination/grass/waste_grass_side_"+z);
						redirectNTMSprite("blocks/contamination/grass/waste_grass_top_"+z);
					}
					for (int z = 0; z <= 6; z++)
						redirectNTMSprite("blocks/contamination/grass_tall/waste_grass_tall_"+z);
				}
				this.manager = null;
				this.map = null;
			} catch (IllegalAccessException e) {
				throw new LeafiaDevFlaw(e);
			}
		}*/ // well that didnt work out. Fuck off!

		@SubscribeEvent
		public void onGetEntityCollision(GetCollisionBoxesEvent evt) {
			if (evt.getEntity() == null) return;
			List<AxisAlignedBB> list = evt.getCollisionBoxesList();
			List<Entity> list1 = evt.getWorld().getEntitiesWithinAABBExcludingEntity(evt.getEntity(), evt.getAabb().grow((double)02.25F));
			for(int i = 0; i < list1.size(); ++i) {
				Entity entity = (Entity)list1.get(i);
				if (!evt.getEntity().isRidingSameEntity(entity)) {
					if (entity instanceof IEntityCustomCollision) {
						List<AxisAlignedBB> aabbs = ((IEntityCustomCollision)entity).getCollisionBoxes(evt.getEntity());
						if (aabbs == null) continue;
						for (AxisAlignedBB aabb : aabbs) {
							if (aabb != null && aabb.intersects(aabb))
								list.add(aabb);
						}
					}
				}
			}
		}
	}
	public static class Fluids {
		/*@SubscribeEvent
		public void filled(FluidFillingEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void spilled(FluidSpilledEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}
		@SubscribeEvent
		public void moved(FluidMotionEvent evt) {
			LeafiaDebug.debugLog(evt.getWorld(),"SCREW YOU! "+evt.getClass().getSimpleName());
			//LeafiaDebug.debugPos(evt.getWorld(),evt.getPos(),3,0x00CCFF,evt.getClass().getSimpleName(),evt.getFluid().getFluid().getName());
		}*/
	}
}
