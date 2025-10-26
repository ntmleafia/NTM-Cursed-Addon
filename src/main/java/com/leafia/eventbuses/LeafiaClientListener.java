package com.leafia.eventbuses;

import com.google.gson.JsonSyntaxException;
import com.hbm.handler.JetpackHandler;
import com.hbm.interfaces.IHoldableWeapon;
import com.hbm.items.weapon.ItemGunEgon;
import com.hbm.items.weapon.ItemGunShotty;
import com.hbm.items.weapon.ItemSwordCutter;
import com.hbm.lib.Library;
import com.hbm.lib.RecoilHandler;
import com.hbm.particle.ParticleFirstPerson;
import com.hbm.sound.GunEgonSoundHandler;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.gear.IADSWeapon;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.passive.LeafiaPassiveLocal;
import com.leafia.passive.effects.LeafiaShakecam;
import com.leafia.shit.leafiashader.BigBruh;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.IEntityCustomCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class LeafiaClientListener {
	public static class HandlerClient {
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
