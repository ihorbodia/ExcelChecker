/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Common;

import static excelchecker.ExcelComparer.DiffsStorage.differences;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
public final class FilesHelper {

    FilesHelper() {

    }
    
    public static void storeOutputFile() {
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

    public static File selectFolder(Component parent) throws IOException {
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        JFileChooser chooser = new JFileChooser(path);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = null;
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        }
        return f;
    }

    public static String getFileNameLabelPath(File file) {
        String result = "..\\";
        File parent = file.getParentFile();
        if (parent != null) {
            result += parent.getName() + "\\" + file.getName();
        } else {
            result += file.getName();
        }
        return result;
    }
}
