var sActivityName = getWorkItemData('activityname');
//added by Yash for fetching locale value
var selectedLocale = '_' + iformLocale.replace('/', '');
var rows = [];
// Added by Yash 16-11-2023 (starts)
var importedDocumentJs = document.createElement("script");
importedDocumentJs.src = "/ALJFS_Campaign/Campaign/CustomJS/Documentation.js?sid=" +
	getWorkItemData("sessionId");
document.head.appendChild(importedDocumentJs);
// Added by Yash 16-11-2023 (Ends)
var importedMessagesJs = document.createElement("script");
importedMessagesJs.src = "/ALJFS_Campaign/Campaign/CustomJS/Messages.js?sid=" +
	getWorkItemData("sessionId");
document.head.appendChild(importedMessagesJs);
loadCSS();
var GlobalRowIndex;
var GlobalCampaignType;
var GlobalRequestType;
var GlobalProductType;
var clickedcount = 0;
function customValidation(op) {
	switch (op) {
		case 'S':

			break;
		case 'I':

			break;
		case 'D':

			break;
		default:
			break;
	}

	return true;
}

function formLoad() {
	//clearValue('Decision');
	clearValue('Comments');
	GlobalCampaignType = getValue('CampaignType');
	GlobalRequestType = getValue('RequestType');
	GlobalProductType = getValue('ProductType');
	try {
		setHeaderBackground();
		loadMessages();
		document.getElementById('VehicleGrid_selectedColumn').style.width = "160px"
		document.getElementById('VehicleGrid_selectedColumn').style.marginLeft = "-50px"
		if (getGridRowCount('VehicleGrid') > 0) {
			document.getElementById('VehicleGriddiv_pad').style.display = 'none';
		} else {
			document.getElementById('VehicleGriddiv_pad').style.display = 'block';
		}
		//new

		//new end
		var CurrentStage = getWorkItemData('ActivityName');
		setValues({ 'CurrentStatus': CurrentStage }, true);
		// setValues({'UserName':getWorkItemData("username")},true);
		onChangeProductType('Load');
		onChangeRequestType('');
		onChangeCampaignType('Load');
		onChangeAppropriation('Load');
		onChangePriority2('Load');
		onChangeProvince();
		onChangeWholesaler();
		formdisable();
		var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
		for (var i = 0; i < callfrom.length; i++) {
			onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
		}
	} catch (err) {
		console.log('Error in formLoad: ' + err);
	}
}

function onRowClick(tableId, rowIndex) {
	if (tableId == 'VehicleGrid') {
		GlobalRowIndex = rowIndex;
	}
	return true;
}

function customListViewValidation(controlId, flag) {
	if (controlId == 'VehicleGrid') {
		saveWorkItem();
		var result = executeServerEvent('onAddDeleteVehicleGrid', 'Change', GlobalRowIndex + "~" + getValue('VehicleGrid_Model') + "~" + flag, true);
		if (result.split('~')[0] == 'F') {
			ShowToastErrorHandleDup(fetchMsg(118));
			return false;
		}
	}
	return true;
}
function onChangeAmountType(CalledFrom) {
	console.log('Inside onChangeAmountType');
	if (CalledFrom == 'Change') {
		clearValue('VehicleGrid_DistributorSupportAmount');
		clearValue('VehicleGrid_WSSupportAmount');
		clearValue('VehicleGrid_ALJFSSupportAmount');
	}
	if (getValue('VehicleGrid_SupportAmounttobe') == 'Absolute value') {
		setStyle('VehicleGrid_DistributorSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_WSSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_MaximumSupportAmount', 'visible', 'true');
		document.getElementById('VehicleGrid_DistributorSupportAmount_label').innerHTML = 'Distributor Support Amount';
		document.getElementById('VehicleGrid_WSSupportAmount_label').innerHTML = 'WS Support Amount';
		document.getElementById('VehicleGrid_ALJFSSupportAmount_label').innerHTML = 'ALJFS Support Amount';
		setStyle('VehicleGrid_DistributorSupportAmount', 'mandatory', 'true');
		setStyle('VehicleGrid_WSSupportAmount', 'mandatory', 'true');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'mandatory', 'true');
	} else if (getValue('VehicleGrid_SupportAmounttobe') == 'Percentage') {
		setStyle('VehicleGrid_DistributorSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_WSSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'visible', 'true');
		setStyle('VehicleGrid_MaximumSupportAmount', 'visible', 'true');
		document.getElementById('VehicleGrid_DistributorSupportAmount_label').innerHTML = 'Distributor Support Amount (%)';
		document.getElementById('VehicleGrid_WSSupportAmount_label').innerHTML = 'WS Support Amount (%)';
		document.getElementById('VehicleGrid_ALJFSSupportAmount_label').innerHTML = 'ALJFS Support Amount (%)';
		setStyle('VehicleGrid_DistributorSupportAmount', 'mandatory', 'true');
		setStyle('VehicleGrid_WSSupportAmount', 'mandatory', 'true');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'mandatory', 'true');
	} else {
		setStyle('VehicleGrid_DistributorSupportAmount', 'mandatory', 'false');
		setStyle('VehicleGrid_WSSupportAmount', 'mandatory', 'false');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'mandatory', 'false');
		setStyle('VehicleGrid_DistributorSupportAmount', 'visible', 'false');
		setStyle('VehicleGrid_WSSupportAmount', 'visible', 'false');
		setStyle('VehicleGrid_ALJFSSupportAmount', 'visible', 'false');
		setStyle('VehicleGrid_MaximumSupportAmount', 'visible', 'false');
	}
}

function listViewLoad(controlId, action) {
	if (controlId == 'VehicleGrid') {
		if (action == 'M') {
			onChangeModelYear();
			onChangeModel();
			if (getValue('CampaignType') == 'Joint Pricing Campaign') {
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'true');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'true');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'true');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'true');
				setStyle('VehicleGrid_Model', 'mandatory', 'true');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'true');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'mandatory', 'true');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'true');
				setStyle('VehicleGrid_Cashback', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'true');
				onChangeAmountType('');
			} else if (getValue('CampaignType') == '50-50 Campaign') {
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'true');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'true');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'true');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'true');
				setStyle('VehicleGrid_Model', 'mandatory', 'true');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'true');
				setStyle('VehicleGrid_DownPayment', 'disable', 'true');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'mandatory', 'false');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'false');
				setStyle('VehicleGrid_Cashback', 'visible', 'false');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_RVPercentage', 'disable', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'false');
				document.getElementById('VehicleGrid_DownPayment_label').innerHTML = 'Down Payment (%)';
				setValues({ 'VehicleGrid_DownPayment': '50' }, true);
				setValues({ 'VehicleGrid_RVPercentage': '50.00' }, true);
				onChangeAmountType('');
			} else {
				setStyle('VehicleGrid_Cashback', 'visible', 'false');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'false');
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'false');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'false');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'false');
				setStyle('VehicleGrid_Model', 'mandatory', 'false');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'false');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_MaximumSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_ALJFSSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'false');
				setStyle('VehicleGrid_SupportAmounttobe', 'mandatory', 'false');
				setStyle('VehicleGrid_WSSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_DistributorSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'false');
				setStyle('VehicleGrid_ALJFSSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_WSSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_DistributorSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'visible', 'false');
				setStyle('VehicleGrid_InsurancePercentage', 'visible', 'false');
				setStyle('VehicleGrid_FinancePercentage', 'visible', 'false');
				setStyle('VehicleGrid_Tenure', 'visible', 'false');
				setStyle('VehicleGrid_GracePeriod', 'visible', 'false');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'false');
			}
		} else if (action == 'A') {
			if (getValue('CampaignType') == 'Joint Pricing Campaign') {
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'true');
				setStyle('VehicleGrid_Cashback', 'visible', 'true');
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'true');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'true');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'true');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'true');
				setStyle('VehicleGrid_Model', 'mandatory', 'true');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'true');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'mandatory', 'true');
				onChangeAmountType('');
			} else if (getValue('CampaignType') == '50-50 Campaign') {
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'true');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'true');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'true');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'true');
				setStyle('VehicleGrid_Model', 'mandatory', 'true');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'true');
				setStyle('VehicleGrid_DownPayment', 'disable', 'true');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'mandatory', 'false');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'false');
				setStyle('VehicleGrid_Cashback', 'visible', 'false');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'true');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'true');
				setStyle('VehicleGrid_RVPercentage', 'disable', 'true');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'false');
				document.getElementById('VehicleGrid_DownPayment_label').innerHTML = 'Down Payment (%)';
				setValues({ 'VehicleGrid_DownPayment': '50' }, true);
				setValues({ 'VehicleGrid_RVPercentage': '50.00' }, true);
				onChangeAmountType('');
			} else {
				setStyle('VehicleGrid_Cashback', 'visible', 'false');
				setStyle('VehicleGrid_RetailPurchaseDiscount', 'visible', 'false');
				setStyle('VehicleGrid_MaximumSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_VehicleBrand', 'mandatory', 'true');
				setStyle('VehicleGrid_Variant', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelYear', 'mandatory', 'false');
				setStyle('VehicleGrid_Tenure', 'mandatory', 'false');
				setStyle('VehicleGrid_GracePeriod', 'mandatory', 'false');
				setStyle('VehicleGrid_Model', 'mandatory', 'false');
				setStyle('VehicleGrid_Colour', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelSuffix', 'mandatory', 'false');
				setStyle('VehicleGrid_ModelCode', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'mandatory', 'false');
				setStyle('VehicleGrid_InsurancePercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_FinancePercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_DownPayment', 'visible', 'false');
				setStyle('VehicleGrid_InsurancePercentage', 'visible', 'false');
				setStyle('VehicleGrid_FinancePercentage', 'visible', 'false');
				setStyle('VehicleGrid_Tenure', 'visible', 'false');
				setStyle('VehicleGrid_GracePeriod', 'visible', 'false');
				setStyle('VehicleGrid_ALJFSSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_WSSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_DistributorSupportAmount', 'visible', 'false');
				setStyle('VehicleGrid_RVPercentage', 'visible', 'false');
				setStyle('VehicleGrid_ALJFSSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_WSSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_DistributorSupportAmount', 'mandatory', 'false');
				setStyle('VehicleGrid_RVPercentage', 'mandatory', 'false');
				setStyle('VehicleGrid_SupportAmounttobe', 'visible', 'false');
			}
		}
	}
}

