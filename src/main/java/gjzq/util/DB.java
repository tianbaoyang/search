package gjzq.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;



public class DB {

	public static int year = 2015;
	public static int lastDate = 1000;
	public static String url = "jdbc:mysql://192.168.38.76:3306/quant"
			+ "?useUnicode=true&characterEncoding=UTF-8";
	public static String user = "quant";
	public static String password = "quant";
	public static Connection connection;
	public static Statement statement;
	
	/**
	 * 初始化mysql连接
	 * */
	public static void init() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, user, password);
			connection.setAutoCommit(false);
			statement = connection.createStatement();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("连接不成功......");
			return;
		}
		System.out.println("连接成功......");
	}
	/**
	 * 关闭mysql连接
	 * */
	public static void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 慧博数据
	 * @param datas 写入mysql的数据
	 * */
	public static void loadHBData(PreparedStatement psmt, String[] datas) {
		try {
			psmt.setString(1, datas[0]);
			psmt.setString(2, datas[1]);
			psmt.setString(3, datas[2]);
			psmt.setString(4, datas[3]);
			psmt.setString(5, datas[4]);
			psmt.setTimestamp(6, new Timestamp(string2date(datas[5]).getTime()));
			psmt.setString(7, datas[6]);
			psmt.setString(8, datas[7]);
			psmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void loadGBData(PreparedStatement psmt, String[] datas, String stockcode) {
		try {
			psmt.setString(1, stockcode);
			psmt.setInt(2, Integer.parseInt(datas[0]));
			psmt.setInt(3, Integer.parseInt(datas[1]));
			psmt.setString(4, datas[2]);
			
			int ret = compareDate(datas[4]);
			if(ret > 0)
				year--;
			psmt.setDate(5, new java.sql.Date(getDate(datas[3]).getTime()));
			psmt.setString(6, datas[4]);
			psmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除表下的某个股票代码的所有数据
	 * @param table 表名称
	 * @param stockcode 股票代码
	 * */
	public static void deleteStock(String table, String stockcode) {
		String sql = "delete from table " + table+"where stockcode = \'"+stockcode+"\'";
		PreparedStatement psmt;
		try {
			psmt = connection.prepareStatement(sql);
			psmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("have deleted...");
	}
	/**
	 * 删除表中所有的数据
	 * @param table 表名
	 * */
	public static void deleteAll(String table) {
		String sql = "truncate table " + table;
		PreparedStatement psmt;
		try {
			psmt = connection.prepareStatement(sql);
			psmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("have deleted...");
	}
	/**
	 * 插入慧博的数据
	 * */
	public static void insertHB(String dirPath) throws IOException {
		PreparedStatement psmt = null;
		try {
			psmt = connection.prepareStatement("insert into hibor_fenxi(id,"
					+ "stockcode,stockname,type,author,time,title,content) values(?,?,?,?,?,?,?,?)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("can't connect....");
		}
		int count = 0;
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for(File file : files) {
			CsvReader reader = new CsvReader(new FileInputStream(file), Charset.forName("gbk"));
			while(reader.readRecord()) {
				String[] datas = reader.getValues();
				loadHBData(psmt, datas);
				if(++count % 100 == 0) {
					try {
						connection.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("have commit nums: "+count);
				}
			}
			try {
				connection.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(file.getName()+" have done");
		}
	}
	
	public static Date getDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		try {
			return sdf.parse(year+"-"+date); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static int compareDate(String date) {
		Pattern pattern = Pattern.compile("(\\d{2})-(\\d{2})");
		Matcher matcher = pattern.matcher(date);
		int num = 0;
		if(matcher.find())
			num = Integer.parseInt(matcher.group(1))*30 + Integer.parseInt(matcher.group(2));
		int compare = Integer.compare(num-180, lastDate);
		lastDate = num;
		return compare;
	}
	public static Date string2date(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		try {
			return sdf.parse(date); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void insertGB(String dirPath) throws IOException {
		PreparedStatement psmt = null;
		try {
			psmt = connection.prepareStatement("insert into em_guba(stockcode,"
					+ "readCount,commentCount,title,commentDate,updateDate) values(?,?,?,?,?,?)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("can't connect....");
		}
		int count = 0;
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.isDirectory())//如果是目录则跳过
				continue;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "utf-8"));
			String line;
			for(int i = 0; i < 5; i ++)
				reader.readLine();
			while((line = reader.readLine()) != null) {
				String[] lines = line.split("\t");
				if(lines.length < 5)
					continue;
				loadGBData(psmt, lines, file.getName());
				if(++count % 1000 == 0) {
					try {
						connection.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("have commit nums: "+count);
				}
			}
			try {
				connection.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(file.getName()+" have done");
			reader.close();
		}
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		init();
		deleteAll("em_guba");
//		insertHB("E:/gjzq/data/慧博解析/行业分析/");
		insertGB("E:/gjzq/data/股吧/");
	}

}
