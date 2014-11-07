<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="/static/resource/js/dlist.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	if ($.fn.select2) {
		$('.select2').each(function() {
			var $this = $(this);
			var width = $this.attr('data-select-width') || '100%';
			$this.select2({
				maximumSelectionSize : 3,
				placeholder: "选择类型",
				allowClear : true,
				width : width
			})
		})
	}
});
</script>
<style>
.row_selected{
	background-color: #B0BED9;
}
</style>
<div id="prefix" class="hide">${ctx}</div>
<div class="jarviswidget jarviswidget-color-darken"
	data-widget-editbutton="false">
	<div>
		<!-- widget edit box -->
		<div class="jarviswidget-editbox">
			<!-- This area used as dropdown edit box -->

		</div>
		<!-- end widget edit box -->

		<!-- widget content -->
		<div class="widget-body no-padding">
			<div class="widget-body-toolbar">
				<form action="${ctx}/sys/dict/" method="post" id="dictForm"
					onsubmit="return false" class="form-inline" role="form">
					<fieldset style="top: 3px">
						<div class="btn-group">
							<button class="btn btn-default dropdown-toggle"
								data-toggle="dropdown">
								操作 <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" style="min-width: 59px">
								<li><a id="dicta" href="#"><i class="fa fa-plus"></i> 添加</a></li>
								<li><a id="dictm" href="#"><i class="fa fa-maxcdn"></i> 修改</a></li>
								<li><a id="dictd" href="#"><i class="fa fa-minus"></i> 删除</a></li>
							</ul>
						</div>
						<div class="form-group">
							<select multiple class="select2" id="types" data-select-width="450px" name="searchDict.types">
								<c:forEach items="${typeList}" var="item">
									<option value="${item.type}">${item.type}</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<div class="input-icon-right">
								<i class="fa fa-eraser" style="cursor: pointer;" id="clearLabel">
									<i class="fa fa-flash"></i>
								</i>
								<input type="text" name="searchDict.label" id="label"
									placeholder="显示名" class="form-control">
							</div>
						</div>
						<button class="btn btn-primary" id="dictSubmit">查询</button>
					</fieldset>
				</form>
			</div>
			<table cellpadding="0" cellspacing="0" border="0"
				class="table table-bordered table-hover"
				id="dictTable">
				<thead>
					<tr>
						<th class="smart-form">
							<label class="checkbox" style="margin-bottom: 3px">
								<input type="checkbox" id="checkAll"><i></i>
							</label>
						</th>
						<th>ID</th>
						<th>显示名</th>
						<th>值</th>
						<th>类型</th>
						<th>操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</div>