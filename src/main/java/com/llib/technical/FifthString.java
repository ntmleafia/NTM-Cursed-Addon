package com.llib.technical;
import com.llib.exceptions.LeafiaDevFlaw;

import java.util.Arrays;

import static com.llib.technical.FifthString.CharType.*;
import static com.llib.technical.FifthString.ControlType.*;

public class FifthString {
	/** {@link LeafiaBitByteUTF#writeUTFLeafia} length-prefixes each escape run with an unsigned short, so a run
	 * longer than this has to be split into several {@link ControlType#SPECIAL} segments. */
	static final int MAX_SPECIAL_RUN = 65535;
	private static final int[] NO_CODES = new int[0];
	private static final String[] NO_UTFS = new String[0];

	/** Over-allocated; only {@code [0,codeCount)} is meaningful. */
	public int[] codes = NO_CODES;
	public int codeCount = 0;
	/** Over-allocated; only {@code [0,utfCount)} is meaningful. */
	public String[] utfs = NO_UTFS;
	public int utfCount = 0;

	/** Empty container, to be filled by {@link #append} while decoding. */
	public FifthString() {}

	public FifthString(String str) {
		char[] chars = str.toCharArray();
		ensureCodes(chars.length+1);
		boolean capital = false;
		for (int i = 0; i < chars.length; i++) {
			char chr = chars[i];
			CharType type = charType(chr);
			if (type.isAlphabet && type.isCapital != capital) {
				if (i+1 < chars.length && charType(chars[i+1]) == type) {
					capital = !capital;
					append(CAPITAL_TOGGLE);
				} else
					append(CAPITAL_ONCE);
				append(chr);
			} else if (type == UNICODE) {
				int runEnd = i;
				while (runEnd < chars.length && runEnd-i < MAX_SPECIAL_RUN && charType(chars[runEnd]) == UNICODE)
					runEnd++;
				append(SPECIAL);
				append(new String(chars,i,runEnd-i));
				i = runEnd-1;
			} else
				append(chr);
		}
		append(END);
	}
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(codeCount);
		int utfIndex = 0;
		boolean capital = false;
		boolean nextCap = false;
		for (int i = 0; i < codeCount; i++) {
			int code = codes[i];
			if (code == END.code)
				break;
			else if (code == CAPITAL_TOGGLE.code)
				capital = !capital;
			else if (code == CAPITAL_ONCE.code)
				nextCap = true;
			else if (code == SPECIAL.code)
				str.append(utfs[utfIndex++]); // for future possiblities where you would want to put a branch here
			else if (code == 26)
				str.append(' ');
			else if (code == 27)
				str.append('_');
			else {
				boolean cap = capital;
				if (nextCap) {
					cap = !cap;
					nextCap = false;
				}
				str.append((char)((cap ? 65 : 97)+code));
			}
		}
		return str.toString();
	}
	private void ensureCodes(int capacity) {
		if (codes.length < capacity)
			codes = Arrays.copyOf(codes,Math.max(capacity,Math.max(codes.length<<1,16)));
	}
	public void append(int code) {
		ensureCodes(codeCount+1);
		codes[codeCount++] = code;
	}
	public void append(String utf) {
		if (utfs.length < utfCount+1)
			utfs = Arrays.copyOf(utfs,Math.max(utfs.length<<1,4));
		utfs[utfCount++] = utf;
	}
	void append(char chr) {
		CharType type = charType(chr);
		switch(type) {
			case UPPER: append(chr-65); break;
			case LOWER: append(chr-97); break;
			case SPACE: append(26); break;
			case UNDERSCORE: append(27); break;
			default: throw new LeafiaDevFlaw("Unsupported character "+Integer.toHexString(chr)+" - "+chr);
		}
	}
	void append(ControlType control) { append(control.code); }
	enum CharType {
		LOWER(false),UPPER(true),SPACE,UNDERSCORE,UNICODE;
		final boolean isAlphabet;
		final boolean isCapital;
		CharType() { isAlphabet = false; isCapital = false; }
		CharType(boolean capital) { isAlphabet = true; isCapital = capital; }
	}
	CharType charType(char chr) {
		if (chr >= 'a' && chr <= 'z') return LOWER;
		if (chr >= 'A' && chr <= 'Z') return UPPER;
		if (chr == ' ') return SPACE;
		if (chr == '_') return UNDERSCORE;
		return UNICODE;
	}
	public enum ControlType {
		CAPITAL_TOGGLE(28),
		CAPITAL_ONCE(29),
		END(30),
		SPECIAL(31), // currently only UTF
		;
		public final int code;
		ControlType(int code) { this.code = code; }
	}
}
