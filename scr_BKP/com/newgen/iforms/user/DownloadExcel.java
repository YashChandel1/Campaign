/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.newgen.iforms.user;

import com.newgen.iforms.api.IFormAPI;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.user.common.NGCommon;
import com.newgen.iforms.user.common.NGLog;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author yash.chandel
 */
@WebServlet("/DownloadExcel")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB
public class DownloadExcel extends HttpServlet {
	// for XLS and XLSX END
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		NGLog.consoleLog("Inside Download Servlet");
		 try {
	           // Process Excel data...
	           // After processing, create a new workbook
	           Workbook newWorkbook = new XSSFWorkbook();
	           // Create a sheet in the new workbook
	           Sheet newSheet = newWorkbook.createSheet("ResultSheet");
	           // Assuming you have data to write (replace with your actual data)
	           List<String> headers = Arrays.asList("Name", "Age", "Country", "Occupation");
	           // Dummy Excel data
//	           List<List<Object>> excelData1 = com.getExcelData();
//	           NGLog.consoleLog("excelData1 using iforms  : " + excelData1);
	           List<List<Object>> excelData = new ArrayList<>();
	           excelData.add(Arrays.asList("John Doe", 30, "USA", "Engineer"));
	           excelData.add(Arrays.asList("Jane Smith", 25, "Canada", "Teacher"));
	           excelData.add(Arrays.asList("Bob Johnson", 40, "UK", "Doctor"));
	           // Create header row
	           Row headerRow = newSheet.createRow(0);
	           for (int i = 0; i < headers.size(); i++) {
	               Cell cell = headerRow.createCell(i);
	               cell.setCellValue(headers.get(i));
	           }
	           // Create data rows
	           for (int i = 0; i < excelData.size(); i++) {
	               Row dataRow = newSheet.createRow(i + 1);
	               List<Object> rowData = excelData.get(i);
	               for (int j = 0; j < rowData.size(); j++) {
	                   Cell cell = dataRow.createCell(j);
	                   // Assuming you have strings in your data, adjust accordingly
	                   cell.setCellValue(String.valueOf(rowData.get(j)));
	               }
	           }
	           // Set content type to Excel
	           response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	           // Set content disposition to trigger download
	           response.setHeader("Content-Disposition", "attachment; filename=CampaignGenerated.xlsx");
	           // Get the OutputStream from the response
	           try (OutputStream outStream = response.getOutputStream()) {
	               // Write the new workbook to the output stream
	               newWorkbook.write(outStream);
	           }
	           // Close the new workbook
	           newWorkbook.close();
	       } catch (IOException e) {
	           NGLog.consoleLog("Exception in Excel 1 : " + e);
	       }
	}

	// for XLS and XLSX END

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 *      * Handles the HTTP <code>GET</code> method.      *      * @param request
	 * servlet request      * @param response servlet response     
	 *
	 *
	 * @throws ServletException if a servlet-specific error occurs     
	 *
	 * @throws IOException      if an I/O error occurs     
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			NGLog.consoleLog("Exception in processRequest doGet : " + e);
		}
	}

	/**
	 *      * Handles the HTTP <code>POST</code> method.      *      * @param
	 * request servlet request      * @param response servlet response     
	 *
	 *
	 * @throws ServletException if a servlet-specific error occurs     
	 *
	 * @throws IOException      if an I/O error occurs     
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			NGLog.consoleLog("Exception in processRequest doPost : " + e);
		}
	}

	/**
	 *      * Returns a short description of the servlet.      *      * @return a
	 * String containing servlet description     
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
