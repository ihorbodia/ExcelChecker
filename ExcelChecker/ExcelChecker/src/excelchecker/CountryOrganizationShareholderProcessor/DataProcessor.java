/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Abstract.DataProcessorAbstract;
import static excelchecker.Common.ExcelHelper.getCellData;
import static excelchecker.Common.ExcelHelper.isCellEmpty;
import excelchecker.Common.WorkbookModel;
import excelchecker.ExcelChecker;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
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

    DataProcessor(File orgExcelFile) throws IOException {
        if (orgExcelFile.getName().contains("xlsx#")) {
            System.out.println("File error");
        } else {
            excelFileName = orgExcelFile.getName();
            this.excelFile = orgExcelFile;
            excelFileInStream = new FileInputStream(this.excelFile);
            excelWorkBook = new XSSFWorkbook(excelFileInStream);
            organisationFileDataSheet = excelWorkBook.getSheetAt(0);
            if (organisationFileDataSheet == null) {
                String test = "";
            }
        }
    }

    @Override
    protected void proceedFiles() throws IOException, ParseException {
        String dataFromBColumn = "";
        String nameOfOrganisation = "";
        Iterator<Row> rowIterator = organisationFileDataSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row orgRow = rowIterator.next();
            if (orgRow != null && !isCellEmpty(orgRow.getCell(2)) && orgRow.getRowNum() != 0) {
                try {
                    String orgCellData = getCellData(orgRow.getCell(2));
                    for (WorkbookModel workBookModel : FilesProcessor.countryDocFiles) {
                        Sheet countryFileDataSheet = workBookModel.workBookFile.getSheetAt(2);
                        for (Row countryRow : countryFileDataSheet) {
                            if (getCellData(countryRow.getCell(0)).contains(orgCellData) && countryRow.getRowNum() != 0) {
                                dataFromBColumn = getCellData(countryRow.getCell(1));
                                nameOfOrganisation = getCellData(countryRow.getCell(0));
                                break;
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in proceedFiles method.", ex);
                }
            }
        }

        for (WorkbookModel workBookModel : FilesProcessor.countryDocFiles) {
            String countryName = excelFileName.split(" ")[0];
            if (workBookModel.name.contains(countryName)) {
                Sheet countryDocSheet = workBookModel.workBookFile.getSheetAt(1);
                for (Row row : countryDocSheet) {
                    if (getCellData(row.getCell(0)).contains(nameOfOrganisation) && row.getRowNum() != 0) {
                        Cell cell = row.getCell(1);
                        int test = 0;
                        if (cell == null) {
                            test = row.getRowNum();
                        }
                        cell.setCellValue(dataFromBColumn);
                    }
                }
            }
        }
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
