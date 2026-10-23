// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.worldgen.lib;

import java.util.Random;
import java.util.Set;

public class SellacityRoadChunk {
	public static final int size = 140;
	public final int[] data = new int[size*size];
	public int x(int index) { return index%size; }
	public int y(int index) { return index/size; }
	public int index(int x,int y) { return x+y*size; }

	protected int bufferX = 0;
	protected int bufferY = 0;
	void setBufferToRandomDirection(Random rand,boolean vertical) {
		bufferX = 0;
		bufferY = 0;
		if (!vertical)
			bufferX = rand.nextInt(2)*2-1;
		else
			bufferY = rand.nextInt(2)*2-1;
	}

	boolean checkAdjacent(int x,int y,int ignore) {
		if (x > 0 && (index(x-1,y) != ignore) && data[index(x-1,y)] == 1)
			return true;
		if (x < size-1 && (index(x+1,y) != ignore) && data[index(x+1,y)] == 1)
			return true;
		if (y > 0 && (index(x,y-1) != ignore) && data[index(x,y-1)] == 1)
			return true;
		if (y < size-1 && (index(x,y+1) != ignore) && data[index(x,y+1)] == 1)
			return true;
		return false;
	}
	boolean checkAdjacent(int x,int y,Set<Integer> ignore) {
		if (x > 0 && !ignore.contains(index(x-1,y)) && data[index(x-1,y)] == 1)
			return true;
		if (x < size-1 && !ignore.contains(index(x+1,y)) && data[index(x+1,y)] == 1)
			return true;
		if (y > 0 && !ignore.contains(index(x,y-1)) && data[index(x,y-1)] == 1)
			return true;
		if (y < size-1 && !ignore.contains(index(x,y+1)) && data[index(x,y+1)] == 1)
			return true;
		return false;
	}
	boolean check(int x,int y) {
		if (!isInBounds(x,y)) return true;
		return data[index(x,y)] == 1;
	}
	boolean isInBounds(int x,int y) {
		if (x < 0) return false;
		if (x >= size) return false;
		if (y < 0) return false;
		if (y >= size) return false;
		return true;
	}

	long debugTime = System.currentTimeMillis();
	void printTime(String message) {
		long newTime = System.currentTimeMillis();
		System.out.println("Took "+((newTime-debugTime)/1000f)+"s for "+message);
		debugTime = newTime;
	}
	public static final int v1$density = 300;
	void algorithmV1(Random rand) { // Snake Algorithm, Rating: 0/10
		// Create a full sized cross of roads (extending all the way to the edges)
		for (int x = 0; x < size; x++)
			data[index(x,size/2)] = 1;
		for (int y = 0; y < size; y++)
			data[index(size/2,y)] = 1;
		printTime("cross generation");

		boolean wasHoriz = false;
		// Generate snake noises
		for (int attempt = 0; attempt < v1$density; attempt++) {
			int x = rand.nextInt(size);
			int y = rand.nextInt(size);

			// if current position is already occupied, continue
			if (data[index(x,y)] == 1) continue;

			// if current position has any adjacent roads, continue
			if (checkAdjacent(x,y,-1)) continue;

			// calculate direction
			int xo = (x >= size/2) ? -1 : 1;
			int yo = (y >= size/2) ? -1 : 1;
			// choose to move vertically or horizontally
			if (wasHoriz) xo = 0; else yo = 0;
			wasHoriz = !wasHoriz;

			int lastIndex = -1;
			while (true) {
				// we don't do out of bounds check here because it's theoretically impossible to go out of bounds
				// because we are always heading towards the center cross we generated earlier
				data[index(x,y)] = 1;
				if (checkAdjacent(x,y,lastIndex)) break;
				lastIndex = index(x,y);
				x += xo;
				y += yo;
			}
		}
		printTime("snake generation");
	}

	public int v2$density = 75;
	void algorithmV2(Random rand) { // Rect Algorithm 4/10
		for (int attempt = 0; attempt < v2$density; attempt++) {
			// generate 2 random points
			int xa = rand.nextInt(size);
			int xb = rand.nextInt(size);
			int ya = rand.nextInt(size);
			int yb = rand.nextInt(size);
			if (xa == xb || ya == yb) continue;

			int x0 = Math.min(xa,xb);
			int x1 = Math.max(xa,xb);
			int y0 = Math.min(ya,yb);
			int y1 = Math.max(ya,yb);

			// generate hollow rectangle
			for (int x = x0; x <= x1; x++) {
				if ((check(x,y0-1) || check(x,y0+1)) && !check(x,y0)) break;
				data[index(x,y0)] = 1;
			}
			for (int x = x0; x <= x1; x++) {
				if ((check(x,y1-1) || check(x,y1+1)) && !check(x,y1)) break;
				data[index(x,y1)] = 1;
			}
			for (int y = y0; y <= y1; y++) {
				if ((check(x0-1,y) || check(x0+1,y)) && !check(x0,y)) break;
				data[index(x0,y)] = 1;
			}
			for (int y = y0; y <= y1; y++) {
				if ((check(x1-1,y) || check(x1+1,y)) && !check(x1,y)) break;
				data[index(x1,y)] = 1;
			}
		}
	}

