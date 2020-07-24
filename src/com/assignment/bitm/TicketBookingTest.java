package com.assignment.bitm;

import org.testng.annotations.Test;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TicketBookingTest {
  static WebDriver driver;
  static Document doc;
  static FileOutputStream fos;
  static PdfWriter writer;
  static float imgWidth;
  static float imgHeight;
  static Image img;
  static byte[] input;
  
  @Test(dataProvider = "dp")
  public void f(String from, String to, String acNonAc, String day, String month, String year) throws InterruptedException, MalformedURLException, IOException, DocumentException {
	  driver.get("http://busbd.com.bd/");
	  //------provide ticket booking info e.g. from, to, date and coach type & take screenshots---
	  Thread.sleep(4000);
	  new Select(driver.findElement(By.id("searchmenu_leavingform"))).selectByVisibleText(from);
	  Thread.sleep(1000);
	  new Select(driver.findElement(By.id("searchmenu_goingto"))).selectByVisibleText(to);
	  Thread.sleep(1500);
	  new Select(driver.findElement(By.id("searchmenu_coachtype"))).selectByVisibleText(acNonAc);
	  Thread.sleep(2000);
	  driver.findElement(By.id("searchmenu_departingon")).click();
	  Thread.sleep(2000);
	  new Select(driver.findElement(By.className("ui-datepicker-year"))).selectByVisibleText(year);
	  Thread.sleep(2000);
	  new Select(driver.findElement(By.className("ui-datepicker-month"))).selectByVisibleText(month);
	  Thread.sleep(2000);
	  driver.findElement(By.linkText(day)).click();
	  takeScreenshot();
	  Thread.sleep(2000);
	  driver.findElement(By.id("searchmenu_submitbutton")).click();
	  Thread.sleep(2000);
	  takeScreenshot();
	  //------------------------------------------------------------------------------------
	  
	  //----------click on view seat & take screenshot---------------------------------
	  List<WebElement> availableSeatNumberList = driver.findElements(By.xpath("//*[@class='search_div']"));
	  List<WebElement> imgList = driver.findElements(By.xpath("//img[@src='http://skins.busbd.com.bd/busbdbrown/default/images/application_view_tile.gif']"));
	  short i=0;
	  short numSeats;
	  for(WebElement e: availableSeatNumberList) {
		  numSeats = Short.parseShort(e.getText());
		  if(numSeats>0) {
			  imgList.get(i).click();
			  break;
		  }
		  i++;
	  }
	  takeScreenshot();
	  //---------------------------------------------------------------------------------------
	  
	  //------------click on seat & take screenshot-------------------------------------------
	  List<WebElement> seatList = driver.findElements(By.cssSelector("a[href='#']"));
	  for(WebElement e: seatList) {
		  if(e.getAttribute("class").trim().contentEquals("tck_seat_hr_checkbox seat_unchecked")) {
			  e.click();
			  break;
		  }
	  }
	 takeScreenshot();
	 //---------------------------------------------------------------------------------------
  }
  
  public static void takeScreenshot() throws MalformedURLException, IOException, DocumentException {
	  input = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	  img = Image.getInstance(input);
	  img.scaleToFit(imgWidth, imgHeight);
	  doc.add(img);
	  doc.add(new Paragraph());
  }
  
  @BeforeMethod
  public void beforeMethod() throws FileNotFoundException, DocumentException {
	  driver = new FirefoxDriver();
	  driver.manage().deleteAllCookies();
	  driver.manage().window().maximize();
	  
	  doc = new Document();
	  String output = "C:\\Users\\user\\eclipse-workspace\\FinalAssignmentBITM\\PDF\\Screenshots.pdf";	
	  fos = new FileOutputStream(output);
	  writer = PdfWriter.getInstance(doc, fos);
	  writer.open();
	  doc.open();
	  imgWidth = (float) (PageSize.A4.getWidth()/1.5);
	  imgHeight = (float) (PageSize.A4.getWidth()/1.5);
  }

  @AfterMethod
  public void afterMethod() throws IOException {
	  driver.close();
	  doc.close();
	  fos.close();
	  writer.close();
  }

  @DataProvider(name = "dp")
  public Object[][] dp() throws EncryptedDocumentException, IOException {
	  FileInputStream fis = new FileInputStream("C:\\Users\\user\\eclipse-workspace\\FinalAssignmentBITM\\input\\bus_ticket_booking.xlsx");
	  Workbook wb = WorkbookFactory.create(fis);
	  DataFormatter formatter = new DataFormatter();
	  Sheet sht = wb.getSheetAt(0);
	  Object[][] data = new Object[1][6];
	  Iterator<Row> rowIt = sht.rowIterator();
	  rowIt.next();	//skips the first row (the headlines)
	  Row row = rowIt.next();
	  data[0][0] = row.getCell(0).toString();	
	  data[0][1] = row.getCell(1).toString();
	  data[0][2] = row.getCell(2).toString();
	  data[0][3] = formatter.formatCellValue(row.getCell(3)); 	
	  data[0][4] = formatter.formatCellValue(row.getCell(4));
	  data[0][5] = formatter.formatCellValue(row.getCell(5));
	  fis.close();
	  return data;
  }
  
  @BeforeClass
  public void beforeClass() {
	  System.setProperty("webdriver.gecko.driver", "D:\\Software Testing\\Software Testing Tools\\Firefox_Gecko_Driver_26\\geckodriver.exe");
  }

  @AfterClass
  public void afterClass() throws IOException {
	  /*
	  fos.close();
	  writer.close();
	  doc.close();
	  */
  }

  @BeforeTest
  public void beforeTest() {
  }

  @AfterTest
  public void afterTest() {
  }

}
