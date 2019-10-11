package cn.imtiger.util.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.imtiger.constant.DatabaseConst;

/**
 * SQL工具类
 * @author ShenHongtai
 * @date 2019-10-11
 */
public class SQLUtil {
	private static final Logger logger = LoggerFactory.getLogger(SQLUtil.class);

	/**
	 * 获取数据库类型
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static String getDataBaseType(Connection connection) throws SQLException {
		String databaseType = null;
		if (connection != null) {
			DatabaseMetaData metaData = connection.getMetaData();
			if (metaData != null) {
				String name = metaData.getDatabaseProductName();
				if (ValidateUtil.isNotBlank(name)) {
					if (name.startsWith("Oracle")) {
						databaseType = DatabaseConst.ORACLE;
					} else if (name.startsWith("PostgreSQL")) {
						databaseType = DatabaseConst.POSTGRESQL;
					} else if (name.startsWith("OSCAR")) {
						databaseType = DatabaseConst.OSCAR;
					} else if (name.startsWith("MySQL")) {
						databaseType = DatabaseConst.MYSQL;
					} else if (name.startsWith("DB2/")) {
						databaseType = DatabaseConst.DB2;
					} else if (name.startsWith("DM")) {
						databaseType = DatabaseConst.DAMENG;
					} else if (name.startsWith("Adaptive")) {
						databaseType = DatabaseConst.SYBASE;
					} else if (name.startsWith("Microsoft")) {
						String ver = metaData.getDatabaseProductVersion();
						ver = ver.substring(0, ver.indexOf("."));
						if (ValidateUtil.isNotBlank(ver)) {
							Double dInt = Double.valueOf(ver);
							if (dInt >= 9) {
								databaseType = DatabaseConst.SQLSERVER2005;
							} else {
								databaseType = DatabaseConst.SQLSERVER;
							}
						} else {
							databaseType = DatabaseConst.SQLSERVER;
						}
					} else if (name.startsWith("KingbaseES")) {
						databaseType = DatabaseConst.KINGBASE;
					} else if (name.startsWith("EnterpriseDB")) {
						databaseType = DatabaseConst.ENTERPRISEDB;
					} else if (name.startsWith("IBM Informix Dynamic Server")) {
						databaseType = DatabaseConst.GBASE;
					} else {
						logger.error("不受支持的数据库厂商：" + name);
					}
				}
			} else {
				logger.error("数据库元数据不能为空");
			}
		} else {
			logger.error("数据库连接不能为空");
		}
		return databaseType;
	}

	/**
	 * 包装分页SQL
	 * @param databaseType
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 * @return
	 * @throws SQLException
	 */
	public static String getPaginateSQL(String databaseType, int pageNumber, int pageSize, String select, String sqlExceptSelect)
			throws SQLException {
		StringBuffer sql = new StringBuffer(255);

		if (ValidateUtil.isNotBlank(databaseType)) {
			switch (databaseType) {
			case DatabaseConst.ORACLE:
				forOraclePaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.POSTGRESQL:
				SQLUtil.forPgSQLPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.OSCAR:
			case DatabaseConst.MYSQL:
				SQLUtil.forMySQLPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.DB2:
				SQLUtil.forDB2Paginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.DAMENG:
				SQLUtil.forDmPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.SQLSERVER2005:
				SQLUtil.forSQLServer2005Paginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.KINGBASE:
				SQLUtil.forKingbasePaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.GBASE:
				SQLUtil.forGbasePaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			case DatabaseConst.ENTERPRISEDB:
				SQLUtil.forEnterpriseDbPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect);
				break;
			default:
				logger.error("不受支持的数据库类型：" + databaseType);
			}
		}
		return sql.toString();
	}

	private static final MessageFormat formatLimitString = new MessageFormat(
		"SELECT GLOBAL_TABLE.* FROM ( " + "SELECT " + "ROW_NUMBER() OVER( {0}) AS __MYSEQ__,TEMP_TABLE.* "
				+ "FROM  ( {1} " + ") TEMP_TABLE) GLOBAL_TABLE " + "WHERE GLOBAL_TABLE.__MYSEQ__>{2}");

	/**
	 * SQLServer2005及以上分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forSQLServer2005Paginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		String sqls = select + " " + sqlExceptSelect;
		int start = (pageNumber - 1) * pageSize;
		sqls = "select top ___TOP_NUM___ __TEMP_ORDER_BY_COLUMN__=0, " + sqls.substring(6);
		String sqlserver = formatLimitString
				.format(new String[] { "ORDER BY __TEMP_ORDER_BY_COLUMN__", sqls, start + "" });
		sqlserver = sqlserver.replace("___TOP_NUM___", (pageSize + start) + "");
		sql.append(sqlserver);
	}

	/**
	 * KingBase数据库
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forKingbasePaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		String paramString = select + " " + sqlExceptSelect;
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		StringBuffer localStringBuffer = new StringBuffer(100);
		localStringBuffer.append(paramString);
		localStringBuffer.append(" limit ").append(start).append(" offset ").append(end);
		sql.append(localStringBuffer);
	}

	/**
	 * Enterprise数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forEnterpriseDbPaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		String paramString = select + " " + sqlExceptSelect;
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		StringBuffer localStringBuffer = new StringBuffer(100);
		localStringBuffer.append("SELECT * FROM ( SELECT ROW_.*, ROWNUM ROWNUM_ FROM ( ");
		localStringBuffer.append(paramString);
		localStringBuffer.append(" ) ROW_ WHERE ROWNUM <= ").append(start).append(") WHERE ROWNUM_ > ").append(end);
		sql.append(localStringBuffer);
	}

	/**
	 * PostgreSQL数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forPgSQLPaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		int limit = pageSize;
		int offset = pageSize * (pageNumber - 1);
		sql.append(select).append(" ").append(sqlExceptSelect);
		sql.append(" limit ").append(limit).append(" offset ").append(offset);
	}

	/**
	 * 达梦数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forDmPaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		int offset = pageSize * (pageNumber - 1);
		sql.append(select).append(" ").append(sqlExceptSelect);
		sql.append(" limit ").append(offset).append(", ").append(pageSize);
	}

	/**
	 * DB2分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forDB2Paginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		String paramString = select + " " + sqlExceptSelect;
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		sql.append("SELECT * FROM  (SELECT B.*, ROWNUMBER() OVER() AS RN FROM  (SELECT * FROM (").append(paramString)
				.append(")   ) AS B   )AS A ");
		sql.append("WHERE A.RN BETWEEN ");
		sql.append(start).append(" AND ").append(end);
	}

	/**
	 * MySQL数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forMySQLPaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String concatsql) {
		int offset = pageSize * (pageNumber - 1);
		sql.append(select).append(" ");
		sql.append(concatsql);
		sql.append(" limit ").append(offset).append(", ").append(pageSize); // limit can use one or two '?' to pass
																			// paras
	}

	/**
	 * GBase数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forGbasePaginate(StringBuffer sql, int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		int offset = pageSize * (pageNumber - 1);
		sql.append("select * from (");
		sql.append(select).append(" ").append(" , rownum  rn ");
		sql.append(sqlExceptSelect);
		sql.append(" ) where ").append(" rn ").append(" between  ").append(offset).append(" and ")
				.append(pageNumber * pageSize);
	}

	/**
	 * Oracle数据库分页
	 * @param sql
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 */
	private static void forOraclePaginate(StringBuffer sql, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		sql.append("select * from ( select row_.*, rownum rownum_ from (  ");
		sql.append(select).append(" ").append(sqlExceptSelect);
		sql.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
		sql.append(" where table_alias.rownum_ >= ").append(start);
	}
}
