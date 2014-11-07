package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.dao.PbcDao;
import com.hwtx.fncel.pbc.entity.AppDataCheck;
import com.hwtx.fncel.pbc.entity.AppPbcInfo;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.pbc.vo.ComputeValue;
import com.hwtx.fncel.pbc.vo.ValueInidataDetail;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.utils.IdGen;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/9/2.
 */
@Component
public class PbcService extends AppService {

    @Resource
    private PbcDao pbcDao;

    @PostConstruct
    public void init() {
        appDao = pbcDao;
    }

    public Page1<AppPbcInfo> getPbcOgs(String name, SysUser sysUser, int offset, int number) {
        return pbcDao.getAppPbcOgs(name, getUserOrgId(sysUser), offset, number);
    }

    @Before(Tx.class)
    public void savePbcInfo(AppPbcInfo appPbcInfo, String roleId, String name) throws Throwable {

        if (roleId == null) {
            throw new RuntimeException("can't find role of pbc info");
        }

        List<SysOrg> orgs = UserUtils.getUser().getOrgs();
        SysOrg ownerOrg = orgs.get(0);
        SysOrg sysOrg = new SysOrg();
        sysOrg.setName(name);
        sysOrg.setDescription(name);
        sysOrg.setPid(ownerOrg.getOrgId());
        sysOrg.setType(AppConstants.ORG_PBC_TYPE);

        int count = 0;
        if (!StrKit.isBlank(appPbcInfo.getId())) {
            sysOrg.setOrgId(appPbcInfo.getOrgId());
            orgService.update(sysOrg);
            count = pbcDao.updateAppPbcInfo(appPbcInfo);
            if (count == 0) {
                throw new ExistException();
            }
        } else {
            String orgId = IdGen.uuid();
            sysOrg.setOrgId(orgId);
            orgService.save(sysOrg, ResultType.INSERT.name());
            String pbcId = IdGen.uuid();
            appPbcInfo.setId(pbcId);
            appPbcInfo.setOrgId(orgId);
            count = pbcDao.insertAppPbcInfo(appPbcInfo);
            if (count == 0) {
                throw new ExistException();
            }
            orgService.assignRoles(orgId, roleId);
        }
    }

    public AppPbcInfo geAppPbcInfo(String id) {
        return pbcDao.getAppPbcInfo(id);
    }

    public int deletePbc(String ids) {
        long count = orgService.getAssignUserCountByOrg(ids);
        if (count > 0) {
            return 0;
        }
        pbcDao.deletePbcOrg(ids);
        orgService.delete(Arrays.asList(ids));
        return 1;
    }

    public Page1<Map<String, Object>> getPbcUsers(SearchSysUser searchSysUser, String type, Integer region, int offset, int count) {
        Page1<Record> page1 = pbcDao.getPbcUsers(searchSysUser, type, region, offset, count);
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Record record : page1.getList()) {
            result.add(record.getColumns());
        }
        return new Page1<>(result, result.size());
    }

    @Resource
    private DataInputService dataInputService;
    @Resource
    private RuleService ruleService;

    public List<ComputeValue> getKpiData(Integer region, Date date) {
        return dataInputService.getKpiData(region, date, AppConstants.Value_Inidata_Submit_Status,
                AppConstants.Value_Inidata_Back_Status, AppConstants.Value_Inidata_Handle_Status);
    }

    public List<ValueInidataDetail> getKpiInidata(Integer region, Date date, String kpiId) {
        Pair<List<String>, List<String>> pair = ruleService.getFormulaInidata(kpiId);
        return dataInputService.getValueInidatas(region, date, pair.getLeft(), pair.getRight(),
                AppConstants.Value_Inidata_Submit_Status, AppConstants.Value_Inidata_Back_Status,
                AppConstants.Value_Inidata_Handle_Status);
    }

    public void backData(AppDataCheck appDataCheck, Date date, String orgId) {
        dataInputService.backData(appDataCheck, date, orgId, AppConstants.BACK_PBC_DATA);
        dataInputService.updateSubmitKpiData(date, appDataCheck.getCheckdRegion(), AppConstants.Value_Inidata_Back_Status);
    }
}
