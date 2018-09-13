/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelComparer;

import static excelchecker.ExcelComparer.DiffsStorage.differences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ibodia
 */
public class FileProcessor {

    String firstWorkerPath;
    String secondWorkerPath;
    DiffsStorage DS;

    public FileProcessor(String firstWorkerPath, String secondWorkerPath) throws IOException, InterruptedException, ExecutionException {
        this.firstWorkerPath = firstWorkerPath;
        this.secondWorkerPath = secondWorkerPath;
        this.DS = new DiffsStorage();
        proceedFiles();
    }

    private void proceedFiles() throws IOException {
        File dir = new File(firstWorkerPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File firstWorkerCountrySubDir : directoryListing) {
                if (firstWorkerCountrySubDir.isDirectory()) {
                    File secondWorkerCountrySubDir = new File(secondWorkerPath + "\\" + firstWorkerCountrySubDir.getName());
                    File[] secondWorkerFilesListing = secondWorkerCountrySubDir.listFiles();

                    for (File firstWorkerExcelFile : firstWorkerCountrySubDir.listFiles()) {
                        File secondExcelFile = null;
                        for (File file : secondWorkerFilesListing) {
                            if (file.getName().startsWith(firstWorkerExcelFile.getName())) {
                                secondExcelFile = file;
                            }
                        }
                        new Thread(new ExcelProcessor(firstWorkerExcelFile, secondExcelFile, DS)).start();
                    }
                }
            }
        }
        storeOutputFile();
    }

    public void storeOutputFile() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet");

        int rowNum = 0;
        for (String diff : differences) {
            Cell cell = sheet.createRow(rowNum++).createCell(0);
            cell.setCellValue(diff);
        }
        try {
            String path = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(new File(path + "\\" + "report.xlsx"));
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}