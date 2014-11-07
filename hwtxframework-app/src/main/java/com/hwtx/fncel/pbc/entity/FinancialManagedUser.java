package com.hwtx.fncel.pbc.entity;

import com.hwtx.modules.sys.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by panye on 2014/10/4.
 */
@Getter
@AllArgsConstructor
public class FinancialManagedUser {

    private SysUser sysUser;
    private String orgId;
}
