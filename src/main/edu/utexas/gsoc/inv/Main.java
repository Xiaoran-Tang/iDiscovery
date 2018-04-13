package edu.utexas.gsoc.inv;

public class Main {

	public static void main(String[] args) {
		InvariantParser parser = new InvariantParser(
				"/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/rjc/desugar-ungreen/v1-invariants/daikon_invariants_iter_1.txt",
				"rjc.rjc.MainSymbolic(double, double, double, double, double, double, double[], double[])");
	}

}
