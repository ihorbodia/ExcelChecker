/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker;

import static excelchecker.DiffsStorage.differences;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ibodia
 */
public class ExcelProcessor implements Runnable {

    String firstExcelFileName;
    String secondExcelFileName;

    Sheet firstWorkerDatatypeSheet;
    Sheet secondWorkerDatatypeSheet;

    final DiffsStorage DS;

    public ExcelProcessor(File firstFile, File secondFile, DiffsStorage storage) throws FileNotFoundException, IOException {

        if (firstFile != null) {
            firstExcelFileName = firstFile.getName();
        } else {
            System.out.printf("Something wrong in first method " + secondFile.getName());
        }
        if (secondFile != null) {
            secondExcelFileName = secondFile.getName();
        } else {
            System.out.printf("Something wrong in secondfile method " + firstFile.getName());
        }

        DS = storage;

        FileInputStream firstWorkerExcelFile = new FileInputStream(firstFile);
        FileInputStream secondWorkerExcelFile = new FileInputStream(secondFile);

        XSSFWorkbook firstWorkerWorkbook = new XSSFWorkbook(firstWorkerExcelFile);
        firstWorkerDatatypeSheet = firstWorkerWorkbook.getSheetAt(0);

        Workbook secondWorkerWorkbook = new XSSFWorkbook(secondWorkerExcelFile);
        secondWorkerDatatypeSheet = secondWorkerWorkbook.getSheetAt(0);
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

    private String getCellData(final Cell cell) {
        if (cell == null || cell.getCellType() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue()).trim();
        } else if (cell == null || cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        return null;
    }

    public void compareFiles() {
        Iterator<Row> rowIterator = firstWorkerDatatypeSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row != null && !isCellEmpty(row.getCell(0))) {
                try {
                    String cellData = getCellData(row.getCell(row.getFirstCellNum()));
                    Row searchedRow = findRow(secondWorkerDatatypeSheet, cellData);
                    if (searchedRow != null) {
                        if (getCellData(row.getCell(row.getFirstCellNum() + 1)).equals(getCellData(searchedRow.getCell(row.getFirstCellNum() + 1)))
                                && getCellData(row.getCell(row.getFirstCellNum() + 2)).equals(getCellData(searchedRow.getCell(row.getFirstCellNum() + 2)))) {
                        } else {
                            synchronized (DS) {
                                differences.add(getCellData(row.getCell(row.getFirstCellNum())));
                            }
                        }
                    } else {
                        synchronized (DS) {
                            differences.add(getCellData(row.getCell(row.getFirstCellNum())));
                        }
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
    }

    private Row findRow(Sheet sheet, String cellContent) {
        for (Row row : sheet) {
            if (getCellData(row.getCell(row.getFirstCellNum())).equals(cellContent)) {
                return row;
            }
        }
        return null;
    }

    @Override
    public void run() {
        compareFiles();
    }
}
