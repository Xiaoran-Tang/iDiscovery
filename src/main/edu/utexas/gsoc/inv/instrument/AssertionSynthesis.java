/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv.instrument;

import java.io.IOException;

public class AssertionSynthesis {
	public static int optimizationLevel;
	public static int AssertNum = 1;
	public static int TestPerAssert = 1;
	public static boolean filterFailedInv = true;
	public static boolean sepassert = true;

	public static void main(String[] args) throws IOException {
		// assert the number of parameters to be three
		assert (args.length == 5);

		// check the form of assertions
		if (args[4].startsWith("separation-restriction"))
			optimizationLevel = 1;
		else if (args[4].startsWith("separation-unrestriction"))
			optimizationLevel = 2;
		else if (args[4].startsWith("unseparation-restriction"))
			optimizationLevel = 3;
		else
			optimizationLevel = 4;

		// new the ast parser
		ASTTreeParser astParser = new ASTTreeParser(args[0], args[1], args[2],
				args[3]);
		// parse the ast
		astParser.parseAST();

		// instrument the program based on ast parsing results
		Instrumenter.instrument();
	}
}
