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

public class PartInputGenerator {
	public static boolean alltests = true;

	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
		String iter = "1";
		if (args.length >= 3)
			iter = args[2];
		List<String> inputs = readRandomOutput(args[0]);
		double ratio = Double.parseDouble(args[6]) / 100;
		for (int i = 0; i < inputs.size() *ratio+1; i++) {
			String cmd = "echo \">>>>>>>>running additional test " + i
					+ "\"\n java -cp ./$1:" + args[3]
					+ " daikon.Chicory --dtrace-file=traces/" + args[4]
					+ "-iter" + iter + "-" + i + ".dtrace.gz " + args[5];
			writer.write(cmd + " " + inputs.get(i) + "\n");
		}
		writer.flush();
		writer.close();
	}

	public static List<String> readRandomOutput(String path) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		while (line != null) {
			// TODO: make it a configurable option
			list.add(line.trim());
			line = reader.readLine();
		}
		reader.close();
		return list;
	}

}
