package com.hwtx.modules.sys.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserTable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String actions;
	private String name;
	private String userId;
	private String login_ip;
	private String login_date;
}
