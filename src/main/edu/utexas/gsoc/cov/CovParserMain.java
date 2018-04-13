package edu.utexas.gsoc.cov;

import java.io.File;
import java.io.IOException;

public class CovParserMain {
	public static void main(String[] args) throws IOException {
		String[] subs = { "tcas", "wbs", "asw", "rjc" };
		String[][] vers = { { "v1", "v2", "v3" }, { "v1" }, { "v1" }, { "v1" } };
		for (int s = 0; s < subs.length; s++) {
			String sub = subs[s];
			for (int v = 0; v < vers[s].length; v++) {
				String ver=vers[s][v];
				CovParser generator = new CovParser(new File(
						"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/"
								+ sub + "/cov-subjects/"+ver+"/target"), "");
				generator.create();
			}
		}
	}
}
