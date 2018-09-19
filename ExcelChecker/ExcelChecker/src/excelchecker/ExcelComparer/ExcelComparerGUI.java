/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelComparer;

import excelchecker.Abstract.TabObject;
import excelchecker.ExcelChecker;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 *
 * @author Ihor
 */
public class ExcelComparerGUI extends TabObject {

    JLabel statusValueLabel = new JLabel("Choose folders");
    Thread workThread;

    Component parent;

    public ExcelComparerGUI() {
        firstLabelInfoPath = "First worker path:";
        secondLabelInfoPath = "Second worker path:";

        firstButtonInfoText = "Worker 1 files..";
        secondButtonInfoText = "Worker 2 files..";

        toolTipStartText = "Choose folders";

        firstLabelInfoVisible = true;
        secondLabelInfoVisible = true;
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
                                new FilesProcessor(firstWorkerPath, secondWorkerPath);
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
                                isProceedButtonEnabled(true);
                                updateToolTip("Something wrong...");
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
