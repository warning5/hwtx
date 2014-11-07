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
		$("#dictInputForm").validator();
		$("#sb").click(function(){
			$("#dictInputForm").submit();
		});
	});

	function saveCallback(json) {
		if (json.code == 'success') {
			message_box.show(json.message, 'success');
			gDialog.fClose();
            HWTX.refreshTable($("#dictTable"));
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
				<form action="${ctx}/sys/dict/save" method="post" id="dictInputForm"
					class="smart-form"
					onsubmit="return ajaxSubmit(this,'json',saveCallback)">
					<input type="hidden" name="dict.id" value="${dict.id}" />
					<input type="hidden" name="dict.sort" value="0"/>
					<div class="row">
						<section class="col col-10">
							<label class="control-label">键值:</label>
							<label class="input">
								<input type="text" name="dict.value" value="${dict.value}"
									maxlength="50" required />
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div>
					<div class="row">
						<section class="col col-10">
							<label class="control-label">标签:</label> 
							<label class="input">
								<input type="text" name="dict.label" value="${dict.label}"
									maxlength="50" required />
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div>
					<div class="row">
						<section class="col col-10">
							<label class="control-label">类型:</label> 
							<label class="input">
								<input type="text" name="dict.type" value="${dict.type}"
									maxlength="50" required />
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div>
					<div class="row">
						<section class="col col-10">
							<label class="control-label">描述:</label> 
							<label class="input">
								<input type="text" name="dict.description" value="${dict.description}"
									maxlength="50"/>
							</label>
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