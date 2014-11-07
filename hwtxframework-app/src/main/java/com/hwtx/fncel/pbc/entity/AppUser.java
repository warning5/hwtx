package com.hwtx.fncel.pbc.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "app_user", pkName = "id")
public class AppUser extends DataEntity<AppUser> {

    public static AppUser dao = new AppUser();

    public java.lang.String getId() {
        return get("id");
    }

    public void setId(java.lang.String id) {
        set("id",id);
    }
        
    public java.lang.String getName() {
        return get("name");
    }

    public void setName(java.lang.String name) {
        set("name",name);
    }
        
    public java.lang.String getGender() {
        return get("gender");
    }

    public void setGender(java.lang.String gender) {
        set("gender",gender);
    }
        
    public java.lang.String getDuty() {
        return get("duty");
    }

    public void setDuty(java.lang.String duty) {
        set("duty",duty);
    }
        
    public java.lang.String getMobilePhone() {
        return get("mobilePhone");
    }

    public void setMobilePhone(java.lang.String mobilePhone) {
        set("mobilePhone",mobilePhone);
    }
        
    public java.lang.String getOfficePhone() {
        return get("officePhone");
    }

    public void setOfficePhone(java.lang.String officePhone) {
        set("officePhone",officePhone);
    }
        
    public java.lang.String getUserId() {
        return get("userId");
    }

    public void setUserId(java.lang.String userId) {
        set("userId",userId);
    }
    
    public String actions = "actions";

    public String getActions() {
        return actions;
    }

}
