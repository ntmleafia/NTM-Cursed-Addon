// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.bomb.missile.customnuke;

import com.hbm.config.BombConfig;
import com.hbm.config.MachineConfig;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.NbtComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemMissileStandard;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.InventoryHelper;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.hbm.tileentity.bomb.TileEntityNukeCustom.CustomNukeEntry;
import com.hbm.tileentity.bomb.TileEntityNukeCustom.EnumEntryType;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.bomb.missile.customnuke.container.CustomNukeMissileContainer;
import com.leafia.contents.bomb.missile.customnuke.container.CustomNukeMissileUI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomNukeMissileItem extends ItemMissileStandard implements IGUIProvider {
	public CustomNukeMissileItem(String s) {
		super(s,MissileFormFactor.MICRO,MissileTier.TIER0);
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}

	// Without this method, your inventory will NOT work!!!
	@Override
	public int getMaxItemUseDuration(@NotNull ItemStack stack) {
		return 1; // return any value greater than zero
	}

	@Override
	public @NotNull ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) player.openGui(MainRegistry.instance, 0, world, hand.ordinal(), 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		EnumHand hand = EnumHand.values()[i1];
		ItemStack held = entityPlayer.getHeldItem(hand);
		return new CustomNukeMissileContainer(entityPlayer.inventory,new CustomNukeMissileInventory(entityPlayer,held));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		EnumHand hand = EnumHand.values()[i1];
		ItemStack held = entityPlayer.getHeldItem(hand);
		return new CustomNukeMissileUI(entityPlayer.inventory,new CustomNukeMissileInventory(entityPlayer,held));
	}

	@Override
	public void addInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn) {
		super.addInformation(stack,worldIn,list,flagIn);
		list.add("§d["+I18nUtil.resolveKey("item.missile_customnuke.desc")+"]§r");
	}

	public static class CustomNukeMissileInventory extends ItemStackHandler {

		public final EntityPlayer player;
		public final ItemStack box;

		private final AtomicBoolean isDirty = new AtomicBoolean(false);
		private final AtomicBoolean isSaving = new AtomicBoolean(false);
		private boolean isClearing = false;

		private final float[] stageTotals = new float[8];
        private final float[] stageMultipliers = new float[8];

		public void update() {
            Arrays.fill(stageTotals, 0);
            Arrays.fill(stageMultipliers, 1);
            for (int slot = 0; slot < getSlots(); slot++) {
                var stack = getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                var entry = TileEntityNukeCustom.entries.get(new NbtComparableStack(stack).makeSingular());
                if (entry == null) continue;
                int stage = entry.type.ordinal();
                if (entry.entry == EnumEntryType.ADD) {
                    stageTotals[stage] += entry.value;
                } else if (entry.entry == EnumEntryType.MULT && stage != 7) {
                    stageMultipliers[stage] *= entry.value;
                }
            }
            for (int stage = 0; stage < 7; stage++) stageTotals[stage] *= stageMultipliers[stage];
            if (stageTotals[0] < 16) stageTotals[1] = 0;
            if (stageTotals[1] < 100) stageTotals[2] = 0;
            if (stageTotals[1] < 50) {
                stageTotals[3] = 0;
                stageTotals[5] = 0;
            }
            if (stageTotals[1] < 25) stageTotals[6] = 0;
            if (stageTotals[5] < 1 || stageTotals[6] < 1) stageTotals[7] = 0;
            tnt = Math.min(stageTotals[0], BombConfig.maxCustomTNTRadius);
            nuke = Math.min(stageTotals[1], BombConfig.maxCustomNukeRadius);
            hydro = Math.min(stageTotals[2], BombConfig.maxCustomHydroRadius);
            bale = Math.min(stageTotals[3], BombConfig.maxCustomBaleRadius);
            dirty = Math.min(stageTotals[4], BombConfig.maxCustomDirtyRadius);
            schrab = Math.min(stageTotals[5], BombConfig.maxCustomSchrabRadius);
            sol = Math.min(stageTotals[6], BombConfig.maxCustomSolRadius);
            euph = Math.min(stageTotals[7], BombConfig.maxCustomEuphLvl);
	}

		public float tnt;
		public float nuke;
		public float hydro;
		public float bale;
		public float dirty;
		public float schrab;
		public float sol;
		public float euph;
		public float getNukeAdj() {
            return adjustedStage(1, BombConfig.maxCustomNukeRadius);
	}

		public float getHydroAdj() {
            return adjustedStage(2, BombConfig.maxCustomHydroRadius);
	}

		public float getBaleAdj() {
            return adjustedStage(3, BombConfig.maxCustomBaleRadius);
	}

		public float getSchrabAdj() {
            return adjustedStage(4, BombConfig.maxCustomSchrabRadius);
	}

		public float getSolAdj() {
            return adjustedStage(5, BombConfig.maxCustomSolRadius);
	}

        public float stageValue(int stage) {
            return switch (stage) {
                case 0 -> tnt;
                case 1 -> nuke;
                case 2 -> hydro;
                case 3 -> bale;
                case 4 -> schrab;
                case 5 -> sol;
                case 6 -> euph;
                default -> throw new IllegalArgumentException("Unknown blast stage: " + stage);
            };
        }

        private float adjustedStage(int stage, float maximum) {
            float result = stageValue(stage);
            if (result == 0) return 0;
            for (int preceding = stage - 1, divisor = 2; preceding >= 0; preceding--, divisor *= 2) {
                result += stageValue(preceding) / divisor;
            }
            return Math.min(result, maximum);
        }

		CustomNukeMissileInventory(EntityPlayer player,ItemStack box) {
			super(27);
			this.player = player;
			this.box = box;

			if (!box.hasTagCompound()) box.setTagCompound(new NBTTagCompound());
			this.deserializeNBT(box.getTagCompound().getCompoundTag("Inventory"));
		}

		@Override
		protected void onContentsChanged(int slot) {
			if (isClearing) {
				return;
			}
			update();
            if (player.world.isRemote) return;
			this.isDirty.set(true);
			scheduleSave();
		}

		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			return 1;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			super.deserializeNBT(nbt);
			update();
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = super.serializeNBT();
			nbt.setFloat("tnt",Math.min(tnt,BombConfig.maxCustomTNTRadius));
			nbt.setFloat("nuke",Math.min(nuke,BombConfig.maxCustomNukeRadius));
			nbt.setFloat("hydro",Math.min(hydro,BombConfig.maxCustomHydroRadius));
			nbt.setFloat("bale",Math.min(bale,BombConfig.maxCustomBaleRadius));
			nbt.setFloat("dirty",Math.min(dirty,BombConfig.maxCustomDirtyRadius));
			nbt.setFloat("schrab",Math.min(schrab,BombConfig.maxCustomSchrabRadius));
			nbt.setFloat("sol",Math.min(sol,BombConfig.maxCustomSolRadius));
			nbt.setFloat("euph",Math.min(euph,BombConfig.maxCustomEuphLvl));
			return nbt;
		}

		private void scheduleSave() {
			if (isSaving.compareAndSet(false, true)) {
				if (!isDirty.compareAndSet(true, false)) {
					isSaving.set(false);
					return;
				}

				final NBTTagCompound nbtToSave = this.serializeNBT();
				CompletableFuture.supplyAsync(() -> Library.getCompressedNbtSize(nbtToSave)).whenCompleteAsync((currentSize,error) -> {
					try {
						if (error != null) {
							MainRegistry.logger.error("Error checking custom nuke missile NBT size for player {}", player.getName(), error);
							return;
						}
						if (currentSize > MachineConfig.crateByteSize) {
							ejectAndClearInventory();
						} else {
							if (box.getTagCompound() != null) {
								box.getTagCompound().setTag("Inventory", nbtToSave);
							}
						}
					} finally {
						isSaving.set(false);
						if (isDirty.get()) {
							scheduleSave();
						}
					}
				}, (runnable) -> ((WorldServer) player.world).addScheduledTask(runnable));
			}
		}

		private void ejectAndClearInventory() {
			this.isClearing = true;
			try {
				InventoryHelper.dropInventoryItems(player.world, player.getPosition(), this);
				for (int i = 0; i < this.getSlots(); i++) {
					this.setStackInSlot(i, ItemStack.EMPTY);
				}
			} finally {
				this.isClearing = false;
			}
			MainRegistry.logger.warn("Custom nuke missile for player {} was oversized and has been emptied to prevent data corruption.", player.getName());
			onContentsChanged(-1);
		}
	}
}
