/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker;

import excelchecker.ExcelComparer.FileProcessor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
/**
 * @param args the command line arguments
 */
public class ExcelChecker extends JFrame {

    FileDialog fc;
    private static final int buttonWidth = 100;
    private static final int buttonHeight = 25;

    private static String firstWorkerPath = "";
    private static String secondWorkerPath = "";

    JLabel statusValueLabel = new JLabel("Choose folders");
    Thread workThread;

    ExcelChecker() {
        super("ExcelChecker");
        setSize(400, 300);
        setTitle("Excel processor v3");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel firstWorkerPathLabel = new JLabel();
        JLabel secondWorkerPathLabel = new JLabel();

        JLabel firstWorkerPathLabelTitle = new JLabel("First worker path:");
        JLabel secondWorkerPathLabelTitle = new JLabel("Second worker path:");
        JLabel statusLabel = new JLabel("Status:");

        Button buttonChooseFirstWorkerFilesPath = new Button("Worker 1 files.."); // Create and add a Button
        buttonChooseFirstWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = selectFolder();
                    firstWorkerPath = f.getAbsolutePath();
                    firstWorkerPathLabel.setText(getFileNameLabelPath(f));
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
                    File f = selectFolder();
                    secondWorkerPath = f.getAbsolutePath();
                    secondWorkerPathLabel.setText(getFileNameLabelPath(f));
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

        JPanel mainPanel = new JPanel(new GridLayout(10, 0, 5, 5));

        mainPanel.add(firstWorkerPathLabelTitle);
        mainPanel.add(firstWorkerPathLabel);
        mainPanel.add(secondWorkerPathLabelTitle);
        mainPanel.add(secondWorkerPathLabel);
        mainPanel.add(new JPanel());
        mainPanel.add(new JPanel());
        mainPanel.add(new JPanel());
        mainPanel.add(statusLabel);
        mainPanel.add(statusValueLabel);

        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 3));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonChooseFirstWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonChooseSecondWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonProceedFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        panel.add(buttonChooseFirstWorkerFilesPath);
        panel.add(buttonChooseSecondWorkerFilesPath);
        panel.add(new JPanel());
        panel.add(buttonProceedFiles);

        this.add(mainPanel, BorderLayout.WEST);
        this.add(panel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public String getFileNameLabelPath(File file) {
        String result = "..\\";
        File parent = file.getParentFile();
        if (parent != null) {
            result += parent.getName() + "\\" + file.getName();
        }
        else 
        {
             result += file.getName();
        }
        return result;
    }

    public File selectFolder() throws IOException {
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        JFileChooser chooser = new JFileChooser(path);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = null;
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        }
        return f;
    }

    public static void main(String[] args) {
        new ExcelChecker().setVisible(true);
    }

}
