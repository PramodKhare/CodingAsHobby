package airdel.a3.util;

import java.io.BufferedReader;
import java.io.FileReader;
/**
 * Tests the performance of airdel.a3.Parser class
 * @author nikit
 *
 */
public class TestParserSpeed {
	
	public static void main(String args[]) throws Exception {
		Parser parser = new Parser(',');
		if(args.length == 0) 
			throw new Error("usage> TestParserSpeed <InputCSV>");
		
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line = "";
		Timer t = new Timer().start();
		while((line = br.readLine()) != null) {
			parser.parse(line);
			if(!parser.isValid()) continue;
			for(String[] keys: parser.KEYS) {
				parser.getKeyValuePairs(keys);
			}
		}
		
		br.close();
		System.out.println(t.stop());
	}
}