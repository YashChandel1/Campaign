/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.newgen.iforms.user;

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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newgen.iforms.user.Model.VehicleExceldata;
import com.newgen.iforms.user.common.NGLog;

/**
 *
 * @author yash.chandel
 */
@WebServlet("/Upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB
public class Upload extends HttpServlet {
	static JSONArray jaar = new JSONArray();
	static List<VehicleExceldata> vehicledata = new ArrayList<>();
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
				List<VehicleExceldata> lis = new ArrayList<>();
				Sheet sheet = workbook.getSheetAt(0);
				String sheetName = sheet.getSheetName();
				NGLog.consoleLog("sheet.getSheetName() : " + sheet.getSheetName());
				SheetNameJson = sheet.getSheetName();
				List<String> headers = new ArrayList<>();
				ArrayNode sheetData = mapper.createArrayNode();
				NGLog.consoleLog("sheet.getLastRowNum() : " + sheet.getLastRowNum());
				for (int j = 1; j <= sheet.getLastRowNum(); j++) {
					VehicleExceldata vec = new VehicleExceldata(sheet.getRow(j).getCell(0).getStringCellValue(),
							sheet.getRow(j).getCell(1).getStringCellValue(),
							sheet.getRow(j).getCell(2).getStringCellValue(),
							sheet.getRow(j).getCell(3).getStringCellValue(),
							(int) sheet.getRow(j).getCell(4).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(5).getNumericCellValue(),
							sheet.getRow(j).getCell(6).getStringCellValue(),
							sheet.getRow(j).getCell(7).getStringCellValue(),
							sheet.getRow(j).getCell(8).getNumericCellValue(),
							sheet.getRow(j).getCell(9).getNumericCellValue(),
							sheet.getRow(j).getCell(12).getNumericCellValue(),
							sheet.getRow(j).getCell(13).getNumericCellValue(),
							sheet.getRow(j).getCell(14).getNumericCellValue(),
							sheet.getRow(j).getCell(15).getNumericCellValue(),
							sheet.getRow(j).getCell(16).getNumericCellValue(),
							sheet.getRow(j).getCell(17).getNumericCellValue(),
							sheet.getRow(j).getCell(18).getNumericCellValue(),
							sheet.getRow(j).getCell(19).getNumericCellValue(),
							sheet.getRow(j).getCell(20).getNumericCellValue(),
							sheet.getRow(j).getCell(21).getNumericCellValue(),
							sheet.getRow(j).getCell(22).getNumericCellValue(),
							sheet.getRow(j).getCell(23).getNumericCellValue(),
							sheet.getRow(j).getCell(24).getNumericCellValue(),
							sheet.getRow(j).getCell(25).getNumericCellValue(),
							sheet.getRow(j).getCell(26).getNumericCellValue(),
							sheet.getRow(j).getCell(27).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(28).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(29).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(30).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(31).getNumericCellValue(),
							(int) sheet.getRow(j).getCell(32).getNumericCellValue(),
							sheet.getRow(j).getCell(33).getNumericCellValue(),
							sheet.getRow(j).getCell(34).getNumericCellValue(),
							sheet.getRow(j).getCell(35).getNumericCellValue(),
							sheet.getRow(j).getCell(36).getNumericCellValue(),
							sheet.getRow(j).getCell(37).getNumericCellValue());
					lis.add(vec);
				}
				lis.forEach(a -> NGLog.consoleLog(a.toString()));
				vehicledata = lis;
				returnVal = "S";
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

	public List<VehicleExceldata> getList() {

		return vehicledata;
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
