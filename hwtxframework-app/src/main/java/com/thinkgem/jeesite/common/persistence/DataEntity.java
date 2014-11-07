package com.thinkgem.jeesite.common.persistence;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.jfinal.ext.plugin.tablebind.NoEntity;
import com.jfinal.plugin.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@NoEntity
@JSONType(ignores = {"attrsEntrySet", "attrNames", "attrValues"})
@Setter
@Getter
public abstract class DataEntity<T extends Model<?>> extends BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public void prePersist() {
        SysUser user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getUserId())) {
            set("update_by", user.getUserId());
            set("create_by", user.getUserId());
        }
        set("update_time", new Timestamp(new Date().getTime()));
        set("create_time", get("update_time"));
    }

    public void preUpdate() {
        SysUser user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getUserId())) {
            set("update_by", user.getUserId());
        }
        set("update_time", new Timestamp(new Date().getTime()));
    }

    public void prePersistByDateTime() {
        set("update_time", new Timestamp(new Date().getTime()));
        set("create_time", get("update_time"));
    }

    public void preUpdateByDateTime() {
        set("update_time", new Timestamp(new Date().getTime()));
    }

    public String getCreate_by() {
        return get("create_by");
    }

    public void setCreate_by(String create_by) {
        set("create_by", create_by);
    }

    public String getUpdate_by() {
        return get("update_by");
    }

    public void setUpdate_by(String update_by) {
        set("update_by", update_by);
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getCreate_time() {
        return get("create_time");
    }

    public void setCreate_time(Date create_time) {
        set("create_time", create_time);
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    public Date getUpdate_time() {
        return get("update_time");
    }

    public void setUpdate_time(Date update_time) {
        set("update_time", update_time);
    }

    public Long getNum() {
        return get("num");
    }

    public void setNum(Long num) {
        set("num", num);
    }

    public Integer getProvince() {
        return get("province");
    }

    public void setProvince(Integer province) {
        set("province", province);
    }

    public Integer getCity() {
        return get("city");
    }

    public void setCity(Integer city) {
        set("city", city);
    }

    public Integer getArea() {
        return get("area");
    }

    public void setArea(Integer area) {
        set("area", area);
    }
}
