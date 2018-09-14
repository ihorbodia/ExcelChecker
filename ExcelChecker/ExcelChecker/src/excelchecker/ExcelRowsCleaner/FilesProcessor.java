/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelRowsCleaner;

import static excelchecker.Common.FilesHelper.storeOutputFile;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ibodia
 */
public class FilesProcessor {
    
    String choosenFolderPath;
    public FilesProcessor(String choosenFolderPath) throws IOException {
        this.choosenFolderPath = choosenFolderPath;
        proceedFiles();
    }

    private void proceedFiles() throws IOException {
        File dir = new File(choosenFolderPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File countrySubdir : directoryListing) {
                if (countrySubdir.isDirectory()) {
                    for (File excelFile : countrySubdir.listFiles()) {
                        new Thread(new DataProcessor(excelFile)).start();
                    }
                }
            }
        }
    }
}
