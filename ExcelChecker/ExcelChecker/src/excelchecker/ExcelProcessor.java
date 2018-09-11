/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ibodia
 */
public class ExcelProcessor {

    public String outputPath;

    String firstExcelFileName;
    String secondExcelFileName;

    Sheet firstWorkerDatatypeSheet;
    Sheet secondWorkerDatatypeSheet;

    ArrayList<String> differences;

    public ExcelProcessor(File firstFile, File secondFile) throws FileNotFoundException, IOException {

        firstExcelFileName = firstFile.getName();
        secondExcelFileName = secondFile.getName();

        FileInputStream firstWorkerExcelFile = new FileInputStream(firstFile);
        FileInputStream secondWorkerExcelFile = new FileInputStream(secondFile);

        XSSFWorkbook firstWorkerWorkbook = new XSSFWorkbook(firstWorkerExcelFile);
        firstWorkerDatatypeSheet = firstWorkerWorkbook.getSheetAt(0);

        Workbook secondWorkerWorkbook = new XSSFWorkbook(secondWorkerExcelFile);
        secondWorkerDatatypeSheet = secondWorkerWorkbook.getSheetAt(0);

        this.outputPath = outputPath;
        differences = new ArrayList<>();
    }

    private boolean isCellEmpty(final Cell cell) {
        if (cell == null || cell.getCellType() == BLANK) {
            return true;
        }
        if (cell.getCellType() == STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }

    public ArrayList<String> compareFiles() {
        boolean inData = true;

        Iterator<Row> rowIterator = firstWorkerDatatypeSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row == null || isCellEmpty(row.getCell(0))) {
                inData = false;
            } else {
                Row searchedRow = findRow(secondWorkerDatatypeSheet, row.getCell(row.getFirstCellNum()).getStringCellValue().trim());
                if (row.getCell(row.getFirstCellNum() + 1).getNumericCellValue()
                        != searchedRow.getCell(row.getFirstCellNum() + 1).getNumericCellValue()
                        || row.getCell(row.getFirstCellNum() + 2).getNumericCellValue()
                        != searchedRow.getCell(row.getFirstCellNum() + 2).getNumericCellValue()) {
                    differences.add(row.getCell(row.getFirstCellNum()).getStringCellValue().trim());
                }
            }
        }
        return differences;
    }

    private static Row findRow(Sheet sheet, String cellContent) {
        for (Row row : sheet) {
            if (row.getCell(row.getFirstCellNum()).getStringCellValue().trim().equals(cellContent)) {
                return row;
            }
        }
        return null;
    }
}
