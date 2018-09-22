/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Abstract.TabObject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
}
