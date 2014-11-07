/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 YU YUE, SOSO STUDIO, wuyuetiger@gmail.com
 *
 * License: GNU Lesser General Public License (LGPL)
 * 
 * Source code availability:
 *  https://github.com/wuyuetiger/dbking
 *  https://code.csdn.net/tigeryu/dbking
 *  https://git.oschina.net/db-unifier/dbking
 */

package com.hwtx.tool.autocode;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampXmlAdapter extends XmlAdapter<Calendar, Timestamp> {

	public Calendar marshal(Timestamp t) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(t);
		return c;
	}

	public Timestamp unmarshal(Calendar c) throws Exception {
		return new Timestamp(c.getTime().getTime());
	}

}
