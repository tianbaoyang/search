package gjzq.search;

import gjzq.util.SegWord;
import gjzq.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csvreader.CsvReader;

public class Weight {

	public static String TOPIC;
	private static int DOCSIZE = 2000;
	private static int topN = 10;
	
	private SegWord segWord;
	private Map<String, Float> tfidfMap;
	private Map<String, Integer> idfMap;
	
	public Weight() {
		segWord = new SegWord();
		idfMap = new HashMap<>();
		tfidfMap = new HashMap<>();
	}
	/**
	 * 建立字典,从本地文本或文件夹中读取内容
	 * */
	public void buildDict(String dirName) throws IOException {
		Set<String> termSet = new HashSet<>();
		int size = 0;
		File dir = new File(dirName);
		File[] files;
		if(dir.isFile())
			files = new File[]{dir};
		else
			files = dir.listFiles();
		for(File file : files) {
			CsvReader reader = new CsvReader(file.getAbsolutePath(),
					',', Charset.forName("gbk"));
			while(reader.readRecord()) {
				String[] ins = reader.getValues();
				try {
					String[] segList = segWord.segWordArray(ins[3]);
					for(String term : segList) {
						if(term.length() > 1 && !SegWord.stopword.containsKey(term))
							termSet.add(term);
					}
					for(String term : termSet) {
						int count = 0;
						if(idfMap.containsKey(term))
							count = idfMap.get(term);
						else
							count = 0;
						count += 1;
						idfMap.put(term, count);
					}
					termSet.clear();
					size ++;
				}catch (Exception e) {
					// TODO: handle exception
					System.err.println(ins[3]+" error.....");
				}
			}
			reader.close();
			System.out.println(file.getName()+" have done");
		}
		DOCSIZE = size;
		serialize("E:/gjzq/关键字/idf.mm");
	}
	/**
	 * 从内存contents中建立字典
	 * */
	public void buildDict(List<String> contents) {
		Set<String> termSet = new HashSet<>();
		for(String content : contents) {
			String[] segList = segWord.segWordArray(content);
			for(String term : segList) {
				if(term.length() > 1 && !SegWord.stopword.containsKey(term))
					termSet.add(term);
			}
			for(String term : termSet) {
				int count = 0;
				if(idfMap.containsKey(term))
					count = idfMap.get(term);
				else
					count = 0;
				count += 1;
				idfMap.put(term, count);
			}
			termSet.clear();
		}
	}
	/**
	 * 计算tf*idf
	 * @param inPath 输入的文件
	 * @param outPath 输出的关键词位置
	 * */
	public void genKeyword(String inPath, String outPath) throws IOException {
		
		List<String> contents = new ArrayList<>();
		String line;
		CsvReader reader = new CsvReader(inPath, ',', Charset.forName("gbk"));
		while(reader.readRecord()) {
			line = reader.getValues()[3];
			if(line.length() == 0)
				continue;
			contents.add(line);
		}
		reader.close();
		StringBuilder builder = new StringBuilder();
		List<Map.Entry<String, Integer>> list = getKeyword(contents);
		for(Map.Entry<String, Integer> entry : list) {
			builder.append(entry.getKey()+"\t"+entry.getValue()+"\r\n");
		}
		Util.writeData(outPath, builder.toString(), Util.CHARSET);
	}
	public List<Map.Entry<String, Integer>> getKeyword(List<String> contents) {
		if(idfMap == null || idfMap.size() == 0)
			buildDict(contents);
		Map<String, Integer> tfMap = new HashMap<>();
		Map<String, Integer> topTerm = new HashMap<>();
		List<Map.Entry<String, Float>> topList;
		for(String content : contents) {
			String[] segs = segWord.segWordArray(content);
			int termSize = 0;
			for(String term : segs) {
				if(term.length() > 1 && !SegWord.stopword.containsKey(term)) {
					++termSize;
					int count = 0;
					if(tfMap.containsKey(term))
						count = tfMap.get(term);
					else
						count = 0;
					count += 1;
					tfMap.put(term, count);//计算tf
				}
			}
			topList = calculate(tfMap, termSize);
			for(Map.Entry<String, Float> entry : topList) {
				int count;
				if(topTerm.containsKey(entry.getKey()))
					count = topTerm.get(entry.getKey());
				else
					count = 0;
				count ++;
				topTerm.put(entry.getKey(), count);
			}
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<>(topTerm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				// TODO Auto-generated method stub
				return arg1.getValue()-arg0.getValue();
			}
		});
		
