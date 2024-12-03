package com.newgen.iforms.user.common;

import java.io.File;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.user.Upload;
import com.newgen.iforms.user.UploadExcelJoint;
import com.newgen.iforms.user.Model.VehicleExceldata;

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

		NGLog.consoleLog("Inside onChangeModelYear in NGCommon");
		String selectedModel = objIForm.getValue("VehicleGrid_Model").toString();
		String sModelQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN arDescription WHEN '" + Locale
				+ "' ='_ar_SA'  THEN arDescription ELSE Description END AS [Model Name],tModelID as [Model ID] FROM LOS_M_Model (NOLOCK) WHERE  YearModel = '"
				+ ModelYear + "' and  tProductID ='" + objIForm.getValue("VehicleGrid_Variant_Code")
				+ "' AND IsActive='1'";
		List<List<String>> sModelResult = null;
		NGLog.consoleLog("sModel Query : " + sModelQuery);
		try {
			sModelResult = objIForm.getDataFromDB(sModelQuery);
			NGLog.consoleLog("sModelResult : " + sModelResult);
			if (sModelResult != null && !sModelResult.isEmpty()) {
				objIForm.clearCombo("VehicleGrid_Model");
				objIForm.clearCombo("VehicleGrid_Colour");
				objIForm.clearCombo("VehicleGrid_ModelCode");
				objIForm.clearCombo("VehicleGrid_ModelSuffix");
				NGLog.consoleLog(
						"VehicleGrid_Model after clear combo : " + objIForm.getValue("VehicleGrid_Model").toString());
				for (int k = 0; k < sModelResult.size(); k++) {
					objIForm.addItemInCombo("VehicleGrid_Model", sModelResult.get(k).get(0),
							sModelResult.get(k).get(1));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in sModelQuery");
		}
		NGLog.consoleLog("selectedVehicleGrid_Model Value : " + selectedModel);
//		objIForm.setValue("Model", selectedModel);
		return "";
	}

	// onChangeModel
	public String onChangeModel(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside onChangeModel in NGCommon");
		String ModelCode = value.split("~")[0];
		String Locale = value.split("~")[1];

		String selectedColour = objIForm.getValue("VehicleGrid_Colour").toString();
		String selectedModelSuffix = objIForm.getValue("VehicleGrid_ModelSuffix").toString();
		String selectedModelCode = objIForm.getValue("VehicleGrid_ModelCode").toString();
		String sColourQuery = "SELECT CASE WHEN '" + Locale + "' ='_ar' THEN ArBodyColour WHEN '" + Locale
				+ "' ='_ar_SA' THEN ArBodyColour ELSE BodyColour END AS [Vehicle Exterior Color] , BodyColorCode AS [Model Color ID] FROM LOS_M_ModelColorMap (NOLOCK) WHERE tModelID IN ("
				+ ModelCode + ")  AND Active='1'";
		String sModelSuffixQuery = "SELECT ModelCodeSuffix FROM LOS_M_Model (NOLOCK) WHERE tModelID IN (" + ModelCode
				+ ") AND IsActive='1'";
		String sModelCodeQuery = "SELECT ModelCode FROM LOS_M_Model (NOLOCK) WHERE tModelID IN (" + ModelCode
				+ ") AND IsActive='1'";
		List<List<String>> sColourResult = null;
		List<List<String>> sModelSuffixResult = null;
		List<List<String>> sModelCodeResult = null;
		try {
			sColourResult = objIForm.getDataFromDB(sColourQuery);
			NGLog.consoleLog("sColourResult : " + sColourResult);
			if (sColourResult != null && !sColourResult.isEmpty()) {
				objIForm.clearCombo("VehicleGrid_Colour");
				NGLog.consoleLog("Colour after clear combo : " + objIForm.getValue("VehicleGrid_Colour").toString());
				for (int k = 0; k < sColourResult.size(); k++) {
					objIForm.addItemInCombo("VehicleGrid_Colour", sColourResult.get(k).get(0),
							sColourResult.get(k).get(1));
				}
			}

			sModelSuffixResult = objIForm.getDataFromDB(sModelSuffixQuery);
			NGLog.consoleLog("sModelSuffixResult : " + sModelSuffixResult);
			if (sModelSuffixResult != null && !sModelSuffixResult.isEmpty()) {
				objIForm.clearCombo("VehicleGrid_ModelSuffix");
				NGLog.consoleLog(
						"ModelSuffix after clear combo : " + objIForm.getValue("VehicleGrid_ModelSuffix").toString());
				for (int k = 0; k < sModelSuffixResult.size(); k++) {
					objIForm.addItemInCombo("VehicleGrid_ModelSuffix", sModelSuffixResult.get(k).get(0),
							sModelSuffixResult.get(k).get(0));
				}
			}

			sModelCodeResult = objIForm.getDataFromDB(sModelCodeQuery);
			NGLog.consoleLog("sModelCodeResult : " + sModelCodeResult);
			if (sModelCodeResult != null && !sModelCodeResult.isEmpty()) {
				objIForm.clearCombo("VehicleGrid_ModelCode");
				NGLog.consoleLog(
						"ModelCode after clear combo : " + objIForm.getValue("VehicleGrid_ModelCode").toString());
				for (int k = 0; k < sModelCodeResult.size(); k++) {
					objIForm.addItemInCombo("VehicleGrid_ModelCode", sModelCodeResult.get(k).get(0),
							sModelCodeResult.get(k).get(0));
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in onChangeModel");
		}
		NGLog.consoleLog("selectedColour Value : " + selectedColour);
		NGLog.consoleLog("selectedModelSuffix Value : " + selectedModelSuffix);
		NGLog.consoleLog("selectedModelCode Value : " + selectedModelCode);
		return "";
	}

	// For generating Campaign code
	public String generateCampCode(IFormReference objIForm, String value) {
		NGLog.consoleLog("Inside generateCampCode in NGCommon");
		NGLog.consoleLog("Value : " + value);
		Set<String> vehiclebrands = new HashSet<>();
		StringBuffer CampCode = new StringBuffer();
		CampCode.append(objIForm.getValue("ProductType"));
		String brandID = objIForm.getTableCellValue("VehicleGrid", 0, 0);
		if ("".equalsIgnoreCase(brandID)) {
			brandID = objIForm.getValue("VehicleGrid_VehicleBrand").toString();
		}
//		int VehicleGridCount = getOrDefaultInt.apply(value, 0);
		for (int i = 0; i < objIForm.getDataFromGrid("VehicleGrid").size(); i++) {
			vehiclebrands.add(objIForm.getTableCellValue("VehicleGrid", i, 0));
		}
		if (!"".equalsIgnoreCase(value)) {
			vehiclebrands.add(value);
		}
		NGLog.consoleLog("vehiclebrands Size : " + vehiclebrands.size());
		try {
			String sBrandquery = "SELECT Top 1 ShortDescription FROM LOS_M_Brand (NOLOCK) WHERE Description = '"
					+ brandID.trim() + "' AND ShortDescription IS NOT NULL and ShortDescription != 'NULL'";
			List<List<String>> sBrandResult = null;
			sBrandResult = objIForm.getDataFromDB(sBrandquery);
			NGLog.consoleLog("Brnad Query  : " + sBrandquery);
			NGLog.consoleLog("Brnad Query Result : " + sBrandResult);
			if (vehiclebrands.size() > 1) {
				CampCode.append("MUL");
			} else if (vehiclebrands.size() == 1) {
				if (!sBrandResult.isEmpty() && sBrandResult != null) {
					CampCode.append(sBrandResult.get(0).get(0).trim());
				}
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
				sWIdataQuery = "SELECT Top 1 CampaignType,CampaignName,ModelPriceRangeMin,ModelPriceRangeMax,AgeRangeMin,AgeRangeMax,IncomeRangeMin,IncomeRangeMax,MinLoanAmount,LoanResidualValue,InsuranceRate,DownPayment,GracePeriod,TenurePeriodForCalculation,LoanFinanceRate,PRFinanceRate,MaxLoanAmount,AdminFeesWithVatRebateAmount,AdminFeesWithVATWaiver,AdminFeesWithoutVATRebateAmount,MonthlyInstalmentwithVAT,BuybackByWholesaler,AdminFeeWaiver,SpecialNeeds,RetailPurchaseDiscount,FullyWaveAdminFee,AppropriationPriority1, AppropriationPriority2, AppropriationPriority3,ApplicationNo FROM LOS_CAMPAIGN_EXT (NOLOCK) WHERE CampaignCode= '"
						+ CampCode_Name + "' ORDER by itemindex desc";
				Keys.addAll(Arrays.asList("CampaignType", "CampaignName", "ModelPriceRangeMin", "ModelPriceRangeMax",
						"AgeRangeMin", "AgeRangeMax", "IncomeRangeMin", "IncomeRangeMax", "MinLoan",
						"LoanResidualValue", "InsuranceRate", "Downpayment", "GracePeriod", "TenurePeriodForCalc",
						"LoanFinanceRate", "PRFinanceRate", "MaxLoan", "FeesWithVATRebate", "FeesWithVATWaiver",
						"FeesWithoutVATRebate", "MonthlywithVAT", "BuybackByWholesaler", "AdminFeeWaiver",
						"SpecialNeeds", "RetailPurchaseDiscount", "FullyWave", "AppropriationPriority1",
						"AppropriationPriority2", "AppropriationPriority3"));
			} else if ("CampaignName".equalsIgnoreCase(CalledFrom)) {
				sWIdataQuery = "SELECT Top 1 CampaignType,CampaignCode,PromoCode,StartDate,EndDate,ModelPriceRangeMin,ModelPriceRangeMax,AgeRangeMin,AgeRangeMax,IncomeRangeMin,IncomeRangeMax,MinLoanAmount,LoanResidualValue,InsuranceRate,DownPayment,GracePeriod,TenurePeriodForCalculation,LoanFinanceRate,PRFinanceRate,MaxLoanAmount,AdminFeesWithVatRebateAmount,AdminFeesWithVATWaiver,AdminFeesWithoutVATRebateAmount,MonthlyInstalmentwithVAT,BuybackByWholesaler,AdminFeeWaiver,SpecialNeeds,RetailPurchaseDiscount,FullyWaveAdminFee,AppropriationPriority1, AppropriationPriority2, AppropriationPriority3,ApplicationNo FROM LOS_CAMPAIGN_EXT (NOLOCK) WHERE CampaignCode=(SELECT CampaignCode FROM Camp_CampaignInfo(NOLOCK) WHERE CampaignName= N'"
						+ CampCode_Name + "') AND  CampaignName= N'" + CampCode_Name + "' ORDER by itemindex desc";
				Keys.addAll(Arrays.asList("CampaignType", "CampaignCode", "PromoCode", "StartDate", "EndDate",
						"ModelPriceRangeMin", "ModelPriceRangeMax", "AgeRangeMin", "AgeRangeMax", "IncomeRangeMin",
						"IncomeRangeMax", "MinLoan", "LoanResidualValue", "InsuranceRate", "Downpayment", "GracePeriod",
						"TenurePeriodForCalc", "LoanFinanceRate", "PRFinanceRate", "MaxLoan", "FeesWithVATRebate",
						"FeesWithVATWaiver", "FeesWithoutVATRebate", "MonthlywithVAT", "BuybackByWholesaler",
						"AdminFeeWaiver", "SpecialNeeds", "RetailPurchaseDiscount", "FullyWave",
						"AppropriationPriority1", "AppropriationPriority2", "AppropriationPriority3"));
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
				String VINNumbersQuery = "";
				if ("CopyCampaignCode".equalsIgnoreCase(CalledFrom)) {
					VINNumbersQuery = "SELECT A.VINNumber FROM LOS_CAMP_CMPLX_VINNumber(NOLOCK) A INNER JOIN LOS_CAMPAIGN_EXT B on A.PID=B.ApplicationNo WHERE B.CampaignCode= '"
							+ CampCode_Name + "'";
				} else if ("CampaignName".equalsIgnoreCase(CalledFrom)) {
					VINNumbersQuery = "SELECT A.VINNumber FROM LOS_CAMP_CMPLX_VINNumber(NOLOCK) A INNER JOIN LOS_CAMPAIGN_EXT B on A.PID=B.ApplicationNo WHERE B.CampaignName= '"
							+ CampCode_Name + "'";
				}
				NGLog.consoleLog("VINNumbersQuery : " + VINNumbersQuery);
				JSONArray VINNumbers = new JSONArray();
				JSONArray OccupationSector = new JSONArray();
				JSONArray ProductSubType = new JSONArray();
				JSONArray VehicleType = new JSONArray();
				JSONArray DistributorWise = new JSONArray();
				JSONArray Wholesalers = new JSONArray();
				JSONArray WholesalerBranch = new JSONArray();
				JSONArray Province = new JSONArray();
				JSONArray City = new JSONArray();
				JSONArray CustomerType = new JSONArray();
				JSONArray CustID = new JSONArray();
				JSONArray Gender = new JSONArray();
				JSONArray EmployeeType = new JSONArray();
				JSONArray EmployerName = new JSONArray();
				JSONArray Nationality = new JSONArray();
				JSONArray Channel = new JSONArray();
				JSONArray MonthlyInstalmentWaiverwithVAT = new JSONArray();
				JSONArray MonthlyInstalmentWaiverWoVAT = new JSONArray();
				JSONArray Instalmenttobewaived = new JSONArray();

				// commenting for testing
//				VINNumbersResult = objIForm.getDataFromDB(VINNumbersQuery);
//				if (VINNumbersResult != null && !VINNumbersResult.isEmpty()) {
//					for (int i = 0; i < VINNumbersResult.size(); i++) {
//						VINNumbers.add(VINNumbersResult.get(i).get(0));
//						objIForm.addItemInCombo("VINNumber", VINNumbersResult.get(i).get(0),
//								VINNumbersResult.get(i).get(0));
//					}
//					NGLog.consoleLog("VINNumbers : " + VINNumbers);
//				}
				// commenting for testing END
				// testing
				String Overallquery = "SELECT(SELECT  String_AGG(OccupationSector,'~') FROM LOS_CAMP_CMPLX_OccupationSector (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS OccupationSector,(SELECT String_AGG(VINNumber,'~')  FROM LOS_CAMP_CMPLX_VINNumber (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS VINNumber,(SELECT String_AGG(ProductSubType,'~')  FROM LOS_CAMP_CMPLX_ProductSubType (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS ProductSubType,(SELECT String_AGG(VehicleType,'~')  FROM LOS_CAMP_CMPLX_VehicleType (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS VehicleType,(SELECT String_AGG(DistributorWise,'~')  FROM LOS_CAMP_CMPLX_DistributorWise (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS DistributorWise,(SELECT String_AGG(Wholesalers,'~')  FROM LOS_CAMP_CMPLX_Wholesalers (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS Wholesalers,(SELECT String_AGG(WholesalerBranch,'~')  FROM LOS_CAMP_CMPLX_WholesalerBranch (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS WholesalerBranch,(SELECT String_AGG(Province,'~')  FROM LOS_CAMP_CMPLX_Province (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS Province,(SELECT String_AGG(City,'~')  FROM LOS_CAMP_CMPLX_City (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS City,(SELECT String_AGG(CustomerType,'~')  FROM LOS_CAMP_CMPLX_CustomerType (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS CustomerType,(SELECT String_AGG(CustID,'~')  FROM LOS_CAMP_CMPLX_CustID (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS CustID,(SELECT String_AGG(Gender,'~')  FROM LOS_CAMP_CMPLX_Gender (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS Gender,(SELECT String_AGG(EmployeeType,'~')  FROM LOS_CAMP_CMPLX_EmployeeType (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS EmployeeType,(SELECT String_AGG(EmployerName,'~')  FROM LOS_CAMP_CMPLX_EmployerName (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS EmployerName,(SELECT String_AGG(Nationality,'~')  FROM LOS_CAMP_CMPLX_Nationality (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS Nationality,(SELECT String_AGG(Channel,'~')  FROM LOS_CAMP_CMPLX_Channel (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS Channel,(SELECT String_AGG(MonthlyInstalmentWaiverwithVAT,'~')  FROM LOS_CAMP_CMPLX_MonthlyInstalmentWaiverwithVAT (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS MonthlyInstalmentWaiverwithVAT,(SELECT String_AGG(MonthlyInstalmentWaiverWoVAT,'~')  FROM LOS_CAMP_CMPLX_MonthlyInstalmentWaiverWoVAT (NOLOCK) WHERE PID = '"
						+ processID
						+ "') AS MonthlyInstalmentWaiverWoVAT,(SELECT String_AGG(Instalmenttobewaived,'~')  FROM LOS_CAMP_CMPLX_Instalmenttobewaived (NOLOCK) WHERE PID = '"
						+ processID + "') AS Instalmenttobewaived";
				NGLog.consoleLog("Overallquery : " + Overallquery);
				List<List<String>> OverallResult = null;
				OverallResult = objIForm.getDataFromDB(Overallquery);
				NGLog.consoleLog("Overallquery Result : " + OverallResult);
				if (OverallResult != null && !OverallResult.isEmpty()) {
					for (int i = 0; i < OverallResult.get(0).size(); i++) {
						String rowData = OverallResult.get(0).get(i);
						if (rowData != null && !rowData.isEmpty()) {
							String[] dataParts = rowData.split("~");
							for (String dataPart : dataParts) {
								if (!dataPart.isEmpty()) {
									switch (i) {
									case 0:
										OccupationSector.add(dataPart);
										break;
									case 1:
										objIForm.addItemInCombo("VINNumber", dataPart, dataPart);
										VINNumbers.add(dataPart);
										break;
									case 2:
										objIForm.addItemInCombo("VINNumber", dataPart, dataPart);
										ProductSubType.add(dataPart);
										break;
									case 3:
										VehicleType.add(dataPart);
										break;
									case 4:
										DistributorWise.add(dataPart);
										break;
									case 5:
										Wholesalers.add(dataPart);
										break;
									case 6:
										WholesalerBranch.add(dataPart);
										break;
									case 7:
										Province.add(dataPart);
										break;
									case 8:
										City.add(dataPart);
										break;
									case 9:
										CustomerType.add(dataPart);
										break;
									case 10:
										objIForm.addItemInCombo("CustID", dataPart, dataPart);
										CustID.add(dataPart);
										break;
									case 11:
										Gender.add(dataPart);
										break;
									case 12:
										EmployeeType.add(dataPart);
										break;
									case 13:
										EmployerName.add(dataPart);
										break;
									case 14:
										Nationality.add(dataPart);
										break;
									case 15:
										Channel.add(dataPart);
										break;
									case 16:
										MonthlyInstalmentWaiverwithVAT.add(dataPart);
										break;
									case 17:
										MonthlyInstalmentWaiverWoVAT.add(dataPart);
										break;
									case 18:
										Instalmenttobewaived.add(dataPart);
										break;
									}
								}
							}
						}
					}
				}
				NGLog.consoleLog("OccupationSector : " + OccupationSector);
				NGLog.consoleLog("VINNumbers : " + VINNumbers);

				String vehiclequery = "SELECT VehicleBrand,Variant,ModelYear,ModelPriceRangeMin,ModelPriceRangeMax,ChildMapper,VehicleBrandCode,VariantCode,ColumnID,Tenure,GracePeriod,DownPayment,FinancePercentage,InsurancePercentage,RVPercentage,SupportAmounttobeinPercentage, MaximumSupportLimit,DistributorSupportAmount, WSSupportAmount, ALJFSSupportAmount,RetailPurchaseDiscount,Cashback FROM LOS_CAMP_CMPLX_VehicleDetails(NOLOCK) where PID= '"
						+ processID + "'";
				String sChildModelQuery = "";
				String sChildColourQuery = "";
				String sChildModelSuffixQuery = "";
				String sChildModelCodeQuery = "";

//				JSONArray Model = new JSONArray();
//				JSONArray Colour = new JSONArray();
//				JSONArray ModelSuffix = new JSONArray();
//				JSONArray ModelCode = new JSONArray();

				List<String> Model = new ArrayList<>();
				List<String> Colour = new ArrayList<>();
				List<String> ModelSuffix = new ArrayList<>();
				List<String> ModelCode = new ArrayList<>();

				NGLog.consoleLog("vehiclequery : " + vehiclequery);
				List<List<String>> sChildModelResult = null;
				List<List<String>> sChildColourResult = null;
				List<List<String>> sChildModelSuffixResult = null;
				List<List<String>> sChildModelCodeResult = null;
				List<List<String>> vehicleResult = null;
				vehicleResult = objIForm.getDataFromDB(vehiclequery);
				JSONArray vehiclArr = new JSONArray();
				NGLog.consoleLog("getdatafrom gid : " + objIForm.getDataFromGrid("VehicleGrid"));
				if (vehicleResult != null && !vehicleResult.isEmpty()) {
					objIForm.clearTable("VehicleGrid");
					for (int i = 0; i < vehicleResult.size(); i++) {
						JSONObject jobj = new JSONObject();
						jobj.put("VehicleGrid_VehicleBrand", vehicleResult.get(i).get(0));
						jobj.put("VehicleGrid_Variant", vehicleResult.get(i).get(1));
						jobj.put("VehicleGrid_ModelYear", vehicleResult.get(i).get(2));
						jobj.put("VehicleGrid_VehiclePriceMin", vehicleResult.get(i).get(3));
						jobj.put("VehicleGrid_VehiclePriceMax", vehicleResult.get(i).get(4));
						jobj.put("VehicleGrid_VehicleBrand_Code", vehicleResult.get(i).get(6));
						jobj.put("VehicleGrid_Variant_Code", vehicleResult.get(i).get(7));
						jobj.put("VehicleGrid_ColumnID", vehicleResult.get(i).get(8));
						jobj.put("VehicleGrid_Tenure", vehicleResult.get(i).get(9));
						jobj.put("VehicleGrid_GracePeriod", vehicleResult.get(i).get(10));
						jobj.put("VehicleGrid_DownPayment", vehicleResult.get(i).get(11));
						jobj.put("VehicleGrid_FinancePercentage", vehicleResult.get(i).get(12));
						jobj.put("VehicleGrid_InsurancePercentage", vehicleResult.get(i).get(13));
						jobj.put("VehicleGrid_RVPercentage", vehicleResult.get(i).get(14));//
						jobj.put("VehicleGrid_SupportAmounttobe", vehicleResult.get(i).get(15));
						jobj.put("VehicleGrid_MaximumSupportAmount", vehicleResult.get(i).get(16));
						jobj.put("VehicleGrid_DistributorSupportAmount", vehicleResult.get(i).get(17));
						jobj.put("VehicleGrid_WSSupportAmount", vehicleResult.get(i).get(18));
						jobj.put("VehicleGrid_ALJFSSupportAmount", vehicleResult.get(i).get(19));
						jobj.put("VehicleGrid_RetailPurchaseDiscount", vehicleResult.get(i).get(20));
						jobj.put("VehicleGrid_Cashback", vehicleResult.get(i).get(21));
						if (!"".equalsIgnoreCase(vehicleResult.get(i).get(5))) {
							// Model
							sChildModelQuery = "SELECT Model FROM LOS_CAMP_CHILD_Model (NOLOCK) WHERE ChildMapper='"
									+ vehicleResult.get(i).get(5).trim() + "'";
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
									+ vehicleResult.get(i).get(5).trim() + "'";
							sChildColourResult = objIForm.getDataFromDB(sChildColourQuery);
							if (sChildColourResult != null && !sChildColourResult.isEmpty()) {
								for (int j = 0; j < sChildColourResult.size(); j++) {
									Colour.add(sChildColourResult.get(j).get(0));
								}
							}
							// ModelSuffix
							sChildModelSuffixQuery = "SELECT ModelSuffix FROM LOS_CAMP_CHILD_ModelSuffix (NOLOCK) WHERE ChildMapper='"
									+ vehicleResult.get(i).get(5).trim() + "'";
							sChildModelSuffixResult = objIForm.getDataFromDB(sChildModelSuffixQuery);
							if (sChildModelSuffixResult != null && !sChildModelSuffixResult.isEmpty()) {
								for (int j = 0; j < sChildModelSuffixResult.size(); j++) {
									ModelSuffix.add(sChildModelSuffixResult.get(j).get(0));
								}
							}
							// ModelCode
							sChildModelCodeQuery = "SELECT ModelCode FROM LOS_CAMP_CHILD_ModelCode (NOLOCK) WHERE ChildMapper='"
									+ vehicleResult.get(i).get(5).trim() + "'";
							sChildModelCodeResult = objIForm.getDataFromDB(sChildModelCodeQuery);
							if (sChildModelCodeResult != null && !sChildModelCodeResult.isEmpty()) {
								for (int j = 0; j < sChildModelCodeResult.size(); j++) {
									ModelCode.add(sChildModelCodeResult.get(j).get(0));
								}
							}
//							jobj.put("Q_CMPLX_VehicleDetails_Model", !Model.isEmpty() ? Model : "");
//							jobj.put("Q_CMPLX_VehicleDetails_Colour", !Colour.isEmpty() ? Colour : "");
//							jobj.put("Q_CMPLX_VehicleDetails_ModelSuffix", !ModelSuffix.isEmpty() ? ModelSuffix : "");
//							jobj.put("Q_CMPLX_VehicleDetails_ModelCode", !ModelCode.isEmpty() ? ModelCode : "");
							jobj.put("Q_CMPLX_VehicleDetails_Model", Model);
							jobj.put("Q_CMPLX_VehicleDetails_Colour", Colour);
							jobj.put("Q_CMPLX_VehicleDetails_ModelSuffix", ModelSuffix);
							jobj.put("Q_CMPLX_VehicleDetails_ModelCode", ModelCode);
						}
						vehiclArr.add(jobj);
					}
					NGLog.consoleLog("vehicleGrid Data : " + vehiclArr);
//					objIForm.addDataToGrid("VehicleGrid", vehiclArr);
					// new added 1st may

//					if ("CopyCampaignCode".equalsIgnoreCase(CalledFrom)) {
//					String jointCampData = "SELECT Brand, Product, Model, Full_Variant, Model_Year, Tenure, Grace_Period, Description, Trim, Retail_Price, Purchase_Discount, Formula_Price, Finance_Charge, Insurance, Total_Amount, Down_Payment, Finance_Amount, MI, RV, RV_Percentage, Admin_Fee, Finance_Percentage, Insur, Total_term_cost_ratio, Admin_Fee_Discount_Amount, Free_first_installment, Free_second_installment, Free_third_installment, Total, Total_Rebate_Discount, Difference, Rebate_Percentage, Rebate_Amount, InsertionOrderID, Free_fourth_Installment, Free_fifth_Installment, Free_sixth_Installment, Cashback FROM Camp_JointCampaignData (NOLOCK) WHERE PID='"
//							+ processID + "'";
//					List<List<String>> jointCampDataResult = null;
//					jointCampDataResult = objIForm.getDataFromDB(jointCampData);
//					if (jointCampDataResult != null && !jointCampDataResult.isEmpty()) {
//						for (int i = 0; i < jointCampDataResult.size(); i++) {
//							String insert ="INSERT INTO Camp_JointCampaignData (Brand, Product, Model, Full_Variant, Model_Year, Tenure, Grace_Period, Description, Trim, Retail_Price, Purchase_Discount, Formula_Price, Finance_Charge, Insurance, Total_Amount, Down_Payment, Finance_Amount, MI, RV, RV_Percentage, Admin_Fee, Finance_Percentage, Insur, Total_term_cost_ratio, Admin_Fee_Discount_Amount, Free_first_installment, Free_second_installment, Free_third_installment, Total, Total_Rebate_Discount, Difference, Rebate_Percentage, Rebate_Amount, CampaignCode, PID, Free_fourth_Installment, Free_fifth_Installment, Free_sixth_Installment, Cashback) VALUES ('"+jointCampDataResult.get(i).get(0)+"','"+jointCampDataResult.get(i).get(1)+"','"+jointCampDataResult.get(i).get(2)+"', '"+jointCampDataResult.get(i).get(3)+"', '"+jointCampDataResult.get(i).get(4)+"', '"+jointCampDataResult.get(i).get(5)+"', '"+jointCampDataResult.get(i).get(6)+"', '"+jointCampDataResult.get(i).get(7)+"', '"+jointCampDataResult.get(i).get(8)+"', '"+jointCampDataResult.get(i).get(9)+"', '"+jointCampDataResult.get(i).get(10)+"', '"+jointCampDataResult.get(i).get(11)+"', '"+jointCampDataResult.get(i).get(12)+"','"+jointCampDataResult.get(i).get(13)+"', '"+jointCampDataResult.get(i).get(14)+"', '"+jointCampDataResult.get(i).get(15)+"', '"+jointCampDataResult.get(i).get(16)+"', '"+jointCampDataResult.get(i).get(17)+"', '"+jointCampDataResult.get(i).get(18)+"', '"+jointCampDataResult.get(i).get(19)+"', '"+jointCampDataResult.get(i).get(20)+"', '"+jointCampDataResult.get(i).get(21)+"', '"+jointCampDataResult.get(i).get(22)+"', '"+jointCampDataResult.get(i).get(23)+"', '"+jointCampDataResult.get(i).get(24)+"', '"+jointCampDataResult.get(i).get(25)+"', '"+jointCampDataResult.get(i).get(26)+"', '"+jointCampDataResult.get(i).get(27)+"', '"+jointCampDataResult.get(i).get(28)+"', '"+jointCampDataResult.get(i).get(29)+"','"+jointCampDataResult.get(i).get(30)+"', '"+jointCampDataResult.get(i).get(31)+"', '"+jointCampDataResult.get(i).get(32)+"', '"+CampCode_Name+"', '"+objIForm.getObjGeneralData().getM_strProcessInstanceId()+"','"+jointCampDataResult.get(i).get(33)+"', '"+jointCampDataResult.get(i).get(34)+"', '"+jointCampDataResult.get(i).get(35)+"', '"+jointCampDataResult.get(i).get(36)+"')";
//							NGLog.consoleLog("INSERT data for joint copy data: " + insert);
//							objIForm.getDataFromDB(insert);
//						}
//					  }
//					}
					// new End 1st may
				}
				objIForm.setValue("VINNumber", VINNumbers);
				objIForm.setValue("OccupationSector", OccupationSector);
				objIForm.setValue("VehicleType", VehicleType);
				objIForm.setValue("DistributorWise", DistributorWise);
				objIForm.setValue("Wholesalers", Wholesalers);
				objIForm.setValue("WholesalerBranch", WholesalerBranch);
				objIForm.setValue("Province", Province);
				objIForm.setValue("City", City);
				objIForm.setValue("CustomerType", CustomerType);
				objIForm.setValue("CustID", CustID);
				objIForm.setValue("Gender", Gender);
				objIForm.setValue("EmployeeType", EmployeeType);
				objIForm.setValue("EmployerName", EmployerName);
				objIForm.setValue("Nationality", Nationality);
				objIForm.setValue("Channel", Channel);
				objIForm.setValue("InstalmentWaiverWithVAT", MonthlyInstalmentWaiverwithVAT);
				objIForm.setValue("InstalmentWaiverWithoutVAT", MonthlyInstalmentWaiverWoVAT);
				objIForm.setValue("InstalmentToBeWaived", Instalmenttobewaived);
				if ("CampaignName".equalsIgnoreCase(CalledFrom)) {
					objIForm.setValue("ProductSubType", ProductSubType);
				}
//				sReturnValue = "S~" + VINNumbers + "~" + vehiclArr + "~" + OccupationSector + "~" + VehicleType + "~"
//						+ DistributorWise + "~" + Wholesalers + "~" + WholesalerBranch + "~" + Province + "~" + City
//						+ "~" + CustomerType + "~" + CustID + "~" + Gender + "~" + EmployeeType + "~" + EmployerName
//						+ "~" + Nationality + "~" + Channel + "~" + MonthlyInstalmentWaiverwithVAT + "~"
//						+ MonthlyInstalmentWaiverWoVAT + "~" + Instalmenttobewaived;
				sReturnValue = "S~" + vehiclArr;
			} else {
				sReturnValue = "F~No Campaign available with Campaign Code : ";
				return sReturnValue;
			}
		} catch (Exception e) {
			sReturnValue = "F~Error in searching Campaign with Campaign Code : ";
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
		String selectedLocale = value.split("~")[1];
		String selectedCity = objIForm.getValue("City").toString();
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
				objIForm.clearCombo("City");
				NGLog.consoleLog("City after clear combo : " + objIForm.getValue("City").toString());
				for (int k = 0; k < sSubCityResult.size(); k++) {
					objIForm.addItemInCombo("City", sSubCityResult.get(k).get(0), sSubCityResult.get(k).get(1));
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

	public String getExcelData(IFormReference objIForm) {
		NGLog.consoleLog("Inside downloadExcel in NGCommon");
		String returnVal = "";
		try {
			String filePath = System.getProperty("user.dir") + File.separator + "CAMPAIGN_CONFIG" + File.separator
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "file.xlsx";
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Campaign");
			String ColumnNamesSequential = "";
			String sColumnsQuery = "SELECT Value FROM LOS_CAMP_M_CONFIGMASTER (NOLOCK) WHERE KeyID='ExcelColumns'";
			List<List<String>> sColumnsQueryResult = null;
			sColumnsQueryResult = objIForm.getDataFromDB(sColumnsQuery);
			NGLog.consoleLog("sColumnsQueryResult : " + sColumnsQueryResult);
			if (sColumnsQueryResult != null && !sColumnsQueryResult.isEmpty()) {
				ColumnNamesSequential = sColumnsQueryResult.get(0).get(0);
			}
			ArrayList<String> ColumnName = new ArrayList<>(Arrays.asList(ColumnNamesSequential.split("~")));
			NGLog.consoleLog("ColumnNamesSequential: " + ColumnNamesSequential);
			NGLog.consoleLog("ColumnName.get(0) : " + ColumnName.get(0).toString());
			NGLog.consoleLog("ColumnName.get(0) after split : " + ColumnName.get(0).split("/")[0] + "\n"
					+ ColumnName.get(0).split("/")[1]);
			NGLog.consoleLog("ColumnName.size() : " + ColumnName.size());
			// Constant Headers start
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < ColumnName.size(); i++) {
				headerRow.createCell(i)
						.setCellValue(ColumnName.get(i).split("/")[0] + "\n" + ColumnName.get(i).split("/")[1]);
			}

			CellStyle centeredStyle = workbook.createCellStyle();
			Font boldfont = workbook.createFont();
			boldfont.setFontName("Cambria");
			boldfont.setFontHeightInPoints((short) 10);
			boldfont.setBold(true);
			centeredStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
			centeredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			centeredStyle.setAlignment(HorizontalAlignment.CENTER);
			centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			centeredStyle.setBorderTop(BorderStyle.MEDIUM);
			centeredStyle.setBorderBottom(BorderStyle.MEDIUM);
			centeredStyle.setBorderLeft(BorderStyle.MEDIUM);
			centeredStyle.setBorderRight(BorderStyle.MEDIUM);
			centeredStyle.setFont(boldfont);
//			centeredStyle.setLocked(true);
//			sheet.protectSheet("Password");
			centeredStyle.setWrapText(true);
			for (int i = 0; i < headerRow.getLastCellNum(); i++) {
				Cell cell = headerRow.getCell(i);
				if (cell != null) {
					sheet.autoSizeColumn(i);

					cell.setCellStyle(centeredStyle);
				}
			}
			// Constant Headers End

			// Table data row Start
			String sVehicleQuery = "SELECT VehicleBrand,VehicleBrandCode,Variant,VariantCode,ModelYear,GracePeriod,Tenure,ChildMapper,DownPayment,FinancePercentage,InsurancePercentage,ALJFSSupportAmount,RVPercentage,SupportAmounttobeinPercentage,MaximumSupportLimit,RetailPurchaseDiscount,Cashback,WSSupportAmount, DistributorSupportAmount FROM LOS_CAMP_CMPLX_VehicleDetails (NOLOCK) WHERE PID='"
					+ objIForm.getObjGeneralData().getM_strProcessInstanceId() + "'";
			List<List<String>> sVehicleResult = null;
			int rows = 1;
			try {
				sVehicleResult = objIForm.getDataFromDB(sVehicleQuery);
				NGLog.consoleLog("sVehicleQuery : " + sVehicleQuery);
				NGLog.consoleLog("sVehicleResult : " + sVehicleResult);
				if (sVehicleResult != null && !sVehicleResult.isEmpty()) {
					String Brand = "", Variant = "", ModelYear = "";
					int GracePeriod, Tenure;
					for (int k = 0; k < sVehicleResult.size(); k++) {
						Brand = sVehicleResult.get(k).get(0);
						Variant = sVehicleResult.get(k).get(2);
						ModelYear = sVehicleResult.get(k).get(4);
						GracePeriod = getOrDefaultInt.apply(sVehicleResult.get(k).get(5), 0);
						Tenure = getOrDefaultInt.apply(sVehicleResult.get(k).get(6), 0);
						rows = createRow(objIForm, sVehicleResult.get(k).get(7).trim(), sheet, Brand, Variant, Tenure,
								GracePeriod, ModelYear, rows, workbook,
								getOrDefaultdob.apply(sVehicleResult.get(k).get(8), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(9), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(10), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(11), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(12), 0.0),
								sVehicleResult.get(k).get(13),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(14), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(15), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(16), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(17), 0.0),
								getOrDefaultdob.apply(sVehicleResult.get(k).get(18), 0.0));
					}
				}
				NGLog.consoleLog("headerRow.getPhysicalNumberOfCells() : " + headerRow.getPhysicalNumberOfCells());
				sheet.protectSheet("password");
				sheet.setColumnWidth(28, 256 * 20);
			} catch (Exception e) {
				NGLog.errorLog("Error in sVariantQuery");
			}
			try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
				workbook.write(fileOut);
				NGLog.consoleLog("Excel file created successfully at: " + filePath);
				returnVal = "S";
			} catch (IOException e) {
				NGLog.consoleLog("Excel file create Error: " + e);
				returnVal = "F";
			} finally {
				try {
					workbook.close();
				} catch (IOException e) {
					NGLog.errorLog("Error : " + Arrays.toString(e.getStackTrace()));
					returnVal = "F";
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error2: " + Arrays.toString(e.getStackTrace()));
			returnVal = "F";
		}
		return returnVal;
	}

	public int createRow(IFormReference objIForm, String ChildMapper, Sheet sheet, String Brand, String Variant,
			int Tenure, int GracePeriod, String ModelYear, int rows, Workbook workbook, double Downpayment,
			double FinancePercent, double InsurancePercent, double RebateSupportAmount, double RVPercent,
			String Supporttobein, double MaxSupportAmt, double RetailPurchaseDiscount, double Cashback,
			double WSSupport, double DisSupport) {
		FinancePercent = FinancePercent / 100;
		InsurancePercent = InsurancePercent / 100;
		RVPercent = RVPercent / 100;

		NGLog.consoleLog("Inside createRow Method");
		String ModelSuffix = "", ModelCode = "", ModelName = "";
		double RetailPrice = 0.0;
		double RebateSupportAmt = 0.0;
		double WSSupportAmt = 0.0;
		double DisSupportAmt = 0.0;
		String numberFormat = "#,##0.00";
		String PercentageFormat = "0.00%";

		CellStyle customstyle = workbook.createCellStyle();
		customstyle.setDataFormat(workbook.createDataFormat().getFormat(numberFormat));
		customstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		customstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		customstyle.setBorderTop(BorderStyle.THIN);
		customstyle.setBorderBottom(BorderStyle.THIN);
		customstyle.setBorderLeft(BorderStyle.THIN);
		customstyle.setBorderRight(BorderStyle.THIN);
		customstyle.setLocked(false);
		CellStyle customstylewithLock = workbook.createCellStyle();
		customstylewithLock.setDataFormat(workbook.createDataFormat().getFormat(numberFormat));
		customstylewithLock.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		customstylewithLock.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		customstylewithLock.setBorderTop(BorderStyle.THIN);
		customstylewithLock.setBorderBottom(BorderStyle.THIN);
		customstylewithLock.setBorderLeft(BorderStyle.THIN);
		customstylewithLock.setBorderRight(BorderStyle.THIN);
		customstylewithLock.setLocked(true);
		CellStyle customPercentagestyle = workbook.createCellStyle();
		customPercentagestyle.setDataFormat(workbook.createDataFormat().getFormat(PercentageFormat));
		customPercentagestyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		customPercentagestyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		customPercentagestyle.setBorderTop(BorderStyle.THIN);
		customPercentagestyle.setBorderBottom(BorderStyle.THIN);
		customPercentagestyle.setBorderLeft(BorderStyle.THIN);
		customPercentagestyle.setBorderRight(BorderStyle.THIN);
		customPercentagestyle.setLocked(false);
		CellStyle customPercentagestylewithlock = workbook.createCellStyle();
		customPercentagestylewithlock.setDataFormat(workbook.createDataFormat().getFormat(PercentageFormat));
		customPercentagestylewithlock.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		customPercentagestylewithlock.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		customPercentagestylewithlock.setBorderTop(BorderStyle.THIN);
		customPercentagestylewithlock.setBorderBottom(BorderStyle.THIN);
		customPercentagestylewithlock.setBorderLeft(BorderStyle.THIN);
		customPercentagestylewithlock.setBorderRight(BorderStyle.THIN);
		customPercentagestylewithlock.setLocked(true);
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		String getModel = "SELECT Model FROM LOS_CAMP_CHILD_Model WHERE ChildMapper='" + ChildMapper + "'";
		List<List<String>> sgetModelResult = null;
		try {
			String AppPriority1 = objIForm.getValue("AppropriationPriority1").toString();
			String AppPriority2 = objIForm.getValue("AppropriationPriority2").toString();
			String AppPriority3 = objIForm.getValue("AppropriationPriority3").toString();
			sgetModelResult = objIForm.getDataFromDB(getModel);
			NGLog.consoleLog("getModelQuery : " + getModel);
			NGLog.consoleLog("sgetModelResult : " + sgetModelResult);
			if (sgetModelResult != null && !sgetModelResult.isEmpty()) {
				for (int k = 0; k < sgetModelResult.size(); k++) {
					String ModelDetailquery = "SELECT ModelCodeSuffix,ModelCode,Description,RetailPrice FROM LOS_M_Model (NOLOCK) WHERE tModelID='"
							+ sgetModelResult.get(k).get(0) + "'";
					List<List<String>> ModelDetailResult = null;
					ModelDetailResult = objIForm.getDataFromDB(ModelDetailquery);
					NGLog.consoleLog("ModelDetailResult : " + ModelDetailResult);

					if (ModelDetailResult != null && !ModelDetailResult.isEmpty()) {
						for (int i = 0; i < ModelDetailResult.size(); i++) {
							ModelSuffix = ModelDetailResult.get(i).get(0);
							ModelCode = ModelDetailResult.get(i).get(1);
							ModelName = ModelDetailResult.get(i).get(2);
							RetailPrice = getOrDefaultdob.apply(ModelDetailResult.get(i).get(3), 0.0);
							if ("Percentage".equalsIgnoreCase(Supporttobein)) {
								RebateSupportAmt = RetailPrice * RebateSupportAmount / 100;
								WSSupportAmt = RetailPrice * WSSupport / 100;
								DisSupportAmt = RetailPrice * DisSupport / 100;
							} else {
								RebateSupportAmt = RebateSupportAmount;
								WSSupportAmt = WSSupport;
								DisSupportAmt = DisSupport;
							}
							if ("50-50 Campaign".equalsIgnoreCase(objIForm.getValue("CampaignType").toString())) {
								Downpayment = (RetailPrice * 50) / 100;
							}
							if (RebateSupportAmt > MaxSupportAmt && MaxSupportAmt != 0) {
								RebateSupportAmt = MaxSupportAmt;

								WSSupportAmt = (RebateSupportAmt * 100 / RebateSupportAmount) * WSSupport / 100;
								DisSupportAmt = (RebateSupportAmt * 100 / RebateSupportAmount) * DisSupport / 100;
							}
							Row row = sheet.createRow(rows);
							row.createCell(0).setCellValue(Brand);
							row.getCell(0).setCellStyle(customstylewithLock);
							row.createCell(1).setCellValue(Variant);
//							row.getCell(1).setCellStyle(createLockedStyle(workbook));
							row.getCell(1).setCellStyle(customstylewithLock);
//							
//							row.createCell(2);
//							row.getCell(2).setCellStyle(customstylewithLock);
//							

							row.createCell(2).setCellValue(ModelCode);
							row.getCell(2).setCellStyle(customstylewithLock);
							row.createCell(3).setCellValue(ModelYear);
							row.getCell(3).setCellStyle(customstylewithLock);
							row.createCell(4).setCellValue(Tenure);
							row.getCell(4).setCellStyle(customstylewithLock);
							row.createCell(5).setCellValue(GracePeriod);
							row.getCell(5).setCellStyle(customstylewithLock);
							row.createCell(6).setCellValue(ModelName);
							row.getCell(6).setCellStyle(customstylewithLock);
							row.createCell(7).setCellValue(ModelSuffix);
							row.getCell(7).setCellStyle(customstylewithLock);
							Cell retail = row.createCell(8);
							retail.setCellValue(RetailPrice);
							retail.setCellStyle(customstylewithLock);

							row.createCell(9).setCellValue(WSSupportAmt); // Whoelsaler
							row.getCell(9).setCellStyle(customstylewithLock);
							row.createCell(10).setCellValue(DisSupportAmt); // Distributor
							row.getCell(10).setCellStyle(customstylewithLock);

							Cell CashBack = row.createCell(11);
							CashBack.setCellValue(Cashback);
							CashBack.setCellStyle(customstylewithLock);

							Cell PurchaseDiscount = row.createCell(12);
							PurchaseDiscount.setCellStyle(customstylewithLock);

							Cell FormulaPrice = row.createCell(13);
							FormulaPrice.setCellFormula("I" + (row.getRowNum() + 1) + "-L" + (row.getRowNum() + 1)
									+ "-M" + (row.getRowNum() + 1)); // FormulaPrice
							evaluator.evaluateFormulaCell(FormulaPrice);
//							FormulaPrice.setCellStyle(customstylewithLock);

							Cell Finance = row.createCell(14);
							Finance.setCellFormula("(N" + (row.getRowNum() + 1) + "-R" + (row.getRowNum() + 1) + ")*X"
									+ (row.getRowNum() + 1) + "*((E" + (row.getRowNum() + 1) + "+F"
									+ (row.getRowNum() + 1) + ")/12)"); // Finance
							evaluator.evaluateFormulaCell(Finance);
//							Finance.setCellStyle(customstylewithLock);

							Cell Insurance = row.createCell(15);
							Insurance.setCellFormula("(N" + (row.getRowNum() + 1) + "*Y" + (row.getRowNum() + 1)
									+ ")*((E" + (row.getRowNum() + 1) + "+F" + (row.getRowNum() + 1) + ")/12)"); // Insurance
							evaluator.evaluateFormulaCell(Insurance);
//							Insurance.setCellStyle(customstylewithLock);

							Cell TotalAmount = row.createCell(16);
							TotalAmount.setCellFormula("N" + (row.getRowNum() + 1) + "+O" + (row.getRowNum() + 1) + "+P"
									+ (row.getRowNum() + 1)); // TotalAmount
							evaluator.evaluateFormulaCell(TotalAmount);
//							TotalAmount.setCellStyle(customstylewithLock);

							Cell downpayment = row.createCell(17);
							downpayment.setCellValue(Downpayment);
							downpayment.setCellStyle(customstylewithLock);

							Cell FinanceAmount = row.createCell(18);
							FinanceAmount.setCellFormula("Q" + (row.getRowNum() + 1) + "-R" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(FinanceAmount);
//							FinanceAmount.setCellStyle(customstylewithLock);

							Cell MI = row.createCell(19);
							MI.setCellFormula("ROUND(((S" + (row.getRowNum() + 1) + "-U" + (row.getRowNum() + 1) + ")/E"
									+ (row.getRowNum() + 1) + ")+0.49,0)");
							evaluator.evaluateFormulaCell(MI);
//							MI.setCellStyle(customstylewithLock);

							Cell RV = row.createCell(20);
							RV.setCellFormula("N" + (row.getRowNum() + 1) + "*V" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(RV);
//							RV.setCellStyle(customstylewithLock);

							Cell RVPercentage = row.createCell(21);
							RVPercentage.setCellValue(RVPercent);
							RVPercentage.setCellStyle(customPercentagestylewithlock);

							Cell AdminFee = row.createCell(22);
							AdminFee.setCellFormula("1%*N" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(AdminFee);
//							AdminFee.setCellStyle(customstylewithLock);

							Cell FIN = row.createCell(23);
							FIN.setCellValue(FinancePercent);
							FIN.setCellStyle(customPercentagestylewithlock);

							Cell InsurancePercentage = row.createCell(24);
							InsurancePercentage.setCellValue(InsurancePercent);
							InsurancePercentage.setCellStyle(customPercentagestylewithlock);

							Cell TotalPercent = row.createCell(25);
							TotalPercent.setCellFormula("X" + (row.getRowNum() + 1) + "+Y" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(TotalPercent);
//							TotalPercent.setCellStyle(customPercentagestylewithlock);

							Cell AdminFeeDiscount = row.createCell(26);

							AdminFeeDiscount.setCellStyle(customstylewithLock);

							Cell FirstrandomZ = row.createCell(27);
							FirstrandomZ.setCellStyle(customstylewithLock);

							Cell SecondrandomAA = row.createCell(28);
							SecondrandomAA.setCellStyle(customstylewithLock);

							Cell ThirdrandomAB = row.createCell(29);
							ThirdrandomAB.setCellStyle(customstylewithLock);

							// Newly added for 4,5,6 installment start
							Cell forthrandomAC = row.createCell(30);
							forthrandomAC.setCellStyle(customstylewithLock);

							Cell fifthrandomAD = row.createCell(31);
							fifthrandomAD.setCellStyle(customstylewithLock);

							Cell sixthrandomAE = row.createCell(32);
							sixthrandomAE.setCellStyle(customstylewithLock);

							Cell TotalAF = row.createCell(33);
							TotalAF.setCellFormula(
									"SUM(AB" + (row.getRowNum() + 1) + ":AG" + (row.getRowNum() + 1) + ")");
							evaluator.evaluateFormulaCell(TotalAF);
//							TotalAF.setCellStyle(customstylewithLock);

							Cell TotalRebateDiscount = row.createCell(34);
							TotalRebateDiscount.setCellFormula("M" + (row.getRowNum() + 1) + "+AH"
									+ (row.getRowNum() + 1) + "+AA" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(TotalRebateDiscount);
//							TotalRebateDiscount.setCellStyle(customstylewithLock);

							Cell Difference = row.createCell(35);
							Difference.setCellFormula(
									"ABS(AI" + (row.getRowNum() + 1) + "-AL" + (row.getRowNum() + 1) + ")");

							evaluator.evaluateFormulaCell(Difference);
//							Difference.setCellStyle(customstylewithLock);

							Cell Rebate = row.createCell(36);
							Rebate.setCellFormula("AL" + (row.getRowNum() + 1) + "/I" + (row.getRowNum() + 1));
							evaluator.evaluateFormulaCell(Rebate);
//							Rebate.setCellStyle(customPercentagestylewithlock);

							Cell RebateAmount = row.createCell(37);
							RebateAmount.setCellValue(RebateSupportAmt);
							RebateAmount.setCellStyle(customstylewithLock);
							JSONArray arry = new JSONArray();
							try {
								if ("Yes".equalsIgnoreCase(objIForm.getValue("MonthlywithVAT").toString())) {
									arry = (JSONArray) objIForm.getValue("InstalmentWaiverWithVAT");
								} else if ("No".equalsIgnoreCase(objIForm.getValue("MonthlywithVAT").toString())) {
									arry = (JSONArray) objIForm.getValue("InstalmentWaiverWithoutVAT");
								}
								NGLog.consoleLog("Array converted : " + arry);
							} catch (Exception e) {
								NGLog.errorLog("Error in conversion : " + e);
							}
							double rebateamt = RebateAmount.getNumericCellValue();
							double Admindiscount = 0.0;
							double MIValue = MI.getNumericCellValue();
							double remAmount = 0.0;
							NGLog.consoleLog("RebateAmount : " + rebateamt);
							NGLog.consoleLog("MIValue : " + MIValue);
							HashMap<Object, Double> data = new HashMap<>();
							switch (AppPriority1) {
							case "101": // Admin Fee
								evaluator.evaluateAll();
								MIValue = MI.getNumericCellValue();
								Admindiscount = AdminFee.getNumericCellValue();
								if (rebateamt >= AdminFee.getNumericCellValue()) {
									AdminFeeDiscount.setCellValue(AdminFee.getNumericCellValue());
								} else {
									AdminFeeDiscount.setCellValue(rebateamt);
								}
								if ("102".equalsIgnoreCase(AppPriority2)) {
									if (!arry.isEmpty()) {
										double remainingAmount = 0.0;
										remainingAmount = rebateamt - AdminFeeDiscount.getNumericCellValue();
										for (Object obj : arry) {
											if (remainingAmount > MIValue) {
												data.put(obj, MIValue);
												remainingAmount -= MIValue;
											} else {
												data.put(obj, remainingAmount);
												remainingAmount -= remainingAmount;
											}
										}
										NGLog.consoleLog("data Hashmap : " + data);
										FirstrandomZ.setCellValue(Optional.ofNullable(data.get("1")).orElse(0.0));
										SecondrandomAA.setCellValue(Optional.ofNullable(data.get("2")).orElse(0.0));
										ThirdrandomAB.setCellValue(Optional.ofNullable(data.get("3")).orElse(0.0));
										forthrandomAC.setCellValue(Optional.ofNullable(data.get("4")).orElse(0.0));
										fifthrandomAD.setCellValue(Optional.ofNullable(data.get("5")).orElse(0.0));
										sixthrandomAE.setCellValue(Optional.ofNullable(data.get("6")).orElse(0.0));
										if (remainingAmount > 0 && "100".equalsIgnoreCase(AppPriority3)) {
											if (RetailPurchaseDiscount <= 0) {
												PurchaseDiscount.setCellValue(remainingAmount);
											} else if (remainingAmount <= RetailPurchaseDiscount) {
												PurchaseDiscount.setCellValue(remainingAmount);
											} else {
												PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
											}
										}
									} else {
										if ((rebateamt - Admindiscount) > 0 && "100".equalsIgnoreCase(AppPriority3)) {
											if (RetailPurchaseDiscount <= 0) {
												PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
											} else if ((rebateamt - Admindiscount) <= RetailPurchaseDiscount) {
												PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
											} else {
												PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
											}
											// PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
										}
									}
								} else if ("100".equalsIgnoreCase(AppPriority2)) {
									if ((rebateamt - Admindiscount) > 0) {
										if (RetailPurchaseDiscount <= 0) {
											PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
											remAmount = 0.0;
										} else if ((rebateamt - Admindiscount) <= RetailPurchaseDiscount) {
											PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
											remAmount = 0.0;
										} else {
											PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
											remAmount = rebateamt - Admindiscount - RetailPurchaseDiscount;
										}
//										PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
										if (remAmount > 0) {
											for (Object obj : arry) {
												if (remAmount > MIValue) {
													data.put(obj, MIValue);
													remAmount -= MIValue;
												} else {
													data.put(obj, remAmount);
													remAmount -= remAmount;
												}
											}
											FirstrandomZ.setCellValue(Optional.ofNullable(data.get("1")).orElse(0.0));
											SecondrandomAA.setCellValue(Optional.ofNullable(data.get("2")).orElse(0.0));
											ThirdrandomAB.setCellValue(Optional.ofNullable(data.get("3")).orElse(0.0));
											forthrandomAC.setCellValue(Optional.ofNullable(data.get("4")).orElse(0.0));
											fifthrandomAD.setCellValue(Optional.ofNullable(data.get("5")).orElse(0.0));
											sixthrandomAE.setCellValue(Optional.ofNullable(data.get("6")).orElse(0.0));
										}
									}
								}
								break;
							case "102": // Installment
								evaluator.evaluateAll();
								MIValue = MI.getNumericCellValue();
								for (Object obj : arry) {
									if (rebateamt > MIValue) {
										data.put(obj, MIValue);
										rebateamt -= MIValue;
									} else {
										data.put(obj, rebateamt);
										rebateamt -= rebateamt;
									}
								}
								NGLog.consoleLog("data Hashmap : " + data);
								FirstrandomZ.setCellValue(Optional.ofNullable(data.get("1")).orElse(0.0));
								SecondrandomAA.setCellValue(Optional.ofNullable(data.get("2")).orElse(0.0));
								ThirdrandomAB.setCellValue(Optional.ofNullable(data.get("3")).orElse(0.0));
								forthrandomAC.setCellValue(Optional.ofNullable(data.get("4")).orElse(0.0));
								fifthrandomAD.setCellValue(Optional.ofNullable(data.get("5")).orElse(0.0));
								sixthrandomAE.setCellValue(Optional.ofNullable(data.get("6")).orElse(0.0));
								if ("101".equalsIgnoreCase(AppPriority2)) {
									if (rebateamt >= AdminFee.getNumericCellValue()) {
										AdminFeeDiscount.setCellValue(AdminFee.getNumericCellValue());
										rebateamt -= AdminFee.getNumericCellValue();
									} else {
										AdminFeeDiscount.setCellValue(rebateamt);
										rebateamt -= rebateamt;
									}
									if ("100".equalsIgnoreCase(AppPriority3)) {
										if (RetailPurchaseDiscount <= 0) {
											PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
										} else if ((rebateamt - Admindiscount) <= RetailPurchaseDiscount) {
											PurchaseDiscount.setCellValue(rebateamt - Admindiscount);
										} else {
											PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
										}
									}
//									PurchaseDiscount.setCellValue(rebateamt);
								} else if ("100".equalsIgnoreCase(AppPriority2)) {
									if (RetailPurchaseDiscount <= 0) {
										PurchaseDiscount.setCellValue(rebateamt);
										rebateamt = 0.0;
									} else if ((rebateamt) <= RetailPurchaseDiscount) {
										PurchaseDiscount.setCellValue(rebateamt);
										rebateamt = 0.0;
									} else {
										PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
										rebateamt -= RetailPurchaseDiscount;
									}
									if (rebateamt >= AdminFee.getNumericCellValue()) {
										AdminFeeDiscount.setCellValue(AdminFee.getNumericCellValue());
//										rebateamt -= AdminFee.getNumericCellValue();
									} else {
										AdminFeeDiscount.setCellValue(rebateamt);
//										rebateamt -= rebateamt;
									}
//									PurchaseDiscount.setCellValue(rebateamt);
								}
								break;
							case "100": // Price of Vehicle
								if (RetailPurchaseDiscount <= 0) {
									PurchaseDiscount.setCellValue(rebateamt);
									rebateamt = 0.0;
								} else if ((rebateamt) <= RetailPurchaseDiscount) {
									PurchaseDiscount.setCellValue(rebateamt);
									rebateamt = 0.0;
								} else {
									PurchaseDiscount.setCellValue(RetailPurchaseDiscount);
									rebateamt -= RetailPurchaseDiscount;
								}
								// Newly added last
								evaluator.evaluateAll();
								MIValue = MI.getNumericCellValue();

//								PurchaseDiscount.setCellValue(rebateamt);
								if ("101".equalsIgnoreCase(AppPriority2)) {
									if (rebateamt >= AdminFee.getNumericCellValue()) {
										AdminFeeDiscount.setCellValue(AdminFee.getNumericCellValue());
										rebateamt -= AdminFee.getNumericCellValue();
									} else {
										AdminFeeDiscount.setCellValue(rebateamt);
										rebateamt -= rebateamt;
									}
									for (Object obj : arry) {
										if (rebateamt > MIValue) {
											data.put(obj, MIValue);
											rebateamt -= MIValue;
										} else {
											data.put(obj, rebateamt);
											rebateamt -= rebateamt;
										}
									}
									NGLog.consoleLog("data Hashmap : " + data);
									FirstrandomZ.setCellValue(Optional.ofNullable(data.get("1")).orElse(0.0));
									SecondrandomAA.setCellValue(Optional.ofNullable(data.get("2")).orElse(0.0));
									ThirdrandomAB.setCellValue(Optional.ofNullable(data.get("3")).orElse(0.0));
									forthrandomAC.setCellValue(Optional.ofNullable(data.get("4")).orElse(0.0));
									fifthrandomAD.setCellValue(Optional.ofNullable(data.get("5")).orElse(0.0));
									sixthrandomAE.setCellValue(Optional.ofNullable(data.get("6")).orElse(0.0));
								} else if ("102".equalsIgnoreCase(AppPriority2)) {
									for (Object obj : arry) {
										if (rebateamt > MIValue) {
											data.put(obj, MIValue);
											rebateamt -= MIValue;
										} else {
											data.put(obj, rebateamt);
											rebateamt -= rebateamt;
										}
									}
									NGLog.consoleLog("data Hashmap : " + data);
									FirstrandomZ.setCellValue(Optional.ofNullable(data.get("1")).orElse(0.0));
									SecondrandomAA.setCellValue(Optional.ofNullable(data.get("2")).orElse(0.0));
									ThirdrandomAB.setCellValue(Optional.ofNullable(data.get("3")).orElse(0.0));
									forthrandomAC.setCellValue(Optional.ofNullable(data.get("4")).orElse(0.0));
									fifthrandomAD.setCellValue(Optional.ofNullable(data.get("5")).orElse(0.0));
									sixthrandomAE.setCellValue(Optional.ofNullable(data.get("6")).orElse(0.0));
									if (rebateamt >= AdminFee.getNumericCellValue()) {
										AdminFeeDiscount.setCellValue(AdminFee.getNumericCellValue());
									} else {
										AdminFeeDiscount.setCellValue(rebateamt);
									}
								}
								break;
							default:
								break;
							}
							FormulaPrice.setCellStyle(customstylewithLock);
							Finance.setCellStyle(customstylewithLock);
							Insurance.setCellStyle(customstylewithLock);
							TotalAmount.setCellStyle(customstylewithLock);
							FinanceAmount.setCellStyle(customstylewithLock);
							MI.setCellStyle(customstylewithLock);
							RV.setCellStyle(customstylewithLock);
							AdminFee.setCellStyle(customstylewithLock);
							TotalPercent.setCellStyle(customPercentagestylewithlock);
							TotalAF.setCellStyle(customstylewithLock);
							TotalRebateDiscount.setCellStyle(customstylewithLock);
							Difference.setCellStyle(customstylewithLock);
							Rebate.setCellStyle(customPercentagestylewithlock);
							for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
								sheet.autoSizeColumn(j);
							}
							rows++;
						}
					}
				}
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in createRow");
		}
		return rows;
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
			System.out.println("Current date returned : " + FormattedDate);
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
}
