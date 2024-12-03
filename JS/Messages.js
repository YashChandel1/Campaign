/*var messages;
//var messagesMap = new Map();
function loadMessages() {
	try {
		//var userLang = selectedLocale;
		console.log("The language is: " + selectedLocale);
		var allMsgs = executeServerEvent("LoadMessages", "change", selectedLocale, "true");
		if (allMsgs == "failure") {
			showBootBox("", "Please check data in messages table for MsgLocale en.", "error");
		} else {
			messages = JSON.parse(allMsgs);

			console.log(messages);
		}
	} catch (err) {
		console.log(err);
	}
}
function fetchMsg(MsgCode) {
	var msg = '';
	try {
		for (var i = 0; i < messages.length; i++) {
			if (messages[i].MsgCode == MsgCode) {
				console.log(messages[i].Msg);
				msg = messages[i].Msg;
			}
		}
	} catch (err) {
		console.log(err);
	}
	return msg;
}*/

var messages;
var messagesMap = new Map();
function loadMessages() {
	try {
		//var userLang = navigator.language || navigator.userLanguage;
		console.log("The language is: " + selectedLocale);
		var allMsgs = executeServerEvent("LoadMessages", "Change", selectedLocale, "true");
		if (allMsgs == "failure") {
			showBootBox("", "Please check data in messages table for MsgLocale en.", "error");
		} else {
			messages = JSON.parse(allMsgs);

			try {
				for (var i = 0; i < messages.length; i++) {
					//			messagesMap.set(messages[i].MsgCode,0);
					var toastCountRefObj = { "count": 0, "refSuccess": "", "refError": "", "refWarning": "", "refInfo": "" };
					messagesMap.set(messages[i].MsgCode, toastCountRefObj);
				}
			} catch (err) {
				console.log(err);
			}
			console.log(messagesMap);
		}
	} catch (err) {
		console.log(err);
	}
}
function fetchMsg(MsgCode) {
	var msg = '';
	try {
		for (var i = 0; i < messages.length; i++) {
			if (messages[i].MsgCode == MsgCode) {
				console.log(messages[i].Msg);
				msg = messages[i].Msg;
			}
		}
	} catch (err) {
		console.log(err);
	}
	return msg;
}

function fetchToastMsg(MsgCode) {

	var msgInfo = {};
	let counter = 0;
	try {
		for (var i = 0; i < messages.length; i++) {
			if (messages[i].MsgCode == MsgCode) {
				console.log(messages[i].Msg);
				if (messagesMap.has(MsgCode) && messagesMap.get(MsgCode).count == 0) {
					counter = parseInt(messagesMap.get(MsgCode).count);
					//				messagesMap.set(MsgCode,++counter);
					messagesMap.get(MsgCode).count = ++counter;
					msgInfo.msgStr = messages[i].Msg;
				} else {
					counter = parseInt(messagesMap.get(MsgCode).count);
					//				messagesMap.set(MsgCode, ++counter);
					messagesMap.get(MsgCode).count = ++counter;
					msgInfo.msgStr = messages[i].Msg + ' (' + messagesMap.get(MsgCode).count + ')';
				}
				msgInfo.msgCode = MsgCode;
			}
		}
	} catch (err) {
		console.log(err);
	}
	return msgInfo;
}
function showBootBoxCustom(control, msg, type, isClose, msgId, msgPlcHolderValues) {
	try {
		//1.check are we recieving all 6 params? chk only count
		console.log('Number of arguments for called function is ' + arguments.length)
		if (arguments.length != 6) {
			//call normal boot box 
			//				var msg = fetchMsg(msgId);
			var msg = fetchToastMsg(msgId);
			//showBootBox(control,msg,type);
			if (type == 'info') {
				//					ShowToastInfo(msg);
				ShowToastInfoHandleDup(msg);
			} else if (type == 'warning') {
				//					ShowToastWarning(msg);
				ShowToastWarningHandleDup(msg);
			} else if (type == 'success') {
				//					ShowToastSuccess(msg);
				ShowToastSuccessHandleDup(msg);
			} else if (type == 'error') {
				//					ShowToastError(msg);
				ShowToastErrorHandleDup(msg);
			}

		}
		else {
			//2.Find msg from msgId 
			//fetchMsg(msgId);
			//3.Replace placeholder with values
			//4.call Bootbox again
			//			   var msgString = fetchMsg(msgId);
			var msgString = fetchToastMsg(msgId);
			if (msgString.msgStr) {
				var MsgKeys = Object.keys(msgPlcHolderValues);
				//				   var finalMsg=msgString;
				var finalMsg = msgString.msgStr;
				for (var i = 0; i < MsgKeys.length; i++) {
					finalMsg = finalMsg.replace(MsgKeys[i], msgPlcHolderValues[MsgKeys[i]]);
				}
				//showBootBox(control,finalMsg,type);
				//						ShowToastInfo(finalMsg);
				//				   		msgToPass = finalMsg.substring(0, finalMsg.lastIndexOf('(') > 0 ? finalMsg.lastIndexOf('(') : finalMsg.length ); // getting finalMsg with updated counter which violating handleDup feature
				msgString.msgStr = finalMsg;
				//				   		ShowToastInfoHandleDup(msgString);
				if (type == 'info') {
					//					ShowToastInfo(msg);
					ShowToastInfoHandleDup(msgString);
				} else if (type == 'warning') {
					//					ShowToastWarning(msg);
					ShowToastWarningHandleDup(msgString);
				} else if (type == 'success') {
					//					ShowToastSuccess(msg);
					ShowToastSuccessHandleDup(msgString);
				} else if (type == 'error') {
					//					ShowToastError(msg);
					ShowToastErrorHandleDup(msgString);
				}
			}
			else {
				// Unable to find message with MsgId 
				showBootBox(control, "Message Id not found:" + msgId, type);
			}
		}
	} catch (err) {
		console.log(err);
	}
}

