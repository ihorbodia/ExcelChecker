/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
public class WorkbookModel {
    public XSSFWorkbook workBook;
    public String name;
    
    public WorkbookModel(File workBook, String name) throws FileNotFoundException, IOException{
        this.workBook = new XSSFWorkbook(new FileInputStream(workBook));
        this.name = name;
    }
}
