package utils;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtils {

  private static Logger log = LoggerFactory.getLogger(ExcelUtils.class);
  public void createExcel(String fileName) {
    try {
      Workbook workbook = null;

      if (fileName.endsWith(".xlsx")) {
        workbook = new XSSFWorkbook();
      } else if (fileName.endsWith(".xls")) {
        workbook = new HSSFWorkbook();
      } else {
        System.err.println("The specified file is not Excel file");
      }
      FileOutputStream fileOut = new FileOutputStream(fileName);
      workbook.write(fileOut);
      fileOut.close();
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addSheetAndColumns(String fileName, boolean formattingColumn, String sheetName, String...columns) {
    try {
      if(!new File(fileName).exists()){
        createExcel(fileName);
      }
      FileInputStream inputStream = new FileInputStream(new File(fileName));
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.createSheet(sheetName);
      CellStyle headerCellStyle = workbook.createCellStyle();
      if(formattingColumn) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setFont(headerFont);
      }
      // Create a Row
      Row headerRow = sheet.createRow(0);

      for (int i = 0; i < columns.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columns[i]);
        cell.setCellStyle(headerCellStyle);
      }

      inputStream.close();
      FileOutputStream outputStream = new FileOutputStream(fileName);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  public void writeInExistingExcel(String fileName, String sheetName, boolean bold, String...values) {
    try {
      FileInputStream inputStream = new FileInputStream(new File(fileName));
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.getSheet(sheetName);
      int rowCount = sheet.getLastRowNum();
      Row row = sheet.createRow(++rowCount);
      int columnCount = 0;
      CellStyle style = workbook.createCellStyle();;
      if(bold) {
        Font cellFont = workbook.createFont();
        cellFont.setBold(true);
//				style=row.getRowStyle();
        style.setFont(cellFont);
      }
      for (Object field : values) {
        Cell cell = row.createCell(columnCount++);
        cell.setCellStyle(style);
        if (field instanceof String) {
          cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
          cell.setCellValue((Integer) field);
        }
      }
      inputStream.close();
      FileOutputStream outputStream = new FileOutputStream(fileName);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  public void writeInExistingExcel(String fileName, String sheetName, boolean bold, List<String> values) {
    try {
      FileInputStream inputStream = new FileInputStream(new File(fileName));
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.getSheet(sheetName);
      int rowCount = sheet.getLastRowNum();
      Row row = sheet.createRow(++rowCount);
      int columnCount = 0;
      CellStyle style = workbook.createCellStyle();;
      if(bold) {
        Font cellFont = workbook.createFont();
        cellFont.setBold(true);
//				style=row.getRowStyle();
        style.setFont(cellFont);
      }
      Cell cell = row.createCell(columnCount);
      cell.setCellValue(rowCount);
      for (Object field : values) {
        cell = row.createCell(++columnCount);
        cell.setCellStyle(style);
        if (field instanceof String) {
          cell.setCellValue((String) field);
        } else if (field instanceof Integer) {
          cell.setCellValue((Integer) field);
        }
      }
      inputStream.close();
      FileOutputStream outputStream = new FileOutputStream(fileName);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   *
   * @param fileName
   * @param query : INSERT INTO sheet4(Name,Country) VALUES('Peter','UK')
   *              or
   *              Update Sheet1 Set Country='US' where ID=100 and name='John'
   */
  public void executeQueryInExcel(String fileName, String query) {
    try{
      Connection connection = null;
      try {
        connection = new Fillo().getConnection(fileName);
        connection.executeUpdate(query);
      }catch (Exception e){
        connection = new Fillo().getConnection(fileName);
        connection.executeUpdate(query);
      }
      connection.close();
    }catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   *
   * @param fileName
   * @param query : reference {@link 'https://codoid.com/fillo/'}
   *              Select * from Sheet1 where column1=value1 and column2=value2 and column3=value3
   *              or
   *              executeQuery("Select * from Sheet1").where("ID=100").where("name='John'");
   *              or
   * @param data  : column1,column2,column3
   * @return
   * @throws Exception
   */
  public List<String> getDataFromExcel(String fileName, String query, String data) throws Exception{
    List<String> ls = new ArrayList<String>();
    Connection  connection =new Fillo().getConnection(fileName);
    String[] reqHeaders = data.split(",");
    Recordset rs = null;
    try {
      rs  = connection.executeQuery(query);
    } catch (FilloException e) {
      log.error("Fillo exception occurred "+e.getMessage());
    }catch(Exception e){
      log.error("Another Exception occurred rather than Fillo Exception"+e.getMessage());
    }
    try {
      int row =0;
      assert rs != null;
      while(rs.next()){
        for (String head : reqHeaders) {
          ls.add(rs.getField(head.trim()));
        }
        row++;
      }
      System.out.println("Total row for "+query+" is: "+row);
      connection.close();
    }catch(Exception e){
//      e.printStackTrace();
    }
    return ls;
  }


  public String[][] getAllDataFromExcel(String fileName, String query, String data) throws Exception{
    Connection  connection =new Fillo().getConnection(fileName);
    String[] reqHeaders = data.split(",");
    String[][] obj = null;
    Recordset rs = null;
    try {
      rs  = connection.executeQuery(query);
    } catch (Exception e) {
      log.error("Exception occurred for: "+query);
    }
    try {
      obj = new String[rs.getCount()][reqHeaders.length];
      int row =0;
      while(rs.next()){
        for(int col =0; col <reqHeaders.length; col++){
          String head = reqHeaders[col];
          obj[row][col] = rs.getField(head.trim());
        }
        row++;
      }
      connection.close();
//      System.out.println("Total row for "+query+" is: "+row);
    } catch (Exception e) {
     // e.printStackTrace();
    }
    return obj;
  }



  public List<String> validateExcelHeaders(String fileName, String headers) throws Exception{
    List<String> ls = new ArrayList<String>();
    String[] reqHeaders = headers.split(",");
    try {
      FileInputStream inputStream = new FileInputStream(new File(fileName));
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.getSheetAt(0);
      if (sheet != null) {
        Iterator<Row> iterator = sheet.iterator();

        Row headerRow = iterator.next();
        Iterator<Cell> cells = headerRow.cellIterator();
        List<String> headerList = new ArrayList<>();
        while (cells.hasNext()) {
          Cell cell = cells.next();
          String textStr = cell.toString();
          headerList.add(textStr);
        }
        System.out.println(headerList);
        for(String validateHeader : reqHeaders) {
          if(!headerList.contains(validateHeader)) {
            ls.add(validateHeader);
            System.out.println(validateHeader+" Header not found Excel: ");
          }
        }
      }
      workbook.close();
    }catch(Exception e) {
      e.printStackTrace();
    }
    return ls;
  }



}
