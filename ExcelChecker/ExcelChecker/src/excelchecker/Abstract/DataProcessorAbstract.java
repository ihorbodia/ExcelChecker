/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excelchecker.Abstract;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Ihor
 */
public abstract class DataProcessorAbstract implements Runnable {
    
    abstract protected void proceedFiles() throws IOException, ParseException;
    @Override
    abstract public void run();
}
