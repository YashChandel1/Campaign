package com.newgen.iforms.user;

import com.newgen.iforms.custom.IFormListenerFactory;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.iforms.user.common.NGLog;

public class ALJ_Campaign implements IFormListenerFactory {
	private static final long serialVersionUID = 1L;

	@Override
	public IFormServerEventHandler getClassInstance(IFormReference ifr) {
		NGLog.consoleLog("Inside ALJ_Campaign -> getClassInstance method called.");
		String activityName = ifr.getActivityName();
		if ("Create Campaign".equalsIgnoreCase(activityName)) {
			return new CampaignMaker();
		} else if ("Campaign Maker".equalsIgnoreCase(activityName)) {
			return new CampaignMaker();
		} else if ("Marketing Manager".equalsIgnoreCase(activityName)
				|| "Operation Marketing".equalsIgnoreCase(activityName)
				|| "Accounts Manager".equalsIgnoreCase(activityName)) {
			return new Approvers();
		} else {
			return new Exit();
		}
	}
}
