<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<style>
.row_selected {
	background-color: #B0BED9;
}
.DTTT.btn-group { 
 	right: 83px;   
    top: 11px; 
}
</style>
<script src="_m_hwtx.bond.org/js/bondorglist.js"></script>
<div id="prefix" class="hide">_m_hwtx.bond.org</div>
<div class="jarviswidget jarviswidget-color-darken" id="wid-id-0"
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
				<form action="${ctx}/sys/user/" method="post" id="userForm"
					onsubmit="return false" class="form-inline" role="form">
					<fieldset style="top: 3px">
						<div class="form-group">
							<div class="input-icon-right">
								<i class="fa fa-search"></i> <input type="text" placeholder="机构名称"
									id="userName" class="form-control" name="searchBondOrg.name">
							</div>
						</div>
						<button class="btn btn-primary" id="userSubmit">查询</button>
					</fieldset>
				</form>
				<div class="DTTT btn-group">
					<a class="btn btn-default btn-sm DTTT_button_copy" id="bonda"><span>添加</span>
					</a> <a class="btn btn-default btn-sm DTTT_button_print" id="bondm"
						title="View print view"><span>修改</span> </a> <a
						class="btn btn-default btn-sm DTTT_button_collection" id="bondd"><span>删除</span>
					</a>
				</div> 
			</div>
			<table cellpadding="0" cellspacing="0" border="0"
				class="table table-bordered table-hover" id="bondOrgTable">
				<thead>
					<tr>
						<th>序号</th>
						<th>名称</th>
						<th>组织机构代码</th>
						<th>机构信息用代码</th>
						<th>法人</th>
						<th>操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</div>