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
		/*document.getElementById('VehicleGrid_selectedColumn').style.width = "160px"
		document.getElementById('VehicleGrid_selectedColumn').style.marginLeft = "-50px"
		if (getGridRowCount('VehicleGrid') > 0) {
			document.getElementById('VehicleGriddiv_pad').style.display = 'none';
		} else {
			document.getElementById('VehicleGriddiv_pad').style.display = 'block';
		}*/
		//new

		//new end
		var CurrentStage = getWorkItemData('ActivityName');
		setValues({ 'CurrentStatus': CurrentStage }, true);
		// setValues({'UserName':getWorkItemData("username")},true);
		onChangeProductType('Load');
		onChangeRequestType('');
		onChangeCampaignType('Load');
		/*nChangeAppropriation('Load');
		onChangePriority2('Load');*/
		onChangeProvince('Form');
		onChangeWholesaler();
		onChangeModel();
		checkGrid();
		formdisable();
		onChangeFeeWithVAT('Load');
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
	} else if (controlId == 'PRGrid') {
		var returndata = checkPRParams('');
		if (returndata) {
			ShowToastWarningHandleDup('Please add at least one price or rebate parameter: Admin Fee, Monthly Installment, or Retail Purchase Discount.');
			return false;
		}
	} else if (controlId == 'table3_table27') {
		var returndata = checkPRParams('Grid');
		if (returndata) {
			ShowToastWarningHandleDup('Please add at least one price or rebate parameter: Admin Fee, Monthly Installment, or Retail Purchase Discount.');
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
	} else if (controlId == 'CampaignGrid') {
		var Camptype = getValue('CampaignType');
		onChangeGridModelYear();
		onChangeGridModel();
		onChangeProvince('Grid');
		switch (Camptype) {
			case 'Joint Pricing Campaign':
				setStyle('Grid_PRGrid', 'visible', 'true');
				setStyle('Grid_CustomerID', 'disable', 'true');
				setStyle('Grid_btn_UploadCustomerID', 'disable', 'true');
				setStyle('Grid_VINNumber', 'disable', 'true');
				setStyle('Grid_btn_UploadVIN', 'disable', 'true');
				setStyle('Grid_CustomerID', 'visible', 'false');
				setStyle('Grid_btn_UploadCustomerID', 'visible', 'false');
				setStyle('Grid_VINNumber', 'visible', 'false');
				setStyle('Grid_btn_UploadVIN', 'visible', 'false');

				setStyle('Grid_AdminVATWaiver', 'visible', 'false');
				setStyle('Grid_AdminFeesVATAmount', 'visible', 'false');
				setStyle('Grid_AdminFeesWoVATAmount', 'visible', 'false');
				setStyle('Grid_InstalmentVATWaiver', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverVAT', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverWoVAT', 'visible', 'false');
				setStyle('Grid_PurchaseDiscount', 'visible', 'false');
				setStyle('Grid_FinanceRateDiscount', 'visible', 'false');

				setStyle('Grid_OccupationSector', 'mandatory', 'false');
				setStyle('Grid_EmployeeType', 'mandatory', 'false');
				setStyle('Grid_EmployerName', 'mandatory', 'false');
				break;

			case 'General Campaign':

				/*setStyle('Sec_CustomerParameters', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'false');
				setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
				*/
				setStyle('Grid_PRGrid', 'visible', 'true');
				setStyle('Grid_CustomerID', 'disable', 'true');
				setStyle('Grid_btn_UploadCustomerID', 'disable', 'true');
				setStyle('Grid_VINNumber', 'disable', 'true');
				setStyle('Grid_btn_UploadVIN', 'disable', 'true');
				setStyle('Grid_CustomerID', 'visible', 'false');
				setStyle('Grid_btn_UploadCustomerID', 'visible', 'false');
				setStyle('Grid_VINNumber', 'visible', 'false');
				setStyle('Grid_btn_UploadVIN', 'visible', 'false');


				setStyle('Grid_OccupationSector', 'mandatory', 'false');
				setStyle('Grid_EmployeeType', 'mandatory', 'false');
				setStyle('Grid_EmployerName', 'mandatory', 'false');

				/*setStyle('btn_UploadExcel', 'visible', 'false');*/
				setStyle('Grid_AdminVATWaiver', 'visible', 'false');
				setStyle('Grid_AdminFeesVATAmount', 'visible', 'false');
				setStyle('Grid_AdminFeesWoVATAmount', 'visible', 'false');
				setStyle('Grid_InstalmentVATWaiver', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverVAT', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverWoVAT', 'visible', 'false');
				setStyle('Grid_PurchaseDiscount', 'visible', 'false');
				setStyle('Grid_FinanceRateDiscount', 'visible', 'false');


				//For All Campaigns
				setStyle('Grid_VehicleBrand', 'disable', 'true');
				setStyle('Grid_Variant', 'disable', 'true');
				setStyle('Grid_ModelYear', 'disable', 'true');
				setStyle('Grid_Model', 'disable', 'true');
				setStyle('Grid_ModelSuffix', 'disable', 'true');
				setStyle('Grid_ModelCode', 'disable', 'true');
				//End
				setStyle('Grid_VehicleType', 'mandatory', 'true');
				setStyle('Grid_DistributorWise', 'mandatory', 'true');
				/*setStyle('Grid_Wholesalers', 'mandatory', 'true');*/
				setStyle('Grid_VehicleBrand', 'mandatory', 'true');
				setStyle('Grid_Variant', 'mandatory', 'true');
				setStyle('Grid_ModelYear', 'mandatory', 'true');
				setStyle('Grid_Model', 'mandatory', 'true');
				setStyle('Grid_DownPayment', 'mandatory', 'true');
				setStyle('Grid_Tenure', 'mandatory', 'true');
				setStyle('Grid_GracePeriod', 'mandatory', 'true');
				setStyle('Grid_RVPercentage', 'mandatory', 'true');
				setStyle('Grid_FinancePercentage', 'mandatory', 'true');
				setStyle('Grid_InsurancePercentage', 'mandatory', 'true');


				break;
			case 'Special Campaign':

				/*setStyle('Sec_CustomerParameters', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'false');
				setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');*/
				setStyle('Grid_PRGrid', 'visible', 'true');
				setStyle('Grid_CustomerID', 'disable', 'true');
				setStyle('Grid_btn_UploadCustomerID', 'disable', 'true');
				setStyle('Grid_VINNumber', 'disable', 'true');
				setStyle('Grid_btn_UploadVIN', 'disable', 'true');
				setStyle('Grid_CustomerID', 'visible', 'false');
				setStyle('Grid_btn_UploadCustomerID', 'visible', 'false');
				setStyle('Grid_VINNumber', 'visible', 'false');
				setStyle('Grid_btn_UploadVIN', 'visible', 'false');

				setStyle('Grid_OccupationSector', 'mandatory', 'false');
				setStyle('Grid_EmployeeType', 'mandatory', 'false');
				setStyle('Grid_EmployerName', 'mandatory', 'false');

				/*setStyle('btn_UploadExcel', 'visible', 'false');*/

				setStyle('Grid_AdminVATWaiver', 'visible', 'false');
				setStyle('Grid_AdminFeesVATAmount', 'visible', 'false');
				setStyle('Grid_AdminFeesWoVATAmount', 'visible', 'false');
				setStyle('Grid_InstalmentVATWaiver', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverVAT', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverWoVAT', 'visible', 'false');
				setStyle('Grid_PurchaseDiscount', 'visible', 'false');
				setStyle('Grid_FinanceRateDiscount', 'visible', 'false');

				break;
			case 'Agreement Campaign':
				/*setStyle('Sec_CustomerParameters', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'false');
				setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');*/
				setStyle('Grid_PRGrid', 'visible', 'true');
				setStyle('Grid_CustomerID', 'disable', 'true');
				setStyle('Grid_btn_UploadCustomerID', 'disable', 'true');
				setStyle('Grid_VINNumber', 'disable', 'true');
				setStyle('Grid_btn_UploadVIN', 'disable', 'true');
				setStyle('Grid_CustomerID', 'visible', 'false');
				setStyle('Grid_btn_UploadCustomerID', 'visible', 'false');
				setStyle('Grid_VINNumber', 'visible', 'false');
				setStyle('Grid_btn_UploadVIN', 'visible', 'false');

				setStyle('Grid_OccupationSector', 'mandatory', 'true');
				setStyle('Grid_EmployeeType', 'mandatory', 'true');
				setStyle('Grid_EmployerName', 'mandatory', 'true');

				/*setStyle('btn_UploadExcel', 'visible', 'false');*/

				setStyle('Grid_AdminVATWaiver', 'visible', 'false');
				setStyle('Grid_AdminFeesVATAmount', 'visible', 'false');
				setStyle('Grid_AdminFeesWoVATAmount', 'visible', 'false');
				setStyle('Grid_InstalmentVATWaiver', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverVAT', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverWoVAT', 'visible', 'false');
				setStyle('Grid_PurchaseDiscount', 'visible', 'false');
				setStyle('Grid_FinanceRateDiscount', 'visible', 'false');

				break;
			case '50-50 Campaign':

				/*setStyle('Sec_CustomerParameters', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'false');
				setStyle('Sec_PriceAndRebateParameters', 'visible', 'false');*/
				setStyle('Grid_PRGrid', 'visible', 'true');
				setStyle('Grid_CustomerID', 'disable', 'true');
				setStyle('Grid_btn_UploadCustomerID', 'disable', 'true');
				setStyle('Grid_VINNumber', 'disable', 'true');
				setStyle('Grid_btn_UploadVIN', 'disable', 'true');
				setStyle('Grid_CustomerID', 'visible', 'false');
				setStyle('Grid_btn_UploadCustomerID', 'visible', 'false');
				setStyle('Grid_VINNumber', 'visible', 'false');
				setStyle('Grid_btn_UploadVIN', 'visible', 'false');

				setStyle('Grid_OccupationSector', 'mandatory', 'false');
				setStyle('Grid_EmployeeType', 'mandatory', 'false');
				setStyle('Grid_EmployerName', 'mandatory', 'false');
				/*setStyle('btn_UploadExcel', 'visible', 'false');*/

				setStyle('Grid_AdminVATWaiver', 'visible', 'false');
				setStyle('Grid_AdminFeesVATAmount', 'visible', 'false');
				setStyle('Grid_AdminFeesWoVATAmount', 'visible', 'false');
				setStyle('Grid_InstalmentVATWaiver', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverVAT', 'visible', 'false');
				setStyle('Grid_InstalmentWaiverWoVAT', 'visible', 'false');
				setStyle('Grid_PurchaseDiscount', 'visible', 'false');
				setStyle('Grid_FinanceRateDiscount', 'visible', 'false');

				break;
			default:

				break;
		}
		if (getValue('Grid_VehicleBrand') == 'All' || getValue('Grid_Variant') == 'All' || getValue('Grid_ModelYear') == 'All') {
			setStyle('Grid_RetailPrice', 'disable', 'true');
		}
		onChangeFeeWithVAT('Load');
	} else if (controlId == 'PRGrid') {
		onChangeAdminFeeType('');
		onChangeInstallmentType('');
		onChangeSupportType('', 'Load');
		try {
			setStyle('duplicateAdvancedListviewchanges_PRGrid', 'visible', 'false');
		} catch (error) {
		}
		setStyle('addAdvancedListviewrowNext_PRGrid', 'visible', 'false');
		//addAdvancedListviewrowNext_PRGrid
	} else if (controlId == 'table3_table27') {
		onChangeAdminFeeType('Grid');
		onChangeInstallmentType('Grid');
		onChangeSupportType('Grid', 'Load');
		setStyle('copyrow_table3_table27', 'visible', 'false');
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
			//generateCampCode();
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
		var ModelYear = getValue('ModelYear');
		var Model = getValue('Model');
		var colour = getValue("Colour");
		var ModelSuffix = getValue("ModelSuffix");
		var ModelCode = getValue("ModelCode");
		/*clearComboOptions('VehicleGrid_Model');*/
		if (getValue('VehicleBrand') == 'All' || getValue('Variant') == 'All' || getValue('ModelYear') == 'All') {
			Model = '0';
			colour = '0';
			ModelSuffix = '0';
			ModelCode = '0';
			setStyle('Model', 'disable', 'true');
			setStyle('Colour', 'disable', 'true');
			setStyle('ModelCode', 'disable', 'true');
			setStyle('ModelSuffix', 'disable', 'true');
		} else {
			setStyle('Model', 'disable', 'false');
		}
		if (ModelYear != '') {
			executeServerEvent('onChangeModelYear', 'Change', ModelYear + '~' + selectedLocale, true);
			setValues({ "Model": Model }, true);
			if (Model != '') {
				onChangeModel();
				setValues({ "Colour": colour }, true);
				setValues({ "ModelSuffix": ModelSuffix }, true);
				setValues({ "ModelCode": ModelCode }, true);
			}
		}
	} catch (err) {
		console.log('Error in onChangeModel: ' + err);
	}
}

function onChangeModel() {
	console.log('onChangeModel');
	try {
		setStyle('Colour', 'disable', 'true');
		setStyle('ModelCode', 'disable', 'true');
		setStyle('ModelSuffix', 'disable', 'true');
		var Model = getValue('Model');
		/*		var colour = getValue("Colour");
				var ModelSuffix = getValue("ModelSuffix");
				var ModelCode = getValue("ModelCode");*/
		clearComboOptions('Colour');
		clearComboOptions('ModelSuffix');
		clearComboOptions('ModelCode');
		if (Model != '') {
			executeServerEvent('onChangeModel', 'Change', Model + '~' + selectedLocale, true);
			/*setValues({ "Colour": colour }, true);
			setValues({ "ModelSuffix": ModelSuffix }, true);
			setValues({ "ModelCode": ModelCode }, true);*/
		}
	} catch (err) {
		console.log('Error in onChangeModel: ' + err);
	}
}

function onChangeGridModelYear() {
	console.log('onChangeModelYear');
	try {
		var ModelYear = getValue('Grid_ModelYear');
		var Model = getValue('Grid_Model');
		var colour = getValue("Grid_Colour");
		var ModelSuffix = getValue("Grid_ModelSuffix");
		var ModelCode = getValue("Grid_ModelCode");
		if (getValue('Grid_VehicleBrand') == 'All' || getValue('Grid_Variant') == 'All' || getValue('Grid_ModelYear') == 'All') {
			setStyle('Grid_Model', 'disable', 'true');
			setStyle('Grid_Colour', 'disable', 'true');
			setStyle('Grid_ModelCode', 'disable', 'true');
			setStyle('Grid_ModelSuffix', 'disable', 'true');
		}
		/*clearComboOptions('VehicleGrid_Model');*/
		if (ModelYear != '') {
			executeServerEvent('onChangeGridModelYear', 'Change', ModelYear + '~' + selectedLocale, true);
			setValues({ "Grid_Model": Model }, true);
			if (Model != '') {
				onChangeGridModel();
				setValues({ "Grid_Colour": colour }, true);
				setValues({ "Grid_ModelSuffix": ModelSuffix }, true);
				setValues({ "Grid_ModelCode": ModelCode }, true);
			}
		}
	} catch (err) {
		console.log('Error in onChangeGridModel: ' + err);
	}
}

function onChangeGridModel() {
	console.log('onChangeModel');
	try {
		var Model = getValue('Grid_Model');
		/*var colour = getValue("Grid_Colour");
		var ModelSuffix = getValue("Grid_ModelSuffix");
		var ModelCode = getValue("Grid_ModelCode");*/
		clearComboOptions('Grid_Colour');
		clearComboOptions('Grid_ModelSuffix');
		clearComboOptions('Grid_ModelCode');
		if (Model != '') {
			executeServerEvent('onChangeGridModel', 'Change', Model + '~' + selectedLocale, true);
			/*setValues({ "Grid_Colour": colour }, true);
			setValues({ "Grid_ModelSuffix": ModelSuffix }, true);
			setValues({ "Grid_ModelCode": ModelCode }, true);*/
		}
	} catch (err) {
		console.log('Error in onChangeGridModel: ' + err);
	}
}


function addRowPostHook(tableId) {
	console.log('Inside addRowPostHook : ' + tableId);
	console.log('Gridrow Count : ' + getGridRowCount(tableId));
	if (tableId == 'PRGrid') {
		console.log('Inside PRGRid');
	}
}

function generateCampCode() {
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
			rowCount = getGridRowCount('CampaignGrid');
			if (getValue('ProductType') != '' && (rowCount > 0 || flag == true)) {
				executeServerEvent('generateCampCode', 'Change', '', true);
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
	console.log('Inside DownloadExcel');
	try {
		var selectedcount = getSelectedRowsIndexes('CampaignGrid');
		var AllPresent = false;
		if (selectedcount.length > 0) {
			for (var i = 0; i < selectedcount.length; i++) {
				if (getValueFromTableCell('CampaignGrid', selectedcount[i], 0) == 'All' || getValueFromTableCell('CampaignGrid', selectedcount[i], 1) == 'All' || getValueFromTableCell('CampaignGrid', selectedcount[i], 2) == 'All') {
					AllPresent = true;
					break;
				}
			}
			if (!AllPresent) {
				saveWorkItem();
				var selectedRows = getSelectedRowsIndexes('CampaignGrid');
				var data = executeServerEvent('getExcelData', 'Click', selectedRows, true);
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
			} else {
				/*ShowToastWarningHandleDup("Simulation cannot be performed for All items in brand, variant, or year.");*/
				ShowToastWarningHandleDup("Simulation cannot be carried out for All selections in brand, variant, or year.");
			}
		} else {
			ShowToastWarningHandleDup('Please select atleast one row to view simulation');
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

		if (getValue('ProductType') == 'AF' && getGridRowCount('CampaignGrid') == 0) {
			showBootBox("CampaignGrid", "Please Add atleast one row in Campaign Parameters table", "error");
			return;
		}

		populateDocumentMaster();
		if (getValue('DecisionEX') != 'Reject' && getValue('DecisionEX') != 'Send Back') {
			var mandatoryStatus = checkMandatoryFields("Sec_FinanceProductParameter");
			if (!mandatoryStatus) {
				return;
			}
			mandatoryStatus = checkMandatoryFields('Sec_CampaignInfo');
			if (!mandatoryStatus) {
				return;
			}
			/*var PRParamcheck = checkPRParams('Sec_PriceAndRebateParameters');
			if (!PRParamcheck && getValue('CampaignType') != '50-50 Campaign') {
				showBootBox("FeesWithVATWaiver", fetchMsg(119), "error");
				return;
			}*/

			var mandatorydoc = mandatoryDocCheck();
			if (mandatorydoc != '' && mandatorydoc != undefined) {
				var mandatorydocData = mandatorydoc.split('~');
				if (mandatorydocData[0] == 'F' && getValue('AutoGenerated') != 'Y') {
					showBootBox("inwardDocument_Grid", fetchMsg("104") + " - " + mandatorydocData[1] + ".", "error");
					//showBootBox("inwardDocument_Grid", fetchMsg("104"), "error");
					return;
				}
			}
			skipValidation();
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
				ShowToastWarningHandleDup(fetchMsg(105));
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
		for (var i = 0; i < getGridRowCount('CampaignGrid'); i++) {
			setStyle('CampaignGrid_' + i, 'disable', 'false');
		}
		document.getElementById('delete_CampaignGrid').style.display = 'none';
	} else if (sActivityName == 'Exit') {
		setValues({ 'DecisionEX': 'Approve' }, true);
	} else if (sActivityName == 'Discard') {
		setValues({ 'DecisionEX': 'Reject' }, true);
	}
}

function deleteRowPostHook(tableId, rowIndices) {
	generateCampCode();
	if (tableId == 'CampaignGrid') {
		/*setValues({ 'ExcelUploaded': 'N' }, true);*/
		/*setValues({ 'ExcelUploaded': 'false' }, true);*/
		if (getValue('ProductType') == 'AF') {
			if (getGridRowCount('CampaignGrid') > 0) {
				/*setStyle('btn_UploadExcel', 'disable', 'false');*/
				setStyle('btn_DownloadExcel', 'disable', 'false');
			} else {
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				/*setStyle('ExcelUploaded', 'disable', 'true');*/
				setStyle('btn_DownloadExcel', 'disable', 'true');
			}
		}
	} else if (tableId == 'PRGrid') {
		setStyle('add_PRGrid', 'disable', 'false');
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
				clearSectionValues('Sec_CampaignParameters', '');
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				/*setStyle('ExcelUploaded', 'disable', 'true');*/
				setStyle('btn_DownloadExcel', 'disable', 'true');
				setStyle('Sec_CampaignParameters', 'visible', 'false');
				onChangeFeeWithVAT('Load');
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
				clearTable('CampaignGrid');
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
				generateCampCode();
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
				if (data == 'F') {
					// showMessage("", serverdata[1], "error");
					ShowToastErrorHandleDup(fetchMsg(106))
					clearValue('CampaignName');
					clearValue('CampaignNameCombo');
					document.getElementById('overlay').style.display = 'none';
					document.getElementById('loader').style.display = 'none';
					return;
				} else if (data == 'S') {
					if (CalledFrom == 'CopyCampaignCode') {
						clearValue('CampaignName');
						clearValue('CampaignNameCombo');
					}
					generateCampCode();
					onChangeRequestType('');
					onChangeWholesaler();
					onChangeProvince('Form');
					onChangeCampaignType('');
					//onChangeProductType('Load');
					if (showmsg == 'Y') {
						showMessage("", "The selected Campaign has a StartDate of " + getValue('StartDate') + " and an EndDate of " + getValue('EndDate'), "error");
					}
					clearValue('StartDate');
					clearValue('EndDate');
				}
				onChangeFullyWave();
				onChangeFeeWithVAT('Load');
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
		clearAddedValues();
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
		clearAddedValues();
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

function SupportAmount(calledFrom) {
	try {
		if (calledFrom == 'Grid') {
			var SupportAmounttobe = getValue('table3_table27_SupportType');
			var AdminAmount = Number(getValue('table3_table27_AdminFeesDiscount'));
			var RetailAmount = Number(getValue('table3_table27_RetailPurchaseDiscount'));
			var CashbackAmount = Number(getValue('table3_table27_Cashback'));
			if (SupportAmounttobe == 'Percentage') { //Absolute value
				if (!(AdminAmount >= 0 && AdminAmount <= 100) || !(RetailAmount >= 0 && RetailAmount <= 100) || !(CashbackAmount >= 0 && CashbackAmount <= 100)) {
					ShowToastErrorHandleDup("Admin Fees Discount/Retail Purchase Discount/Cashback should be equals to or less then 100%.");
					clearValue('table3_table27_AdminFeesDiscount');
					clearValue('table3_table27_RetailPurchaseDiscount');
					clearValue('table3_table27_Cashback');
				}
			} else if (SupportAmounttobe == 'Absolute value') {
				if (!(AdminAmount >= 0 && AdminAmount <= 5000)) {
					ShowToastErrorHandleDup("Admin Fees Discount should not be less than 0 or more than 5000.");
					clearValue('table3_table27_AdminFeesDiscount');
				}
			}
		} else {
			var SupportAmounttobe = getValue('Q_CMPLX_PRParameters_SupportType');
			var AdminAmount = Number(getValue('AdminFeesDiscount'));
			var RetailAmount = Number(getValue('Q_CMPLX_PRParameters_RetailPurchaseDiscount'));
			var CashbackAmount = Number(getValue('Q_CMPLX_PRParameters_CashBack'));
			if (SupportAmounttobe == 'Percentage') { //Absolute value
				if (!(AdminAmount >= 0 && AdminAmount <= 100) || !(RetailAmount >= 0 && RetailAmount <= 100) || !(CashbackAmount >= 0 && CashbackAmount <= 100)) {
					ShowToastErrorHandleDup("Admin Fees Discount/Retail Purchase Discount/Cashback should be equals to or less then 100%.");
					clearValue('AdminFeesDiscount');
					clearValue('Q_CMPLX_PRParameters_RetailPurchaseDiscount');
					clearValue('Q_CMPLX_PRParameters_CashBack');
				}
			} else if (SupportAmounttobe == 'Absolute value') {
				if (!(AdminAmount >= 0 && AdminAmount <= 5000)) {
					ShowToastErrorHandleDup("Admin Fees Discount should not be less than 0 or more than 5000.");
					clearValue('AdminFeesDiscount');
				}
			}
		}
	} catch (err) {
		console.log('Error in SupportAmount: ' + err);
	}
}

function onChangeMaxAdminFee(calledFrom) {
	var AdminAmount = 0;
	if (calledFrom == 'Grid') {
		AdminAmount = Number(getValue('table3_table27_MaximumAdminFeeSupport'));
		if (!(AdminAmount >= 0 && AdminAmount <= 5000)) {
			ShowToastErrorHandleDup("Admin Fees Max Discount should not be less than 0 or more than 5000.");
			clearValue('table3_table27_MaximumAdminFeeSupport');
		}
	} else {
		AdminAmount = Number(getValue('Q_CMPLX_PRParameters_MaximumAdminFee'));
		if (!(AdminAmount >= 0 && AdminAmount <= 5000)) {
			ShowToastErrorHandleDup("Admin Fees Max Discount should not be less than 0 or more than 5000.");
			clearValue('Q_CMPLX_PRParameters_MaximumAdminFee');
		}
	}
}

function onChangeSupportType(calledFrom, Event) {
	var AdminFeeType = "";
	if (calledFrom == 'Grid') {
		AdminFeeType = getValue('table3_table27_AdminFeesVATWaiver');
		if (getValue('table3_table27_SupportType') == 'Percentage' && AdminFeeType != '') {
			setStyle('table3_table27_MaximumAdminFeeSupport', 'visible', 'true');
			setStyle('table3_table27_MaximumAdminFeeSupport', 'mandatory', 'true');
		} else {
			setStyle('table3_table27_MaximumAdminFeeSupport', 'visible', 'false');
			setStyle('table3_table27_MaximumAdminFeeSupport', 'mandatory', 'false');
		}
		if (Event == 'Change') {
			clearValue('table3_table27_AdminFeesDiscount');
			clearValue('table3_table27_RetailPurchaseDiscount');
			clearValue('table3_table27_Cashback');
		}
		if (getValue('table3_table27_SupportType') == 'Percentage') {
			document.getElementById('table3_table27_AdminFeesDiscount_label').textContent = 'Admin Fees Support %';
			document.getElementById('table3_table27_RetailPurchaseDiscount_label').textContent = 'Rebate %';
			document.getElementById('table3_table27_Cashback_label').textContent = 'Cashback %';
		}
	} else {
		AdminFeeType = getValue('FeesWithVATWaiver');
		if (getValue('Q_CMPLX_PRParameters_SupportType') == 'Percentage' && AdminFeeType != '') {
			setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'visible', 'true');
			setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'mandatory', 'true');
		} else {
			setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'visible', 'false');
			setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'mandatory', 'false');
		}
		if (Event == 'Change') {
			clearValue('AdminFeesDiscount');
			clearValue('Q_CMPLX_PRParameters_RetailPurchaseDiscount');
			clearValue('Q_CMPLX_PRParameters_CashBack');
		}
		if (getValue('Q_CMPLX_PRParameters_SupportType') == 'Percentage') {
			document.getElementById('AdminFeesDiscount_label').textContent = 'Admin Fees Support %';
			document.getElementById('Q_CMPLX_PRParameters_RetailPurchaseDiscount_label').textContent = 'Rebate %';
			document.getElementById('Q_CMPLX_PRParameters_CashBack_label').textContent = 'Cashback %';
		}
	}
}

function isValidSequence() {
	var data = validateRegex();
	if (!data) {
		clearValue('table3_table27_Installment');
		ShowToastErrorHandleDup('Please enter a comma-separated list of numbers between 1 and 6, with no duplicates.');
	}
}


function validateRegex() {
	var sequence = getValue('table3_table27_Installment');
	if (sequence != '') {
		const pattern = /^(?:[1-6](?:,[1-6])*)?$/;
		if (!pattern.test(sequence)) {
			return false;
		}
		// Split the numbers and check for uniqueness
		const numbers = sequence.split(',');
		const uniqueNumbers = new Set(numbers);
		return numbers.length === uniqueNumbers.size;
	}
}


function onChangeAdminFeeType(calledFrom) {
	var AdminFeeType = "";
	if (calledFrom == 'Grid') {
		AdminFeeType = getValue('table3_table27_AdminFeesVATWaiver');
		if (AdminFeeType != '') {
			setStyle('table3_table27_AdminFeesDiscount', 'visible', 'true');
			setStyle('table3_table27_AdminFeesDiscount', 'mandatory', 'true');
			if (getValue('table3_table27_SupportType') == 'Percentage') {
				setStyle('table3_table27_MaximumAdminFeeSupport', 'visible', 'true');
				setStyle('table3_table27_MaximumAdminFeeSupport', 'mandatory', 'true');
			} else {
				setStyle('table3_table27_MaximumAdminFeeSupport', 'visible', 'false');
				setStyle('table3_table27_MaximumAdminFeeSupport', 'mandatory', 'false');
			}
		} else {
			setStyle('table3_table27_AdminFeesDiscount', 'visible', 'false');
			setStyle('table3_table27_AdminFeesDiscount', 'mandatory', 'false');
		}
	} else {
		AdminFeeType = getValue('FeesWithVATWaiver');
		if (AdminFeeType != '') {
			setStyle('AdminFeesDiscount', 'visible', 'true');
			setStyle('AdminFeesDiscount', 'mandatory', 'true');
			if (getValue('Q_CMPLX_PRParameters_SupportType') == 'Percentage') {
				setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'visible', 'true');
				setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'mandatory', 'true');
			} else {
				setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'visible', 'false');
				setStyle('Q_CMPLX_PRParameters_MaximumAdminFee', 'mandatory', 'false');
			}
		} else {
			setStyle('AdminFeesDiscount', 'visible', 'false');
			setStyle('AdminFeesDiscount', 'mandatory', 'false');
		}
	}
}

