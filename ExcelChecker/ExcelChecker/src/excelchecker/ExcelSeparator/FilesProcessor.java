/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelSeparator;

import excelchecker.Common.FilesHelper;
import excelchecker.Common.StringConsts;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author ibodia
 */
public class FilesProcessor {

    String chosenFolderPath;

    String ToBeCheckedFilesFolderPath;
    String ToBeDividedFolderPath;
    String PerfectFolderPath;

    public FilesProcessor(String chosenFolderPath) throws IOException {
        this.chosenFolderPath = chosenFolderPath;
        createOutputFolders();
        proceedFiles();
    }

    private void proceedFiles() throws IOException {
        File dir = new File(chosenFolderPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File excelFile : directoryListing) {
                if (!excelFile.isDirectory()) {
                     new Thread(new excelchecker.ExcelSeparator.DataProcessor(excelFile)).start();
                }
            }

        }
    }

    private void createOutputFolders() throws FileNotFoundException {
        String choosenFolderParenPath = new File(chosenFolderPath).getParent();
        ToBeDividedFolderPath = choosenFolderParenPath + "\\" + "Column C to be divided by 100";
        ToBeCheckedFilesFolderPath = choosenFolderParenPath + "\\" + "To be checked";
        PerfectFolderPath = choosenFolderParenPath + "\\" + "Perfect";
        
        File toBeDividedFolder = new File(ToBeDividedFolderPath);
        if (!toBeDividedFolder.mkdir()) {
            toBeDividedFolder.setWritable(true);
            StringConsts.ToBeDividedFolderPath = ToBeDividedFolderPath;
        }
        
        File perfectFilesFolder = new File(PerfectFolderPath);
        if (!perfectFilesFolder.mkdir()) {
            perfectFilesFolder.setWritable(true);
            StringConsts.PerfectFolderPath = PerfectFolderPath;
        }
        
        File toBeCheckedFolder = new File(ToBeCheckedFilesFolderPath);
        if (!toBeCheckedFolder.mkdir()) {
            toBeCheckedFolder.setWritable(true);
            StringConsts.ToBeCheckedFilesFolderPath = ToBeCheckedFilesFolderPath;
        }
    }
}
