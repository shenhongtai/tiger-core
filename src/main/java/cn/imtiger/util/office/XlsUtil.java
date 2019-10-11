package cn.imtiger.util.office;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import cn.imtiger.util.bean.ExcelInfo;
import cn.imtiger.util.data.ValidateUtil;
import cn.imtiger.util.io.FileTransferUtil;

/**
 * Excel工具类 
 * @author ShenHongtai
 * @date 2019-7-13
 */
public class XlsUtil {
	/**
	 * 检查文件扩展名是否为XLS(Excel 97-2003)
	 * @param fileName
	 */
	public static boolean isXlsFile(String fileName) {
		return ValidateUtil.isSpecifiedFileType(fileName, "xls");
	}

	/**
	 * 检查文件扩展名是否为XLSX(Excel 2007+)
	 * @param fileName
	 */
	public static boolean isXlsxFile(String fileName) {
		return ValidateUtil.isSpecifiedFileType(fileName, "xlsx");
	}

	/**
	 * 检查文件扩展名是否为CSV
	 * @param fileName
	 */
	public static boolean isCsvFile(String fileName) {
		return ValidateUtil.isSpecifiedFileType(fileName, "csv");
	}

	/**
	 * 检查是否为Excel文件
	 * @param fileName
	 */
	public static boolean isExcelFile(String fileName) {
		return isXlsFile(fileName) || isXlsxFile(fileName);
	}

	/**
	 * 导出为Excel文件并下载
	 * @param response
	 * @param fileName
	 * @param data
	 * @throws Exception
	 */
	public static void exportWithHTTP(HttpServletRequest request, HttpServletResponse response, String fileName, ExcelInfo data) throws Exception {
		/**
		 * 设置内容类型
		 */
		response.setHeader("content-Type", "application/vnd.ms-excel");
		/**
		 * 设置文件名
		 */
		fileName = FileTransferUtil.formatCharsetForFileName(request, fileName);
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		export(data, response.getOutputStream());
	}

	/**
	 * 导出为Excel文件并写入输出流
	 * @param data
	 * @param out
	 * @throws Exception
	 */
	public static void export(ExcelInfo data, OutputStream out) throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			String sheetName = data.getName();
			if (null == sheetName) {
				sheetName = "Sheet1";
			}
			XSSFSheet sheet = wb.createSheet(sheetName);
			writeExcel(wb, sheet, data);
			wb.write(out);
		} finally {
			wb.close();
		}
	}

	private static void writeExcel(XSSFWorkbook wb, Sheet sheet, ExcelInfo data) {
		int rowIndex = 0;
		rowIndex = writeTitlesToExcel(wb, sheet, data.getTitles());
		writeRowsToExcel(wb, sheet, data.getRows(), rowIndex);
		setColumnsWidth(sheet, data.getColumnsWidth());
	}

	private static int writeTitlesToExcel(XSSFWorkbook wb, Sheet sheet, List<String> titles) {
		int rowIndex = 0;
		int colIndex = 0;

		Font titleFont = wb.createFont();
		titleFont.setFontName("simsun");
		titleFont.setBold(true);
		titleFont.setColor(IndexedColors.BLACK.index);

		XSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		titleStyle.setFillForegroundColor(new XSSFColor(new Color(182, 184, 192)));
		titleStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		titleStyle.setFont(titleFont);
		setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new Color(0, 0, 0)));

		Row titleRow = sheet.createRow(rowIndex);
		colIndex = 0;

		for (String field : titles) {
			Cell cell = titleRow.createCell(colIndex);
			cell.setCellValue(field);
			cell.setCellStyle(titleStyle);
			colIndex++;
		}

		rowIndex++;
		return rowIndex;
	}

	private static int writeRowsToExcel(XSSFWorkbook wb, Sheet sheet, List<List<Object>> rows, int rowIndex) {
		int colIndex = 0;

		Font dataFont = wb.createFont();
		dataFont.setFontName("simsun");
		dataFont.setColor(IndexedColors.BLACK.index);

		XSSFCellStyle dataStyle = wb.createCellStyle();
		dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		dataStyle.setFont(dataFont);
		setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new Color(0, 0, 0)));

		if (rows != null && rows.size() > 0) {
			for (List<Object> rowData : rows) {
				Row dataRow = sheet.createRow(rowIndex);
				colIndex = 0;
				for (Object cellData : rowData) {
					Cell cell = dataRow.createCell(colIndex);
					if (cellData != null) {
						cell.setCellValue(cellData.toString());
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(dataStyle);
					colIndex++;
				}
				rowIndex++;
			}
		}
		return rowIndex;
	}

	private static void setColumnsWidth(Sheet sheet, List<Integer> list) {
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				sheet.setColumnWidth(i, Double.valueOf(list.get(i) * 255.86 + 184.27).intValue());
			}
		}
	}

	private static void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
		style.setBorderTop(border);
		style.setBorderLeft(border);
		style.setBorderRight(border);
		style.setBorderBottom(border);
		style.setBorderColor(BorderSide.TOP, color);
		style.setBorderColor(BorderSide.LEFT, color);
		style.setBorderColor(BorderSide.RIGHT, color);
		style.setBorderColor(BorderSide.BOTTOM, color);
	}
}