function onChangeInstallmentType(calledFrom) {
	var installmenttype = "";
	if (calledFrom == 'Grid') {
		installmenttype = getValue('table3_table27_MonthlyInstalmentVATWaiver');
		if (installmenttype != '') {
			setStyle('table3_table27_Installment', 'visible', 'true');
			setStyle('table3_table27_Installment', 'mandatory', 'true');
		} else {
			setStyle('table3_table27_Installment', 'visible', 'false');
			setStyle('table3_table27_Installment', 'mandatory', 'false');
		}
	} else {
		installmenttype = getValue('MonthlywithVAT');
		if (installmenttype != '') {
			setStyle('MonthlyInstalments', 'visible', 'true');
			setStyle('Q_CMPLX_PRParameters_MaximumInstalmentSupport', 'visible', 'true');
			setStyle('MonthlyInstalments', 'mandatory', 'true');
			setStyle('Q_CMPLX_PRParameters_MaximumInstalmentSupport', 'mandatory', 'true');
		} else {
			setStyle('MonthlyInstalments', 'visible', 'false');
			setStyle('Q_CMPLX_PRParameters_MaximumInstalmentSupport', 'visible', 'false');
			setStyle('MonthlyInstalments', 'mandatory', 'false');
			setStyle('Q_CMPLX_PRParameters_MaximumInstalmentSupport', 'mandatory', 'false');
		}
	}
}

