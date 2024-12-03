/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.newgen.iforms.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newgen.iforms.user.common.NGLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author yash.chandel
 */
@WebServlet("/UploadExcelJoint")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB
public class UploadExcelJoint extends HttpServlet {
	static JSONArray jaar = new JSONArray();

	// for XLS and XLSX END
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		NGLog.consoleLog("Inside Servlet");
		InputStream inputStream = null;
		Workbook workbook = null;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode excelData = mapper.createObjectNode();
		String SheetNameJson = "";
		// Added by Yash
		String returnVal = "F";
		// End by Yash
		NGLog.consoleLog("Before Part has been created.");
		try {
			Part file = request.getPart("file");
			NGLog.consoleLog("Data from client side : " + request.getParameter("Data"));
			NGLog.consoleLog("After Part has been created.");
			inputStream = file.getInputStream();
			// Use WorkbookFactory to create a Workbook instance based on the file type
			workbook = WorkbookFactory.create(inputStream);
			NGLog.consoleLog("Workbook" + workbook);
			NGLog.consoleLog("Workbook Sheets" + workbook.getNumberOfSheets());
			if (workbook.getNumberOfSheets() >= 1) {
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					Sheet sheet = workbook.getSheetAt(i);
					String sheetName = sheet.getSheetName();
					NGLog.consoleLog("sheet.getSheetName() : " + sheet.getSheetName());
					SheetNameJson = sheet.getSheetName();
					List<String> headers = new ArrayList<>();
					ArrayNode sheetData = mapper.createArrayNode();
					NGLog.consoleLog("sheet.getLastRowNum() : " + sheet.getLastRowNum());
					for (int j = 0; j <= sheet.getLastRowNum(); j++) {
//	                   NGLog.consoleLog(" Inside LOOP  of sheet.getLastRowNum() : " + sheet.getLastRowNum());
						Row row = sheet.getRow(j);
//	                   NGLog.consoleLog(" Row " + row);
						if (j == 0) {
							for (int k = 0; k < row.getLastCellNum(); k++) {
								headers.add(row.getCell(k).getStringCellValue());
								NGLog.consoleLog(" headers " + headers);
							}
						} else {
							ObjectNode rowData = mapper.createObjectNode();
							NGLog.consoleLog(" headers Size " + headers.size());
							for (int k = 0; k < headers.size(); k++) {
								Cell cell = row.getCell(k);
								String headerName = headers.get(k).split("\n")[1];
								returnVal = "S";
								if (cell != null) {
									switch (cell.getCellType()) {
									case FORMULA:
										rowData.put(headerName, cell.getNumericCellValue());
										break;
									case BOOLEAN:
										rowData.put(headerName, cell.getBooleanCellValue());
										break;
									case NUMERIC:
										rowData.put(headerName, cell.getNumericCellValue());
										break;
									case BLANK:
										rowData.put(headerName, "");
										break;
									default:
										rowData.put(headerName, cell.getStringCellValue());
										break;
									}
								} else {
									rowData.put(headerName, "");
								}
							}
							sheetData.add(rowData);
						}
					}
					excelData.set(sheetName, sheetData);
				}
				NGLog.consoleLog("excelData.toString() for joint: " + excelData.toString());
				JSONObject jobj = new JSONObject();
				JSONParser JSONParse = new JSONParser();
				try {
					jobj = (JSONObject) JSONParse.parse(excelData.toString());
				} catch (org.json.simple.parser.ParseException ex) {
					NGLog.consoleLog("Exception in Excel 0 : " + ex);
				}
				JSONArray sapI = (JSONArray) jobj.get(SheetNameJson);
				NGLog.consoleLog("Retrun data for  joint: " + sapI.toString());
				returnVal = returnVal + "~" + sapI.toString();
				jaar = sapI;
				NGLog.consoleLog("Return jaar from Servlet for joint : " + jaar);
				NGLog.consoleLog("Return Values from Servlet for joint : " + returnVal);
			} else {
				returnVal = "F";
			}
			out.println(returnVal);
			out.close();
		} catch (Exception e) {
			returnVal = "F";
			NGLog.consoleLog("Exception in Excel 1 : " + e);
			out.println(returnVal);
			out.close();
		}
	}

	public JSONArray getJSON() {
		NGLog.consoleLog("Inside getJSON ");
		NGLog.consoleLog("JSON Received : " + jaar);
		return jaar;

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
