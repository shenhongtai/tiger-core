package cn.imtiger.util.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Excel表格导出对象
 * @author shen_hongtai
 * @date 2019-7-13
 */
public class ExcelInfo implements Serializable {
    private static final long serialVersionUID = 4444017239100620999L;

    // 表头
    private List<String> titles;
    
    // 列宽
    private List<Integer> columnsWidth;

    // 数据
    private List<List<Object>> rows;

    // 页签名称
    private String name;

    public List<Integer> getColumnsWidth() {
		return columnsWidth;
	}

	public void setColumnsWidth(List<Integer> columnsWidth) {
		this.columnsWidth = columnsWidth;
	}

	public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public void setRows(List<List<Object>> rows) {
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