function getFinalMsgWithPlaceHolder(msgId, msgPlcHolderValues) {
	try {
		var msg = fetchMsg(msgId);
		if (msg) {
			var MsgKeys = Object.keys(msgPlcHolderValues);
			var finalMsg = msg;
			for (var i = 0; i < MsgKeys.length; i++) {
				finalMsg = finalMsg.replace(MsgKeys[i], msgPlcHolderValues[MsgKeys[i]]);
			}
		}
	} catch (err) {
		console.log(err);
	}
	return finalMsg;
}


//Info,Error and Warning toasts
function ShowToastError(textMsg) {
	try {
		$.toast({
			heading: 'Error',
			text: textMsg,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'error',
			hideAfter: 20000
		});
	} catch (error) {
		console.log(error);
	}
}

function ShowToastSuccess(textMsg) {
	try {
		$.toast({
			heading: 'Success',
			text: textMsg,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'success',
			hideAfter: 15000
		});
	} catch (error) {
		console.log(error);
	}
}

function ShowToastInfo(textMsg) {
	try {
		$.toast({
			heading: 'Information',
			text: textMsg,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'info',
			hideAfter: 10000
		});
	} catch (error) {
		console.log(error);
	}
}

function ShowToastWarning(textMsg) {
	try {
		$.toast({
			heading: 'Warning',
			text: textMsg,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'warning',
			hideAfter: 15000,
			allowToastClose: true
		});
	} catch (error) {
		console.log(error);
	}
}

function ShowToastTabLoad(textMsg) {
	try {
		$.toast({
			heading: 'Warning',
			text: textMsg,
			position: 'bottom-right',
			icon: 'warning',
			allowToastClose: true,
			bgColor: 'rgba(249, 165, 0, 0.83)',
			textAlign: 'center',
			hideAfter: false
		});
	} catch (error) {
		console.log(error);
	}
}

let refSuccess = '';
let refError = '';
let refWarning = '';
let refInfo = '';

// Info,Error and Warning toasts
function ShowToastErrorHandleDup(msgInfoJson) {
	try {
		msgInfoJson = handleHardCodedMsg(msgInfoJson);
		//   	if( messagesMap.get(msgInfo.msgCode) >= 0 && messagesMap.get(msgInfo.msgCode) <= 1 ){
		refError = $.toast({
			heading: 'Error',
			//           text: textMsg,
			text: msgInfoJson.msgStr,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'error',
			hideAfter: 10000,
			afterHidden: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					//   			  messagesMap.set(msgInfo.msgCode,0);
					messagesMap.get(msgInfoJson.msgCode).count = 0;
				}
			},
			afterShown: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).refError = refError;
				}
			},
			beforeShow: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					try {
						if (messagesMap.get(msgInfoJson.msgCode).refError) {
							messagesMap.get(msgInfoJson.msgCode).refError.reset();
						}
					} catch (err) {
						console.log(err);
					}
				}
			}
		});
		if (messagesMap.has(msgInfoJson.msgCode)) {
			messagesMap.get(msgInfoJson.msgCode).refError = refError;
		}
		//       updateMessagesMap(20000, msgInfo.msgCode);
		//       console.log(refError);
		//   	}else{
		//   		console.log("messagesMap.get(msgInfo.msgCode) :   "+messagesMap.get(msgInfo.msgCode) );
		//   		if(messagesMap.has(msgInfo.msgCode) && messagesMap.get(msgInfo.msgCode)>1){
		//   			console.log("Inside autoClose  ",refError );
		//   			refError.update({text : msgInfo.msgStr});
		//       	}
		//   	}
	} catch (err) {
		console.log(err);
	}

}

