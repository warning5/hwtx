/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.common.persistence;

import com.jfinal.plugin.activerecord.Model;

public class BaseDao {

    public void update(Model<?> model) {
        model.update();
    }

    public void save(Model<?> model) {
        model.save();
    }

    public Model<?> findById(Model<?> model, String id) {
        return model.findById(id);
    }
}