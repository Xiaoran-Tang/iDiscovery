/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.cov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WbsCovDriverGenerator {
	/*
	 * Takes subject, technique, and iteration number as arguments
	 */
	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(
						"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/cov-subjects/"
								+ args[0] + "/wbs/WBSDriver.java"));
		getTests(args[1], args[0], Integer.parseInt(args[2]));
		StringBuilder string = new StringBuilder();
		string.append("package wbs;\n");
		string.append("import java.io.IOException;\n");
		string.append("import java.io.FileInputStream;\n");
		string.append("import java.util.Properties;\n");

		string.append("public class WBSDriver {\n");
		string.append("	public static void main(String[] args) {\n");
		string.append("		int res = 0;\n");
		string.append("Properties properties = new Properties();\n");
		string.append("try {\n");
		string.append("properties.load(new FileInputStream(\"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/cov-subjects/"
				+ args[0] + "/wbs/" + "tests.properties\"));\n");
		string.append("} catch (IOException e) {\n");
		string.append("e.printStackTrace();}\n");
		string.append("	WBS wbs = new WBS();\n");
		// string.append("	  System.out.println(\"start! \");\n");
		string.append("for(String key : properties.stringPropertyNames()) {\n");
		// string.append("	  System.out.println(\"key: \"+key);\n");
		string.append("	  String value = properties.getProperty(key);\n");
		string.append("   String[] items=value.split(\",\");\n");
		string.append("   wbs.update(Integer.parseInt(items[0]), Boolean.parseBoolean(items[1]), Boolean.parseBoolean(items[2]));\n");
		string.append("}\n");
		string.append("	}\n");
		string.append("	}");
		writer.write(string.toString());
		writer.flush();
		writer.close();

	}

	public static void getTests(String tech, String sub, int n)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(
						"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/cov-subjects/"
								+ sub + "/wbs/" + "tests.properties"));
		String path = "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/"
				+ tech + "/" + sub + "-invariants/symbolic_output_iter_";
		List<String> allTests = new ArrayList<String>();
		allTests.addAll(getInitialTests());
		for (int i = 1; i <= n; i++) {
			allTests.addAll(getTestsForFile(path + i + ".txt"));
		}
		int testNum = 1;
		for (String test : allTests) {
			writer.write((testNum++) + " = " + test + "\n");
		}
		writer.flush();
		writer.close();
	}

	public static List<String> getInitialTests() throws IOException {
		String path = "/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/run_daikon.sh";
		BufferedReader reader = new BufferedReader(new FileReader(path));
		List<String> tests = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("java -cp")) {
				int index = line.lastIndexOf("wbs.WBS");
				tests.add(synthesisInputs(line.substring(index + 7).trim(), " "));
			}
			line = reader.readLine();
		}
		return tests;
	}

	public static List<String> getTestsForFile(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		List<String> tests = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			if (line.contains("<TEST>")) {
				String test = getTestInputForWbs(line);
				tests.add(test);
			}
			line = reader.readLine();
		}
		return tests;

	}

	public static String getTestInputForWbs(String line) {
		String test = "";
		line = line.substring(line.indexOf(":") + 1).trim();
		if (line.contains("\t-->"))
			line = line.substring(0, line.indexOf("\t"));
		test = synthesisInputs(line, ",");
		return test;
	}

	private static String synthesisInputs(String line, String splitter) {
		String test = "";
		String[] inputs = line.split(splitter);
		for (int i = 0; i < inputs.length; i++) {
			test += inputs[i];
			if (i < inputs.length - 1)
				test += ",";
		}
		return test;
	}
}
