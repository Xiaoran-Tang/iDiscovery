package rjc;

public class RJCDriver {
	
	public static void main(String[] args){
		
		RJCMain rjcmain = new RJCMain();
		rjcmain.DoSimSymbolic(Double.parseDouble(args[0]),
				Double.parseDouble(args[1]), Double.parseDouble(args[2]),
				Double.parseDouble(args[3]), Double.parseDouble(args[4]),
				Double.parseDouble(args[5]));
	}

}
