package gjzq.search;

import gjzq.util.USolr;
import gjzq.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parse {

	private StringBuilder errorBuilder = new StringBuilder();
	private Data data;
	private Index index;
	
	public Parse() {
		index = new Index();
	}
	
	public Data parseHtml(String id, String source) {
		int idx;
		data = new Data();
		data.setId(id);
		Document doc = Jsoup.parse(source);
		try {
			String title = doc.getElementsByTag("h1").get(0).text().trim();
			data.setTitle(title);
			Elements elements = doc.getElementsByClass("btab").get(0).getElementsByTag("tr");
			Elements eles = elements.get(0).getElementsByTag("td");
			String stockname = eles.get(0).text();
			idx = stockname.indexOf(' ')+1;
			data.setStockname(stockname.substring(idx));
			String stockcode = eles.get(1).text();
			idx = stockcode.indexOf(' ')+1;
			if(idx != 0)
				data.setStockcode(stockcode.substring(idx));
			String uploadtime = eles.get(2).text();
			idx = uploadtime.indexOf('：')+1;
			data.setUploadtime(Util.getDate(uploadtime.substring(idx)));
			eles = elements.get(1).getElementsByTag("td");
			String type = eles.get(0).text();
			idx = type.indexOf(' ')+1;
			data.setType(type.substring(idx));
			String author = eles.get(2).text();
			idx = author.indexOf(' ')+1;
			data.setAuthor(author.substring(idx).split("，"));
			eles = elements.get(2).getElementsByTag("td");
			String from = eles.get(0).text();
			idx = from.indexOf(' ')+1;
			data.setSource(from.substring(idx));
			String recommend = eles.get(2).text();
			idx = recommend.indexOf(' ')+1;
			data.setRecommend(recommend.substring(idx));
			String snippt = doc.getElementsByClass("p_main").text().replaceAll("\\u00a0", "");
			data.setSnippt(snippt);
			return data;
		} catch (Exception e) {
			// TODO: handle exception
			errorBuilder.append(id+"\r\n");
			return null;
		}
	}
	public Map<String, String> getAllResult(String query) {
		USolr solr = new USolr("gjzq");
		solr.setRows(-1);
		solr.setQuery(query);
		solr.ExecuteQuery();
		SolrDocumentList results = solr.getQueryResult();
		Map<String, String> ids = new HashMap<>();
		for(SolrDocument result : results)
			ids.put(result.get("id").toString(),"");
		return ids;
	}
	public void parse(String dirName) {
		int numbers = 0;
		List<Data> dataList = new ArrayList<Data>();
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		for(File file : files) {
			long fileLen = file.length();
			byte[] fileContent = new byte[(int) fileLen];
			try {
				FileInputStream inputStream = new FileInputStream(file);
				inputStream.read(fileContent);
				inputStream.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
			Data data = parseHtml(file.getName(), new String(fileContent,
					Charset.forName(Util.CHARSET)));
			if(data == null)
				continue;
			dataList.add(data);
			if(dataList.size() % 3000 == 0) {
//				index.commitDocs(dataList);
				index.parseDocs(dataList, numbers);
				++numbers;				
				System.out.println("have commit： "+dataList.size());
				dataList.clear();
			}
//			System.out.println(file.getName()+" have done");
		}
//		index.commitDocs(dataList);
		index.parseDocs(dataList, numbers);
		System.out.println("have commit： "+dataList.size());
		Util.writeData("E:/gjzq/error2", errorBuilder.toString(), Util.CHARSET);
	}
	public void reParse(String dirName) {
		BufferedReader reader;
		Map<String, String> map = new HashMap<>();
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream("E:/gjzq/error1"), Util.CHARSET));
			String line;
			while((line = reader.readLine()) != null) {
				map.put(line, "");
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Data> dataList = new ArrayList<Data>();
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		for(File file : files) {
			if(!map.containsKey(file.getName()))
				continue;
			long fileLen = file.length();
			byte[] fileContent = new byte[(int) fileLen];
			try {
				FileInputStream inputStream = new FileInputStream(file);
				inputStream.read(fileContent);
				inputStream.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
			Data data = parseHtml(file.getName(), new String(fileContent,
					Charset.forName(Util.CHARSET)));
			if(data == null)
				continue;
			dataList.add(data);
		}
		index.commitDocs(dataList);
		System.out.println("have commit： "+dataList.size());
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String dirName = "E:/gjzq/data/慧博/公司调研/";
		Parse parse = new Parse();
		parse.parse(dirName);
//		parse.reParse(dirName);
//		String string = "    fudan".replaceAll("\\u00a0", "");
//		System.out.println(string);
//		System.out.println(Integer.toHexString(string.charAt(1)));
	}

}
