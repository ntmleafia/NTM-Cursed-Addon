package com.llib.technical;

import com.llib.exceptions.LeafiaDevFlaw;

public class BitByteBuf {
	/** Backing store. Stream byte {@code p} lives at {@code bytes[offset+usedBytes-1-p]}, so the stream runs
	 * <i>backwards</i> through the used region {@code [offset,offset+usedBytes)} and appending consumes the free
	 * prefix {@code [0,offset)}. Growing therefore never has to move the existing payload, which is what lets
	 * {@link #allocateBits} amortise; the free prefix is always freshly-allocated zeroes, which {@link #insertBits}
	 * relies on since it ORs rather than assigns. */
	public byte[] bytes;
	protected int offset;
	protected int usedBytes;
	/** {@link #bytes} is memory we do not own (a wrapped netty payload); the first write must copy it out. */
	private boolean shared;

	protected BitByteBuf() {
		this.bytes = new byte[16];
		this.offset = 16;
		this.usedBytes = 0;
	}
	/** Index of stream byte 0. Stream byte {@code p} is at {@code payloadBase()-p}. */
	protected final int payloadBase() { return offset+usedBytes-1; }
	public final int payloadOffset() { return offset; }
	public final int payloadLength() { return usedBytes; }
	/** Adopts {@code data} as the payload; the buffer may write to it in place. */
	public final void wrapOwned(byte[] data) {
		this.bytes = data;
		this.offset = 0;
		this.usedBytes = data.length;
		this.shared = false;
	}
	/** Points at foreign memory without copying. Reads hit {@code data} directly; the first write copies out. */
	public final void wrapShared(byte[] data,int off,int length) {
		this.bytes = data;
		this.offset = off;
		this.usedBytes = length;
		this.shared = true;
	}
	public final void wrapShared(BitByteBuf other) {
		wrapShared(other.bytes,other.offset,other.usedBytes);
	}
	/** The payload as a standalone array, in the same order it goes onto the wire. */
	public final byte[] toByteArray() {
		byte[] out = new byte[usedBytes];
		System.arraycopy(bytes,offset,out,0,usedBytes);
		return out;
	}
	private void reallocate(int capacity) {
		byte[] newBytes = new byte[capacity];
		int newOffset = capacity-usedBytes;
		System.arraycopy(bytes,offset,newBytes,newOffset,usedBytes);
		bytes = newBytes;
		offset = newOffset;
		shared = false;
	}
	protected void allocateBits(int bits) {
		int needed = (bits+7)>>3;
		if (needed <= usedBytes) {
			if (shared)
				reallocate(Math.max(usedBytes<<1,16));
			return;
		}
		int grow = needed-usedBytes;
		if (shared || grow > offset)
			reallocate(Math.max(needed,Math.max(usedBytes<<1,16)));
		offset -= grow;
		usedBytes = needed;
	}
	int failsafeCount = 0;
	public int extractBits(int start,int end) {
		int outValue = 0;
		int minPos = start>>3;
		int maxPos = end>>3;
		int startOffset = start&7;
		int filter = (int)(((long)1<<((end&7)+1))-1);
		int base = payloadBase();
		for (int bytepos = minPos; bytepos <= maxPos; bytepos++) {
			int extract;
			if (bytepos < usedBytes) {
				extract = bytes[base-bytepos]&0xFF;
				failsafeCount = 0;
			} else {
				extract = 0;
				System.out.println("SERIOUS WARNING: Attempt to extract byte "+bytepos+", outside range 0 ~ "+(usedBytes-1)+"!");
				failsafeCount++;
				if (failsafeCount > 50)
					throw new LeafiaDevFlaw("BitByteBuf: Attempt to extract byte "+bytepos+", outside range 0 ~ "+(usedBytes-1)+"!");
			}
			if (bytepos == maxPos)
				extract = extract&filter;
			outValue = outValue | (int)((long)extract << (bytepos-minPos)*8 >>> startOffset);
		}
		return outValue;
	}
	protected int insertBits(int start,int value,int length) {
		allocateBits(start+length);
		int end = start+length-1;
		int minPos = start>>3;
		int maxPos = end>>3;
		int startOffset = start&7;
		int base = payloadBase();
		long padded = value&0xFFFFFFFFL;
		for (int bytepos = minPos; bytepos <= maxPos; bytepos++)
			bytes[base-bytepos] = (byte)(bytes[base-bytepos] | padded << startOffset >>> (bytepos-minPos)*8);
		return start+length;
	}
	/** Bulk byte-aligned append. Equivalent to {@code length} calls of {@code insertBits(writeBit,src[i],8)} but
	 * sizes the payload once. Append-only: it assigns rather than ORs, so it must not be aimed at bytes that were
	 * already written (rewind-and-patch has to keep going through {@link #insertBits}). */
	protected void insertAlignedBytes(int startBit,byte[] src,int srcIndex,int length) {
		allocateBits(startBit+(length<<3));
		int base = payloadBase()-(startBit>>3);
		for (int i = 0; i < length; i++)
			bytes[base-i] = src[srcIndex+i];
	}
	/** Bulk byte-aligned read, the inverse of {@link #insertAlignedBytes}. */
	protected void extractAlignedBytes(int startBit,byte[] dst,int dstIndex,int length) {
		int base = payloadBase()-(startBit>>3);
		for (int i = 0; i < length; i++)
			dst[dstIndex+i] = bytes[base-i];
	}
}
