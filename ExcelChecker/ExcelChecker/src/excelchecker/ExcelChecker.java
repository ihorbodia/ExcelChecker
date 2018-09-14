/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker;

import excelchecker.ExcelComparer.ExcelComparerGUI;
import excelchecker.ExcelRowsCleaner.RowsCleanerGUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 *
 * @author Ihor
 */
/**
 * @param args the command line arguments
 */
public class ExcelChecker extends JFrame {

    ExcelChecker() {
        super("ExcelChecker");
        setSize(400, 300);
        setTitle("Excel processor v3");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JTabbedPane panes = new JTabbedPane();

        panes.add("Excel comparer", new ExcelComparerGUI().initTab());
        panes.add("Excel rows cleaner", new RowsCleanerGUI().initTab());

        this.add(panes);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        new ExcelChecker().setVisible(true);
    }

}
