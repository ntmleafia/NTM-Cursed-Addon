package com.leafia.contents.worldgen.biomes.artificial;

import com.hbm.lib.ModDamageSource;
import com.hbm.util.RenderUtil;
import com.leafia.Tags;
import com.leafia.contents.worldgen.AddonBiome;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.eventbuses.LeafiaClientListener.Digamma;
import com.leafia.passive.LeafiaPassiveLocal;
import com.leafia.settings.AddonConfig;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

public class DigammaCrater extends AddonBiome {
	public static final List<NullEntity> NULL_LIST = Lists.newArrayList();
	public static boolean isDigammaBiome(Biome biome) {
		if (AddonConfig.schizoMode)
			return true;
		return biome instanceof DigammaCrater;
	}
	public DigammaCrater(String resource) {
		super(resource,new BiomeProperties("Digamma Crater")
				.setRainfall(0).setRainDisabled()
				.setWaterColor(0xFF0000)
		);
		spawnableCreatureList.clear();
		this.postInit = ()->{
			//BiomeManager.addBiome(BiomeType.DESERT,new BiomeEntry(this,15));
			BiomeDictionary.addTypes(this,Type.SWAMP,Type.WET);
		};
	}
	@Override
	public int getSkyColorByTemp(float currentTemperature) {
		return 0;
	}
	@Override
	public int getFogColor() {
		return getSkyColorByTemp(0);
	}
	@Override
	public float getFogStart(float original) { return original*0f; }
	@Override
	public float getFogEnd(float original) { return original*0.4f; }
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		return 0x666666;
	}
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		return 0x666666;
	}

	public static class NullEntity extends EntityLiving {
		int timer = 0;
		public static class EntityAIWatchForever extends EntityAIBase {
			final EntityLiving self;
			public EntityAIWatchForever(EntityLiving self) {
				this.self = self;
			}
			@Override
			public boolean shouldExecute() {
				return true;
			}
			@SideOnly(Side.CLIENT)
			public EntityPlayer getPlayer() {
				return Minecraft.getMinecraft().player;
			}
			@Override
			public void updateTask() {
				if (self.world.isRemote) {
					EntityPlayer player = getPlayer();
					self.getLookHelper().setLookPosition(player.posX,player.posY+player.getEyeHeight(),player.posZ,(float) self.getHorizontalFaceSpeed(),(float) self.getVerticalFaceSpeed());
				}
			}
		}
		public NullEntity(World worldIn) {
			super(worldIn);
			tasks.addTask(0,new EntityAIWatchForever(this));
			setRenderDistanceWeight(getRenderDistanceWeight()*10);
			setEntityInvulnerable(true);
			setNoGravity(true);
		}
		@Override
		public boolean isEntityInvulnerable(DamageSource source) {
			return true;
		}
		@Override
		public boolean hasNoGravity() {
			return true;
		}
		@SideOnly(Side.CLIENT)
		void check() {
			updateEntityActionState();
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (!isDigammaBiome(world.getBiome(new BlockPos(player.posX,player.posY,player.posZ)))) {
				setDead();
				return;
			}
			double dist = new Vec3d(player.posX,player.posY,player.posZ).distanceTo(new Vec3d(posX,posY,posZ));
			if (dist < 20 || dist > 200)
				setDead();
			LeafiaPassiveLocal.nullCounter++;
		}
		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!isDigammaBiome(world.getBiome(new BlockPos(posX,posY,posZ))))
				setDead();
			if (world.isRemote)
				check();
			else {
				timer++;
				if (timer >= 20) // failsafe
					this.setDead();
			}
		}
	}
	public static class NullRender extends RenderBiped<NullEntity> {
		public static final IRenderFactory<NullEntity> FACTORY = NullRender::new;
		public static final ResourceLocation texture = new ResourceLocation(Tags.MODID,"textures/omfgitsnullminecraft.png");
		public static final ResourceLocation eyes = new ResourceLocation(Tags.MODID,"textures/omfgitsnullminecraft_e.png");
		public NullRender(RenderManager renderManagerIn) {
			super(renderManagerIn,new ModelBiped(),0);
		}
		boolean renderOnlyEyes = false;
		@Override
		protected void renderModel(NullEntity entity,float limbSwing,float limbSwingAmount,float ageInTicks,float netHeadYaw,float headPitch,float scaleFactor) {
			LeafiaGls.pushMatrix();
			EntityPlayer player = Minecraft.getMinecraft().player;
			double dist = new Vec3d(entity.posX,entity.posY,entity.posZ).distanceTo(new Vec3d(player.posX,player.posY,player.posZ));
			float brightness = (float)Math.min(Math.pow((dist-20)/20,2),1);
			LeafiaGls.translate(entity.world.rand.nextGaussian()*0.05*(1-brightness),entity.world.rand.nextGaussian()*0.05*(1-brightness),entity.world.rand.nextGaussian()*0.05*(1-brightness));
			renderOnlyEyes = true;
			int light = entity.world.getCombinedLight(new BlockPos(entity.posX,entity.posY,entity.posZ),0);
			int lx = light & 0xFFFF;
			int ly = light >> 16;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
			LeafiaGls.disableLighting();
			LeafiaGls.color(1,brightness,brightness);
			super.renderModel(entity,limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch,scaleFactor);

			renderOnlyEyes = false;
			LeafiaGls.enableLighting(); // i mean it doesn't matter because it's pitch black but well uhm
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lx,ly);
			boolean lastBlend = RenderUtil.isBlendEnabled();
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
			LeafiaGls.color(1,1,1,1-entity.world.rand.nextFloat()*0.95f*(brightness*0.75f+0.25f));
			super.renderModel(entity,limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch,scaleFactor);
			if (!lastBlend)
				LeafiaGls.disableBlend();
			LeafiaGls.color(1,1,1);
			LeafiaGls.popMatrix();
		}
		@Override
		protected ResourceLocation getEntityTexture(NullEntity entity) {
			return renderOnlyEyes ? eyes : texture;
		}
	}
	public static class DigammaBackstabPacket implements LeafiaCustomPacketEncoder {
		@SideOnly(Side.CLIENT)
		void handleLocal() {
			Digamma.backstab(Minecraft.getMinecraft().player);
		}
		@Override
		public void encode(LeafiaBuf buf) { }
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			return (ctx)->{
				if (ctx.side == Side.SERVER) {
					EntityPlayer player = ctx.getServerHandler().player;
					player.attackEntityFrom(ModDamageSource.digamma,4);
					player.velocityChanged = false;
					LeafiaCustomPacket.__start(new DigammaBackstabPacket()).__sendToClient(player);
				} else
					handleLocal();
			};
		}
	}
}
