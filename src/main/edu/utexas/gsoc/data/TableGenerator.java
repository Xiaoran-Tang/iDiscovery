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
import java.io.FileWriter;
import java.io.IOException;

public class TableGenerator {

	// static String[] subs = { "v1", /* "v2", "v3"/*, "v4", "v5" */};
	static String[] techs = { "undesugar-ungreen", "desugar-ungreen"/*
																	 * ,
																	 * "undesugar-green"
																	 * ,
																	 * "desugar-green"
																	 */};
	static String[] iters = { "IterOne", "IterTwo", "IterThree", "IterFour",
			"IterFive", "IterSix", "IterSeven", "IterEight", "IterNine",
			"IterTen" };
	// static String meth = "wbs.WBS.update(int, boolean, boolean)";
	// public static String meth="tcas.TCAS.alt_sep_test()";
	// public static String
	// meth="heap.HeapArrayV2.driver(int, int, int, int, int)";
	// public static String
	// meth="aswnew.asw.Main0(double, boolean, boolean, boolean, boolean, double, double, boolean, double, boolean, boolean, boolean)";
	// public static String
	// meth="rjc.rjc.MainSymbolic(double, double, double, double, double, double, double[], double[])";
	// static String curSub = "wbs";
	// static String curSub="asw";
	// static String curSub = "tcas";
	// public static String curSub="rjc";
	static String[][] vers = { { "v1", "v2", "v3" }, { "v1" }, { "v1" },
			{ "v1" } };
	static String[] curSubs = { "tcas", "wbs", "asw", "rjc" };
	static String[] meths = {
			"tcas.TCAS.alt_sep_test()",
			"wbs.WBS.update(int, boolean, boolean)",
			"aswnew.asw.Main0(double, boolean, boolean, boolean, boolean, double, double, boolean, double, boolean, boolean, boolean)",
			"rjc.rjc.MainSymbolic(double, double, double, double, double, double, double[], double[])" };
	// static String work =
	// "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/";
	static String work = "/Users/zhanglingming/Research/papers/tacas2014/data/";
	static String paper = "/Users/zhanglingming/Research/papers/issta2014/";

	// static String dir =
	// "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"
	// + curSub + "/";

	public static void main(String[] args) throws IOException {

		genData(paper + "tables/" + "data.tex");
		genTable(paper + "tables/");
	}

	private static void genTable(String f) throws IOException {
		for (String tech : techs) {
			String techName = getTechName(tech);

			BufferedWriter writer = new BufferedWriter(new FileWriter(f + tech
					+ "-table.tex"));
			writer.write("\\begin{table*}[t!]\n");
			writer.write("\\center\\scriptsize");
			if (tech.equals("desugar-ungreen"))
				writer.write("\\caption{\\label{tab:"
						+ tech
						+ "}Experimental results for \\idisc{} with optimizations}\n");
			else
				writer.write("\\caption{\\label{tab:"
						+ tech
						+ "}Experimental results for \\idisc{} without optimizations}\n");
			writer.write("\\begin{tabular}{|c|c||c|c|c||l|l||l|l|l|l|l|}\n");
			writer.write("\\hline\n");
			writer.write("Subjects&Iter.&\\multicolumn{3}{c||}{Invariants}&\\multicolumn{2}{c||}{Invariant Cost}&\\multicolumn{5}{c|}{Symbolic Execution Cost}\\\\\\cline{3-12}");
			writer.write("&&");
			for (int i = 0; i < 1; i++) {
				writer.write("Num&Del&New&");
			}
			writer.write("Tracing&Inference&");
			writer.write("Tests&SCalls&States&Memory&Time\\\\\n");
			writer.write("\\hline\\hline\n");
			for (int s = 0; s < curSubs.length; s++) {
				String curSub = curSubs[s];
				String[] vs = vers[s];
				int[] iterNum = getIterNum(curSub, tech, vs);

				for (int i = 0; i < vs.length; i++) {
					String ver = vs[i];
					String subName = getSubName(curSub, ver);
					// System.out.println(subName+": "+iterNum[i]);
					int iterLimit = iterNum[i];
					if (curSub.equals("asw") || curSub.equals("rjc")) {
						if (tech.equals("undesugar-ungreen")) {
							iterLimit = iterNum[i] + 1;
						}
					}
					for (int iter = 0; iter < iterLimit; iter++) {
						if (iter == 0) {
							writer.write("\\" + curSub);
							if (curSub.equals("tcas"))
								writer.write("-" + ver);
						}
						String iterName = iters[iter];
						String prefix = "\\" + techName + subName + iterName;
						String[] ranges = { "Used" };
						String[] locs = { /* "Pre", "Post" */"All" };
						String[] vals = { "Num", "Del", "New" };
						writer.write("&" + (iter + 1) + " ");
						for (String r : ranges) {
							for (String l : locs) {
								for (String v : vals)
									writer.write("&" + prefix + r + l + v);
							}
						}
						writer.write("&" + prefix + "Trace &" + prefix
								+ "Infer ");

						if (iter == iterNum[i]) {
							for (int n = 0; n < 4; n++) {
								writer.write("&--");
							}
							writer.write("& TimeOut ");
							writer.write("\\\\\n");
						} else {
							writer.write("&" + prefix + "TestNum &" + prefix
									+ "TestSolverCall &" + prefix
									+ "TestStates &" + prefix + "TestMem &"
									+ prefix + "TestGenTime");
							writer.write("\\\\\n");
//							if (iterLimit > iterNum[i]
//									&& iter == iterLimit - 1)
//								writer.write("\\\\\\hline\\hline\n");
//							else
//								writer.write("\\\\\n");
						}
					}
					writer.write("\\hline\\hline\n");
				}
			}
			writer.write("\\end{tabular}\n");
			// writer.write("\\vspace*{-0.6cm}\n");
			writer.write("\\end{table*}\n");
			writer.flush();
			writer.close();
		}
	}

