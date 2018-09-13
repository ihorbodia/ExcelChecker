/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelComparer;

import excelchecker.ExcelChecker;
import excelchecker.Common.FilesHelper;
import static excelchecker.Common.StringConsts.buttonHeight;
import static excelchecker.Common.StringConsts.buttonWidth;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Ihor
 */
public class ExcelComparer extends JFrame {
    
    FileDialog fc;
    private static String firstWorkerPath = "";
    private static String secondWorkerPath = "";

    JLabel statusValueLabel = new JLabel("Choose folders");
    Thread workThread;
    
    Component parent;
    
    public ExcelComparer(){
        
    }
    
    public JPanel initTab()
    {
        JTabbedPane comparerTab = new JTabbedPane();
        
        JLabel firstWorkerPathLabel = new JLabel();
        JLabel secondWorkerPathLabel = new JLabel();

        JLabel firstWorkerPathLabelTitle = new JLabel("First worker path:");
        JLabel secondWorkerPathLabelTitle = new JLabel("Second worker path:");
        JLabel statusLabel = new JLabel("Status:");

        Button buttonChooseFirstWorkerFilesPath = new Button("Worker 1 files.."); // Create and add a Button
        
        buttonChooseFirstWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = FilesHelper.selectFolder(parent);
                    firstWorkerPath = f.getAbsolutePath();
                    firstWorkerPathLabel.setText(FilesHelper.getFileNameLabelPath(f));
                    if (!firstWorkerPath.isEmpty() && !secondWorkerPath.isEmpty()) {
                        statusValueLabel.setText("You can start process files");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button buttonChooseSecondWorkerFilesPath = new Button("Worker 2 files.."); // Create and add a Button
        buttonChooseSecondWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = FilesHelper.selectFolder(parent);
                    secondWorkerPath = f.getAbsolutePath();
                    secondWorkerPathLabel.setText(FilesHelper.getFileNameLabelPath(f));
                    if (!firstWorkerPath.isEmpty() && !secondWorkerPath.isEmpty()) {
                        statusValueLabel.setText("You can start process files");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button buttonProceedFiles = new Button("Proceed files..");
        buttonProceedFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonProceedFiles.setEnabled(false);
                statusValueLabel.setText("Processing...");
                try {
                    workThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                new FileProcessor(firstWorkerPath, secondWorkerPath);
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
        });
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel contentPanel = new JPanel(new GridLayout(10, 0, 5, 5));

        contentPanel.add(firstWorkerPathLabelTitle);
        contentPanel.add(firstWorkerPathLabel);
        contentPanel.add(secondWorkerPathLabelTitle);
        contentPanel.add(secondWorkerPathLabel);
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(statusLabel);
        contentPanel.add(statusValueLabel);

        JPanel controlsPanel = new JPanel(new GridLayout(0, 4, 5, 3));
        controlsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonChooseFirstWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonChooseSecondWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonProceedFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        controlsPanel.add(buttonChooseFirstWorkerFilesPath);
        controlsPanel.add(buttonChooseSecondWorkerFilesPath);
        controlsPanel.add(new JPanel());
        controlsPanel.add(buttonProceedFiles);
        
        mainPanel.add(contentPanel, BorderLayout.WEST);
        mainPanel.add(controlsPanel);
        
        return mainPanel;
    }
}
