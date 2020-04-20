package cn.imtiger.util.io;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件操作工具类
 * @author shen_hongtai
 * @date 2019-7-25
 */
public class FileTransferUtil {
	/**
	 * 格式化文件名字符集
	 * 
	 * @param request
	 * @param fileName
	 * @return String 文件名
	 * @throws UnsupportedEncodingException
	 */
	public static String formatCharsetForFileName(HttpServletRequest request, String fileName)
			throws UnsupportedEncodingException {
		if (request.getHeader("User-Agent").toLowerCase(Locale.getDefault()).indexOf("firefox") > 0
				|| request.getHeader("User-Agent").toLowerCase(Locale.getDefault()).indexOf("chrome") > 0) {
			/**
			 * Chrome or Firefox
			 */
			fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		} else if (request.getHeader("User-Agent").toLowerCase(Locale.getDefault()).indexOf("msie") > 0) {
			/**
			 * Internet Explorer
			 */
			fileName = URLEncoder.encode(fileName, "UTF-8");
		} else {
			/**
			 * Others
			 */
			fileName = URLEncoder.encode(fileName, "UTF-8");
		}
		return fileName;
	}

	/**
	 * 从程序资源路径下载文件
	 * 
	 * @param request  请求对象
	 * @param response 返回对象
	 * @param fileName 文件名
	 * @param filePath 文件路径
	 * @throws Exception
	 */
	public static void downloadFileFromResource(HttpServletRequest request, HttpServletResponse response,
			String fileName, String filePath) throws Exception {
		/**
		 * Set content type
		 */
		String contentType = "multipart/form-data";
		response.setHeader("content-Type", contentType);
		/**
		 * Set file name
		 */
		fileName = formatCharsetForFileName(request, fileName);
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		/**
		 * Get resource file
		 */
		BufferedInputStream input = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(filePath);
		/**
		 * Transfer data
		 */
		OutputStream out = response.getOutputStream();
		byte[] b = new byte[2048];
		int total = 0;
		int len;
		while ((len = input.read(b)) != -1) {
			out.write(b, 0, len);
			total += len;
		}
		response.setHeader("Content-Length", String.valueOf(total));
		if (input != null) {
			input.close();
		}
	}
}
