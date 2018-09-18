/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelSeparator;

import static excelchecker.Common.ExcelHelper.getNumericDataFromCell;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.Common.StringConsts;
import excelchecker.ExcelChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    void proceedFile() throws IOException {
        double sum = 0.0;
        Iterator<Row> rowIterator = excelDataSheet.rowIterator();
        if (!rowIterator.hasNext()) {
            sum = -1;
        }
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row == null || isCellEmpty(row.getCell(2))) {
                break;
            }
            else {
                try {
                    double cellData = getNumericDataFromCell(row.getCell(2));
                    sum += cellData;
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in proceedFile method.", ex);
                }
            }
        }
        
        if (sum > 1.0) {
            storeFile(StringConsts.ToBeDividedFolderPath);
        }
        else if (sum <= 1.0 && sum >= 0.0) {
            storeFile(StringConsts.PerfectFolderPath);
        }
        else if (sum < 0.0){
            storeFile(StringConsts.ToBeCheckedFilesFolderPath);
        }
        excelFileInStream.close();
    }
    
    private void storeFile(String path) throws IOException {
        String resPath = path +"\\"+ excelFile.getName();
        FileOutputStream fileOut = new FileOutputStream(resPath);
        excelWorkBook.write(fileOut);
        excelWorkBook.close();
        fileOut.close();
    }

    @Override
    public void run() {
        try {
            proceedFile();
        } catch (IOException ex) {
            Logger.getLogger(excelchecker.ExcelSeparator.DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