function clickLabelLink(labelId) {
	if (labelId == "createnewapplication") {
		var ScreenHeight = screen.height;
		var ScreenWidth = screen.width;
		var windowH = 600;
		var windowW = 1300;
		var WindowHeight = windowH - 100;
		var WindowWidth = windowW;
		var WindowLeft = parseInt(ScreenWidth / 2) - parseInt(WindowWidth / 2);
		var WindowTop = parseInt(ScreenHeight / 2) - parseInt(WindowHeight / 2) - 50;
		var wiWindowRef = window.open("../viewer/portal/initializePortal.jsp?NewApplication=Y&pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid), 'NewApplication', 'scrollbars=yes,left=' + WindowLeft + ',top=' + WindowTop + ',height=' + windowH + ',width=' + windowW + ',resizable=yes')
	}
}
function allowPrecisionInText() {
	return 2;
}

function maxCharacterLimitInRichTextEditor(id) {

	// return no of characters allowed as per condition based on id of the field
	return -1;
}
function showCharCountInRichTextEditor(id) {

	// return true; -- To show character count in RTE
	// return false; -- To hide character count in RTE
	return true;
}
function onChangeEventInRichTextEditor(id) {
	// Write code here on change of Rich Text Editor
}
function froalaEnterKeyOption(id) {

	//When ENTER key is hit, a BR / DIV tag is inserted.
	//return FroalaEditor.ENTER_DIV
	//return FroalaEditor.ENTER_BR;
	return FroalaEditor.ENTER_P;
}

function showCustomErrorMessage(controlId, errorMsg) {
	return errorMsg;
}

function resizeSubForm(buttonId) {
	return {
		"Height": 450,
		"Width": 950
	};
}

function selectFeatureToBeIncludedInRichText() {
	return {
		'bold': true,
		'italic': true,
		'underline': true,
		'strikeThrough': true,
		'subscript': true,
		'superscript': true,
		'fontFamily': true,
		'fontSize': true,
		'color': true,
		'inlineStyle': false,
		'inlineClass': false,
		'clearFormatting': true,
		'emoticons': false,
		'fontAwesome': false,
		'specialCharacters': false,
		'paragraphFormat': true,
		'lineHeight': true,
		'paragraphStyle': true,
		'align': true,
		'formatOL': false,
		'formatUL': false,
		'outdent': false,
		'indent': false,
		'quote': false,
		'insertLink': false,
		'insertImage': false,
		'insertVideo': false,
		'insertFile': false,
		'insertTable': true,
		'insertHR': true,
		'selectAll': true,
		'getPDF': false,
		'print': false,
		'help': false,
		'html': false,
		'fullscreen': false,
		'undo': true,
		'redo': true

	}
}

function allowDuplicateInDropDown(comboName) {
	return false;
}

function postChangeEventHandler(controlId, responseData) {

}
function isSectionWiseJSRequired() {

	return false;
}
function openBAMWindow() {
	var sessionId = getWorkItemData("sessionid");
	/*var URL="http://192.168.158.104:8080/bam/login/ExtendSession.app?CalledFrom=EXT&UserId="+getWorkItemData('userinfo').username+"&UserIndex="+getWorkItemData('userinfo').userindex+"&SessionId="+sessionId+"&CabinetName="+cabinetName+"&LaunchClient=RI&ReportIndex=36&AjaxRequest=Y&OAPDomHost=192.168.158.104:8080&CalledAs=MS&OAPDomPrt=http:";
	*/
	/* userIndex
	 * userName
	 * cabinetName
	 * sessionid--getWorkItemData("sessionid")
	*/
	window.open(URL);
}


function restrictMultipleDocUpload() {
	return false;
}

function selectRowHook(tableId, selectedRowsArray, isAllRowsSelected) {


}
function onChangeVehicleBrand(CalledFrom) {
	console.log('onChangeVehicleBrand');
	var VehicleBrand = getValue('VehicleBrand');
	var Variant = getValue('Variant');
	if (VehicleBrand != '') {
		executeServerEvent('onChangeVehicleBrand', 'Change', VehicleBrand, true);
		setValues({ "Variant": Variant }, true);
		if (CalledFrom != 'Load') {
			generateCampCode();
		}
	}
}

function onChangeVariant() {
	console.log('onChangeVariant');
	var Variant = getValue('Variant');
	var ModelYear = getValue('ModelYear');
	if (Variant != '') {
		executeServerEvent('onChangeVariant', 'Change', Variant, true);
		setValues({ "ModelYear": ModelYear }, true);
	}
}

function onChangeModelYear() {
	console.log('onChangeModelYear');
	try {
		var ModelYear = getValue('VehicleGrid_ModelYear');
		var Model = getValue('VehicleGrid_Model');
		var colour = getValue("VehicleGrid_Colour");
		var ModelSuffix = getValue("VehicleGrid_ModelSuffix");
		var ModelCode = getValue("VehicleGrid_ModelCode");
		clearComboOptions('VehicleGrid_Model');
		if (ModelYear != '') {
			executeServerEvent('onChangeModelYear', 'Change', ModelYear + '~' + selectedLocale, true);
			setValues({ "VehicleGrid_Model": Model }, true);
			if (Model != '') {
				onChangeModel();
				setValues({ "VehicleGrid_Colour": colour }, true);
				setValues({ "VehicleGrid_ModelSuffix": ModelSuffix }, true);
				setValues({ "VehicleGrid_ModelCode": ModelCode }, true);
			}
		}
	} catch (err) {
		console.log('Error in onChangeModel: ' + err);
	}
}

function onChangeModel() {
	console.log('onChangeModel');
	try {
		var Model = getValue('VehicleGrid_Model');
		var colour = getValue("VehicleGrid_Colour");
		var ModelSuffix = getValue("VehicleGrid_ModelSuffix");
		var ModelCode = getValue("VehicleGrid_ModelCode");
		clearComboOptions('VehicleGrid_Colour');
		clearComboOptions('VehicleGrid_ModelSuffix');
		clearComboOptions('VehicleGrid_ModelCode');
		if (Model != '') {
			executeServerEvent('onChangeModel', 'Change', Model + '~' + selectedLocale, true);
			setValues({ "VehicleGrid_Colour": colour }, true);
			setValues({ "VehicleGrid_ModelSuffix": ModelSuffix }, true);
			setValues({ "VehicleGrid_ModelCode": ModelCode }, true);
		}
	} catch (err) {
		console.log('Error in onChangeModel: ' + err);
	}
}


function addRowPostHook(tableId) {
	console.log('Inside addRowPostHook : ' + tableId);
	console.log('Gridrow Count : ' + getGridRowCount(tableId));
}

function generateCampCode(CalledFrom, data) {
	console.log('generateCampCode');
	var flag = false;
	if (getValue('CampaignType') == 'General Campaign') {
		flag = true;
	} else if (getValue('CampaignType') == 'Special Campaign') {
		flag = true;
	} else if (getValue('CampaignType') == 'Agreement Campaign') {
		flag = true;
	} else if (getValue('CampaignType') == '50-50 Campaign') {
		flag = true;
	} else {
		flag = false;
	}

	try {
		if (getValue('RequestType') == 'New') {
			if (CalledFrom == 'Add') {
				rowCount = getGridRowCount('VehicleGrid') + 1;
			} else {
				rowCount = getGridRowCount('VehicleGrid');
			}
			if (getValue('ProductType') != '' && (rowCount > 0 || flag == true)) {
				executeServerEvent('generateCampCode', 'Change', data, true);
			} else {
				setValues({ "CampaignCode": "" }, true);
			}
			saveWorkItem();
		}
	} catch (err) {
		console.log('Error in generateCampCode: ' + err);
	}
}

function DownloadExcel() {
	//    
	var mandatoryStatus;
	mandatoryStatus = checkMandatoryFields("Sec_FinanceProductParameter");
	if (!mandatoryStatus) {
		return;
	}
	/*mandatoryStatus = checkMandatoryFields("Sec_VehicleParameters");
	if (!mandatoryStatus) {
		return;
	}*/
	mandatoryStatus = checkMandatoryFields("Sec_PriceAndRebateParameters");
	if (!mandatoryStatus) {
		return;
	}
	mandatoryStatus = checkMandatoryFields("Sec_AppropriationParameters");
	if (!mandatoryStatus) {
		return;
	}

	try {
		saveWorkItem();
		var data = executeServerEvent('getExcelData', 'Click', '', true);
		if (data == 'S') {
			var stringData = getWorkItemData('ProcessInstanceId');
			var formData = new FormData();
			formData.append("data", stringData);
			var form = document.createElement("form");
			form.method = "POST";
			form.action = "http://" + window.location.host + "/ALJFS_Campaign/Downexcel";
			for (var pair of formData.entries()) {
				var input = document.createElement("input");
				input.type = "hidden";
				input.name = pair[0];
				input.value = pair[1];
				form.appendChild(input);
			}
			document.body.appendChild(form);
			form.submit();
		}
	} catch (err) {
		console.log('Error in DownloadExcel: ' + err);
	}
}

function importData(CalledFrom) {

	try {
		var fileInput = document.createElement("input");
		fileInput.type = "file";
		fileInput.accept = ".xlsx";
		fileInput.addEventListener("change", function() {
			const allowedExtensions = /(\.xlsx|\.xls)$/i;
			if (!allowedExtensions.exec(fileInput.value)) {
				ShowToastErrorHandleDup(fetchMsg(120));
				return;
			}
			var file = fileInput.files[0];
			var formData = new FormData();
			formData.append("file", file);
			formData.append("Data", CalledFrom);
			var xhr = new XMLHttpRequest();
			xhr.open("POST", "http://" + window.location.host + "/ALJFS_Campaign/UploadExcel");
			xhr.onload = function() {
				if (xhr.status === 200) {
					var serverdata = xhr.response;
					var AllData = serverdata.split('~');
					var message = AllData[0];
					var arr = [];
					if (message == 'S') {
						var data = JSON.parse(AllData[1]);
						if (CalledFrom == 'VIN') {
							clearComboOptions('VINNumber');
							for (var i = 0; i < data.length; i++) {
								var VINNumber = data[i].VINNumber.trim();
								const regex = /^[a-zA-Z][a-zA-Z\d]{16}$/;
								// if(VINNumber != '' && VINNumber != undefined){
								// arr.push(VINNumber);
								// addItemInCombo("VINNumber",VINNumber,VINNumber);
								// }
								//Newadded
								if (regex.test(VINNumber)) {
									if (!arr.includes(VINNumber)) {
										arr.push(VINNumber);
										addItemInCombo("VINNumber", VINNumber, VINNumber);
									}
								} else {
									continue;
								}

								//NewaddedEND

								setValues({ "VINNumber": arr }, true);
							}
							if (arr.length == 0) {
								ShowToastErrorHandleDup(fetchMsg(107));
								clearComboOptions('VINNumber');
							} else {
								ShowToastSuccessHandleDup(fetchMsg(100));
							}
						} else if (CalledFrom == 'CustID') {
							clearComboOptions('CustID');
							const regex = /^[1-2]\d{9}$/;
							for (var i = 0; i < data.length; i++) {
								var CustID = data[i].CustID.trim();
								// var CustID=data[i].CustID;
								// if(CustID != '' && CustID != undefined){
								// arr.push(CustID);
								// addItemInCombo("CustID",CustID,CustID);
								// }
								//New Added
								if (regex.test(CustID)) {
									if (!arr.includes(CustID)) {
										arr.push(CustID);
										addItemInCombo("CustID", CustID, CustID);
									}
								} else {
									continue;
								}

								//New Added END
								setValues({ "CustID": arr }, true);
							}
							if (arr.length == 0) {
								ShowToastErrorHandleDup(fetchMsg(108));
								clearComboOptions('CustID');
							} else {
								ShowToastSuccessHandleDup(fetchMsg(101));
							}
						}
					} else {
						if (CalledFrom == 'VIN') {
							ShowToastErrorHandleDup(fetchMsg(107));
							clearComboOptions('VINNumber');
						} else {
							ShowToastErrorHandleDup(fetchMsg(108));
							clearComboOptions('CustID');
						}
					}
				} else {
					ShowToastErrorHandleDup(fetchMsg(102));
				}
			};
			xhr.send(formData);
		});
		fileInput.click();
	} catch (err) {
		console.log('Error in importData: ' + err);
	}
}

function checkMandatoryFields(sectionFrameID) {
	console.log("Inside Check Mandatory Fields");
	console.log("Section Frame ID Selected is : " + sectionFrameID);
	try {
		if (!checkMandatoryLeft(sectionFrameID)) {
			var jsonArray = getMandatoryFieldList(sectionFrameID);
			for (i = 0; i < jsonArray.length; i++) {
				var controlIDs = jsonArray[i].ControlId;
				var validateFlag = jsonArray[i].Validate;
				if (getValue(controlIDs) == '' || getValue(controlIDs) == undefined) {
					validateFlag = false;
				}
				if (!validateFlag) {
					var labelName = document.getElementById(controlIDs + "_label").innerText;
					showBootBox(controlIDs, fetchMsg("103") + " " + labelName + ".", "error");
					setFocus(controlIDs);
					return false;
				}
			}
			return validateFlag;
		} else {
			return true;
		}
	} catch (err) {
		console.log('Error in checkMandatoryFields: ' + err);
	}
}

// To skip filling the mandatory fields.(Use In the case of Discard only)
function skipValidation() {
	return true;
}

function onClickSubmit() {
	console.log("Inside onClickSubmit");
	try {
		populateDocumentMaster();
		if (getValue('DecisionEX') != 'Reject' && getValue('DecisionEX') != 'Send Back') {
			var mandatoryStatus = checkMandatoryFields("sec_Basic");
			if (!mandatoryStatus) {
				return;
			}
			mandatoryStatus = checkMandatoryFields('Sec_DecisionDetails');
			if (!mandatoryStatus) {
				return;
			}
			var PRParamcheck = checkPRParams('Sec_PriceAndRebateParameters');
			if (!PRParamcheck && getValue('CampaignType') != '50-50 Campaign') {
				showBootBox("FeesWithVATWaiver", fetchMsg(119), "error");
				return;
			}
			var mandatorydoc = mandatoryDocCheck();
			if (mandatorydoc != '' && mandatorydoc != undefined) {
				var mandatorydocData = mandatorydoc.split('~');
				if (mandatorydocData[0] == 'F' && getValue('AutoGenerated') != 'Y') {
					showBootBox("inwardDocument_Grid", fetchMsg("104") + " - " + mandatorydocData[1] + ".", "error");
					//showBootBox("inwardDocument_Grid", fetchMsg("104"), "error");
					return;
				}
			}
		} else {
			skipValidation();
		}
		//DecisionEX
		var msg = '';
		if (getValue('DecisionEX') == 'Reject') {
			msg = 'The Workitem will be Discarded, Do you want to continue?';
		} else if (getValue('DecisionEX') == 'Exit') {
			msg = 'The Workitem will move to Exit stage, Do you want to continue?';
		} else if (getValue('DecisionEX') == 'Send Back') {
			msg = 'The Workitem will be send back to campaign maker, Do you want to continue?';
		} else {
			msg = 'The Workitem will move to Next stage, Do you want to continue?';
		}
		var CampaignType = getValue('CampaignType');
		if (CampaignType == 'Special Campaign') {
			var flag = false;
			if (getValue('VINNumber').length > 0 || getValue('CustID').length > 0) {
				flag = false;
			} else {
				flag = true;
				ShowToastErrorHandleDup(fetchMsg(109));
				return;
			}
		}
		/*else if (CampaignType == 'Joint Pricing Campaign' && getValue('RequestType') == 'New' && getValue('DecisionEX') == 'Exit') {
			if (getValue('ExcelUploaded') != 'Y') {
				ShowToastErrorHandleDup(fetchMsg(110));
				return;
			}
		}*/
		else if (CampaignType == '50-50 Campaign') {
			if (getGridRowCount('VehicleGrid') < 1) {
				ShowToastErrorHandleDup('Please add vehicle details in vehicle parameters');
				return;
			}
		}
		if (confirm(msg)) {
			setValues({ 'Decision': getValue('DecisionEX') }, true);
			PopulateHistoryTable();
			completeWorkItem();
		}
	} catch (err) {
		console.log('Error in onClickSubmit: ' + err);
	}
}

function dateCheck() {
	console.log("Inside dateCheck");
	try {
		if (getValue('StartDate') != '' && getValue('EndDate') != '') {
			var MonthYearARR = getValue('StartDate').split('/');
			var date = MonthYearARR[0];
			var month = MonthYearARR[1];
			var year = MonthYearARR[2];
			var newStartDate = month + "/" + date + "/" + year;
			MonthYearARR = getValue('EndDate').split('/');
			date = MonthYearARR[0];
			month = MonthYearARR[1];
			year = MonthYearARR[2];
			var newEndDate = month + "/" + date + "/" + year;
			var date1 = new Date(newStartDate);
			var date2 = new Date(newEndDate);
			var diffTime = date2 - date1;
			var diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
			if (diffDays < 0) {
				setDateRange('EndDate', getValue('StartDate'), '');
				ShowToastWarningHandleDup(fetchMsg(105))
				setValues({ 'EndDate': '' }, true);
				return;
			} else if (diffDays > 0) {
				setDateRange('EndDate', getValue('StartDate'), '');
				return;
			}
		}
	} catch (err) {
		console.log('Error in dateCheck: ' + err);
	}
}

function setHeaderBackground() {
	try {

		let headerframe = document.getElementById("headerframe1");
		let elementsInFrame = headerframe.querySelectorAll("*");
		for (let i = 0; i < elementsInFrame.length; i++) {
			elementsInFrame[i].style.color = "white";
			elementsInFrame[i].style.backgroundColor = "transparent";
		}
		headerframe.style.backgroundImage = 'url(' + location.protocol + '//' + location.host + '/ALJFS_Campaign/components/viewer/resources/images/header.png)';
		headerframe.style.backgroundRepeat = 'no-repeat';
		headerframe.style.backgroundSize = 'cover';

	} catch (error) {
		console.log(error);
	}
}

function formdisable() {
	if (sActivityName == 'Marketing Manager' || sActivityName == 'Accounts Manager' || sActivityName == 'Operation Marketing') {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('Sec_DecisionDetails', 'disable', 'false');
		setStyle('Description', 'disable', 'true');
		setStyle('DescriptionArabic', 'disable', 'true');
		setStyle('table8', 'disable', 'true');
		setColumnDisable('inwardDocument_Grid', 5, false, true);
		setStyle('btn_DownloadExcel', 'disable', 'false');
	} else if (sActivityName == 'Exit') {
		setValues({ 'DecisionEX': 'Approve' }, true);
	} else if (sActivityName == 'Discard') {
		setValues({ 'DecisionEX': 'Reject' }, true);
	}
}

function deleteRowPostHook(tableId, rowIndices) {
	generateCampCode('', '');
	if (tableId == 'VehicleGrid') {
		/*setValues({ 'ExcelUploaded': 'N' }, true);*/
		setValues({ 'ExcelUploaded': 'false' }, true);
	}
	if (getValue('CampaignType') == 'Joint Pricing Campaign' || getValue('CampaignType') == '50-50 Campaign') {
		if (getGridRowCount('VehicleGrid') > 0) {
			/*setStyle('btn_UploadExcel', 'disable', 'false');*/
			setStyle('ExcelUploaded', 'disable', 'false');
			setStyle('btn_DownloadExcel', 'disable', 'false');
		} else {
			/*setStyle('btn_UploadExcel', 'disable', 'true');*/
			setStyle('ExcelUploaded', 'disable', 'true');
			setStyle('btn_DownloadExcel', 'disable', 'true');
		}
	}
}

function onChangeRequestType(CalledFrom) {
	console.log('Inside onChangeRequestType');
	try {
		//GlobalProductType
		if (GlobalRequestType != '' && GlobalRequestType != getValue('RequestType') && CalledFrom == 'Change') {
			if (confirm('All the form data will get cleared, Do you want to continue?')) {
				clearSectionValues('Sec_CampaignInfo', '');
				//clearSectionValues('Sec_FinanceProductParameter', '');
				clearSectionValues('Sec_VehicleParameters', '');
				clearSectionValues('Sec_CustomerParameters', '');
				clearSectionValues('Sec_PriceAndRebateParameters', '');
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				setStyle('ExcelUploaded', 'disable', 'true');
				setStyle('btn_DownloadExcel', 'disable', 'true');
				var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
				for (var i = 0; i < callfrom.length; i++) {
					onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
				}
			} else {
				setValues({ 'RequestType': GlobalRequestType }, true);
				return;
			}
		}
		if (CalledFrom == 'Change') {
			document.getElementById('overlay').style.display = 'block';
			document.getElementById('loader').style.display = 'block';
			setTimeout(function() {
				RequestTypeCondition(CalledFrom);
				document.getElementById('overlay').style.display = 'none';
				document.getElementById('loader').style.display = 'none';
			}, 100);
		} else {
			RequestTypeCondition(CalledFrom);
		}
	} catch (err) {
		document.getElementById('overlay').style.display = 'none';
		document.getElementById('loader').style.display = 'none';
		console.log('Error in onChangeRequestType: ' + err);
	}
}

function RequestTypeCondition(CalledFrom) {
	var RequestType = getValue('RequestType');
	if (RequestType != '') {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('Sec_FinanceProductParameter', 'disable', 'false');
		setStyle('RequestType', 'disable', 'false');
		if (RequestType == 'Modify') {
			if (CalledFrom == 'Change') {
				clearValue('CampaignCode');
			}
			/*setStyle('ProductSubType', 'disable', 'true');*/
			setStyle('CampaignNameCombo', 'visible', 'true');
			setStyle('CampaignNameCombo', 'mandatory', 'true');
			setStyle('CampaignName', 'visible', 'false');
			setStyle('CampaignName', 'mandatory', 'false');
			setStyle('CampaignNameCombo', 'disable', 'false');
			setStyle('StartDate', 'disable', 'false');
			setStyle('EndDate', 'disable', 'false');
			setStyle('Sec_DecisionDetails', 'disable', 'false');
		} else if (RequestType == 'New') {
			if (CalledFrom == 'Change') {
				clearValue('CampaignCode');
				generateCampCode('', '');
			}
			setStyle('CampaignNameCombo', 'visible', 'false');
			setStyle('CampaignNameCombo', 'mandatory', 'false');
			setStyle('CampaignName', 'visible', 'true');
			setStyle('CampaignName', 'mandatory', 'true');
			setStyle('CampaignCode', 'disable', 'true');
			setStyle('ALJFSSupportAmount', 'disable', 'true');
			/*setStyle('CopyCampaignCode', 'disable', 'false');*/
			setStyle('CampaignType', 'disable', 'false');
		}
	} else {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('RequestType', 'disable', 'false');
		//Cash
		setStyle('Sec_FinanceProductParameter', 'disable', 'false');
		//Cash end
	}
	GlobalRequestType = RequestType;

}

//for new request to copy campaign details
function onChangeCopyCampCode(CalledFrom) {
	console.log('Inside onChangeCopyCampCode called from' + CalledFrom);
	var Code_Name = '';
	var showmsg = 'N';
	try {
		document.getElementById('overlay').style.display = 'block';
		document.getElementById('loader').style.display = 'block';
		setTimeout(function() {
			if (CalledFrom == 'CopyCampaignCode') {
				Code_Name = getValue('CopyCampaignCode');
			} else if (CalledFrom == 'CampaignName') {
				Code_Name = getValue('CampaignNameCombo');
				showmsg = 'Y';
				setValues({ 'CampaignName': Code_Name }, true);
			}
			if (Code_Name != '') {
				var data = executeServerEvent('onChangeCopyCampCode', 'Change', CalledFrom + '~' + Code_Name, true);
				var serverdata = data.split('~');
				if (serverdata[0] == 'F') {
					// showMessage("", serverdata[1], "error");
					ShowToastErrorHandleDup(fetchMsg(106))
					clearValue('CampaignName');
					clearValue('CampaignNameCombo');
					document.getElementById('overlay').style.display = 'none';
					document.getElementById('loader').style.display = 'none';
					return;
				} else if (serverdata[0] == 'S') {
					if (serverdata[1] != '[]') {
						var data2 = JSON.parse(serverdata[1]);
						addDataToAdvancedGrid('VehicleGrid', data2);
					}
					if (CalledFrom == 'CopyCampaignCode') {
						clearValue('CampaignName');
						clearValue('CampaignNameCombo');
					}
					generateCampCode('', '');
					onChangeRequestType('');
					onChangeWholesaler();
					onChangeProvince();
					onChangeCampaignType('');
					onChangeAppropriation('Load');
					onChangePriority2('Load');
					//onChangeProductType('Load');
					if (showmsg == 'Y') {
						showMessage("", "The selected Campaign has a StartDate of " + getValue('StartDate') + " and an EndDate of " + getValue('EndDate'), "error");
					}
					clearValue('StartDate');
					clearValue('EndDate');
				}
				onChangeFullyWave();
				var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
				for (var i = 0; i < callfrom.length; i++) {
					onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
				}
			}
			document.getElementById('overlay').style.display = 'none';
			document.getElementById('loader').style.display = 'none';
		}, 100);
	} catch (err) {
		document.getElementById('overlay').style.display = 'none';
		document.getElementById('loader').style.display = 'none';
		console.log('Error in onChangeCopyCampCode: ' + err);
	}
}



function onChangeCampaignName() {
	console.log('Inside onChangeCampaignName');
	try {
		if (getValue('RequestType') == 'New') {
			var data = executeServerEvent('onChangeCampaignName', 'Change', getValue('CampaignName').trim(), true);
			if (data == 'F') {
				/*				ShowToastErrorHandleDup('The Campaign Name ' + getValue('CampaignName').trim() + ' ' + fetchMsg(121));*/
				ShowToastErrorHandleDup(fetchMsg(126));
				clearValue('CampaignName');
			}
		}
	} catch (err) {
		console.log('Error in onChangeCopyCampCode: ' + err);
	}
}

function onChangePromoCode() {
	console.log('Inside onChangePromoCode');
	try {
		if (getValue('RequestType') == 'New') {
			var data = executeServerEvent('onChangePromoCode', 'Change', getValue('PromoCode').trim(), true);
			if (data == 'F') {
				ShowToastErrorHandleDup('The PromoCode ' + getValue('PromoCode').trim() + ' ' + fetchMsg(121));
				clearValue('PromoCode');
			}
		}
	} catch (err) {
		console.log('Error in onChangePromoCode: ' + err);
	}
}

function onClickTab(tabId, sheetindex) {
	if (tabId == 'tab1' && sheetindex == '1') { //Documentation Tab
		if (sActivityName == 'Campaign Maker' || sActivityName == 'Create Campaign') {
			populateDocumentMaster();
		}
		for (var row = 0; row < getGridRowCount('inwardDocument_Grid'); row++) {
			removeAsteriskSignDoc(row + 1);
			if (getValueFromTableCell('inwardDocument_Grid', row, 6) == 'Yes' || getValueFromTableCell('inwardDocument_Grid', row, 6) == 'Opt') {
				putAsteriskSignDoc(row + 1);
			}
			if (getValueFromTableCell('inwardDocument_Grid', row, 9) == '') {
				setCellDisabled('inwardDocument_Grid', row, 5, true);
				if (sActivityName == 'Campaign Maker' || sActivityName == 'Create Campaign') {
					setCellDisabled('inwardDocument_Grid', row, 4, false);
					setCellDisabled('inwardDocument_Grid', row, 11, true);
				} else {
					setCellDisabled('inwardDocument_Grid', row, 4, true);
					setCellDisabled('inwardDocument_Grid', row, 11, true);
				}
			} else {
				setCellDisabled('inwardDocument_Grid', row, 5, false);
				if (sActivityName == 'Campaign Maker' || sActivityName == 'Create Campaign') {
					setCellDisabled('inwardDocument_Grid', row, 4, true);
					setCellDisabled('inwardDocument_Grid', row, 11, false);
				} else {
					setCellDisabled('inwardDocument_Grid', row, 4, true);
					setCellDisabled('inwardDocument_Grid', row, 11, true);
				}
			}
		}
		onChangeChannel();
	} else if (tabId == 'tab1' && sheetindex == '2') {
		filterTableData('table8', 'Sort', '4', 'table8', 'Table');
		// setTimeout(1000);
		filterTableData('table8', 'Sort', '4', 'table8', 'Table');
		setStyle('table8', 'disable', 'true');
		document.getElementById('Description').style = "pointer-events:unset";
		document.getElementById('DescriptionArabic').style = "pointer-events:unset";
		setTextAreaHeight('Description');
		setTextAreaHeight('DescriptionArabic');
		var fadedHeader = document.querySelector('.faded-header');
		if (fadedHeader) {
			// Change the background color to solid
			fadedHeader.style.opacity = '1'; // Solid color
		}
	}
}

function setTextAreaHeight(ID) {
	const textarea = document.getElementById(ID);
	setStyleImportant(textarea, 'min-height', '60px');
	setStyleImportant(textarea, 'max-height', '60px');
	textarea.style.height = '60px';
}

function setStyleImportant(element, property, value) {
	element.style.setProperty(property, value, 'important');
}


function populateDocumentMaster() {
	try {
		if (getGridRowCount('inwardDocument_Grid') == 0 && getValue('AutoGenerated') != 'Y' && getValue('ProductType') != '') {
			executeServerEvent('populateDocumentMaster', 'Click', selectedLocale, true);
		}
		for (var i = 0; i < getGridRowCount('inwardDocument_Grid'); i++) {
			setCellDisabled("inwardDocument_Grid", i, 1, "true");
			setCellDisabled("inwardDocument_Grid", i, 2, "true");
			setCellDisabled("inwardDocument_Grid", i, 3, "true");
			setCellDisabled("inwardDocument_Grid", i, 4, "false");
			setCellDisabled("inwardDocument_Grid", i, 5, "false");
		}
	} catch (err) {
		console.log('Error in populateDocumentMaster: ' + err);
	}
}

function mandatoryDocCheck() {
	try {
		for (var i = 0; i < getGridRowCount('inwardDocument_Grid'); i++) {
			if (getValueFromTableCell('inwardDocument_Grid', i, 9) == '' && getValueFromTableCell('inwardDocument_Grid', i, 6) == 'Yes') {
				return 'F~' + getValueFromTableCell('inwardDocument_Grid', i, 8);
			} else if (getValueFromTableCell('inwardDocument_Grid', i, 6) == 'Opt' && getValueFromTableCell('inwardDocument_Grid', i, 9) == '' && sActivityName == 'Operation Marketing') {
				return 'F~' + getValueFromTableCell('inwardDocument_Grid', i, 8);
			}
		}
	} catch (err) {
		console.log('Error in mandatoryDocCheck: ' + err);
	}
}

function SupportAmount() {
	try {
		var DisAmount = getValue('VehicleGrid_DistributorSupportAmount');
		var WSAmount = getValue('VehicleGrid_WSSupportAmount');
		if (getValue('VehicleGrid_SupportAmounttobe') == 'Percentage' && (Number(DisAmount) > 100 || Number(WSAmount) > 100 || (Number(DisAmount) + Number(WSAmount)) > 100)) {
			// showBootBox("", "Distributor/WS Support Amount should be equals to or less then 100%.", "error");
			ShowToastErrorHandleDup(fetchMsg(111));
			clearValue('VehicleGrid_DistributorSupportAmount');
			clearValue('VehicleGrid_WSSupportAmount');
			clearValue('VehicleGrid_ALJFSSupportAmount');
		} else {
			var ALJFSAmount = (Number(DisAmount) + Number(WSAmount)).toFixed(2);
			var MaximumSupportAmount = Number(getValue('VehicleGrid_MaximumSupportAmount')).toFixed(2);
			if (Number(ALJFSAmount) <= Number(MaximumSupportAmount) || MaximumSupportAmount == 0.00) {
				setValues({ 'VehicleGrid_ALJFSSupportAmount': ALJFSAmount }, true);
			} else if (getValue('VehicleGrid_SupportAmounttobe') == 'Absolute value') {
				clearValue('VehicleGrid_DistributorSupportAmount');
				clearValue('VehicleGrid_WSSupportAmount');
				clearValue('VehicleGrid_ALJFSSupportAmount');
				// showBootBox("", "ALJFS Support Amount should not be more than Maximum Support Amount.", "error");
				ShowToastErrorHandleDup(fetchMsg(112));
			}
		}
	} catch (err) {
		console.log('Error in SupportAmount: ' + err);
	}
}


function modifyRowPostHook(controlId) {
	if (controlId == 'VehicleGrid') {
		generateCampCode('', '');
		/*setValues({ 'ExcelUploaded': 'N' }, true);*/
		setValues({ 'ExcelUploaded': 'false' }, true);
	}
}


function tableOperation(tableId, operationType) {
	if (tableId == 'VehicleGrid') {
		if (operationType == "AddRow") {
			generateCampCode('Add', getValue('VehicleGrid_VehicleBrand'));
			/*setValues({ 'ExcelUploaded': 'N' }, true);*/
			setValues({ 'ExcelUploaded': 'false' }, true);
			/*setStyle('btn_UploadExcel', 'disable', 'false');*/
			setStyle('ExcelUploaded', 'disable', 'false');
			setStyle('btn_DownloadExcel', 'disable', 'false');
			// New ended by Yash
		} else if (operationType == "DeleteRow") {
			if (confirm('The selected row(s) will be deleted, Do you want to continue?')) {
				return true;
			} else {
				return false;
			}
		}
	}
}

function onChangeProductType(CalledFrom) {
	try {
		var ProductType = getValue('ProductType');
		var ProductSubType = getValue('ProductSubType');
		var CampaignType = getValue('CampaignType');
		var Channel = getValue('Channel');

		//GlobalProductType
		if (GlobalProductType != '' && GlobalProductType != ProductType && CalledFrom != 'Load') {
			if (confirm('All the form data will get cleared, Do you want to continue?')) {
				clearSectionValues('Sec_CampaignInfo', '');
				//clearSectionValues('Sec_FinanceProductParameter', '');
				clearSectionValues('Sec_VehicleParameters', '');
				clearSectionValues('Sec_CustomerParameters', '');
				clearSectionValues('Sec_PriceAndRebateParameters', '');
				clearValue('RequestType');
				var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
				for (var i = 0; i < callfrom.length; i++) {
					onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
				}
				onChangeRequestType('');
				for (var i = 0; i < getGridRowCount('inwardDocument_Grid'); i++) {
					if (getValueFromTableCell('inwardDocument_Grid', i, 9) != '') {
						try {
							window.parent.deleteDoc(getValueFromTableCell('inwardDocument_Grid', i, 9));
						} catch (error) {
							console.log('Ignore the error :  ' + error);
						}
					}
				}
				refreshFrame('Sec_InwardDocuments');
				clearTable('inwardDocument_Grid');
			} else {
				setValues({ 'RequestType': GlobalRequestType }, true);
				return;
			}
		}
		if (ProductType != '') {
			//Cash Start
			setStyle('Sec_CampaignInfo', 'visible', 'true');
			setStyle('Sec_VehicleParameters', 'visible', 'true');
			setStyle('Sec_CustomerParameters', 'visible', 'true');
			if (ProductType == 'CF') {
				document.getElementsByClassName('sectionStyle')[3].childNodes[0].textContent = 'Cash Parameters';
				setStyle('VehicleGrid', 'visible', 'false');
				setStyle('VehicleType', 'visible', 'false');
				setStyle('VehicleType', 'mandatory', 'false');
				setStyle('VINNumber', 'visible', 'false');
				setStyle('UploadVIN', 'visible', 'false');

				setStyle('DistributorWise', 'visible', 'false');
				setStyle('WholesalerBranch', 'visible', 'false');
				setStyle('Wholesalers', 'visible', 'false');
				setStyle('FullyWave', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'false');
				setStyle('Notes1', 'visible', 'false');
				setStyle('Note', 'visible', 'false');
				setStyle('CustID', 'visible', 'false');
				setStyle('UploadCustID', 'visible', 'false');
				setStyle('RetailPurchaseDiscount', 'visible', 'false');
				setStyle('MonthlywithVAT', 'visible', 'false');
			} else {
				setStyle('VehicleGrid', 'visible', 'true');
				setStyle('VehicleType', 'visible', 'true');
				setStyle('VehicleType', 'mandatory', 'true');
				setStyle('VINNumber', 'visible', 'true');
				setStyle('UploadVIN', 'visible', 'true');
				setStyle('DistributorWise', 'visible', 'true');
				setStyle('WholesalerBranch', 'visible', 'true');
				setStyle('Wholesalers', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'true');
				setStyle('FullyWave', 'visible', 'false');
				document.getElementsByClassName('sectionStyle')[3].childNodes[0].textContent = 'Vehicle Parameters';
			}
			//Cash END

			executeServerEvent('onChangeProductType', 'Change', selectedLocale, true);
			setValues({ "ProductSubType": ProductSubType }, true);
			setValues({ "Channel": Channel }, true);
			if (CalledFrom != 'Load') {
				generateCampCode('', '');
				saveWorkItem();
			} else {
				setValues({ "CampaignType": CampaignType }, true);
			}
		} else {
			//Cash Start
			setStyle('Sec_CampaignInfo', 'visible', 'false');
			setStyle('Sec_VehicleParameters', 'visible', 'false');
			setStyle('Sec_CustomerParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'false');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			//Cash END
			clearComboOptions('ProductSubType');
		}
		GlobalProductType = getValue('ProductType');
	} catch (err) {
		console.log('Error in onChangeProductType: ' + err);
	}
}

function onChangeProvince() {
	try {
		var Province = getValue('Province');
		var City = getValue('City');
		if (Province != '') {
			executeServerEvent('onChangeProvince', 'Change', Province + '~' + selectedLocale, true);
			setValues({ "City": City }, true);
		} else {
			clearComboOptions('City');
		}
	} catch (err) {
		console.log('Error in onChangeProvince: ' + err);
	}
}

//onChangeWholesaler
function onChangeWholesaler() {
	try {
		var Wholesalers = getValue('Wholesalers');
		var WholesalerBranch = getValue('WholesalerBranch');
		if (Wholesalers != '') {
			executeServerEvent('onChangeWholesaler', 'Change', Wholesalers + '~' + selectedLocale, true);
			setValues({ "WholesalerBranch": WholesalerBranch }, true);
		} else {
			clearComboOptions('WholesalerBranch');
		}
	} catch (err) {
		console.log('Error in onChangeWholesaler: ' + err);
	}
}

function onChangeCampaignType(calledFrom) {
	try {
		if (GlobalCampaignType != '' && GlobalCampaignType != getValue('CampaignType') && calledFrom == 'Change') {
			if (confirm('All the form data will get cleared, Do you want to continue?')) {
				clearSectionValues('Sec_CampaignInfo', 'CampaignType');
				//clearSectionValues('Sec_FinanceProductParameter', 'CampaignType');
				clearSectionValues('Sec_VehicleParameters', 'CampaignType');
				clearSectionValues('Sec_CustomerParameters', 'CampaignType');
				clearSectionValues('Sec_PriceAndRebateParameters', 'CampaignType');
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				setStyle('ExcelUploaded', 'disable', 'true');
				setStyle('btn_DownloadExcel', 'disable', 'true');
			} else {
				setValues({ 'CampaignType': GlobalCampaignType }, true);
				return;
			}
		}
		if (calledFrom == 'Change') {
			document.getElementById('overlay').style.display = 'block';
			document.getElementById('loader').style.display = 'block';
			setTimeout(function() {
				CampaignTypeCondition();
				document.getElementById('overlay').style.display = 'none';
				document.getElementById('loader').style.display = 'none';
			}, 100);
		} else {
			CampaignTypeCondition();
		}
	} catch (err) {
		document.getElementById('overlay').style.display = 'none';
		document.getElementById('loader').style.display = 'none';
		console.log('Error in onChangeCampaignType: ' + err);
	}
}

function CampaignTypeCondition() {
	var Camptype = getValue('CampaignType');
	var Reqtype = getValue('RequestType');
	if (Camptype != '' && Reqtype == 'New') {
		setStyle('sec_Basic', 'disable', 'false');
		setStyle('CampaignNameCombo', 'visible', 'false');
		setStyle('CampaignNameCombo', 'mandatory', 'false');
		setStyle('CampaignName', 'visible', 'true');
		setStyle('CampaignName', 'mandatory', 'true');
		setStyle('CampaignCode', 'disable', 'true');
		setStyle('ALJFSSupportAmount', 'disable', 'true');
		/*setStyle('CopyCampaignCode', 'disable', 'false');*/
		setStyle('RequestType', 'disable', 'false');
		if (Reqtype != '') {
			setStyle('CampaignType', 'disable', 'false');
		} else {
			setStyle('CampaignType', 'disable', 'true');
		}
	} else if (Camptype != '' && Reqtype == 'Modify') {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('Sec_FinanceProductParameter', 'disable', 'false');
		setStyle('ProductSubType', 'disable', 'true');
		setStyle('CampaignNameCombo', 'visible', 'true');
		setStyle('CampaignNameCombo', 'mandatory', 'true');
		setStyle('CampaignName', 'visible', 'false');
		setStyle('CampaignName', 'mandatory', 'false');
		setStyle('CampaignNameCombo', 'disable', 'false');
		setStyle('StartDate', 'disable', 'false');
		setStyle('EndDate', 'disable', 'false');
		setStyle('RequestType', 'disable', 'false');
		setStyle('Sec_DecisionDetails', 'disable', 'false');
	} else {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('Sec_FinanceProductParameter', 'disable', 'false');
		setStyle('CampaignNameCombo', 'visible', 'false');
		setStyle('CampaignNameCombo', 'mandatory', 'false');
		setStyle('CampaignName', 'visible', 'true');
		setStyle('CampaignName', 'mandatory', 'true');
		setStyle('CampaignCode', 'disable', 'true');
		setStyle('ALJFSSupportAmount', 'disable', 'true');
		/*setStyle('CopyCampaignCode', 'disable', 'false');*/
		setStyle('RequestType', 'disable', 'false');
		if (Reqtype != '') {
			setStyle('CampaignType', 'disable', 'false');
			if (Reqtype == 'Modify') {
				setStyle('sec_Basic', 'disable', 'true');
				setStyle('Sec_FinanceProductParameter', 'disable', 'false');
				setStyle('ProductSubType', 'disable', 'true');
				setStyle('CampaignNameCombo', 'visible', 'true');
				setStyle('CampaignNameCombo', 'mandatory', 'true');
				setStyle('CampaignName', 'visible', 'false');
				setStyle('CampaignName', 'mandatory', 'false');
				setStyle('CampaignNameCombo', 'disable', 'false');
				setStyle('StartDate', 'disable', 'false');
				setStyle('EndDate', 'disable', 'false');
				setStyle('RequestType', 'disable', 'false');
				setStyle('Sec_DecisionDetails', 'disable', 'false');
			}
		} else {
			setStyle('CampaignType', 'disable', 'true');
		}
	}
	switch (Camptype) {
		case 'Joint Pricing Campaign':
			setStyle('Sec_CustomerParameters', 'visible', 'false');
			setStyle('Sec_AppropriationParameters', 'visible', 'true');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('RetailPurchaseDiscount', 'visible', 'false');
			setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
			setStyle('Note', 'visible', 'false');
			setStyle('CustID', 'disable', 'true');
			setStyle('UploadCustID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('UploadVIN', 'disable', 'true');
			setStyle('CustID', 'visible', 'false');
			setStyle('UploadCustID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('UploadVIN', 'visible', 'false');
			setStyle('PRFinanceRate', 'visible', 'false');
			setStyle('Note', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('Notes1', 'visible', 'false');
			setStyle('AppropriationPriority1', 'mandatory', 'true');
			setStyle('AppropriationPriority2', 'mandatory', 'true');
			setStyle('AppropriationPriority3', 'mandatory', 'true');
			setStyle('FeesWithVATWaiver', 'visible', 'true');
			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'true');*/
			setStyle('ExcelUploaded', 'visible', 'true');
			setStyle('ExcelUploaded', 'mandatory', 'true');
			setStyle('btn_DownloadExcel', 'visible', 'true');
			if (getGridRowCount('VehicleGrid') > 0) {
				/*setStyle('btn_UploadExcel', 'disable', 'false');*/
				setStyle('ExcelUploaded', 'disable', 'false');
				setStyle('btn_DownloadExcel', 'disable', 'false');
			} else {
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				setStyle('ExcelUploaded', 'disable', 'true');
				setStyle('btn_DownloadExcel', 'disable', 'true');
			}
			if (getValue('AutoGenerated') == 'Y') {
				setStyle('ExcelUploaded', 'visible', 'false');
				setStyle('ExcelUploaded', 'mandatory', 'false');
				setStyle('btn_DownloadExcel', 'visible', 'false');
			} else {
				setStyle('ExcelUploaded', 'visible', 'true');
				setStyle('ExcelUploaded', 'mandatory', 'true');
				setStyle('btn_DownloadExcel', 'visible', 'true');
			}
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			break;
		case 'General Campaign':
			clearValue('AppropriationPriority1');
			clearValue('AppropriationPriority2');
			clearValue('AppropriationPriority3');
			setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
			setStyle('EndDate', 'mandatory', 'true');

			setStyle('CustID', 'disable', 'true');
			setStyle('UploadCustID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('UploadVIN', 'disable', 'true');
			setStyle('CustID', 'visible', 'false');
			setStyle('UploadCustID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('UploadVIN', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('AppropriationPriority1', 'mandatory', 'false');
			setStyle('AppropriationPriority2', 'mandatory', 'false');
			setStyle('AppropriationPriority3', 'mandatory', 'false');

			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			setStyle('Notes1', 'visible', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');
			setStyle('PRFinanceRate', 'visible', 'true');
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			if (getValue('ProductType') == 'CF') {
				setStyle('RetailPurchaseDiscount', 'visible', 'false');
				setStyle('MonthlywithVAT', 'visible', 'false');
				setStyle('Note', 'visible', 'false');
				onChangeFullyWave();
			} else {
				setStyle('FeesWithVATWaiver', 'visible', 'true');
				setStyle('RetailPurchaseDiscount', 'visible', 'true');
				setStyle('MonthlywithVAT', 'visible', 'true');
				setStyle('Note', 'visible', 'true');
			}
			break;
		case 'Special Campaign':
			clearValue('AppropriationPriority1');
			clearValue('AppropriationPriority2');
			clearValue('AppropriationPriority3');
			setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
			setStyle('Note', 'visible', 'true');
			setStyle('Notes1', 'visible', 'true');
			setStyle('CustID', 'disable', 'false');
			setStyle('UploadCustID', 'disable', 'false');
			setStyle('VINNumber', 'disable', 'false');
			setStyle('UploadVIN', 'disable', 'false');
			setStyle('CustID', 'visible', 'true');
			setStyle('UploadCustID', 'visible', 'true');
			setStyle('VINNumber', 'visible', 'true');
			setStyle('UploadVIN', 'visible', 'true');
			setStyle('AppropriationPriority1', 'mandatory', 'false');
			setStyle('AppropriationPriority2', 'mandatory', 'false');
			setStyle('AppropriationPriority3', 'mandatory', 'false');
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('PromoCode', 'mandatory', 'true');
			setStyle('RetailPurchaseDiscount', 'visible', 'true');
			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			setStyle('PRFinanceRate', 'visible', 'true');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');
			setStyle('FeesWithVATWaiver', 'visible', 'true');
			break;
		case 'Agreement Campaign':
			clearValue('AppropriationPriority1');
			clearValue('AppropriationPriority2');
			clearValue('AppropriationPriority3');
			setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('CustID', 'disable', 'true');
			setStyle('UploadCustID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('UploadVIN', 'disable', 'true');
			setStyle('CustID', 'visible', 'false');
			setStyle('UploadCustID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('UploadVIN', 'visible', 'false');
			setStyle('AppropriationPriority1', 'mandatory', 'false');
			setStyle('AppropriationPriority2', 'mandatory', 'false');
			setStyle('AppropriationPriority3', 'mandatory', 'false');
			setStyle('Note', 'visible', 'true');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('RetailPurchaseDiscount', 'visible', 'true');
			setStyle('OccupationSector', 'mandatory', 'true');
			setStyle('EmployeeType', 'mandatory', 'true');
			setStyle('EmployerName', 'mandatory', 'true');
			setStyle('PRFinanceRate', 'visible', 'true');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');
			setStyle('Notes1', 'visible', 'false');
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			setStyle('FeesWithVATWaiver', 'visible', 'true');
			break;
		case '50-50 Campaign':
			setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'false');
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('RetailPurchaseDiscount', 'visible', 'false');
			setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
			setStyle('Note', 'visible', 'false')
			setStyle('CustID', 'disable', 'true');
			setStyle('UploadCustID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('UploadVIN', 'disable', 'true');
			setStyle('CustID', 'visible', 'false');
			setStyle('UploadCustID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('UploadVIN', 'visible', 'false');
			setStyle('PRFinanceRate', 'visible', 'false');
			setStyle('Note', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('Notes1', 'visible', 'false');
			setStyle('AppropriationPriority1', 'mandatory', 'false');
			setStyle('AppropriationPriority2', 'mandatory', 'false');
			setStyle('AppropriationPriority3', 'mandatory', 'false');
			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'true');
			setStyle('FeesWithVATWaiver', 'visible', 'true');
			if (getGridRowCount('VehicleGrid') > 0) {
				setStyle('btn_DownloadExcel', 'disable', 'false');
			} else {
				setStyle('btn_DownloadExcel', 'disable', 'true');
			}
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			break;
		default:
			setStyle('Note', 'visible', 'false');
			setStyle('Notes1', 'visible', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');
			break;
	}

	generateCampCode('', '');
	GlobalCampaignType = Camptype;
	var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
	for (var i = 0; i < callfrom.length; i++) {
		onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
	}
	saveWorkItem();
}

function postHookPickListOk(columns, controlId) {
	if (controlId == 'VehicleGrid_VehicleBrand') {
		setValues({ 'VehicleGrid_VehicleBrand_Code': columns[1] }, true);
	} else if (controlId == 'VehicleGrid_Variant') {
		setValues({ 'VehicleGrid_Variant_Code': columns[1] }, true);
	}
}

function getRandomInt(max) {
	return Math.floor(Math.random() * max);
}

function onChangeFeeWithVAT(CalledFrom) {

	var calledFrom = CalledFrom.split('~')[0];
	var type = CalledFrom.split('~')[1];

	console.log('Inside onChangePRParams');
	if (calledFrom == 'FeeWithVAT') {
		var FeeWithVat = getValue('FeesWithVATWaiver');
		if (getValue('CampaignType') != 'Joint Pricing Campaign') {
			if (type == 'Change') {
				clearValue('FeesWithVATRebate');
				clearValue('FeesWithoutVATRebate');
			}
			if (FeeWithVat == 'Yes') {
				setStyle('FeesWithVATRebate', 'visible', 'true');
				setStyle('FeesWithVATRebate', 'mandatory', 'true');
				setStyle('FeesWithoutVATRebate', 'visible', 'false');
				setStyle('FeesWithoutVATRebate', 'mandatory', 'false');
			} else if (FeeWithVat == 'No') {
				setStyle('FeesWithVATRebate', 'visible', 'false');
				setStyle('FeesWithVATRebate', 'mandatory', 'false');
				setStyle('FeesWithoutVATRebate', 'visible', 'true');
				setStyle('FeesWithoutVATRebate', 'mandatory', 'true');
			} else {
				setStyle('FeesWithVATRebate', 'visible', 'false');
				setStyle('FeesWithVATRebate', 'mandatory', 'false');
				setStyle('FeesWithoutVATRebate', 'visible', 'false');
				setStyle('FeesWithoutVATRebate', 'mandatory', 'false');
			}
		} else {
			setStyle('FeesWithVATRebate', 'visible', 'false');
			setStyle('FeesWithVATRebate', 'mandatory', 'false');
			setStyle('FeesWithoutVATRebate', 'visible', 'false');
			setStyle('FeesWithoutVATRebate', 'mandatory', 'false');
		}
	} else if (calledFrom == 'MonthlywithVAT') {
		if (type == 'Change') {
			clearValue('InstalmentWaiverWithVAT');
			clearValue('InstalmentWaiverWithoutVAT');
		}
		var MonthlywithVAT = getValue('MonthlywithVAT');
		if (MonthlywithVAT == 'Yes') {
			setStyle('InstalmentWaiverWithVAT', 'visible', 'true');
			setStyle('InstalmentWaiverWithVAT', 'mandatory', 'true');
			setStyle('InstalmentWaiverWithoutVAT', 'visible', 'false');
			setStyle('InstalmentWaiverWithoutVAT', 'mandatory', 'false');
		} else if (MonthlywithVAT == 'No') {
			setStyle('InstalmentWaiverWithVAT', 'visible', 'false');
			setStyle('InstalmentWaiverWithVAT', 'mandatory', 'false');
			setStyle('InstalmentWaiverWithoutVAT', 'visible', 'true');
			setStyle('InstalmentWaiverWithoutVAT', 'mandatory', 'true');
		} else {
			setStyle('InstalmentWaiverWithVAT', 'visible', 'false');
			setStyle('InstalmentWaiverWithVAT', 'mandatory', 'false');
			setStyle('InstalmentWaiverWithoutVAT', 'visible', 'false');
			setStyle('InstalmentWaiverWithoutVAT', 'mandatory', 'false');
		}
	}
}

function UploadExcel() {
	console.log('Inside UploadExcel');
	try {
		var fileInput = document.createElement("input");
		fileInput.type = "file";
		fileInput.accept = ".xlsx";
		fileInput.addEventListener("change", function() {
			const allowedExtensions = /(\.xlsx|\.xls)$/i;
			if (!allowedExtensions.exec(fileInput.value)) {
				ShowToastErrorHandleDup(fetchMsg(120));
				return;
			}
			var file = fileInput.files[0];
			var formData = new FormData();
			formData.append("file", file);
			var xhr = new XMLHttpRequest();
			xhr.open("POST", "http://" + window.location.host + "/ALJFS_Campaign/Upload");
			xhr.onload = function() {
				if (xhr.status === 200) {
					var message = xhr.response.replace('\n', '');
					if (message == 'S') {
						var queryresult = executeServerEvent('InsertExcelData', 'Change', '', true);
						if (queryresult == 'F') {
							// showBootBox("", "Error in saving excel data.", "error");
							ShowToastErrorHandleDup(fetchMsg(113));
						} else {
							/*setValues({ "ExcelUploaded": "Y" }, true);*/
							setValues({ 'ExcelUploaded': 'true' }, true);
							// showBootBox("", "Data saved successfully.", "error");
							ShowToastSuccessHandleDup(fetchMsg(114));
						}
					} else {
						ShowToastErrorHandleDup(fetchMsg(113));
						console.log('Error in importData returned F : ');
					}
				} else {
					ShowToastErrorHandleDup(fetchMsg(113));
					console.log('Error in importData: ' + err);
				}
			};
			xhr.send(formData);
		});
		fileInput.click();
	} catch (err) {
		console.log('Error in importData: ' + err);
	}
}

function onChangeAppropriation(CalledFrom) {
	console.log('Inside onChangeAppropriation');
	var Appro = getValue('AppropriationPriority1');
	if (CalledFrom != 'Load') {
		clearComboOptions('AppropriationPriority3');
		clearValue('AppropriationPriority3');
	}
	var Appro = getValue('AppropriationPriority1');
	if (getValue('CampaignType') == 'Joint Pricing Campaign' && getValue('RequestType') == 'New') {
		try {
			if (Appro == '101') {
				setStyle('FeesWithVATWaiver', 'mandatory', 'true');
				setStyle('MonthlywithVAT', 'mandatory', 'false');
				setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
				setStyle('AppropriationPriority3', 'mandatory', 'false');
				setStyle('AppropriationPriority3', 'disable', 'true');
			} else if (Appro == '102') {
				setStyle('MonthlywithVAT', 'mandatory', 'true');
				setStyle('FeesWithVATWaiver', 'mandatory', 'false');
				setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
				setStyle('AppropriationPriority3', 'mandatory', 'false');
				setStyle('AppropriationPriority3', 'disable', 'true');
			} else if (Appro == '100') {
				setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
				setStyle('MonthlywithVAT', 'mandatory', 'false');
				setStyle('FeesWithVATWaiver', 'mandatory', 'false');
				setStyle('AppropriationPriority3', 'mandatory', 'true');
				setStyle('AppropriationPriority3', 'disable', 'false');
			} else {
				setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
				setStyle('FeesWithVATWaiver', 'mandatory', 'false');
				setStyle('MonthlywithVAT', 'mandatory', 'false');
				clearCombo('AppropriationPriority2');
				clearCombo('AppropriationPriority3');
			}
			if (Appro != '') {
				executeServerEvent('onChangePriority1', 'Change', selectedLocale + "~" + CalledFrom, true);
			}
		} catch (err) {
			console.log('Error in onChangeAppropriation: ' + err);
		}
	} else {
		setStyle('FeesWithVATWaiver', 'mandatory', 'false');
		setStyle('MonthlywithVAT', 'mandatory', 'false');
		setStyle('RetailPurchaseDiscount', 'mandatory', 'false');
		setStyle('AppropriationPriority1', 'mandatory', 'false');
		setStyle('AppropriationPriority2', 'mandatory', 'false');
		setStyle('AppropriationPriority3', 'mandatory', 'false');
		if (Appro != '') {
			executeServerEvent('onChangePriority1', 'Change', selectedLocale + "~" + CalledFrom, true);
		}
	}
}



function onChangePriority2(CalledFrom) {
	if (getValue('AppropriationPriority2') != '') {
		executeServerEvent('onChangePriority2', 'Change', selectedLocale + '~' + CalledFrom, true);
	}
}

function validateArabicFields(id) {
	console.log('Inside validateArabicFields');
	if (id != '') {
		try {
			var input = getValue(id);
			const regex = /^[\u0600-\u06FF\u0660-\u0669\d\s!\"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+$/;
			if (regex.test(input)) {
			} else {
				clearValue(id);
				// showBootBox("", "Only Arabic character is applicable", "error");
				ShowToastErrorHandleDup(fetchMsg(115));
			}
		} catch (err) {
			console.log('Error in validateArabicFields: ' + err);
		}
	}
}

function clearOnChangeVehicle(calledFrom) {
	if (calledFrom == 'VehicleGrid_VehicleBrand') {
		clearValue('VehicleGrid_ModelYear');
		clearValue('VehicleGrid_Variant');
		clearValue('VehicleGrid_Variant_Code');
		clearValue('VehicleGrid_Tenure');
		clearValue('VehicleGrid_GracePeriod');
		clearValue('VehicleGrid_DownPayment');
		clearValue('VehicleGrid_FinancePercentage');
		clearValue('VehicleGrid_InsurancePercentage');
		clearValue('VehicleGrid_RVPercentage');
		clearValue('VehicleGrid_SupportAmounttobe');
		clearValue('VehicleGrid_MaximumSupportAmount');
		clearValue('VehicleGrid_DistributorSupportAmount');
		clearValue('VehicleGrid_WSSupportAmount');
		clearValue('VehicleGrid_ALJFSSupportAmount');
		clearValue('VehicleGrid_RetailPurchaseDiscount');
		clearComboOptions('VehicleGrid_Model');
		clearComboOptions('VehicleGrid_ModelSuffix');
		clearComboOptions('VehicleGrid_ModelCode');
		clearComboOptions('VehicleGrid_Colour');
	} else if (calledFrom == 'VehicleGrid_Variant') {
		clearValue('VehicleGrid_ModelYear');
		clearValue('VehicleGrid_Tenure');
		clearValue('VehicleGrid_GracePeriod');
		clearValue('VehicleGrid_DownPayment');
		clearValue('VehicleGrid_FinancePercentage');
		clearValue('VehicleGrid_InsurancePercentage');
		clearValue('VehicleGrid_RVPercentage');
		clearValue('VehicleGrid_SupportAmounttobe');
		clearValue('VehicleGrid_MaximumSupportAmount');
		clearValue('VehicleGrid_DistributorSupportAmount');
		clearValue('VehicleGrid_WSSupportAmount');
		clearValue('VehicleGrid_ALJFSSupportAmount');
		clearValue('VehicleGrid_RetailPurchaseDiscount');
		clearComboOptions('VehicleGrid_Model');
		clearComboOptions('VehicleGrid_ModelSuffix');
		clearComboOptions('VehicleGrid_ModelCode');
		clearComboOptions('VehicleGrid_Colour');
	} else if (calledFrom == 'VehicleGrid_ModelYear') {
		clearValue('VehicleGrid_Tenure');
		clearValue('VehicleGrid_GracePeriod');
		clearValue('VehicleGrid_DownPayment');
		clearValue('VehicleGrid_FinancePercentage');
		clearValue('VehicleGrid_InsurancePercentage');
		clearValue('VehicleGrid_RVPercentage');
		clearValue('VehicleGrid_SupportAmounttobe');
		clearValue('VehicleGrid_MaximumSupportAmount');
		clearValue('VehicleGrid_DistributorSupportAmount');
		clearValue('VehicleGrid_WSSupportAmount');
		clearValue('VehicleGrid_ALJFSSupportAmount');
		clearValue('VehicleGrid_RetailPurchaseDiscount');
		clearComboOptions('VehicleGrid_Model');
		clearComboOptions('VehicleGrid_ModelSuffix');
		clearComboOptions('VehicleGrid_ModelCode');
		clearComboOptions('VehicleGrid_Colour');
	} else if (calledFrom == 'CopyCampaignCode') {
		clearSectionValues('Sec_CampaignInfo', 'CampaignType');
		/*clearSectionValues('Sec_FinanceProductParameter', 'CampaignType');*/
		clearSectionValues('Sec_VehicleParameters', 'CampaignType');
		clearSectionValues('Sec_CustomerParameters', 'CampaignType');
		clearSectionValues('Sec_PriceAndRebateParameters', 'CampaignType');
		var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
		for (var i = 0; i < callfrom.length; i++) {
			onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
		}
		generateCampCode('', '');
		saveWorkItem();
	} else if (calledFrom == 'CampaignNameCombo') {
		clearSectionValues('Sec_CampaignInfo', 'CampaignType');
		/*clearSectionValues('Sec_FinanceProductParameter', 'CampaignType');*/
		clearSectionValues('Sec_VehicleParameters', 'CampaignType');
		clearSectionValues('Sec_CustomerParameters', 'CampaignType');
		clearSectionValues('Sec_PriceAndRebateParameters', 'CampaignType');
		clearValue('CampaignType');
		var callfrom = ['FeeWithVAT', 'MonthlywithVAT'];
		for (var i = 0; i < callfrom.length; i++) {
			onChangeFeeWithVAT(callfrom[i] + '~' + 'Load');
		}
		saveWorkItem();
	}
	if (getValue('CampaignType') == '50-50 Campaign') {
		setValues({ 'VehicleGrid_DownPayment': '50' }, true);
		setValues({ 'VehicleGrid_RVPercentage': '50.00' }, true);
	}
}


function onchangeRange(calledFrom) {
	console.log('Inside onfocuscity');
	var min;
	var max;
	if (calledFrom == 'Age') {
		min = getValue('AgeRangeMin');
		max = getValue('AgeRangeMax');
	} else if (calledFrom == 'Income') {
		min = getValue('IncomeRangeMin');
		max = getValue('IncomeRangeMax');
	}
	if (min != '' && max != '') {
		if (Number(min) > Number(max) && calledFrom == 'Age') {
			ShowToastErrorHandleDup(fetchMsg(116));
			clearValue('AgeRangeMin');
			clearValue('AgeRangeMax');
		} else if (Number(min) > Number(max) && calledFrom == 'Income') {
			ShowToastErrorHandleDup(fetchMsg(117));
			clearValue('IncomeRangeMin');
			clearValue('IncomeRangeMax');
		}
	}
}

function clearSectionValues(controlId, Calledfor) {
	var controls;
	controls = $('#' + controlId + " .control-class");
	for (var i = 0; i < controls.length; i++) {
		if (Calledfor == 'CampaignType' && Calledfor == controls[i].id) {
			continue;
		} else {
			if (controls[i].id != 'RequestType') {
				clearValue(controls[i].id);
			}
		}
	}
	if (controlId == 'Sec_VehicleParameters') {
		clearTable('VehicleGrid');
	}
}

function removeAsteriskSignDoc(row) {
	var tableRow = document.getElementById('inwardDocument_Grid').rows[row];
	var cell = tableRow.cells[2];
	var asterisk = cell.querySelector('.required');
	if (asterisk) {
		cell.removeChild(asterisk);
	}
}


function putAsteriskSignDoc(row) {
	var tableRow = document.getElementById('inwardDocument_Grid').rows[row];
	var cell = tableRow.cells[2];
	var existingText = getValueFromTableCell('inwardDocument_Grid', row - 1, 1);

	cell.textContent = existingText + ' ';
	cell.style.fontSize = '14px';
	cell.style.color = '#000';
	cell.style.fontFamily = 'karbon-regular-webfont';
	var asterisk = document.createElement('span');
	asterisk.textContent = '*';
	asterisk.className = 'required';
	asterisk.style.color = 'red';
	cell.appendChild(asterisk);
}

//stage,decision,comments,username,Datetime
function PopulateHistoryTable() {
	console.log('Inside commentHistoryPopulateAtFormSubmit');
	var UserValue = getWorkItemData("UserName");
	var commentsValue = getValue("Comments");
	var WorkStageName = getWorkItemData('ActivityName');
	var decision = getSelectedItemLabel('DecisionEX');
	var CurrentDate = executeServerEvent('getCurrentDate', 'Click', '', true);
	var jsonObjForGrid = [{
		'Application Stage': WorkStageName,
		'Decision Taken': decision,
		'Comments': commentsValue,
		'Comments By': UserValue,
		'Comments Date': CurrentDate
	}];
	addDataToGrid('table8', jsonObjForGrid);
}

function checkPRParams(controlId) {
	var controls;
	controls = $('#' + controlId + " .control-class");
	var flag = false;
	for (var i = 0; i < controls.length; i++) {
		if (getValue(controls[i].id) != '' && getValue(controls[i].id) != undefined) {
			flag = true;
		}
	}
	if (!flag) {
		return false;
	} else {
		return true;
	}
}



function onChangeChannel() {
	console.log('Inside onChangeChannel');
	var Channels = getValue('Channel');
	for (var j = 0; j < getGridRowCount('inwardDocument_Grid'); j++) {
		removeAsteriskSignDoc(j + 1);
		if (Channels.includes(getValueFromTableCell('inwardDocument_Grid', j, 10))) {
			setTableCellData('inwardDocument_Grid', j, 6, 'Opt');
		} else if ((Channels.includes('MA') || Channels.includes('PO')) && getValueFromTableCell('inwardDocument_Grid', j, 10) == 'ALL') {
			setTableCellData('inwardDocument_Grid', j, 6, 'Opt');
		} else if (Channels.length == 0 && getValueFromTableCell('inwardDocument_Grid', j, 10) != '') {
			setTableCellData('inwardDocument_Grid', j, 6, 'No');
		} else if (getValueFromTableCell('inwardDocument_Grid', j, 10) != '') {
			setTableCellData('inwardDocument_Grid', j, 6, 'No');
		}
		if (getValueFromTableCell('inwardDocument_Grid', j, 6) == 'Yes' || Channels.includes(getValueFromTableCell('inwardDocument_Grid', j, 10)) || getValueFromTableCell('inwardDocument_Grid', j, 6) == 'Opt') {
			putAsteriskSignDoc(j + 1);
		} else if ((Channels.includes('MA') || Channels.includes('PO')) && getValueFromTableCell('inwardDocument_Grid', j, 10) == 'ALL') {
			putAsteriskSignDoc(j + 1);
		}
	}
}

function onChangeRichText(TextAreaID) {
	console.log('Inside onChangeRichText');
	try {
		if (TextAreaID == 'Campaign_Benefits_AR') {
			var input = getRichTextData(TextAreaID);
			if (input != '') {
				input = jQuery('<div>').html(input).text();
				const regex = /^[\u0600-\u06FF\u0660-\u0669\d\s!\"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+$/;
				if (regex.test(input)) {
				} else {
					saveRichTextData('Campaign_Benefits_AR', '');
					// showBootBox("", "Only Arabic character is applicable", "error");
					ShowToastErrorHandleDup(fetchMsg(115));
				}
			}
		} else if (TextAreaID == 'Campaign_Benefits_ENG') {
			var input = getRichTextData(TextAreaID);
			if (input != '') {
				input = jQuery('<div>').html(input).text();
				const regex = /^[a-zA-Z0-9\s !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]*$/;
				if (regex.test(input)) {
				} else {
					saveRichTextData('Campaign_Benefits_ENG', '');
					// showBootBox("", "Only Arabic character is applicable", "error");
					ShowToastErrorHandleDup(fetchMsg(122));
				}
			}
		}
	} catch (err) {
		console.log('Error in validateArabicFields: ' + err);
	}
}

function validateEngFields(id) {
	console.log('Inside validateEngFields');
	if (id != '') {
		try {
			var input = getValue(id);
			const regex = /^[a-zA-Z0-9 !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]*$/;
			if (regex.test(input)) {

			} else {
				clearValue(id);
				ShowToastErrorHandleDup(fetchMsg(122));
			}
		} catch (err) {
			console.log('Error in validateArabicFields: ' + err);
		}
	}
}

//Added for Cash
function onChangeFullyWave() {
	console.log('Inside FullyWave');
	var fullywave = getValue('FullyWave');
	if (getValue('ProductType') == 'CF') {
		if (fullywave == 'No') {
			setStyle('FeesWithVATWaiver', 'visible', 'true');
			setStyle('FeesWithVATWaiver', 'mandatory', 'true');
		} else {
			setStyle('FeesWithVATWaiver', 'visible', 'false');
			setStyle('FeesWithVATWaiver', 'mandatory', 'false');
			clearValue('FeesWithVATWaiver');
			onChangeFeeWithVAT('FeeWithVAT');
		}
	}
}

//Added for Cash
function CheckAdminFee(ControlId) {
	console.log('Inside CheckAdminFee');
	if (getValue(ControlId) != '') {
		if (getValue('ProductType') == 'AF') {
			if (Number(getValue(ControlId)) > 5000) {
				ShowToastWarningHandleDup(fetchMsg(123));
				clearValue(ControlId);
				return;
			}
			/*else if (Number(getValue(ControlId)) < 0) {
				ShowToastWarningHandleDup(fetchMsg(124));
				clearValue(ControlId);
				return;
			}*/
		}
		/*else if (getValue('ProductType') == 'CF' && Number(getValue(ControlId)) < 0) {
			ShowToastWarningHandleDup(fetchMsg(124));
			clearValue(ControlId);
			return;
		}*/
	}
}

//Added for Cash
function CheckFinanceRate() {
	console.log('Inside CheckProfitRate');
	if (getValue('PRFinanceRate') != '') {
		if (getValue('ProductType') == 'CF') {
			var Limit = executeServerEvent('getFinanceRateLimit', 'Change', '', true);
			if (Number(getValue('PRFinanceRate')) > Number(Limit)) {
				ShowToastWarningHandleDup(fetchMsg(125) + ' ' + Limit + '%');
				clearValue('PRFinanceRate');
				return;
			}
		}
	}
}

function checkMandatoryLeft(frameId) {
	var mandatoryFields = $("#" + frameId + " [required=''] ");
	for (var i = 0; i < mandatoryFields.length; i++) {
		var value;
		var blankField = false;
		var blankFieldControl;
		var control = jQuery(mandatoryFields[i]);
		var ctrlType = control.attr("type");
		var iscontrolvisible = isControlVisible(control.get(0), "", blankField, blankFieldControl);
		if (iscontrolvisible) {
			if (ctrlType == "text" || ctrlType == "textarea" || typeof ctrlType == "undefined") {
				value = getControlValue(document.getElementById(control.attr("id")));
				if (typeof ctrlType == "undefined" && value == "")
					value = jQuery(control).val();
				if (value == "" || value == null) {
					return false;
				}
			}
			else if (ctrlType == "radio") {
				if (document.querySelector('input[name="' + control.prop("name") + '"]:checked') == null) {
					return false;
				}
			}
			else if (ctrlType == "checkbox") {
				value = control.prop("checked");
				if (!value) {
					return false;
				}
			}
		}
	}
	var docControl = validateMandatoryDoument(false, frameId);
	if (!docControl) {
		return false;
	}
	return true;
}

function loadCSS() {
	// Create a link element
	var link = document.createElement('link');

	// Set the attributes for the link element
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = `/ALJFS_Campaign/components/viewer/resources/css/CustomLoaderYash.css`; // Adjust the path if needed

	// Append the link element to the head
	document.head.appendChild(link);

	var overlay = document.createElement('div');
	overlay.id = 'overlay';
	document.body.appendChild(overlay);

	// Create loader
	var loader = document.createElement('div');
	loader.id = 'loader';
	var loaderAnimation = document.createElement('div');
	loaderAnimation.className = 'loader-animation';
	loader.appendChild(loaderAnimation);
	// Show loader
	document.body.appendChild(loader);

}
