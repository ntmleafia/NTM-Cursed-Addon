package com.leafia.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.llib.exceptions.LeafiaDevFlaw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class _ConfigBuilder {
	private static final String path = "config/hbm/leafia.lcfg";
	private static final File file = new File(path);
	private Map<String,String> values = new HashMap<>();
	private Map<String,Integer> lineIndices = new HashMap<>();
	private List<String> lines = new ArrayList<>();
	public void createEmptyFile() {
		if (!Files.exists(file.toPath())) {
			try {
				String dir = "";
				for (String s : path.split("/")) {
					if (s.contains(".")) break;
					if (!dir.equals(""))
						dir = dir + "/";
					dir = dir + s;
				}
				Files.createDirectories(new File(dir).toPath());
				Files.createFile(file.toPath());
			} catch (IOException e) {
				throw new LeafiaDevFlaw(e);
			}
		}
	}
	public static class LeafiaConfigError extends RuntimeException {
		public LeafiaConfigError(String s) {
			super("\uD83C\uDF3F"+s);
		}
	}
	public void _lineBreak() {
		lines.add("");
	}
	private static final String separator = "----------------------------------------------------------";
	public void _separator() {
		lines.add(separator);
	}
	public void _category(String category) {
		lines.add("# "+category);
	}
	public void _comment(String comment) {
		lines.add(" # "+comment);
	}
	private int readingLine = -1;
	public String _string(String key,String def) {
		String value = values.getOrDefault(key,def);
		readingLine = lineIndices.getOrDefault(key,-1);
		lines.add(" "+key+": "+value);
		_lineBreak();
		return value;
	}
	public boolean _boolean(String key,boolean def) {
		String value = _string(key,Boolean.toString(def));
		if (value.equalsIgnoreCase("false"))
			return false;
		else if (value.equalsIgnoreCase("true"))
			return true;
		throw new LeafiaConfigError("Malformed config in "+path+" (line "+readingLine+" should be boolean)");
	}
	public int _integer(String key,int def) {
		String value = _string(key,Integer.toString(def));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new LeafiaConfigError("Malformed config in "+path+" (line "+readingLine+" should be integer)");
		}
	}
	public float _float(String key,float def) {
		String value = _string(key,Float.toString(def));
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			throw new LeafiaConfigError("Malformed config in "+path+" (line "+readingLine+" isn't a number or has too many decimals)");
		}
	}
	public double _double(String key,double def) {
		String value = _string(key,Double.toString(def));
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new LeafiaConfigError("Malformed config in "+path+" (line "+readingLine+" isn't a number or has too many decimals)");
		}
	}
	public JsonElement _json(String key,String def,Class<? extends JsonElement> clazz) {
		String value = _string(key,def);
		try {
			return new Gson().fromJson(value,clazz);
		} catch (RuntimeException e) {
			throw new LeafiaConfigError("Malformed config in "+path+" (line "+readingLine+" should be JSON of type "+clazz.getSimpleName()+")");
		}
	}
	public void loadConfig() {
		values.clear();
		try {
			List<String> list = Files.readAllLines(file.toPath());
			int line = 1;
			for (String s : list) {
				if (s.trim().startsWith("# ")) continue;
				if (s.trim().isEmpty()) continue;
				if (s.equals(separator)) continue;
				if (s.contains(":")) {
					String key = s.substring(0,s.indexOf(":")).trim();
					String value = s.substring(s.indexOf(":")+1).trim();
					values.put(key,value);
					lineIndices.put(key,line);
				} else
					throw new LeafiaConfigError("Malformed config in "+path+" (line "+line+")");
				line++;
			}
		} catch (IOException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
	public void saveConfig() {
		try {
			Files.write(file.toPath(),lines);
		} catch (IOException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
}
