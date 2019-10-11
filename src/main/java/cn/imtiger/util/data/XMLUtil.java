package cn.imtiger.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * XML解析
 * @author ShenHongtai
 * @date 2019-7-13
 */
public class XMLUtil {
	/**
	 * XML转换为JSON
	 * @param xml
	 * @return com.alibaba.fastjson.JSONObject
	 * @throws DocumentException
	 */
	public static JSONObject parseJSON(String xml) throws DocumentException {
		return (JSONObject) JSON.toJSON(parseMap(xml));
	}
	
	/**
	 * XML转换为Map
	 * @param xml
	 * @return java.util.Map
	 * @throws DocumentException
	 */
	public static Map<String, Object> parseMap(String xml) throws DocumentException {
		// XML转成doc对象
		Document doc = DocumentHelper.parseText(xml);
		// 获取根元素，准备递归解析这个XML树
		Element root = doc.getRootElement();
		Map<String, Object> map = new HashMap<>();
		map.put(root.getName(), getValue(root));
		return map;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> getValue(Element root) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (root.elements() != null) {
			// 如果当前跟节点有子节点，找到子节点
			List<Element> list = root.elements();
			// 遍历每个节点
			for (Element e : list) {
				if (e.elements().size() > 0) {
					// 当前节点不为空，递归遍历子节点
					Map<String, Object> childMap = getValue(e);
					// 置入父节点map
					putAllowDuplicate(map, e.getName(), childMap);
				}
				if (e.elements().size() == 0) {
					// 如果为叶子节点，那么直接把值放入父节点map
					putAllowDuplicate(map, e.getName(), e.getTextTrim());
				}
			}
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private static void putAllowDuplicate(Map<String, Object> map, String childName, Object child) {
		// 判断放入的key是否存在
		if (map.containsKey(childName)) {
			// 判断已存在的对象是否为List
			Object existChild = map.get(childName);
			if (existChild instanceof List) {
				// 加入原有List
				((List<Object>) existChild).add(child);
			} else if (existChild instanceof Map) {
				// 将已有对象转为List
				List<Object> childList = new ArrayList<>();
				childList.add(existChild);
				childList.add(child);
				map.put(childName, childList);
			}
		} else {
			map.put(childName, child);
		}
	}
}
