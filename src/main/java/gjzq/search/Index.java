package gjzq.search;

import gjzq.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class Index {

	private HttpSolrServer server = null;
	private static String serverUrl = "http://localhost:8080/solr/";
	
	public Index() {
		try {
			if(server == null) {
				server = new HttpSolrServer(serverUrl);
				server.setSoTimeout(100000);
				server.setConnectionTimeout(100000);
				server.setDefaultMaxConnectionsPerHost(100);
				server.setMaxTotalConnections(100);
				server.setFollowRedirects(false);
				server.setAllowCompression(true);
				server.setMaxRetries(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("error");
		}
		selectIndex("gjzq");
	}
	/**
	 * 选择solr的表索引
	 * @param table 表名
	 * */
	public void selectIndex(String table) {
		server.setBaseURL(serverUrl + table);
	}

	public void deleteDocs() {

		try {
			server.deleteByQuery("*:*");
			server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void commitDoc() {
		Collection<SolrInputDocument> docs = new ArrayList<>();
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("uploadtime", new java.util.Date());
		doc.addField("id", "2");
		docs.add(doc);
		
		try {
			server.add(docs);
			server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void optimize() {

		try {
			server.optimize();
			server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void commitDocs(List<Data> dataList) {
		
		Collection<SolrInputDocument> docs = new ArrayList<>();
		for(Data data : dataList) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", data.getId());
			doc.addField("snippt", data.getSnippt());
			doc.addField("title", data.getTitle());
			doc.addField("author", data.getAuthor());
			doc.addField("stockname", data.getStockname());
			doc.addField("stockcode", data.getStockcode());
			doc.addField("source", data.getSource());
			doc.addField("type", data.getType());
			doc.addField("recommend", data.getRecommend());
			doc.addField("uploadtime", data.getUploadtime());
			docs.add(doc);
		}
		try {
			server.add(docs);
			server.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void parseDocs(List<Data> datas, int fileName) {
		for(Data data : datas) {
			String[] out = {data.getId(), data.getStockcode()==null?"":data.getStockcode(),
					data.getStockname(),data.getType(), array2string(data.getAuthor()),
					data.getUploadtime().toString(),data.getTitle(),
					data.getSnippt()==null?"":data.getSnippt()};
			Util.writeCsvData("E:/gjzq/data/慧博解析/公司调研1/"+fileName+".csv", out, "gbk", true);
		}
		Util.closeCsvWriter();
	}
	
	public static String array2string(String[] text) {
		String ret = "";
		for(int i = 0; i < text.length; i ++)
			ret += "," + text[i];
		return ret.substring(1);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Index index = new Index();
//		index.commitDoc();
		index.deleteDocs();
	}

}