function ShowToastSuccessHandleDup(msgInfoJson) {
	try {
		msgInfoJson = handleHardCodedMsg(msgInfoJson);
		//   	if( messagesMap.get(msgInfo.msgCode) >= 0 && messagesMap.get(msgInfo.msgCode) <= 1 ){
		refSuccess = $.toast({
			heading: 'Success',
			//           text: textMsg,
			text: msgInfoJson.msgStr,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'success',
			hideAfter: 10000,
			afterHidden: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).count = 0;
				}
			},
			afterShown: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).refSuccess = refSuccess;
				}
			},
			beforeShow: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					try {
						if (messagesMap.get(msgInfoJson.msgCode).refSuccess) {
							messagesMap.get(msgInfoJson.msgCode).refSuccess.reset();
						}
						//                   	if(messagesMap.has(msgInfoJson.msgCode)){
						//                   		messagesMap.get(msgInfoJson.msgCode).refSuccess = refSuccess;
						//                   	}
					} catch (err) {
						console.log(err);
					}
				}
			}
		});
		if (messagesMap.has(msgInfoJson.msgCode)) {
			messagesMap.get(msgInfoJson.msgCode).refSuccess = refSuccess;
		}
		//       updateMessagesMap(15000, msgInfo.msgCode);
		//       console.log(refSuccess);
		//   	}else{
		//   		console.log("messagesMap.get(msgInfo.msgCode) :   "+messagesMap.get(msgInfo.msgCode) );
		//   		if(messagesMap.has(msgInfo.msgCode) && messagesMap.get(msgInfo.msgCode)>1){
		//   			console.log("Inside autoClose  ",refSuccess );
		//   			refSuccess.update({text : msgInfo.msgStr , loader : true , hideAfter: 15000});
		//       	}
		//   	}
	} catch (err) {
		console.log(err);
	}
}

function ShowToastInfoHandleDup(msgInfoJson) {
	try {
		msgInfoJson = handleHardCodedMsg(msgInfoJson);
		//  if( messagesMap.get(msgInfo.msgCode) >= 0 && messagesMap.get(msgInfo.msgCode) <= 1 ){
		refInfo = $.toast({
			heading: 'Information',
			//           text: textMsg,
			text: msgInfoJson.msgStr,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'info',
			hideAfter: 10000,
			afterHidden: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).count = 0;
				}
			},
			afterShown: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).refInfo = refInfo;
				}
			},
			beforeShow: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					try {
						if (messagesMap.get(msgInfoJson.msgCode).refInfo) {
							messagesMap.get(msgInfoJson.msgCode).refInfo.reset();
						}
					} catch (err) {
						console.log(err);
					}
				}
			}
		});
		if (messagesMap.has(msgInfoJson.msgCode)) {
			messagesMap.get(msgInfoJson.msgCode).refInfo = refInfo;
		}
		//       updateMessagesMap(10000, msgInfo.msgCode);
		//       console.log(refInfo);
		//   	}else{
		//   		console.log("messagesMap.get(msgInfo.msgCode) :   "+messagesMap.get(msgInfo.msgCode) );
		//   		if(messagesMap.has(msgInfo.msgCode) && messagesMap.get(msgInfo.msgCode)>1){
		//   			console.log("Inside autoClose  ",refInfo );
		//   			refInfo.update({text : msgInfo.msgStr , loader : false , hideAfter : 10000});
		//     	}
		//   	}
	} catch (err) {
		console.log(err);
	}
}

