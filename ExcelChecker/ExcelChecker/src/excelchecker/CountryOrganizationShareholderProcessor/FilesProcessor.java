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
import java.util.ArrayList;

/**
 *
 * @author Ihor
 */
class FilesProcessor {

    String chosenFolderPath;
    String organizationFile;
    static ArrayList<WorkbookModel> countryDocFiles;

    public FilesProcessor(String chosenFolderPath) throws IOException {
        this.chosenFolderPath = chosenFolderPath;
        countryDocFiles = new ArrayList<WorkbookModel>();
        initCountryDocFiles();
        proceedFiles();
    }

    private void proceedFiles() throws IOException {
        File dir = new File(chosenFolderPath + "\\result");
        File[] orgFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("organisation shareholder analysis to do");
            }
        });

        for (File orgFile : orgFiles) {
            new Thread(new excelchecker.CountryOrganizationShareholderProcessor.DataProcessor(orgFile)).start();
        }
    }

    private void initCountryDocFiles() throws FileNotFoundException, IOException {
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
                    if (docFiles.length > 1) {
                        return;
                    }
                    countryDocFiles.add(new WorkbookModel(docFiles[0], docFiles[0].getName()));
                    System.out.println(docFiles[0].getAbsolutePath());
                }
            }
        }
        System.out.println("test");

    }
}
