package com.leafia.contents.control.fuel.nuclearfuel;

import com.custom_hbm.contents.torex.LCETorex;
import com.hbm.capability.HbmLivingProps;
import com.hbm.config.BombConfig;
import com.hbm.config.GeneralConfig;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityBalefire;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.handler.ArmorUtil;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.interfaces.IHasCustomModel;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.init.hazards.ItemRads.MultiRadContainer;
import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.items.itembase.AddonItemHazardBase;
import com.leafia.init.hazards.types.radiation.Neutrons;
import com.leafia.overwrite_contents.interfaces.IMixinEntityNukeExploisonMK5;
import com.llib.LeafiaLib;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeafiaRodItem extends AddonItemHazardBase implements IHasCustomModel {
	/*
	@Override
	public ItemHazardModule getHazards(ItemHazardModule hazards,ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			if (nbt.hasKey("incoming")) {
				double rad = Math.pow(nbt.getDouble("incoming"),0.65)/2;
				hazards.radiation.neutrons += rad;
			}
		}
		return hazards;
	}*/

	public enum ItemType {
		VOID,
		BILLET,
		BONEMEAL,
		DEPLETED
	}
	public enum Purity {
		RAW,		// no icon
		ISOTOPE,	// "I" icon
		FUEL,		// "F" icon
		SOURCE,		// "S" icon
		BREEDER,	// "B" icon
		UNSTABLE	// "X" icon
	}
	double lerp(double a, double b, double t) {
		return a+(b-a)*t;
	}
	public Item baseItem = null;
	public int baseMeta = 0;
	public ItemType baseItemType = ItemType.VOID;
	public ModelResourceLocation specialRodModel = null;
	public IBakedModel bakedSpecialRod = null;
	public Purity purity = Purity.RAW;
	public String functionId = "null";
	public double life = 0;
	public double meltingPoint = 1538;
	public String label = "ERROR!";
	public Item newFuel = null;
	public double emission = 1;
	public double reactivity = 1;
	public boolean splitIntoFast = true;
	public boolean splitWithFast = false;
	public boolean splitWithAny = false;
	public static ItemStack comparePriority(ItemStack a,@Nullable ItemStack b) {
		/*
		NBTTagCompound data = a.getTagCompound();
		if (data == null) {
			return b;
		} else {
			if (data.getBoolean("melting")) {
				if (b != null) {
					NBTTagCompound data2 = b.getTagCompound();
					if (data2 == null)
						return a;
					else {
						if (data2.getBoolean("melting")) {
							boolean validA = a.getItem() instanceof ItemLeafiaRod;
							boolean validB = b.getItem() instanceof ItemLeafiaRod;
							if (!validA && !validB)
								return null;
							else if (validA && !validB)
								return a;
							else if (!validA && validB)
								return b;
							int myPriority = ((ItemLeafiaRod) a.getItem()).meltdownPriority;
							if (((ItemLeafiaRod) b.getItem()).meltdownPriority > myPriority)
								return b;
							else if (myPriority > 0)
								return a;
							else return null;
						} else return a;
					}
				} else return a;
			} else return b;
		}*/
		boolean validA = a.getItem() instanceof LeafiaRodItem;
		if (b == null) {
			if (validA) return a;
			else return null;
		} else {
			boolean validB = b.getItem() instanceof LeafiaRodItem;
			if (!validA && !validB)
				return null;
			else if (validA && !validB)
				return a;
			else if (!validA && validB)
				return b;
			int myPriority = ((LeafiaRodItem) a.getItem()).meltdownPriority;
			if (((LeafiaRodItem) b.getItem()).meltdownPriority > myPriority)
				return b;
			else
				return a;
		}
	}
	public int meltdownPriority = 0;

	public float detonateRadius = 5;
	public boolean detonateNuclear = false;
	public boolean detonateVisualsOnly = false;
	public String detonateConfiguration = "default";
	public LeafiaRodItem resetDetonate() {
		detonateRadius = 5;
		detonateNuclear = false;
		detonateVisualsOnly = false;
		detonateConfiguration = "default";
		return this;
	}
	public void nuke(World world,BlockPos pos) {
		float x = pos.getX()+0.5f;
		float y = pos.getY()+0.5f;
		float z = pos.getZ()+0.5f;
		switch(functionId) {
			case "balefire": case "blazingbalefire": {
				EntityNukeTorex.statFacBale(world,x,y,z,180);
				EntityBalefire bf = new EntityBalefire(world);
				bf.posX = x;
				bf.posY = y;
				bf.posZ = z;
				bf.destructionRange = 280;
				world.spawnEntity(bf);
				break;
			}
			case "dgomega": {
				try {
					for (Entity entity : world.loadedEntityList) {
						if (entity instanceof EntityLivingBase living) {
							Vec3d entityPos = new Vec3d(living.posX,living.posY,living.posZ);
							Vec3d myPos = new Vec3d(x,y,z);
							double distance = entityPos.distanceTo(myPos);
							double dg = HbmLivingProps.getDigamma(living);
							double level = Math.max(dg,Math.min(50-distance/20,9.99999999));
							HbmLivingProps.setDigamma(living,level);
						}
					}
				} catch (ConcurrentModificationException ignored) {} // fuck off
				LCETorex.statFacDigamma(world,x,y,z,50);
				EntityNukeExplosionMK5 mk5 = EntityNukeExplosionMK5.statFac(world,50,x,y,z);
				((IMixinEntityNukeExploisonMK5)mk5).setDigammaFallout();
				world.spawnEntity(mk5);
				break;
			}
			case "sa326": {
				EntityNukeExplosionMK3 entity = new EntityNukeExplosionMK3(world);
				entity.posX = x;
				entity.posY = y;
				entity.posZ = z;
				if(!EntityNukeExplosionMK3.isJammed(world, entity)){
					entity.destructionRange = 50;
					entity.speed = BombConfig.blastSpeed;
					entity.coefficient = 1.0F;
					entity.waste = false;

					world.spawnEntity(entity);

					EntityCloudFleija cloud = new EntityCloudFleija(world, 50);
					cloud.posX = x;
					cloud.posY = y;
					cloud.posZ = z;
					world.spawnEntity(cloud);
				}
				break;
			}
			default: {
				EntityNukeTorex.statFac(world,x,y,z,100);
				world.spawnEntity(EntityNukeExplosionMK5.statFac(world,100,x,y,z));
				break;
			}
		}
	}
	public float detonate(@Nullable World world, @Nullable BlockPos pos) {
		boolean explode = (world != null);
		switch(functionId) {
			case "flashgold": case "flashlead":
				meltdownPriority = 10;
				if (explode) {
					float x = pos.getX()+0.5f;
					float y = pos.getY()+0.5f;
					float z = pos.getZ()+0.5f;
					detonateRadius *= 1.5f;
					EntityNukeTorex.statFac(world,x,y,z,detonateRadius);
					if (detonateNuclear && !detonateVisualsOnly)
						world.spawnEntity(EntityNukeExplosionMK5.statFac(world,(int)detonateRadius,x,y,z));
				}
				break;
			case "balefire": case "blazingbalefire":
				meltdownPriority = 20;
				if (explode) {
					float x = pos.getX()+0.5f;
					float y = pos.getY()+0.5f;
					float z = pos.getZ()+0.5f;
					detonateRadius *= 2.5f;
					EntityNukeTorex.statFacBale(world,x,y,z,detonateRadius);
					if (detonateNuclear && !detonateVisualsOnly) {
						EntityBalefire bf = new EntityBalefire(world);
						bf.posX = x;
						bf.posY = y;
						bf.posZ = z;
						bf.destructionRange = (int)detonateRadius;
						world.spawnEntity(bf);
					}
				}
				break;
			default:
				break;
		}
		if (explode && !detonateVisualsOnly) {
			ChunkRadiationManager.proxy.incrementRad(world, pos, 1000F, 2000F);
			if (!detonateNuclear) {
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), detonateRadius * 1.5f, true);
				ExplosionNukeGeneric.waste(world, pos.getX(), pos.getY(), pos.getZ(), (int)detonateRadius*2);
			}
		}
		return detonateRadius;
	}
	double lastY = 0;
	/**
	 * Does nuclear fissions
	 * @param stack The fuel rod stack to cause fission reaction
	 * @param updateHeat true for fission reaction, false for item tooltip
	 * @param x Incoming heat
	 * @param cool Should represent coolant %, range 0~1
	 * @param desiredTemp Temperature of coolant
	 * @param coolingRate Temperature of hot coolant
	 * @return Tooltip message
	 */
	public String HeatFunction(@Nullable ItemStack stack, boolean updateHeat, double x, double cool, double desiredTemp, double coolingRate) {
		return HeatFunction(stack,updateHeat,x,cool,desiredTemp,coolingRate,0);
	}
	/**
	 * Does nuclear fissions
	 * @param stack The fuel rod stack to cause fission reaction
	 * @param updateHeat true for fission reaction, false for item tooltip
	 * @param x Incoming heat
	 * @param cool Should represent coolant %, range 0~1
	 * @param desiredTemp Temperature of coolant
	 * @param coolingRate Temperature of hot coolant
	 * @return Tooltip message
	 */
	public String HeatFunction(@Nullable ItemStack stack, boolean updateHeat, double x, double cool, double desiredTemp, double coolingRate, double minimumRequired) {
		return HeatFunction(stack,updateHeat,x,cool,desiredTemp,coolingRate,minimumRequired,1);
	}
	/**
	 * Does nuclear fissions
	 * @param stack The fuel rod stack to cause fission reaction
	 * @param updateHeat true for fission reaction, false for item tooltip
	 * @param x Incoming heat
	 * @param cool Should represent coolant %, range 0~1
	 * @param desiredTemp Temperature of coolant
	 * @param coolingRate Temperature of hot coolant
	 * @return Tooltip message
	 */
	public String HeatFunction(@Nullable ItemStack stack, boolean updateHeat, double x, double cool, double desiredTemp, double coolingRate, double minimumRequired, double heatMultiplier) {
		NBTTagCompound data = null;
		String flux = TextFormatting.RED+"0°C"+TextFormatting.YELLOW;
		String temp = TextFormatting.GOLD+"ERROR°C"+TextFormatting.YELLOW;
		double heat = 0;
		if (stack != null) {
			data = stack.getTagCompound();
			if (data != null) {
				heat = data.getDouble("heat")-20;
				flux = TextFormatting.RED+String.format("%01.2f",data.getDouble("incoming"))+"°C"+TextFormatting.YELLOW;
				temp = TextFormatting.GOLD+"("+String.format("%01.2f",data.getDouble("heat"))+"°C-20)"+TextFormatting.YELLOW;
			}
		}
		boolean disableDecay = false;
		String n = "0";
		double tempx = heat-20;
		double y = 0; // x = 20+~~
		switch(functionId) {
			// DEPLETED
			case "depleteduranium": case "depletedmox":
				y = 80-20;
				n = "80-20";
				disableDecay = true;
				break;
			case "depletedplutonium":
				y = 90-20;
				n = "90-20";
				disableDecay = true;
				break;
			case "depletedthorium":
				y = 60-20;
				n = "60-20";
				disableDecay = true;
				break;
			case "depletedschrabidium":
				y = 100-20;
				n = "100-20";
				disableDecay = true;
				break;

			// URANIUM
			case "meu235": case "nu": case "u238":
				y = Math.pow(x*8,0.56)*3;
				n = "("+flux+"×8)^0.56×3 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;
			case "heu235": case "heu233":
				y = Math.pow(x*8,0.56)*3+Math.pow(Math.max(x-2500,0)/1000,3);
				n = "("+flux+"×8)^0.56×3 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;
			case "mox":
				y = Math.pow(x*8,0.56)*2;
				n = "("+flux+"×8)^0.56×2 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;

			// THORIUM
			case "th232":
				y = Math.pow(x*4,0.35)*3;
				n = "("+flux+"×4)^0.35×3 "+TextFormatting.DARK_AQUA+"(LIKE, REALLY POOR)";
				break;
			case "thmeu":
				y = Math.pow(x*65,0.35)*3;
				n = "("+flux+"×64)^0.35×3 "+TextFormatting.DARK_AQUA+"(POOR)";
				break;

			// PLUTONIUM
			case "lepu239": case "mepu239": case "npu": case "pu240":
				y = Math.pow(x*8,0.6)*3;
				n = "("+flux+"×8)^0.6×3 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;
			case "hepu239": case "hepu241":
				y = Math.pow(x*8,0.6)*3+Math.pow(Math.max(x-2500,0)/800,3);
				n = "("+flux+"×8)^0.6×3 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;

			// AMERICIUM
			case "leam242": case "meam242": case "heam242":
				y = Math.pow(x*8,0.64)*3;
				n = "("+flux+"×8)^0.64×3 "+TextFormatting.GOLD+"(RISKY)";
				break;
			case "heam241":
				y = Math.pow(x*8,0.64)*3+Math.pow(Math.max(x-2500,0)/800,3);
				n = "("+flux+"×8)^0.64×3 "+TextFormatting.GOLD+"(RISKY)";
				break;

			// NEPTUNIUM
			case "menp237": case "henp237":
				y = Math.pow(x*8,0.52)*3;
				n = "("+flux+"×8)^0.52×3 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;

			// SCHRABIDIUM
			case "lesa326": case "mesa326": case "hesa326": case "sa326": case "sa327":
				y = Math.pow(x,0.65)*12+Math.pow(Math.max(x-2500,0)/1600,3);
				n = ""+flux+"^0.65×12 "+TextFormatting.GOLD+"(RISKY)";
				break;

			// RADIUM
			case "ra226be":
				y = 300/(1+Math.pow(Math.E,-0.02*x))-150;
				n = "300/(1+e^(-0.02×"+flux+"))-150 "+TextFormatting.DARK_AQUA+"(POOR)";
				break;

			// OTHER
			case "potentialinstantblowoutapplicator":
				y = Math.tan(Math.min(heat/400,0.5)*Math.PI)+x/4;
				n = "tan(min("+temp+"/400,0.5)*PI)\n + "+flux+"/4 "+TextFormatting.DARK_RED+"(JUST NO)";
				break;

			// B.F.
			case "balefire":
				y = 100*Math.pow(x/1000-2,3)-500*Math.pow(x/1000-2,2)+x/600+2800;
				n = "100(("+flux+"/1000-2)³)-500(("+flux+"/1000-2)²)+"+flux+"/600+2800 "+TextFormatting.RED+"(DANGEROUS)";
				break;
			case "blazingbalefire":
				double z = x+1000;
				y = 100*Math.pow(z/1000-2,3)-500*Math.pow(z/1000-2,2)+z/600+2800;
				n = "100(("+flux+"/1000-1)³)-500(("+flux+"/1000-1)²)+("+flux+"+1000)/600+2800 "+TextFormatting.RED+"(DANGEROUS)";
				break;

			// COPY OLD SHIT
			case "po210": case "po210be":
				y = 700+Math.pow(x*2,0.69);
				n = "700 + ("+flux+"×2)^0.69 "+TextFormatting.GOLD+"(RISKY)";
				break;
			case "au198":
				y = 1580+Math.pow(x,0.75)*2.5;
				n = "1580 + "+flux+"^0.75 * 2.5 "+TextFormatting.GOLD+"(RISKY)";
				break;
			case "pb209":
				y = 2300+x*0.4;
				n = "2300 + "+flux+"×0.4 "+TextFormatting.DARK_RED+"(DANGEROUS)";
				break;

			// SOME RUSHED CRAP
			case "pu238": case "pu238be":
				y = 1330+Math.pow(x*2,0.62);
				n = "1330 + ("+flux+"*2)^0.62 "+TextFormatting.GOLD+"(RISKY)";
				break;


			case "leaus": case "heaus":
				y = Math.pow(x*5,0.65)*2;
				n = "("+flux+"×5)^0.65×2 "+TextFormatting.DARK_GREEN+"(FINE)";
				break;

			case "debug":
				y = Math.max(heat,0)+Math.sqrt(x);
				n = temp+"+20+√"+flux+TextFormatting.GRAY+" (DEBUG)";
				disableDecay = true;
				break;

			// YHARONITE
			case "yhxxx":
				y = Math.pow(x,0.75)*8;
				n = ""+flux+"^0.75×8 "+TextFormatting.RED+"(DANGEROUS)";
				disableDecay = true;
				break;
			// DIGAMMA
			case "dgomega":
				y = Math.pow(1.01916169,x);
				n = "1.01916169^"+flux+" "+TextFormatting.DARK_RED+"(JUST NO)";
				disableDecay = true;
				break;
			case "kys3000":
				y = Math.tan(x);
				n = "tan("+flux+") "+TextFormatting.LIGHT_PURPLE+"(KYS)";
				break;
		}
		lastY = y;
		if (updateHeat) {
			double decay = 0;
			if(data == null) {
				data = new NBTTagCompound();
				stack.setTagCompound(data);
				data.setDouble("heat",20);
				data.setDouble("depletion",0);
				data.setBoolean("melting",false);
				data.setInteger("generosityTimer",90);
				data.setInteger("spillage",0);
				data.setDouble("decay",decay);
			}
			boolean meltdown = data.getBoolean("melting");
			data.setDouble("incoming",x);
			if (meltdown) {
				//y = heat+20;
				//cool = 0; // it's only a matter of time until your machine explodes >:)
				data.setInteger("spillage",data.getInteger("spillage")+1);
			}
			if (data.getDouble("depletion") >= life && life > 0)
				y = 0;
			heat = data.getDouble("heat");
			decay = data.getDouble("decay");
			double heatX = Math.max(heat,20);
			double heatMg = Math.pow(Math.abs((20+y)-heat)+1,0.25)-1;
			if (heatX > 20+y)
				heatMg = heatMg * -1;
			//else if ((heatX >= meltingPoint) && (meltingPoint != 0) && !meltdown)
			//	heatMg = heatMg * Math.max(lerp(1,0,(heatX-meltingPoint)/(Math.pow(meltingPoint,0.75)+200)),0);
			if (Math.abs(heatMg) < 0.00001)
				heatMg = 0;
			if (!meltdown && (heatMg != 0)) {
				double curDepletion = data.getDouble("depletion") + Math.max(heatMg/2+Math.pow(x,0.95)/2000, 0); // +y is preferred but it doesnt really work with inert materials like lithium soo
				data.setDouble("depletion", curDepletion);
			}
			if (heatMg > 0)
				heatMg *= heatMultiplier;
			double decayBase = Math.min(heatMg/2,x);
			if (decayBase > decay)
				decay += (decayBase-decay)*0.01;
			if (disableDecay) decay = 0;
			decay *= 0.99992694932; // this is f*cked lmao //0.99854;
			data.setDouble("decay",decay);
			double fuckoff = 1-Math.pow(Math.min(decay*20/12,1),0.5);
			if (heatMg < 0)
				heatMg *= fuckoff;
			double newTemp = heat+heatMg+Math.max(decay-Math.max(heatMg,0),0);
			if (Double.isNaN(newTemp))
				newTemp = Double.MAX_VALUE;
			//newTemp += decay * Math.pow(Math.max(1-Math.max(newTemp,20)/1300,0),0.2); old algorithm
			double cooled = (
					Math.pow(
							Math.max(newTemp-desiredTemp,0)+1,
							Math.pow(coolingRate,0.5)/100
					)-1
			)*cool;
			if (cooled < minimumRequired) cooled = 0;
			double newCooledTemp = Math.max(newTemp-cooled,-273.15/*20*/);
			data.setDouble("cooled",cooled);
			if (Double.isNaN(newCooledTemp))
				newCooledTemp = Double.MAX_VALUE;
			data.setDouble(
					"heat",
					newCooledTemp
			);
			if (newCooledTemp >= 10_000_000) // make it hotter
				data.setBoolean("nuke",true); // new update
			if (!meltdown && (meltingPoint != 0)) {
				int timer = data.getInteger("generosityTimer");
				int initial = timer;
				if (newCooledTemp >= meltingPoint) { // oh no!
					if (newCooledTemp - heatX >= 0)
						timer = timer - 1;
					else
						timer = Math.min(timer + 20, 90);
					if (timer <= 0) {
						timer = 0;
						data.setBoolean("melting", true);
					}
				} else {
					timer = 90;
					data.setInteger("spillage",Math.max(data.getInteger("spillage")-1,0));
				}
				if (timer != initial)
					data.setInteger("generosityTimer", timer);
			} if (meltdown && newCooledTemp < meltingPoint)
				data.setBoolean("melting", false);
		}
		return n;
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return (getDurabilityForDisplay(stack) > 0); }
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		NBTTagCompound data = stack.getTagCompound();
		if (data != null)
			return Math.min(data.getDouble("depletion")/life,1);
		return 0;
	}
	public double getFlux(@Nullable ItemStack stack) {
		if (stack == null)
			return 0;
		if(stack.getItem() instanceof LeafiaRodItem otherRod) {
			double compat = 0.5;
			if (this.splitWithAny || otherRod.splitIntoFast == this.splitWithFast)
				compat = 3;
			compat = compat * otherRod.emission * this.reactivity;
			NBTTagCompound data = stack.getTagCompound();
			if (data != null)
				return Math.max(data.getDouble("heat"),20)*compat;
		}
		return 0;
	}
	public double getFlux(@Nullable ItemStack stack,boolean moderated) {
		if (stack == null)
			return 0;
		if(stack.getItem() instanceof LeafiaRodItem otherRod) {
			double compat = 0.5;
			if (this.splitWithAny || (moderated != this.splitWithFast))
				compat = 3;
			compat = compat * otherRod.emission * this.reactivity;
			NBTTagCompound data = stack.getTagCompound();
			if (data != null)
				return Math.max(data.getDouble("heat"),20)*compat;
		}
		return 0;
	}
	public ItemStack getDecayProduct(ItemStack stack) {
		NBTTagCompound data = stack.getTagCompound();
		if (data == null)
			return null;
		else {
			if (data.getBoolean("melting")) {
				// TODO: add molten fuel rods
			} else {
				boolean isDepleted = (data.getDouble("depletion") >= life);
				if (isDepleted) {
					if (newFuel != null) {
						NBTTagCompound newData = data.copy();
						newData.setDouble("depletion", 0);
						ItemStack newStack = new ItemStack(newFuel, 1, 0, newData);
						newStack.setTagCompound(newData);
						return newStack;
					}
				}
			}
		}
		return null;
	}
	public boolean decay(ItemStack stack, ItemStackHandler inventory, int slot) {
		ItemStack newFuel = getDecayProduct(stack);
		if (newFuel != null) {
			inventory.setStackInSlot(slot,newFuel);
			return true;
		}
		return false;
	}
	int meltdownFlash = 0;
	public String formatHeatMultiplier(double x) {
		boolean isDiv = false;
		if (x < 1) {
			isDiv = true;
			x = 1/x;
		}
		x = Math.round(x*1000)/1000d;
		String s = Double.toString(x);
		if (s.endsWith(".0"))
			s = s.substring(0,s.length()-2);
		return (isDiv ? "/" : "×")+s;
	}
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		String res = this.getRegistryName().getPath();
		LeafiaRodItem item = LeafiaRodItem.fromResourceMap.get(res);
		NBTTagCompound data = stack.getTagCompound();
		double heat = 20;
		double depletion = 0;
		double decay = 0;
		boolean meltdown = false;
		if(data != null) {
			heat = data.getDouble("heat");
			depletion = data.getDouble("depletion");
			meltdown = data.getBoolean("melting");
			decay = data.getDouble("decay");
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			list.add(TextFormatting.YELLOW + I18nUtil.resolveKey("item.leafiarod.heatfunc_shift"));
			for (String s : graph)
				list.add("  "+funcColor+s);
		} else {
			if (item.baseItem != null)
				list.add(TextFormatting.DARK_GRAY + I18nUtil.resolveKey(item.baseItem.getTranslationKey()+".name") + ((life != 0) ? ("  " + TextFormatting.DARK_GREEN + "["+(int)Math.max(Math.ceil((1-depletion/life)*100),0)+"%]") : ""));
			else if (life != 0)
				list.add(TextFormatting.DARK_GREEN + "["+(int)Math.max(Math.ceil((1-depletion/life)*100),0)+"%]");
			if (newFuel != null) {
				if (newFuel instanceof LeafiaRodItem)
					list.add(TextFormatting.DARK_GRAY + "  " + I18nUtil.resolveKey("item.leafiarod.decays",((LeafiaRodItem)newFuel).label));
				else
					list.add(TextFormatting.DARK_GRAY + "  " + I18nUtil.resolveKey("item.leafiarod.decays",I18nUtil.resolveKey(newFuel.getTranslationKey()+".name")));
			}
			if (life != 0) {
				list.add(TextFormatting.DARK_GREEN + "  "+I18nUtil.resolveKey("item.leafiarod.life",life+"°C"));
				list.add(TextFormatting.GOLD + "  "+I18nUtil.resolveKey("item.leafiarod.decayheat","+"+String.format("%01.3f",decay*20)+"°C/s"));
			}
			if (emission != 1)
				list.add(TextFormatting.AQUA+"  "+I18nUtil.resolveKey("item.leafiarod.reac.out",formatHeatMultiplier(emission)));
			if (reactivity != 1)
				list.add(TextFormatting.AQUA+"  "+I18nUtil.resolveKey("item.leafiarod.reac.in",formatHeatMultiplier(reactivity)));
			if (splitWithAny)
				list.add(TextFormatting.AQUA + "  "+I18nUtil.resolveKey("item.leafiarod.pref.all"));
			else if (splitWithFast)
				list.add(TextFormatting.LIGHT_PURPLE + "  "+I18nUtil.resolveKey("item.leafiarod.pref.fast"));
			if (!splitIntoFast)
				list.add(TextFormatting.AQUA + "  "+I18nUtil.resolveKey("item.leafiarod.moderated"));
			super.addInformation(stack,worldIn,list,flagIn);
			list.add("");
			list.add(TextFormatting.YELLOW + I18nUtil.resolveKey("item.leafiarod.heatfunc",item.HeatFunction(stack,false,0,0,0,0)));
			list.add(TextFormatting.GOLD + I18nUtil.resolveKey("item.leafiarod.temp",String.format("%01.1f",heat)+"°C"));
		}
		if (meltingPoint != 0) {
			list.add(TextFormatting.DARK_RED + I18nUtil.resolveKey("item.leafiarod.meltingpt",String.format("%01.1f",meltingPoint)));
			double percent = heat/meltingPoint;
			int barLength = 60;
			String bar = "";
			boolean dark = false;
			double barPercent = (heat >= 0) ? percent : 1-heat/-273.15;
			for (int i = 0; i < barLength; i++) {
				if ((i >= Math.floor(barLength*barPercent)) && !dark) {
					dark = true;
					bar = bar + ((heat >= 0) ? TextFormatting.DARK_GRAY : TextFormatting.WHITE);
				}
				bar = bar + "|";
			}
			int status = 0;
			if (heat > 300)
				status = 1;
			if (percent > 0.65)
				status = 2;
			if (heat < 0)
				status = -1;
			if (meltdown)
				status = 3;
			if (functionId.equals("dgomega")) {

				switch(status) {
					case 0:
						list.add(TextFormatting.RED+"["+bar+TextFormatting.RED+"]");
						list.add(TextFormatting.RED+"  "+I18nUtil.resolveKey("item.leafiarod.status.1dg"));
						break;
					case 1:
						list.add(TextFormatting.DARK_RED+"["+bar+TextFormatting.DARK_RED+"]");
						list.add(TextFormatting.DARK_RED+"  "+I18nUtil.resolveKey("item.leafiarod.status.2dg"));
						break;
					case 2:
						list.add(TextFormatting.GRAY+"["+bar+TextFormatting.GRAY+"]");
						list.add(TextFormatting.GRAY+"  "+I18nUtil.resolveKey("item.leafiarod.status.3dg"));
						break;
					case 3:
						list.add(TextFormatting.GRAY+"["+bar+TextFormatting.GRAY+"]");
						meltdownFlash = Math.floorMod(meltdownFlash+1,20);
						list.add((meltdownFlash >= 11) ? "" : TextFormatting.GRAY +"  "+I18nUtil.resolveKey("item.leafiarod.status.4dg"));
						break;
					case -1:
						list.add(TextFormatting.DARK_GREEN+"["+TextFormatting.DARK_GRAY+bar+TextFormatting.DARK_GREEN+"]");
						list.add(TextFormatting.GREEN+"  "+I18nUtil.resolveKey("item.leafiarod.status.0dg"));
						break;
				}
			} else {
				switch(status) {
					case 0:
						list.add(TextFormatting.LIGHT_PURPLE+"["+bar+TextFormatting.LIGHT_PURPLE+"]");
						list.add(TextFormatting.LIGHT_PURPLE+"  "+I18nUtil.resolveKey("item.leafiarod.status.1"));
						break;
					case 1:
						list.add(TextFormatting.GREEN+"["+bar+TextFormatting.GREEN+"]");
						list.add(TextFormatting.GREEN+"  "+I18nUtil.resolveKey("item.leafiarod.status.2"));
						break;
					case 2:
						list.add(TextFormatting.RED+"["+bar+TextFormatting.RED+"]");
						list.add(TextFormatting.RED+"  "+I18nUtil.resolveKey("item.leafiarod.status.3"));
						break;
					case 3:
						list.add(TextFormatting.DARK_RED+"["+bar+TextFormatting.DARK_RED+"]");
						meltdownFlash = Math.floorMod(meltdownFlash+1,20);
						list.add((meltdownFlash >= 11) ? "" : TextFormatting.DARK_RED +"  "+I18nUtil.resolveKey("item.leafiarod.status.4"));
						break;
					case -1:
						list.add(TextFormatting.DARK_AQUA+"["+TextFormatting.DARK_GRAY+bar+TextFormatting.DARK_AQUA+"]");
						list.add(TextFormatting.AQUA+"  "+I18nUtil.resolveKey("item.leafiarod.status.0"));
						break;
				}
			}
		}
	}
	public static final ModelResourceLocation rodModel = new ModelResourceLocation(
			"leafia:leafia_rod", "bakeMe");
	public static final Map<String,LeafiaRodItem> fromResourceMap = new HashMap<>();

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected){
		super.onUpdate(stack,worldIn,entity,itemSlot,isSelected);
		this.HeatFunction(stack,true,0,0,0,0);

		NBTTagCompound data = stack.getTagCompound();
		if (data != null) {
			double heat = data.getDouble("heat");
			boolean reacher = false;
			if(entity instanceof EntityPlayer && !GeneralConfig.enable528 && (heat < 900))
				reacher = Library.checkForHeld((EntityPlayer) entity, ModItems.reacher);
			if (heat >= 1500)
				entity.attackEntityFrom(DamageSource.ON_FIRE,6);
			if (heat >= 3200)
				entity.attackEntityFrom(DamageSource.ON_FIRE,9);
			if((heat >= 80) && !reacher && (!(entity instanceof EntityPlayer) || (entity instanceof EntityPlayer && !ArmorUtil.checkForAsbestos((EntityPlayer)entity)))){
				entity.setFire(2+(int)Math.floor(heat/100));
			}
		}

		ItemStack nextItem = this.getDecayProduct(stack);
		if (nextItem != null)
			entity.replaceItemInInventory(itemSlot,nextItem);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		double green = 255;
		NBTTagCompound data = stack.getTagCompound();
		if (data != null)
			green = Math.pow(MathHelper.clamp(1-data.getDouble("depletion")/life,0,1),1.25)*155+100;
		return ((int)green)<<8;
	}

	protected String[] graph = new String[0];
	protected String funcColor = "";
	public LeafiaRodItem(String s,double heatGenerated,double meltingPoint) {
		super("leafia_rod_" + s.replace("-","").replace(" ","").toLowerCase());
		this.label = s;
		s = s.replace("-","").replace(" ","").toLowerCase();
		this.functionId = s;
		s = "leafia_rod_" + s;
		fromResourceMap.put(s,this);

		String fnc = HeatFunction(null,false,0,0,0,0);
		if (!fnc.equals("0")) {
			graph = LeafiaLib.drawGraph(45,4,0,0,meltingPoint,0,meltingPoint,(heat)->{HeatFunction(null,false,heat,0,0,0); return lastY;});
			for (int i = fnc.length()-1; i >= 0; i--) {
				String sub = fnc.substring(i,Math.min(i+2,fnc.length()));
				if (sub.startsWith("§")) {
					funcColor = sub;
					break;
				}
			}
		}

		//this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.life = heatGenerated;
		this.meltingPoint = meltingPoint;

		this.setContainerItem(LeafiaRods.leafRod);

		addRad(emptyRad);
		addModifier(Neutrons.class,genericRodRadModifier);

		detonate(null,null);
	}
	static final MultiRadContainer emptyRad = new MultiRadContainer(0,0,0,0,0);
	static final IHazardModifier genericRodRadModifier = (stack,entityLivingBase,baseLevel)->{
		double add = 0;
		if (baseLevel < 0)
			baseLevel = 0;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			if (nbt.hasKey("incoming"))
				add = Math.pow(nbt.getDouble("incoming"),0.65)/2;
		}
		return baseLevel+add;
	};

	public LeafiaRodItem setAppearance(Item baseItem,ItemType baseItemType,Purity purity) {
		return setAppearance(baseItem,0,baseItemType,purity);
	}
	public LeafiaRodItem setAppearance(Item baseItem,int baseMeta,ItemType baseItemType,Purity purity) {
		this.baseItem = baseItem;
		this.baseMeta = baseMeta;
		this.baseItemType = baseItemType;
		this.purity = purity;
		return this;
	}
	String decayProductBuffer = null;
	public LeafiaRodItem setBaseItem(Item baseItem) { this.baseItem = baseItem; return this; }
	public LeafiaRodItem setEmission(double emission) { this.emission = emission; return this; }
	public LeafiaRodItem setReactivity(double reactivity) { this.reactivity = reactivity; return this; }
	public LeafiaRodItem setSpecialRodModel() { specialRodModel = new ModelResourceLocation("leafia:leafia_rod_"+functionId, "bakeMe"); return this; }
	public LeafiaRodItem setModerated() { this.splitIntoFast = false; return this; }
	public LeafiaRodItem setDecayProduct(String funcName) { decayProductBuffer = funcName; return this; }
	public LeafiaRodItem preferFast() { this.splitWithFast = true; return this; }
	public LeafiaRodItem preferAny() { this.splitWithAny = true; return this; }

	@Override
	public AddonItemHazardBase addRad(MultiRadContainer container) {
		container = container.copy().multiply(0.5);
		if (container.neutrons == 0)
			container.neutrons = -1;
		return super.addRad(container);
	}

	public static void confirmDecayProducts() {
		for (LeafiaRodItem item : fromResourceMap.values()) {
			if (item.decayProductBuffer != null)
				item.newFuel = fromResourceMap.get("leafia_rod_"+item.decayProductBuffer);
		}
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18nUtil.resolveKey("item.leafiarod",label);
	}

	@Override
	public ModelResourceLocation getResourceLocation() {
		if (specialRodModel != null)
			return specialRodModel;
		else
			return rodModel;
	}

	public static class EmptyLeafiaRod extends AddonItemBase {

		public EmptyLeafiaRod() {
			super("leafia_rod");
		}
		@Override
		@SideOnly(Side.CLIENT)
		public String getItemStackDisplayName(ItemStack stack) {
			return I18nUtil.resolveKey("item.leafiarodempty");
		}
		@Override
		public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
			super.addInformation(stack,worldIn,list,flagIn);
			list.add(TextFormatting.DARK_GRAY + I18nUtil.resolveKey("info.leafiarod.empty"));
		}
	}
}
