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

public class OldTableGenerator {
	
	static String[] subs = { "v1", /*"v2", "v3"/*, "v4", "v5"*/ };
	static String[] techs = {"undesugar-ungreen", "desugar-ungreen", "undesugar-green", "desugar-green" };
	static String[] iters = { "IterOne", "IterTwo", "IterThree", "IterFour",
			"IterFive", "IterSix", "IterSeven", "IterEight", "IterNine",
			"IterTen" };
	static String meth = "wbs.WBS.update(int, boolean, boolean)";
//	public static String meth="tcas.TCAS.alt_sep_test()";
	//public static String meth="heap.HeapArrayV2.driver(int, int, int, int, int)";
//	public static String meth="aswnew.asw.Main0(double, boolean, boolean, boolean, boolean, double, double, boolean, double, boolean, boolean, boolean)";
//public static String meth="rjc.rjc.MainSymbolic(double, double, double, double, double, double, double[], double[])";
	static String curSub = "wbs";
//	static String curSub="asw";
//	static String curSub = "tcas";
//public static String curSub="rjc";
	static String dir = "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"+curSub+"/";

	public static void main(String[] args) throws IOException {

		genData("/Users/zhanglingming/Research/papers/tacas2014/tables/"
				+ curSub + "-data.tex");
		genTable("/Users/zhanglingming/Research/papers/tacas2014/tables/");
	}

	private static void genTable(String f) throws IOException {
		for (String tech : techs) {
			String techName = getTechName(tech);
			int[] iterNum=getIterNum(tech);
			BufferedWriter writer = new BufferedWriter(new FileWriter(f
					+ curSub + "-" + tech + "-table.tex"));
			writer.write("\\begin{table*}[h!]\n");
			writer.write("\\center\\scriptsize");
			writer.write("\\caption{\\label{tab:" + tech
					+ "}Experimental results for " + curSub +" with "+tech+ "}\n");
			writer.write("\\begin{tabular}{|c|c||c|c|c||c|c|c||c|c|c||c|c|c||c|c||c|c|}\n");
			writer.write("\\hline\n");
			writer.write("Subjects&Iter.&\\multicolumn{3}{c||}{UsedPreInvs}&\\multicolumn{3}{c||}{UsedPostInvs}&\\multicolumn{3}{c||}{AllPreInvs}&\\multicolumn{3}{c||}{AllPostInvs}&Instr.&Branch&Symbc-&Symbc-\\\\\\cline{3-14}");
			writer.write("&&");
			for (int i = 0; i < 4; i++) {
				writer.write("Num&Del&New&");
			}
			writer.write("Coverage&Coverage&TestNum&Time\\\\\n");
			writer.write("\\hline\\hline\n");
			for (int i=0;i<subs.length;i++) {
				String sub = subs[i];
				String subName = getSubName(sub);
				for (int iter = 0; iter < iterNum[i]; iter++) {
					if (iter == 0)
						writer.write(curSub + "-" + sub);
					String iterName = iters[iter];
					String prefix = "\\" + techName + subName + iterName;
					String[] ranges = { "Used", "All" };
					String[] locs = { "Pre", "Post" };
					String[] vals = { "Num", "Del", "New" };
					writer.write("&" + (iter + 1) + " ");
					for (String r : ranges) {
						for (String l : locs) {
							for (String v : vals)
								writer.write("&" + prefix + r + l + v);
						}
					}
					writer.write("&" + prefix + "ICov" + "&" + prefix + "BCov"
							+ "&" + prefix + "TestNum &" + prefix
							+ "TestGenTime");
					if (iter == (iterNum[i]-1))
						writer.write("\\\\\\hline\n");
					else
						writer.write("\\\\\\cline{2-18}\n");
				}
				writer.write("\\hline\n");
			}
			writer.write("\\end{tabular}\n");
			writer.write("\\end{table*}\n");
			writer.flush();
			writer.close();
		}
	}

	private static void genData(String f) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for (String tech : techs) {
			int[] iterNum=getIterNum(tech);
			for (int i=0;i<subs.length;i++) {
				String sub=subs[i];
				System.out.println(">>>>results for " + curSub + "-" + sub);
				String path = dir + tech + File.separator + sub + "-invariants";
				for (int iter = 0; iter < iterNum[i]; iter++) {
					String file = path + File.separator
							+ "symbolic_output_iter_" + (iter + 1) + ".txt";
					SymbcResParser.parseSymbcOut(file, tech, sub, iter, writer, curSub);
					//InvResParser.parseInvOut(tech, sub, iter, writer, curSub);
					CovResParser.parseCovOut(tech, sub, iter, writer, curSub);
				}
			}
		}
		writer.flush();
		writer.close();
	}
	
	public static int[] getIterNum(String tech){
		int[] res=new int[subs.length];
		for(int i=0;i<subs.length;i++){
			File d=new File(dir + tech + File.separator + subs[i] + "-invariants"); 
			File[] files=d.listFiles();
			int num=0;
			for(File file:files){
				if(file.getName().startsWith("symbolic_output"))
					num++;
			}
			res[i]=num;
		}
		return res;
	}

	public static String getTechName(String tech) {
		if (tech.equals("undesugar-ungreen"))
			return "UndesugarUngreen";
		else if (tech.equals("undesugar-green"))
			return "UndesugarGreen";
		else if (tech.equals("desugar-green"))
			return "DesugarGreen";
		return "DesugarUngreen";
	}

	public static String getSubName(String sub) {
		if (sub.equals("v1"))
			return curSub + "One";
		else if (sub.equals("v2"))
			return curSub + "Two";
		else if (sub.equals("v3"))
			return curSub + "Three";
		else if (sub.equals("v4"))
			return curSub + "Four";
		else
			return curSub + "Five";
	}

}
