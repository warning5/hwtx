package com.hwtx.fncel.pbc.entity;

import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.modules.sys.entity.SysOrg;
import com.jfinal.core.HwTx;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;

@TableBind(tableName = "app_pbc_info", pkName = "id")
public class AppPbcInfo extends DataEntity<AppPbcInfo> implements AppOrg {

    @Getter
    @Setter
    String pname;
    @Setter
    @Getter
    private SysOrg sysOrg;

    public static AppPbcInfo dao = new AppPbcInfo();

    public java.lang.String getId() {
        return get("id");
    }

    public void setId(java.lang.String id) {
        set("id", id);
    }

    public java.lang.String getContact() {
        return get("contact");
    }

    public void setContact(java.lang.String contact) {
        set("contact", contact);
    }

    public java.lang.String getPhone() {
        return get("phone");
    }

    public void setPhone(java.lang.String phone) {
        set("phone", phone);
    }

    public java.lang.String getAddress() {
        return get("address");
    }

    public void setAddress(java.lang.String address) {
        set("address", address);
    }

    public void setOrgId(java.lang.String orgId) {
        set("orgId", orgId);
    }

    public String getRegion() {
        RegionService regionService = HwTx.getComponent("regionService");
        return regionService.getProvince(getProvince()) + "-" + regionService.getCity(getCity()) + "-" + regionService
                .getArea(getArea());
    }

    public String actions = "actions";

    @Override
    public String getOrgId() {
        return get("orgId");
    }

    public String getActions() {
        return actions;
    }

    public String getName() {
        return get("name");
    }
}
