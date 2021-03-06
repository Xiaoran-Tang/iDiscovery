/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.symbc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimplerSymbcInputGenerator {
	public static boolean alltests = true;

	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("idisc_run_daikon.sh"));
		String iter = "1";
		//if (args.length >= 3)
			iter = args[1];
		List<String> inputs = readSymbcOutput(args[0]);
		for (int i = 0; i < inputs.size(); i++) {
			String cmd = "echo \">>>>>>>>running test " + i
					+ "\"\n java -cp " + args[2]
					+ " daikon.Chicory --dtrace-file=traces/" + "iter" + iter + "-" + i + ".dtrace.gz " + args[3];
			writer.write(cmd + " " + inputs.get(i) + "\n");
		}
		writer.flush();
		writer.close();
	}

	public static List<String> readSymbcOutput(String path) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("<TEST>")
					&& (alltests || line.contains("java.lang.AssertionError"))) {
				// TODO: make it a configurable option
				String[] item = line.split("\t");
				String test = "";
				for (String input : item[1].split(",")) {
					if (!input.contains("@"))
						test += " " + input;
				}
				list.add(test);
			}
			line = reader.readLine();
		}
		return list;
	}

}
