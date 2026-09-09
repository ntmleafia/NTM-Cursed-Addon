package com.leafia.savedata;

import com.hbm.blocks.ModBlocks;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.passive.LeafiaPassiveLocal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FalloutSavedData extends WorldSavedData {
	public static final String key = "leafiaFallout";
	public static FalloutSavedData forWorld(World world) {
		FalloutSavedData data = (FalloutSavedData)world.getPerWorldStorage().getOrLoadData(FalloutSavedData.class,key);
		if (data == null) {
			world.getPerWorldStorage().setData(key,new FalloutSavedData(key));
			data = (FalloutSavedData)world.getPerWorldStorage().getOrLoadData(FalloutSavedData.class,key);
		}
		data.world = world;
		return data;
	}
	World world;
	public FalloutSavedData(String name) {
		super(name);
	}
	public List<FalloutData> falloutMap = new ArrayList<>();
	public static class FalloutData {
		int timeElapsed;
		public final Vec3d pos;
		public final double radius;
		double lifetime;
		public void expire() {
			timeElapsed = (int)lifetime+1;
		}
		double getLifetimeFromRadius(double radius) {
			return 600+radius/100*(12000*10);
		}
		public FalloutData(Vec3d pos,double radius) {
			this.pos = pos;
			this.radius = radius;
			lifetime = getLifetimeFromRadius(radius);
		}
		public FalloutData(NBTTagCompound compound) {
			pos = new Vec3d(compound.getDouble("x"),0,compound.getDouble("z"));
			radius = compound.getDouble("r");
			timeElapsed = compound.getInteger("t");
			lifetime = getLifetimeFromRadius(radius);
		}
		public NBTTagCompound serialize() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setDouble("x",pos.x);
			compound.setDouble("z",pos.z);
			compound.setDouble("r",radius);
			compound.setInteger("t",timeElapsed);
			return compound;
		}
		boolean tick() { // returns true when expired
			timeElapsed++;
			if (timeElapsed > lifetime)
				return true;
			return false;
		}
		public double getDensity() {
			return Math.max(1-timeElapsed/lifetime,0);
		}
		public double getDistanceMultiplier(Vec3d pos) {
			double baseDensity = 1-this.pos.distanceTo(pos)/radius;
			return Math.min(Math.pow(baseDensity*2,0.5),1);
		}
	}
	@Nullable
	public FalloutData getDensest(Vec3d pos) {
		double density = 0;
		FalloutData ret = null;
		for (FalloutData data : falloutMap) {
			double d = data.pos.distanceTo(pos);
			if (d <= data.radius) {
				double dens = data.getDensity()*data.getDistanceMultiplier(pos);
				if (dens >= density) {
					density = dens;
					ret = data;
				}
			}
		}
		return ret;
	}
	public int syncTimer = 0;
	public static int getHeight(World world,int x,int z) {
		int y = world.getActualHeight();
		MutableBlockPos bp = new MutableBlockPos();
		bp.setPos(x,y-1,z);
		while (bp.getY() >= 0) {
			IBlockState state = world.getBlockState(bp);
			if (state.getMaterial().isReplaceable()) {
				y--;
				bp.setPos(x,y-1,z);
			} else break;
		}
		if (y < world.getActualHeight())
			return y;
		else
			return world.getActualHeight()-1;
	}
	public void tick() {
		syncTimer--;
		Set<Integer> ignoreX = new HashSet<>();
		Set<Integer> ignoreZ = new HashSet<>();
		MutableBlockPos bp = new MutableBlockPos();
		for (int i = 0; i < falloutMap.size();) {
			FalloutData data = falloutMap.get(i);
			if (!data.tick()) {
				i++;
				if (world != null && !world.isRemote && world.rand.nextInt(50) <= data.timeElapsed/data.lifetime*10) {
					int ccx = (int)(data.pos.x/16+0.5);
					int ccz = (int)(data.pos.z/16+0.5);
					int cr = (int)(data.radius/16+1);
					int j = 0;
					int dim = cr*2+1;
					while (j < dim*dim) {
						j += world.rand.nextInt(50)+1;
						int cx = ccx-cr+j%dim;
						int cz = ccz-cr+j/dim;
						if (!ignoreX.contains(cx) || !ignoreZ.contains(cz)) {
							ignoreX.add(cx);
							ignoreZ.add(cz);
							bp.setPos(cx*16+world.rand.nextInt(16),0,cz*16+world.rand.nextInt(16));
							Vec3d p = new Vec3d(bp.getX()+0.5,0,bp.getZ()+0.5);
							if (data.pos.distanceTo(p) <= data.radius) {
								bp.setY(getHeight(world,bp.getX(),bp.getZ()));
								if (ModBlocks.fallout.canPlaceBlockAt(world,bp))
									world.setBlockState(bp,ModBlocks.fallout.getDefaultState());
							}
						}
					}
				}
			} else {
				falloutMap.remove(data);
				markDirty();
				syncTimer = 0;
			}
		}
		if (syncTimer <= 0) {
			syncTimer = 10*20;
			if (world != null && !world.isRemote)
				LeafiaCustomPacket.__start(new FalloutSyncPacket(this)).__sendToAllInDimension(world.provider.getDimension());
		}
	}
	public static class FalloutSyncPacket implements LeafiaCustomPacketEncoder {
		public FalloutSavedData save;
		public FalloutSyncPacket() { }
		public FalloutSyncPacket(FalloutSavedData save) {
			this.save = save;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeInt(save.falloutMap.size());
			for (FalloutData data : save.falloutMap) {
				buf.writeDouble(data.pos.x);
				buf.writeDouble(data.pos.z);
				buf.writeDouble(data.radius);
				buf.writeInt(data.timeElapsed);
			}
		}
		@SideOnly(Side.CLIENT)
		void loadBullshit(List<FalloutData> newMap) {
			World world = Minecraft.getMinecraft().world;
			FalloutSavedData saved = forWorld(world);
			LeafiaPassiveLocal.queueFunctionPost(()->{
				saved.falloutMap.clear();
				saved.falloutMap.addAll(newMap);
			});
		}
		@Override
		public @org.jetbrains.annotations.Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			List<FalloutData> newMap = new ArrayList<>();
			int count = buf.readInt();
			for (int i = 0; i < count; i++) {
				FalloutData data = new FalloutData(new Vec3d(buf.readDouble(),0,buf.readDouble()),buf.readDouble());
				data.timeElapsed = buf.readInt();
				newMap.add(data);
			}
			return (ctx)->loadBullshit(newMap);
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		falloutMap.clear();
		NBTTagList list = nbt.getTagList("map",10);
		for (NBTBase base : list) {
			if (base instanceof NBTTagCompound compound)
				falloutMap.add(new FalloutData(compound));
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (FalloutData data : falloutMap)
			list.appendTag(data.serialize());
		compound.setTag("map",list);
		return compound;
	}
}
