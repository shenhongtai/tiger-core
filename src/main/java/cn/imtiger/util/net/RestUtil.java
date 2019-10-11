package cn.imtiger.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.imtiger.util.data.ValidateUtil;

/**
 * 请求处理工具类
 * @author ShenHongtai
 * @date 2019-7-13
 */
public class RestUtil {
	/**
	 * 返回字符串格式数据
	 * @param rel
	 * @param response
	 * @throws IOException
	 */
	public static void write(String rel, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(rel);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 获取JSON格式请求数据
	 * @param request
	 * @return
	 */
	public static String getJsonByRequest(HttpServletRequest request) {
		String approvalJson = "";
		String inputLine = "";
		// 解析传递过来的参数
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			while ((inputLine = br.readLine()) != null) {
				approvalJson += inputLine;
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return approvalJson;
	}
	
	/**
	 * 获取当前用户IPv4地址
	 * @param request
	 * @return
	 */
	public static String getIPv4Address(HttpServletRequest request) {
		String ip = request.getHeader("x-real-ip");
		if (ValidateUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
			if (ip != null && ip.length() > 15) {
				if (ip.indexOf(",") > 0) {
					ip = ip.substring(0, ip.indexOf(","));
				}
			}
		}
		if (ValidateUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ValidateUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ValidateUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1")) {
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
					ip = inet.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		return ip;
	}

	/**
	 * 获取当前用户IPv6地址
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getIPv6Address() {
		Enumeration allNetInterfaces = null;
		String ipAddress = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			Enumeration addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = (InetAddress) addresses.nextElement();
				if (ip != null && ip instanceof Inet6Address) {
					ipAddress = ip.getHostAddress();
					String[] ipArr = ipAddress.split("%");
					if (ipArr.length > 1) {
						ipAddress = ipArr[0];
					}
				}
			}
		}
		return ipAddress;
	}

	/**
	 * 获取当前用户端口
	 * @param request
	 * @return
	 */
	public static String getPort(HttpServletRequest request) {
		int port = request.getLocalPort();
		return port + "";
	}
}
