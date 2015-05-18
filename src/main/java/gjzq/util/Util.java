package gjzq.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvWriter;

public class Util {

	public final static String CHARSET = "utf-8";
	public static BufferedWriter writer = null;
	public static CsvWriter csvWriter = null;
	
	public static List<String> getKeywords(String filePath, int size) {
		try {
			List<String> keywords = new ArrayList<>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), Util.CHARSET));
			String line;
			int count = 0;
			while((line = reader.readLine()) != null) {
				if(count++ == size)
					break;
				keywords.add(line);
			}
			reader.close();
			return keywords;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("不能获取A股列表....");
		}
		return null;
	}
	public static Map<String, String> getAstock() {
		try {
			Map<String, String> map = new HashMap<>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("./models/astock.txt"), Util.CHARSET));
			String line;
			while((line = reader.readLine()) != null)
				map.put(line,"");
			reader.close();
			return map;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("不能获取A股列表....");
		}
		return null;
	}
	public static void writeCsvData(String filePath, String[] data, String charset, boolean append) {
		try {
			if(csvWriter == null)
				csvWriter = new CsvWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), charset), ',');
			csvWriter.writeRecord(data);
			csvWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void closeCsvWriter() {
		csvWriter.close();
		csvWriter = null;
	}
	public static void writeData(String filePath, String data, String charset) {
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), charset));
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeData(String filePath, String data, String charset, boolean apend) {
		try {
			if(writer == null)
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, apend), charset));
			writer.write(data);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void closeWriter() {
		try {
			writer.close();
			writer = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void serialize(Object object, String filePath) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(filePath));
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("序列化失败....");
		}
	}
	public static Object deserialize(String filePath) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(filePath));
			Object obj = ois.readObject();
			ois.close();
			System.out.println("反序列化成功........");
			return obj;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("反序列化失败......");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String array2string(String[] arr) {
		if(arr.length == 0)
			return "";
		else if(arr.length == 1)
			return arr[0];
		else {
			String ret = "";
			for(int i = 0; i < arr.length-1; i ++)
				ret += arr[i]+",";
			ret += arr[arr.length-1];
			return ret;
		}
	}
	public static Date getDate(String text) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(text);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
 	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getDate("2015-05-04 17:07:42").getTime());
		System.out.println(new Date(1430730462000l));
	}

}
