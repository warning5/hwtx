package com.jfinal.ext.plugin.tablebind;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
@TableBind(tableName = "blog")
public class Blog extends Model<Blog> {
    public final static Blog dao = new Blog(), me = dao;
}
