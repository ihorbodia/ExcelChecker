/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Abstract.DataProcessorAbstract;
import static excelchecker.Common.ExcelHelper.getCellData;
import static excelchecker.Common.ExcelHelper.getNumericDataFromCell;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.ExcelChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
class DataProcessor extends DataProcessorAbstract {
    
    Sheet organisationFileDataSheet;
    String excelFileName;
    FileInputStream excelFileInStream;
    XSSFWorkbook excelWorkBook;
    File excelFile;
    
    DataProcessor(File orgExcelFile) throws IOException
    {
         if (orgExcelFile != null) {
            excelFileName = excelFile.getName();
            this.excelFile = orgExcelFile;
        } else {
            System.out.print(excelFile.getName() + "Something wrong in first method ");
        }
        excelFileInStream = new FileInputStream(excelFile);
        excelWorkBook = new XSSFWorkbook(excelFileInStream);
        organisationFileDataSheet = excelWorkBook.getSheetAt(0);
    }
    
    @Override
    protected void proceedFiles() throws IOException, ParseException {
        String dataFromBColumn;
        Iterator<Row> rowIterator = organisationFileDataSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row orgRow = rowIterator.next();
            if (orgRow != null && !isCellEmpty(orgRow.getCell(2))) {
                try {
                    String orgCellData = getCellData(orgRow.getCell(2));
                    for (XSSFWorkbook workBook : FilesProcessor.countryDocFiles) {
                        Sheet countryFileDataSheet = workBook.getSheetAt(2);
                        for (Row countryRow : countryFileDataSheet) {
                            if (getCellData(countryRow.getCell(2)).contains(orgCellData)) {
                                dataFromBColumn = getCellData(countryRow.getCell(1));
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in compareFiles method.", ex);
                }
            }
        }
        
        for (XSSFWorkbook countryWorkBook : FilesProcessor.countryDocFiles) {
            if (excelFileName.split(" ")[0] == countryWorkBook.get) {
                
            }
            Sheet excelSheetToStore = countryWorkBook.getSheetAt(1);
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
            Logger.getLogger(excelchecker.CountryOrganizationShareholderProcessor.DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(excelchecker.CountryOrganizationShareholderProcessor.DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
