/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

    private static String firstWorkerPath;
    private static String secondWorkerPath;

    ExcelChecker() {
        super("ExcelChecker");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        fc = new FileDialog(this, "Choose a file", FileDialog.LOAD);
        fc.setDirectory("C:\\");

        Button buttonChooseFirstWorkerFilesPath = new Button("Worker 1 files.."); // Create and add a Button
        buttonChooseFirstWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    firstWorkerPath = selectFolder();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button buttonChooseSecondWorkerFilesPath = new Button("Worker 2 files.."); // Create and add a Button
        buttonChooseSecondWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    secondWorkerPath = selectFolder();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button buttonProceedFiles = new Button("Proceed files.."); // Create and add a Button
        buttonProceedFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 3));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonChooseFirstWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonChooseSecondWorkerFilesPath.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        buttonProceedFiles.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        panel.add(buttonChooseFirstWorkerFilesPath);
        panel.add(buttonChooseSecondWorkerFilesPath);
        panel.add(new JPanel());
        panel.add(buttonProceedFiles);
        this.add(panel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }
    
    public void ComapreFiles(String firstWorkerSubdirPath, String secondWorkerSubdirPath)
    {
        
    }

    public void proceedFiles(String firstWorkerPath, String secondWorkerPath) {
        File dir = new File(firstWorkerPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()) {
                    File secondSameWorkerSubDir = new File(secondWorkerPath+"\\"+child.getName());
                    File[] secondWorkerFilesListing = dir.listFiles();
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
    }

    public String selectFolder() throws IOException {
        String path = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //int returnVal = chooser.showSaveDialog(this);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            path = f.getCanonicalPath();
        } else {
            // user changed their mind
        }
        return path;
    }

    public static void main(String[] args) {
        new ExcelChecker().setVisible(true);
    }
}
