package com.leafia.eventbuses;

import com.custom_hbm.GuiBackupsWarning;
import com.google.gson.JsonSyntaxException;
import com.hbm.blocks.ILookOverlay;
import com.hbm.items.ModItems;
import com.hbm.render.GuiCTMWarning;
import com.custom_hbm.util.LCETuple.*;
import com.hbm.render.item.ItemRenderBase;
import com.leafia.contents.AddonItems;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.gear.IADSWeapon;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static com.hbm.main.ModEventHandlerClient.swapModels;

public class LeafiaClientListener {
	public static class HandlerClient {
		@SubscribeEvent
		public void modelBaking(ModelBakeEvent evt) {
			IRegistry<ModelResourceLocation,IBakedModel> reg = evt.getModelRegistry();
			for(Entry<Item,ItemRenderBase> entry : ItemRendererInit.renderers.entrySet()){
				swapModels(entry.getKey(), reg);
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
					if (world.getBlockState(mop.getBlockPos()).getBlock() instanceof ILookOverlay) {
						((ILookOverlay) world.getBlockState(mop.getBlockPos()).getBlock()).printHook(event,world,mop.getBlockPos().getX(),mop.getBlockPos().getY(),mop.getBlockPos().getZ());
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
			e.setNewfov(e.getFov()*multiplier);
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
