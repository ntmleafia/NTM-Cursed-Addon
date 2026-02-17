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

		public void update() {
			float tnt = 0F,		tntMod = 1F;
			float nuke = 0F,	nukeMod = 1F;
			float hydro = 0F,	hydroMod = 1F;
			float bale = 0F,	baleMod = 1F;
			float dirty = 0F,	dirtyMod = 1F;
			float schrab = 0F,	schrabMod = 1F;
			float sol = 0F,		solMod = 1F;
			float euph = 0F;

			for(int i = 0; i < getSlots(); i ++) {
				ItemStack stack = getStackInSlot(i);
				if(stack.isEmpty())
					continue;

				ComparableStack comp = new NbtComparableStack(stack).makeSingular();
				CustomNukeEntry ent = TileEntityNukeCustom.entries.get(comp);

				if(ent == null)
					continue;

				if(ent.entry == EnumEntryType.ADD) {

					switch(ent.type) {
						case TNT: tnt += ent.value; break;
						case NUKE: nuke += ent.value; break;
						case HYDRO: hydro += ent.value; break;
						case BALE: bale += ent.value; break;
						case DIRTY: dirty += ent.value; break;
						case SCHRAB: schrab += ent.value; break;
						case SOL: sol += ent.value; break;
						case EUPH: euph += ent.value; break;
					}

				} else if(ent.entry == EnumEntryType.MULT) {

					switch(ent.type) {
						case TNT: tntMod *= ent.value; break;
						case NUKE: nukeMod *= ent.value; break;
						case HYDRO: hydroMod *= ent.value; break;
						case BALE: baleMod *= ent.value; break;
						case DIRTY: dirtyMod *= ent.value; break;
						case SOL: solMod *= ent.value; break;
						case SCHRAB: schrabMod *= ent.value; break;
					}
				}
			}
			tnt *= tntMod;
			nuke *= nukeMod;
			hydro *= hydroMod;
			bale *= baleMod;
			dirty *= dirtyMod;
			sol *= solMod;
			schrab *= schrabMod;

			if(tnt < 16) nuke = 0;
			if(nuke < 100) hydro = 0;
			if(nuke < 50) bale = 0;
			if(nuke < 50) schrab = 0;
			if(nuke < 25) sol = 0;
			if(schrab < 1 || sol < 1) euph = 0;

			this.tnt = Math.min(tnt, BombConfig.maxCustomTNTRadius);
			this.nuke = Math.min(nuke, BombConfig.maxCustomNukeRadius);
			this.hydro = Math.min(hydro, BombConfig.maxCustomHydroRadius);
			this.bale = Math.min(bale, BombConfig.maxCustomBaleRadius);
			this.dirty = Math.min(dirty, BombConfig.maxCustomDirtyRadius);
			this.schrab = Math.min(schrab, BombConfig.maxCustomSchrabRadius);
			this.sol = Math.min(sol, BombConfig.maxCustomSolRadius);
			this.euph = Math.min(euph, BombConfig.maxCustomEuphLvl);
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

			if(nuke == 0)
				return 0;

			return Math.min(nuke + tnt / 2, BombConfig.maxCustomNukeRadius);
		}

		public float getHydroAdj() {

			if(hydro == 0)
				return 0;

			return Math.min(hydro + nuke / 2 + tnt / 4, BombConfig.maxCustomHydroRadius);
		}

		public float getBaleAdj() {

			if(bale == 0)
				return 0;

			return Math.min(bale + hydro / 2 + nuke / 4 + tnt / 8, BombConfig.maxCustomBaleRadius);
		}

		public float getSchrabAdj() {

			if(schrab == 0)
				return 0;

			return Math.min(schrab + bale / 2 + hydro / 4 + nuke / 8 + tnt / 16, BombConfig.maxCustomSchrabRadius);
		}

		public float getSolAdj() {

			if(sol == 0)
				return 0;

			return Math.min(sol + schrab / 2 + bale / 4 + hydro / 8 + nuke / 16 + tnt / 32, BombConfig.maxCustomSolRadius);
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
