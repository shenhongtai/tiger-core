package cn.imtiger.util.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * 数据处理工具类
 * @author ShenHongtai
 * @date 2019-7-13
 */
public class StringUtil {
	private static int ZIP_BLOCK_SIZE = 2048;
	protected static String[] T_IMAGE_TYPES = { "[B", "Blob", "BLOB", "Image", "java.io.InputStream", "OracleBlob",
			"oracle.sql.BLOB", "oracle.sql.CLOB" };
    
	/**
	 * 统计字符串中子字符串出现次数
	 * @param str
	 * @param substr
	 * @return
	 */
	public static int containsCount(String str, String substr) {
		int count = 0;
		if (ValidateUtil.isNotBlank(str) && ValidateUtil.isNotBlank(substr)) {
	        while (str.indexOf(substr) != -1) {
	        	count++;
	        	str = str.substring(str.indexOf(substr) + substr.length());
	        }
		}	    
		return count;
	}

	/**
	 * 读取配置文件
	 * @param filePath
	 * @param key
	 * @return
	 */
	public static String getProperty(String filePath, String key) {
		Properties properties = new Properties();
		BufferedReader bufferedReader = null;
		// 使用InPutStream流读取properties文件
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			properties.load(bufferedReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 获取key对应的value值
		return properties.getProperty(key);
	}
	
	/**
	 * String转Blob
	 * @param str
	 * @param conn
	 * @return
	 */
	public static Blob stringToBlob(String str, Connection conn) {
		Blob blob = null;
		try {
			byte[] buffer = str.getBytes("ASCII");
			blob = conn.createBlob();
			blob.setBytes(1, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blob;
	}

	/**
	 * 字符串单引号替换成双引号
	 * @param str
	 * @return
	 */
	public static String filtSingleQuotes(String str) {
		String result = null;
		result = str.replaceAll("'", "\"");
		return result;
	}

	/**
	 * 数组是否存在字符串
	 * @param str
	 * @param strArr
	 * @return
	 */
	public static boolean stringInArray(String str, String[] strArr) {
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i].equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 结果集转JSON
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String rsToJsonString(ResultSet rs) throws SQLException {
		return listToJsonString(rsToList(rs));
	}

	/**
	 * 结果集转JSONArray
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static JSONArray rsToJson(ResultSet rs) throws SQLException {
		return listToJson(rsToList(rs));
	}

	/**
	 * List转JSONArray
	 * @param list
	 * @return
	 */
	public static JSONArray listToJson(List<Object> list) {
		return JSONArray.parseArray(JSON.toJSONString(list));
	}

	/**
	 * List转JSONString
	 * @param list
	 * @return
	 */
	public static String listToJsonString(List<Object> list) {
		return JSON.toJSONString(list);
	}

	/**
	 * 结果集转List
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List<Object> rsToList(ResultSet rs) throws SQLException {
		List<Object> list = new ArrayList<Object>();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		while (rs.next()) {
			Map<String, Object> rowData = new HashMap<>(columnCount);
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	/**
	 * MD5加密
	 * @param data
	 * @return
	 */
	public static String encodeMD5(String data) {
		return DigestUtils.md5Hex(data);
	}
	
	/**
	 * 是否Image类型列
	 * @param strClsName
	 * @return
	 */
	public static boolean isImageColumn(String strClsName) {
		int i = 0;
		for (int size = T_IMAGE_TYPES.length; i < size; i++) {
			if (T_IMAGE_TYPES[i].equals(strClsName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 清除字符串空格
	 * @param data
	 * @return
	 */
	public static String clearSpace(String data) {
		String re = "";
		String[] words = data.split(" ");

		for (int i = 0; i < words.length; i++) {
			re = re + words[i];
		}
		return re;
	}

	/**
	 * 清除字符串指定字符
	 * @param data
	 * @param chars
	 * @return
	 */
	public static String clearChar(String data, String chars) {
		String re = "";
		String[] words = data.split(chars);

		for (int i = 0; i < words.length; i++) {
			re = re + words[i];
		}
		return re;
	}

	/**
	 * 将字符串数组用标识连接为字符串
	 * @param stringArray
	 * @param symbol
	 * @return
	 */
	public static String convertStringArrayToStringBySymbol(String[] stringArray, char symbol) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringArray.length; i++) {
			sb.append(stringArray[i]);
			sb.append(symbol);
		}
		return sb.toString();
	}

	/**
	 * 将字符串用标识分隔为字符串数组
	 * @param string
	 * @param symbol
	 * @return
	 */
	public static String[] convertStringToStringArrayBySymbol(String string, String symbol) {
		Vector<Object> stringVector = convertStringToStringVectorBySymbol(string, symbol);
		String[] stringArray = new String[stringVector.size()];
		for (int i = 0; i < stringVector.size(); i++) {
			stringArray[i] = ((String) stringVector.elementAt(i));
		}
		return stringArray;
	}

	/**
	 * 将字符串用标识分隔为Vector数组
	 * @param string
	 * @param symbol
	 * @return
	 */
	public static Vector<Object> convertStringToStringVectorBySymbol(String string, String symbol) {
		StringTokenizer st = new StringTokenizer(string, symbol, true);
		Vector<Object> stringVector = new Vector<>();
		while (st.hasMoreElements()) {
			stringVector.addElement(st.nextElement());
		}
		return stringVector;
	}

	/**
	 * 将Vector数组用标识连接为字符串
	 * @param stringVector
	 * @param symbol
	 * @return
	 */
	public static String convertStringVectorToStringBySymbol(Vector<Object> stringVector, String symbol) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringVector.size(); i++) {
			sb.append(stringVector.elementAt(i));
			sb.append(symbol);
		}
		return sb.toString();
	}

	/**
	 * 金额格式化
	 * @param value
	 * @return
	 */
	public static String defaultFormat(double value) {
		double min = 0.01;
		if (value < min) {
			return "&nbsp;";
		}
		NumberFormat nf = new DecimalFormat("###,###.00");
		return nf.format(value);
	}

	/**
	 * 指定位置填充字符串
	 * @param psStr
	 * @param psC
	 * @param psLen
	 * @return
	 */
	public static String fillString(String psStr, char psC, int psLen) {
		if (psStr.length() > psLen) {
			return psStr.substring(0, psLen);
		}
		char[] vcTemp = new char[psLen];
		for (int i = 0; i < psLen; i++) {
			vcTemp[i] = psC;
		}
		String vsTemp = new String(vcTemp);
		String vsResult = psStr.concat(vsTemp);
		return vsResult.substring(0, psLen);
	}

	/**
	 * GBK转码为Unicode
	 * @param original
	 * @return
	 */
	public static String convertGBKToUnicode(String original) {
		if (original != null) {
			try {
				return new String(original.getBytes("GBK"), "ISO8859_1");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Unicode转码为GBK
	 * @param original
	 * @return
	 */
	public static String convertUnicodeToGBK(String original) {
		if (original != null) {
			try {
				return new String(original.getBytes("ISO8859_1"), "GBK");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 数据解压缩
	 * @param pBytesInput
	 * @return
	 */
	public static byte[] unZipBytes(byte[] pBytesInput) {
		ByteArrayInputStream pBytesIn = new ByteArrayInputStream(pBytesInput);
		ByteArrayOutputStream pBytesOut = new ByteArrayOutputStream();

		GZIPInputStream pZip = null;
		try {
			pZip = new GZIPInputStream(pBytesIn);
			byte[] pRead = new byte[ZIP_BLOCK_SIZE];
			while (true) {
				int iRead = pZip.read(pRead);
				if (iRead <= 0) {
					break;
				}
				pBytesOut.write(pRead, 0, iRead);
			}

			return pBytesOut.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pZip.close();
				pBytesIn.close();
			} catch (Exception localException3) {
			}
		}
		return null;
	}

	/**
	 * 数据压缩
	 * @param pBytesInput
	 * @return
	 */
	public static byte[] zipBytes(byte[] pBytesInput) {
		ByteArrayOutputStream pBytesOut = new ByteArrayOutputStream();
		GZIPOutputStream pZip = null;

		int iTotalSize = pBytesInput.length;
		int iTimes = iTotalSize / ZIP_BLOCK_SIZE;
		int iLeft = iTotalSize % ZIP_BLOCK_SIZE;
		try {
			pZip = new GZIPOutputStream(pBytesOut);

			for (int i = 0; i < iTimes; i++) {
				pZip.write(pBytesInput, i * ZIP_BLOCK_SIZE, ZIP_BLOCK_SIZE);
			}

			if (iLeft > 0) {
				pZip.write(pBytesInput, iTimes * ZIP_BLOCK_SIZE, iLeft);
			}

			pZip.finish();

			return pBytesOut.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pZip.close();
				pBytesOut.close();
			} catch (Exception localException3) {
			}
		}
		return null;
	}

	/**
	 * Base64转为Bytes
	 * @param pText
	 * @return
	 */
	public static byte[] t2B(String pText) {
		return Base64.getUrlDecoder().decode(pText);
	}

	/**
	 * Bytes转为Base64
	 * @param pBytes
	 * @return
	 */
	public static String b2T(byte[] pBytes) {
		return Base64.getEncoder().encodeToString(pBytes);
	}

	/**
	 * 生成32位随机码
	 * @return
	 */
	public static String createUUID() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
