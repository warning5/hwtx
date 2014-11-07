package com.hwtx.tool;

import com.hwtx.tool.autocode.Generator;

/**
 * Created by panye on 2014/9/10.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        generator.generatEntity("/home/panye/ideaworkspace/hwtxframework/hwtxframework-app/src/main/java/com/hwtx/fncel/pbc/entity", "autocode", "com.hwtx.fncel.pbc.entity",
                "value_synthetical");
    }
}
