/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.inv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

public class InvariantParser {
	public Set<String> preInvs;
	public Set<String> postInvs;
	public Set<String> allInvs;
	public Set<String> actualUsedPreInvs;
	public Set<String> actualUsedPostInvs;
	public Set<String> actualUsedAllInvs;
	public Set<String> usedPreInvs;
	public Set<String> usedPostInvs;
	public Set<String> usedAllInvs;
	public Map<String, String> oldVars;
	public Map<String, String> invMap;
	public int varCounter = 0;

	public static String ENTER = ":::ENTER";
	public static String EXIT = ":::EXIT";

	private static Logger logger = Logger.getLogger(InvariantParser.class);

	public InvariantParser(String file, String meth) {
		preInvs = new HashSet<String>();
		postInvs = new HashSet<String>();
		allInvs = new HashSet<String>();
		actualUsedPreInvs = new HashSet<String>();
		actualUsedPostInvs = new HashSet<String>();
		actualUsedAllInvs = new HashSet<String>();
		usedPreInvs = new HashSet<String>();
		usedPostInvs = new HashSet<String>();
		usedAllInvs = new HashSet<String>();
		oldVars = new HashMap<String, String>();
		invMap = new HashMap<String, String>();

		File invFile = new File(file);
		logger.debug("parsing inv file: " + invFile.getAbsolutePath());
		logger.debug("for method: " + meth);
		try {
			Scanner scanner = new Scanner(invFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.equals(meth + ENTER)) {
					while (scanner.hasNextLine()) {
						line = scanner.nextLine().trim();
						if (line.startsWith("==")
								|| line.startsWith("Exiting Daikon."))
							break;
						preInvs.add(line);
						allInvs.add(line);
						if (!line.contains("\\new")
								&& !line.contains(".getClass()")
								&& !line.contains("%")
								&& !containScientific(line)) {
							logger.debug("parsing used pre-invariant: " + line);
							usedPreInvs.add(line);
							usedAllInvs.add(line);
							line = preprocess(line);
							actualUsedPreInvs.add(line);
							actualUsedAllInvs.add(line);
						}
					}
				} else if (line.equals(meth + EXIT)) {
					while (scanner.hasNextLine()) {
						line = scanner.nextLine().trim();
						if (line.startsWith("==")
								|| line.startsWith("Exiting Daikon."))
							break;
						postInvs.add(line);
						allInvs.add(line);
						if (!line.contains("\\new")
								&& !line.contains(".getClass()")
								&& !line.contains("%")&& !containScientific(line)) {
							logger.debug("parsing used post-invariant: " + line);
							usedPostInvs.add(line);
							usedAllInvs.add(line);
							line = preprocess(line);
							actualUsedPostInvs.add(line);
							actualUsedAllInvs.add(line);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String preprocess(String constraint) {
		String res = constraint;
		// System.out.println(res);
		while (res.contains("\\old(")) {
			int index = res.indexOf("\\old(");
			String temp = res.substring(index + 5);
			String oldVar = getOldVar(temp);
			if (!oldVars.containsKey(oldVar)) {
				oldVars.put(oldVar, "iDiscovery_PreVariable_" + (varCounter++));
			}
			res = res.replace("\\old(" + oldVar + ")", oldVars.get(oldVar));
		}
		if (res.contains("\\result")) {
			if (!oldVars.containsKey("\\result"))
				oldVars.put("\\result", "iDiscovery_Result");
			res = res.replace("\\result", oldVars.get("\\result"));
		}
		invMap.put(res, constraint);
		// System.out.println(">>" + res);
		return res;
	}

	public String getOldVar(String temp) {
		int i = 0;
		int match = 0;
		while (i < temp.length()) {
			if (temp.charAt(i) == '(') {
				match++;
			}
			if (temp.charAt(i) == ')') {
				if (match > 0)
					match--;
				else
					break;
			}
			i++;
		}
		return temp.substring(0, i);
	}

	public boolean containScientific(String inv) {
		if(inv.contains("E7 ") ||inv.contains("E8 ") ||inv.contains("E9 ") || inv.contains("E10 ")
				|| inv.contains("E11 ") || inv.contains("E12 ")
				|| inv.contains("E13 ") || inv.contains("E14 "))
		{
			//System.out.println(inv);
			return true;
		}
		//System.out.println(inv);
		return false;
	}

	public Set<String> getPreInvs() {
		return preInvs;
	}

	public Set<String> getPostInvs() {
		return postInvs;
	}

	public Set<String> getActualUsedPreInvs() {
		return actualUsedPreInvs;
	}

	public Set<String> getActualUsedPostInvs() {
		return actualUsedPostInvs;
	}

	public Set<String> getActualUsedAllInvs() {
		return actualUsedAllInvs;
	}

	public Set<String> getusedPreInvs() {
		return usedPreInvs;
	}

	public Set<String> getusedPostInvs() {
		return usedPostInvs;
	}

	public Set<String> getusedAllInvs() {
		return usedAllInvs;
	}

	public Set<String> getAllInvs() {
		return allInvs;
	}

}
