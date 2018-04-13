/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv;

import java.util.Set;

public class InvariantDiffer {
	public static void main(String[] args) {
		String file1 = args[0];
		String file2 = args[1];
		String meth = args[2];
		InvariantParser parser1 = new InvariantParser(file1, meth);
		InvariantParser parser2 = new InvariantParser(file2, meth);

		diff(parser1.getActualUsedAllInvs(), parser2.getActualUsedAllInvs());
		diff(parser1.getActualUsedPreInvs(), parser2.getActualUsedPreInvs());
		diff(parser1.getActualUsedPostInvs(), parser2.getActualUsedPostInvs());

		diff(parser1.getAllInvs(), parser2.getAllInvs());
		diff(parser1.getPreInvs(), parser2.getPreInvs());
		diff(parser1.getPostInvs(), parser2.getPostInvs());
		System.out.println();
	}

	public static void diff(Set<String> invs1, Set<String> invs2) {
		int common = 0;
		for (String inv1 : invs1) {
			if (invs2.contains(inv1))
				common++;
		}
		System.out.print(common + "\t" + (invs1.size() - common) + "\t"
				+ (invs2.size() - common)+"\t");
	}

}
