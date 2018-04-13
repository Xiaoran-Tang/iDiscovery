/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.utexas.gsoc.symbc.SymbcInputGenerator;

public class SymbcResParser {
	public static void parseSymbcOut(String file, String tech, String sub,
			int iter, BufferedWriter writer, String curSub) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String techName = TableGenerator.getTechName(tech);
		String subName = TableGenerator.getSubName(curSub, sub);
		int testSize = 0;
		String time = "NA";
		String state = "NA";
		String mem = "NA";
		String insn = "NA";
		String solver = "NA";
		String line = reader.readLine();
		while (line != null) {
			if (line.contains("<TEST>")
					&& (SymbcInputGenerator.alltests || line
							.contains("java.lang.AssertionError")))
				testSize++;
			else if (line.startsWith("elapsed time:       "))
				time = line.replace("elapsed time:       ", "").trim();
			else if (line.startsWith("states:             ")) {
				line = line.replace("states:             new=", "");
				state = line.substring(0, line.indexOf(","));
			} else if (line.startsWith("instructions:       ")) {
				insn = line.replace("instructions:       ", "").trim();
			} else if (line.startsWith("max memory:         ")) {
				mem = line.replace("max memory:         ", "").trim();
			} else if (line.startsWith("<SOLVERCALL> ")) {
				solver = line.replace("<SOLVERCALL> ", "").trim();
			}
			line = reader.readLine();
		}
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestNum}{" + testSize + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestGenTime}{" + time + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestStates}{" + state + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestSolverCall}{" + solver
				+ "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestInsns}{" + insn + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "TestMem}{" + mem + "}\n");
		System.out.println("NumOfTests: " + testSize + "\t GenTime: " + time);
	}

	public static void parseGreenOut(String tech, String sub, int iter,
			BufferedWriter writer, String curSub) throws IOException {
		String file = TableGenerator.work + curSub + "/" + tech.replace("-ungreen", "-green")
				+ File.separator + sub + "-invariants" + File.separator
				+ "symbolic_output_iter_" + (iter + 1) + ".txt";
		;
		String techName = TableGenerator.getTechName(tech);
		String subName = TableGenerator.getSubName(curSub, sub);
		if (curSub.equals("wbs")) {
			writer.write("\\newcommand{\\" + techName + subName
					+ OldTableGenerator.iters[iter] + "GreenTime}{(00:00:00)}\n");
			return;
		}
		if (!curSub.equals("tcas")) {
			writer.write("\\newcommand{\\" + techName + subName
					+ OldTableGenerator.iters[iter] + "GreenTime}{(--)}\n");
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int testSize = 0;
		String time = "NA";
		String state = "NA";
		String mem = "NA";
		String insn = "NA";
		String solver = "NA";
		String line = reader.readLine();
		while (line != null) {
			if (line.contains("<TEST>")
					&& (SymbcInputGenerator.alltests || line
							.contains("java.lang.AssertionError")))
				testSize++;
			else if (line.startsWith("elapsed time:       "))
				time = line.replace("elapsed time:       ", "").trim();
			else if (line.startsWith("states:             ")) {
				line = line.replace("states:             new=", "");
				state = line.substring(0, line.indexOf(","));
			} else if (line.startsWith("instructions:       ")) {
				insn = line.replace("instructions:       ", "").trim();
			} else if (line.startsWith("max memory:         ")) {
				mem = line.replace("max memory:         ", "").trim();
			} else if (line.startsWith("<SOLVERCALL> ")) {
				solver = line.replace("<SOLVERCALL> ", "").trim();
			}
			line = reader.readLine();
		}
		// writer.write("\\newcommand{\\" + techName + subName
		// + TableGenerator.iters[iter] + "TestNum}{" + testSize + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "GreenTime}{(" + time + ")}\n");
		// writer.write("\\newcommand{\\" + techName + subName
		// + TableGenerator.iters[iter] + "TestStates}{" + state + "}\n");
		// writer.write("\\newcommand{\\" + techName + subName
		// + TableGenerator.iters[iter] + "TestSolverCall}{" + solver
		// + "}\n");
		// writer.write("\\newcommand{\\" + techName + subName
		// + TableGenerator.iters[iter] + "TestInsns}{" + insn + "}\n");
		// writer.write("\\newcommand{\\" + techName + subName
		// + TableGenerator.iters[iter] + "TestMem}{" + mem + "}\n");
		// System.out.println("NumOfTests: " + testSize + "\t GenTime: " +
		// time);
	}
}
