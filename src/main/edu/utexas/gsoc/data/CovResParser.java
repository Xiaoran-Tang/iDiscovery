/*******************************************************************************
 * Copyright � 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

public class CovResParser {
	public static void parseCovOut(String tech, String ver, int iter,
			BufferedWriter writer, String subject) throws IOException {
		String file = "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"+subject+"/cov-subjects/"
				+ ver
				+ "/cov-"
				+ tech/*.replace("undesugar-", "desugar-")*///TODO: temp for fixing coverage bug
				+ "-"
				+ (iter) + ".txt";
		String techName = OldTableGenerator.getTechName(tech);
		String subName = OldTableGenerator.getSubName(ver);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		reader.close();
		String[] items = line.split("\t");

		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "ICov}{"
				+ items[1].replace("%", "\\%") + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "BCov}{"
				+ items[2].replace("%", "\\%") + "}\n");
		// System.out.println("NumOfTests: " + testSize + "\t GenTime: " +
		// time);
	}
}
