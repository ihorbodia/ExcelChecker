/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelRowsCleaner;

import excelchecker.Abstract.TabObject;
import excelchecker.ExcelChecker;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author ibodia
 */
public class RowsCleanerGUI extends TabObject {

    File filesFolder;

    String chooseFolderPath;
    Component parent;

    JLabel statusValueLabel = new JLabel("Choose folder");
    Thread workThread;

    public RowsCleanerGUI() {
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
                buttonProceedFiles.setEnabled(false);
                statusValueLabel.setText("Processing...");
                try {
                    workThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                new FilesProcessor(firstWorkerPath);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (!workThread.isAlive()) {
                                            statusValueLabel.setText("Finished");
                                            buttonProceedFiles.setEnabled(true);
                                            break;
                                        }
                                    }
                                }).start();
                            } catch (Exception ex) {
                                Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                                statusValueLabel.setText("Something wrong...");
                                buttonProceedFiles.setEnabled(true);
                            }
                        }
                    });
                    workThread.start();
                } catch (Exception ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
}
