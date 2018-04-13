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

public class GreenTableGenerator {

	// static String[] subs = { "v1", /* "v2", "v3"/*, "v4", "v5" */};
	static String[] techs = { "undesugar-ungreen",
			"desugar-ungreen" };
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
	static String[][] vers = { { "v1", "v2", "v3" }, { "v1" } };
	static String[] curSubs = { "tcas", "wbs" };
	static String[] meths = {
			"tcas.TCAS.alt_sep_test()",
			"wbs.WBS.update(int, boolean, boolean)",
			"aswnew.asw.Main0(double, boolean, boolean, boolean, boolean, double, double, boolean, double, boolean, boolean, boolean)",
			"rjc.rjc.MainSymbolic(double, double, double, double, double, double, double[], double[])" };
	// static String work =
	// "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/";

	static String work = "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/";
	static String paper = "/Users/zhanglingming/Research/papers/issta2014/";

	// static String dir =
	// "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"
	// + curSub + "/";

	public static void main(String[] args) throws IOException {

		genCostTable(paper + "tables/");
	}



	private static void genCostTable(String f) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(f
				+ "green-table.tex"));
		writer.write("\\begin{table}[h!]\n");
		writer.write("\\center\\scriptsize");
		writer.write("\\caption{\\label{tab:green-table"
				+ "}Symbolic execution costs for \\idisc{} with/without \\gree{}}\n");
		writer.write("\\begin{tabular}{|c|c||cc||cc|}\n");
		writer.write("\\hline\n");
		writer.write("Subjects&Iter.&\\multicolumn{2}{c||}{Non-optimizations}&\\multicolumn{2}{c|}{Both Optimizations}\\\\\\cline{3-6}");
		writer.write("&");
		for (int i = 0; i < 2; i++) {
			writer.write("&No Green &(Green)");
		}
		writer.write("\\\\\n");
		writer.write("\\hline\\hline\n");
		for (int s = 0; s < curSubs.length; s++) {
			String curSub = curSubs[s];
			String[] vs = vers[s];

			for (int i = 0; i < vs.length; i++) {
				String ver = vs[i];
				String subName = getSubName(curSub, ver);
				int[] iterNum = getIterNum(curSub, techs[0], vs);
				for (int iter = 0; iter < iterNum[i]; iter++) {
					if (iter == 0) {
						writer.write("\\" + curSub);
						if (curSub.equals("tcas"))
							writer.write("-" + ver);
					}
					String iterName = iters[iter];
					writer.write("&" + (iter + 1) + " ");
					for (String tech : techs) {
						int[] curIterNum = getIterNum(curSub, tech, vs);
						if (curIterNum[i] <= iter) {
							writer.write("& --& --");
							continue;
						}
						String techName = TableGenerator.getTechName(tech);
						String prefix = "\\" + techName + subName + iterName;
						String[] vals = {  "TestGenTime","GreenTime" };

								for (String v : vals)
									writer.write("&" + prefix + v);
					}
					if (iter == (iterNum[i] - 1))
						writer.write("\\\\\\hline\n");
					else
						writer.write("\\\\\n");
				}
				writer.write("\\hline\n");
			}
		}
		writer.write("\\end{tabular}\n");
		writer.write("\\end{table}\n");
		writer.flush();
		writer.close();
	}


	public static int[] getIterNum(String sub, String tech, String[] vs) {
		int[] res = new int[vs.length];
		for (int i = 0; i < vs.length; i++) {
			File d = new File(work + sub + "/" + tech + File.separator + vs[i]
					+ "-invariants");
			System.out.println(">>" + d.getAbsolutePath());
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
