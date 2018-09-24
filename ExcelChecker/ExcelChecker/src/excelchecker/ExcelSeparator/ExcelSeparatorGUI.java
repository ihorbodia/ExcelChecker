/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelSeparator;

import excelchecker.Abstract.TabObject;
import excelchecker.ExcelChecker;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ibodia
 */
public class ExcelSeparatorGUI extends TabObject {
    Component parent;
    Thread workThread;

    public ExcelSeparatorGUI() {
        firstLabelInfoPath = "Path to choosen folder:";
        firstButtonInfoText = "Choose folder";
        
        toolTipStartText = "Choose folder";
        
        firstLabelInfoVisible = true;
        secondLabelInfoVisible = false;
        informationMessage = "For nordic countries only (i.e. Denmark, Finland, Norway and Sweden), the program allocates the files done by freelancers to different folders: \"perfect\", \"to be checked\", and \"column C to be divided by 0\".";
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
                                new excelchecker.ExcelSeparator.FilesProcessor(firstWorkerPath);
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
                                Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                                updateToolTip("Something wrong...");
                                isProceedButtonEnabled(true);
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
