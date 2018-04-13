/*******************************************************************************
 * Copyright 2013 Lingming Zhang
 * 
 * All rights reserved. This project was initially started during the 2013 Google Summer of Code program.
 * 
 * Contributors:
 * 	Lingming Zhang - initial design and implementation
 ******************************************************************************/
package edu.utexas.gsoc.utils;

import java.text.DecimalFormat;

public class NumberPrinter {

	public static String formatPerc(double d) {
		DecimalFormat format = new DecimalFormat("#0.00");
		return format.format(d * 100) + "%";
	}
	public static String format2d(double d) {
		DecimalFormat format = new DecimalFormat("#0.00");
		return format.format(d);
	}
	public static String format3d(double d) {
		DecimalFormat format = new DecimalFormat("#0.000");
		return format.format(d);
	}
	public static String format4d(double d) {
		DecimalFormat format = new DecimalFormat("#0.0000");
		return format.format(d);
	}
	
	public static void main(String[] args){
		System.out.println(formatPerc(0.666666));
		System.out.println(formatPerc(1/0));
	}
}
