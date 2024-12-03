package com.newgen.iforms.user.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.user.Upload;
import com.newgen.iforms.user.UploadExcelJoint;
import com.newgen.iforms.user.Model.VehicleExceldata;

import lombok.val;

public class NGCommon {
	IFormReference objIForm = null;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat cons = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private BiFunction<String, Integer, Integer> getOrDefaultInt = (val, defVal) -> {
		if (val == null || "".equalsIgnoreCase(val)) {
			return defVal;
		} else {
			return Integer.parseInt(val);
		}
	};
	private BiFunction<String, Double, Double> getOrDefaultdob = (val, defVal) -> {
		if (val == null || "".equalsIgnoreCase(val) || val.equalsIgnoreCase("__") || val.matches("[a-zA-Z]+")) {
			return defVal;
		} else {
			return Double.parseDouble(val);
		}
	};
	BiFunction<String, JSONArray, JSONArray> getOrDefaultJSON = (val, defVal) -> {
		JSONArray jsonArray = new JSONArray();
		if (val == null || "".equalsIgnoreCase(val)) {
			return defVal;
		} else {
			JSONParser par = new JSONParser();
			try {
				return (JSONArray) par.parse(val);
			} catch (org.json.simple.parser.ParseException e) {
				NGLog.errorLog("Error in converison JSON : " + e);
				return defVal;
			}
		}
	};

