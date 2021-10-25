package com.jaccro.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.jaccro.model.MarcaBluebook;
import com.jaccro.model.ValorAnioVersionBluebook;
import com.jaccro.model.VersionBluebook;
import com.jaccro.repository.MarcaBluebookRepository;
import com.jaccro.repository.ValorAnioVersionBluebookRepository;
import com.jaccro.util.ExceptionUtil;
import com.jaccro.util.NumberUtil;
import com.jaccro.util.StreamUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelService {
  
  private static final Logger LOGGER = LogManager.getLogger(ExcelService.class);
  
  XSSFWorkbook workBook = null;
  XSSFCellStyle activeVersionStyle;
  XSSFCellStyle inactiveVersionStyle;

  public void write() {
    long startTime = System.currentTimeMillis();

    try {
      LOGGER.debug("Inicio proceso de generación de excel");

      workBook = new XSSFWorkbook();
      XSSFSheet sheet = workBook.createSheet("datos");

      activeVersionStyle = workBook.createCellStyle();
      setActiveVersionStyle(activeVersionStyle);
      
      inactiveVersionStyle = workBook.createCellStyle();
      setInactiveVersionStyle(inactiveVersionStyle);

      int rowIndex = 0;

      List<ValorAnioVersionBluebook> listAllValoresAnio = (new ValorAnioVersionBluebookRepository()).listAll();
      
      List<Integer> allYears = listAllValoresAnio
          .stream()
          .filter(StreamUtil.distinctByKey(valor -> valor.getAnio()))
          .map(valor -> valor.getAnio())
          .collect(Collectors.toList());
      Collections.sort(allYears, Collections.reverseOrder());

      List<VersionBluebook> listVersion = listAllValoresAnio
          .stream()
          .map(valor -> new VersionBluebook(valor.getVersion().getId(), valor.getVersion().getModeloBluebook(), 
            valor.getVersion().getCodigo(), valor.getVersion().getNombre(), valor.getVersion().getAnioLanzamiento(), 
            valor.getVersion().getAnioVigencia(), valor.getVersion().getValorReferencial(), valor.getVersion().isActivo()))
          .filter(StreamUtil.distinctByKey(valor -> valor.getId()))
          .collect(Collectors.toList());
      
      int maxColumnIndex = createHeader(sheet, rowIndex, allYears);
      rowIndex++;

      for(MarcaBluebook brand : (new MarcaBluebookRepository()).listAll()){
        createBrandRow(sheet, brand, rowIndex, maxColumnIndex);
        rowIndex++;

        List<VersionBluebook> listVersionByBrand = listVersion
            .stream()
            .filter(valor -> valor.getModeloBluebook().getMarcaBluebook().getId()==brand.getId())
            .collect(Collectors.toList());

        Collections.sort(listVersionByBrand, new Comparator<VersionBluebook>() {
          @Override
          public int compare(VersionBluebook v1, VersionBluebook v2) {
            return v1.getModeloBluebook().getNombre().compareTo(v2.getModeloBluebook().getNombre());
          }
        });
        
        for(VersionBluebook version : listVersionByBrand){
          List<ValorAnioVersionBluebook> valoresAnioVersion = listAllValoresAnio
              .stream()
              .filter(v -> v.getVersion().getId()==version.getId())
              .collect(Collectors.toList());
          createVersionRow(sheet, brand, version, allYears, valoresAnioVersion, rowIndex);
          rowIndex++;
        }
      }

      createExcelFile(workBook);
    } catch (IOException e) {
      LOGGER.error(ExceptionUtil.stacktraceToString(e));
    } catch(Exception e){
      LOGGER.error(ExceptionUtil.stacktraceToString(e));
    }
    long endTime = System.currentTimeMillis();
    double totalSeconds = (endTime-startTime)/1000;
    
    LOGGER.info(String.format("Fin de proceso de generación de excel. Tiempo de ejecución: %s segundos", totalSeconds));
  }

  private XSSFCellStyle createHeaderStyle(){
    XSSFFont headerFont = workBook.createFont();
    headerFont.setFontHeightInPoints((short)11);
    headerFont.setBold(true);
    byte[] fontRGB = new byte[]{(byte)255, (byte)255, (byte)255};
    headerFont.setColor(new XSSFColor(fontRGB, null));

    XSSFCellStyle headerStyle = workBook.createCellStyle();
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    byte[] backgroundRGB = new byte[]{(byte)42, (byte)96, (byte)153};
    headerStyle.setFillForegroundColor(new XSSFColor(backgroundRGB, null));
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setFont(headerFont);

    return headerStyle;
  }

  private XSSFCellStyle createBrandStyle(){
    XSSFFont headerFont = workBook.createFont();
    headerFont.setFontHeightInPoints((short)11);
    headerFont.setBold(true);

    XSSFCellStyle headerStyle = workBook.createCellStyle();
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    byte[] backgroundRGB = new byte[]{(byte)180, (byte)199, (byte)220};
    headerStyle.setFillForegroundColor(new XSSFColor(backgroundRGB, null));
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setFont(headerFont);

    return headerStyle;
  }

  private void setActiveVersionStyle(XSSFCellStyle cellStyle){
    XSSFFont font = workBook.createFont();
    font.setFontHeightInPoints((short)10);

    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    byte[] backgroundRGB = new byte[]{(byte)51, (byte)163, (byte)81};
    cellStyle.setFillForegroundColor(new XSSFColor(backgroundRGB, null));
    cellStyle.setBorderTop(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setFont(font);
  }

  private void setInactiveVersionStyle(XSSFCellStyle cellStyle){
    XSSFFont font = workBook.createFont();
    font.setFontHeightInPoints((short)10);

    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    byte[] backgroundRGB = new byte[]{(byte)242, (byte)46, (byte)46};
    cellStyle.setFillForegroundColor(new XSSFColor(backgroundRGB, null));
    cellStyle.setBorderTop(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setFont(font);
  }

  private XSSFCellStyle createVersionStyle(){
    XSSFFont font = workBook.createFont();
    font.setFontHeightInPoints((short)10);

    XSSFCellStyle headerStyle = workBook.createCellStyle();
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setFont(font);

    return headerStyle;
  }

  private void createExcelFile(Workbook workbook) throws Exception {
    try{
      FileOutputStream fileOut = new FileOutputStream("BLUEBOOK.xlsx");
      workbook.write(fileOut);
    } catch (IOException e){
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    }
  }

  private int createHeader(XSSFSheet sheet, int rowIndex, List<Integer> allYears) throws Exception {  
    XSSFCellStyle style = createHeaderStyle();
    Row brandRow = sheet.createRow(rowIndex);
    Cell cell0 = brandRow.createCell(0);
    cell0.setCellStyle(style);
    cell0.setCellValue("ID marca");
    Cell cell1 = brandRow.createCell(1);
    cell1.setCellStyle(style);
    cell1.setCellValue("Código versión");
    Cell cell2 = brandRow.createCell(2);
    cell2.setCellStyle(style);
    cell2.setCellValue("Modelo");
    Cell cell3 = brandRow.createCell(3);
    cell3.setCellStyle(style);
    cell3.setCellValue("Versión");
    Cell cell4 =brandRow.createCell(4);
    cell4.setCellStyle(style);
    cell4.setCellValue("Activo");
    Cell cell5 = brandRow.createCell(5);
    cell5.setCellStyle(style);
    cell5.setCellValue("Lanz");
    Cell cell6 = brandRow.createCell(6);
    cell6.setCellStyle(style);
    cell6.setCellValue("Vig");
    Cell cell7 = brandRow.createCell(7);
    cell7.setCellStyle(style);
    cell7.setCellValue("VRN");

    int cellIndex = 7;
    for(int anio : allYears){
      cellIndex++;
      Cell cell = brandRow.createCell(cellIndex);
      cell.setCellStyle(style);
      cell.setCellValue(anio);
    }

    return cellIndex;
  }

  private void createBrandRow(XSSFSheet sheet, MarcaBluebook brand, int rowIndex, int maxColumnIndex){
    XSSFCellStyle style = createBrandStyle();
    Row brandRow = sheet.createRow(rowIndex);

    for(int i=0; i<=maxColumnIndex; i++){
      Cell cell = brandRow.createCell(i);
      if(i==0) cell.setCellValue(brand.getNombre());
      cell.setCellStyle(style);
    }
    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, maxColumnIndex));
  }

  private void createVersionRow(
      XSSFSheet sheet, 
      MarcaBluebook brand, 
      VersionBluebook version, 
      List<Integer> allYears, 
      List<ValorAnioVersionBluebook> valoresAnio, 
      int rowIndex){

    XSSFCellStyle style = createVersionStyle();
    Row versionRow = sheet.createRow(rowIndex);
    Cell cell0 = versionRow.createCell(0);
    cell0.setCellStyle(style);
    cell0.setCellValue(brand.getId());
    Cell cell1 = versionRow.createCell(1);
    cell1.setCellStyle(style);
    if( NumberUtil.isInteger(version.getCodigo()) )
      cell1.setCellValue( Integer.parseInt(version.getCodigo()) );
    else 
      cell1.setCellValue( version.getCodigo() );
    Cell cell2 = versionRow.createCell(2);
    cell2.setCellStyle(style);
    cell2.setCellValue(version.getModeloBluebook().getNombre());
    Cell cell3 = versionRow.createCell(3);
    cell3.setCellStyle(style);
    cell3.setCellValue(version.getNombre());
    Cell cell4 = versionRow.createCell(4);
    cell4.setCellStyle( version.isActivo() ? activeVersionStyle : inactiveVersionStyle );
    Cell cell5 = versionRow.createCell(5);
    cell5.setCellStyle(style);
    cell5.setCellValue(version.getAnioLanzamiento());
    Cell cell6 = versionRow.createCell(6);
    cell6.setCellStyle(style);
    cell6.setCellValue(version.getAnioVigencia());
    Cell cell7 = versionRow.createCell(7);
    cell7.setCellStyle(style);
    cell7.setCellValue(version.getValorReferencial());

    int cellIndex = 7;
    for( int anio : allYears ){
      cellIndex++;
      List<ValorAnioVersionBluebook> auxList = valoresAnio.stream().filter(v -> v.getAnio() == anio).collect(Collectors.toList());
      Cell cell = versionRow.createCell(cellIndex);
      cell.setCellStyle(style);
      if(auxList.isEmpty())
        cell.setCellValue("");
      else
        cell.setCellValue(auxList.get(0).getValorReferencial());
      
    }
  }
}
