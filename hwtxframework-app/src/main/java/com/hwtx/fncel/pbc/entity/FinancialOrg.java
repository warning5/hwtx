package com.hwtx.fncel.pbc.entity;

import com.hwtx.modules.sys.service.RegionService;
import com.jfinal.core.HwTx;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;

@TableBind(tableName = "app_financial_info", pkName = "id")
public class FinancialOrg extends DataEntity<FinancialOrg> implements AppOrg{

    public static FinancialOrg dao = new FinancialOrg();

    public String getPhone() {
        return get("phone");
    }

    public void setPhone(String phone) {
        set("phone", phone);
    }

    public String getAddress() {
        return get("address");
    }

    public void setAddress(String address) {
        set("address", address);
    }

    public String getId() {
        return get("id");
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getUniCode() {
        return get("uniCode");
    }

    public void setUniCode(String uniCode) {
        set("uniCode", uniCode);
    }

    public String getContact() {
        return get("contact");
    }

    public void setContact(String contact) {
        set("contact", contact);
    }

    public String getOrgId() {
        return get("orgId");
    }

    public void setOrgId(String orgId) {
        set("orgId", orgId);
    }

    public String getRegion() {
        RegionService regionService = HwTx.getComponent("regionService");
        return regionService.getProvince(getProvince()) + "-" + regionService.getCity(getCity()) + "-" + regionService
                .getArea(getArea());
    }

    public String actions = "actions";

    public String getActions() {
        return actions;
    }
}
