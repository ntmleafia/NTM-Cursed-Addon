package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.items.machine.ItemFELCrystal;
import com.hbm.items.machine.ItemFELCrystal.EnumWavelengths;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toclient.LoopedSoundPacket;
import com.hbm.sound.AudioWrapper;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityFEL;
import com.hbm.tileentity.machine.TileEntitySILEX;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.leafia.dev.optimization.LeafiaParticlePacket.DFCBlastParticle;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = TileEntityFEL.class)
public abstract class MixinTileEntityFEL extends TileEntityMachineBase implements ITickable, IEnergyReceiverMK2 {
	@Shadow(remap = false)
	public long power;

	@Shadow(remap = false)
	public boolean isOn;

	@Shadow(remap = false)
	public EnumWavelengths mode;

	@Shadow(remap = false)
	@Final
	public static int powerReq;

	@Shadow(remap = false)
	public int distance;

	@Shadow(remap = false)
	protected abstract boolean rotationIsValid(int silexMeta,int felMeta);

	@Shadow(remap = false)
	public boolean missingValidSilex;

	@Shadow(remap = false)
	private int audioDuration;

	@Shadow(remap = false)
	private AudioWrapper audio;

	@Shadow(remap = false)
	@Final
	public static long maxPower;

	public MixinTileEntityFEL(int scount) {
		super(scount);
	}

	/**
	 * @author ntmlefai
	 * @reason fuck up the dfc exploit
	 */
	@Overwrite
	public void update() {

		if(!world.isRemote) {

			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
			this.trySubscribe(world, pos.getX() +dir.offsetX * -5, pos.getY() + 1, pos.getZ() + dir.offsetZ  * -5, dir);
			this.power = Library.chargeTEFromItems(inventory, 0, power, maxPower);

			if(this.isOn && !(inventory.getStackInSlot(1).getCount() == 0)) {

				if(inventory.getStackInSlot(1).getItem() instanceof ItemFELCrystal crystal) {

					this.mode = crystal.wavelength;

				} else { this.mode = EnumWavelengths.NULL; }

			} else { this.mode = EnumWavelengths.NULL; }

			int range = 24;
			boolean silexSpacing = false;
			double xCoord = pos.getX();
			double yCoord = pos.getY();
			double zCoord = pos.getZ();
			if(this.isOn &&  this.mode != EnumWavelengths.NULL) {
				if(this.power < powerReq* Math.pow(4, mode.ordinal())){
					this.mode = EnumWavelengths.NULL;
					this.power = 0;
				} else {
					int distance = this.distance-1;
					double blx = Math.min(xCoord, xCoord + (double)dir.offsetX * distance) + 0.2;
					double bux = Math.max(xCoord, xCoord + (double)dir.offsetX * distance) + 0.8;
					double bly = Math.min(yCoord, 1 + yCoord + (double)dir.offsetY * distance) + 0.2;
					double buy = Math.max(yCoord, 1 + yCoord + (double)dir.offsetY * distance) + 0.8;
					double blz = Math.min(zCoord, zCoord + (double)dir.offsetZ * distance) + 0.2;
					double buz = Math.max(zCoord, zCoord + (double)dir.offsetZ * distance) + 0.8;

					List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(blx, bly, blz, bux, buy, buz));

					for(EntityLivingBase entity : list) {
						switch (this.mode) {
							case IR -> {}
							case VISIBLE -> entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60 * 60 * 65536, 0));
							case UV -> entity.setFire(10);
							case GAMMA -> ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 25);
							case DRX -> ContaminationUtil.applyDigammaData(entity, 0.1F);
						}
					}

