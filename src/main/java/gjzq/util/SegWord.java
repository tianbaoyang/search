package gjzq.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;

import edu.fudan.ml.types.Dictionary;
import edu.fudan.nlp.cn.tag.CWSTagger;

public class SegWord {

	public static Map<String, String> stopword;
	private static CWSTagger seg;
	
	public SegWord() {
		try {
			seg = new CWSTagger("./models/seg.m",
					new Dictionary("./models/dictionary.txt"));
			if(stopword == null)
				stopWords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("分词加载不成功....");
		}
	}
	public void stopWords() throws IOException {
		stopword = new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream("./models/stopword"), Util.CHARSET));
		String input;
		while((input = reader.readLine()) != null)
			if(input.charAt(0) != '#')
				stopword.put(input, "");
		reader.close();
	}
	public String[] segWordArray(String input) {
		return seg.tag2Array(input);
	}
	public List<String> segWordList(String input) {
		return seg.tag2List(input);
	}
	public void segWords(String dirName) throws IOException {
		Map<String, Integer> dictMap = new HashMap<>();
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		for(File file : files) {
			CsvReader reader = new CsvReader(file.getAbsolutePath());
			while(reader.readRecord()) {
				String[] record = reader.getValues();
				try {
					String[] segList = seg.tag2Array(record[7]);
					for(String term : segList) {
						if(!stopword.containsKey(term)) {
							int count = 0;
							if(dictMap.containsKey(term))
								count = dictMap.get(term);
							count += 1;
							dictMap.put(term, count);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("error: "+record[7]);
				}
			}
			reader.close();
			System.out.println(file.getName()+" have done");
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SegWord segWord = new SegWord();
		System.out.println(segWord.segWordList("互联网"));
	}

}
