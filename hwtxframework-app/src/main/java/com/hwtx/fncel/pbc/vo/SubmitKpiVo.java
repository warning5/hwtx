package com.hwtx.fncel.pbc.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hwtx.fncel.pbc.util.AppConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by panye on 2014/10/21.
 */
public class SubmitKpiVo {

    private Date date;
    private Date vkpiSubmitTime;
    private Date vkpiHandleTime;
    @Getter
    @Setter
    private Integer vkpiStatus;
    private String orgName;
    private Integer vkpiRegion;
    private String orgId;

    public SubmitKpiVo(Date date, Date vkpiSubmitTime, Date vkpiHandleTime, Integer vkpiStatus) {
        this(date, vkpiSubmitTime, vkpiHandleTime, vkpiStatus, null);
    }

    public SubmitKpiVo(Date date, Date vkpiSubmitTime, Date vkpiHandleTime, Integer vkpiStatus, String orgName) {
        this.date = date;
        this.vkpiSubmitTime = vkpiSubmitTime;
        this.vkpiHandleTime = vkpiHandleTime;
        this.vkpiStatus = vkpiStatus;
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getVkpiRegion() {
        return vkpiRegion;
    }

    public void setVkpiRegion(Integer vkpiRegion) {
        this.vkpiRegion = vkpiRegion;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getVkpiSubmitTime() {
        return vkpiSubmitTime;
    }

    public void setVkpiSubmitTime(Date vkpiSubmitTime) {
        this.vkpiSubmitTime = vkpiSubmitTime;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getVkpiHandleTime() {
        return vkpiHandleTime;
    }

    public void setVkpiHandleTime(Date vkpiHandleTime) {
        this.vkpiHandleTime = vkpiHandleTime;
    }

    @JSONField(format = "yyyy", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatusShow() {
        return AppConstants.getAppStatusShow(vkpiStatus);
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getActions() {
        return "actions";
    }
}
