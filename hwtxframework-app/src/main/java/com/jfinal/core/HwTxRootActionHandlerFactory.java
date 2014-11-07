package com.jfinal.core;

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;

public class HwTxRootActionHandlerFactory implements RootActionHandlerFactory {

	@Override
	public Handler createHandler(Constants constants, ActionMapping actionMapping) {
		HwTxActionHandler handler = HwTx.getManagedBean(HwTxActionHandler.class);
		handler.setActionMapping(actionMapping);
		handler.setDevMode(constants.getDevMode());
		handler.setModuleManager(HwTx.getModuleManager());
		handler.setModulePrefix(JFinalConfig.getProperty("default.module.name", "_m_"));
		return handler;
	}
}