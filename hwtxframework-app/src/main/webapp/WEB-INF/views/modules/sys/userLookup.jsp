<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/datetimepicker.jsp"%>
<style>
.row_selected{
	background-color: #B0BED9;
}
</style>
<script src="/static/resource/js/ulookup.js"></script>
<div id="prefix" class="hide">${ctx}</div>
<div id="uorgId" class="hide">${orgId}</div>
<div class="modal-body">
<div class="jarviswidget jarviswidget-color-darken" data-widget-editbutton="false" style="margin: -16px 0 30px;">
	<div>
		<div class="widget-body no-padding">
			<div class="widget-body-toolbar" style="margin: -1px;border-bottom: 2px solid #CCCCCC;">
				<form action="${ctx}/sys/user/" method="post" id="userForm"
					onsubmit="return false" class="form-inline" role="form">
					<input name="searchSysUser.orgId" type="hidden" id="searchSysUserOrgId" value="${orgId}"/>
					<input name="searchSysUser.includeUser" type="hidden" id="includeUser" value="${includeUser}"/>
					<fieldset style="top: 3px">
						<c:if test="${! empty includeUser}">
							<button class="btn btn-primary disabled" id="deluser">删除</button>
						</c:if>
						<div class="form-group">
							<div class="input-icon-right">
								<i class="fa fa-user"></i> <input type="text" placeholder="用户名"
									id="userName" class="form-control"
									name="searchSysUser.userName">
							</div>
						</div>
						<div class="form-group">
							<div class="input-icon-right">
								<i class="fa fa-times" style="cursor: pointer;" id="clearstart">
									<i class="fa fa-calendar"></i>
								</i> <input type="text" data-format="yyyy-MM-dd HH:mm:ss"
									name="searchSysUser.startDate" id="startdate"
									placeholder="开始时间" class="form-control">
							</div>
						</div>
						<div class="form-group">
							<div class="input-icon-right">
								<i class="fa fa-times" style="cursor: pointer;" id="clearend">
									<i class="fa fa-calendar"></i>
								</i> <input type="text" data-format="yyyy-MM-dd" name="
									searchSysUser.finishDate" id="finishdate" placeholder="结束时间"
									class="form-control">
							</div>
						</div>
						<button class="btn btn-primary" id="userSubmit">查询</button>
					</fieldset>
				</form>
			</div>
			<table cellpadding="0" cellspacing="0" border="0"
				class="table table-bordered table-hover"
				id="userTable">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Login_Date</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</div>
</div>
<div class="modal-footer">
	<a href="#" class="btn" data-dismiss="modal" id="cl">关闭</a>
	<c:if test="${empty includeUser}">
		<a href="#" class="btn btn-primary" id="osb">保存</a>
	</c:if>
</div>