/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Common;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ihor
 */
public class WorkbookModel {
    public XSSFWorkbook workBook;
    public String name;
    
    WorkbookModel(XSSFWorkbook workBook, String name){
        
    }
}