function ShowToastWarningHandleDup(msgInfoJson) {
	try {
		msgInfoJson = handleHardCodedMsg(msgInfoJson);
		//  if( messagesMap.get(msgInfo.msgCode) >= 0 && messagesMap.get(msgInfo.msgCode) <= 1 ){
		refWarning = $.toast({
			heading: 'Warning',
			//           text: textMsg,
			text: msgInfoJson.msgStr,
			showHideTransition: 'plain',
			position: 'bottom-right',
			icon: 'warning',
			hideAfter: 10000,
			allowToastClose: true,
			afterHidden: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).count = 0;
				}
			},
			afterShown: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					messagesMap.get(msgInfoJson.msgCode).refWarning = refWarning;
				}
			},
			beforeShow: function() {
				if (messagesMap.has(msgInfoJson.msgCode)) {
					try {
						if (messagesMap.get(msgInfoJson.msgCode).refWarning) {
							messagesMap.get(msgInfoJson.msgCode).refWarning.reset();
						}
					} catch (err) {
						console.log(err);
					}
				}
			}
		});
		if (messagesMap.has(msgInfoJson.msgCode)) {
			messagesMap.get(msgInfoJson.msgCode).refWarning = refWarning;
		}
		//       updateMessagesMap(15000, msgInfo.msgCode);
		//       console.log(refWarning);
		//   	}else{
		//   		console.log("messagesMap.get(msgInfo.msgCode) :   "+messagesMap.get(msgInfo.msgCode) );
		//   		if(messagesMap.has(msgInfo.msgCode) && messagesMap.get(msgInfo.msgCode)>1){
		//   			console.log("Inside autoClose  ",refWarning );
		//   			refWarning.update({text : msgInfo.msgStr});
		//   		}
		//   	}
	} catch (err) {
		console.log(err);
	}
}

function handleHardCodedMsg(msg) {
	var msgInfo = {}; /// hashCode should be msgCode
	try {
		var msgCode = generateHashCode(msg);
		let isMsgCodePresent = isMsgCode(msgCode);
		if ((typeof msg) == 'string') {
			if (!isMsgCodePresent) {
				//				var msgObj = JSON.parse('{ "Msg" : '+'"'+msg+'"'+', "MsgCode": '+'"'+msgCode+'"'+'}');
				var msgObj = JSON.parse('{ "Msg" : ' + '"' + '' + '"' + ', "MsgCode": ' + '"' + msgCode + '"' + '}');
				msgObj.Msg = msg;
				var toastCountRefObj = { "count": 0, "refSuccess": "", "refError": "", "refWarning": "", "refInfo": "" };
				messages.push(msgObj);
				messagesMap.set(msgCode, toastCountRefObj);
				msgInfo = fetchToastMsg(msgCode);
			} else {
				msgInfo = fetchToastMsg(msgCode);
			}
		} else {
			msgInfo = msg;
		}
	} catch (err) {
		console.log(err);
	}
	return msgInfo;
}

function generateHashCode(str) {
	var hash = 0, chr;
	try {
		if (str.length === 0) return hash;
		for (i = 0; i < str.length; i++) {
			chr = str.charCodeAt(i);
			hash = ((hash << 5) - hash) + chr;
			hash |= 0; // Convert to 32bit integer
		}
	} catch (err) {
		console.log(err);
	}
	return hash;
}

function isMsgCode(msgCode) {
	var flag = false;
	try {
		for (var k = 0; k < messages.length; k++) {
			if (messages[k].MsgCode == msgCode) {
				flag = true;
			}
		}
	} catch (err) {
		console.log(err);
	}
	return flag;
}

function showBootBox1(control, msg, type, isClose, btnMsg) {
	//var msg = fetchMsg(msgId);
	if (type === 'error') {
		bootbox.alert({
			size: "medium",
			message: msg,
			closeButton: isClose,
			buttons: {
				ok: {
					label: btnMsg
				}
			},
			callback: function() {
				if (window.okOperation) {
					okOperation(control);
				} else {
					if (control !== null) {
						switch (control) {
							case 'Decision': {
								this.modal('hide');
								if (typeof saveAdvancedListviewchanges_PersonalDetails != 'undefined')
									saveAdvancedListviewchanges_PersonalDetails.click();
								setFocus(control);
								setStyle('DecisionComments', 'disable', 'false');
								break;
							}
							case 'Q_LOS_ALJ_CMPLX_BankingInfo_ReEnterIBANNumber': {
								clearValue('Q_LOS_ALJ_CMPLX_BankingInfo_ReEnterIBANNumber', true);
								break;
							}
							case 'Q_LOS_ALJ_CMPLX_VehicleSummary_FinalAdminFeeAmount': {
								//clearValue('Q_LOS_ALJ_CMPLX_VehicleSummary_FinalAdminFeeAmount', true);
								//clearValue('Q_LOS_ALJ_CMPLX_VehicleSummary_AdminFeeWithVAT', true);
								setValues({ 'Q_LOS_ALJ_CMPLX_VehicleSummary_FinalAdminFeeAmount': getValue('Q_LOS_ALJ_CMPLX_VehicleSummary_AdminFeeAmount') }, true);
								executeServerEvent(control, "onFinalAdminFeeAmtChange", "", true);
								break;
							}

						}
					}
				}
			}
		});
	}
}