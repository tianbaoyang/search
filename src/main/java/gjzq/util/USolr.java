package gjzq.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;








import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.client.solrj.response.TermsResponse;


public class USolr {
	private HttpSolrServer server = null;
	private String url = "http://localhost:8080/solr/";//10.227.3.46
	private SolrQuery solrquery = null;
	private SolrDocumentList queryresult = null;
	private Map<String, Map<String, List<String>>> highlightresult = null;
	private QueryResponse queryresponse = null;
	
	ModifiableSolrParams params = null;
	private boolean isClosed = false;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public USolr() {

		params = new ModifiableSolrParams();
		solrquery = new SolrQuery();
		try {
			if (server == null) {
				System.out.println("solr instance have create!");
				server = new HttpSolrServer(url);
				server.setSoTimeout(500000); // socket read timeout
				server.setConnectionTimeout(20000);//设置链接超时时间
				server.setDefaultMaxConnectionsPerHost(100);
				server.setMaxTotalConnections(100);
				server.setFollowRedirects(false); // defaults to false
				// allowCompression defaults to false.
				// Server side must support gzip or deflate for this to have any
				// effect.
				server.setAllowCompression(true);
				server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		selectIndex("nielsen_new");
	}

	public USolr(String core) {
		solrquery = new SolrQuery();
		try {
			if (server == null) {
				server = new HttpSolrServer(url);
				server.setSoTimeout(50000); // socket read timeout
				server.setConnectionTimeout(1000);
				server.setDefaultMaxConnectionsPerHost(100);
				server.setMaxTotalConnections(100);
				server.setFollowRedirects(false); // defaults to false
				// allowCompression defaults to false.
				// Server side must support gzip or deflate for this to have any
				// effect.
				server.setAllowCompression(true);
				server.setMaxRetries(1); // defaults to 0. > 1 not recommended.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		selectIndex(core);
	}

	public void setIndexWithUrl(String full_url) {
		server.setBaseURL(full_url);
	}

	// 删除对应索引库下的索�?
	public void DeleteIndex() throws Exception {
		server.deleteByQuery("*:*");
		server.commit();
		System.out.println("Delete done");
	}

	// 以下�?��列函数为 为Solr的server构�?查询参数 的接�?
	// 选择索引库，index为solr的core名称，例如poi,street�?
	public void selectIndex(String index) {
		server.setBaseURL(url + index);
	}

	public void clear() {
		queryresponse=null;
		solrquery.clear();
	}

	public boolean set(String field,String value){
		if (solrquery == null)
			return false;
		else {
			try {
				solrquery.set(field, value);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean set(String field,int value){
		if (solrquery == null)
			return false;
		else {
			try {
				solrquery.set(field, value);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean set(String field,boolean value){
		if (solrquery == null)
			return false;
		else {
			try {
				solrquery.set(field, value);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public String get(String field){
		if (solrquery == null || field==null)
			return null;
		else {
			try {
				return solrquery.get(field);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	public boolean getBool(String field){
		if (solrquery == null || field==null)
			return false;
		else {
			try {
				return solrquery.getBool(field);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	/**
	 * if everything goes on smoothly, return a normal value in solrquery
	 * otherwise it will return -1 as an error flag
	 * @param field
	 * @return
	 */
	public int getInt(String field){
		if (solrquery == null || field==null)
			return -1;
		else {
			try {
				return solrquery.getInt(field);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
	}
	
	public boolean setQuery(String query) {
		if (solrquery == null || query == null)
			return false;
		else {
			try {
				solrquery.setQuery(query);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public String getQuery(){
		if(solrquery==null)
			return null;
		return solrquery.getQuery();
	}
	
	
	//test for terms!!!!===========================================================================
	public boolean setTestQuery(String query) {
		SolrQuery params = solrquery;  
        params.set("q", query);  
        params.set("qt", "/terms");  
          
        //parameters settings for terms requestHandler   
        // 参�?（refer to）http://wiki.apache.org/solr/TermsComponent  
        params.set("terms", "true");  
        params.set("terms.fl","id");  
          
        //params.set("terms.lower", ""); //term lower bounder�?��的字�? 
      //  params.set("terms.lower.incl", "true");  
       // params.set("terms.mincount", "1");  
       // params.set("terms.maxcount", "100");  
          
        //http://localhost:8983/solr/terms?terms.fl=text&terms.prefix=�?// using for auto-completing  
        params.set("terms.prefix", "");   
        //params.set("terms.regex", "");  
        //params.set("terms.regex.flag", "case_insensitive");  
          
        params.set("terms.limit", "50");  
   //     params.set("terms.upper", ""); //结束的字�? 
    //    params.set("terms.upper.incl", "false");  
          
        params.set("terms.raw", "true");  
        params.set("terms.sort", "index");
        return true;
	}
	//test for terms!!!!===========================================================================
	
	
	public String getFields(){
		if(solrquery==null)
			return null;
		return solrquery.getFields();
	}
	public boolean setFields(String... fields) {
		if (solrquery == null || fields == null)
			return false;
		else {
			try {
				solrquery.setFields(fields);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public String[] getFilter(){
		if(solrquery==null)
			return null;
		return solrquery.getFilterQueries();
	}
	public boolean addFilter(String... filter) {
		if (solrquery == null || filter == null)
			return false;
		else {
			try {
				solrquery.addFilterQuery(filter);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean removeFilter(String fq) {
		return solrquery.removeFilterQuery(fq);
	}

	// order为true则升序，order为false则降?
	public boolean addSortField(String sortfield, boolean order) {
		if (solrquery == null || sortfield == null)
			return false;
		else {
			try {
				if (order)
					solrquery.addSortField(sortfield, SolrQuery.ORDER.asc);
				else
					solrquery.addSortField(sortfield, SolrQuery.ORDER.desc);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public String[] getSortFields(){
		if(solrquery==null)
			return null;
		return solrquery.getSortFields();
	}
	public String getSortField(){
		if(solrquery==null)
			return null;
		return solrquery.getSortField();
	}
	public boolean removeSortField(String sortfield, boolean order) {
		if (solrquery == null || sortfield == null)
			return false;
		else {
			try {
				if (order)
					solrquery.removeSortField(sortfield, SolrQuery.ORDER.asc);
				else
					solrquery.removeSortField(sortfield, SolrQuery.ORDER.desc);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean getHighlight(){
		if(solrquery==null)
			return false;
		return solrquery.getHighlight();
	}
	public boolean setHighlight(boolean attribute) {
		if (solrquery == null)
			return false;
		else {
			solrquery.setHighlight(attribute);
			return true;
		}
	}

	public String[] getHighlightField(){
		if(solrquery==null)
			return null;
		return solrquery.getHighlightFields();
	}
	public boolean addHighlightField(String highlight) {
		if (solrquery == null || highlight == null)
			return false;
		else {
			try {
				solrquery.addHighlightField(highlight);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean removeHighlightField(String highlight) {
		if(solrquery==null)
			return false;
		return solrquery.removeHighlightField(highlight);
	}

	public String getHighlightSimplePre(){
		if(solrquery==null)
			return null;
		return solrquery.getHighlightSimplePre();
	}
	public boolean setHighlightSimplePre(String str) {
		if (solrquery == null || str == null)
			return false;
		else {
			try {
				solrquery.setHighlightSimplePre(str);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public String getHighlightSimplePost() {
		if(solrquery==null)
			return null;
		return solrquery.getHighlightSimplePost(); 
	}	
	public boolean setHighlightSimplePost(String str) {
		if (solrquery == null || str == null)
			return false;
		else {
			try {
				solrquery.setHighlightSimplePost(str);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean setHighlightSinppets(int size) {
		if(solrquery == null || size < 0)
			return false;
		else {
			solrquery.setHighlightSnippets(size);
			return true;
		}
	}
	public boolean setHighlightFragsize(int size) {
		if(solrquery == null || size < 0)
			return false;
		else {
			solrquery.setHighlightFragsize(size);
			return true;
		}
	}
	public int getStart(){
		if(solrquery==null)
			return -1;
		return solrquery.getStart();
	}

	public boolean setStart(int start) {
		if (solrquery == null || start < 0)
			return false;
		else {
			try {
				solrquery.setStart(start);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public int getRows(){
		if(solrquery==null)
			return -1;
		return solrquery.getRows();
	}

	public boolean setRows(int rows) {
		if (solrquery == null || rows < 0)
			return false;
		else {
			try {
				solrquery.setRows(rows);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean getDismax(){
		if(solrquery==null)
			return false;
		String tmp = solrquery.get("defType");
		if(tmp.equals("edismax"))
			return true;
		return false;
	}	
	public boolean setDismax(boolean flag) {
		if (solrquery == null || flag == false)
			return false;
		else {
			try {
				solrquery.set("defType", "edismax");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	// if qf contains not only one field, just part them with space,
	// eg:"var_poi_chinese var_address_chinese"
	public String getDismaxField(){
		if(solrquery==null)
			return null;
		return solrquery.get("qf");
	}
	public boolean setDismaxField(String qf) {
		if (solrquery == null || qf == null)
			return false;
		else {
			try {
				solrquery.set("qf", qf);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean removeDismaxField(String qf) {
		if (solrquery == null || qf == null)
			return false;
		else {
			try {
				solrquery.remove("qf", qf);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public ArrayList<String> getSpellCheckResponse(String spellcheck){
		ArrayList<String> array = new ArrayList<String>();
		selectIndex("poi");
		try{
			params.set("qt", "/spell");
		    params.set("q", spellcheck);
		    params.set("spellcheck", "on");
		    //params.set("spellcheck.build", "true");
	
		    QueryResponse response = server.query(params);
		    SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
		    if (!spellCheckResponse.isCorrectlySpelled()) {
		        for (Suggestion suggestion : response.getSpellCheckResponse().getSuggestions()) {
		           array.addAll(suggestion.getAlternatives());
//		           System.out.println("original token: " + suggestion.getToken() + " - alternatives: " + suggestion.getAlternatives());
		        }
		    }
	    }catch (Exception e) {
	        e.printStackTrace();
	    }
	    return array;
	}

	public boolean setDebugQuery(boolean flag) {
		if (solrquery == null)
			return false;
		else {
			try {
				if (flag)
					solrquery.set("debugQuery", "on");
				else
					solrquery.set("debugQuery", "off");
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public void ExecuteQuery() {
		try {
			queryresponse = server.query(solrquery);
//			System.out.println("查询耗时（ms）：" + queryresponse.getQTime());
			}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	public float getQueryTime(){
		return (float)queryresponse.getQTime()/1000;
	}
	// close
	public boolean isClosed()
	{
		return isClosed;
	}
	public void resetClosed()
	{
		isClosed = false;
	}
	
	public void close() {
		isClosed = true;
	}
	public void shutdown(){
		server.shutdown();
	}
	/*
	 * two interfaces left. 1. return a normal result for query 2. return the
	 * highlight result.
	 */
	public SolrDocumentList getQueryResult() {
		queryresult = queryresponse.getResults();
		return this.queryresult;
	}

	public Map<String, Map<String, List<String>>> getHightlightResult() {
		highlightresult = queryresponse.getHighlighting();
		return this.highlightresult;
	}


//	public void print() {
//		List<SolrDocument> docs2 = getQueryResult();// 得到结果�?
//		int ik = 0;
//		for (SolrDocument doc : docs2) {// 遍历结果�?
//			
//			for (Iterator iter = doc.iterator(); iter.hasNext();) {
//				Map.Entry<String, Object> entry = (Entry<String, Object>) iter.next();
//				System.out.print("Key :" + entry.getKey() + "  ");
//				System.out.println("Value :" + entry.getValue());
//				Object ob = new Object();
//			}
//						
//			ik++;
//			System.out.println("----------------------------------");
//		}
//		System.out.println(ik);
//	}
	public void printTerms(){
		if(queryresponse != null ){  
            System.out.println("查询耗时（ms）：" + queryresponse.getQTime());  
            //System.out.println(response.toString());  
              
            TermsResponse termsResponse = queryresponse.getTermsResponse();  
            if(termsResponse != null) {  
                Map<String, List<TermsResponse.Term> > termsMap = termsResponse.getTermMap();  
                  
                for(Map.Entry<String, List<TermsResponse.Term> > termsEntry : termsMap.entrySet()) {  
                    System.out.println("Field Name: " + termsEntry.getKey());  
                    List<TermsResponse.Term> termList = termsEntry.getValue();  
                    System.out.println("Term    :  Frequency");  
                    for(TermsResponse.Term term : termList) {  
                        System.out.println(term.getTerm() + "   :   " + term.getFrequency());  
                    }  
                    System.out.println();  
                }
            }
		}
	}
	public void test() throws SolrServerException {
	     SolrQuery query = new SolrQuery();  
	     query.setQuery("content:������ѧ");  
	     query.setHighlight(true); // ���������������query.setParam("hl", "true");  
	     query.addHighlightField("name");// �����ֶ�  
	     query.setHighlightSimplePre("<font color='red'>");//��ǣ������ؼ���ǰ׺  
	     query.setHighlightSimplePost("</font>");//��׺  
		 QueryResponse rsp = server.query(query);
		 System.out.println();
	     SolrDocumentList docs = rsp.getResults();
	     int t = rsp.getQTime();
	     long num = docs.getNumFound();
	     System.out.println("time:"+t+"total:"+num);
	}
	public static void term_main(String[] args)throws Exception{
		USolr solr = new USolr();
		solr.selectIndex("");
		solr.setTestQuery("a ");
		solr.ExecuteQuery();
		solr.printTerms();
	}

	public static void main(String[] args) throws Exception {
		USolr solr = new USolr();
		
		solr.selectIndex("nutch");
		solr.setFields("*", "score");
		solr.setStart(0);
		solr.setRows(3);
		//solr.setDismax(true);
		solr.test();
		
		//solr.addFilter("area1:上海�?);
		//solr.solrquery.set("sfield", "position");
		//solr.solrquery.set("pt", "31.29853,121.50143");
		//solr.solrquery.set("d", "5");
		//solr.addSortField("geodist()",true);
		//solr.setDismaxField("var_poi_chinese var_poi_alias var_address_chinese text BigTag");
		//solr.setQuery(qs);
		//System.out.println(solr.url+"shops/select/?"+solr.solrquery.toString());
//		ArrayList<String> suggestion = solr.getSpellCheckResponse("");
//		System.out.println(suggestion);
//		suggestion = solr.getSpellCheckResponse("adidae");
//		System.out.println(suggestion);
		// solr.setDebugQuery(true);
		// solr.addSortField("score", false);
		try {
			//solr.ExecuteQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		solr.print();
		solr.clear();
		solr.close();
	}

}
