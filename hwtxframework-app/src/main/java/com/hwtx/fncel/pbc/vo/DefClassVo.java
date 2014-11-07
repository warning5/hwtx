package com.hwtx.fncel.pbc.vo;

import com.hwtx.fncel.pbc.entity.DefClass;
import com.hwtx.fncel.pbc.entity.DefKpi;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by panye on 2014/10/6.
 */
@Getter
@AllArgsConstructor
public class DefClassVo {
    private DefClass defClass;
    private List<DefKpi> defKpis;
}
