package edu.utexas.gsoc.random;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utexas.gsoc.symbc.SymbcInputGenerator;

public class AswV1Gen extends MetaGenerator {

	public AswV1Gen() {
		super();
	}

	public List<String> generate(int limit) {
		List<String> tests = new ArrayList<String>();
		for (int i = 0; i < limit; i++)
			tests.add(getDouble() + " " + getBoolean() + " " + getBoolean()
					+ " " + getBoolean() + " " + getBoolean() + " "
					+ getDouble() + " " + getDouble() + " " + getBoolean()
					+ " " + getDouble() + " " + getBoolean() + " "
					+ getBoolean() + " " + getBoolean() );
		return tests;
	}

	public static void genCovDriver(boolean symbc, double ratio)
			throws IOException {
		AswV1Gen gen = new AswV1Gen();
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(
						"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/asw/cov-subjects/v1"
								+ "/aswnew/ASWDriver.java"));
		StringBuilder string = new StringBuilder();
		string.append("package aswnew;\n");
		string.append("import java.io.IOException;\n");
		string.append("import java.io.FileInputStream;\n");
		string.append("import java.util.Properties;\n");

		string.append("public class ASWDriver {\n");
		string.append("	public static void main(String[] args) {\n");
		string.append("		int res = 0;\n");
		string.append("	asw myAsw = new asw();\n");
		string.append("	myAsw.Init3();\n");
		List<String> symbcTests = SymbcInputGenerator
				.readSymbcOutput("/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/asw/initial-tests/v1-symbolic_output.txt");
		// string.append("	  System.out.println(\"start! \");\n");
		List<String> tests = symbcTests;
		if (!symbc) {
			tests = gen.generate(symbcTests.size());
			BufferedWriter tstWriter = new BufferedWriter(
					new FileWriter(
							"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/initial-inputs/asw-v1-rand.txt"));
			for (String test : tests) {
				tstWriter.write(test + "\n");
			}
			tstWriter.flush();
			tstWriter.close();
		}
		for (int i = 0; i < tests.size() * ratio; i++) {
			String test = tests.get(i);
			String[] items = test.trim().split(" ");
			string.append("   myAsw.Main0(");
			for (int j=0;j<items.length-1;j++){
				String item=items[j];
				string.append(item + ",");
			}
			string.append(items[items.length-1]+");\n");
		}

		string.append("	}\n");
		string.append("	}");
		writer.write(string.toString());
		writer.flush();
		writer.close();
		System.out.println("Test Number: "+tests.size()*ratio);
	}

}
