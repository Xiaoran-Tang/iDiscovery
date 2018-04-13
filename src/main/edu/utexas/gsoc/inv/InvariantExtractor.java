/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

public class InvariantExtractor {
	private static Logger logger=Logger.getLogger(InvariantExtractor.class);
	public static void main(String[] args) throws IOException {
		String file1 = args[0];
		String meth = args[1];
		logger.debug("method under test is: "+meth);
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
		InvariantParser parser1 = new InvariantParser(file1, meth);
		

		writer.write("====pre-invariants=====\n");
		diff(parser1.getusedPreInvs(), writer);
		writer.write("====post-invariants=====\n");
		diff(parser1.getusedPostInvs(), writer);
		writer.flush();
		writer.close();
	}

	public static void diff(Set<String> invs1, BufferedWriter writer) throws IOException {
		logger.debug("start"+invs1.size());
		for (String inv1 : invs1) {
			writer.write(inv1 + "\n");
			logger.debug("extracting "+inv1);
		}
	}

}
