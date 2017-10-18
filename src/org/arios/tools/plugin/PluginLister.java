package org.arios.tools.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PluginLister {
	
	static BufferedWriter bw;
	static List<String> oldPlugins = new ArrayList<>();
	/**
	 * 
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String...args) throws Throwable {
		bw = new BufferedWriter(new FileWriter("./pluginlist.txt"));
		load(new File("C:/Users/v4rg/Downloads/Arios public release/Arios #498/plugin/"));
		list(new File("./plugin/"));
		bw.flush();
		bw.close();
	}
	/**
	 * 
	 * Lists the files in the given directory.
	 * @param dir The directory file.
	 * @throws Throwable 
	 */
	private static void load(File dir) throws Throwable {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				load(f);
				continue;
			}
			oldPlugins.add(f.getName());
		}
	}
	
	/**
	 * Lists the files in the given directory.
	 * @param dir The directory file.
	 * @throws Throwable 
	 */
	private static void list(File dir) throws Throwable {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				list(f);
				continue;
			}
			if (!oldPlugins.contains(f.getName())) {
			bw.append(f.getAbsolutePath());
			bw.newLine();
			}
		}
	}

}