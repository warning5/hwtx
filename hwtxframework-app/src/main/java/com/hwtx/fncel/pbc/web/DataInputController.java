package com.hwtx.fncel.pbc.web;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.entity.AppOrg;
import com.hwtx.fncel.pbc.entity.DefInidata;
import com.hwtx.fncel.pbc.entity.ValueInidata;
import com.hwtx.fncel.pbc.entity.ValueInidatastatics;
import com.hwtx.fncel.pbc.exception.FieldNullException;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.service.DataInputService;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.service.IniDataService;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.DefValueInidata;
import com.hwtx.fncel.pbc.vo.InputDataVo;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.service.OrgService;
import com.hwtx.modules.sys.service.RoleService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.web.BaseController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.hwtx.fncel.util.FncelUtils.getStatistic;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/input")
public class DataInputController extends BaseController {

    @Resource
    private DataInputService dataInputService;
    @Resource
    private AppOrgService appOrgService;
    @Resource
    private IniDataService iniDataService;
    @Resource
    private RoleService roleService;
    @Resource
    private OrgService orgService;

    @ActionKey(value = {"list"})
    public void list() {
        String type = getPara("type");
        setAttr("type", type);
        boolean statistic = getStatistic(this);
        String orgId = appOrgService.getAppOrg().getOrgId();
        String typeName = null;
        if (!statistic) {
            typeName = roleService.getRoleName(type);
        } else {
            SysOrg sysOrg = orgService.getOrg(orgId);
            if (sysOrg != null) {
                typeName = sysOrg.getName();
            }
        }
        setAttr("typeName", typeName);
        setAttr("statistic", statistic);
        setAttr("orgId", orgId);
        render("/app/input/list.jsp");
    }

    @ActionKey(value = {"show"})
    public void show() {
        String type = getPara("type");
        setAttr("type", type);
        setAttr("statistic", getPara("statistic"));
        setAttr("startdate", FncelUtils.getDate(new Date()));
        setAttr("definidata", iniDataService.getDefInidatasBySubmitRole(type));
        render("/app/input/input.jsp");
    }

    @ActionKey(value = {"edit"})
    public void edit() {
        int status = AppConstants.Value_Inidata_Save_Status;
        if (getPara("status") != null) {
            status = getParaToInt("status");
        }
        showOneData(status);
    }

