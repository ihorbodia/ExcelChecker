/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Common.WorkbookModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihor
 */
class FilesProcessor implements Runnable {

    String chosenFolderPath;
    String organizationFile;
    String errorMessage;

    public FilesProcessor(String chosenFolderPath) throws IOException {
        this.chosenFolderPath = chosenFolderPath;
        initCountryDocFiles();
    }

    private void initCountryDocFiles() {
        File dir = new File(chosenFolderPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File countryDirectory : directoryListing) {
                if ((countryDirectory.isDirectory() && !countryDirectory.isFile()) && !countryDirectory.getName().contains("result")) {
                    File[] docFiles = countryDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.contains("doc");
                        }
                    });
                    if (docFiles.length == 1) {
                        try {
                            CountryFilesHolder.countryDocFiles.add(new WorkbookModel(docFiles[0], docFiles[0].getName()));
                        }
                        catch (IOException ex) {
                            errorMessage = "Problem with country doc file";
                        }
                    }
                }
            }
        }
        else {
            errorMessage = "Something wrong with chosen folder";
        }
    }

    @Override
    public void run() {
        File dir = new File(chosenFolderPath + "\\result");
        File[] orgFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("organisation shareholder analysis to do");
            }
        });

        ExecutorService executor = Executors.newCachedThreadPool(); 
        for (File orgFile : orgFiles) {
            Runnable worker = null;
            try {
                worker = new excelchecker.CountryOrganizationShareholderProcessor.DataProcessor(orgFile);
            } catch (IOException ex) {
                Logger.getLogger(FilesProcessor.class.getName()).log(Level.SEVERE, null, ex);
                errorMessage = "Something wrong with organisation shareholder file: "+ orgFile.getName();
            }
            executor.execute(worker);
        }
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            errorMessage = "Program interrupted";
        }
    }
}
