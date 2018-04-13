package edu.utexas.gsoc.random;

import java.io.IOException;

public class CovMain {
	public static void main(String[] args) throws IOException{
		double ratio=0.2;
		boolean symbc=false;
		TcasV1Gen.genCovDriver(symbc, ratio);
		TcasV2Gen.genCovDriver(symbc, ratio);
		TcasV3Gen.genCovDriver(symbc, ratio);
		WbsV1Gen.genCovDriver(symbc, ratio);
		RjcV1Gen.genCovDriver(symbc, ratio);
		AswV1Gen.genCovDriver(symbc, ratio);
		}

}
