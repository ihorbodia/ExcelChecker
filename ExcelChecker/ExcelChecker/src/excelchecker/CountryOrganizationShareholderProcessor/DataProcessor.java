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

    DataProcessor(File orgExcelFile) throws IOException {
        if (orgExcelFile.getName().contains("xlsx#")) {
            System.out.println("File error");
        } else {
            excelFileName = orgExcelFile.getName();
            this.excelFile = orgExcelFile;
            excelFileInStream = new FileInputStream(this.excelFile);
            excelWorkBook = new XSSFWorkbook(excelFileInStream);
            excelWorkBook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            organisationFileDataSheet = excelWorkBook.getSheetAt(0);
        }
    }

    @Override
    protected void proceedFiles() throws IOException, ParseException {
        String dataFromBColumn = "";
        String nameOfOrganisation = "";
        Iterator<Row> rowIterator = organisationFileDataSheet.rowIterator();
        ArrayList<Row> rowsForDelete = new ArrayList<Row>();
        Row orgRow = null;
        while (rowIterator.hasNext()) {
            orgRow = rowIterator.next();
            if (orgRow != null && !isCellEmpty(orgRow.getCell(2)) && orgRow.getRowNum() != 0) {
                try {
                    String orgCellData = getCellData(orgRow.getCell(2));
                    for (WorkbookModel workBookModel : CountryFilesHolder.countryDocFiles) {
                        Sheet countryFileDataSheet = workBookModel.workBookFile.getSheetAt(2);
                        for (Row countryRow : countryFileDataSheet) {
                            if (getCellData(countryRow.getCell(0)).contains(orgCellData) && countryRow.getRowNum() != 0) {
                                dataFromBColumn = getCellData(countryRow.getCell(1));
                                nameOfOrganisation = getCellData(countryRow.getCell(0));
                                updateExcelFile(nameOfOrganisation, dataFromBColumn);
                                rowsForDelete.add(orgRow);
                                break;
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in proceedFiles method.", ex);
                    System.out.println("ERROR: Problem with proceedFiles in data processor while processing: " + excelFile.getName());
                } catch (IllegalArgumentException iae) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in proceedFiles method", iae);
                    System.out.println("ERROR:" + iae.getMessage() + ": " + excelFile.getName());
                } catch (OutOfMemoryError e) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, "Something wrong in proceedFiles method, memory error.", e);
                    System.out.println("ERROR: Memory error thrown with: " + excelFile.getName());
                }
            }
        }
        for (Row rowToDelete : rowsForDelete) {
            rowToDelete.getCell(0).setCellValue("");
            rowToDelete.getCell(1).setCellValue("");
            rowToDelete.getCell(2).setCellValue("");
            rowToDelete.getCell(3).setCellValue("");
        }
        System.out.println("INFO: File have been processed: " + excelFile.getName());
        FileOutputStream outputStream = new FileOutputStream(excelFile.getAbsolutePath());
        excelWorkBook.write(outputStream);
        excelWorkBook.close();
        outputStream.close();
    }

    private void updateExcelFile(String namaOfOrganization, String dataFromBColumn) {
        synchronized (CountryFilesHolder.lock) {
            if (namaOfOrganization != "" && dataFromBColumn != "") {
                for (WorkbookModel workBookModel : CountryFilesHolder.countryDocFiles) {
                    String countryName = excelFileName.split(" ")[0];
                    if (workBookModel.name.contains(countryName)) {
                        Sheet countryDocSheet = workBookModel.workBookFile.getSheetAt(1);
                        for (Row row : countryDocSheet) {
                            if (getCellData(row.getCell(0)).contains(namaOfOrganization) && row.getRowNum() != 0) {
                                Cell cell = row.getCell(1);
                                cell.setCellValue(dataFromBColumn);
                                return;
                            }
                        }
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
