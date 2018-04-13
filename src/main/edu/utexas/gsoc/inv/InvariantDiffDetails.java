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

public class InvariantDiffDetails {
	public static void main(String[] args) {
		String file1 = args[0];
		String file2 = args[1];
		String meth = args[2];
		InvariantParser parser1 = new InvariantParser(file1, meth);
		InvariantParser parser2 = new InvariantParser(file2, meth);

		//diffDetails(parser1.getUsedAllInvs(), parser2.getUsedAllInvs());
		//diffDetails(parser1.getUsedPreInvs(), parser2.getUsedPreInvs());
		diffDetails(parser1.getActualUsedPostInvs(), parser2.getActualUsedPostInvs());

		//diffDetails(parser1.getAllInvs(), parser2.getAllInvs());
		//diffDetails(parser1.getPreInvs(), parser2.getPreInvs());
		diffDetails(parser1.getPostInvs(), parser2.getPostInvs());
		System.out.println();
	}

	public static void diffDetails(Set<String> invs1, Set<String> invs2) {
		int common = 0;
		System.out.println(">>>>i1-i2:");
		for (String inv1 : invs1) {
			System.out.println(inv1);
			if (!invs2.contains(inv1))
				System.out.println(inv1);
		}
		System.out.println(">>>>i2-i1:");
		for(String inv2:invs2){
			if(!invs1.contains(inv2))
				System.out.println(inv2);
		}
		
	}

}
