package com.hwtx.fncel.pbc.service;

import com.google.common.collect.Lists;
import com.hwtx.fncel.pbc.dao.FinancialDao;
import com.hwtx.fncel.pbc.entity.FinancialOrg;
import com.hwtx.fncel.pbc.exception.ExistException;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysOrg;
import com.hwtx.modules.sys.entity.SysUser;
import com.hwtx.modules.sys.service.RoleService;
import com.hwtx.modules.sys.utils.UserUtils;
import com.hwtx.modules.sys.vo.SearchSysUser;
import com.hwtxframework.ioc.annotation.Component;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page1;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.thinkgem.jeesite.common.utils.IdGen;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by panye on 2014/10/2.
 */
@Component
public class FinancialService extends AppService {

    @Resource
    private FinancialDao financialDao;
    @Resource
    private RoleService roleService;

    @PostConstruct
    public void init() {
        appDao = financialDao;
    }

    public Page1<Map<String, Object>> getFinancialUsers(SearchSysUser searchSysUser, String type,
                                                        Integer region, int offset, int count) {
        Page1<Record> page1 = financialDao.getFinancialUsers(searchSysUser, type, region, offset, count);
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Record record : page1.getList()) {
            result.add(record.getColumns());
        }
        return new Page1<Map<String, Object>>(result, result.size());
    }

    public void deleteFinancial(String orgId) {
        financialDao.deleteFinancialByOrgId(orgId);
    }

    public Page1<FinancialOrg> getFinancialOrgs(String name, SysUser sysUser, int offset, int number) {
        return financialDao.getFinancialOrgs(name, getUserOrgId(sysUser), offset, number);
    }

    public FinancialOrg getFinancialOrg(String id) {
        return financialDao.getFinancialOrg(id);
    }

    @Before(Tx.class)
    public void save(FinancialOrg financialOrg) throws Throwable {
        int count = 0;
        if (!StrKit.isBlank(financialOrg.getId())) {
            SysOrg sysOrg = new SysOrg();
            sysOrg.setName(financialOrg.getName());
            sysOrg.setDescription(financialOrg.getName());
            sysOrg.setOrgId(financialOrg.getOrgId());
            orgService.update(sysOrg);
            count = financialDao.updateFinancialOrg(financialOrg);
            if (count == 0) {
                throw new ExistException();
            }
        } else {
            SysOrg sysOrg = new SysOrg();
            String orgId = IdGen.uuid();
            sysOrg.setName(financialOrg.getName());
            sysOrg.setOrgId(orgId);
            sysOrg.setDescription(financialOrg.getName());
            sysOrg.setType(AppConstants.ORG_FINANCIAL_TYPE);
            orgService.save(sysOrg, ResultType.INSERT.name());
            String financialId = IdGen.uuid();
            financialOrg.setId(financialId);
            financialOrg.setOrgId(orgId);
            count = financialDao.insertFinancialOrg(financialOrg);
            if (count == 0) {
                throw new ExistException();
            }
            financialDao.relationPbcAndFin(sysUserService.getUserOrg(UserUtils.getUser().getUserId()), financialId);
            String roleId = roleService.getRoleIdByName("金融机构");
            if (roleId == null) {
                throw new RuntimeException("can't find role of financial");
            }
            orgService.assignRoles(orgId, roleId);
        }
    }

    @Before(Tx.class)
    public int deleteFinancialOrg(String ids) {
        long count = orgService.getAssignUserCountByOrg(ids);
        if (count > 0) {
            return 0;
        }
        orgService.delete(Arrays.asList(ids));
        deleteFinancial(ids);
        return 1;
    }
}
