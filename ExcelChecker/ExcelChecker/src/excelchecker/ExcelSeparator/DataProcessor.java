/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelSeparator;

import static excelchecker.Common.ExcelHelper.getNumericDataFromCell;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.ExcelChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    XSSFWorkbook excelWorkBook;
    File excelFile;

    DataProcessor(File excelFile) throws IOException {
        if (excelFile != null) {
            excelFileName = excelFile.getName();
            this.excelFile = excelFile;
        } else {
            System.out.print(excelFile.getName() + "Something wrong in first method ");
        }

        excelFileInStream = new FileInputStream(excelFile);

        excelWorkBook = new XSSFWorkbook(excelFileInStream);
        excelDataSheet = excelWorkBook.getSheetAt(0);
    }

    void proceedFiles() throws IOException {
        //ArrayList<Row> rowsForSum = new ArrayList<>();
        double sum = 0.0;
        Iterator<Row> rowIterator = excelDataSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row != null && !isCellEmpty(row.getCell(2))) {
                try {
                    double cellData = getNumericDataFromCell(row.getCell(2));
                    sum += cellData;
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
        
        excelFileInStream.close();
        FileOutputStream fileOut = new FileOutputStream(excelFile.getAbsolutePath());
        excelWorkBook.write(fileOut);
        excelWorkBook.close();
        fileOut.close();
        
    }

    @Override
    public void run() {
        try {
            proceedFiles();
        } catch (IOException ex) {
            Logger.getLogger(excelchecker.ExcelSeparator.DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
