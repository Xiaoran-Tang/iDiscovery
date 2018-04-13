package edu.utexas.gsoc.random;

import java.io.BufferedReader;
import java.util.Random;

public class MetaGenerator {
	long seed = 123456789;
	Random rand;
	public MetaGenerator() {
		rand = new Random(seed);
	}

	public boolean getBoolean() {
		return rand.nextBoolean();
	}

	public int getInt() {
		return rand.nextInt();
	}

	public int getRangeInt(int min, int max) {
		return (int) (((float)Math.abs(rand.nextInt()) )/ Integer.MAX_VALUE * (max - min) + min);
	}
	
	public double getDouble() {
		return rand.nextDouble();
	}

	public double getRangeDouble(double min, double max) {
		return  (Math.abs(rand.nextDouble()) ) * (max - min) + min;
	}

//	public static void main(String[] args) {
//		MetaGenerator gen = new MetaGenerator();
//		for (int i = 0; i < 100; i++) {
//			System.out.println(gen.getRangeInt(0, 3));
//		}
//	}


}
