package com.thinkgem.jeesite.common.persistence;

import com.jfinal.ext.plugin.tablebind.NoEntity;
import com.jfinal.plugin.activerecord.Model;

import java.io.Serializable;

@NoEntity
public abstract class BaseEntity<T extends Model<?>> extends Model<T> implements Serializable {


}
