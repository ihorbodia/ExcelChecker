/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Abstract;

import excelchecker.Common.FilesHelper;
import static excelchecker.Common.Constants.buttonHeight;
import static excelchecker.Common.Constants.buttonWidth;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author ibodia
 */
public abstract class TabObject extends JTabbedPane {

    protected String firstWorkerPath = "";
    protected String secondWorkerPath = "";

    protected Button buttonChooseFirstWorkerFilesPath;
    protected Button buttonChooseSecondWorkerFilesPath;
    protected Button buttonProceedFiles;

    protected String firstLabelInfoPath;
    protected String secondLabelInfoPath;
    protected String toolTipLabelInfo;

    protected String firstButtonInfoText;
    protected String secondButtonInfoText;

    protected String toolTipStartText;

    protected boolean firstLabelInfoVisible;
    protected boolean secondLabelInfoVisible;

    private JLabel statusToolTipLabel = new JLabel();
    JPanel controlsPanel;
    Component parent;

    public TabObject() {
        controlsPanel = new JPanel(new GridLayout(0, 4, 5, 3));
    }

    protected void updateToolTip(String text) {
        if ((!firstWorkerPath.isEmpty() && firstLabelInfoVisible) || (!secondWorkerPath.isEmpty() && secondLabelInfoVisible)) {
            this.statusToolTipLabel.setText(text);
        }
    }

    protected void isProceedButtonEnabled(boolean value) {
        this.buttonProceedFiles.setEnabled(value);
    }

    public JPanel initTab() {
        statusToolTipLabel.setText(toolTipStartText);
        JLabel firstWorkerPathLabel = new JLabel();
        firstWorkerPathLabel.setVisible(firstLabelInfoVisible);
        JLabel secondWorkerPathLabel = new JLabel();
        secondWorkerPathLabel.setVisible(secondLabelInfoVisible);

        JLabel firstWorkerPathLabelTitle = new JLabel(firstLabelInfoPath);
        firstWorkerPathLabelTitle.setVisible(firstLabelInfoVisible);

        JLabel secondWorkerPathLabelTitle = new JLabel(secondLabelInfoPath);
        secondWorkerPathLabelTitle.setVisible(secondLabelInfoVisible);

        JLabel statusLabel = new JLabel("Status:");

        buttonChooseFirstWorkerFilesPath = new Button(firstButtonInfoText); // Create and add a Button
        buttonChooseFirstWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = FilesHelper.selectFolder(parent);
                    if (f != null) {
                        firstWorkerPath = f.getAbsolutePath();
                        firstWorkerPathLabel.setText(FilesHelper.getFileNameLabelPath(f));
                        updateToolTip("You can start process files");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        buttonChooseFirstWorkerFilesPath.setVisible(firstLabelInfoVisible);

        buttonChooseSecondWorkerFilesPath = new Button(secondButtonInfoText); // Create and add a Button
        buttonChooseSecondWorkerFilesPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = FilesHelper.selectFolder(parent);
                    if (f != null) {
                        secondWorkerPath = f.getAbsolutePath();
                        secondWorkerPathLabel.setText(FilesHelper.getFileNameLabelPath(f));
                        updateToolTip("You can start process files");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExcelChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        buttonChooseSecondWorkerFilesPath.setVisible(secondLabelInfoVisible);

        buttonProceedFiles = new Button("Proceed files..");
        buttonProceedFiles.addActionListener(configureProceedHandler());

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
        contentPanel.add(statusToolTipLabel);

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

    public abstract ActionListener configureProceedHandler();
}