    @ActionKey(value = {"delete"})
    public void delete() {
        String type = getPara("type");
        AppOrg appOrg = appOrgService.getAppOrg();
        Integer region = appOrgService.getRegion(appOrg);
        try {
            Date date = FncelUtils.getDate(getPara("date"));
            dataInputService.delete(date, getStatistic(this), type, region, appOrg.getOrgId());
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
        } catch (Exception e) {
            render(getErrorRenderJson(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    @Override
    protected Page1<InputDataVo> getAllData() {
        String type = getPara("type");
        String orgId = getPara("orgId");
        List<InputDataVo> inputDataVos = dataInputService.getTableData(type, appOrgService.getRegion(), orgId, getStatistic(this));
        return new Page1<>(inputDataVos, inputDataVos.size());
    }

    @ActionKey(value = {"update"})
    public void update() {
        try {
            Date date = FncelUtils.getDate(getPara("startdate"));
            AppOrg appOrg = appOrgService.getAppOrg();
            String type = getPara("type");
            Integer region = appOrgService.getRegion(appOrg);
            dataInputService.update(buildHandeData(date, appOrg, type), date, getStatistic(this), type, region,
                    appOrg.getOrgId());
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "更新成功"));
        } catch (ExistException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("该年份数据已存在", HttpServletResponse.SC_BAD_GATEWAY));
        } catch (FieldNullException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("字段'" + e.getField() + "'不能为空",
                    HttpServletResponse.SC_BAD_REQUEST));
        } catch (IllegalArgumentException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("无法获取用户所属机构", HttpServletResponse.SC_BAD_GATEWAY));
        } catch (Exception e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    @ActionKey(value = {"save"})
    public void save() {
        try {
            Date date = FncelUtils.getDate(getPara("startdate"));
            AppOrg appOrg = appOrgService.getAppOrg();
            String type = getPara("type");
            boolean statistic = getStatistic(this);
            dataInputService.save(buildHandeData(date, appOrg, type), type, appOrgService.getRegion(appOrg), date,
                    statistic, appOrg.getOrgId());
            renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功"));
        } catch (ExistException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("该年份数据已存在", HttpServletResponse.SC_BAD_GATEWAY));
        } catch (FieldNullException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("字段'" + e.getField() + "'不能为空",
                    HttpServletResponse.SC_BAD_REQUEST));
        } catch (IllegalArgumentException e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson("无法获取用户所属机构", HttpServletResponse.SC_BAD_GATEWAY));
        } catch (Exception e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST));
        }
    }

    @ActionKey(value = {"view"})
    public void view() {
        showOneData(AppConstants.Value_Inidata_Save_Status, AppConstants.Value_Inidata_Submit_Status);
        setAttr("readOnly", true);
    }

    private List<ValueInidata> buildHandeData(Date date, AppOrg appOrg, String type) throws FieldNullException {
        Integer region = appOrgService.getRegion(appOrg);
        List<DefInidata> defInidatas = iniDataService.getDefInidatasBySubmitRole(type);
        List<ValueInidata> values = Lists.newArrayList();
        List<ValueInidata> valueInidatas = null;
        for (int i = 0; i < defInidatas.size(); i++) {
            DefInidata defInidata = defInidatas.get(i);
            String value = getPara(defInidata.getDiniDataName());
            if (StrKit.isBlank(value)) {
                if (defInidata.getDiniDataRequired() == 1) {
                    throw new FieldNullException(defInidata.getDiniDataLabel());
                }
            }
            ValueInidata valueInidata = null;
            if (defInidata.getIsStaticMark() == 1) {
                if (i == 0) {
                    valueInidatas = dataInputService.getValues(date, region, type);
                    if (valueInidatas.size() == 0) {
                        valueInidatas = null;
                    }
                }
                if (valueInidatas == null) {
                    valueInidata = buildValueInidata(defInidata, date, region, type);
                } else {
                    valueInidata = getValueInidataByDefInidataId(valueInidatas, defInidata.getDiniDataId());
                    if (valueInidata == null) {
                        continue;
                    }
                    valueInidata.setInsert(false);
                }
                if (valueInidata.getViniStatus() == null) {
                    valueInidata.setViniStatus(AppConstants.Value_Inidata_Save_Status);
                }
                buildValueStatistic(valueInidata, appOrg, value, date, region);
                valueInidata.setViniStatus(null);
            } else {
                valueInidata = buildValueInidata(defInidata, date, region, type);
                valueInidata.setViniDataValue(Float.valueOf(value));
            }
            values.add(valueInidata);
        }
        return values;
    }

    private void buildValueStatistic(ValueInidata valueInidata, AppOrg appOrg, String value, Date date, Integer region) {
        ValueInidatastatics valueInidatastatics = new ValueInidatastatics();
        valueInidatastatics.setSiniDataId(IdGen.uuid());
        valueInidatastatics.setViniDataId(valueInidata.getViniDataId());
        valueInidatastatics.setSiniDataRegion(valueInidata.getViniDataRegion());
        valueInidatastatics.setSubmitOrg(appOrg.getOrgId());
        valueInidatastatics.setSiniDataValue(Float.valueOf(value));
        valueInidatastatics.setSiniDate(date);
        valueInidatastatics.setSiniDataRegion(region);
        valueInidatastatics.setSiniStatus(valueInidata.getViniStatus());
        valueInidata.setValueInidatastatistic(valueInidatastatics);
    }

    private ValueInidata getValueInidataByDefInidataId(List<ValueInidata> valueInidatas, String defInidataId) {
        for (ValueInidata valueInidata : valueInidatas) {
            if (valueInidata.getDiniDataId().equals(defInidataId)) {
                return valueInidata;
            }
        }
        return null;
    }

    private ValueInidata buildValueInidata(DefInidata defInidata, Date date, Integer region, String type) {
        ValueInidata valueInidata = new ValueInidata();
        String viniDataId = IdGen.uuid();
        valueInidata.setViniDataId(viniDataId);
        valueInidata.setDiniDataId(defInidata.getDiniDataId());
        valueInidata.setIsStastic(defInidata.getIsStaticMark());
        valueInidata.setViniDateFillDate(new Date());
        valueInidata.setViniDataRegion(region);
        valueInidata.setViniStatus(AppConstants.Value_Inidata_Save_Status);
        valueInidata.setViniDataSubmitOrgRole(type);
        valueInidata.setViniDate(date);

        return valueInidata;
    }

    private void showOneData(Integer... status) {
        String type = getPara("type");
        boolean statistic = getStatistic(this);
        List<DefValueInidata> defValueInidatas = null;
        try {
            defValueInidatas = dataInputService.getDefValueInidatasBySubmitRole(type, appOrgService.getRegion(),
                    FncelUtils.getDate(getPara("date")), statistic, getPara("orgId"), status);
        } catch (Exception e) {
            logger.error("{}", e);
            renderJson(getErrorRenderJson(e.getMessage(), HttpServletResponse.SC_BAD_GATEWAY));
            return;
        }
        if (defValueInidatas != null & defValueInidatas.size() != 0) {
            setAttr("date", defValueInidatas.get(0).getDate());
        }
        setAttr("type", type);
        setAttr("statistic", statistic);
        setAttr("defValueInidata", defValueInidatas);
        render("/app/input/edit.jsp");
    }
}