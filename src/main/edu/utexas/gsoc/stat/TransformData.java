package edu.utexas.gsoc.stat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TransformData {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("stat.data"));
		BufferedWriter writer=new BufferedWriter(new FileWriter("/Users/zhanglingming/Research/papers/issta2014/data/data.txt"));
		String line = reader.readLine();
		while (line != null) {
			if (line.length() > 0) {
				String[] items = line.split("\t");
				for (String item : items) {
					if (item.contains("("))
						item = item.substring(item.indexOf("(") + 1,
								item.indexOf("%"));
					System.out.print(item + "\t");
					writer.write(item+"\t");
				}
			}
			System.out.println();
			writer.write("\n");
			line = reader.readLine();
		}
		writer.flush();
		writer.close();
	}
}
