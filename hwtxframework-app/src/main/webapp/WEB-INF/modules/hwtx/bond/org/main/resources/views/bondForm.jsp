<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/validator1.jsp"%>
<style>
.jarviswidget>div {
	border-width: 0;
}

.smart-form footer {
	background: none; 
}

.smart-form .col-select {
    box-sizing: border-box;
    float: left;
    min-height: 1px;
    padding-left: 15px;
}
</style>

<script>
	$(document).ready(function() {
		$("#bondOrgInputForm").validator();
		$("#sb").click(function(){
			$("#bondOrgInputForm").submit();
		});
	});

	function saveCallback(json) {
		if (json.code == 'success') {
			message_box.show(json.message, 'success');
			gDialog.fClose();
			refreshTable();
		} else {
			message_box.show(json.message, 'error');
		}
		return true;
	}
</script>
<form action="_m_hwtx.bond.org/bond/org/save" method="post" id="bondOrgInputForm" autocomplete="off" onsubmit="return ajaxSubmit(this,'json',saveCallback)">
	<div class="modal-body" style="margin-left: 20px">
		<div class="jarviswidget" id="wid-id-8" data-widget-editbutton="false"
			data-widget-custombutton="false">
			<div>
				<div class="jarviswidget-editbox">
					<!-- This area used as dropdown edit box -->
				</div>
				<!-- end widget edit box -->
	
				<!-- widget content -->
				<div class="widget-body no-padding smart-form">
					<input type="hidden" name="bondOrg.id" value="${bondOrg.id}" />
					<div class="row">
						<section class="col">
							<label class="control-label">用户名:</label> <label class="input">
								<input type="text" name="bondOrg.name" value="${bondOrg.name}"
								maxlength="50" id="bondOrg_name" data-rule="用户名: required;match(bondOrg.name);" />
							</label>
							<div class="help-block with-errors"></div>
						</section>
						<section class="col">
							<label class="control-label">组织机构代码:</label> <label class="input">
								<input type="text" name="bondOrg.orgId" maxlength="50" id="bondOrg_orgId" required/>
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div> 
					<div class="row">
						<section class="col col-10">
							<label class="control-label">机构信用代码:</label> <label class="input">
								<input type="text" name="bondOrg.credit_code" maxlength="50" id="bondOrg_credit_code" required/>
							</label>
							<div class="help-block with-errors"></div>
						</section>
					</div>
					<div class="row">
						<section class="col-select"> 
							<label class="control-label">区域:</label> 
							<label class="select">
								<select class="combox" ref="w_combox_city" refUrl="_m_hwtx.bond.org/region/city" url="_m_hwtx.bond.org/region/province">
									<option value="">请选择</option>
								</select> <i></i> 
							</label>
							<div class="help-block with-errors"></div>
						</section>
						<section class="col-select col-3">
							<label class="control-label"></label> 
							<label class="select">
								<select class="combox" id="w_combox_city" ref="w_combox_area" refUrl="_m_hwtx.bond.org/region/area">
									<option value="">所有城市</option>
								</select> <i></i> 
							</label>
						</section>
						<section class="col-select col-3">
							<label class="control-label"></label> 
							<label class="select">
								<select class="combox" id="w_combox_area">
									<option value="">所有区县</option>
								</select> <i></i> 
							</label>
						</section>
					</div> 
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<input id="cl" class="btn btn-primary" type="button" value="关闭" data-dismiss="modal"/>&nbsp;
		<input id="sb" class="btn btn-primary" type="submit" value="保存"/> 
	</div>
</form>