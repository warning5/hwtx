package com.hwtx.modules.sys.entity;

import com.jfinal.ext.plugin.tablebind.NoEntity;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@NoEntity
public abstract class Region<T extends Model<?>> extends DataEntity<T> {

    private static final long serialVersionUID = 8050396255231005672L;

    public Integer getId() {
        return get("id");
    }

    public void setId(int id) {
        set("id", id);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public Integer getCode() {
        Object code = get("code");
        if (code instanceof String && StrKit.isBlank((String) code)) {
            return null;
        }
        return (Integer) code;
    }

    public void setCode(Integer code) {
        set("code", code);
    }

    public Integer getPcode() {
        return get("pcode");
    }

    public void setPcode(Integer pcode) {
        set("pcode", pcode);
    }

    public Integer getEnable() {
        return get("enable");
    }

    public void setEnable(Integer enable) {
        set("enable", enable);
    }
}
