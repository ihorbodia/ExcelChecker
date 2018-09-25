/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

/**
 *
 * @author Ihor
 */
public final class ExcelHelper {

    public static boolean isCellEmpty(final Cell cell) {
        if (cell == null || cell.getCellType() == BLANK) {
            return true;
        }
        return cell.getCellType() == STRING && cell.getStringCellValue().isEmpty();
    }

    public static String getCellData(final Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                return NumberToTextConverter.toText(cell.getNumericCellValue()).trim();
            } else if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            }
        }
        return "";
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getNumericDataFromCell(final Cell cell) throws ParseException {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(symbols);
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.STRING) {
                double result = 0.0;
                try {
                    result = Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    String data = cell.getStringCellValue().trim().replaceAll("\t", "").replaceAll("\\s+", "").replaceAll("(^\\h*)|(\\h*$)", "");
                    result = df.parse(StringUtils.strip(data)).doubleValue();
                }
                return result;
            }
        }
        return 0;
    }

    public static Row findRow(Sheet sheet, String cellContent) {
        for (Row row : sheet) {
            if (getCellData(row.getCell(row.getFirstCellNum())).equals(cellContent)) {
                return row;
            }
        }
        return null;
    }

    public static void removeEmptyRows(File file, ArrayList<Row> rows) throws IOException {
        if (rows.size() > 0) {
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : rows) {
                if (getCellData(row.getCell(0)).trim().equals("")) {
                    removeRow(sheet, row.getRowNum());
                }
            }
            File sfile = new File(file.getAbsolutePath());
            OutputStream out = new FileOutputStream(sfile);
            wb.write(out);
            out.flush();
            out.close();
        }
    }

    private static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }
}
