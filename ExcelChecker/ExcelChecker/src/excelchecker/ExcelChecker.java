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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
/**
 * @param args the command line arguments
 */
public class ExcelChecker extends JFrame {

    public static ArrayList<String> differences;

    FileDialog fc;
    private static final int buttonWidth = 100;
    private static final int buttonHeight = 25;

    private static String firstWorkerPath;
    private static String secondWorkerPath;

    private static String firstWorkerPathLabel;
    private static String secondWorkerPathLabel;

    ExcelChecker() {
        super("ExcelChecker");

        differences = new ArrayList<>();
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel firstWorkerPathLabel = new JLabel();
        JLabel secondWorkerPathLabel = new JLabel();

        JLabel firstWorkerPathLabelTitle = new JLabel("First worker path:");
        JLabel secondWorkerPathLabelTitle = new JLabel("Second worker path:");

        Button buttonChooseFirstWorkerFilesPath = new Button("Worker 1 files.."); // Create and add a Button
        buttonChooseFirstWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = selectFolder();
                    firstWorkerPath = f.getAbsolutePath();
                    firstWorkerPathLabel.setText(getFileNameLabelPath(f));
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
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button buttonProceedFiles = new Button("Proceed files.."); // Create and add a Button
        buttonProceedFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    proceedFiles(firstWorkerPath, secondWorkerPath);
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JPanel mainPanel = new JPanel(new GridLayout(10, 0, 5, 5));

        mainPanel.add(firstWorkerPathLabelTitle);
        mainPanel.add(firstWorkerPathLabel);
        mainPanel.add(secondWorkerPathLabelTitle);
        mainPanel.add(secondWorkerPathLabel);

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
        File parent = file.getParentFile();
        File parent2 = parent.getParentFile();
        System.out.println(file.getParent());
        return ("..\\" + parent2.getName() + "\\" + parent.getName() + "\\" + file.getName());
    }

    public void proceedFiles(String firstWorkerPath, String secondWorkerPath) throws IOException {
        File dir = new File(firstWorkerPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File firstWorkerCountrySubDir : directoryListing) {
                if (firstWorkerCountrySubDir.isDirectory()) {
                    File secondWorkerCountrySubDir = new File(secondWorkerPath + "\\" + firstWorkerCountrySubDir.getName());
                    File[] secondWorkerFilesListing = secondWorkerCountrySubDir.listFiles();

                    for (File firstWorkerExcelFile : firstWorkerCountrySubDir.listFiles()) {
                        File secondExcelFile = null;
                        for (File file : secondWorkerFilesListing) {
                            if (file.getName().startsWith(firstWorkerExcelFile.getName())) {
                                secondExcelFile = file;
                            }
                        }
                        differences.addAll(new ExcelProcessor(firstWorkerExcelFile, secondExcelFile).compareFiles());
                    }
                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        storeOutputFile();
    }

    public File selectFolder() throws IOException {
        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        JFileChooser chooser = new JFileChooser(path);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = null;
        //int returnVal = chooser.showSaveDialog(this);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        } else {
            // user changed their mind
        }
        return f;
    }

    public static void main(String[] args) {
        new ExcelChecker().setVisible(true);
    }

    public void storeOutputFile() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet");

        int rowNum = 0;
        System.out.println("Creating excel");

        for (String diff : differences) {
            Cell cell = sheet.createRow(rowNum++).createCell(0);
            cell.setCellValue(diff);
        }

        try {
            String path = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(new File(path + "\\" + "report.xlsx"));
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
