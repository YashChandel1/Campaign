function loadDocument() {
	try {
		if (activityName == 'Introduction') {
			setStyle('frame21', 'visible', 'false');
			setStyle('frame22', 'visible', 'false');

			var result = executeServerEvent("onClickDocumentationTab", "", selectedLocale, true);
			console.log(result);
			if (getValue('formDisabledReason') != 'ID_expired') {
				let rowCount = getGridRowCount('inwardDocument_Grid');
				for (let i = 0; i < rowCount; i++) {
					let val = getValueFromTableCell('inwardDocument_Grid', i, 12);
					if (val == '' || val == undefined) {
						setCellDisabled('inwardDocument_Grid', i, 3, false);
						setCellDisabled('inwardDocument_Grid', i, 4, true);
						setCellDisabled('inwardDocument_Grid', i, 5, true);
					} else {
						setCellDisabled('inwardDocument_Grid', i, 3, true);
						setCellDisabled('inwardDocument_Grid', i, 4, false);
						setCellDisabled('inwardDocument_Grid', i, 5, false);
					}
				}
			}
		}
	} catch (err) {
		console.log("Error occured while loading document in Inward Document grid : " + err);
	}
}

var globalDocType = '';
var globaldocRowIndex = '';
var globalDocname = '';
var globalDocGrid = '';
function uploadDocument(tableId, rowIndex) {
	try {
		globaldocRowIndex = rowIndex;
		globalDocGrid = tableId;
		globalDocType = getValueFromTableCell(tableId, rowIndex, 8);
		window.parent.setDocType(globalDocType);
		window.parent.openImportDocWin('S');
	} catch (err) {
		console.log("Error in uploadDocument : " + err);
	}

}

function getFormattedCurrentDateTime() {
	try {
		var currentdate = new Date();
		var dd = currentdate.getDate();
		var mm = (currentdate.getMonth() + 1);
		var yy = currentdate.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}

		if (mm < 10) {
			mm = '0' + mm;
		}
		var datetime = dd + "/" +
			mm + "/" +
			yy + " " +
			currentdate.getHours() + ":" +
			currentdate.getMinutes() + ":" +
			currentdate.getSeconds();
		return datetime;
	} catch (err) {
		console.log("Error in getFormattedCurrentDateTime : " + err);
	}
	return "";
}

function newFileAddSuccess(docIndex, eventType, docName, errorMessage) {
	try {
		//var docType = docName.substring(0, docName.indexOf("("));
		if(docIndex !=''){
			setTableCellData(globalDocGrid, globaldocRowIndex, 9, docIndex, true);
			// setTableCellData('inwardDocument_Grid', globaldocRowIndex, 2, 'Uploaded', true);
			setTableCellData('inwardDocument_Grid', globaldocRowIndex, 2, userName, true);

			// let dateTime = getFormattedCurrentDateTime(); executeServerEvent('getCurrentDate', 'Click','', true);
			let dateTime = executeServerEvent('getCurrentDate', 'Click','', true);
			setTableCellData('inwardDocument_Grid', globaldocRowIndex, 3, dateTime, true);

			if(getValueFromTableCell('inwardDocument_Grid',globaldocRowIndex,9) !=''){
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,4,true);
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,5,false);
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,11,false);
			}else{
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,4,false);
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,5,true);
				setCellDisabled('inwardDocument_Grid',globaldocRowIndex,11,true);
			}

		setTimeout(function oneMiliSecond() {
			window.top.closewindows();
			saveWorkItem();
		}, 1);
		}
	} catch (err) {
		console.log("Error in newFileAddSuccess : " + err); 
	}
}

function viewDocument(tableId, rowIndex) {
	var di = getValueFromTableCell(tableId, rowIndex, 9);

	if (di == "") {
		showBootBox("", "Please Upload the document to view.", "error");
		return;
	} else {
		parent.openDocumentIframe(di);
	}
}

function deleteDocument(tableId, rowIndex) {
	try {
		console.log(tableId + "-" + rowIndex);
		var btns = {
			confirm: {
				label: 'Yes',
				className: 'btn-success'
			},
			cancel: {
				label: 'No',
				className: 'btn-danger'
			}
		}
		var callback = function(result) {
			if (result == true) {
				console.log("Selected Yes");
				var docIndex = getValueFromTableCell(tableId, rowIndex, 9);
				if (docIndex != "") {
					try{
						window.parent.deleteDoc(docIndex);
					}catch(err){
						console.log("Error occured in window.parent.deleteDoc : " + err);
					}
					setCellDisabled('inwardDocument_Grid',rowIndex,5,true);
					setCellDisabled('inwardDocument_Grid',rowIndex,11,true);
					setCellDisabled('inwardDocument_Grid',rowIndex,4,false);
					setTableCellData(tableId, rowIndex, 2, "", true);
					setTableCellData(tableId, rowIndex, 9, "", true);
					setTableCellData(tableId, rowIndex, 3, "", true);
					window.parent.refreshDocPanelWrapper();
					saveWorkItem();
				}
			} else if (result == false) {
				console.log("Selected No");
			}
		}
		showConfirmDialog("Deleting the document.<br>Are you certain you want to remove the document ?<br>If the document is removed, it cannot be accessed any longer", btns, callback);
	} catch (err) {
		console.log("Error occured in deleteDocument : " + err);
	}
}


function onDocTableRowAdd() {
	try {
		let rowCount = getGridRowCount('inwardDocument_Grid');

		setCellDisabled('inwardDocument_Grid', rowCount - 1, 4, true);
		setCellDisabled('inwardDocument_Grid', rowCount - 1, 5, true);
	} catch (err) {
		console.log('Error in onDocTableRowAdd' + err);
	}
}


function viewGeneratedDoc(tableId, rowIndex) {
	console.log("Inside viewGeneratedDoc --> ");
	try {
		var di = getValueFromTableCell(tableId, rowIndex, 5);

		if (di == "") {
			showBootBox("", "Please generate the document to view.", "error");
			return;
		} else {
			parent.openDocumentIframe(di);
		}
	} catch (err) {
		console.log(err);
	}
}

function generateDoc(tableId, rowIndex) {
	try {
		var res = executeServerEvent("GenerateOutward_document", "generateDoc", rowIndex, true);
		window.parent.refreshDocumentListExt();
		if (res != "") {
			const responseObj = JSON.parse(res);
			if (responseObj.status == "success") {
				setTableCellData(tableId, rowIndex, 4, getFormattedCurrentDateTime());
				setTimeout(function oneMiliSecond() {
					saveWorkItem();
				}, 1);
			}
			showBootBox("", responseObj.message, "error");
		}
	} catch (err) {
		console.error(err);
	}

}

function checkDocPresentOnDone() {
	try{
		let rowCount = getGridRowCount('inwardDocument_Grid');

		for(let i = 0; i<rowCount; i++) {
			let val = getValueFromTableCell('inwardDocument_Grid', i, 12);
			if(val == '' || val == undefined) {
				showBootBox("", fetchMsg('103'), "error");
				return false;
			}
		}
	} catch(err) {
		console.log('Error in checkDocPresentOnDone: ' + err);
	}
}