					this.power -= (long) (powerReq * ((mode.ordinal() == 0) ? 0 : Math.pow(4, mode.ordinal())));
					for(int i = 3; i < range; i++) {

						double x = xCoord + dir.offsetX * i;
						double y = yCoord + 1;
						double z = zCoord + dir.offsetZ * i;

						IBlockState b = world.getBlockState(new BlockPos(x, y, z));

						if(!(b.getMaterial().isOpaque()) && b != Blocks.TNT) {
							this.distance = range;
							silexSpacing = false;
							continue;
						}

						if(b.getBlock() == ModBlocks.machine_silex) {
							BlockPos silex_pos = new BlockPos(x + dir.offsetX, yCoord, z + dir.offsetZ);
							TileEntity te = world.getTileEntity(silex_pos);

							if(te instanceof TileEntitySILEX silex) {
								int meta = silex.getBlockMetadata() - BlockDummyable.offset;
								if(rotationIsValid(meta, this.getBlockMetadata() - BlockDummyable.offset) && i >= 5 && !silexSpacing) {
									if(silex.mode != this.mode) {
										silex.mode = this.mode;
										this.missingValidSilex = false;
										silexSpacing = true;
									}
								} else {
									world.setBlockToAir(silex_pos);
									world.spawnEntity(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(Item.getItemFromBlock(ModBlocks.machine_silex))));
								}
							}

						} else if(b.getBlock() != Blocks.AIR){
							this.distance = i;
							float hardness = b.getBlock().getExplosionResistance(null);
							boolean blocked = false;
							if (b.getBlock() != ModBlocks.dfc_core) {
								switch (this.mode) {
									case IR -> {
										if (b.getMaterial().isOpaque() || b.getMaterial() == Material.GLASS)
											blocked = true;
									}
									case VISIBLE -> {
										if (b.getMaterial().isOpaque()) {
											if (hardness < 10 && world.rand.nextInt(40) == 0) {
												world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
												world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
											} else {
												blocked = true;
											}
										}
									}
									case UV -> {
										if (b.getMaterial().isOpaque()) {
											if (hardness < 100 && world.rand.nextInt(20) == 0) {
												world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
												world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
											} else {
												blocked = true;
											}
										}
									}
									case GAMMA -> {
										if (b.getMaterial().isOpaque()) {
											if (hardness < 3000 && world.rand.nextInt(5) == 0) {
												world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
												world.setBlockState(new BlockPos(x, y, z), ModBlocks.balefire.getDefaultState());
											} else {
												blocked = true;
											}
										}
									}
									case DRX -> {
										world.playSound(null, x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
										world.setBlockState(new BlockPos(x, y, z), ((MainRegistry.polaroidID == 11) ? ModBlocks.digamma_matter : ModBlocks.fire_digamma).getDefaultState());
										world.setBlockState(new BlockPos(x, y - 1, z), ModBlocks.ash_digamma.getDefaultState());
									}
								}
							} else {
								BlockPos p = new BlockPos(x,y,z);
								TileEntity te = world.getTileEntity(p);
								if (te instanceof TileEntityCore core) {
									IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
									double scale = 0.4;
									double power = (powerReq * ((mode.ordinal() == 0) ? 0 : Math.pow(4, mode.ordinal())))/5000;
									mixin.setDFCIncomingSpk(mixin.getDFCIncomingSpk() + power);
									switch(this.mode) {
										/*case RADIO:
											core.temperature = core.temperature + 1*scale;
											break;
										case MICRO:
											core.temperature = core.temperature + 5*scale;
											break;*/
										case IR:
											mixin.setDFCTemperature(mixin.getDFCTemperature() + 15*scale);
											break;
										case VISIBLE:
											mixin.setDFCTemperature(mixin.getDFCTemperature() + 7*scale);
											break;
										case UV:
											mixin.setDFCTemperature(mixin.getDFCTemperature() + 8.5*scale);
											break;
										/*case XRAY: {
											core.temperature = core.temperature+12.5*scale;
											DFCBlastParticle blast = new DFCBlastParticle(0,0.75f,0.75f,2);
											blast.emit(new Vec3d(p).add(0.5,0.5,0.5),new Vec3d(0,1,0),world.provider.getDimension(),200);
											RadiationSavedData.incrementRad(world,p,5,10);
										} break;*/
										case GAMMA: {
											mixin.setDFCTemperature(mixin.getDFCTemperature() + 32.5*scale);
											DFCBlastParticle blast = new DFCBlastParticle(0.4f,1f,0.2f,5);
											blast.emit(new Vec3d(p).add(0.5,0.5,0.5),new Vec3d(0,1,0),world.provider.getDimension(),200);
											ChunkRadiationManager.proxy.incrementRad(world,p,5,50);
										} break;
										case DRX: {
											mixin.setDFCTemperature(mixin.getDFCTemperature() + 75.5*scale);
											DFCBlastParticle blast = new DFCBlastParticle(1,0.2f,0.2f,10);
											blast.emit(new Vec3d(p).add(0.5,0.5,0.5),new Vec3d(0,1,0),world.provider.getDimension(),200);
											ContaminationUtil.radiate(world,x+0.5,y+0.5,z+0.5,64,0,15,0);
										} break;
									}
								}
								blocked = true;
							}
							if(blocked)
								break;
						}
					}
					PacketDispatcher.wrapper.sendToAll(new LoopedSoundPacket(pos.getX(), pos.getY(), pos.getZ()));
				}
			}

			networkPackNT(250);
		} else {

			if(power > powerReq * Math.pow(2, mode.ordinal()) && isOn && !(mode == EnumWavelengths.NULL) && distance - 3 > 0) {
				audioDuration += 2;
			} else {
				audioDuration -= 3;
			}

			audioDuration = MathHelper.clamp(audioDuration, 0, 60);

			if(audioDuration > 10) {

				if(audio == null) {
					audio = createAudioLoop();
					audio.startSound();
				} else if(!audio.isPlaying()) {
					audio = rebootAudio(audio);
				}

				audio.updateVolume(getVolume(2F));
				audio.updatePitch((audioDuration - 10) / 100F + 0.5F);

			} else {

				if(audio != null) {
					audio.stopSound();
					audio = null;
				}
			}
		}
	}
}
