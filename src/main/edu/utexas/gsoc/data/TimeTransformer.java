package edu.utexas.gsoc.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TimeTransformer {
	static String dir="/Users/zhanglingming/Research/papers/issta2014/tables/";
	public static void main(String[] args) throws IOException{
		transform("old-data.tex", "data.tex");
		transform("old-opdata.tex", "opdata.tex");
		
		
	}
	
	public static void transform(String inpath, String outpath) throws IOException{
		BufferedReader reader=new BufferedReader(new FileReader(dir+inpath));
		BufferedWriter writer=new BufferedWriter(new FileWriter(dir+outpath));
		
		String line=reader.readLine();
		while(line!=null){
			if(!line.contains(":")){
				writer.write(line+"\n");
			}else{
				writer.write(format(line)+"\n");
			}
			
			line=reader.readLine();
		}
		writer.flush();
		writer.close();
		reader.close();
		
	}
	
	public static String format(String line){
		System.out.println(line);
		int start=line.lastIndexOf("{")+1;
		int end=line.length()-1;
		String orgtime=line.substring(start, end);
		if(orgtime.contains("(")){
			orgtime=orgtime.substring(1,orgtime.length()-1);
			String[] items=orgtime.split(":");
			int sec=Integer.parseInt(items[0])*3600+Integer.parseInt(items[1])*60+Integer.parseInt(items[2]);
			return line.substring(0,start)+"("+sec+"s)}";
		}else{
			String[] items=orgtime.split(":");
			int sec=Integer.parseInt(items[0])*3600+Integer.parseInt(items[1])*60+Integer.parseInt(items[2]);
			return line.substring(0,start)+sec+"s}";
		}
		
	}

}
