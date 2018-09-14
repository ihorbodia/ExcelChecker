/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.ExcelRowsCleaner;

import static excelchecker.Common.Constants.buttonHeight;
import static excelchecker.Common.Constants.buttonWidth;
import excelchecker.Common.FilesHelper;
import excelchecker.ExcelChecker;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.border.EmptyBorder;

/**
 *
 * @author ibodia
 */
public class RowsCleanerGUI extends JFrame {

    File filesFolder;

    String chooseFolderPath;
    Component parent;

    JLabel statusValueLabel = new JLabel("Choose folder");
    Thread workThread;

    public RowsCleanerGUI() {

    }

    public JPanel initTab() {
        JLabel choosenFolderPathLabel = new JLabel();

        JLabel firstWorkerPathLabelTitle = new JLabel("Path to choosen folder:");
        JLabel statusLabel = new JLabel("Status:");

        Button chooseFolderPathButon = new Button("Choose folder");
        chooseFolderPathButon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File folder = FilesHelper.selectFolder(parent);
                    if (folder != null) {
                        chooseFolderPath = folder.getAbsolutePath();
                        choosenFolderPathLabel.setText(FilesHelper.getFileNameLabelPath(folder));
                        if (!chooseFolderPath.isEmpty()) {
                            statusValueLabel.setText("You can start process files");
                        }
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
                                new FilesProcessor(chooseFolderPath);
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
        contentPanel.add(choosenFolderPathLabel);
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(new JPanel());
        contentPanel.add(statusLabel);
        contentPanel.add(statusValueLabel);

        JPanel controlsPanel = new JPanel(new GridLayout(0, 4, 5, 3));
        controlsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        chooseFolderPathButon.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonProceedFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        controlsPanel.add(chooseFolderPathButon);
        controlsPanel.add(new JPanel());
        controlsPanel.add(new JPanel());
        controlsPanel.add(buttonProceedFiles);

        mainPanel.add(contentPanel, BorderLayout.WEST);
        mainPanel.add(controlsPanel);

        return mainPanel;
    }
}
