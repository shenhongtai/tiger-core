package cn.imtiger.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具类
 * @author ShenHongtai
 * @date 2019-10-11
 */
public class CollectionUtil {
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
