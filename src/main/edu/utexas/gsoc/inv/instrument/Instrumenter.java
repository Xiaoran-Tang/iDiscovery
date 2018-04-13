/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv.instrument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.utexas.gsoc.inv.InvariantParser;

public class Instrumenter {
	public static boolean hasReturn = false;
	public static Set<String> arrays;
	private static Logger logger = Logger.getLogger(Instrumenter.class);

	public static void instrument() throws IOException {
		arrays = new HashSet<String>();
		InvariantParser invParser = new InvariantParser(ASTTreeParser.invFile,
				ASTTreeParser.srcMethod);

		if (!ASTTreeParser.retType.equals("void")) {
			hasReturn = true;
		}
		// read original java file and store in a list
		List<String> src = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(
				ASTTreeParser.origJavaFile));
		String line = reader.readLine();
		while (line != null) {
			src.add(line);
			line = reader.readLine();
		}
		reader.close();

		// start to generate new java file with assertions
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				ASTTreeParser.genJavaFile));

		// write the original java file before the type root starts
		for (int i = 0; i < ASTTreeParser.importLine; i++) {
			writer.write(src.get(i) + "\n");
		}

		// write the additional import statement
		writer.write("import gov.nasa.jpf.vm.Verify;\n");

		// write the original java file before the tested method
		for (int i = ASTTreeParser.importLine; i < ASTTreeParser.startLine; i++) {
			writer.write(src.get(i) + "\n");
		}

		// generate variable definitions for newly introduced variables
		writer.write("\n\t\t// iDiscovery: new variables introduced by iDiscovery\n");
		for (String oldVar : invParser.oldVars.keySet()) {
			if (oldVar.equals("\\result"))
				continue;
			// System.out.println(oldVar);
			// for(String key: ASTTreeParser.typeMap.keySet()){
			// System.out.print(key+", "+type);
			// }
			String type = Utils.getType(ASTTreeParser.typeMap,oldVar);
			//System.out.println(oldVar+"->"+type);
			if(oldVar.contains("(")){
				type="int";
			}
			else if (type.contains("[]"))
				arrays.add(invParser.oldVars.get(oldVar));
			writer.write("\t\t" + type + " " + invParser.oldVars.get(oldVar)
					+ "=" + oldVar + ";\n");
		}

		// generate pre-condition constraints
		writer.write("\n\t\t// iDiscovery: pre-condition invariants injected by iDiscovery\n");
		for (String inv : invParser.actualUsedPreInvs) {
			writer.write(getAssertion(inv, invParser) + "\n");
		}

		writer.write("\n\t\t// original method body begins\n");
		for (int i = ASTTreeParser.startLine; i < ASTTreeParser.endLine - 1; i++) {
			String l = src.get(i).trim();
			if (l.startsWith("return ")) {
				writer.write("\t\t" + ASTTreeParser.retType
						+ " iDiscovery_Result=" + l.replace("return ", "")
						+ "\n");
				break; // TODO: make it also work for multiple exit points
			} else
				writer.write(src.get(i) + "\n");
		}
		writer.write("\t\t// original method body ends\n\n");

		// generate post-condition constraints
		writer.write("\n\t\t// iDiscovery: post-condition invariants injected by iDiscovery\n");
		for (String inv : invParser.actualUsedPostInvs) {
			writer.write(getAssertion(inv, invParser) + "\n");
		}
		if (hasReturn) {
			writer.write("\t\treturn iDiscovery_Result;\n");
		}

		// write the rest of the original file
		for (int i = ASTTreeParser.endLine - 1; i < src.size(); i++) {
			writer.write(src.get(i) + "\n");
		}

		writer.flush();
		writer.close();
	}

	public static String getAssertion(String inv, InvariantParser invParser) {
		// if the invariant contains array equality comparison, we would discard
		// it since it will be subsumed by other constraints
		for (String a : arrays) {
			if (inv.contains(a) && inv.contains("==")) {
				logger.debug("discard assertion: " + inv
						+ " because it contains array comparison for " + a);
				return "";
			}
		}

		// if the user wants the desugared version assertions
		if (AssertionSynthesis.optimizationLevel==4)
			return "\t\t" + "assert(" + inv + ");";
		
		if (AssertionSynthesis.optimizationLevel==3)
			return "\t\t" + "if (Verify.getCounter("
			+ (AssertionSynthesis.AssertNum) + ") < "
			+ AssertionSynthesis.TestPerAssert + "&& !(" + inv + ")) {\n"
			+ "\t\t\tVerify.incrementCounter("
			+ (AssertionSynthesis.AssertNum++) + ");\n"
			+ "\t\t\tthrow new AssertionError(\""
			+ invParser.invMap.get(inv).replace("\\", "\\\\").replace("\"", "\\\"") + "\");\n"
			+ "\t\t}\n";
		
		if (AssertionSynthesis.optimizationLevel==2)
			return "\t\t"+"if(Verify.getBoolean()){\n"+
			"\t\t\t" + "assert(" + inv + ");"+
			"\t\t\tVerify.ignoreIf(true);\n"+
			 "\t\t}\n";
		

		// if the user does not want the desugared version assertions
		return "\t\t"+"if(Verify.getBoolean()){\n"+
		"\t\t\t" + "if (Verify.getCounter("
		+ (AssertionSynthesis.AssertNum) + ") < "
		+ AssertionSynthesis.TestPerAssert + "&& !(" + inv + ")) {\n"
		+ "\t\t\t\tVerify.incrementCounter("
		+ (AssertionSynthesis.AssertNum++) + ");\n"
		+ "\t\t\t\tthrow new AssertionError(\""
		+ invParser.invMap.get(inv).replace("\\", "\\\\").replace("\"", "\\\"") + "\");\n"
		+ "\t\t\t}\n"
		+"\t\t\tVerify.ignoreIf(true);\n"+
		 "\t\t}\n";
	
	}

}
