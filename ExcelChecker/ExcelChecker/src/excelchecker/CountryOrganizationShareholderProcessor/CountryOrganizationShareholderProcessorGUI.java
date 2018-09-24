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
                try {
                    workThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                new excelchecker.CountryOrganizationShareholderProcessor.FilesProcessor(firstWorkerPath);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (!workThread.isAlive()) {
                                            try {
                                                saveCountryFiles();
                                            } catch (IOException ex) {
                                                Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            updateToolTip("Finished");
                                            isProceedButtonEnabled(true);
                                            break;
                                        }
                                    }
                                }).start();
                            } catch (Exception ex) {
                                Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                                updateToolTip("Something wrong...");
                                isProceedButtonEnabled(true);
                            }
                        }
                    });
                    workThread.start();
                } catch (Exception ex) {
                    Logger.getLogger(CountryOrganizationShareholderProcessorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
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
