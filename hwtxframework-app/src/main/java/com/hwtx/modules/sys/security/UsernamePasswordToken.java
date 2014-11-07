/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.hwtx.modules.sys.security;

import lombok.Getter;

public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    @Getter
    private String captcha;

    public UsernamePasswordToken(String username, char[] password, boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

}