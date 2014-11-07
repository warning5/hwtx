package com.hwtx.modules.sys.dao;

public class SysSqlConstants {

	public final static String dic_findAllList = "dic.findAllList";
	public final static String dic_findListByPage = "dic.findListByPage";
	public final static String dic_findAllType = "dic.findAllType";
	public final static String dic_delete = "dic.delete";

	public final static String user_findByLoginName = "user.findByLoginName";
	public final static String user_updatePasswordById = "user.updatePasswordById";
	public final static String user_getUserRoles = "user.getUserRoles";
	public final static String user_findAllUser = "user.findAllUser";
	public final static String user_delUser = "user.delUser";
	public final static String user_saveUser = "user.saveUser";
	public final static String user_getUserByName = "user.getUserByName";
	public final static String user_updateSysUserWithOutPwd = "user.updateSysUserWithOutPwd";
	public final static String user_delUserRoles = "user.delUserRoles";
	public final static String user_assignRoles = "user.assignRoles";
	public final static String user_getOrgFuns = "user.getOrgFuns";
	public final static String user_updateFuncPermissionsSort = "user.updateFuncPermissionsSort";
	public final static String user_getUserOrg = "user.getUserOrg";

	public final static String role_findAllRoles = "role.findAllRoles";
	public final static String role_deleteRoles = "role.deleteRoles";
	public final static String role_getRolePermissions = "role.getRolePermissions";
	public final static String role_insertRolePermissions = "role.insertRolePermissions";
	public final static String role_deleteRolePermissions = "role.deleteRolePermissions";
	public final static String role_getAssignedRoles = "role.getAssignedRoles";
	public final static String role_getRolesByUserId = "role.getRolesByUserId";
	public final static String role_getRoleByName = "role.getRoleByName";
	public final static String role_getRoleFuns = "role.getRoleFuns";
	public final static String role_getAllChildren = "role.getAllChildren";

	public final static String org_getOrgsByPid = "org.getOrgsByPid";
	public final static String org_deleteOrgs = "org.deleteOrgs";
	public final static String org_getUserCount = "org.getUserCount";
	public final static String org_assignUser = "org.assignUser";
	public final static String org_delAssignedUser = "org.delAssignedUser";
	public final static String org_getOrgRoles = "org.getOrgRoles";
	public final static String org_delOrgRoles = "org.delOrgRoles";
	public final static String org_assignRoles = "org.assignRoles";
	public final static String org_countOrgByNamePidType = "org.countOrgByNamePidType";

	public final static String biz_getAllBizs = "biz.getAllBizs";
	public final static String biz_getAssignedBizs = "biz.getAssignedBizs";
	public final static String biz_delete = "biz.delete";
	public final static String biz_getBizParams = "biz.getBizParams";
	public final static String biz_insertOrUpdateDef = "biz.insertOrUpdateDef";
	public final static String biz_deleteAllParams = "biz.deleteAllParams";
	public final static String biz_insertParams = "biz.insertParams";
	public final static String biz_getAllChildren = "biz.getAllChildren";

	public final static String function_getResourceFunctions = "function.getResourceFunctions";
	public final static String function_getResourceFunction = "function.getResourceFunction";
	public final static String function_getAllMenus = "function.getAllMenus";
	public final static String function_getAllResources = "function.getAllResources";
	public final static String function_getAllChildren = "function.getAllChildren";
	public final static String function_batchDelete = "function.batchDelete";

	
	public final static String CACHE_ALL_USER_KEY = "cache.all.user";
	public final static String CACHE_ALL_DICT_TYPE = "cache.all.dict.type";
	public final static String CACHE_ALL_DICT = "cache.all.dict";
	public final static String SYS_CACHE = "sysCache";
}
