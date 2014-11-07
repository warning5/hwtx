package com.hwtx.fncel.pbc.vo;

import com.hwtx.fncel.pbc.entity.DefInidata;

/**
 * Created by panye on 2014/9/30.
 */
public class DefValueInidata {

    private DefInidata defInidata;
    private String value;
    private String date;

    public DefValueInidata(DefInidata defInidata, String value, String date) {
        this.defInidata = defInidata;
        this.value = value;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public DefInidata getDefInidata() {
        return defInidata;
    }

}
