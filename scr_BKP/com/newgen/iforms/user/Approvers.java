package com.newgen.iforms.user;

import java.io.File;
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

public class Approvers implements IFormServerEventHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeFormLoad(FormDef arg0, IFormReference ifr) {
		NGLog.consoleLog("Inside beforeFormLoad Start of Approvers");
		ifr.setValue("ApplicationNo", ifr.getObjGeneralData().getM_strProcessInstanceId());
		ifr.setValue("UserName", ifr.getObjGeneralData().getM_strUserName());
		ifr.setValue("UserID", ifr.getObjGeneralData().getM_strUserPersonalName());
		NGLog.consoleLog("Inside beforeFormLoad End of Approvers");
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
		NGLog.consoleLog("Inside executeServerEvent on Approvers-> control : " + control + " , event : " + event
				+ " , value : " + value);

		NGCommon objCommon = new NGCommon();
		switch (event) {
		case "Change": {
			switch (control) {
			case "onChangePriority1": {
				NGLog.consoleLog("Inside onChangePriority1");
				return objCommon.onChangePriority1(ifr, value);
			}
			case "onChangePriority2": {
				NGLog.consoleLog("Inside onChangePriority2");
				return objCommon.onChangePriority2(ifr, value);
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
			case "onChangeVehicleBrand": {
				NGLog.consoleLog("Inside onChangeVehicleBrand");
				return objCommon.onChangeVehicleBrand(ifr, value);
			}
			case "onChangeModelYear": {
				NGLog.consoleLog("Inside onChangeModelYear");
				return objCommon.onChangeModelYear(ifr, value);
			}
			case "onChangeModel": {
				NGLog.consoleLog("Inside onChangeModel");
				return objCommon.onChangeModel(ifr, value);
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
			}
		}
		case "Click": {
			switch (control) {
			case "getExcelData": {
				NGLog.consoleLog("Inside getExcelData");
				return objCommon.getExcelData(ifr);
			}
			case "getCurrentDate": {
				NGLog.consoleLog("Inside getCurrentDate");
				return objCommon.getCurrentDateTime();
			}
			default: {
				return "";
			}
			}
		}
		}
		return "";
	}

	@Override
	public String generateHTML(IFormReference arg0, EControl arg1) {

		return "";
	}

	@Override
	public String getCustomFilterXML(FormDef arg0, IFormReference arg1, String arg2) {

		return "";
	}

	@Override
	public String getWidgetNameToBeShown(IFormReference arg0) {

		return "";
	}

	@Override
	public boolean introduceWorkItemInSpecificProcess(IFormReference arg0, String arg1) {

		return false;
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2) {

		return "";
	}

	@Override
	public String introduceWorkItemInWorkFlow(IFormReference arg0, HttpServletRequest arg1, HttpServletResponse arg2,
			WorkdeskModel arg3) {

		return "";
	}

	@Override
	public String onChangeEventServerSide(IFormReference arg0, String arg1) {

		return "";
	}

	@Override
	public String postHookExportToPDF(IFormReference arg0, File arg1) {

		return "";
	}

	@Override
	public void postHookOnDocumentOperations(IFormReference arg0, String arg1, String arg2, int arg3, String arg4) {

	}

	@Override
	public void postHookOnDocumentUpload(IFormReference arg0, String arg1, String arg2, File arg3, int arg4) {

	}

	@Override
	public String setMaskedValue(IFormReference arg0, String arg1, String arg2) {

		return arg2;
	}

	@Override
	public void updateDataInWidget(IFormReference arg0, String arg1) {

	}

	@Override
	public String validateDocumentConfiguration(IFormReference arg0, String arg1, String arg2, File arg3, Locale arg4) {

		return "";
	}

	@Override
	public JSONArray validateSubmittedForm(FormDef arg0, IFormReference arg1, String arg2) {

		return null;
	}
}