	private static void genData(String f) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for (String tech : techs) {
			for (int s = 0; s < curSubs.length; s++) {
				String curSub = curSubs[s];
				String[] vs = vers[s];
				int[] iterNum = getIterNum(curSub, tech, vs);
				for (int i = 0; i < vs.length; i++) {
					String ver = vs[i];
					System.out.println(">>>>results for " + curSub + "-" + ver);
					String path = work + curSub + "/" + tech + File.separator
							+ ver + "-invariants";
					for (int iter = 0; iter < iterNum[i]; iter++) {
						String file = path + File.separator
								+ "symbolic_output_iter_" + (iter + 1) + ".txt";
						SymbcResParser.parseSymbcOut(file, tech, ver, iter,
								writer, curSub);
						SymbcResParser.parseGreenOut(tech, ver, iter, writer,
								curSub);
						InvResParser.parseInvOut(path, tech, ver, iter, writer,
								curSub, s);
						InvCostParser.parse(work, tech, ver, iter, writer,
								curSub);
						// CovResParser.parseCovOut(tech, sub, iter, writer,
						// curSub);
					}
					InvResParser.parseInvOut(path, tech, ver, iterNum[i],
							writer, curSub, s);
					InvCostParser.parse(work, tech, ver, iterNum[i], writer,
							curSub);
				}
			}
		}
		writer.flush();
		writer.close();
	}

	public static int[] getIterNum(String sub, String tech, String[] vs) {
		int[] res = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			File d = new File(work + sub + "/" + tech + File.separator + vs[i]
					+ "-invariants");
			// System.out.println(d.getAbsolutePath());
			File[] files = d.listFiles();
			int num = 0;
			for (File file : files) {
				if (file.getName().startsWith("symbolic_output")
						&& !file.getName().equals("symbolic_output_iter_0.txt"))
					num++;
			}
			res[i] = num;
		}
		return res;
	}

	public static String getTechName(String tech) {
		if (tech.equals("undesugar-ungreen"))
			return "UndesugarUngreen";
		else if (tech.equals("undesugar-green"))
			return "UndesugarUngreen";
		else if (tech.equals("desugar-green"))
			return "DesugarUngreen";
		else if (tech.equals("desugar-5-ungreen"))
			return "DesugarUngreenOne";
		else if (tech.equals("desugar-10-ungreen"))
			return "DesugarUngreenTwo";
		else if (tech.equals("desugar-15-ungreen"))
			return "DesugarUngreenThree";
		else if (tech.equals("desugaropone-ungreen"))
			return "DesugarOpOneUngreen";
		else if (tech.equals("desugaroptwo-ungreen"))
			return "DesugarOpTwoUngreen";
		else if (tech.equals("desugar-rand05-ungreen"))
			return "DesugarRandZeroFiveUngreen";
		else if (tech.equals("desugar-rand10-ungreen"))
			return "DesugarRandOneZeroUngreen";
		else if (tech.equals("desugar-rand15-ungreen"))
			return "DesugarRandOneFiveUngreen";
		else if (tech.equals("desugar-rand20-ungreen"))
			return "DesugarRandTwoZeroUngreen";
		else if (tech.equals("desugar-rand100-ungreen"))
			return "DesugarRandOneZeroZeroUngreen";

		return "DesugarUngreen";
	}

	public static String getSubName(String curSub, String v) {
		if (v.equals("v1"))
			return curSub + "One";
		else if (v.equals("v2"))
			return curSub + "Two";
		else if (v.equals("v3"))
			return curSub + "Three";
		else if (v.equals("v4"))
			return curSub + "Four";
		else
			return curSub + "Five";
	}

}
