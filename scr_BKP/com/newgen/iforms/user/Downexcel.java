/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.newgen.iforms.user;


import com.newgen.iforms.user.common.NGLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author yash.chandel
 */
@SuppressWarnings("serial")
@WebServlet("/Downexcel")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 50) // 50MB
public class Downexcel extends HttpServlet {
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		NGLog.consoleLog("Inside Download Servlet");

		String filePath = System.getProperty("user.dir") + File.separator + "CAMPAIGN_CONFIG" + File.separator
				+ request.getParameter("data") + "file.xlsx";
		
		try (FileInputStream fileIn = new FileInputStream(filePath);
				BufferedInputStream bufIn = new BufferedInputStream(fileIn);
				OutputStream outStream = response.getOutputStream()) {
		
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=VehicleDetails.xlsx");
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = bufIn.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			if (fileIn != null) {
				NGLog.consoleLog("fileIn exist");
				fileIn.close();
			}
			if (bufIn != null) {
				NGLog.consoleLog("bufIn exist");
				bufIn.close();
			}
			if (outStream != null) {
				NGLog.consoleLog("outStream exist");
				outStream.close();
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				
			}
			
			deleteFile(filePath);
			NGLog.consoleLog("Download triggered, and Excel file deleted successfully.");
		} catch (IOException e) {
			NGLog.errorLog(Arrays.toString(e.getStackTrace()));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading the file.");
		}
	}

	
	private void deleteFile(String filePath) {
		File fileToDelete = new File(filePath);
		if (fileToDelete.delete()) {
			NGLog.consoleLog("File deleted successfully from: " + filePath);
		} else {
			NGLog.consoleLog("Failed to delete the file: " + filePath);
		}
	}

	

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
	}

}
