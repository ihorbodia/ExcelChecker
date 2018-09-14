/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelRowsCleaner;

import excelchecker.ExcelChecker;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import static excelchecker.Common.ExcelHelper.getNumericDataFromCell;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

    public void proceedFiles() throws IOException {
        ArrayList<Row> rowsForDelete = new ArrayList<>();
        Iterator<Row> rowIterator = excelDataSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row != null && !isCellEmpty(row.getCell(1))) {
                try {
                    double cellData = getNumericDataFromCell(row.getCell(1));
                    if (cellData == 0.0) {
                        rowsForDelete.add(row);
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
        
        for (Row rowToDelete : rowsForDelete) {
            excelDataSheet.removeRow(rowToDelete);
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
            Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