function modifyRowPostHook(controlId) {
	if (controlId == 'VehicleGrid') {
		//generateCampCode();
		/*setValues({ 'ExcelUploaded': 'N' }, true);*/
		/*setValues({ 'ExcelUploaded': 'false' }, true);*/
	}
}


function tableOperation(tableId, operationType) {
	if (tableId == 'CampaignGrid') {
		/*if (operationType == "AddRow") {
			generateCampCode('Add', getValue('VehicleGrid_VehicleBrand'));
			setValues({ 'ExcelUploaded': 'N' }, true);
			setValues({ 'ExcelUploaded': 'false' }, true);
			setStyle('btn_UploadExcel', 'disable', 'false');
			setStyle('ExcelUploaded', 'disable', 'false');
			setStyle('btn_DownloadExcel', 'disable', 'false');
			// New ended by Yash
		} else */
		if (operationType == "DeleteRow") {
			if (confirm('The selected row(s) will be deleted, Do you want to continue?')) {
				return true;
			} else {
				return false;
			}
		}
	} else if (tableId == 'PRGrid') {
		if (operationType == "AddRow") {
			var count = Number(getGridRowCount('PRGrid')) + 1;
			setStyle('add_PRGrid', 'disable', 'true');
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
				clearSectionValues('Sec_CampaignParameters', '');
				setStyle('Sec_CampaignParameters', 'visible', 'false');
				clearValue('RequestType');
				onChangeFeeWithVAT('Load');
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
			if (ProductType == 'CF') {
				setStyle('VehicleType', 'visible', 'false');
				setStyle('VehicleType', 'mandatory', 'false');
				setStyle('VINNumber', 'visible', 'false');
				setStyle('UploadVIN', 'visible', 'false');

				setStyle('VehicleBrand', 'visible', 'false');
				setStyle('Variant', 'visible', 'false');
				setStyle('ModelYear', 'visible', 'false');
				setStyle('Model', 'visible', 'false');
				setStyle('ModelSuffix', 'visible', 'false');
				setStyle('ModelCode', 'visible', 'false');
				setStyle('Colour', 'visible', 'false');
				setStyle('DownPayment', 'visible', 'false');
				setStyle('Tenure', 'visible', 'false');
				setStyle('GracePeriod', 'visible', 'false');
				setStyle('RVPercentage', 'visible', 'false');
				setStyle('FinancePercentage', 'visible', 'false');
				setStyle('InsurancePercentage', 'visible', 'false');
				setStyle('PRGrid', 'visible', 'false');
				setStyle('CampaignGrid', 'visible', 'false');
				setStyle('InstalmentVATWaiver', 'visible', 'false');
				setStyle('FinanceRateDiscount', 'visible', 'true');
				setStyle('PurchaseDiscount', 'visible', 'false');
				setStyle('btn_AddGrid', 'visible', 'false');

				setStyle('DistributorWise', 'visible', 'false');
				setStyle('WholesalerBranch', 'visible', 'false');
				setStyle('Wholesalers', 'visible', 'false');
				setStyle('FullyWave', 'visible', 'true');
				/*setStyle('Sec_AppropriationParameters', 'visible', 'false');*/
				setStyle('Notes1', 'visible', 'false');
				setStyle('Note', 'visible', 'false');
				setStyle('CustID', 'visible', 'false');
				setStyle('UploadCustID', 'visible', 'false');


				/*setStyle('ExcelUploaded', 'visible', 'false');
				setStyle('ExcelUploaded', 'mandatory', 'false')*/;
				setStyle('btn_DownloadExcel', 'visible', 'false');


			} else {
				setStyle('VehicleGrid', 'visible', 'true');
				setStyle('VehicleType', 'visible', 'true');
				setStyle('VehicleType', 'mandatory', 'false');
				setStyle('VINNumber', 'visible', 'true');
				setStyle('UploadVIN', 'visible', 'true');

				setStyle('VehicleBrand', 'visible', 'true');
				setStyle('Variant', 'visible', 'true');
				setStyle('ModelYear', 'visible', 'true');
				setStyle('Model', 'visible', 'true');
				setStyle('ModelSuffix', 'visible', 'true');
				setStyle('ModelCode', 'visible', 'true');
				setStyle('Colour', 'visible', 'true');
				setStyle('DownPayment', 'visible', 'true');
				setStyle('Tenure', 'visible', 'true');
				setStyle('GracePeriod', 'visible', 'true');
				setStyle('RVPercentage', 'visible', 'true');
				setStyle('FinancePercentage', 'visible', 'true');
				setStyle('InsurancePercentage', 'visible', 'true');
				setStyle('PRGrid', 'visible', 'true');
				setStyle('CampaignGrid', 'visible', 'true');
				setStyle('FinanceRateDiscount', 'visible', 'false');
				setStyle('InstalmentVATWaiver', 'visible', 'false');
				setStyle('PurchaseDiscount', 'visible', 'false');
				setStyle('AdminVATWaiver', 'visible', 'false');
				setStyle('FullyWave', 'visible', 'false');
				setStyle('btn_AddGrid', 'visible', 'true');

				setStyle('DistributorWise', 'visible', 'true');
				setStyle('WholesalerBranch', 'visible', 'false');
				setStyle('Wholesalers', 'visible', 'true');
				setStyle('Sec_AppropriationParameters', 'visible', 'true');

				/*	setStyle('ExcelUploaded', 'visible', 'true');
					setStyle('ExcelUploaded', 'mandatory', 'true');*/
				setStyle('btn_DownloadExcel', 'visible', 'true');

				if (getGridRowCount('CampaignGrid') > 0) {
					/*setStyle('btn_UploadExcel', 'disable', 'false');*/
					/*setStyle('ExcelUploaded', 'disable', 'false');*/
					setStyle('btn_DownloadExcel', 'disable', 'false');
				} else {
					/*setStyle('btn_UploadExcel', 'disable', 'true');*/
					/*setStyle('ExcelUploaded', 'disable', 'true');*/
					setStyle('btn_DownloadExcel', 'disable', 'true');
				}
				if (getValue('AutoGenerated') == 'Y') {
					/*setStyle('ExcelUploaded', 'visible', 'false');
					setStyle('ExcelUploaded', 'mandatory', 'false');*/
					setStyle('btn_DownloadExcel', 'visible', 'false');
				}

			}
			//Cash END

			executeServerEvent('onChangeProductType', 'Change', selectedLocale, true);
			setValues({ "ProductSubType": ProductSubType }, true);
			setValues({ "Channel": Channel }, true);
			if (CalledFrom != 'Load') {
				generateCampCode();
				saveWorkItem();
			} else {
				setValues({ "CampaignType": CampaignType }, true);
			}
		} else {
			//Cash Start
			setStyle('Sec_CampaignInfo', 'visible', 'false');
			setStyle('Sec_CampaignParameters', 'visible', 'false');
			//Cash END
			clearComboOptions('ProductSubType');
		}
		GlobalProductType = getValue('ProductType');
	} catch (err) {
		console.log('Error in onChangeProductType: ' + err);
	}
}

