package gjzq.search;

import java.util.Date;

public class Data {

	private String id;
	private String snippt;
	private String title;
	private String[] authors;
	private String type;
	private String source;
	private String stockname;
	private String stockcode;
	private String recommend;
	private Date uploadtime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSnippt() {
		return snippt;
	}
	public void setSnippt(String snippt) {
		this.snippt = snippt;
	}
	
	public String[] getAuthor() {
		return authors;
	}
	public void setAuthor(String[] author) {
		this.authors = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStockname() {
		return stockname;
	}
	public void setStockname(String stockname) {
		this.stockname = stockname;
	}
	public String getStockcode() {
		return stockcode;
	}
	public void setStockcode(String stockcode) {
		this.stockcode = stockcode;
	}
	public Date getUploadtime() {
		return uploadtime;
	}
	public void setUploadtime(Date uploadtime) {
		this.uploadtime = uploadtime;
	}
	public String getRecommend() {
		return recommend;
	}
	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}
	
	
}