	int v3$spacing = 3;
	int v3$nothingChance = 2;
	int v3$dualPathChance = 2;
	void algorithmV3(Random rand) { // Maze Algorithm 6/10 (Still looks maze)
		for (int x = 0; x < size; x+=v3$spacing) {
			for (int y = 0; y < size; y+=v3$spacing) {
				// create dots every v3$spacing pixels
				if (rand.nextInt(v3$nothingChance) == 0) continue;
				data[index(x,y)] = 1;
			}
		}
		for (int x = 0; x < size; x+=v3$spacing) {
			for (int y = 0; y < size; y+=v3$spacing) {
				// set to random direction
				setBufferToRandomDirection(rand,rand.nextBoolean());

				// if there's dot on direction we chose...
				if (!check(x+bufferX*v3$spacing,y+bufferY*v3$spacing)) continue;

				// ...then we draw a line towards that dot
				int max = v3$spacing-1;
				// we check for bounds because check method passes if it's out of bounds
				if (isInBounds(x+bufferX*max,y+bufferY*max)) {
					for (int i = 0; i <= max; i++)
						data[index(x+bufferX*i,y+bufferY*i)] = 1;
				}

				// at random chance, we also draw opposite line
				if (rand.nextInt(v3$dualPathChance) == 0) {
					bufferX *= -1;
					bufferY *= -1;

					// only if, ofcourse, there's also dot in that direction aswell.
					if (!check(x+bufferX*v3$spacing,y+bufferY*v3$spacing)) continue;
					// we check for bounds because check method passes if it's out of bounds
					if (isInBounds(x+bufferX*max,y+bufferY*max)) {
						for (int i = 0; i <= max; i++)
							data[index(x+bufferX*i,y+bufferY*i)] = 1;
					}
				}
			}
		}
		for (int x = 0; x < size; x+=v3$spacing) {
			for (int y = 0; y < size; y+=v3$spacing) {
				// remove dots connected to nowhere
				if (!checkAdjacent(x,y,-1))
					data[index(x,y)] = 0;
			}
		}
	}

	int v4$cellSize = 7;
	void algorithmV4(Random rand) { // Cell Algorithm 9/10 (I'll be using this one)
		int cellCount = size/v4$cellSize;
		int[] cells = new int[cellCount*cellCount];
		// choose random 0 or 1 (0 for horizontal, 1 for vertical)
		for (int i = 0; i < cells.length; i++)
			cells[i] = rand.nextInt(2);
		for (int cx = 0; cx < cellCount; cx++) {
			for (int cy = 0; cy < cellCount; cy++) {
				int xs = cx*v4$cellSize;
				int ys = cy*v4$cellSize;
				int ci = cx+cy*cellCount; // cell-index

				// draw a line
				if (cells[ci] == 0) {
					// horizontal case
					for (int xo = 0; xo < v4$cellSize; xo++) {
						int x = xs+xo;
						int y = ys+v4$cellSize/2;
						if (isInBounds(x,y))
							data[index(x,y)] = 1;
						else
							break;
					}
					int cixp = (cx+1)+cy*cellCount;
					int cixn = (cx-1)+cy*cellCount;
					// if cell adjacent to the right isn't in the same direction
					if (cx+1 >= cellCount || cells[cixp] != cells[ci]) {
						// draw vertical line on right edge
						for (int yo = 0; yo < v4$cellSize; yo++) {
							int x = xs+v4$cellSize;
							int y = ys+yo;
							if (isInBounds(x,y))
								data[index(x,y)] = 1;
							else
								break;
						}
					}
					// if cell adjacent to the left isn't in the same direction
					if (cx-1 < 0 || cells[cixn] != cells[ci]) {
						// draw vertical line on left edge
						for (int yo = 0; yo < v4$cellSize; yo++) {
							int x = xs;
							int y = ys+yo;
							if (isInBounds(x,y))
								data[index(x,y)] = 1;
							else
								break;
						}
					}
				} else if (cells[ci] == 1) {
					// vertical case
					for (int yo = 0; yo < v4$cellSize; yo++) {
						int x = xs+v4$cellSize/2;
						int y = ys+yo;
						if (isInBounds(x,y))
							data[index(x,y)] = 1;
						else
							break;
					}
					int ciyp = cx+(cy+1)*cellCount;
					int ciyn = cx+(cy-1)*cellCount;
					// if cell adjacent to the bottom isn't in the same direction
					if (cy+1 >= cellCount || cells[ciyp] != cells[ci]) {
						// draw horizontal line on bottom edge
						for (int xo = 0; xo < v4$cellSize; xo++) {
							int x = xs+xo;
							int y = ys+v4$cellSize;
							if (isInBounds(x,y))
								data[index(x,y)] = 1;
							else
								break;
						}
					}
					// if cell adjacent to the top isn't in the same direction
					if (cy-1 < 0 || cells[ciyn] != cells[ci]) {
						// draw horizontal line on top edge
						for (int xo = 0; xo < v4$cellSize; xo++) {
							int x = xs+xo;
							int y = ys;
							if (isInBounds(x,y))
								data[index(x,y)] = 1;
							else
								break;
						}
					}
				}
			}
		}
	}

	public SellacityRoadChunk(Random rand) {
		algorithmV4(rand);
	}
}
