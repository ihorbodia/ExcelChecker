/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelRowsCleaner;

import static excelchecker.Common.ExcelHelper.findRow;
import static excelchecker.Common.ExcelHelper.getCellData;
import static excelchecker.Common.ExcelHelper.getNumericDataFromCell;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.ExcelChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ibodia
 */
class DataProcessor implements Runnable {

    Sheet excelDataSheet;
    String excelFileName;
    FileInputStream excelFileInStream;

    DataProcessor(File excelFile) throws FileNotFoundException, IOException {

        if (excelFile != null) {
            excelFileName = excelFile.getName();
        } else {
            System.out.printf("Something wrong in first method " + excelFile.getName());
        }

        excelFileInStream = new FileInputStream(excelFile);

        XSSFWorkbook excelWorkBook = new XSSFWorkbook(excelFileInStream);
        excelDataSheet = excelWorkBook.getSheetAt(0);
    }

    public void proceedFiles() throws IOException {
        Iterator<Row> rowIterator = excelDataSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row != null && !isCellEmpty(row.getCell(1))) {
                try {
                    double cellData = getNumericDataFromCell(row.getCell(row.getFirstCellNum()));
                    if (cellData == 0 ) {
                        excelDataSheet.removeRow(row);
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
        excelFileInStream.close();
    }

    @Override
    public void run() {
        try {
            proceedFiles();
        } catch (IOException ex) {
            Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
