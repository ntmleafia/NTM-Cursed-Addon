// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.hbm.handler.neutron;

import com.hbm.handler.neutron.NeutronNodeWorld.StreamWorld;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKNeutronNode;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKNeutronStream;
import com.hbm.handler.neutron.RBMKNeutronHandler.RBMKType;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.interfaces.Spaghetti;
import com.hbm.tileentity.machine.rbmk.*;
import com.leafia.dev.LeafiaDebug;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RealerSimNeutronStream extends RBMKNeutronStream {

	private int i;
	private int j;
	private List<BlockPos> blockedPositions = new ArrayList<>();
	private BlockPos basePos;
	public static int absorptionRadius = 1;

	public RealerSimNeutronStream(NeutronNode origin,Vec3d vector) {
		super(origin,vector);
	}
	public RealerSimNeutronStream(NeutronNode origin,Vec3d vector,double flux,double ratio) {
		super(origin,vector,flux,ratio);
	}
	private static TileEntity blockPosToTE(World worldObj, BlockPos pos) {
		return worldObj.getTileEntity(pos);
	}

	public Vec3d lookAt(double x0,double z0,double x1,double z1) {
		Vec3d vec = new Vec3d(x1-x0,0,z1-z0);
		if (vec.length() > 0)
			vec = vec.normalize();
		return vec;
	}

	@Override
	public Iterator<BlockPos> getBlocks(int range) {

		i = 1;
		j = 0;
		blockedPositions.clear();
		basePos = origin.tile.getPos();

		return new Iterator<>() {
			@Override
			public boolean hasNext() {
				return i <= range*2;
			}

			@Override
			public BlockPos next() {
				double xd0 = vector.x*j;
				double zd0 = vector.z*j;
				int x0 = (int)Math.floor(0.5+xd0);
				int z0 = (int)Math.floor(0.5+zd0);
				j++;
				int x1 = (int)Math.floor(0.5+vector.x*j);
				int z1 = (int)Math.floor(0.5+vector.z*j);
				i++;

				BlockPos p1 = new BlockPos(basePos.getX()+x1,basePos.getY(),basePos.getZ()+z1);
				for (int xo = -absorptionRadius; xo <= absorptionRadius; xo++) {
					for (int zo = -absorptionRadius; zo <= absorptionRadius; zo++) {
						BlockPos pa = p1.add(xo,0,zo);
						if (!blockedPositions.contains(pa)) {
							blockedPositions.add(pa);
							TileEntity te = blockPosToTE(origin.tile.getWorld(),pa);
							if (te instanceof TileEntityRBMKAbsorber || te instanceof TileEntityRBMKControl) {
								vector = lookAt(
										basePos.getX()+0.5+xd0,
										basePos.getZ()+0.5+zd0,
										pa.getX()+0.5,
										pa.getZ()+0.5
								);
								basePos = new BlockPos(basePos.getX()+x0,basePos.getY(),basePos.getZ()+z0);
								j = 1;
								x1 = (int)Math.floor(0.5+vector.x*j);
								z1 = (int)Math.floor(0.5+vector.z*j);
								p1 = new BlockPos(basePos.getX()+x1,basePos.getY(),basePos.getZ()+z1);
								//LeafiaDebug.debugPos(origin.tile.getWorld(),p1,0.05f,0xFFFF00,"CURVED");
								return p1;
							}
						}
					}
				}
				//LeafiaDebug.debugPos(origin.tile.getWorld(),p1,0.05f,0xFFFF00,"STRAIGHT");
				return p1;
			}
		};
	}

	/// Highlighted added parts with javadoc comments because it'd be pain to update this method
	/// whenever it gets changed upstream
	@Override
	@Spaghetti("What the fuck bob")
	public void runStreamInteraction(World worldObj, StreamWorld streamWorld) {

		// do nothing if there's nothing to do lmao
		if (fluxQuantity == 0D)
			return;

		BlockPos pos = origin.tile.getPos();

		TileEntityRBMKBase originTE;

		NeutronNode node = streamWorld.getNode(pos);
		if (node != null) {
			originTE = (TileEntityRBMKBase) node.tile;
		} else {
			originTE = (TileEntityRBMKBase) blockPosToTE(worldObj,pos);
			if (originTE == null) return; // Doesn't exist anymore!

			streamWorld.addNode(new RBMKNeutronNode(originTE,originTE.getRBMKType(),originTE.hasLid()));
		}

		int moderatedCount = 0;

		Iterator<BlockPos> iterator = getBlocks(RBMKNeutronHandler.fluxRange);

		while (iterator.hasNext()) {

			BlockPos targetPos = iterator.next();

			if (fluxQuantity == 0D) // Whoops, used it all up!
				return;

			NeutronNode targetNode = streamWorld.getNode(targetPos);
			if (targetNode == null) {
				TileEntity te = blockPosToTE(worldObj,targetPos); // ok, maybe it didn't get added to the list somehow??
				if (te instanceof TileEntityRBMKBase) {
					targetNode = RBMKNeutronHandler.makeNode(streamWorld,(TileEntityRBMKBase) te);
					streamWorld.addNode(targetNode); // whoops!
				} else {
					int hits = getHits(targetPos); // Get the amount of hits on blocks.
					if (hits == RBMKNeutronHandler.columnHeight) // If stream is fully blocked.
						return;
					else if (hits > 0) { // If stream is partially blocked.
						LeafiaDebug.debugPos(worldObj,pos,0.05F,0xFF000,"RAD LEAK");
						irradiateFromFlux(pos,hits);
						fluxQuantity *= 1-((double) hits/RBMKNeutronHandler.columnHeight); // Inverse to get partial blocking by blocks.
						continue;
					} else { // Nothing hit!
						LeafiaDebug.debugPos(worldObj,pos,0.05F,0xFF000,"RAD LEAK");
						irradiateFromFlux(pos,0);
						continue;
					}
				}
			}

			RBMKType type = (RBMKType) targetNode.data.get("type");

			if (type == RBMKType.OTHER || type == null) // pass right on by!
				continue;

			// we established earlier during `getNodes()` that they should all be RBMKBase TEs
			// no issue with casting here!
			TileEntityRBMKBase nodeTE = (TileEntityRBMKBase) targetNode.tile;

			if (!(boolean) targetNode.data.get("hasLid"))
				ChunkRadiationManager.proxy.incrementRad(worldObj,targetPos,(float) (this.fluxQuantity*0.05F));

			if (type == RBMKType.MODERATOR || nodeTE.isModerated()) {
				if (worldObj.rand.nextInt(4) == 0) { /// <- ADDED ///////////////////////////////////
					moderatedCount++;
					moderateStream();
				}
			}

			if (nodeTE instanceof IRBMKFluxReceiver) {
				if (moderatedCount > 0 || this.fluxRatio == 0) { /// <- ADDED ///////////////////////////////////
					IRBMKFluxReceiver column = (IRBMKFluxReceiver) nodeTE;

					if (type == RBMKType.ROD) {
						TileEntityRBMKRod rod = (TileEntityRBMKRod) column;

						if (rod.hasRod) {
							rod.receiveFlux(this);
							return;
						}

					} else if (type == RBMKType.OUTGASSER) {
						TileEntityRBMKOutgasser outgasser = ((TileEntityRBMKOutgasser) column);

						if (outgasser.canProcess()) {
							column.receiveFlux(this);
							return;
						}
					}
				}
			} else if (type == RBMKType.CONTROL_ROD) {
				TileEntityRBMKControl rod = (TileEntityRBMKControl) nodeTE;

				if (rod.level > 0.0D) {

					this.fluxQuantity *= rod.getMult();
					continue;
				}
				return;
			} else if (type == RBMKType.REFLECTOR) {

				if (((TileEntityRBMKBase) this.origin.tile).isModerated())
					moderatedCount++;

				if (this.fluxRatio > 0 && moderatedCount > 0)
					for (int i = 0; i < moderatedCount; i++)
						moderateStream();

				if (moderatedCount > 0 || this.fluxRatio == 0) { /// <- ADDED ///////////////////////////////////
					if (RBMKNeutronHandler.reflectorEfficiency != 1.0D) {
						this.fluxQuantity *= RBMKNeutronHandler.reflectorEfficiency;
						continue;
					}

					((TileEntityRBMKRod) originTE).receiveFlux(this);
				}
				return;
			} else if (type == RBMKType.ABSORBER) {
				if (RBMKNeutronHandler.absorberEfficiency == 1)
					return;

				this.fluxQuantity *= RBMKNeutronHandler.absorberEfficiency;
			}
		}

		NeutronNode[] nodes = getNodes(streamWorld,true);

		NeutronNode lastNode = nodes[(nodes.length-1)];

		if (lastNode == null) { // This implies that there was *no* last node, meaning either way it was never caught.
			// There is really no good way to figure out where exactly it should irradiate, so just irradiate at the origin tile.

			//LeafiaDebug.debugPos(worldObj,origin.tile.getPos(),0.05F,0xFF000,"RAD LEAK");
			/// SCREW THIS
			//irradiateFromFlux(origin.tile.getPos().add(this.vector.x,0,this.vector.z));
			return;
		}

		RBMKType lastNodeType = (RBMKType) lastNode.data.get("type");

		if (lastNodeType == RBMKType.CONTROL_ROD) {
			TileEntityRBMKControl rod = (TileEntityRBMKControl) lastNode.tile;
			if (rod.getMult() > 0.0D) {
				this.fluxQuantity *= rod.getMult();
				BlockPos posAfter = lastNode.tile.getPos().add(this.vector.x,0,this.vector.z);

				// The below code checks if the block after the control rod is actually a block or if it's an RBMK rod.
				// Resolves GitHub issue #1933.
				if (NeutronNodeWorld.getNode(worldObj,pos) == null) {
					TileEntity te = blockPosToTE(worldObj,posAfter);
					if (te instanceof TileEntityRBMKBase) {
						RBMKNeutronNode nodeAfter = RBMKNeutronHandler.makeNode(NeutronNodeWorld.getOrAddWorld(worldObj),(TileEntityRBMKBase) te);
						NeutronNodeWorld.getOrAddWorld(worldObj).addNode(nodeAfter);
					} else {
						LeafiaDebug.debugPos(worldObj,posAfter,0.05F,0xFF000,"RAD LEAK");
						irradiateFromFlux(posAfter); // I'm so mad about this...
					}
				}
			}
		}
	}
}
