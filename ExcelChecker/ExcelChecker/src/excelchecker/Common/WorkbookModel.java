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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
public class WorkbookModel {
    public XSSFWorkbook workBookFile;
    public String name;
    public File file;
    
    public WorkbookModel(File workBookFile, String name) throws FileNotFoundException, IOException{
        this.workBookFile = new XSSFWorkbook(new FileInputStream(workBookFile));
        this.workBookFile.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        this.name = name;
        this.file = workBookFile;
    }
}
