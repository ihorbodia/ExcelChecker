/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.CountryOrganizationShareholderProcessor;

import excelchecker.Common.WorkbookModel;
import java.util.ArrayList;

/**
 *
 * @author ibodia
 */
public final class ShareholderRowsToRemove {
    protected static ArrayList<WorkbookModel> rowsToRemove;
    public static final Object lock = new Object();
    ShareholderRowsToRemove(){
        rowsToRemove = new ArrayList<WorkbookModel>();
    }
}
