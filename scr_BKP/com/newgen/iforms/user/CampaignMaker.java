package com.newgen.iforms.user;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.iforms.user.common.NGCommon;
import com.newgen.iforms.user.common.NGLog;
import com.newgen.iforms.user.repository.LoadMessagesOuter;
import com.newgen.mvcbeans.model.WorkdeskModel;

public class CampaignMaker implements IFormServerEventHandler {
	NGCommon objCommon = null;
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		NGLog.consoleLog("Inside beforeFormLoad Start of CampaignMaker");
		ifr.setValue("ApplicationNo", ifr.getObjGeneralData().getM_strProcessInstanceId());
		ifr.setValue("UserName", ifr.getObjGeneralData().getM_strUserName());
		ifr.setValue("UserID", ifr.getObjGeneralData().getM_strUserPersonalName());
		ifr.setValue("CampaignCreatedBy", ifr.getObjGeneralData().getM_strIntroducedBy());
		if ("".equalsIgnoreCase(ifr.getValue("MakerEmail").toString())) {
			NGLog.consoleLog("Maker Index : " + ifr.getObjGeneralData().getM_strUserIndex());
			ifr.setValue("MakerEmail", getEmailID(ifr.getObjGeneralData().getM_strUserIndex(), ifr));
		}
		NGLog.consoleLog("Inside beforeFormLoad End of CampaignMaker");
	}

	public String getEmailID(String UserIndex, IFormReference ifr) {
		NGLog.consoleLog("Inside getEmailID");
		String EmailId = "";
		String sEmailQuery = "SELECT MailId FROM PDBUser(NOLOCK) WHERE UserIndex='" + UserIndex + "' ";
		List<List<String>> sEmailQueryResult = null;
		try {
			sEmailQueryResult = ifr.getDataFromDB(sEmailQuery);
			NGLog.consoleLog("sEmailQueryResult : " + sEmailQueryResult);
			if (sEmailQueryResult != null && !sEmailQueryResult.isEmpty()) {
				EmailId = sEmailQueryResult.get(0).get(0);
			}
		} catch (Exception e) {
			NGLog.errorLog("Error in getEmailID");
		}
		NGLog.consoleLog("Email ID : " + EmailId);
		return EmailId;
	}

	@Override
	public String executeCustomService(FormDef arg0, IFormReference arg1, String arg2, String arg3, String arg4) {
		return "";
	}

	@Override
	public JSONArray executeEvent(FormDef arg0, IFormReference arg1, String arg2, String arg3) {

		return null;
	}

	@Override
	public String executeServerEvent(IFormReference ifr, String control, String event, String value) {
		NGLog.consoleLog("Inside executeServerEvent on CampaignMaker-> control : " + control + " , event : " + event
				+ " , value : " + value);
		NGCommon objCommon = new NGCommon();
		switch (event) {
		case "Change": {
			switch (control) {
			case "onChangeVehicleBrand": {
				NGLog.consoleLog("Inside onChangeVehicleBrand");
				return objCommon.onChangeVehicleBrand(ifr, value);
			}
			case "onChangeProductType": {
				NGLog.consoleLog("Inside onChangeProductType");
				return objCommon.onChangeProductType(ifr, value);
			}
			case "onChangeProvince": {
				NGLog.consoleLog("Inside onChangeProvince");
				return objCommon.onChangeProvince(ifr, value);
			}
			case "onChangeWholesaler": {
				NGLog.consoleLog("Inside onChangeWholesaler");
				return objCommon.onChangeWholesaler(ifr, value);
			}
			case "onChangeModelYear": {
				NGLog.consoleLog("Inside onChangeModelYear");
				return objCommon.onChangeModelYear(ifr, value);
			}
			case "onChangeModel": {
				NGLog.consoleLog("Inside onChangeModel");
				return objCommon.onChangeModel(ifr, value);
			}
			case "generateCampCode": {
				NGLog.consoleLog("Inside generateCampCode");
				return objCommon.generateCampCode(ifr, value);
			}
			case "onChangeCopyCampCode": {
				NGLog.consoleLog("Inside onChangeCopyCampCode");
				return objCommon.onChangeCopyCampCode(ifr, value);
			}
			case "InsertExcelData": {
				NGLog.consoleLog("Inside InsertExcelData");
				return objCommon.InsertExcelDatadeserialized(ifr);
			}
			case "onChangePriority1": {
				NGLog.consoleLog("Inside onChangePriority1");
				return objCommon.onChangePriority1(ifr, value);
			}
			case "onChangePriority2": {
				NGLog.consoleLog("Inside onChangePriority2");
				return objCommon.onChangePriority2(ifr, value);
			}
			case "LoadMessages": {
				NGLog.consoleLog("Calling outer static class");
				if (value == null || value == "") {
					NGLog.consoleLog("Language not found.");
					return "failure";
				}
				JSONArray jsonLoadMessages = LoadMessagesOuter.LoadMsgs.fetchMsgs(ifr, value);
				if (jsonLoadMessages == null) {
					return "failure";
				} else {
					return jsonLoadMessages.toJSONString();
				}
			}
			case "onAddDeleteVehicleGrid": {
				NGLog.consoleLog("Inside onAddDeleteVehicleGrid");
				return objCommon.onAddDeleteVehicleGrid(ifr, value);
			}
			case "onChangeCampaignName": {
				NGLog.consoleLog("Inside onChangeCampaignName");
				return objCommon.onChangeCampaignName(ifr, value);
			}
			case "onChangePromoCode": {
				NGLog.consoleLog("Inside onChangeCampaignName");
				return objCommon.onChangePromoCode(ifr, value);
			}
			case "getFinanceRateLimit": {
				NGLog.consoleLog("Inside getFinanceRateLimit");
				return objCommon.getFinanceRateLimit(ifr);
			}
			}
		}
		case "Click": {
			switch (control) {
			case "populateDocumentMaster": {
				NGLog.consoleLog("Inside populateDocumentMaster");
				return objCommon.populateDocumentMaster(ifr, value);
			}
			case "getExcelData": {
				NGLog.consoleLog("Inside getExcelData");
				return objCommon.getExcelData(ifr);
			}
			case "getCurrentDate": {
				NGLog.consoleLog("Inside getCurrentDate");
				return objCommon.getCurrentDateTime();
			}
			}
		}
		}
		return "";
	}

	@Override
	public String generateHTML(IFormReference arg0, EControl arg1) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getCustomFilterXML(FormDef arg0, IFormReference arg1, String arg2) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getWidgetNameToBeShown(IFormReference arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean introduceWorkItemInSpecificProcess(IFormReference arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			WorkdeskModel arg3) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String onChangeEventServerSide(IFormReference arg0, String arg1) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String postHookExportToPDF(IFormReference arg0, File arg1) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void postHookOnDocumentOperations(IFormReference arg0, String arg1, String arg2, int arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHookOnDocumentUpload(IFormReference arg0, String arg1, String arg2, File arg3, int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public String setMaskedValue(IFormReference arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return arg2;
	}

	@Override
	public void updateDataInWidget(IFormReference arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public String validateDocumentConfiguration(IFormReference arg0, String arg1, String arg2, File arg3, Locale arg4) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public JSONArray validateSubmittedForm(FormDef arg0, IFormReference arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
