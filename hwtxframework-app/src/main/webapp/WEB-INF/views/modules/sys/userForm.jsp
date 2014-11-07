<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/validator.jsp"%>
<style>
.jarviswidget>div {
	border-width: 0;
}

.smart-form footer {
	background: none;
}
</style>

<script>
	$(document).ready(function() {
		$("#userInputForm").validator();
		$("#sb").click(function(){
			$("#userInputForm").submit();
		});
	});

	function saveCallback(json) {
		if (json.code == 'success') {
			message_box.show(json.message, 'success');
			HWTX.gDialogClose();
            HWTX.refreshTable($("#userTable"));
		} else {
			message_box.show(json.message, 'error');
		}
		return true;
	}
</script>
<div class="modal-body" style="margin-left: 20px">
	<div class="jarviswidget" id="wid-id-8" data-widget-editbutton="false"
		data-widget-custombutton="false">
		<div>
			<div class="jarviswidget-editbox">
				<!-- This area used as dropdown edit box -->
			</div>
			<!-- end widget edit box -->

			<!-- widget content -->
			<div class="widget-body no-padding">
				<form action="${ctx}/sys/user/save" method="post" id="userInputForm"
					class="smart-form"
					onsubmit="return ajaxSubmit(this,'json',saveCallback)">
					<input type="hidden" name="sysUser.userId" value="${sysUser.userId}" />
					<div class="row">
						<section class="col col-10">
							<label class="control-label">用户名:</label> <label class="input">
								<input type="text" name="sysUser.name" value="${sysUser.name}"
								maxlength="50" required />
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div>
					<div class="row">
						<section class="col col-10">
							<label class="control-label">密码:</label> <label class="input">
								<input type="password" name="sysUser.pwd" maxlength="50" />
							</label>
							<c:if test="${!empty sysUser.userId}">
								<div class="note">若不修改密码，请留空</div>
							</c:if>
							<div class="help-block with-errors"></div>
						</section>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<div class="modal-footer">
	<a href="#" class="btn" data-dismiss="modal" id="cl">关闭</a>
	<a href="#" class="btn btn-primary" id="sb">保存</a>
</div>