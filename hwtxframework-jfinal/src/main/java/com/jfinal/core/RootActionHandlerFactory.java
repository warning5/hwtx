package com.jfinal.core;

import com.jfinal.config.Constants;
import com.jfinal.handler.Handler;

public interface RootActionHandlerFactory {

	public Handler createHandler(Constants constants, ActionMapping actionMapping);

}