		return list;
	}
	/**
	 * 计算tf*idf值,取topN的tf*idf值对应的term
	 * */
	public List<Map.Entry<String, Float>> calculate(Map<String, Integer> tfMap, int termSize) {
		for(Map.Entry<String, Integer> entry : tfMap.entrySet()) {
			float tf = (float)entry.getValue()/termSize;
			int idfCount = 0;
			if (idfMap.containsKey(entry.getKey()))
				idfCount = idfMap.get(entry.getKey());
			else
				idfCount = 1;
			float idf = (float) Math.log(DOCSIZE/idfCount);
			tfidfMap.put(entry.getKey(), tf*idf);
		}
		List<Map.Entry<String, Float>> list = new ArrayList<>(tfidfMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

			@Override
			public int compare(Entry<String, Float> arg0,
					Entry<String, Float> arg1) {
				return Float.compare(arg1.getValue(), arg0.getValue());
			}
		});
		topN = topN > list.size() ? list.size() : topN;
		return list.subList(0, topN);
	}
	/**
	 * 序列化
	 * @param filePath "E:/gjzq/idf.mm"
	 * */
	public void serialize(String filePath) {
		Util.serialize(idfMap, filePath);
	}
	public void deserialize(String filePath) {
		idfMap = (Map<String, Integer>) Util.deserialize(filePath);
	}
	public Map<String, Integer> getIdf() throws IOException {
		Map<String, Integer> map = new HashMap<>();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream("E:/gjzq/data/idf"), Util.CHARSET));
		String line;
		while((line = reader.readLine()) != null) {
			String[] lines = line.split("\t");
			map.put(lines[0], Integer.parseInt(lines[1]));
		}
		reader.close();
		return map;
	}
	
	/**
	 * 计算所有相关是文档的tf*idf值
	 * */
	public void tfIdfAll(String inPath, String outPath) throws IOException {
//		idfMap = getIdf();
//		deserialize("E:/gjzq/关键字/idf.mm");
		Map<String, Integer> tfMap = new HashMap<>();
		int termSize = 0;
		String line;
		CsvReader reader = new CsvReader(inPath, ',', Charset.forName("gbk"));
		while(reader.readRecord()) {
			line = reader.getValues()[3];
			if(line.length() == 0)
				continue;
			String[] segs = segWord.segWordArray(line);
			for(String term : segs) {
				if(term.length() > 1 && !SegWord.stopword.containsKey(term)) {
					termSize ++;
					int count = 0;
					if(tfMap.containsKey(term))
						count = tfMap.get(term);
					else
						count = 0;
					count += 1;
					tfMap.put(term, count);
				}
			}
		}
		reader.close();
		List<Map.Entry<String, Float>> topList = calculate(tfMap, termSize);
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, Float> entry : topList)
			builder.append(entry.getKey()+"\t"+entry.getValue()+"\r\n");
		Util.writeData(outPath, builder.toString(), Util.CHARSET);
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		topN = 10;
		String base = "E:/gjzq/关键字/";
		String topic = "yidaiyilu";
		
		Weight weight = new Weight();
		weight.genKeyword(base+topic+".csv", base+topic+"_keyword");
//		dict.buildDict("E:/gjzq/data/慧博解析/行业分析/");
//		dict.output();
//		dict.print();
//		dict.tfIdf(base+topic+".csv", topic);
	}

}
