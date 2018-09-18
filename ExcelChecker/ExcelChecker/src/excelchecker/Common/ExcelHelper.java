/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
        return null;
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
                    String data = cell.getStringCellValue().trim();
                    result = df.parse(data).doubleValue();
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
}
