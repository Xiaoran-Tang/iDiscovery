package edu.utexas.gsoc.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvCostParser {

	public static void parse(String work, String tech, String ver, int iter,
			BufferedWriter writer, String curSub) throws IOException {
		String techName = TableGenerator.getTechName(tech);
		String subName = TableGenerator.getSubName(curSub, ver);
		File file = null;
		if (curSub.equals("tcas"))
			file = new File(work + curSub
					+ File.separator + ver + "-" + tech + "-time.log");
		else
			file = new File(work + curSub
				//	+ File.separator+ ver+ "-" + tech + "-time.log");//need to be changed when collecting non-op table
		+ File.separator + tech + "-time.log");//need to be changed when collecting non-op table

		if (!file.exists()) {
			writer.write("\\newcommand{\\" + techName + subName
					+ OldTableGenerator.iters[iter] + "Trace}{NA}\n");
			writer.write("\\newcommand{\\" + techName + subName
					+ OldTableGenerator.iters[iter] + "Infer}{NA}\n");
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		List<String> traceTime = new ArrayList<String>();
		List<String> inferTime = new ArrayList<String>();
		while (line != null) {
			//System.out.println(line);
			if (line.startsWith("user	")) {
				String time =formatTime(line);
				//System.out.println("ha: "+time);
				if (traceTime.size() > inferTime.size()) {
					inferTime.add(time);
				} else
					traceTime.add(time);
			}
			line = reader.readLine();
		}
		reader.close();
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "Trace}{" + traceTime.get(iter)
				+ "}\n");
		writer.write("\\newcommand{\\" + techName + subName
				+ OldTableGenerator.iters[iter] + "Infer}{" + inferTime.get(iter)
				+ "}\n");
	}
	static String formatTime(String line){
		line=line.replace("user	", "").trim();
		String[] item=line.split("m");
		String m=item[0];
		String s=item[1].substring(0,item[1].indexOf("."));
		int im=Integer.parseInt(m);
		int ih=0;
		if(im>60){
			ih=im/60;
			im=im%60;
		}
		int is=Integer.parseInt(s);
		return formatInteger(ih)+":"+formatInteger(im)+":"+formatInteger(is);
	}
	public static String formatInteger(int s){
		DecimalFormat format=new DecimalFormat("00");
		return format.format(s);
	}
}
