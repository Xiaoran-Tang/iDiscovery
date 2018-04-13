package edu.utexas.gsoc.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenFakeCov {
	public static void main(String[] args) throws IOException{
		String[] vs={"v1","v2","v3","v4","v5"};
		String[] ts={"undesugar-ungreen","desugar-ungreen"};
		BufferedWriter wer=new BufferedWriter(new FileWriter("/Users/zhanglingming/Research/tools/gsoc2013/svvat-nasa/GSoC2013/wbs/fakecov.sh"));
		for(String v:vs){
			for(int i=0;i<6;i++){
				for(String t:ts)
				wer.write("cp cov-subjects/"+v+"/cov-"+t+"-"+i+".txt cov-subjects/"+v+"/cov-"+t.replace("-ungreen", "-green")+"-"+i+".txt\n");
			}
			
		}
		wer.flush();
		wer.close();
		
	}

}
