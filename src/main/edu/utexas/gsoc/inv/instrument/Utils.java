package edu.utexas.gsoc.inv.instrument;

import java.util.Map;

public class Utils {

	public static String getType(Map<String, String> typeMap, String oldVar) {
		String type = typeMap.get(oldVar);
		if (type == null) {
			String[] items = oldVar.split("\\.");
			// System.out.println(oldVar+" "+oldVar.indexOf("."));
			String ini = items[0];
			int i = 0;
			while (!typeMap.containsKey(ini)) {
				if (i == items.length - 1)
					return null;
				ini += "." + items[++i];
			}
			String iniType = typeMap.get(ini);
			return getType(typeMap, oldVar.replace(ini, iniType));

		}
		return type;
	}

}
