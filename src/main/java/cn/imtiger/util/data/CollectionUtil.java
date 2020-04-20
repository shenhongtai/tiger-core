package cn.imtiger.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 集合工具类
 * @author shen_hongtai
 * @date 2020-4-20
 */
public class CollectionUtil {
	
	/**
	 * 将Map转换为JSONObject
	 * @param map
	 * @return
	 */
	public static JSONObject toJSONObject(Map<String, Object> map) {
		return JSONObject.parseObject(JSON.toJSONString(map));
	}
	
	/**
	 * 将List<Map>转换为JSONArray
	 * @param map
	 * @return
	 */
	public static JSONArray toJSONArray(List<Map<String, Object>> list) {
		return JSONArray.parseArray(JSON.toJSONString(list));
	}
	
	/**
	 * 批量将Map的key值全部转换为大写
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> toUpperKeyMaps(List<Map<String, Object>> list) {
		List<Map<String, Object>> listNew = new ArrayList<>();
		for (Map<String, Object> map : list) {
			listNew.add(toUpperKeyMap(map));
		}
		return listNew;
	}

	/**
	 * Map的key值全部转换为大写
	 * @param map
	 * @return
	 */
	public static Map<String, Object> toUpperKeyMap(Map<String, Object> map) {
		Map<String, Object> mapNew = new HashMap<>();
		Set<Map.Entry<String, Object>> entries = map.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			mapNew.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		return mapNew;
	}

	/**
	 * 批量将Map的key值全部转换为小写
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> toLowerKeyMaps(List<Map<String, Object>> list) {
		List<Map<String, Object>> listNew = new ArrayList<>();
		for (Map<String, Object> map : list) {
			listNew.add(toLowerKeyMap(map));
		}
		return listNew;
	}

	/**
	 * Map的key值全部转换为小写
	 * @param map
	 * @return
	 */
	public static Map<String, Object> toLowerKeyMap(Map<String, Object> map) {
		Map<String, Object> mapNew = new HashMap<>();
		Set<Map.Entry<String, Object>> entries = map.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			mapNew.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		return mapNew;
	}
}
