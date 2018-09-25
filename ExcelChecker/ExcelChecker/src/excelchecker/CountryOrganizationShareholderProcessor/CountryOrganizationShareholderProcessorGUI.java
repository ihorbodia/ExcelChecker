/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Abstract.TabObject;
import excelchecker.Common.WorkbookModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
public class CountryOrganizationShareholderProcessorGUI extends TabObject {

    Component parent;
    Thread workThread;
    boolean isSuccessfully = true;
    String errorMessage;

    public CountryOrganizationShareholderProcessorGUI() {
        firstLabelInfoPath = "Path to choosen folder:";
        firstButtonInfoText = "Choose folder";

        toolTipStartText = "Choose folder";

        firstLabelInfoVisible = true;
        secondLabelInfoVisible = false;

        informationMessage = "For organisation shareholders only, not individual shareholders, "
                + "the program gets \"ff\" or \"nff\" or \"insurance company\" "
                + "from all country doc files, previous version, into the country "
                + "doc files where it is missing. The goal is to reduce the organisation "
                + "shareholder analysis work burden.";
        errorMessage = "";
        new CountryFilesHolder();
    }

    @Override
    public ActionListener configureProceedHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isProceedButtonEnabled(false);
                updateToolTip("Processing...");
                isSuccessfully = true;
                ExecutorService executor = Executors.newSingleThreadExecutor();
                new Thread(new Runnable() {
                    FilesProcessor fp;
                    @Override
                    public void run() {
                        try {
                            fp = new excelchecker.CountryOrganizationShareholderProcessor.FilesProcessor(firstWorkerPath);
                            executor.submit(fp);
                        } catch (IOException ex) {
                            isSuccessfully = false;
                            errorMessage = fp.errorMessage;
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        executor.shutdown();
                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        } catch (InterruptedException ex) {
                            isSuccessfully = false;
                            errorMessage = "Program interrupted";
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            saveCountryFiles();
                        } catch (IOException ex) {
                            isSuccessfully = false;
                            errorMessage = "Problem with saving country doc files";
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (isSuccessfully) {
                            updateToolTip("Finished");
                        } else {
                            if (errorMessage == "") {
                                updateToolTip("Something wrong");
                            }
                            else{
                                updateToolTip(errorMessage);
                            }
                        }
                        isProceedButtonEnabled(true);
                    }
                }).start();
            }
        };
    }

    private void saveCountryFiles() throws FileNotFoundException, IOException {
        for (WorkbookModel countryDocFile : CountryFilesHolder.countryDocFiles) {
            FileOutputStream fileOut = new FileOutputStream(countryDocFile.file.getAbsolutePath());
            countryDocFile.workBookFile.write(fileOut);
            countryDocFile.workBookFile.close();
            fileOut.close();
        }
    }
}