function onChangeProvince(calledFrom) {
	try {
		var CityID = '';
		var ProvinceID = '';
		if (calledFrom == 'Grid') {
			var Province = getValue('Grid_Province');
			var City = getValue('Grid_City');
			CityID = 'Grid_City';
			ProvinceID = 'Grid_Province';
		} else {
			var Province = getValue('Province');
			var City = getValue('City');
			CityID = 'City';
			ProvinceID = 'Province';
		}
		if (Province != '') {
			executeServerEvent('onChangeProvince', 'Change', Province + '~' + calledFrom + '~' + selectedLocale, true);
			if (calledFrom == 'Grid') {
				setValues({ 'Grid_City': City }, true);
			} else {
				setValues({ 'City': City }, true);
			}
		} else {
			if (calledFrom == 'Grid') {
				clearComboOptions('Grid_City');;
			} else {
				clearComboOptions('City');
			}

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
				clearSectionValues('Sec_CampaignParameters', 'CampaignType');
				/*setStyle('btn_UploadExcel', 'disable', 'true');*/
				/*setStyle('ExcelUploaded', 'disable', 'true');*/
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
			clearTable('CampaignGrid');
		} else {
			CampaignTypeCondition();
		}
		makeMandatory();
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
		setStyle('Sec_CampaignParameters', 'visible', 'true');
		setStyle('CampaignNameCombo', 'visible', 'false');
		setStyle('CampaignNameCombo', 'mandatory', 'false');
		setStyle('CampaignName', 'visible', 'true');
		setStyle('CampaignName', 'mandatory', 'true');
		setStyle('CampaignCode', 'disable', 'true');
		/*setStyle('ALJFSSupportAmount', 'disable', 'true');*/
		/*setStyle('CopyCampaignCode', 'disable', 'false');*/
		setStyle('RequestType', 'disable', 'false');
		if (Reqtype != '') {
			setStyle('CampaignType', 'disable', 'false');
		} else {
			setStyle('CampaignType', 'disable', 'true');
		}
	} else if (Camptype != '' && Reqtype == 'Modify') {
		setStyle('sec_Basic', 'disable', 'true');
		setStyle('Sec_CampaignParameters', 'visible', 'true');
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
		setStyle('Sec_CampaignParameters', 'visible', 'false');
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
			/*setStyle('Sec_CustomerParameters', 'visible', 'false');
			setStyle('Sec_AppropriationParameters', 'visible', 'true');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');*/
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('Note', 'visible', 'false');

			setStyle('CustomerID', 'disable', 'true');
			setStyle('btn_UploadCustomerID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('btn_UploadVIN', 'disable', 'true');
			setStyle('CustomerID', 'visible', 'false');
			setStyle('btn_UploadCustomerID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('btn_UploadVIN', 'visible', 'false');

			setStyle('Note', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('Notes1', 'visible', 'false');



			/*setStyle('AdminVATWaiver', 'visible', 'false');
			setStyle('AdminFeesVATAmount', 'visible', 'false');
			setStyle('AdminFeesWoVATAmount', 'visible', 'false');
			setStyle('InstalmentVATWaiver', 'visible', 'false');
			setStyle('InstalmentWaiverVAT', 'visible', 'false');
			setStyle('InstalmentWaiverWoVAT', 'visible', 'false');
			setStyle('PurchaseDiscount', 'visible', 'false');
			setStyle('FinanceRateDiscount', 'visible', 'false');*/
			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			/*setStyle('PRGrid', 'visible', 'true');*/
			/*		setStyle('ExcelUploaded', 'visible', 'true');
					setStyle('ExcelUploaded', 'mandatory', 'true');
					setStyle('btn_DownloadExcel', 'visible', 'true');	
					if (getGridRowCount('CampaignGrid') > 0) {
						setStyle('btn_UploadExcel', 'disable', 'false');
						setStyle('ExcelUploaded', 'disable', 'false');
						setStyle('btn_DownloadExcel', 'disable', 'false');
					} else {
						setStyle('btn_UploadExcel', 'disable', 'true');
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
					}*/
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			break;

		case 'General Campaign':

			/*setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');
			*/
			/*setStyle('PRGrid', 'visible', 'false');*/
			setStyle('EndDate', 'mandatory', 'true');
			setStyle('CustomerID', 'disable', 'true');
			setStyle('btn_UploadCustomerID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('btn_UploadVIN', 'disable', 'true');
			setStyle('CustomerID', 'visible', 'false');
			setStyle('btn_UploadCustomerID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('btn_UploadVIN', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');

			setStyle('Notes1', 'visible', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			/*setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');*/

			clearComboOptions('VINNumber');
			clearComboOptions('CustID');
			if (getValue('ProductType') == 'CF') {
				setStyle('Note', 'visible', 'false');
				onChangeFullyWave();
			} else {
				/*setStyle('AdminVATWaiver', 'visible', 'true');
				setStyle('AdminFeesVATAmount', 'visible', 'true');
				setStyle('AdminFeesWoVATAmount', 'visible', 'true');
				setStyle('InstalmentVATWaiver', 'visible', 'true');
				setStyle('InstalmentWaiverVAT', 'visible', 'true');
				setStyle('InstalmentWaiverWoVAT', 'visible', 'true');
				setStyle('PurchaseDiscount', 'visible', 'true');
				setStyle('FinanceRateDiscount', 'visible', 'true');*/
				setStyle('Note', 'visible', 'true');
				if (getGridRowCount('CampaignGrid') > 0) {
					setStyle('btn_DownloadExcel', 'disable', 'false');
				}
			}
			break;
		case 'Special Campaign':

			/*setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');*/
			/*setStyle('PRGrid', 'visible', 'false');*/

			setStyle('Note', 'visible', 'true');
			setStyle('Notes1', 'visible', 'true');

			setStyle('CustomerID', 'disable', 'true');
			setStyle('btn_UploadCustomerID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('btn_UploadVIN', 'disable', 'true');
			setStyle('CustomerID', 'visible', 'false');
			setStyle('btn_UploadCustomerID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('btn_UploadVIN', 'visible', 'false');

			setStyle('EndDate', 'mandatory', 'true');
			setStyle('PromoCode', 'mandatory', 'true');

			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			/*setStyle('PRFinanceRate', 'visible', 'true');*/

			/*setStyle('btn_UploadExcel', 'visible', 'false');*/

			/*setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');*/

			/*setStyle('AdminVATWaiver', 'visible', 'true');
			setStyle('AdminFeesVATAmount', 'visible', 'true');
			setStyle('AdminFeesWoVATAmount', 'visible', 'true');
			setStyle('InstalmentVATWaiver', 'visible', 'true');
			setStyle('InstalmentWaiverVAT', 'visible', 'true');
			setStyle('InstalmentWaiverWoVAT', 'visible', 'true');
			setStyle('PurchaseDiscount', 'visible', 'true');
			setStyle('FinanceRateDiscount', 'visible', 'true');*/

			break;
		case 'Agreement Campaign':
			/*setStyle('PRGrid', 'visible', 'false');*/

			/*setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'true');*/

			setStyle('EndDate', 'mandatory', 'true');

			setStyle('CustomerID', 'disable', 'true');
			setStyle('btn_UploadCustomerID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('btn_UploadVIN', 'disable', 'true');
			setStyle('CustomerID', 'visible', 'false');
			setStyle('btn_UploadCustomerID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('btn_UploadVIN', 'visible', 'false');



			setStyle('Note', 'visible', 'true');
			setStyle('PromoCode', 'mandatory', 'false');


			setStyle('OccupationSector', 'mandatory', 'true');
			setStyle('EmployeeType', 'mandatory', 'true');
			setStyle('EmployerName', 'mandatory', 'true');

			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			/*setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'false');*/
			setStyle('Notes1', 'visible', 'false');
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');

			/*setStyle('AdminVATWaiver', 'visible', 'true');
			setStyle('AdminFeesVATAmount', 'visible', 'true');
			setStyle('AdminFeesWoVATAmount', 'visible', 'true');
			setStyle('InstalmentVATWaiver', 'visible', 'true');
			setStyle('InstalmentWaiverVAT', 'visible', 'true');
			setStyle('InstalmentWaiverWoVAT', 'visible', 'true');
			setStyle('PurchaseDiscount', 'visible', 'true');
			setStyle('FinanceRateDiscount', 'visible', 'true');*/

			break;
		case '50-50 Campaign':

			/*setStyle('PRGrid', 'visible', 'false');*/

			/*setStyle('Sec_CustomerParameters', 'visible', 'true');
			setStyle('Sec_AppropriationParameters', 'visible', 'false');
			setStyle('Sec_PriceAndRebateParameters', 'visible', 'false');*/

			setStyle('EndDate', 'mandatory', 'true');
			setStyle('Note', 'visible', 'false')

			setStyle('CustID', 'disable', 'true');
			setStyle('UploadCustID', 'disable', 'true');
			setStyle('VINNumber', 'disable', 'true');
			setStyle('UploadVIN', 'disable', 'true');
			setStyle('CustID', 'visible', 'false');
			setStyle('UploadCustID', 'visible', 'false');
			setStyle('VINNumber', 'visible', 'false');
			setStyle('UploadVIN', 'visible', 'false');


			setStyle('Note', 'visible', 'false');
			setStyle('PromoCode', 'mandatory', 'false');
			setStyle('Notes1', 'visible', 'false');

			setStyle('OccupationSector', 'mandatory', 'false');
			setStyle('EmployeeType', 'mandatory', 'false');
			setStyle('EmployerName', 'mandatory', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			/*setStyle('ExcelUploaded', 'visible', 'false');
			setStyle('ExcelUploaded', 'mandatory', 'false');
			setStyle('btn_DownloadExcel', 'visible', 'true');*/


			/*	if (getGridRowCount('CampaignGrid') > 0) {
					setStyle('btn_DownloadExcel', 'disable', 'false');
				} else {
					setStyle('btn_DownloadExcel', 'disable', 'true');
				}*/
			clearComboOptions('VINNumber');
			clearComboOptions('CustID');

			/*setStyle('AdminVATWaiver', 'visible', 'true');
			setStyle('AdminFeesVATAmount', 'visible', 'true');
			setStyle('AdminFeesWoVATAmount', 'visible', 'true');
			setStyle('InstalmentVATWaiver', 'visible', 'true');
			setStyle('InstalmentWaiverVAT', 'visible', 'true');
			setStyle('InstalmentWaiverWoVAT', 'visible', 'true');
			setStyle('PurchaseDiscount', 'visible', 'true');
			setStyle('FinanceRateDiscount', 'visible', 'true');
*/
			break;
		default:
			setStyle('Note', 'visible', 'false');
			setStyle('Notes1', 'visible', 'false');
			/*setStyle('btn_UploadExcel', 'visible', 'false');*/
			/*	setStyle('ExcelUploaded', 'visible', 'false');
				setStyle('ExcelUploaded', 'mandatory', 'false');*/
			setStyle('btn_DownloadExcel', 'visible', 'false');
			setStyle('Sec_CampaignParameters', 'visible', 'false');
			break;
	}

	generateCampCode();
	GlobalCampaignType = Camptype;
	onChangeFeeWithVAT('Load');
	saveWorkItem();
}

function postHookPickListOk(columns, controlId) {
	if (controlId == 'VehicleBrand') {
		setValues({ 'VehicleBrandCode': columns[1] }, true);
	} else if (controlId == 'Variant') {
		setValues({ 'VariantCode': columns[1] }, true);
	}
}

function getRandomInt(max) {
	return Math.floor(Math.random() * max);
}

function onChangeFeeWithVAT(CalledFrom) {
	console.log('Inside onChangePRParams');
	var FeeWithVat = getValue('AdminVATWaiver');
	if (CalledFrom == 'Change') {
		clearValue('CashAdminFeesDiscount');
	}
	if (FeeWithVat != '') {
		setStyle('CashAdminFeesDiscount', 'visible', 'true');
		setStyle('CashAdminFeesDiscount', 'mandatory', 'true');
	} else {
		setStyle('CashAdminFeesDiscount', 'visible', 'false');
		setStyle('CashAdminFeesDiscount', 'mandatory', 'false');
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
							/*setValues({ 'ExcelUploaded': 'true' }, true);*/
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
	if (calledFrom == 'VehicleBrand') {
		clearValue('ModelYear');
		clearValue('Variant');
		clearValue('Variant_Code');
		/*		clearValue('Tenure');
				clearValue('GracePeriod');
				clearValue('DownPayment');
				clearValue('FinancePercentage');
				clearValue('InsurancePercentage');
				clearValue('RVPercentage');
				clearValue('SupportAmounttobe');
				clearValue('MaximumSupportAmount');
				clearValue('DistributorSupportAmount');
				clearValue('WSSupportAmount');
				clearValue('ALJFSSupportAmount');
				clearValue('RetailPurchaseDiscount');*/
		clearComboOptions('Model');
		clearComboOptions('ModelSuffix');
		clearComboOptions('ModelCode');
		clearComboOptions('Colour');
	} else if (calledFrom == 'Variant') {
		clearValue('ModelYear');
		/*clearValue('Tenure');
		clearValue('GracePeriod');
		clearValue('DownPayment');
		clearValue('FinancePercentage');
		clearValue('InsurancePercentage');
		clearValue('RVPercentage');
		clearValue('SupportAmounttobe');
		clearValue('MaximumSupportAmount');
		clearValue('DistributorSupportAmount');
		clearValue('WSSupportAmount');
		clearValue('ALJFSSupportAmount');
		clearValue('RetailPurchaseDiscount');*/
		clearComboOptions('Model');
		clearComboOptions('ModelSuffix');
		clearComboOptions('ModelCode');
		clearComboOptions('Colour');
	} else if (calledFrom == 'ModelYear') {
		/*clearValue('Tenure');
		clearValue('GracePeriod');
		clearValue('DownPayment');
		clearValue('FinancePercentage');
		clearValue('InsurancePercentage');
		clearValue('RVPercentage');
		clearValue('SupportAmounttobe');
		clearValue('MaximumSupportAmount');
		clearValue('DistributorSupportAmount');
		clearValue('WSSupportAmount');
		clearValue('ALJFSSupportAmount');
		clearValue('RetailPurchaseDiscount');*/
		clearComboOptions('Model');
		clearComboOptions('ModelSuffix');
		clearComboOptions('ModelCode');
		clearComboOptions('Colour');
	} else if (calledFrom == 'CopyCampaignCode') {
		clearSectionValues('Sec_CampaignInfo', 'CampaignType');
		/*clearSectionValues('Sec_FinanceProductParameter', 'CampaignType');*/
		clearSectionValues('Sec_CampaignParameters', 'CampaignType');
		onChangeFeeWithVAT('Load');
		generateCampCode();
		saveWorkItem();
	} else if (calledFrom == 'CampaignNameCombo') {
		clearSectionValues('Sec_CampaignInfo', 'CampaignType');
		/*clearSectionValues('Sec_FinanceProductParameter', 'CampaignType');*/
		clearSectionValues('Sec_CampaignParameters', 'CampaignType');
		clearValue('CampaignType');
		onChangeFeeWithVAT('Load');
		onChangeCampaignType('');
		saveWorkItem();
	}
	if (getValue('CampaignType') == '50-50 Campaign') {
		setValues({ 'DownPayment': '50' }, true);
		setValues({ 'RVPercentage': '50.00' }, true);
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
	} else if (calledFrom == 'Grid_Age') {
		min = getValue('Grid_AgeRangeMin');
		max = getValue('Grid_AgeRangeMax');
	} else if (calledFrom == 'Grid_Income') {
		min = getValue('Grid_IncomeRangeMin');
		max = getValue('Grid_IncomeRangeMax');
	}
	if (min != '' && max != '') {
		if (Number(min) > Number(max) && (calledFrom == 'Age' || calledFrom == 'Grid_Age')) {
			ShowToastErrorHandleDup(fetchMsg(116));
			if (calledFrom == 'Age') {
				clearValue('AgeRangeMin');
				clearValue('AgeRangeMax');
			} else {
				clearValue('Grid_AgeRangeMin');
				clearValue('Grid_AgeRangeMax');
			}
		} else if (Number(min) > Number(max) && (calledFrom == 'Income' || calledFrom == 'Grid_Income')) {
			ShowToastErrorHandleDup(fetchMsg(117));
			if (calledFrom == 'Income') {
				clearValue('IncomeRangeMin');
				clearValue('IncomeRangeMax');
			} else {
				clearValue('Grid_IncomeRangeMin');
				clearValue('Grid_IncomeRangeMax');
			}
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
	if (controlId == 'Sec_CampaignParameters') {
		setStyle('Model', 'disable', 'false');
		/*setStyle('Colour', 'disable', 'false');
		setStyle('ModelCode', 'disable', 'false');
		setStyle('ModelSuffix', 'disable', 'false');*/
		clearTable('PRGrid');
		clearTable('CampaignGrid');
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

/*function checkPRParams(controlId) {
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
}*/



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
		if (fullywave == 'N') {
			setStyle('AdminVATWaiver', 'visible', 'true');
			setStyle('AdminVATWaiver', 'mandatory', 'true');
		} else {
			setStyle('AdminVATWaiver', 'visible', 'false');
			setStyle('AdminVATWaiver', 'mandatory', 'false');
			clearValue('AdminVATWaiver');
			onChangeFeeWithVAT('Change');
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
	if (getValue('FinanceRateDiscountscount') != '') {
		if (getValue('ProductType') == 'CF') {
			var Limit = executeServerEvent('getFinanceRateLimit', 'Change', '', true);
			if (Number(getValue('FinanceRateDiscount')) > Number(Limit)) {
				ShowToastWarningHandleDup(fetchMsg(125) + ' ' + Limit + '%');
				clearValue('FinanceRateDiscount');
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

function onClickAddGrid() {
	console.log('Inside onClickAddGrid');
	var mandatoryStatus;
	mandatoryStatus = checkMandatoryFields("Sec_FinanceProductParameter");
	if (!mandatoryStatus) {
		return;
	}
	mandatoryStatus = checkMandatoryFields("Sec_CampaignInfo");
	if (!mandatoryStatus) {
		return;
	}
	mandatoryStatus = checkMandatoryFields("Sec_CampaignParameters");
	if (!mandatoryStatus) {
		return;
	}
	if (getGridRowCount('PRGrid') == 0) {
		ShowToastWarningHandleDup('Please Add the Price and Rebate Parameters.');
		return;
	}
	var serverdata = executeServerEvent('onClickAddGrid', 'Click', '', true);
	if (serverdata == 'S') {
		ShowToastSuccessHandleDup('Data Successfully Added.');
	} else {
		ShowToastErrorHandleDup('Error in Adding Data');
		return;
	}
	clearAddedValues();
	generateCampCode();
	setStyle('add_PRGrid', 'disable', 'false');
	setStyle('btn_DownloadExcel', 'disable', 'false');
}


function checkGrid() {
	console.log('Inside checkGrid');
	if (getGridRowCount('PRGrid') >= 1) {
		setStyle('add_PRGrid', 'disable', 'true');
	}
}

function makeMandatory() {
	console.log('Inside makeMandatory');
	if (sActivityName == 'Campaign Maker' || sActivityName == 'Create Campaign') {
		if (getValue('ProductType') == 'AF' && getValue('RequestType') == 'New' && getValue('CampaignType') != '') {
			setStyle('VehicleType', 'mandatory', 'true');
			setStyle('DistributorWise', 'mandatory', 'true');
			/*setStyle('Wholesalers', 'mandatory', 'true');*/
			setStyle('VehicleBrand', 'mandatory', 'true');
			setStyle('Variant', 'mandatory', 'true');
			setStyle('ModelYear', 'mandatory', 'true');
			setStyle('Model', 'mandatory', 'true');
			setStyle('DownPayment', 'mandatory', 'true');
			setStyle('Tenure', 'mandatory', 'true');
			setStyle('GracePeriod', 'mandatory', 'true');
			setStyle('RVPercentage', 'mandatory', 'true');
			setStyle('FinancePercentage', 'mandatory', 'true');
			setStyle('InsurancePercentage', 'mandatory', 'true');
		} else {
			setStyle('VehicleType', 'mandatory', 'false');
			setStyle('DistributorWise', 'mandatory', 'false');
			/*setStyle('Wholesalers', 'mandatory', 'false');*/
			setStyle('VehicleBrand', 'mandatory', 'false');
			setStyle('Variant', 'mandatory', 'false');
			setStyle('ModelYear', 'mandatory', 'false');
			setStyle('Model', 'mandatory', 'false');
			setStyle('DownPayment', 'mandatory', 'false');
			setStyle('Tenure', 'mandatory', 'false');
			setStyle('GracePeriod', 'mandatory', 'false');
			setStyle('RVPercentage', 'mandatory', 'false');
			setStyle('FinancePercentage', 'mandatory', 'false');
			setStyle('InsurancePercentage', 'mandatory', 'false');
		}
	}
}

function checkPRParams(calledFrom) {
	console.log('Inside checkPRParams');
	var returnVal = false;
	if (calledFrom == 'Grid') {
		if ((getValue('table3_table27_AdminFeesDiscount') == '' || Number(getValue('table3_table27_AdminFeesDiscount')) == 0) && getValue('table3_table27_Installment') == '' && (getValue('table3_table27_RetailPurchaseDiscount') == '' || Number(getValue('table3_table27_RetailPurchaseDiscount')) == 0) && (getValue('table3_table27_Cashback') == '' || Number(getValue('table3_table27_Cashback')) == 0)) {
			returnVal = true;
		}
	} else {
		if ((getValue('AdminFeesDiscount') == '' || Number(getValue('AdminFeesDiscount')) == 0) && (getValue('Q_CMPLX_PRParameters_RetailPurchaseDiscount') == '' || Number(getValue('Q_CMPLX_PRParameters_RetailPurchaseDiscount')) == 0) && getValue('MonthlyInstalments').length == 0 && (getValue('Q_CMPLX_PRParameters_CashBack') == '' || Number(getValue('Q_CMPLX_PRParameters_CashBack')) == 0)) {
			returnVal = true;
		}
	}
	return returnVal;
}

function clearAddedValues() {
	if (getValue('ProductType') == 'AF') {
		clearValue('VehicleType');
		clearValue('DistributorWise');
		clearValue('Wholesalers');
		clearValue('VehicleBrand');
		clearValue('VehicleBrandCode');
		clearValue('Variant');
		clearValue('VariantCode');
		clearValue('ModelYear');
		clearValue('Model');
		clearValue('ModelSuffix');
		clearValue('ModelCode');
		clearComboOptions('Model');
		clearComboOptions('ModelSuffix');
		clearComboOptions('ModelCode');
		clearComboOptions('Colour');
		clearValue('Colour');
		clearValue('Model');
		clearValue('Model');
		clearValue('Model');
		clearValue('Color');
		clearValue('DownPayment');
		clearValue('Tenure');
		clearValue('GracePeriod');
		clearValue('FinancePercentage');
		clearValue('InsurancePercentage');
		clearValue('RVPercentage');
		clearValue('CustomerType');
		clearValue('CustomerID');
		clearValue('Province');
		clearValue('Gender');
		clearValue('City');
		clearValue('AgeRangeMin');
		clearValue('AgeRangeMax');
		clearValue('OccupationSector');
		clearValue('EmployeeType');
		clearValue('EmployerName');
		clearValue('IncomeRangeMin');
		clearValue('IncomeRangeMax');
		clearValue('Nationality');
		clearValue('SpecialNeeds');
		clearTable('PRGrid');
		setStyle('Model', 'disable', 'false');
		/*setStyle('Colour', 'disable', 'false');
		setStyle('ModelCode', 'disable', 'false');
		setStyle('ModelSuffix', 'disable', 'false');*/
	}
}

function onChangeRetailPrice() {
	console.log('Inside onChangeRetailPrice');
	try {
		var ActualretailPrice = getValue('Grid_ActualRetailPrice');
		var data = executeServerEvent('onChangeRetailPrice', 'Change', '', true);
		if (data.split('~')[0] == 'F') {
			ShowToastErrorHandleDup('The retail price must not be more than ' + data.split('~')[1] + '% above or below the actual retail price.');
			setValues({ 'Grid_RetailPrice': ActualretailPrice }, true);
			return;
		}
	} catch (err) {
		console.log('Error in onChangeRetailPrice');
	}

}
