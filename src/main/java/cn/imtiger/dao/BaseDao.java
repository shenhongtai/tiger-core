package cn.imtiger.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JDBC通用数据访问层
 * @author shen_hongtai
 * @date 2019-12-11
 */
public class BaseDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void execute(String sql) throws Exception {
		this.jdbcTemplate.execute(sql);
	}

	public void batchUpdate(String sql, List<Object[]> batchArgs) {
		this.jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	public int update(String sql) throws Exception {
		return this.jdbcTemplate.update(sql);
	}

	public int update(String sql, Object param) throws Exception {
		return this.jdbcTemplate.update(sql, new Object[]{param});
	}

	public int update(String sql, Object[] params) throws Exception {
		return this.jdbcTemplate.update(sql, params);
	}

	public List<Map<String, Object>> query(String sql) throws Exception {
		sql = sql.toUpperCase();
		return this.jdbcTemplate.queryForList(sql);
	}

	public List<Map<String, Object>> query(String sql, Object param)
			throws Exception {
		sql = sql.toUpperCase();
		return this.jdbcTemplate.queryForList(sql, new Object[]{param});
	}

	public List<Map<String, Object>> query(String sql, Object[] params)
			throws Exception {
		sql = sql.toUpperCase();
		return this.jdbcTemplate.queryForList(sql, params);
	}

	public Map<String, Object> queryOne(String sql) throws Exception {
		try {
			sql = sql.toUpperCase();
			return this.jdbcTemplate.queryForMap(sql);
		} catch (Exception var3) {
			return null;
		}
	}

	public Map<String, Object> queryOne(String sql, Object[] params)
			throws Exception {
		try {
			sql = sql.toUpperCase();
			return this.jdbcTemplate.queryForMap(sql, params);
		} catch (Exception var4) {
			return null;
		}
	}
}
