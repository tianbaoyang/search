package gjzq.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import gjzq.util.USolr;
import gjzq.util.Util;


public class Search {

	private String execQuery;
	private Map<String, Integer> recommends;
	private List<String[]> results;
	private SolrDocumentList docs;
	private USolr solr;
	
	public Search() {
		solr = new USolr("gjzq");
		results = new ArrayList<>();
		recommends = new HashMap<>();
	}
	/**
	 * 检索完毕，则关闭solr，释放资源
	 * */
	public void shutdown() {
		solr.shutdown();
	}
	/**
	 * 使用solr查找主题相关的词
	 * */
	public void search(String filePath) {
		execQuery = getExecQuery();
		solr.setRows(2000);
		solr.setFields("score","*");
		solr.addSortField("score", false);
		solr.setQuery(execQuery);
		solr.ExecuteQuery();
		docs =solr.getQueryResult();
		
		long numFound = docs.getNumFound();
		System.out.println("size: "+docs.size() + "\tnumfound: "+numFound);
		if(numFound != 0) {
			for (SolrDocument doc : docs) {
				String[] res = {doc.get("stockname").toString(),doc.get("uploadtime").toString(),
						doc.get("title").toString(), doc.get("snippt").toString()};
				results.add(res);
				if(doc.get("type").toString().equals("公司调研")) {
					String recommend = doc.get("stockname").toString();
					int count = 0;
					if(recommends.containsKey(recommend))
						count = recommends.get(recommend);
					else
						count = 0;
					count += 1;
					recommends.put(recommend, count);
				}
			}
		}
		outputTopic(filePath);
	}
	/**
	 * 二级查询
	 * 查询数目为200篇最相关的结果
	 * */
	public List<String> reSearch(String execQuery, String outPath) {
		List<String> results = new ArrayList<>();
		solr.setRows(300);
		solr.setFields("score","*");
		solr.addSortField("score", false);
		solr.setQuery(execQuery);
		solr.ExecuteQuery();
		docs =solr.getQueryResult();
		
		long numFound = docs.getNumFound();
		System.out.println("size: "+docs.size() + "\tnumfound: "+numFound);
		if(numFound != 0) {
			for (SolrDocument doc : docs) {
				String snippt = doc.get("snippt").toString();
				if(snippt.length() > 0)
					results.add(snippt);
			}
		}
		return results;
	}
	/**
	 * 输出与主题相关的内容
	 * */
	public void outputTopic(String filePath) {
		for(String result[] : results)
			Util.writeCsvData(filePath, result, "gbk", true);
	}
	/**
	 * 输出推荐个股列表，按推荐次数倒排
	 * */
	public void outputRecommend(String filePath) {
		StringBuilder builder = new StringBuilder();
		List<Map.Entry<String, Integer>> list = new ArrayList<>(recommends.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				// TODO Auto-generated method stub
				return arg1.getValue()-arg0.getValue();
			}
		});
		for(Map.Entry<String, Integer> entry : list)
			builder.append(entry.getKey()+"\t"+entry.getValue()+"\r\n");
		Util.writeData(filePath, builder.toString(), Util.CHARSET);
	}
	public String getExecQuery() {
		return "\"一带一路\"~10000";// \"亚投行\"~10000 \"海上丝路\"~10000 \"高铁出海\"~10000 \"装备出海\"~10000";
//		return "\"工业4.0\"~10000 \"智能制造\"~10000";// \"机器人\"~1000 \"中国制造\"";
//		return "能源 互联网 光伏";
	}

	public void subKeyword(String filePath) {
		List<String> contents;
		String outPath = "E:/gjzq/关键字/keyword_tmp";
		List<String> keywords = Util.getKeywords(filePath, 20);
		for(String keyword : keywords) {
			keyword = keyword.split("\t")[0];
			contents = reSearch(keyword, outPath);
			Weight weight = new Weight();
			List<Map.Entry<String, Integer>> list = weight.getKeyword(contents);//计算二级关键字
			StringBuilder builder = new StringBuilder();
			builder.append(keyword);
			for(Map.Entry<String, Integer> entry : list)
				builder.append(" "+entry.getKey());
			builder.append("\r\n");
			Util.writeData(filePath+"_sub", builder.toString(), Util.CHARSET, true);
			System.out.println(keyword+" have done....");
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String base = "E:gjzq/关键字/";
		String topic = "nengyuan";
		Search search = new Search();
//		search.search(base+topic+".csv");
//		search.outputRecommend(base+topic+"_recommend");
		search.subKeyword(base+topic+"_keyword");
	}

}
