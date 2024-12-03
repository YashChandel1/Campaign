/* ------------------------------------------------------------------------ 
 * NEWGEN SOFTWARE TECHNOLOGIES LIMITED 
* Group                  		:CTS
* Product/Project Name          :OP Bank
* Module                        :RLOS
* File                          :LoadMessagesOuter.java
* Author Name                   :Akash Goel
* Date                          :2-Jan-2023
* Purpose                       :Contains business logic to load Notification msgs from cache or from db;
* ---------------------------------------------------------------------------*/
package com.newgen.iforms.user.repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.user.common.NGLog;

public class LoadMessagesOuter {

	public static class LoadMsgs {

		protected static final Map<String, JSONArray> msgCache = new HashMap<String, JSONArray>();

		public static JSONArray fetchMsgs(IFormReference ifr, String lang) {

//			lang = lang.substring(0, 2);
			NGLog.consoleLog("SIze of msgCache:" + msgCache.size());
			if (msgCache.containsKey(lang) && msgCache.get(lang) != null) {
				NGLog.consoleLog("Notification msgs are picked from cache for language:" + lang + " for user: "
						+ ifr.getUserName());
			} else {
				OperResult oprRes;
				oprRes = getMsgsFromDb(ifr, lang);
				if (oprRes.statusOperation == "Success") {
					if (!msgCache.containsKey(lang)) {
						msgCache.put(lang, oprRes.outMsgs);
						NGLog.consoleLog("Notification msgs are picked from DB for language:" + lang + " for user: "
								+ ifr.getUserName());
					} else if (msgCache.get(lang) == null) {
						NGLog.consoleLog("Msgs are null for language:" + lang);
						msgCache.put(lang, oprRes.outMsgs);
					}
				} else {
					msgCache.put(lang, null);
					NGLog.consoleLog("Unable to fetch Notification msgs from DB for language:" + lang + " for user: "
							+ ifr.getUserName());
				}

			}
			return msgCache.get(lang);
		}

		static OperResult getMsgsFromDb(IFormReference ifr, String lang) {

			OperResult oprRes = new LoadMessagesOuter().new OperResult();
			oprRes.statusOperation = "Failure";
			try {
				String query = "SELECT MsgCode, Msg FROM LOS_CAMP_M_Message WITH (NOLOCK) WHERE MsgLocale='" + lang
						+ "'";
				NGLog.consoleLog(query);
				List<List<String>> dbval = ifr.getDataFromDB(query);
				JSONArray jsonLoadMessages = new JSONArray();
				if (!dbval.isEmpty()) {
					for (int i = 0; i < dbval.size(); i++) {
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("MsgCode", dbval.get(i).get(0));
						jsonobj.put("Msg", dbval.get(i).get(1));
						jsonLoadMessages.add(jsonobj);

					}
//					Msgs = jsonLoadMessages;
					oprRes.outMsgs = jsonLoadMessages;
					oprRes.statusOperation = "Success";
				}
			} catch (Exception e) {
				NGLog.errorLog("Exception in getMsgsFromDb:" + Arrays.toString(e.getStackTrace()));
				oprRes.statusOperation = "Failure";
			}
			return oprRes;
		}
	}

	class OperResult {
		String statusOperation;
		JSONArray outMsgs;
	}
}