	// onChangeVehicleBrand
	public String onChangeVehicleBrand(IFormReference objIForm, String value) {

		NGLog.consoleLog("Inside onChangeVehicleBrand in NGCommon");
		String selectedVariant = objIForm.getValue("Variant").toString();
		String sVariantQuery = "SELECT  Description ,tProductID FROM LOS_M_ProductType (NOLOCK) WHERE tBrandID IN ("
				+ value + ")";
		List<List<String>> sVariantResult = null;
		try {
			sVariantResult = objIForm.getDataFromDB(sVariantQuery);
			NGLog.consoleLog("sVariantResult : " + sVariantResult);
			if (sVariantResult != null && !sVariantResult.isEmpty()) {
				objIForm.clearCombo("Variant");
				NGLog.consoleLog("Variant after clear combo : " + objIForm.getValue("Variant").toString());
				for (int k = 0; k < sVariantResult.size(); k++) {
					objIForm.addItemInCombo("Variant", sVariantResult.get(k).get(0), sVariantResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in sVariantQuery");
		}
		NGLog.consoleLog("selectedVariant Value : " + selectedVariant);
		return "";
	}

	// onChangeVariant
	public String onChangeVariant(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeVariant in NGCommon");
		String selectedModelYear = objIForm.getValue("ModelYear").toString();
		String sModelYearQuery = "SELECT YearModel AS [ Model Year], tModelID AS [Model ID] FROM LOS_M_Model (NOLOCK) WHERE tProductID IN ("
				+ value + ")";
		List<List<String>> sModelYearResult = null;
		try {
			sModelYearResult = objIForm.getDataFromDB(sModelYearQuery);
			NGLog.consoleLog("sModelYearResult : " + sModelYearResult);
			if (sModelYearResult != null && !sModelYearResult.isEmpty()) {
				objIForm.clearCombo("ModelYear");
				NGLog.consoleLog("ModelYear after clear combo : " + objIForm.getValue("ModelYear").toString());
				for (int k = 0; k < sModelYearResult.size(); k++) {
					objIForm.addItemInCombo("ModelYear", sModelYearResult.get(k).get(0),
							sModelYearResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in sModelYearQuery");
		}
		NGLog.consoleLog("selectedModelYear Value : " + selectedModelYear);
		return "";
	}

	// onChangeModelYear
	public String onChangeModelYear(IFormReference objIForm, String value) {
		String ModelYear = value.split("~")[0];
		String Locale = value.split("~")[1];
//		boolean AllFlag = false;
		NGLog.consoleLog("Inside onChangeModelYear in NGCommon");
		String selectedModel = objIForm.getValue("Model").toString();
		String sModelQuery = "";
		String BrandCode = objIForm.getValue("VehicleBrandCode").toString();
		if ("0".equalsIgnoreCase(BrandCode)) {
			sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
			// sModelQuery = "SELECT Description,tModelID FROM LOS_M_Model (NOLOCK) WHERE
			// tProductID IN (SELECT tProductID FROM LOS_M_ProductType (nolock) WHERE
			// IsActive='1' AND tBrandID IN (SELECT tBrandID FROM LOS_M_Brand (NOLOCK) WHERE
			// IsActive='1')) AND YearModel IN (SELECT Description FROM LOS_M_ModelYear
			// (NOLOCK) WHERE IsActive='Y') AND IsActive='1'";
//			AllFlag = true;
		} else {
			String VariantCode = objIForm.getValue("VariantCode").toString();
			if ("0".equalsIgnoreCase(VariantCode)) {
				sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
				/*
				 * if ("ALL".equalsIgnoreCase(ModelYear)) { sModelQuery =
				 * "SELECT 'All' AS [Model Name], 0 AS [Model ID]"; // sModelQuery = "SELECT
				 * Description,tModelID FROM LOS_M_Model (NOLOCK) WHERE // tProductID IN (SELECT
				 * tProductID FROM LOS_M_ProductType (nolock) WHERE // IsActive='1' AND tBrandID
				 * IN (SELECT tBrandID FROM LOS_M_Brand (NOLOCK) WHERE // IsActive='1')) AND
				 * YearModel IN (SELECT Description FROM LOS_M_ModelYear // (NOLOCK) WHERE
				 * IsActive='Y') AND IsActive='1'"; } else { // Model query sModelQuery =
				 * "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN arDescription WHEN '" + Locale
				 * +
				 * "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
				 * + ModelYear +
				 * "' and  tProductID IN (SELECT tProductID FROM LOS_M_ProductType WHERE tBrandID='"
				 * + BrandCode + "' AND IsActive='1') AND IsActive='1'"; }
				 */
//				AllFlag = true;
			} else {
				// Model query
				if ("All".equalsIgnoreCase(ModelYear)) {
					sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
//					AllFlag = true;
				} else {
					sModelQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN arDescription WHEN '" + Locale
							+ "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
							+ ModelYear + "' and  tProductID ='" + objIForm.getValue("VariantCode")
							+ "' AND IsActive='1'";
				}
			}
		}
		/*
		 * sModelQuery = "SELECT CASE WHEN '" + Locale +
		 * "' ='_ar' THEN arDescription WHEN '" + Locale +
		 * "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
		 * + ModelYear + "' and  tProductID ='" + objIForm.getValue("VariantCode") +
		 * "' AND IsActive='1'";
		 */
		List<List<String>> sModelResult = null;
		NGLog.consoleLog("sModel Query : " + sModelQuery);
		try {
			sModelResult = objIForm.getDataFromDB(sModelQuery);
			NGLog.consoleLog("sModelResult : " + sModelResult);
			if (sModelResult != null && !sModelResult.isEmpty()) {
				objIForm.clearCombo("Model");
				objIForm.clearCombo("Colour");
				objIForm.clearCombo("ModelCode");
				objIForm.clearCombo("ModelSuffix");
				NGLog.consoleLog("Model after clear combo : " + objIForm.getValue("Model").toString());
				for (int k = 0; k < sModelResult.size(); k++) {
					objIForm.addItemInCombo("Model", sModelResult.get(k).get(0), sModelResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in sModelQuery");
		}
//		if (AllFlag) {
//			NGLog.consoleLog("Flag True");
//			objIForm.setValue("Model", "0");
//			NGLog.consoleLog("After Setting Model Code Value : " + objIForm.getValue("Model"));
//		}
		NGLog.consoleLog("selectedVehicleGrid_Model Value : " + selectedModel);
//		objIForm.setValue("Model", selectedModel);
		return "";
	}

	// onChangeModel
	public String onChangeModel(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeModel in NGCommon");
		String ModelCode = value.split("~")[0];
		String Locale = value.split("~")[1];

		String selectedColour = objIForm.getValue("Colour").toString();
		String selectedModelSuffix = objIForm.getValue("ModelSuffix").toString();
		String selectedModelCode = objIForm.getValue("ModelCode").toString();
		String sColourQuery = "";
		String sModelSuffixQuery = "";
		String sModelCodeQuery = "";

		JSONArray ModelCodeJson = new JSONArray();
		JSONArray ModelSuffixJson = new JSONArray();
		JSONArray ModelColourJson = new JSONArray();

		if (!"0".equalsIgnoreCase(ModelCode)) {
			sColourQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN ArBodyColour WHEN '" + Locale
					+ "' ='_ar_SA' THEN ArBodyColour ELSE BodyColour END AS [Vehicle Exterior Color] , BodyColorCode AS [Model Color ID] FROM LOS_M_ModelColorMap (NOLOCK) WHERE tModelID IN ("
					+ ModelCode + ")  AND Active='1'";
			sModelSuffixQuery = "SELECT ModelCodeSuffix,ModelCodeSuffix FROM LOS_M_Model (NOLOCK) WHERE tModelID IN ("
					+ ModelCode + ") AND IsActive='1'";
			sModelCodeQuery = "SELECT ModelCode,ModelCode FROM LOS_M_Model (NOLOCK) WHERE tModelID IN (" + ModelCode
					+ ") AND IsActive='1'";
		} else {
			sColourQuery = "SELECT 'All',0";
			sModelSuffixQuery = "SELECT 'All',0";
			sModelCodeQuery = "SELECT 'All',0";
		}
		List<List<String>> sColourResult = null;
		List<List<String>> sModelSuffixResult = null;
		List<List<String>> sModelCodeResult = null;
		try {
			sColourResult = objIForm.getDataFromDB(sColourQuery);
			NGLog.consoleLog("sColourResult : " + sColourResult);
			if (sColourResult != null && !sColourResult.isEmpty()) {
				objIForm.clearCombo("Colour");
				NGLog.consoleLog("Colour after clear combo : " + objIForm.getValue("Colour").toString());
				for (int k = 0; k < sColourResult.size(); k++) {
					objIForm.addItemInCombo("Colour", sColourResult.get(k).get(0), sColourResult.get(k).get(1));
					ModelColourJson.add(sColourResult.get(k).get(1));
				}
			}
			sModelSuffixResult = objIForm.getDataFromDB(sModelSuffixQuery);
			NGLog.consoleLog("sModelSuffixResult : " + sModelSuffixResult);
			if (sModelSuffixResult != null && !sModelSuffixResult.isEmpty()) {
				objIForm.clearCombo("ModelSuffix");
				NGLog.consoleLog("ModelSuffix after clear combo : " + objIForm.getValue("ModelSuffix").toString());
				for (int k = 0; k < sModelSuffixResult.size(); k++) {
					objIForm.addItemInCombo("ModelSuffix", sModelSuffixResult.get(k).get(0),
							sModelSuffixResult.get(k).get(1));
					ModelSuffixJson.add(sModelSuffixResult.get(k).get(1));
				}
			}
			sModelCodeResult = objIForm.getDataFromDB(sModelCodeQuery);
			NGLog.consoleLog("sModelCodeResult : " + sModelCodeResult);
			if (sModelCodeResult != null && !sModelCodeResult.isEmpty()) {
				objIForm.clearCombo("ModelCode");
				NGLog.consoleLog("ModelCode after clear combo : " + objIForm.getValue("ModelCode").toString());
				for (int k = 0; k < sModelCodeResult.size(); k++) {
					objIForm.addItemInCombo("ModelCode", sModelCodeResult.get(k).get(0),
							sModelCodeResult.get(k).get(1));
					ModelCodeJson.add(sModelCodeResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeModel");
		}
//		if ("All".equalsIgnoreCase(objIForm.getValue("Variant").toString())
//				|| "All".equalsIgnoreCase(objIForm.getValue("ModelYear").toString())
//				|| "All".equalsIgnoreCase(objIForm.getValue("VehicleBrand").toString())) {
//			NGLog.consoleLog("Inside All Condition");
//			objIForm.setValue("ModelSuffix", "0");
//			objIForm.setValue("ModelCode", "0");
//			objIForm.setValue("Colour", "0");
//			NGLog.consoleLog("After setting Value :" + objIForm.getValue("ModelCode"));
//		}
		NGLog.consoleLog("Colour JSON Value : " + ModelCodeJson);
		NGLog.consoleLog("ModelSuffix JSON Value : " + ModelSuffixJson);
		NGLog.consoleLog("ModelCode JSON Value : " + ModelCodeJson);

		objIForm.setValue("ModelCode", ModelCodeJson);
		objIForm.setValue("Colour", ModelColourJson);
		objIForm.setValue("ModelSuffix", ModelSuffixJson);
		return "";
	}

	// onChangeModelYear
	public String onChangeGridModelYear(IFormReference objIForm, String value) {
		String ModelYear = value.split("~")[0];
		String Locale = value.split("~")[1];

		NGLog.consoleLog("Inside onChangeGridModelYear in NGCommon");
		String selectedModel = objIForm.getValue("Grid_Model").toString();
		String sModelQuery = "";
		String BrandCode = objIForm.getValue("Grid_VehicleBrandCode").toString();
		if ("0".equalsIgnoreCase(BrandCode)) {
			sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
			// sModelQuery = "SELECT Description,tModelID FROM LOS_M_Model (NOLOCK) WHERE
			// tProductID IN (SELECT tProductID FROM LOS_M_ProductType (nolock) WHERE
			// IsActive='1' AND tBrandID IN (SELECT tBrandID FROM LOS_M_Brand (NOLOCK) WHERE
			// IsActive='1')) AND YearModel IN (SELECT Description FROM LOS_M_ModelYear
			// (NOLOCK) WHERE IsActive='Y') AND IsActive='1'";
		} else {
			String VariantCode = objIForm.getValue("VariantCode").toString();
			if ("0".equalsIgnoreCase(VariantCode)) {
				sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
				/*
				 * if ("ALL".equalsIgnoreCase(ModelYear)) { sModelQuery =
				 * "SELECT 'All' AS [Model Name], 0 AS [Model ID]"; // sModelQuery = "SELECT
				 * Description,tModelID FROM LOS_M_Model (NOLOCK) WHERE // tProductID IN (SELECT
				 * tProductID FROM LOS_M_ProductType (nolock) WHERE // IsActive='1' AND tBrandID
				 * IN (SELECT tBrandID FROM LOS_M_Brand (NOLOCK) WHERE // IsActive='1')) AND
				 * YearModel IN (SELECT Description FROM LOS_M_ModelYear // (NOLOCK) WHERE
				 * IsActive='Y') AND IsActive='1'"; } else { // Model query sModelQuery =
				 * "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN arDescription WHEN '" + Locale
				 * +
				 * "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
				 * + ModelYear +
				 * "' and  tProductID IN (SELECT tProductID FROM LOS_M_ProductType WHERE tBrandID='"
				 * + BrandCode + "' AND IsActive='1') AND IsActive='1'"; }
				 */
			} else {
				// Model query
				if ("All".equalsIgnoreCase(ModelYear)) {
					sModelQuery = "SELECT 'All' AS [Model Name], 0 AS [Model ID]";
				} else {
					sModelQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN arDescription WHEN '" + Locale
							+ "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
							+ ModelYear + "' and  tProductID ='" + objIForm.getValue("Grid_VariantCode")
							+ "' AND IsActive='1'";
				}
			}
		}
		/*
		 * sModelQuery = "SELECT CASE WHEN '" + Locale +
		 * "' ='_ar' THEN arDescription WHEN '" + Locale +
		 * "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
		 * + ModelYear + "' and  tProductID ='" + objIForm.getValue("VariantCode") +
		 * "' AND IsActive='1'";
		 */
		List<List<String>> sModelResult = null;
		NGLog.consoleLog("sModel Query : " + sModelQuery);
		try {
			sModelResult = objIForm.getDataFromDB(sModelQuery);
			NGLog.consoleLog("sModelResult : " + sModelResult);
			if (sModelResult != null && !sModelResult.isEmpty()) {
				objIForm.clearCombo("Grid_Model");
				objIForm.clearCombo("Grid_Colour");
				objIForm.clearCombo("Grid_ModelCode");
				objIForm.clearCombo("Grid_ModelSuffix");
				NGLog.consoleLog("Model after clear combo : " + objIForm.getValue("Grid_Model").toString());
				for (int k = 0; k < sModelResult.size(); k++) {
					objIForm.addItemInCombo("Grid_Model", sModelResult.get(k).get(0), sModelResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in sModelQuery");
		}
		NGLog.consoleLog("selectedVehicleGrid_Model Value : " + selectedModel);
//			objIForm.setValue("Model", selectedModel);
		return "";
	}

	// onChangeModel
	public String onChangeGridModel(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeGridModel in NGCommon");
		String ModelCode = value.split("~")[0];
		String Locale = value.split("~")[1];

		String selectedColour = objIForm.getValue("Grid_Colour").toString();
		String selectedModelSuffix = objIForm.getValue("Grid_ModelSuffix").toString();
		String selectedModelCode = objIForm.getValue("Grid_ModelCode").toString();
		String sColourQuery = "";
		String sModelSuffixQuery = "";
		String sModelCodeQuery = "";
		JSONArray ModelCodeJson = new JSONArray();
		JSONArray ModelSuffixJson = new JSONArray();
		JSONArray ModelColourJson = new JSONArray();
		if (!"0".equalsIgnoreCase(ModelCode)) {
			sColourQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN ArBodyColour WHEN '" + Locale
					+ "' ='_ar_SA' THEN ArBodyColour ELSE BodyColour END AS [Vehicle Exterior Color] , BodyColorCode AS [Model Color ID] FROM LOS_M_ModelColorMap (NOLOCK) WHERE tModelID IN ("
					+ ModelCode + ")  AND Active='1'";
			sModelSuffixQuery = "SELECT ModelCodeSuffix,ModelCodeSuffix FROM LOS_M_Model (NOLOCK) WHERE tModelID IN ("
					+ ModelCode + ") AND IsActive='1'";
			sModelCodeQuery = "SELECT ModelCode,ModelCode FROM LOS_M_Model (NOLOCK) WHERE tModelID IN (" + ModelCode
					+ ") AND IsActive='1'";
		} else {
			sColourQuery = "SELECT 'All',0";
			sModelSuffixQuery = "SELECT 'All',0";
			sModelCodeQuery = "SELECT 'All',0";
		}
		List<List<String>> sColourResult = null;
		List<List<String>> sModelSuffixResult = null;
		List<List<String>> sModelCodeResult = null;
		try {
			sColourResult = objIForm.getDataFromDB(sColourQuery);
			NGLog.consoleLog("sColourResult : " + sColourResult);
			if (sColourResult != null && !sColourResult.isEmpty()) {
				objIForm.clearCombo("Grid_Colour");
				NGLog.consoleLog("Colour after clear combo : " + objIForm.getValue("Grid_Colour").toString());
				for (int k = 0; k < sColourResult.size(); k++) {
					objIForm.addItemInCombo("Grid_Colour", sColourResult.get(k).get(0), sColourResult.get(k).get(1));
					ModelColourJson.add(sColourResult.get(k).get(1));
				}
			}

			sModelSuffixResult = objIForm.getDataFromDB(sModelSuffixQuery);
			NGLog.consoleLog("sModelSuffixResult : " + sModelSuffixResult);
			if (sModelSuffixResult != null && !sModelSuffixResult.isEmpty()) {
				objIForm.clearCombo("Grid_ModelSuffix");
				NGLog.consoleLog("ModelSuffix after clear combo : " + objIForm.getValue("Grid_ModelSuffix").toString());
				for (int k = 0; k < sModelSuffixResult.size(); k++) {
					objIForm.addItemInCombo("Grid_ModelSuffix", sModelSuffixResult.get(k).get(0),
							sModelSuffixResult.get(k).get(1));
					ModelSuffixJson.add(sModelSuffixResult.get(k).get(1));
				}
			}

			sModelCodeResult = objIForm.getDataFromDB(sModelCodeQuery);
			NGLog.consoleLog("sModelCodeResult : " + sModelCodeResult);
			if (sModelCodeResult != null && !sModelCodeResult.isEmpty()) {
				objIForm.clearCombo("Grid_ModelCode");
				NGLog.consoleLog("ModelCode after clear combo : " + objIForm.getValue("Grid_ModelCode").toString());
				for (int k = 0; k < sModelCodeResult.size(); k++) {
					objIForm.addItemInCombo("Grid_ModelCode", sModelCodeResult.get(k).get(0),
							sModelCodeResult.get(k).get(1));
					ModelCodeJson.add(sModelCodeResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeGridModel");
		}
		NGLog.consoleLog("ModelColourJson Value : " + ModelColourJson);
		NGLog.consoleLog("ModelSuffixJson Value : " + ModelSuffixJson);
		NGLog.consoleLog("ModelCodeJson Value : " + ModelCodeJson);
		objIForm.setValue("Grid_ModelCode", ModelCodeJson);
		objIForm.setValue("Grid_Colour", ModelColourJson);
		objIForm.setValue("Grid_ModelSuffix", ModelSuffixJson);
		return "";
	}

	// For generating Campaign code
	public String generateCampCode(IFormReference objIForm) {
		NGLog.consoleLog("Inside generateCampCode in NGCommon");
		Set<String> vehiclebrands = new HashSet<>();
		StringBuffer CampCode = new StringBuffer();
		CampCode.append(objIForm.getValue("ProductType"));
//		int VehicleGridCount = getOrDefaultInt.apply(value, 0);
		for (int i = 0; i < objIForm.getDataFromGrid("CampaignGrid").size(); i++) {
			vehiclebrands.add(objIForm.getTableCellValue("CampaignGrid", i, 9));
		}
		NGLog.consoleLog("vehiclebrands Size : " + vehiclebrands.size());
		try {
			if (vehiclebrands.size() == 1 && !vehiclebrands.contains("0")) {
				String brandID = objIForm.getTableCellValue("CampaignGrid", 0, 9);
				String sBrandquery = "SELECT Top 1 ShortDescription FROM LOS_M_Brand (NOLOCK) WHERE tBrandID = '"
						+ brandID.trim() + "' AND ShortDescription IS NOT NULL and ShortDescription != 'NULL'";
				List<List<String>> sBrandResult = null;
				sBrandResult = objIForm.getDataFromDB(sBrandquery);
				NGLog.consoleLog("Brnad Query  : " + sBrandquery);
				NGLog.consoleLog("Brnad Query Result : " + sBrandResult);
				if (!sBrandResult.isEmpty() && sBrandResult != null) {
					CampCode.append(sBrandResult.get(0).get(0).trim());
				}
			} else if (vehiclebrands.size() > 1) {
				CampCode.append("MUL");
			} else if (vehiclebrands.contains("0")) {
				CampCode.append("MUL");
			} else {
				CampCode.append("GEN");
			}
			Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int currentmonth = localDate.getMonthValue();
			CampCode.append((currentmonth > 9) ? currentmonth : "0" + currentmonth);
			// Current Year
			int year = localDate.getYear();
			String yr = String.valueOf(year);
			CampCode.append(yr.substring(2));

			// Sequential Number
			String sNumberquery = "SELECT TOP 1 CampaignCode FROM LOS_CAMPAIGN_EXT WHERE RequestType ='NEW' AND SUBSTRING(CampaignCode, CHARINDEX('/', CampaignCode) - 2, CHARINDEX('/', CampaignCode)-8)='"
					+ yr.substring(2) + "' AND ISNULL( ApplicationNo ,'')!='"
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId()
					+ "' ORDER BY CAST(SUBSTRING(CampaignCode, CHARINDEX('/', CampaignCode) + 1, LEN(CampaignCode)) AS INT) DESC";
			List<List<String>> sNumberResult = null;
			sNumberResult = objIForm.getDataFromDB(sNumberquery);
			if (sNumberResult != null && !sNumberResult.isEmpty()) {
				if ("".equalsIgnoreCase(objIForm.getValue("CampaignCode").toString())) {
					int num = getOrDefaultInt.apply(sNumberResult.get(0).get(0).split("/")[1], 0);
					num = num + 1;
					CampCode.append("/" + ((num > 9) ? num : "0" + num));
				} else {
					CampCode.append("/" + objIForm.getValue("CampaignCode").toString().split("/")[1]);
				}
			} else {
				CampCode.append("/01");
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeModel");
		}
		NGLog.consoleLog("Campaign Code Generated : " + CampCode);
		objIForm.setValue("CampaignCode", CampCode.toString());
		return "";
	}

	public String onChangeCopyCampCode(IFormReference objIForm, String Value) {
		NGLog.consoleLog("Inside onChangeCopyCampCode in NGCommon");
		String sReturnValue = "";
		String processID = "";
		String CalledFrom = Value.split("~")[0];
		String CampCode_Name = Value.split("~")[1];
		try {
			String sWIdataQuery = "";
			List<String> Keys = new ArrayList<>();
			if ("CopyCampaignCode".equalsIgnoreCase(CalledFrom)) {
				sWIdataQuery = "SELECT TOP 1 a.CampaignType,a.CampaignName,a.AgeRangeMin,a.AgeRangeMax,a.IncomeRangeMin,a.IncomeRangeMax,a.PRFinanceRate,a.AdminFeesWithVATWaiver,a.AdminFeesDiscount,a.SpecialNeeds,a.FullyWaveAdminFee,a.ApplicationNo FROM LOS_CAMPAIGN_EXT (NOLOCK) a INNER JOIN Camp_CampaignInfo b ON b.CampaignCode=a.CampaignCode  WHERE b.CampaignCode= '"
						+ CampCode_Name + "' AND b.isActive='Y' ORDER by a.itemindex desc";
				Keys.addAll(Arrays.asList("CampaignType", "CampaignName", "AgeRangeMin", "AgeRangeMax",
						"IncomeRangeMin", "IncomeRangeMax", "PRFinanceRate", "AdminVATWaiver", "CashAdminFeesDiscount",
						"SpecialNeeds", "FullyWave"));
			} else if ("CampaignName".equalsIgnoreCase(CalledFrom)) {
				sWIdataQuery = "SELECT Top 1 a.CampaignType,a.CampaignCode,a.PromoCode,a.StartDate,a.EndDate,a.AgeRangeMin,a.AgeRangeMax,a.IncomeRangeMin,a.IncomeRangeMax,a.PRFinanceRate,a.AdminFeesWithVATWaiver,a.AdminFeesDiscount,a.SpecialNeeds,a.FullyWaveAdminFee,a.ApplicationNo FROM LOS_CAMPAIGN_EXT (NOLOCK)  a INNER JOIN Camp_CampaignInfo b ON b.CampaignCode=a.CampaignCode  WHERE a.CampaignCode=(SELECT CampaignCode FROM Camp_CampaignInfo(NOLOCK) WHERE CampaignName= N'"
						+ CampCode_Name + "') AND  a.CampaignName= N'" + CampCode_Name
						+ "' AND b.isActive='Y' ORDER by a.itemindex desc";
				Keys.addAll(Arrays.asList("CampaignType", "CampaignCode", "PromoCode", "StartDate", "EndDate",
						"AgeRangeMin", "AgeRangeMax", "IncomeRangeMin", "IncomeRangeMax", "PRFinanceRate",
						"AdminVATWaiver", "CashAdminFeesDiscount", "SpecialNeeds", "FullyWave"));
			}
			List<List<String>> sWIdataResult = null;
			NGLog.consoleLog("Query for over all data : " + sWIdataQuery);

			sWIdataResult = objIForm.getDataFromDB(sWIdataQuery);
			if (sWIdataResult != null && !sWIdataResult.isEmpty()) {
				String regex = "([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})";
				JSONObject ExtData = new JSONObject();
				for (int i = 0; i < sWIdataResult.get(0).size() - 1; i++) {
					if ((sWIdataResult.get(0).get(i)).matches(regex)) {
						try {
							Date dts = cons.parse(sWIdataResult.get(0).get(i));
							NGLog.consoleLog("Date formated : " + sdf.format(dts));
							ExtData.put(Keys.get(i), sdf.format(dts));
						} catch (ParseException e) {
							NGLog.errorLog("Error in date formatting : " + sWIdataResult.get(0).get(i));
						}
					} else {
						ExtData.put(Keys.get(i), sWIdataResult.get(0).get(i));
					}
				}
				NGLog.consoleLog("Before" + sWIdataResult.get(0).get(sWIdataResult.get(0).size() - 1));
				processID = sWIdataResult.get(0).get(sWIdataResult.get(0).size() - 1);
				NGLog.consoleLog("ProcessID captured is : " + processID);
				SetValues(objIForm, ExtData);

				// For Cash
				if ("CF".equalsIgnoreCase(objIForm.getValue("ProductType").toString())) {
					NGLog.consoleLog("Inside Cash Parameters");
					String sQuery = "";
					JSONArray CashCustomerType = new JSONArray();
					JSONArray CashProvince = new JSONArray();
					JSONArray CashCity = new JSONArray();
					JSONArray CashGender = new JSONArray();
					JSONArray CashOccupation = new JSONArray();
					JSONArray CashEmployeeType = new JSONArray();
					JSONArray CashEmployerName = new JSONArray();
					JSONArray CashNationality = new JSONArray();

					List<List<String>> CashCustomerTypeResult = null;
					List<List<String>> CashProvinceResult = null;
					List<List<String>> CashCityResult = null;
					List<List<String>> CashGenderResult = null;
					List<List<String>> CashOccupationResult = null;
					List<List<String>> CashEmployeeTypeResult = null;
					List<List<String>> CashEmployerNameResult = null;
					List<List<String>> CashNationalityResult = null;

					sQuery = "SELECT CustomerType FROM LOS_CAMP_CMPLX_CustomerType (NOLOCK) WHERE PID='" + processID
							+ "'";
					CashCustomerTypeResult = objIForm.getDataFromDB(sQuery);
					if (CashCustomerTypeResult != null && !CashCustomerTypeResult.isEmpty()) {
						for (int j = 0; j < CashCustomerTypeResult.size(); j++) {
							CashCustomerType.add(CashCustomerTypeResult.get(j).get(0));
						}
					}
					sQuery = "SELECT Province FROM LOS_CAMP_CMPLX_Province (NOLOCK) WHERE PID='" + processID + "'";
					CashProvinceResult = objIForm.getDataFromDB(sQuery);
					if (CashProvinceResult != null && !CashProvinceResult.isEmpty()) {
						for (int j = 0; j < CashProvinceResult.size(); j++) {
							CashProvince.add(CashProvinceResult.get(j).get(0));
						}
					}
					sQuery = "SELECT City FROM LOS_CAMP_CMPLX_City (NOLOCK) WHERE PID='" + processID + "'";
					CashCityResult = objIForm.getDataFromDB(sQuery);
					if (CashCityResult != null && !CashCityResult.isEmpty()) {
						for (int j = 0; j < CashCityResult.size(); j++) {
							CashCity.add(CashCityResult.get(j).get(0));
						}
					}
					sQuery = "SELECT Gender FROM LOS_CAMP_CMPLX_Gender (NOLOCK) WHERE PID='" + processID + "'";
					CashGenderResult = objIForm.getDataFromDB(sQuery);
					if (CashGenderResult != null && !CashGenderResult.isEmpty()) {
						for (int j = 0; j < CashGenderResult.size(); j++) {
							CashGender.add(CashGenderResult.get(j).get(0));
						}
					}
					sQuery = "SELECT OccupationSector FROM LOS_CAMP_CMPLX_OccupationSector (NOLOCK) WHERE PID='"
							+ processID + "'";
					CashOccupationResult = objIForm.getDataFromDB(sQuery);
					if (CashOccupationResult != null && !CashOccupationResult.isEmpty()) {
						for (int j = 0; j < CashOccupationResult.size(); j++) {
							CashOccupation.add(CashOccupationResult.get(j).get(0));
						}
					}
					sQuery = "SELECT EmployeeType FROM LOS_CAMP_CMPLX_EmployeeType (NOLOCK) WHERE PID='" + processID
							+ "'";
					CashEmployeeTypeResult = objIForm.getDataFromDB(sQuery);
					if (CashEmployeeTypeResult != null && !CashEmployeeTypeResult.isEmpty()) {
						for (int j = 0; j < CashEmployeeTypeResult.size(); j++) {
							CashEmployeeType.add(CashEmployeeTypeResult.get(j).get(0));
						}
					}
					sQuery = "SELECT EmployerName FROM LOS_CAMP_CMPLX_EmployerName (NOLOCK) WHERE PID='" + processID
							+ "'";
					CashEmployerNameResult = objIForm.getDataFromDB(sQuery);
					if (CashEmployerNameResult != null && !CashEmployerNameResult.isEmpty()) {
						for (int j = 0; j < CashEmployerNameResult.size(); j++) {
							CashEmployerName.add(CashEmployerNameResult.get(j).get(0));
						}
					}
					sQuery = "SELECT Nationality FROM LOS_CAMP_CMPLX_Nationality (NOLOCK) WHERE PID='" + processID
							+ "'";
					CashNationalityResult = objIForm.getDataFromDB(sQuery);
					if (CashNationalityResult != null && !CashNationalityResult.isEmpty()) {
						for (int j = 0; j < CashNationalityResult.size(); j++) {
							CashNationality.add(CashNationalityResult.get(j).get(0));
						}
					}
					NGLog.consoleLog("Cash Parameters : " + CashCustomerType);

					objIForm.setValue("CustomerType", CashCustomerType);
					objIForm.setValue("Province", CashProvince);
					objIForm.setValue("City", CashCity);
					objIForm.setValue("Gender", CashGender);
					objIForm.setValue("OccupationSector", CashOccupation);
					objIForm.setValue("EmployeeType", CashEmployeeType);
					objIForm.setValue("EmployerName", CashEmployerName);
					objIForm.setValue("Nationality", CashNationality);
				}
				List<List<String>> subProductResult = null;
				JSONArray ProductSubType = new JSONArray();
				String sProductQuery = "SELECT ProductSubType from LOS_CAMP_CMPLX_ProductSubType (NOLOCK) WHERE PID ='"
						+ processID + "'";
				subProductResult = objIForm.getDataFromDB(sProductQuery);
				if (subProductResult != null && !subProductResult.isEmpty()) {
					for (int j = 0; j < subProductResult.size(); j++) {
						ProductSubType.add(subProductResult.get(j).get(0));
					}
				}

				// For Cash End
				String Campaignquery = "SELECT DistributorWise, VehicleBrand, Variant, VehicleBrandCode, VariantCode, ModelYear, GracePeriod, Tenure, FinancePercentage, InsurancePercentage, DownPaymentPercentage, RetailPrice, MinAge, MaxAge, MinIncome, MaxIncome, SpecialNeeds, ModelName, RVPercentage, ChildMapper,ActualRetailPrice FROM LOS_CAMP_CMPLX_CampaignParameters WHERE PID='"
						+ processID + "'";
				NGLog.consoleLog("Campaignquery : " + Campaignquery);
				List<List<String>> CampaignResult = null;
				CampaignResult = objIForm.getDataFromDB(Campaignquery);
				JSONArray CampaignArr = new JSONArray();
				NGLog.consoleLog("getdatafrom grid : " + objIForm.getDataFromGrid("CampaignGrid"));
				if (CampaignResult != null && !CampaignResult.isEmpty()) {
					objIForm.clearTable("CampaignGrid");
					for (int i = 0; i < CampaignResult.size(); i++) {
						JSONObject jobj = new JSONObject();
						String sChildModelQuery = "";
						String sChildColourQuery = "";
						String sChildModelSuffixQuery = "";
						String sChildModelCodeQuery = "";
						String sChildCustomerTypeQuery = "";
						String sChildCityQuery = "";
						String sChildCustomerIDQuery = "";
						String sChildEmployeeTypeQuery = "";
						String sChildEmployerNameQuery = "";
						String sChildGenderQuery = "";
						String sChildNationalityQuery = "";
						String sChildOccupationSectorQuery = "";
						String sChildProvinceQuery = "";
						String sChildVehicleTypeQuery = "";
						String sChildVINNumberQuery = "";
						String sChildWholesalersQuery = "";
						String sChildPRParamsQuery = "";

						JSONArray Model = new JSONArray();
						JSONArray Colour = new JSONArray();
						JSONArray ModelSuffix = new JSONArray();
						JSONArray ModelCode = new JSONArray();
						JSONArray OccupationSector = new JSONArray();
						JSONArray Province = new JSONArray();
						JSONArray City = new JSONArray();
						JSONArray CustomerType = new JSONArray();
						JSONArray CustomerID = new JSONArray();
						JSONArray Gender = new JSONArray();
						JSONArray EmployeeType = new JSONArray();
						JSONArray EmployerName = new JSONArray();
						JSONArray Nationality = new JSONArray();
						JSONArray Channel = new JSONArray();
						JSONArray VehicleType = new JSONArray();
						JSONArray VINNumber = new JSONArray();
						JSONArray Wholesalers = new JSONArray();

						List<List<String>> sChildModelResult = null;
						List<List<String>> sChildColourResult = null;
						List<List<String>> sChildModelSuffixResult = null;
						List<List<String>> sChildModelCodeResult = null;
						List<List<String>> sChildCustomerTypeResult = null;
						List<List<String>> sChildCityResult = null;
						List<List<String>> sChildCustomerIDResult = null;
						List<List<String>> sChildEmployeeTypeResult = null;
						List<List<String>> sChildEmployerNameResult = null;
						List<List<String>> sChildGenderResult = null;
						List<List<String>> sChildNationalityResult = null;
						List<List<String>> sChildOccupationSectorResult = null;
						List<List<String>> sChildProvinceResult = null;
						List<List<String>> sChildVehicleTypeResult = null;
						List<List<String>> sChildVINNumberResult = null;
						List<List<String>> sChildWholesalersResult = null;
						List<List<String>> sChildPRParamsResult = null;
						jobj.put("Grid_DistributorWise", CampaignResult.get(i).get(0));
						jobj.put("Grid_VehicleBrand", CampaignResult.get(i).get(1));
						jobj.put("Grid_Variant", CampaignResult.get(i).get(2));
						jobj.put("Grid_VehicleBrandCode", CampaignResult.get(i).get(3));
						jobj.put("Grid_VariantCode", CampaignResult.get(i).get(4));
						jobj.put("Grid_ModelYear", CampaignResult.get(i).get(5));
						jobj.put("Grid_GracePeriod", CampaignResult.get(i).get(6));
						jobj.put("Grid_Tenure", CampaignResult.get(i).get(7));
						jobj.put("Grid_FinancePercentage", CampaignResult.get(i).get(8));
						jobj.put("Grid_InsurancePercentage", CampaignResult.get(i).get(9));
						jobj.put("Grid_DownPayment", CampaignResult.get(i).get(10));
						jobj.put("Grid_RetailPrice", CampaignResult.get(i).get(11));
						jobj.put("Grid_ActualRetailPrice", CampaignResult.get(i).get(20));
						jobj.put("Grid_AgeRangeMin", CampaignResult.get(i).get(12));
						jobj.put("Grid_AgeRangeMax", CampaignResult.get(i).get(13));
						jobj.put("Grid_IncomeRangeMin", CampaignResult.get(i).get(14));
						jobj.put("Grid_IncomeRangeMax", CampaignResult.get(i).get(15));
						jobj.put("Grid_SpecialNeeds", CampaignResult.get(i).get(16));
						jobj.put("Grid_ModelName", CampaignResult.get(i).get(17));
						jobj.put("Grid_RVPercentage", CampaignResult.get(i).get(18));
						if (!"".equalsIgnoreCase(CampaignResult.get(i).get(19))) {
							// Model
							sChildModelQuery = "SELECT Model FROM LOS_CAMP_CHILD_Model (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							NGLog.consoleLog("sChildModelQuery : " + sChildModelQuery);
							NGLog.consoleLog("sChildModelResult : " + sChildModelResult);
							sChildModelResult = objIForm.getDataFromDB(sChildModelQuery);
							NGLog.consoleLog("sChildModelResult : " + sChildModelResult);
							if (sChildModelResult != null && !sChildModelResult.isEmpty()) {
								for (int j = 0; j < sChildModelResult.size(); j++) {
									Model.add(sChildModelResult.get(j).get(0));
								}
							}
							// Colour
							sChildColourQuery = "SELECT Colour FROM LOS_CAMP_CHILD_Colour (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildColourResult = objIForm.getDataFromDB(sChildColourQuery);
							if (sChildColourResult != null && !sChildColourResult.isEmpty()) {
								for (int j = 0; j < sChildColourResult.size(); j++) {
									Colour.add(sChildColourResult.get(j).get(0));
								}
							}
							// ModelSuffix
							sChildModelSuffixQuery = "SELECT ModelSuffix FROM LOS_CAMP_CHILD_ModelSuffix (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildModelSuffixResult = objIForm.getDataFromDB(sChildModelSuffixQuery);
							if (sChildModelSuffixResult != null && !sChildModelSuffixResult.isEmpty()) {
								for (int j = 0; j < sChildModelSuffixResult.size(); j++) {
									ModelSuffix.add(sChildModelSuffixResult.get(j).get(0));
								}
							}
							// ModelCode
							sChildModelCodeQuery = "SELECT ModelCode FROM LOS_CAMP_CHILD_ModelCode (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildModelCodeResult = objIForm.getDataFromDB(sChildModelCodeQuery);
							if (sChildModelCodeResult != null && !sChildModelCodeResult.isEmpty()) {
								for (int j = 0; j < sChildModelCodeResult.size(); j++) {
									ModelCode.add(sChildModelCodeResult.get(j).get(0));
								}
							}

							// CustomerType
							sChildCustomerTypeQuery = "SELECT CustomerType FROM LOS_CAMP_CHILD_CustomerType (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildCustomerTypeResult = objIForm.getDataFromDB(sChildCustomerTypeQuery);
							if (sChildCustomerTypeResult != null && !sChildCustomerTypeResult.isEmpty()) {
								for (int j = 0; j < sChildCustomerTypeResult.size(); j++) {
									CustomerType.add(sChildCustomerTypeResult.get(j).get(0));
								}
							}

							// City
							sChildCityQuery = "SELECT City FROM LOS_CAMP_CHILD_City (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildCityResult = objIForm.getDataFromDB(sChildCityQuery);
							if (sChildCityResult != null && !sChildCityResult.isEmpty()) {
								for (int j = 0; j < sChildCityResult.size(); j++) {
									City.add(sChildCityResult.get(j).get(0));
								}
							}

							// CustomerID
							sChildCustomerIDQuery = "SELECT CustomerID FROM LOS_CAMP_CHILD_CustomerID (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildCustomerIDResult = objIForm.getDataFromDB(sChildCustomerIDQuery);
							if (sChildCustomerIDResult != null && !sChildCustomerIDResult.isEmpty()) {
								for (int j = 0; j < sChildCustomerIDResult.size(); j++) {
									CustomerID.add(sChildCustomerIDResult.get(j).get(0));
								}
							}

							// EmployeeType
							sChildEmployeeTypeQuery = "SELECT EmployeeType FROM LOS_CAMP_CHILD_EmployeeType (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildEmployeeTypeResult = objIForm.getDataFromDB(sChildEmployeeTypeQuery);
							if (sChildEmployeeTypeResult != null && !sChildEmployeeTypeResult.isEmpty()) {
								for (int j = 0; j < sChildEmployeeTypeResult.size(); j++) {
									EmployeeType.add(sChildEmployeeTypeResult.get(j).get(0));
								}
							}

							// EmployerName
							sChildEmployerNameQuery = "SELECT EmployerName FROM LOS_CAMP_CHILD_EmployerName (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildEmployerNameResult = objIForm.getDataFromDB(sChildEmployerNameQuery);
							if (sChildEmployerNameResult != null && !sChildEmployerNameResult.isEmpty()) {
								for (int j = 0; j < sChildEmployerNameResult.size(); j++) {
									EmployerName.add(sChildEmployerNameResult.get(j).get(0));
								}
							}

							// Gender
							sChildGenderQuery = "SELECT Gender FROM LOS_CAMP_CHILD_Gender (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildGenderResult = objIForm.getDataFromDB(sChildGenderQuery);
							if (sChildGenderResult != null && !sChildGenderResult.isEmpty()) {
								for (int j = 0; j < sChildGenderResult.size(); j++) {
									Gender.add(sChildGenderResult.get(j).get(0));
								}
							}

							// Nationality
							sChildNationalityQuery = "SELECT Nationality FROM LOS_CAMP_CHILD_Nationality (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildNationalityResult = objIForm.getDataFromDB(sChildNationalityQuery);
							if (sChildNationalityResult != null && !sChildNationalityResult.isEmpty()) {
								for (int j = 0; j < sChildNationalityResult.size(); j++) {
									Nationality.add(sChildNationalityResult.get(j).get(0));
								}
							}

							// OccupationSector
							sChildOccupationSectorQuery = "SELECT OccupationSector FROM LOS_CAMP_CHILD_OccupationSector (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildOccupationSectorResult = objIForm.getDataFromDB(sChildOccupationSectorQuery);
							if (sChildOccupationSectorResult != null && !sChildOccupationSectorResult.isEmpty()) {
								for (int j = 0; j < sChildOccupationSectorResult.size(); j++) {
									OccupationSector.add(sChildOccupationSectorResult.get(j).get(0));
								}
							}

							// Province
							sChildProvinceQuery = "SELECT Province FROM LOS_CAMP_CHILD_Province (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildProvinceResult = objIForm.getDataFromDB(sChildProvinceQuery);
							if (sChildProvinceResult != null && !sChildProvinceResult.isEmpty()) {
								for (int j = 0; j < sChildProvinceResult.size(); j++) {
									Province.add(sChildProvinceResult.get(j).get(0));
								}
							}

							// VehicleType
							sChildVehicleTypeQuery = "SELECT VehicleType FROM LOS_CAMP_CHILD_VehicleType (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildVehicleTypeResult = objIForm.getDataFromDB(sChildVehicleTypeQuery);
							if (sChildVehicleTypeResult != null && !sChildVehicleTypeResult.isEmpty()) {
								for (int j = 0; j < sChildVehicleTypeResult.size(); j++) {
									VehicleType.add(sChildVehicleTypeResult.get(j).get(0));
								}
							}

							// VINNumber
							sChildVINNumberQuery = "SELECT VINNumber FROM LOS_CAMP_CHILD_VINNumber (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildVINNumberResult = objIForm.getDataFromDB(sChildVINNumberQuery);
							if (sChildVINNumberResult != null && !sChildVINNumberResult.isEmpty()) {
								for (int j = 0; j < sChildVINNumberResult.size(); j++) {
									VINNumber.add(sChildVINNumberResult.get(j).get(0));
								}
							}

							// Wholesalers
							sChildWholesalersQuery = "SELECT Wholesalers FROM LOS_CAMP_CHILD_Wholesalers (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildWholesalersResult = objIForm.getDataFromDB(sChildWholesalersQuery);
							if (sChildWholesalersResult != null && !sChildWholesalersResult.isEmpty()) {
								for (int j = 0; j < sChildWholesalersResult.size(); j++) {
									Wholesalers.add(sChildWholesalersResult.get(j).get(0));
								}
							}
							JSONArray PRParameters = new JSONArray();
							sChildPRParamsQuery = "SELECT SupportType, AdminFeesVATWaiver, AdminFeesDiscount, MonthlyInstalmentVATWaiver, RetailPurchaseDiscount, Cashback, MaximumAdminFeeSupport, MaximumInstalmentSupport, SupportGivenBy, Installments FROM LOS_CAMP_CMPLX_PriceRebateParameters (NOLOCK) WHERE ChildMapper='"
									+ CampaignResult.get(i).get(19).trim() + "'";
							sChildPRParamsResult = objIForm.getDataFromDB(sChildPRParamsQuery);
							if (sChildPRParamsResult != null && !sChildPRParamsResult.isEmpty()) {
								for (int j = 0; j < sChildPRParamsResult.size(); j++) {
									JSONObject PRParams = new JSONObject();
									PRParams.put("Support Amount to be given as", sChildPRParamsResult.get(j).get(0));
									PRParams.put("Rebate", sChildPRParamsResult.get(j).get(4));
									PRParams.put("Admin Fees with VAT Waiver", sChildPRParamsResult.get(j).get(1));
									PRParams.put("Admin Fees Support", sChildPRParamsResult.get(j).get(2));
									PRParams.put("Monthly Instalment with VAT Waiver",
											sChildPRParamsResult.get(j).get(3));
									PRParams.put("Free Monthly Instalments", sChildPRParamsResult.get(j).get(9));
									PRParams.put("Cashback", sChildPRParamsResult.get(j).get(5));
									PRParams.put("Support Given by", sChildPRParamsResult.get(j).get(8));
									PRParams.put("Max Admin Fee support amount", sChildPRParamsResult.get(j).get(6));
									PRParams.put("Max Instalment support amount", sChildPRParamsResult.get(j).get(7));
									PRParameters.add(PRParams);
								}
							}
							jobj.put("Q_CMPLX_CampaignParamters_Model", Model);
							jobj.put("Q_CMPLX_CampaignParamters_Colour", Colour);
							jobj.put("Q_CMPLX_CampaignParamters_ModelSuffix", ModelSuffix);
							jobj.put("Q_CMPLX_CampaignParamters_ModelCode", ModelCode);
							jobj.put("Q_CMPLX_CampaignParamters_Wholesalers", Wholesalers);
							jobj.put("Q_CMPLX_CampaignParamters_VehicleType", VehicleType);
							jobj.put("Q_CMPLX_CampaignParamters_CustomerType", CustomerType);
							jobj.put("Q_CMPLX_CampaignParamters_CustomerID", CustomerID);
							jobj.put("Q_CMPLX_CampaignParamters_Province", Province);
							jobj.put("Q_CMPLX_CampaignParamters_Gender", Gender);
							jobj.put("Q_CMPLX_CampaignParamters_City", City);
							jobj.put("Q_CMPLX_CampaignParamters_OccupationSector", OccupationSector);
							jobj.put("Q_CMPLX_CampaignParamters_EmployeeType", EmployeeType);
							jobj.put("Q_CMPLX_CampaignParamters_EmployerName", EmployerName);
							jobj.put("Q_CMPLX_CampaignParamters_Nationality", Nationality);
							jobj.put("table3_table27", PRParameters);
						}
						CampaignArr.add(jobj);
					}
				}
				objIForm.setValue("ProductSubType", ProductSubType);
				NGLog.consoleLog("CampaignGrid Data : " + CampaignArr);
				objIForm.addDataToGrid("CampaignGrid", CampaignArr);
				sReturnValue = "S";
			} else {
				sReturnValue = "F";
				return sReturnValue;
			}
		} catch (Exception e) {
			sReturnValue = "F";
			NGLog.errorLog("Error in onChangeCopyCampCode");
			return sReturnValue;
		}
		return sReturnValue;
	}

	public String onChangeCampaignName(IFormReference objIForm, String CampaignName) {
		NGLog.consoleLog("Inside onChangeCampaignName in NGCommon");
		String sReturnValue = "F";
		try {
			String sWIdataQuery = "SELECT CampaignName FROM LOS_CAMPAIGN_EXT (NOLOCK) WHERE CampaignName IS NOT NULL AND CampaignName= '"
					+ CampaignName.trim() + "' AND ProductType='" + objIForm.getValue("ProductType").toString()
					+ "' ORDER BY ApplicationNo DESC";
			List<List<String>> sWIdataResult = null;
			sWIdataResult = objIForm.getDataFromDB(sWIdataQuery);
			if (sWIdataResult != null && !sWIdataResult.isEmpty()) {
				sReturnValue = "F";
//				return sReturnValue;
			} else {
				sReturnValue = "S";
//				return sReturnValue;
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeCampaignName : " + e);
//			return sReturnValue;
		}
		return sReturnValue;
	}

	public String onChangePromoCode(IFormReference objIForm, String PromoCode) {
		NGLog.consoleLog("Inside onChangePromoCode in NGCommon");
		String sReturnValue = "F";
		try {
			String sWIdataQuery = "SELECT Promocode FROM LOS_CAMPAIGN_EXT (NOLOCK) WHERE promocode IS NOT NULL AND promocode= '"
					+ PromoCode.trim() + "'AND ProductType='" + objIForm.getValue("ProductType").toString()
					+ "'  ORDER BY ApplicationNo DESC";
			List<List<String>> sWIdataResult = null;
			sWIdataResult = objIForm.getDataFromDB(sWIdataQuery);
			if (sWIdataResult != null && !sWIdataResult.isEmpty()) {
				sReturnValue = "F";
			} else {
				sReturnValue = "S";
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangePromoCode : " + e);
		}
		return sReturnValue;
	}

	public String populateDocumentMaster(IFormReference objIForm, String selectedLocale) {
		NGLog.consoleLog("Inside populateDocumentMaster in NGCommon");
		String sReturnValue = "";
		String selectedProduct = objIForm.getValue("ProductType").toString();
		try {
			String sDocumentQuery = "SELECT DocumentName,Mandatory,DocType,DocumentId,EntityType FROM LOS_CAMP_M_DOCUMENT (NOLOCK) WHERE ApplicableFor LIKE '%"
					+ selectedProduct + "%' AND IsActive='Y' ORDER BY Sno";
			List<List<String>> DocumentResult = null;
			DocumentResult = objIForm.getDataFromDB(sDocumentQuery);
			JSONArray JArr = new JSONArray();
			if (DocumentResult != null && !DocumentResult.isEmpty()) {
				for (int i = 0; i < DocumentResult.size(); i++) {
					JSONObject jobj = new JSONObject();
					jobj.put("Document Name", DocumentResult.get(i).get(0));
					jobj.put("isMandatory", DocumentResult.get(i).get(1));
					jobj.put("Document Type", DocumentResult.get(i).get(2));
					jobj.put("DocumentCode", DocumentResult.get(i).get(3));
					jobj.put("ApplicantType", DocumentResult.get(i).get(4));
					JArr.add(jobj);
				}
			}
			objIForm.addDataToGrid("inwardDocument_Grid", JArr);
			sReturnValue = "S~Document Master Fetched Successfully";
		} catch (Exception e) {
			sReturnValue = "F~Error in Fetching Document Master";
			NGLog.errorLog("Error in populateDocumentMaster");
		}
		return sReturnValue;
	}

	public String onChangeProductType(IFormReference objIForm, String selectedLocale) {
		NGLog.consoleLog("Inside onChangeProductType in NGCommon");
		String selectedsubProduct = objIForm.getValue("ProductSubType").toString();
		String selectedProduct = objIForm.getValue("ProductType").toString();
		String selectedCampaignType = objIForm.getValue("CampaignType").toString();
//		String sSubProductQuery = "SELECT  Product,Code FROM LOS_M_ALJSubproduct (NOLOCK) WHERE  Language ='"
//				+ selectedLocale + "' AND ProductCategory='" + selectedProduct + "'";
		String sSubProductQuery = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN PRODUCT_DESC_L WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN PRODUCT_DESC_L ELSE PRODUCT_DESC END, PRODUCT_ID FROM LOS_M_LMS_PRODUCT with (NOLOCK) WHERE REC_STATUS='A' AND PRODUCT_CATEGORY = '"
				+ selectedProduct + "'ORDER BY PRODUCT_DESC";
		List<List<String>> sSubProductResult = null;

		String sCampaignTypeQuery = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN CampaignType_Ar WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN CampaignType_Ar ELSE CampaignType_Eng END, Code FROM LOS_CAMP_M_CampaignTypes (NOLOCK) WHERE ApplicableFor LIKE '%"
				+ selectedProduct + "%' AND isActive = 'Y' ORDER BY CampaignType_Eng";
		List<List<String>> sCampaignTypeResult = null;

		String sChannelQuery = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN DescriptionAr WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN DescriptionAr ELSE Description END, Code FROM LOS_CAMP_M_SourceChannel (NOLOCK) WHERE ApplicableFor LIKE '%"
				+ selectedProduct + "%' AND isActive = 'Y' ORDER BY Description";
		List<List<String>> sChannelResult = null;
		NGLog.consoleLog("sSubProductQuery : " + sSubProductQuery);
		NGLog.consoleLog("sCampaignTypeQuery : " + sCampaignTypeQuery);
		NGLog.consoleLog("sChannelQuery : " + sChannelQuery);
		try {
			sSubProductResult = objIForm.getDataFromDB(sSubProductQuery);
			sCampaignTypeResult = objIForm.getDataFromDB(sCampaignTypeQuery);
			sChannelResult = objIForm.getDataFromDB(sChannelQuery);
			NGLog.consoleLog("sSubProductResult : " + sSubProductResult);
			NGLog.consoleLog("sCampaignTypeResult : " + sCampaignTypeResult);
			NGLog.consoleLog("sChannelResult : " + sChannelResult);
			if (sSubProductResult != null && !sSubProductResult.isEmpty()) {
				objIForm.clearCombo("ProductSubType");
				NGLog.consoleLog(
						"ProductSubType after clear combo : " + objIForm.getValue("ProductSubType").toString());
				for (int k = 0; k < sSubProductResult.size(); k++) {
					objIForm.addItemInCombo("ProductSubType", sSubProductResult.get(k).get(0),
							sSubProductResult.get(k).get(1));
				}
			}
			if (sCampaignTypeResult != null && !sCampaignTypeResult.isEmpty()) {
				objIForm.clearCombo("CampaignType");
				NGLog.consoleLog("CampaignType after clear combo : " + objIForm.getValue("CampaignType").toString());
				for (int k = 0; k < sCampaignTypeResult.size(); k++) {
					objIForm.addItemInCombo("CampaignType", sCampaignTypeResult.get(k).get(0),
							sCampaignTypeResult.get(k).get(1));
				}
			}
			if (sChannelResult != null && !sChannelResult.isEmpty()) {
				objIForm.clearCombo("Channel");
				NGLog.consoleLog("Channel after clear combo : " + objIForm.getValue("Channel").toString());
				for (int k = 0; k < sChannelResult.size(); k++) {
					objIForm.addItemInCombo("Channel", sChannelResult.get(k).get(0), sChannelResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeProductType");
		}
		NGLog.consoleLog("selectedSubproductType Value : " + selectedsubProduct);
//		objIForm.setValue("Variant", selectedVariant);
		return "";
	}

	public String onChangeProvince(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeProvince in NGCommon");
		String Province = value.split("~")[0];
		String CalledFrom = value.split("~")[1];
		String selectedLocale = value.split("~")[2];
		String ProvinceID = "";
		String CityID = "";
		if ("Grid".equalsIgnoreCase(CalledFrom)) {
			ProvinceID = "Grid_Province";
			CityID = "Grid_City";
		} else {
			ProvinceID = "Province";
			CityID = "City";
		}
		String selectedCity = objIForm.getValue(CityID).toString();
		String sSubCityQuery = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN LocalityName_Arb WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN LocalityName_Arb ELSE LocalityName_Eng END, CityCode FROM los_m_city WITH(NOLOCK) WHERE IsActive = '1' AND CityCode != 'NULL' AND ProvinceId IN ("
				+ Province + ") order by LocalityName_Eng";
		List<List<String>> sSubCityResult = null;
		try {
			sSubCityResult = objIForm.getDataFromDB(sSubCityQuery);
			NGLog.consoleLog("sSubCity Query  : " + sSubCityQuery);
			NGLog.consoleLog("sSubCityResult : " + sSubCityResult);
			if (sSubCityResult != null && !sSubCityResult.isEmpty()) {
				objIForm.clearCombo(CityID);
				NGLog.consoleLog("City after clear combo : " + objIForm.getValue(CityID).toString());
				for (int k = 0; k < sSubCityResult.size(); k++) {
					objIForm.addItemInCombo(CityID, sSubCityResult.get(k).get(0), sSubCityResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeProductType");
		}
		NGLog.consoleLog("selectedCity Value : " + selectedCity);
//		objIForm.setValue("Variant", selectedVariant);
		return "";
	}

	public String onChangeWholesaler(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeWholesaler in NGCommon");
		String Wholesalers = value.split("~")[0];
		String selectedLocale = value.split("~")[1];
		String selectedBranch = objIForm.getValue("WholesalerBranch").toString();
		String sSubBranchQuery = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN WholesalerBranchName_Ar WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN WholesalerBranchName_Ar ELSE WholesalerBranchName_Eng END, WholesalerBranch_Code FROM LOS_CAMP_M_WholesalerBranch WITH(NOLOCK) WHERE IsActive = '1' AND Wholesaler_Code IN ('"
				+ Wholesalers.replaceAll(",", "','") + "')";
		List<List<String>> sSubBranchResult = null;
		try {
			sSubBranchResult = objIForm.getDataFromDB(sSubBranchQuery);
			NGLog.consoleLog("sSubBranchQuery Query  : " + sSubBranchQuery);
			NGLog.consoleLog("sSubBranchResult : " + sSubBranchResult);
			if (sSubBranchResult != null && !sSubBranchResult.isEmpty()) {
				objIForm.clearCombo("WholesalerBranch");
				NGLog.consoleLog("City after clear combo : " + objIForm.getValue("City").toString());
				for (int k = 0; k < sSubBranchResult.size(); k++) {
					objIForm.addItemInCombo("WholesalerBranch", sSubBranchResult.get(k).get(0),
							sSubBranchResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeWholesaler");
		}
		NGLog.consoleLog("selectedBranch Value : " + selectedBranch);
//		objIForm.setValue("Variant", selectedVariant);
		return "";
	}

	public void SetValues(IFormReference objIForm, JSONObject Jobj) {
		NGLog.consoleLog("Inside SetValues in NGCommon");
		NGLog.consoleLog("JSONObject : " + Jobj);
		Jobj.forEach((key, value) -> {
			objIForm.setValue(key.toString(), value.toString());
		});
	}

	private String getDBFormattedValueString(String field) {
		StringBuilder Value = new StringBuilder();
		Value.append("'");
		Value.append(field);
		Value.append("'");
		Value.append(",");
		return Value.toString();
	}

	private String getDbformattedValueInt(int field) {
		StringBuilder Value = new StringBuilder();
		Value.append(field);
		Value.append(",");
		return Value.toString();
	}

	private String getDbformattedValuedouble(double field) {
		StringBuilder Value = new StringBuilder();
		Value.append(field);
		Value.append(",");
		return Value.toString();
	}

	public String InsertExcelDatadeserialized(IFormReference objIForm) {
		NGLog.consoleLog("Inside InsertExcelDatadeserialized ----------------------------------");
		String returnVal = "S";
		String InsertColumns = "Brand, Product, Full_Variant, Model_Year, Tenure, Grace_Period, Description, Trim, Retail_Price, Purchase_Discount, Formula_Price, Finance_Charge, Insurance, Total_Amount, Down_Payment, Finance_Amount, MI, RV, RV_Percentage, Admin_Fee, Finance_Percentage, Insur, Total_term_cost_ratio, Admin_Fee_Discount_Amount, Free_first_installment, Free_second_installment, Free_third_installment, Total, Total_Rebate_Discount, Difference, Rebate_Percentage, Rebate_Amount, Free_fourth_Installment, Free_fifth_Installment, Free_sixth_Installment, Cashback";
		try {
			Upload up1 = new Upload();
			ObjectMapper objectMapper = new ObjectMapper();

			List<VehicleExceldata> vehicleInfo = up1.getList();
			NGLog.consoleLog("Vehicle Info data : " + vehicleInfo);
			String deleteQuery = "delete from Camp_JointCampaignData where PID='"
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "'";
			objIForm.saveDataInDB(deleteQuery);
			StringBuffer values = new StringBuffer();
			vehicleInfo.forEach(a -> {
				StringBuilder insertedValue = new StringBuilder();
				insertedValue.append(getDBFormattedValueString(a.getBrand()));
				insertedValue.append(getDBFormattedValueString(a.getProduct()));
				insertedValue.append(getDBFormattedValueString(a.getFullVariant()));
				insertedValue.append(getDBFormattedValueString(a.getModelYear()));
				insertedValue.append(getDbformattedValueInt(a.getTenure()));
				insertedValue.append(getDbformattedValueInt(a.getGracePeriod()));
				insertedValue.append(getDBFormattedValueString(a.getDescription()));
				insertedValue.append(getDBFormattedValueString(a.getTrim()));
				insertedValue.append(getDbformattedValuedouble(a.getRetailPrice()));
				insertedValue.append(getDbformattedValuedouble(a.getPurchaseDiscount()));
				insertedValue.append(getDbformattedValuedouble(a.getFormulaPrice()));
				insertedValue.append(getDbformattedValuedouble(a.getFinanceCharge()));
				insertedValue.append(getDbformattedValuedouble(a.getInsurance()));
				insertedValue.append(getDbformattedValuedouble(a.getTotalAmount()));
				insertedValue.append(getDbformattedValuedouble(a.getDownPayment()));
				insertedValue.append(getDbformattedValuedouble(a.getFinanceAmount()));
				insertedValue.append(getDbformattedValuedouble(a.getMi()));
				insertedValue.append(getDbformattedValuedouble(a.getRv()));
				insertedValue.append(getDbformattedValuedouble(a.getRvPercentage()));
				insertedValue.append(getDbformattedValuedouble(a.getAdminFee()));
				insertedValue.append(getDbformattedValuedouble(a.getFinancePercentage()));
				insertedValue.append(getDbformattedValuedouble(a.getInsur()));
				insertedValue.append(getDbformattedValuedouble(a.getTotalTermCostRatio()));
				insertedValue.append(getDbformattedValuedouble(a.getAdminFeeDiscountAmount()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeFirstInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeSecondInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeThirdInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getTotal()));
				insertedValue.append(getDbformattedValuedouble(a.getTotalRebateDiscount()));
				insertedValue.append(getDbformattedValuedouble(a.getDifference()));
				insertedValue.append(getDbformattedValuedouble(a.getRebatePercentage()));
				insertedValue.append(getDbformattedValuedouble(a.getRebateAmount()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeFourthInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeFifthInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getFreeSixthInstallment()));
				insertedValue.append(getDbformattedValuedouble(a.getCashback()));
				insertedValue
						.append(getDBFormattedValueString(objIForm.getObjGeneralData().getM_strProcessInstanceId()));
				insertedValue.append(getDBFormattedValueString(objIForm.getValue("CampaignCode").toString()));
				insertedValue = insertedValue.replace(insertedValue.lastIndexOf(","),
						insertedValue.lastIndexOf(",") + 1, "");
				values.append("(");
				values.append(insertedValue);
				values.append(")");
				values.append(",");
			});
			NGLog.consoleLog("values data : " + values);
			String query = "Insert into Camp_JointCampaignData (" + InsertColumns + ",PID,CampaignCode" + ") Values "
					+ values.replace(values.lastIndexOf(","), values.lastIndexOf(",") + 1, "") + ";";
			NGLog.consoleLog("Query printed : " + query);
			int result = objIForm.saveDataInDB(query);
			NGLog.consoleLog("Is query executed result : " + result);
			if (result == -1) {
				returnVal = "F";
				deleteQuery = "delete from Camp_JointCampaignData where PID='"
						+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "'";
				objIForm.saveDataInDB(deleteQuery);
			} else {
				NGLog.consoleLog("VehicleGrid Data : " + objIForm.getDataFromGrid("VehicleGrid"));
			}
		} catch (Exception e) {
			returnVal = "F";
			NGLog.errorLog("Error in InsertExcelDatadeserialized : ", e);
		}
		return returnVal;
	}

	public static String getCommaSeparatedString(JSONArray jsonArray) {
		// Start by getting the first element (skip handling last element)
		StringBuilder result = new StringBuilder();

		// Loop over all elements in the JSONArray
		for (int i = 0; i < jsonArray.size(); i++) {
			// Directly append the element to the StringBuilder
			result.append(jsonArray.get(i));

			// Append a comma after every element except the last one
			if (i < jsonArray.size() - 1) {
				result.append(", ");
			}
		}

		return result.toString();
	}

	public String writeExcel(IFormReference objIForm, String SelectedRows) {
		NGLog.consoleLog("Inside writeExcel Method");
		NGLog.consoleLog("SelectedRows Received from Client : " + SelectedRows);
		try {
			int[] Rows = Arrays.stream(SelectedRows.split(",")).mapToInt(Integer::parseInt).toArray();
			// Check if the input file exists
			String inputFilePath = System.getProperty("user.dir") + File.separator + "CAMPAIGN_CONFIG" + File.separator
					+ "Template.xlsx";
			String outputFilePath = System.getProperty("user.dir") + File.separator + "CAMPAIGN_CONFIG" + File.separator
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "file.xlsx";
			NGLog.consoleLog("inputFilePath: " + inputFilePath);

			File inputFile = new File(inputFilePath);
			if (!inputFile.exists()) {
				NGLog.consoleLog("Input file does not exist at the provided path.");
				return "F";
			}

			// Open the input file and create a workbook
			NGLog.consoleLog("Opening input file...");
			try (FileInputStream fileInputStream = new FileInputStream(inputFile);
					Workbook workbook = new XSSFWorkbook(fileInputStream)) {
				NGLog.consoleLog("Inside method file: File opened successfully");
				// Access the first sheet
				Sheet sheet = workbook.getSheetAt(0);
				NGLog.consoleLog("Accessing the first sheet");

				// Get the last row index and create a new row at the end of the sheet
				int lastRowNum = sheet.getPhysicalNumberOfRows();
				JSONArray CampaignData = objIForm.getDataFromGrid("CampaignGrid");
				NGLog.consoleLog("CampaignData : " + CampaignData);
				CellStyle percentageStyle = workbook.createCellStyle();
				DataFormat format = workbook.createDataFormat();
				percentageStyle.setDataFormat(format.getFormat("0.00%"));

				String customFormat = "_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)";

				// Create a cell style and apply the custom number format
				CellStyle customStyle = workbook.createCellStyle();
				customStyle.setDataFormat(format.getFormat(customFormat));

				for (int i = 0; i < Rows.length; i++) {
					JSONObject CampaignRow = (JSONObject) CampaignData.get(Rows[i]);
					NGLog.consoleLog("CampaignRow : " + CampaignRow);
					int formularow = lastRowNum + 1;
					String Brand = CampaignRow.get("Q_CMPLX_CampaignParamters_VehicleBrand").toString();
					String Variant = CampaignRow.get("Q_CMPLX_CampaignParamters_Variant").toString();
					JSONArray ModelC = (JSONArray) CampaignRow.get("Q_CMPLX_CampaignParamters_ModelCode");
					String ModelCode = ModelC.get(0).toString();
//					Q_CMPLX_CampaignParamters_Model

					JSONArray Models = (JSONArray) CampaignRow.get("Q_CMPLX_CampaignParamters_Model");
					String Model = Models.get(0).toString();
					double JSAPDiscountAmount = getJSAPDiscounts(objIForm, Model);
					JSONArray ModelS = (JSONArray) CampaignRow.get("Q_CMPLX_CampaignParamters_ModelSuffix");

					String ModelSuffix = ModelS.get(0).toString();
					String ModelYear = CampaignRow.get("Q_CMPLX_CampaignParamters_ModelYear").toString();
					String ModelName = CampaignRow.get("Q_CMPLX_CampaignParamters_ModelName").toString();

					JSONArray Colours = (JSONArray) CampaignRow.get("Q_CMPLX_CampaignParamters_Colour");

					NGLog.consoleLog("Retail Price Before Discount  : "
							+ CampaignRow.get("Q_CMPLX_CampaignParamters_RetailPrice").toString());
					String Colour = getColours(objIForm, getCommaSeparatedString(Colours));
					String ContractPeriod = CampaignRow.get("Q_CMPLX_CampaignParamters_Tenure").toString();
					String GracePeriod = CampaignRow.get("Q_CMPLX_CampaignParamters_GracePeriod").toString();
					double RetailPrice = (getOrDefaultdob
							.apply(CampaignRow.get("Q_CMPLX_CampaignParamters_RetailPrice").toString(), 0.0)
							- JSAPDiscountAmount);
					NGLog.consoleLog("Retail Price After Discount  : " + RetailPrice);
					String FinancePercentage = CampaignRow.get("Q_CMPLX_CampaignParamters_FinancePercentage")
							.toString();
					String InsurancePercentage = CampaignRow.get("Q_CMPLX_CampaignParamters_InsurancePercentage")
							.toString();
					String DownPaymentPercentage = CampaignRow.get("Q_CMPLX_CampaignParamters_DownPaymentPercentage")
							.toString();
					String RVPercentage = CampaignRow.get("Q_CMPLX_CampaignParamters_RVPercentage").toString();
					JSONArray ChildArray = (JSONArray) CampaignRow.get("table3_table27");
					JSONObject ChildGrid = (JSONObject) ChildArray.get(0);
					int MonthlyInstalments = "".equalsIgnoreCase(ChildGrid.get("Free Monthly Instalments").toString())
							? 0
							: ChildGrid.get("Free Monthly Instalments").toString().split(",").length;

					String MaximumInstalmentsupport = ChildGrid.get("Max Instalment support amount").toString();

					double Cashback = getOrDefaultdob.apply(ChildGrid.get("Cashback").toString(), 0.0);
					double RetailPurchaseDiscount = getOrDefaultdob.apply(ChildGrid.get("Rebate").toString(), 0.0);
					double AdminFeesupport = getOrDefaultdob.apply(ChildGrid.get("Admin Fees Support").toString(), 0.0);
					String SupportType = ChildGrid.get("Support Amount to be given as").toString();
					double AdminFeePercentage = 0.0;
					if ("Percentage".equalsIgnoreCase(SupportType)) {
						AdminFeePercentage = AdminFeesupport;
						Cashback = (RetailPrice * Cashback) / 100;
						RetailPurchaseDiscount = ((RetailPrice - Cashback) * RetailPurchaseDiscount) / 100;
					} else {
						double Amount1 = RetailPrice - Cashback - RetailPurchaseDiscount;
						NGLog.consoleLog("Amount1 :" + Amount1);
						double Amount2 = (Amount1 * getOrDefaultdob.apply(DownPaymentPercentage, 0.0) / 100);
						NGLog.consoleLog("Amount2 :" + Amount2);
						double Amount3 = (Amount1 - Amount2) / 100;
						NGLog.consoleLog("Amount3 :" + Amount3);
						AdminFeePercentage = (AdminFeesupport / Amount3) * 100;
						NGLog.consoleLog("AdminFeePercentage :" + AdminFeePercentage);
					}

					// Calculations

//					Row row = sheet.createRow(StartingRow);
					Row row = sheet.getRow(lastRowNum);
					if (row == null) {
						NGLog.consoleLog("Row Created :" + lastRowNum);
						row = sheet.createRow(lastRowNum);
					}
					// Fill the new row with data
					row.createCell(0).setCellValue(Brand);
					row.createCell(1).setCellValue(Variant);
					row.createCell(2).setCellValue(ModelCode);
					row.createCell(3).setCellValue(ModelSuffix);
					row.createCell(4).setCellValue(ModelYear);
					row.createCell(5).setCellValue(ModelName);
					row.createCell(6).setCellValue(Colour);
					row.createCell(7).setCellValue(getOrDefaultInt.apply(ContractPeriod, 0));
					row.createCell(8).setCellFormula("H" + formularow + "-J" + formularow);
					row.createCell(9).setCellValue(getOrDefaultInt.apply(GracePeriod, 0));
					Cell cell10 = row.createCell(10);
					cell10.setCellValue(RetailPrice);
					cell10.setCellStyle(customStyle);
					Cell cell11 = row.createCell(11);
					cell11.setCellValue(Cashback);
					cell11.setCellStyle(customStyle);
					Cell cell12 = row.createCell(12);
					cell12.setCellValue(RetailPurchaseDiscount);
					cell12.setCellStyle(customStyle);
					row.createCell(13).setCellFormula("K" + formularow + "-L" + formularow + "-M" + formularow);
					row.getCell(13).setCellStyle(customStyle);
					Cell cell14 = row.createCell(14);
					cell14.setCellValue(getOrDefaultdob.apply(FinancePercentage, 0.0) / 100);
					cell14.setCellStyle(percentageStyle);
					Cell cell15 = row.createCell(15);
					cell15.setCellValue(getOrDefaultdob.apply(InsurancePercentage, 0.0) / 100);
					cell15.setCellStyle(percentageStyle);
					Cell cell16 = row.createCell(16);
					cell16.setCellValue(getOrDefaultdob.apply(DownPaymentPercentage, 0.0) / 100);
					cell16.setCellStyle(percentageStyle);
					// N2*Q2
					row.createCell(17).setCellFormula("N" + formularow + "*Q" + formularow);
					row.getCell(17).setCellStyle(customStyle);
					row.createCell(18).setCellFormula(
							"ROUND(((AC" + formularow + "-AD" + formularow + ")/I" + formularow + ")+0.49,0)");
					row.getCell(18).setCellStyle(customStyle);
					// RV Percentage RVPercentage
					Cell cell19 = row.createCell(19);
					cell19.setCellValue(getOrDefaultdob.apply(RVPercentage, 0.0) / 100);
					cell19.setCellStyle(percentageStyle);
					Cell cell20 = row.createCell(20);
					cell20.setCellValue(AdminFeePercentage / 100);
					cell20.setCellStyle(percentageStyle);
					// IF(((N2-R2)*1%)*U2>5000,5000,((N2-R2)*1%)*U2)
					row.createCell(21).setCellFormula("IF(((N" + formularow + "-R" + formularow + ")*1%)*U" + formularow
							+ ">5000,5000,((N" + formularow + "-R" + formularow + ")*1%)*U" + formularow + ")");
					row.getCell(21).setCellStyle(customStyle);
					row.createCell(22).setCellValue(MonthlyInstalments);
					row.createCell(23).setCellValue(getOrDefaultdob.apply(MaximumInstalmentsupport, 0.0));
					row.getCell(23).setCellStyle(customStyle);

					row.createCell(25).setCellFormula("N" + formularow + "-R" + formularow);
					row.getCell(25).setCellStyle(customStyle);
					row.createCell(26).setCellFormula(
							"Z" + formularow + "*O" + formularow + "*((I" + formularow + "+J" + formularow + ")/12)");
					row.getCell(26).setCellStyle(customStyle);
					row.createCell(27).setCellFormula(
							"N" + formularow + "*P" + formularow + "*((I" + formularow + "+J" + formularow + ")/12)");
					row.getCell(27).setCellStyle(customStyle);
					row.createCell(28).setCellFormula(
							"R" + formularow + "+Z" + formularow + "+AA" + formularow + "+AB" + +formularow);
					row.getCell(28).setCellStyle(customStyle);
					row.createCell(29).setCellFormula("N" + formularow + "*T" + formularow);
					row.getCell(29).setCellStyle(customStyle);
					lastRowNum++;
				}
				FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				for (Row r : sheet) {
					for (Cell c : r) {
						if (c.getCellType() == CellType.FORMULA) {
							// Recalculate the formula in the cell
							formulaEvaluator.evaluateFormulaCell(c); // Force evaluation
						}
					}
				}
				// Write the modified workbook to a new file
				NGLog.consoleLog("Writing to output file...");
				try (FileOutputStream fos = new FileOutputStream(new File(outputFilePath))) {
					workbook.write(fos);
					NGLog.consoleLog("File written successfully to: " + outputFilePath);
					return "S";
				}
			} catch (IOException e) {
				NGLog.errorLog("Error occurred while processing the Excel file: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			NGLog.errorLog("Unexpected error: " + e.getMessage());
			e.printStackTrace();
		}
		return "F";
	}

	public double getJSAPDiscounts(IFormReference objIForm, String ModelID) {
		NGLog.consoleLog("Inside getJSAPDiscounts ...");
		String jsapquery = "SELECT Amount FROM LOS_M_SalesDiscount WHERE IsActive=1 and tModelID='" + ModelID + "'";
		List<List<String>> JSAPResult = null;
		double DisountAmount = 0.0;
		try {
			JSAPResult = objIForm.getDataFromDB(jsapquery);
			NGLog.consoleLog("JSAPResult : " + JSAPResult);
			if (JSAPResult != null && !JSAPResult.isEmpty()) {
				for (int i = 0; i < JSAPResult.size(); i++) {
					DisountAmount += getOrDefaultdob.apply(JSAPResult.get(i).get(0), 0.0);
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Unexpected error in getJSAPDiscounts: " + e);
		}
		NGLog.consoleLog("DisountAmount ..." + DisountAmount);
		return DisountAmount;
	}

	public String getColours(IFormReference objIForm, String ColourCodes) {
		NGLog.consoleLog("Inside getColours ...");
		StringBuilder Colours = new StringBuilder();
		try {
			String[] parts = ColourCodes.split(",");
			StringBuilder formattedString = new StringBuilder();
			for (int i = 0; i < parts.length; i++) {
				formattedString.append("'").append(parts[i].trim()).append("'");
				// Add a comma if it's not the last element
				if (i < parts.length - 1) {
					formattedString.append(",");
				}
			}
			String Colourquery = "SELECT DISTINCT BodyColour FROM LOS_M_ModelColorMap (NOLOCK) WHERE BodyColorCode IN ("
					+ formattedString + ")";
			NGLog.consoleLog("Colourquery: " + Colourquery);
			List<List<String>> ColourResult = null;
			ColourResult = objIForm.getDataFromDB(Colourquery);
			NGLog.consoleLog("ColourResult : " + ColourResult);
			if (ColourResult != null && !ColourResult.isEmpty()) {
				for (int i = 0; i < ColourResult.size(); i++) {
					Colours.append(ColourResult.get(i).get(0));
					if (i < ColourResult.size() - 1) {
						Colours.append(", ");
					}
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Unexpected error in getColours: " + e);
		}

		return Colours.toString();
	}

	private static CellStyle createLockedStyle(Workbook workbook) {
		CellStyle lockedStyle = workbook.createCellStyle();
		lockedStyle.setLocked(true);
		return lockedStyle;

	}

	private static CellStyle createUnlockedStyle(Workbook workbook) {
		CellStyle unlockStyle = workbook.createCellStyle();
		unlockStyle.setLocked(false);
		return unlockStyle;
	}

	public String onChangePriority1(IFormReference objIForm, String Value) {
		NGLog.consoleLog("Inside onChangePriority1 in NGCommon");
		String selectedPriority2 = objIForm.getValue("AppropriationPriority2").toString();
		String selectedLocale = Value.split("~")[0];
		String calledFrom = Value.split("~")[1];

		String sPriority2Query = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN DescriptionAr WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN DescriptionAr ELSE Description END,Code FROM LOS_CAMP_M_AppropriationPriority WITH(NOLOCK) WHERE IsActive = 'Y' AND Code NOT IN ('"
				+ objIForm.getValue("AppropriationPriority1") + "') AND Code != 100";
		NGLog.consoleLog("sPriority2Query : " + sPriority2Query);
		List<List<String>> sPriority2Result = null;
		try {
			sPriority2Result = objIForm.getDataFromDB(sPriority2Query);
			NGLog.consoleLog("sPriority2Result : " + sPriority2Result);
			if (sPriority2Result != null && !sPriority2Result.isEmpty()) {
				objIForm.clearCombo("AppropriationPriority2");
				NGLog.consoleLog("AppropriationPriority2 after clear combo : "
						+ objIForm.getValue("AppropriationPriority2").toString());
				for (int k = 0; k < sPriority2Result.size(); k++) {
					objIForm.addItemInCombo("AppropriationPriority2", sPriority2Result.get(k).get(0),
							sPriority2Result.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangePriority1");
		}
		NGLog.consoleLog("selectedPriority2 Value : " + selectedPriority2);
		if ("Load".equalsIgnoreCase(calledFrom)) {
			objIForm.setValue("AppropriationPriority2", selectedPriority2);
		}
		return "";
	}

	public String onChangePriority2(IFormReference objIForm, String Value) {
		NGLog.consoleLog("Inside onChangePriority2 in NGCommon");
		String selectedPriority3 = objIForm.getValue("AppropriationPriority3").toString();
		String selectedLocale = Value.split("~")[0];
		String CalledFrom = Value.split("~")[0];
		String sPriority3Query = "SELECT CASE WHEN '" + selectedLocale + "' ='_ar' THEN DescriptionAr WHEN '"
				+ selectedLocale
				+ "' ='_ar_SA'  THEN DescriptionAr ELSE Description END,Code FROM LOS_CAMP_M_AppropriationPriority WITH(NOLOCK) WHERE IsActive = 'Y' AND Code NOT IN ('"
				+ objIForm.getValue("AppropriationPriority2") + "','" + objIForm.getValue("AppropriationPriority1")
				+ "') AND Code !=100";
		NGLog.consoleLog("sPriority3Query : " + sPriority3Query);
		List<List<String>> sPriority3Result = null;
		try {
			sPriority3Result = objIForm.getDataFromDB(sPriority3Query);
			NGLog.consoleLog("sPriority2Result : " + sPriority3Result);
			if (sPriority3Result != null && !sPriority3Result.isEmpty()) {
				objIForm.clearCombo("AppropriationPriority3");
				NGLog.consoleLog("AppropriationPriority3 after clear combo : "
						+ objIForm.getValue("AppropriationPriority2").toString());
				for (int k = 0; k < sPriority3Result.size(); k++) {
					objIForm.addItemInCombo("AppropriationPriority3", sPriority3Result.get(k).get(0),
							sPriority3Result.get(k).get(1));
					if (sPriority3Result.size() == 1) {
						objIForm.setValue("AppropriationPriority3", sPriority3Result.get(k).get(1));
					}
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangePriority1");
		}
		NGLog.consoleLog("selectedPriority2 Value : " + selectedPriority3);
		if ("Load".equalsIgnoreCase(CalledFrom)) {
			objIForm.setValue("AppropriationPriority3", selectedPriority3);
		}
		return "";
	}

	// onAddDeleteVehicleGrid Newly added after stable for model Duplicate check
	public String onAddDeleteVehicleGrid(IFormReference objIForm, String Value) {
		String ReturnVal = "S~Success";
		NGLog.consoleLog("Inside onAddDeleteVehicleGrid");
		Set<Integer> Models = new HashSet<>();
		Set<Integer> SelectedModels = new HashSet<>();

		try {
			String flag = Value.split("~")[2];
			NGLog.consoleLog("After flag : ");
			int SelectedRow;
			if ("M".equalsIgnoreCase(flag)) {
				SelectedRow = getOrDefaultInt.apply(Value.split("~")[0], null);
			} else {
				SelectedRow = -10;
			}
			String ChildMapper = "";
			NGLog.consoleLog("SelectedRow : " + SelectedRow);
			NGLog.consoleLog("Selected Models : " + Value.split("~")[1]);
			String[] SelectedModel = Value.split("~")[1].split(",");
			for (String string : SelectedModel) {
				SelectedModels.add(getOrDefaultInt.apply(string, 0));
			}
			NGLog.consoleLog("SelectedModels Sets: " + SelectedModels);
			String ChildMapperQuery = "SELECT ChildMapper FROM LOS_CAMP_CMPLX_VehicleDetails (NOLOCK) WHERE PID='"
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "' ORDER BY InsertionOrderId";
//			String modelQuery = "SELECT Model FROM LOS_CAMP_CHILD_Model (NOLOCK) WHERE ChildMapper IN (SELECT ChildMapper FROM LOS_CAMP_CMPLX_VehicleDetails WHERE PID='"
//					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "') ORDER BY InsertionOrderId";
			NGLog.consoleLog("ChildMapperQuery : " + ChildMapperQuery);
			List<List<String>> ChildMapperQueryResult = objIForm.getDataFromDB(ChildMapperQuery);
			if (ChildMapperQueryResult != null && !ChildMapperQueryResult.isEmpty()) {
				for (int i = 0; i < ChildMapperQueryResult.size(); i++) {
					ChildMapper = ChildMapperQueryResult.get(i).get(0);
					if (i != SelectedRow) {
						String modelQuery = "SELECT Model FROM LOS_CAMP_CHILD_Model (NOLOCK) WHERE ChildMapper ='"
								+ ChildMapper + "'";
						NGLog.consoleLog("ModelQuery : " + modelQuery);
						List<List<String>> ModelResult = objIForm.getDataFromDB(modelQuery);
						NGLog.consoleLog("ModelResult : " + ModelResult);
						for (int j = 0; j < ModelResult.size(); j++) {
							Models.add(getOrDefaultInt.apply(ModelResult.get(j).get(0), 0));
						}
					} else {
						NGLog.consoleLog("Same row check skip : " + SelectedRow);
					}
				}
			}
			NGLog.consoleLog("Models Sets: " + Models);
			for (Integer element1 : Models) {
				if (SelectedModels.contains(element1)) {
					NGLog.consoleLog("Common Model found: " + element1);
					ReturnVal = "F~" + element1;
					break;
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onAddDeleteVehicleGrid: " + e);
		}

		return ReturnVal;
	}

	public String getCurrentDateTime() {
		String FormattedDate = "";
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date CurrentDate = new Date();
			FormattedDate = dateformat.format(CurrentDate);
			NGLog.consoleLog("Current date returned : " + FormattedDate);
		} catch (Exception e) {
			NGLog.errorLog("Error in getCurrentDateTime" + e);
		}
		return FormattedDate;
	}

	public String getFinanceRateLimit(IFormReference objIForm) {
		NGLog.consoleLog("Inside getFinanceRateLimit");
		String financeRate = "0";
		try {
			String financeQuery = "SELECT Value FROM LOS_CAMP_M_CONFIGMASTER(NOLOCK) WHERE KeyID='CFFinanceRate'";
			List<List<String>> financeQueryResult = objIForm.getDataFromDB(financeQuery);
			if (financeQueryResult != null && !financeQueryResult.isEmpty()) {
				financeRate = financeQueryResult.get(0).get(0);
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in getFinanceRateLimit" + e);
		}
		return financeRate;
	}

	public String onClickAddGrid(IFormReference objIForm) {
		NGLog.consoleLog("Inside onClickAddGrid");
		JSONArray CampaignArray = new JSONArray();
		String ReturnVal = "F";
		try {
			// Fetched all the form data
			JSONArray VehicleType = checkJSON(objIForm.getValue("VehicleType").toString());
			String DistributorWise = objIForm.getValue("DistributorWise").toString();
			JSONArray Wholesalers = checkJSON(objIForm.getValue("Wholesalers").toString());
			JSONArray Channel = checkJSON(objIForm.getValue("Channel").toString());
			String VehicleBrand = objIForm.getValue("VehicleBrand").toString();
			String VehicleBrandCode = objIForm.getValue("VehicleBrandCode").toString();
			String Variant = objIForm.getValue("Variant").toString();
			String VariantCode = objIForm.getValue("VariantCode").toString();
			String ModelYear = objIForm.getValue("ModelYear").toString();
			JSONArray Model = checkJSON(objIForm.getValue("Model").toString());
			/*
			 * JSONArray ModelSuffix =
			 * checkJSON(objIForm.getValue("ModelSuffix").toString()); JSONArray ModelCode =
			 * checkJSON(objIForm.getValue("ModelCode").toString());
			 */
			JSONArray Color = new JSONArray();
			String DownPayment = objIForm.getValue("DownPayment").toString();
			String Tenure = objIForm.getValue("Tenure").toString();
			String GracePeriod = objIForm.getValue("GracePeriod").toString();
//			String RV = objIForm.getValue("RV").toString();
			String FinancePercentage = objIForm.getValue("FinancePercentage").toString();
			String InsurancePercentage = objIForm.getValue("InsurancePercentage").toString();
			String RVPercentage = objIForm.getValue("RVPercentage").toString();
			JSONArray CustomerType = checkJSON(objIForm.getValue("CustomerType").toString());
			JSONArray CustomerID = checkJSON(objIForm.getValue("CustomerID").toString());
			JSONArray Province = checkJSON(objIForm.getValue("Province").toString());
			JSONArray Gender = checkJSON(objIForm.getValue("Gender").toString());
			JSONArray City = checkJSON(objIForm.getValue("City").toString());
			String AgeRangeMin = objIForm.getValue("AgeRangeMin").toString();
			String AgeRangeMax = objIForm.getValue("AgeRangeMax").toString();
			JSONArray OccupationSector = checkJSON(objIForm.getValue("OccupationSector").toString());
			JSONArray EmployeeType = checkJSON(objIForm.getValue("EmployeeType").toString());
			JSONArray EmployerName = checkJSON(objIForm.getValue("EmployerName").toString());
			String IncomeRangeMin = objIForm.getValue("IncomeRangeMin").toString();
			String IncomeRangeMax = objIForm.getValue("IncomeRangeMax").toString();
			JSONArray Nationality = checkJSON(objIForm.getValue("Nationality").toString());
			String SpecialNeeds = objIForm.getValue("SpecialNeeds").toString();
			String sModelQuery = "";
			// Fetching the Price and Rebate Parameters.......
			JSONArray PRParams = new JSONArray();
			if (objIForm.getDataFromGrid("PRGrid").size() > 0) {
				NGLog.consoleLog("JSON Object Creation for PRGrid ");
				JSONObject PRJson = new JSONObject();
				PRJson.put("Support Amount to be given as", objIForm.getTableCellValue("PRGrid", 0, 0));
				PRJson.put("Rebate", objIForm.getTableCellValue("PRGrid", 0, 1));
				PRJson.put("Admin Fees with VAT Waiver", objIForm.getTableCellValue("PRGrid", 0, 2));
				PRJson.put("Admin Fees Support", objIForm.getTableCellValue("PRGrid", 0, 3));
				PRJson.put("Monthly Instalment with VAT Waiver", objIForm.getTableCellValue("PRGrid", 0, 4));
				PRJson.put("Free Monthly Instalments", objIForm.getTableCellValue("PRGrid", 0, 5));
				PRJson.put("Cashback", objIForm.getTableCellValue("PRGrid", 0, 6));
				PRJson.put("Support Given by", objIForm.getTableCellValue("PRGrid", 0, 7));
				PRJson.put("Max Admin Fee support amount", objIForm.getTableCellValue("PRGrid", 0, 8));
				PRJson.put("Max Instalment support amount", objIForm.getTableCellValue("PRGrid", 0, 9));
				PRParams.add(PRJson);
			}
			NGLog.consoleLog("JSON created for PRGRid : " + PRParams);
			// Creating Row Data
			for (int i = 0; i < Model.size(); i++) {
				JSONObject jobj = new JSONObject();
				jobj.put("Q_CMPLX_CampaignParamters_VehicleType", VehicleType);
				jobj.put("Grid_DistributorWise", DistributorWise);
				jobj.put("Q_CMPLX_CampaignParamters_Wholesalers", Wholesalers);
				jobj.put("Grid_VehicleBrand", VehicleBrand);
				jobj.put("Grid_VehicleBrandCode", VehicleBrandCode);
				jobj.put("Grid_Variant", Variant);
				jobj.put("Grid_VariantCode", VariantCode);
				jobj.put("Grid_ModelYear", ModelYear);
				JSONArray jM = new JSONArray();
				jM.add(Model.get(i));
				jobj.put("Q_CMPLX_CampaignParamters_Model", jM);
				JSONArray ModelSuffix = new JSONArray();
				JSONArray ModelCode = new JSONArray();
				if (!"0".equalsIgnoreCase(Model.get(i).toString())) {
					sModelQuery = "SELECT b.RetailPrice,b.ModelCode,b.ModelCodeSuffix,b.Description,STRING_AGG(a.BodyColorCode, ',') AS BodyColorCodes FROM LOS_M_Model b RIGHT JOIN (SELECT DISTINCT BodyColorCode, tModelID FROM LOS_M_ModelColorMap WHERE tModelID = '"
							+ Model.get(i)
							+ "') a ON a.tModelID = b.tModelID GROUP BY b.RetailPrice, b.ModelCode, b.ModelCodeSuffix, b.Description";
					List<List<String>> sModelResult = null;
					NGLog.consoleLog("sModel Query : " + sModelQuery);
					try {
						sModelResult = objIForm.getDataFromDB(sModelQuery);
						NGLog.consoleLog("sModelQuery : " + sModelQuery);
						NGLog.consoleLog("sModelResult in Add to Grid : " + sModelResult);
						if (sModelResult != null && !sModelResult.isEmpty()) {
							jobj.put("Grid_RetailPrice", sModelResult.get(0).get(0));
							jobj.put("Grid_ActualRetailPrice", sModelResult.get(0).get(0));
							ModelSuffix.add(sModelResult.get(0).get(2));
							ModelCode.add(sModelResult.get(0).get(1));
							jobj.put("Q_CMPLX_CampaignParamters_ModelSuffix", ModelSuffix);
							jobj.put("Q_CMPLX_CampaignParamters_ModelCode", ModelCode);
							jobj.put("Grid_ModelName", sModelResult.get(0).get(3));
							Color = convertToJSONArray(sModelResult.get(0).get(4));
							jobj.put("Q_CMPLX_CampaignParamters_Colour", Color);
						}
					} catch (Exception e) {
						NGLog.errorLog("Error in sModel Query" + e);
					}
				} else {
					ModelSuffix.add("0");
					ModelCode.add("0");
					Color.add("0");
					jobj.put("Q_CMPLX_CampaignParamters_ModelSuffix", ModelSuffix);
					jobj.put("Q_CMPLX_CampaignParamters_ModelCode", ModelCode);
					jobj.put("Q_CMPLX_CampaignParamters_Colour", Color);
					jobj.put("Grid_ModelName", "All");
				}
				/*
				 * jobj.put("Q_CMPLX_CampaignParamters_Model", Model);
				 * jobj.put("Q_CMPLX_CampaignParamters_ModelSuffix", ModelSuffix);
				 * jobj.put("Q_CMPLX_CampaignParamters_ModelCode", ModelCode);
				 * jobj.put("Q_CMPLX_CampaignParamters_Colour", Color);
				 */
				NGLog.consoleLog("Json Array from Query : " + Color);
				jobj.put("Grid_DownPayment", DownPayment);
				jobj.put("Grid_Tenure", Tenure);
				jobj.put("Grid_GracePeriod", GracePeriod);
//				jobj.put("Grid_RV", RV);
				jobj.put("Grid_FinancePercentage", FinancePercentage);
				jobj.put("Grid_InsurancePercentage", InsurancePercentage);
				jobj.put("Grid_RVPercentage", RVPercentage);
				jobj.put("Q_CMPLX_CampaignParamters_CustomerType", CustomerType);
				jobj.put("Q_CMPLX_CampaignParamters_CustomerID", CustomerID);
				jobj.put("Q_CMPLX_CampaignParamters_Province", Province);
				jobj.put("Q_CMPLX_CampaignParamters_Gender", Gender);
				jobj.put("Q_CMPLX_CampaignParamters_City", City);
				jobj.put("Grid_AgeRangeMin", AgeRangeMin);
				jobj.put("Grid_AgeRangeMax", AgeRangeMax);
				jobj.put("Q_CMPLX_CampaignParamters_OccupationSector", OccupationSector);
				jobj.put("Q_CMPLX_CampaignParamters_EmployeeType", EmployeeType);
				jobj.put("Q_CMPLX_CampaignParamters_EmployerName", EmployerName);
				jobj.put("Grid_IncomeRangeMin", IncomeRangeMin);
				jobj.put("Grid_IncomeRangeMax", IncomeRangeMax);
				jobj.put("Q_CMPLX_CampaignParamters_Nationality", Nationality);
				jobj.put("Grid_SpecialNeeds", SpecialNeeds);
				jobj.put("table3_table27", PRParams);
				NGLog.consoleLog("JSON Object Created : " + jobj);
				CampaignArray.add(jobj);
				// Grid_ModelName
			}

			NGLog.consoleLog("JSON Array Created for campaign Grid: " + CampaignArray);
			objIForm.addDataToGrid("CampaignGrid", CampaignArray);

			NGLog.consoleLog("After Adding data : " + objIForm.getDataFromGrid("CampaignGrid"));

		} catch (Exception e) {
			NGLog.errorLog("Error in onClickAddGrid" + e);
		}
		if (CampaignArray.size() > 0) {
			ReturnVal = "S";
		}

		return ReturnVal;
	}

	public JSONArray convertToJSONArray(String input) {
		// Split the input string by commas to get an array of values

		NGLog.consoleLog("Inside convertToJSONArray");
		String[] values = input.split(",");

		// Create a new JSONArray to store the values
		JSONArray jsonArray = new JSONArray();

		// Loop through the values and add them to the JSONArray
		for (String value : values) {
			jsonArray.add(value.trim()); // Using trim to remove any leading/trailing spaces
		}
		NGLog.consoleLog("returned JSON Array : " + jsonArray);
		return jsonArray;
	}

	public JSONArray checkJSON(String Value) {
		JSONArray Json = new JSONArray();
		JSONParser par = new JSONParser();
		try {
			if (!"".equalsIgnoreCase(Value)) {
				Json = (JSONArray) par.parse(Value);
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in checkJSON" + e);
		}
		return Json;
	}

	public String onChangeRetailPrice(IFormReference objIForm) {
		NGLog.consoleLog("Inside onChangeRetailPrice");
		String returnFlag = "F";
		double Percentage = 0.0;
		try {
			double RetailPrice = getOrDefaultdob.apply(objIForm.getValue("Grid_RetailPrice").toString(), 0.0);
			double ActualRetailPrice = getOrDefaultdob.apply(objIForm.getValue("Grid_ActualRetailPrice").toString(),
					0.0);
			NGLog.consoleLog("RetailPrice : " + RetailPrice);
			NGLog.consoleLog("ActualRetailPrice : " + ActualRetailPrice);

			String retailPriceQuery = "SELECT VALUE FROM LOS_CAMP_M_CONFIGMASTER (NOLOCK) WHERE KeyID='RetailPricePercentage'";
			List<List<String>> retailResult = null;
			NGLog.consoleLog("retailPriceQuery Query : " + retailPriceQuery);
			try {
				retailResult = objIForm.getDataFromDB(retailPriceQuery);
				NGLog.consoleLog("retailResult : " + retailResult);
				if (retailResult != null && !retailResult.isEmpty()) {
					Percentage = getOrDefaultdob.apply(retailResult.get(0).get(0), 0.0);
				}
			} catch (Exception ex) {
				NGLog.errorLog("Error in retailPriceQuery" + ex);
			}
			NGLog.consoleLog("Percentage : " + Percentage);
			double AdjustedAmount = (ActualRetailPrice * Percentage) / 100;
			NGLog.consoleLog("AdjustedAmount : " + AdjustedAmount);
			if (RetailPrice > (ActualRetailPrice + AdjustedAmount)
					|| RetailPrice < (ActualRetailPrice - AdjustedAmount)) {
				returnFlag = "F";
			} else {
				returnFlag = "S";
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeRetailPrice" + e);
		}
		NGLog.consoleLog("Return Value : " + returnFlag + "~" + Percentage);
		return returnFlag + "~" + Percentage;
	}

}
