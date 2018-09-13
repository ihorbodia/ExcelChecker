/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelComparer;

import static excelchecker.Common.ExcelHelper.findRow;
import static excelchecker.Common.ExcelHelper.getCellData;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.ExcelChecker;
import static excelchecker.ExcelComparer.DiffsStorage.differences;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
                                if (!differences.contains(firstExcelFileName)) {
                                    differences.add(firstExcelFileName);
                                }
                            }
                        }
                    } else {
                        synchronized (DS) {
                            if (!differences.contains(firstExcelFileName)) {
                                differences.add(firstExcelFileName);
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
    }

    @Override
    public void run() {
        compareFiles();
    }
}
