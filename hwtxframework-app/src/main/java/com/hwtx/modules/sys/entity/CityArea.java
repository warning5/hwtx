package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;

@TableBind(tableName = "dict_city_area", pkName = "id")
public class CityArea extends Region<CityArea> {

	private static final long serialVersionUID = -5917633810255007670L;
	
	public static final CityArea dao = new CityArea();

    public int getType(){
        return get("type");
    }

    public void setType(int type){
        set("type",type);
    }
}
