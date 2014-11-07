<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/validator.jsp"%>
<%@include file="/WEB-INF/views/include/jstree.jsp"%>
<style>
.smart-form footer {
	background: none;
}
.orgboard{
 	border: 1px solid #BBBBBB;
    border-radius: 4px;
    overflow: auto;
    padding: 10px 15px;
    position: relative;
}

.u-tree {
    border-right: 1px solid #DDDDDD !important;
    float: left;
    height: 400px;
    overflow-y: auto;
    position: relative;
}
.u-text {
    border-right: 1px solid #DDDDDD !important;
    float: left;
    height: 350px;
    overflow-y: auto;
    position: relative;
    margin-left: 13px;
}
</style>
<script src="${ctxStatic}/resource/js/bizlist.js"></script>
<div id="prefix" class="hide">${ctx}</div>
<div data-widget-sortable="false" data-widget-custombutton="false"
	data-widget-fullscreenbutton="false" data-widget-deletebutton="false"
	data-widget-togglebutton="false" data-widget-editbutton="false"
	data-widget-colorbutton="false" id="wid-id-o3" class="jarviswidget well">
	<div>
		<div class="jarviswidget-editbox"></div>
		<div class="widget-body">
			<a href="javascript:void(0);" class="btn btn-primary" id="biza"><i class="fa fa-plus"></i> 添加</a>
			<a href="javascript:void(0);" class="btn btn-primary" id="bizd"><i class="fa fa-minus"></i> 删除</a>
			<hr class="simple">
			<div class="tab-content padding-10" id="myTabContent1">
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-md-4 col-lg-4 orgboard u-tree">
						<div id="bizcontainer"></div>
					</div>
					<div class="col-xs-12 col-sm-7 col-md-7 col-lg-7 orgboard u-text">
						<input type="hidden" id="newCreateId" />
						<form id="bizForm" method="post" action="${ctx}/sys/biz/save" class="smart-form"
							onsubmit="return bizSubmit(this)">
							<fieldset style="padding-top: 6px">
								<input type="hidden" name="save.type" id="save_type" />
								<input type="hidden" name="sysBizPermission.permissionId" id="sysBizPermission_permissionId" /> 
								<input type="hidden" name="sysBizPermission.pid" id="sysBizPermission_pid" />
								<input type="hidden" name="sysBizPermission.sequence" id="sysBizPermission_sequence" value="0"/>
								<div class="row">
									<section class="col col-5">
										<label class="control-label">归属:</label>
										<label class="input">
											 <input type="text"	id="sysBizPermission_pname" />
										</label>
									</section>
									<section class="col col-6">
										<label class="control-label">名称:</label> 
										<label class="input">
											<input type="text" name="sysBizPermission.name" maxlength="50"
											class="required" id="sysBizPermission_name" required />
										</label>
										<div class="help-block with-errors"></div>
									</section>
								</div>
								<div class="row">
									<section class="col col-6">
										<label class="control-label">描述:</label> <label class="input">
											<input type="text" name="sysBizPermission.description" maxlength="50"
											id="sysBizPermission_description" />
										</label>
										<div class="help-block with-errors"></div>
									</section>
									<section class="col col-5">
										<label class="control-label">类型:</label>
										<div class="inline-group">
											<label class="radio">
												<input type="radio" name="sysBizPermission.type" value="0"/><i></i>分类 
											</label>
											<label class="radio">
												<input type="radio" name="sysBizPermission.type" value="1" checked="checked"/><i></i>数据权限
											</label>
										</div>
									</section>
								</div>
								<div class="row">
									<section class="col col-6">
										<label class="control-label">是否启用:</label>
										<div class="inline-group">
											<label class="radio">
												<input type="radio" name="sysBizPermission.show" value="0" checked="checked"/><i></i>是 
											</label>
											<label class="radio">
												<input type="radio" name="sysBizPermission.show" value="1"/><i></i>否
											</label>
										</div>
									</section>
									<section class="col col-6" id="bizdef" style="display: none;">
										<label class="control-label">表达式:</label> 
										<label class="input">
											<a href="#" class="btn btn-success btn-sm" id="permissionDef"><i class="fa fa-user"></i> 查看</a>
										</label>
									</section>
								</div>
							</fieldset>
							<footer>
								<input id="btnSubmit" class="btn btn-primary" type="submit"
									value="保存" />
							</footer>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>