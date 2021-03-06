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

        new CountryFilesHolder();
    }

    @Override
    public ActionListener configureProceedHandler() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isProceedButtonEnabled(false);
                updateToolTip("Processing...");
                ExecutorService executor = Executors.newSingleThreadExecutor();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            executor.submit(new excelchecker.CountryOrganizationShareholderProcessor.FilesProcessor(firstWorkerPath));
                        } catch (IOException ex) {
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                            updateToolTip("Something wrong");
                            isProceedButtonEnabled(true);
                        }
                        executor.shutdown();
                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                            updateToolTip("Something wrong");
                            isProceedButtonEnabled(true);
                        }
                        try {
                            saveCountryFiles();
                        } catch (IOException ex) {
                            Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                            updateToolTip("Something wrong");
                            isProceedButtonEnabled(true);
                        }
                        updateToolTip("Finished");
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
