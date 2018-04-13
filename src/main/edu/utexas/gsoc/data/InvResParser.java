/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import edu.utexas.gsoc.inv.InvariantParser;

public class InvResParser {
	public static void parseInvOut(String dir, String tech, String ver,
			int iter, BufferedWriter writer, String subject, int s) throws IOException {
		String techName = TableGenerator.getTechName(tech);
		String subName = TableGenerator.getSubName(subject,ver);
		//String dir="/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"+subject;
		
		String initInvFile = dir+ File.separator
				+ "daikon_invariants_iter_1.txt";
		String curInvFile = dir + File.separator
				+ "daikon_invariants_iter_" + (iter + 1) + ".txt";
		InvariantParser initParser = new InvariantParser(initInvFile,
				TableGenerator.meths[s]);
		InvariantParser curParser = new InvariantParser(curInvFile,
				TableGenerator.meths[s]);
		int[] res = diff(initParser.getusedPreInvs(),
				curParser.getusedPreInvs());
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPreNum}{" + res[0] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPreDel}{" + res[1] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPreNew}{" + res[2] + "}\n");

		int[] postRes = diff(initParser.getusedPostInvs(), curParser.getusedPostInvs());

		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPostNum}{" + postRes[0] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPostDel}{" + postRes[1] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedPostNew}{" + postRes[2] + "}\n");
		
		
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedAllNum}{" + (res[0] +postRes[0])+ "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedAllDel}{" +(res[1] +postRes[1]) + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "UsedAllNew}{" + (res[2] +postRes[2])+ "}\n");
		
		System.out.println(iter+": "+(res[0] +postRes[0])+" "+(res[1] +postRes[1])+" "+(res[2] +postRes[2]));

		res = diff(initParser.getPreInvs(), curParser.getPreInvs());

		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPreNum}{" + res[0] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPreDel}{" + res[1] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPreNew}{" + res[2] + "}\n");

		res = diff(initParser.getPostInvs(), curParser.getPostInvs());

		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPostNum}{" + res[0] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPostDel}{" + res[1] + "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "AllPostNew}{" + res[2] + "}\n");
	}

	public static int[] diff(Set<String> invs1, Set<String> invs2) {
		int[] res = new int[3];
		int common = 0;
		for (String inv1 : invs1) {
			if (invs2.contains(inv1))
				common++;
		}
		res[0] = invs2.size();
		res[1] = invs1.size() - common;
		res[2] = invs2.size() - common;
		return res;
	}

}
