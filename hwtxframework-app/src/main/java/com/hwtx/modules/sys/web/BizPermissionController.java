package com.hwtx.modules.sys.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.hwtx.modules.sys.dao.ResultType;
import com.hwtx.modules.sys.entity.SysBizPermission;
import com.hwtx.modules.sys.entity.SysBizPermissionParam;
import com.hwtx.modules.sys.service.BizService;
import com.hwtx.modules.sys.service.ServiceResponse;
import com.hwtx.modules.sys.validator.SysBizValidator;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.web.BaseController;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/sys/biz")
public class BizPermissionController extends BaseController {

	@Resource
	private BizService bizService;

	@ActionKey(value = { "list", "/" })
	public void list() {
		render("/modules/sys/biz/bizList.jsp");
	}

	@ActionKey(value = "treeData")
	public void treeData() {
		String jsonTree = bizService.getBizTree();
		renderJson(jsonTree);
	}

	@ActionKey(value = "showDef")
	public void showDef() {
		setAttr("id", getPara("id"));
		render("/modules/sys/biz/bizDef.jsp");
	}

	@ActionKey(value = "def")
	public void def() {
		String bizId = getPara("bizId");
		SysBizPermission sysBizPermission = bizService.getBiz(bizId);
		List<SysBizPermissionParam> params = bizService.getBizParams(bizId);
		Map<String, Object> result = Maps.newHashMap();
		result.put("params", JSON.toJSONString(params));
		result.put("count", params.size());
		result.put("def", sysBizPermission.getPermission_def());
		renderJson(JSON.toJSONString(result));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Page1<Map<String, Object>> getPageData(int iDisplayStart,
			int fcount) {
		String def = getPara("def");
		String params = getPara("params");
		List<SysBizPermissionParam> lparams = JSON.parseArray(params,
				SysBizPermissionParam.class);
		Map<String, String[]> allparams = getParaMap();
		return bizService.getPageValues(def, lparams, allparams, iDisplayStart,
				fcount);
	}

	@ActionKey(value = "saveDef")
	public void saveDef() {
		String bizId = getPara("bizId");
		String def = getPara("def");
		String params = getPara("params");
		List<SysBizPermissionParam> lparams = JSON.parseArray(params,
				SysBizPermissionParam.class);
		bizService.saveDef(bizId, def, lparams);
		renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "保存成功!"));
	}

	@ActionKey(value = "deftest")
	public void deftest() {
		String def = getPara("def");
		String params = getPara("params");
		List<SysBizPermissionParam> lparams = JSON.parseArray(params,
				SysBizPermissionParam.class);
		Map<String, String[]> allparams = getParaMap();
		try {
			String[] cols = bizService
					.getSampleColumns(def, lparams, allparams);
			if (cols != null) {
				String js = getTableJs(cols, getRequest().getContextPath(),
						def, lparams, allparams);
				if (js == null) {
					throw new Exception();
				}
				Map<String, Object> result = Maps.newHashMap();
				result.put("code", Constants.RENDER_JSON_SUCCESS);
				result.put("header", Joiner.on(",").join(cols));
				result.put("js", js);
				renderJson(JSON.toJSONString(result));
			} else {
				renderJson(getRenderJson(Constants.RENDER_JSON_ERROR,
						"参数错误或无法查询到数据."));
			}
		} catch (Exception e) {
			logger.error("{}", e);
			renderJson(getRenderJson(Constants.RENDER_JSON_ERROR,
					"参数错误或无法查询到数据."));
		}
	}

	private String getTableJs(String[] cols, String contextPath, String def,
			List<SysBizPermissionParam> lparams, Map<String, String[]> allparams) {
		File file = new File(getClass().getClassLoader()
				.getResource("templates" + File.separator + "testTable.js")
				.getFile());
		try {
			String templ = FileUtils.readFileToString(file,
					Charset.forName("UTF-8"));
			templ = templ.replace("^ajaxSource^",
					"\"" + contextPath + Global.getAdminPath()
							+ "/sys/biz/data\"");
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < cols.length; i++) {
				builder.append("{\"mData\": \"" + cols[i]
						+ "\",\"bSortable\": false }");
				if (i + 1 != cols.length) {
					builder.append(",");
				}
			}
			templ = templ.replace("^columns^", builder.toString());

			builder = new StringBuilder();
			for (SysBizPermissionParam sysBizPermissionParam : lparams) {
				String name = sysBizPermissionParam.getName();
				String value = allparams.get(name)[0];
				builder.append("aoData.push({\"name\":\"" + name
						+ "\",\"value\":\"" + value + "\"})");
				builder.append(",");
			}
			builder.append("aoData.push({\"name\":\"def\",\"value\":\"" + def
					+ "\"})");
			builder.append(",");
			builder.append("aoData.push({\"name\":\"params\",\"value\":JSON.stringify(paramContainer)})");

			templ = templ.replace("^params^", builder.toString());

			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(templ);
			templ = m.replaceAll("");

			return templ;
		} catch (IOException e) {
			logger.error("{}", e);
		}
		return null;
	}

	@ActionKey(value = "save")
	@Before(SysBizValidator.class)
	public void save() {
		SysBizPermission sysBizPermission = getModel(SysBizPermission.class);
		String save_type = getPara("save.type");
		Map<String, Object> result = Maps.newHashMap();
		if (sysBizPermission.getType().equals("1")
				&& checkName(sysBizPermission.getPermissionId(),
						sysBizPermission.getName(), save_type)) {
			result.put("code", Constants.RENDER_JSON_ERROR);
			result.put("message", "保存权限'" + sysBizPermission.getName()
					+ "'失败, 权限名已存在");
		} else {
			bizService.save(sysBizPermission, save_type);
			result.put("code", Constants.RENDER_JSON_SUCCESS);
			result.put("id", sysBizPermission.getPermissionId());
			result.put("name", sysBizPermission.getName());
			result.put("type", sysBizPermission.getType());
			result.put("message", "更新权限'" + sysBizPermission.getName() + "'成功");
		}
		renderJson(JSON.toJSONString(result));
	}

	private boolean checkName(Integer id, String name, String type) {
		SysBizPermission sysBizPermission = bizService.findBizByName(name);
		if (sysBizPermission == null) {
			return false;
		}

		if (ResultType.INSERT.name().equals(type)) {
			return true;
		}

		if (id != sysBizPermission.getPermissionId()) {
			return true;
		}
		return false;
	}

	@ActionKey(value = "detail")
	public void getDetail() {
		String bizId = getPara("bizId");
		SysBizPermission sysBizPermission = bizService.getBiz(bizId);
		if (sysBizPermission != null) {
			renderJson(JSON.toJSONString(sysBizPermission));
		}
	}

	@ActionKey(value = "move")
	public void move() {
		String _old_parent = getPara("_old_parent");
		String _new_parent = getPara("_new_parent");
		String _moved_id = getPara("_moved_id");
		ServiceResponse serviceResponse = bizService.move(_moved_id,
				_old_parent, _new_parent);
		renderJson(serviceResponse.json());
	}

	@ActionKey(value = "delete")
	public void delete() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "请选择删除项"));
		} else {
			Collection<String> result = bizService.delete(id);

			if (result.size() != 0) {
				renderJson(getRenderJson(Constants.RENDER_JSON_ERROR,
						result.toString()));
			} else {
				renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "删除成功"));
			}
		}
	}
}
