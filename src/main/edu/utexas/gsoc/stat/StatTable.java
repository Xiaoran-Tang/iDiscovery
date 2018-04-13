package edu.utexas.gsoc.stat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utexas.gsoc.utils.NumberPrinter;

public class StatTable {
	static String[] curSubs = { "tcas-v1", "tcas-v2", "tcas-v3", "wbs", "asw",
			"rjc" };

	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/zhanglingming/Research/papers/issta2014/tables/"
				+ "stat-table.tex"));
		BufferedReader reader=new BufferedReader(new FileReader("Stat.res"));
		List<String> pearson=new ArrayList<String>();
		List<String> spearman=new ArrayList<String>();
		String line=reader.readLine();
		while(line!=null){
			if(pearson.size()<6)
				pearson.add(line);
			else
				spearman.add(line);
			line=reader.readLine();
		}
		reader.close();
		
		writer.write("\\begin{table}[h!]\n");
		writer.write("\\center\\scriptsize");
		writer.write("\\caption{\\label{tab:stat-table"
				+ "}Correlation analysis between initial test coverage and number of added invariants by \\idisc{}}\n");
		writer.write("\\begin{tabular}{|c||ccc||ccc|}\n");
		writer.write("\\hline\n");
		writer.write("Subjects&\\multicolumn{3}{c||}{Pearson's $r$}&\\multicolumn{3}{c|}{Spearman's $\\rho$}\\\\\\cline{2-7}");
		for (int i = 0; i < 2; i++) {
			writer.write("&Inst.&Branch&Line");
		}
		writer.write("\\\\\n");
		writer.write("\\hline\\hline\n");
		for (int s = 0; s < curSubs.length; s++) {
			String curSub = curSubs[s];
			writer.write(curSub);
			String[] pitems=pearson.get(s).split(" ");
			String[] sitems=spearman.get(s).split(" ");
			for(String pitem:pitems){
				if(pitem.equals("NA"))
					writer.write("&--");
				else
				writer.write("&"+NumberPrinter.format3d(Double.parseDouble(pitem)));
			}
			for(String sitem:sitems){
				if(sitem.equals("NA"))
					writer.write("&--");
				else
				writer.write("&"+NumberPrinter.format3d(Double.parseDouble(sitem)));
			}
			writer.write("\\\\\n");
		}
		writer.write("\\hline");
		writer.write("\\end{tabular}\n");
		writer.write("\\end{table}\n");
		writer.flush();
		writer.close();

	}
}
