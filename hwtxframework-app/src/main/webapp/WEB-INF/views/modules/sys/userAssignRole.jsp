<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/jstree.jsp"%>
<style>
.select2-container-multi .select2-choices {
    border: 0px;
}
</style>
<script type="text/javascript">

	var roles = new Array();
	
	$(document).ready(function() {
		
		
		var tmr = $("#roleIds").val().split(",");
		if(tmr[0]!=''){
			roles = roles.concat(tmr);
		}
		
		$('#uarscontainer').jstree(
		{
			"core" : {
				"animation" : 2,
				"check_callback" : true,
				"multiple" : false,
				"themes" : {
					"dots" : true
				},
				'data' : {
					'url' : '${ctx}/sys/role/tree',
					'data' : function(node) {
						return {
							'id' : node.id
						};
					}
				}
			},
			"plugins" : [ "sort"]
		});
		
		$('#uarscontainer').on("dblclick.jstree", ".jstree-anchor", $.proxy(function (e) {
			e.preventDefault();
			$(e.currentTarget).focus();
			obj = $('#uarscontainer').jstree(true).get_node(e.currentTarget);
			var index = jQuery.inArray(obj.id,roles);
			if(index == -1 && obj.id != '-1' && !obj.icon){
				$("#choice").append("<li class=\"select2-search-choice\">"+
					"<div>"+obj.text+"</div>"+
					"<a href=\"#\" onclick=\"removeChoice(this,'"+obj.id+"')\" class=\"select2-search-choice-close\" tabindex=\"-1\"></a>"+
					"</li>");
				roles.push(obj.id);
			}
		}, this));
		
		$("#userARole").click(function(){
			$.post("${ctx}/sys/user/assignRole", { "userId": '${userId}', "roles": roles.toString()},
				function(data) {
					message_box.show(data.message,data.code);
					HWTX.gDialogClose();
				},"json").error(function(data) { message_box.show(data.message,data.code); });
		});
});
		 
	 function removeChoice(choice,id){
		 $(choice).closest(".select2-search-choice").fadeOut('fast',function(){
			$(this).remove();
			roles = jQuery.grep(roles, function (a) { return a != id; });
		 });
	 }
</script>	
<div class="modal-body">
	<input id="roleIds" value="${roleIds}" type="hidden"/>
	<div class="row" style="margin-top: 15px;margin-left: 13px">
		<div class="col-xs-6 col-sm-6 col-md-6 col-lg-5">
			<div id="uarscontainer"></div>
		</div>
		<div class="col-xs-6 col-sm-6 col-md-6 col-lg-7 select2-container select2-container-multi select2">
			<ul class="select2-choices" id="choice">
				<c:forEach items="${roles}" var="role">
					<li class="select2-search-choice">
						<div>${role.value}</div>
						<a href="#" onclick="removeChoice(this,'${role.key}')" class="select2-search-choice-close" tabindex="-1"></a>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
</div>
<div class="modal-footer">
	<a href="#" class="btn" data-dismiss="modal" id="cl">关闭</a>
	<a href="#" class="btn btn-primary" id="userARole">保存</a>
</div>