package com.leafia.eventbuses;

import com.custom_hbm.GuiBackupsWarning;
import com.google.gson.JsonSyntaxException;
import com.hbm.blocks.ILookOverlay;
import com.hbm.capability.HbmLivingProps;
import com.hbm.interfaces.IHasCustomModel;
import com.hbm.items.IDynamicModels;
import com.hbm.render.GuiCTMWarning;
import com.custom_hbm.util.LCETuple.*;
import com.hbm.render.item.TEISRBase;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodBakedModel;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodRender;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.gear.IADSWeapon;
import com.leafia.contents.gear.utility.FuzzyIdentifierBakedModel;
import com.leafia.contents.gear.utility.FuzzyIdentifierRender;
import com.leafia.contents.gear.utility.FuzzyIdentifierItem;
import com.leafia.contents.machines.reactors.lftr.components.arbitrary.MSRArbitraryBlock;
import com.leafia.contents.machines.reactors.lftr.components.ejector.MSREjectorBlock;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementBlock;
import com.leafia.contents.machines.reactors.lftr.components.plug.MSRPlugBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.contents.network.ff_duct.FFDuctStandard;
import com.leafia.contents.network.pipe_amat.AmatDuctStandard;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.init.ItemRendererInit;
import com.leafia.init.ResourceInit;
import com.leafia.passive.LeafiaPassiveLocal;
import com.leafia.passive.effects.LeafiaShakecam;
import com.leafia.passive.rendering.TopRender;
import com.leafia.shit.leafiashader.BigBruh;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.IEntityCustomCollision;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static com.hbm.main.client.NTMClientRegistry.swapModels;

public class LeafiaClientListener {
	public static class Digamma {
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
		public static void update() {
			digammaDose = (float)Math.pow(HbmLivingProps.getDigamma(Minecraft.getMinecraft().player)/10,0.666);
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
		}
		@SubscribeEvent
		public void modelBaking(ModelBakeEvent evt) {
			IRegistry<ModelResourceLocation,IBakedModel> reg = evt.getModelRegistry();
			for(Entry<Item,TEISRBase> entry : ItemRendererInit.renderers.entrySet()){
				swapModels(entry.getKey(), reg);
			}
			{
				Object object = evt.getModelRegistry().getObject(FuzzyIdentifierItem.fuzzyModel);
				if (object instanceof IBakedModel) {
					IBakedModel model = (IBakedModel) object;
					FuzzyIdentifierRender.INSTANCE.itemModelFuzzy = model;
					evt.getModelRegistry().putObject(FuzzyIdentifierItem.fuzzyModel,new FuzzyIdentifierBakedModel());
				}
			}
			{
				for (LeafiaRodItem item : LeafiaRodItem.fromResourceMap.values()) {
					if (item.specialRodModel != null) {
						Object object = evt.getModelRegistry().getObject(item.specialRodModel);
						if(object instanceof IBakedModel) {
							item.bakedSpecialRod = (IBakedModel)object;
						}
						evt.getModelRegistry().putObject(item.specialRodModel, new LeafiaRodBakedModel());
					}
				}
			}
			{
				Object object = evt.getModelRegistry().getObject(LeafiaRodItem.rodModel);
				if(object instanceof IBakedModel) {
					IBakedModel model = (IBakedModel) object;
					LeafiaRodRender.INSTANCE.itemModel = model;
					evt.getModelRegistry().putObject(LeafiaRodItem.rodModel, new LeafiaRodBakedModel());
				}
			}
		}

		@SubscribeEvent
		public void blockColorsEvent(ColorHandlerEvent.Block evt) {
			FFDuctStandard.registerColorHandler(evt);
			AmatDuctStandard.registerColorHandler(evt);
		}

		private void registerModel(Item item,int meta) {
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
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void renderWorld(RenderWorldLastEvent evt) {
			TopRender.main(evt);
		}
		@SubscribeEvent
		public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
			if(event.getType() == ElementType.CROSSHAIRS) {
				Minecraft mc = Minecraft.getMinecraft();
				World world = mc.world;
				RayTraceResult mop = mc.objectMouseOver;

				if (mop != null && mop.typeOfHit == mop.typeOfHit.BLOCK) {
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
		@SubscribeEvent
		public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
			if (seenWarning) return;
			if (!backupsWarning) return;
			if (Minecraft.getMinecraft().currentScreen instanceof GuiCTMWarning) return;
			if (event.getGui() instanceof GuiCTMWarning) {
				seenWarning = false;
				return;
			}
			if (event.getGui() instanceof net.minecraft.client.gui.GuiMainMenu) {
				if (backupsWarning) {
					GuiBackupsWarning.text.add("Backups is recommended as the addon is highly unstable.");
					GuiBackupsWarning.downloadButtonIndex = GuiBackupsWarning.text.size();
					GuiBackupsWarning.text.add("Click to download Backups");
				}
				GuiBackupsWarning.text.add("");
				GuiBackupsWarning.text.add("Press any key to continue");
				Minecraft.getMinecraft().displayGuiScreen(new GuiBackupsWarning());
				seenWarning = true;
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

			e.setNewfov(e.getFov()*multiplier*fovMultiplier);
		}

		public static final Logger LOGGER = LogManager.getLogger();
		final Set<TileEntity> validatedTEs = new HashSet<>();
		private static final Map<String,BigBruh> shaderGroups = new HashMap<>();
		int lastW = 0;
		int lastH = 0;
		public HandlerClient() {
			LeafiaShakecam.noise = new NoiseGeneratorPerlin(new Random(),1);
			this.addShader("tom",new ResourceLocation("leafia:shaders/help/tom_desat.json"));
			this.addShader("nuclear",new ResourceLocation("leafia:shaders/help/nuclear.json"));
			this.addShader("drx",new ResourceLocation("leafia:shaders/help/digamma.json"));
		}
		@SubscribeEvent
		public void renderTick(RenderTickEvent e){
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null) {
				if (e.phase == Phase.END) {
					boolean needsUpdate = false;
					for (BigBruh shaderGroup : shaderGroups.values()) {
						LeafiaGls.matrixMode(5890);
						LeafiaGls.pushMatrix();
						LeafiaGls.loadIdentity();
						Minecraft mc = Minecraft.getMinecraft();
						Framebuffer mainCanvas = mc.getFramebuffer();
						if (shaderGroup != null)
						{
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
						if (validatedTEs.size() > 0) {
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
						for (TileEntity entity : entities) {
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
