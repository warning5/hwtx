package com.hwtx.fncel.pbc.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SaveKpiValue {
    String dkpiId;
    String vclassId;
    float value;
}