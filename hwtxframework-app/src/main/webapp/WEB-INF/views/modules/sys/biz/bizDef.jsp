<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/validator.jsp"%>
<script type="text/javascript" src="${ctxStatic}/resource/js/json2.js"></script>
<script type="text/javascript">

var cont = $("#addp").html();
var paramContainer = new Array();

function save_param(){
	 var param = {
	      'name': $("#paramname").val(),
	      'serial': $("#paramserial").val(),
	      'type' : $("#paramtype").val()
	 };
	 paramContainer.push(param);
	 $("#defcount").html(parseInt($("#defcount").html())+1);
	 $("#defap").popover("hide");
}

function del_param(name,td){
	
	paramContainer = jQuery.grep(paramContainer, function (a) { return a.name != name; });
	$(td).parent().parent().remove();
	$("#defcount").html(parseInt($("#defcount").html())-1);
}

function testIt(){
	
	var def = $("#sysBizPermission_permission_def").val();
	if(def == ""){
		alert("表达式不能为空!");
		return;
	}
	
	var data = $("#runParam").serializeArray();
	var item = {
		"name":"def",
		"value":def
	};
	data.push(item);
	item = {
		"name":"params",
		"value":JSON.stringify(paramContainer)
	};
	data.push(item);
	$.ajax({
		type : 'post',
		url : prefix+"/sys/biz/deftest",
		data : data,
		dataType : "json",
		cache : false,
		success : function(content) {
			if(content.code=="success"){
				var table = content.js;
				var cols = content.header.split(",");
		     	table += "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"table table-bordered table-hover\" id=\"bizTestTable\">"+
						 "<thead><tr>";
				for(i in cols){
					table+= "<th>"+cols[i]+"</th>";
				}
				table+="</tr></thead></table>";
				$("#table").html(table);
				$("#deftest").popover("hide");
			}else{
				message_box.show(content.message, content.code);
			}
		}
	});
}

$(document).ready(function() {
	
	$("#popover-hover").popover({
		trigger : "click",
		placement : "bottom",
		html : true,
		title : "参数列表",
		content :function(){
			if(paramContainer.length ==0){
				return "";
			}
			var con = "<table class=\"table table-bordered table-condensed\">";
			con += "<thead><tr><th>名称</th><th>序号</th><th>类型</th>	<th>操作</th></tr></thead><tbody>";
			for(i in paramContainer){
				item = paramContainer[i];
				con +="<tr><td>"+item.name+"</td><td>"+item.serial+"</td><td>"+item.type+"</td><td>"+
					  "<a href='#' onclick='del_param(\""+item.name+"\",this)'>删除</a></td></tr>";
			}
			con +="</tbody></table>";
			
			return con;
		}
	});
	
	$("#bsb").click(function(){
		
		def = $("#sysBizPermission_permission_def").val();
		
		if(def==""){
			alert("表达式为空");
			return;
		}
		
		$.ajax({
			type : 'post',
			dataType : "json",
			url : prefix+'/sys/biz/saveDef',
			data : {bizId : "${id}",def : def,params:JSON.stringify(paramContainer)},
			cache : false,
			success : function(content) {
				message_box.show(content.message, content.code);
				gDialog.fClose();
			},
			error : function(content) {
				message_box.show(content.message, content.code);
			}
		});
	});
	
	$("#defap").popover({
		trigger : "click",
		html : true,
		placement : "bottom",
		title:"<i class='fa fa-fw fa-pencil'></i> 添加参数",
		content:cont
	});
	
	$("#deftest").popover({
		trigger : "click",
		html : true,
		placement : "bottom",
		title:"<i class='fa fa-fw fa-book'></i> 测试参数",
		content:function(){
			if(paramContainer.length==0){
				testIt();
				return;
			}
			var con = "<table class=\"table table-bordered table-condensed\">";
			con += "<thead><tr><th>序号</th><th>名称</th><th>值</th></tr></thead><tbody>";
			for(i in paramContainer){
				item = paramContainer[i];
				con +="<tr><td>"+item.serial+"</td><td>"+item.name+"</td>"+
					  "<td><input type='text'name='"+item.name+"'size='10'required/></td></tr>";
			}
			con +="</tbody></table>";
			
			$("#paramset").html(con);
			return $("#runp").html();
		}
	});
	
	$.ajax({
		type : 'get',
		dataType : "json",
		url : prefix+'/sys/biz/def',
		data : {bizId : "${id}"},
		cache : false,
		success : function(content) {
			$("#sysBizPermission_permission_def").val(content.def);
			$("#defcount").html(content.count);
			paramContainer = JSON.parse(content.params);
		},
		error : function(content) {
			message_box.show(content.message, content.code);
		}
	});
});
</script>
<div id="prefix" class="hide">${ctx}</div>
<div class="modal-body">
<div>
		<div>
		<div class="jarviswidget-editbox"></div>
		<div class="widget-body">
					
			<a href="javascript:void(0);" class="btn btn-success" id="popover-hover" style="margin-left: 40px">
				<i class="fa fa-pencil"></i> 参数&nbsp;&nbsp;<span class="badge bg-color-blue txt-color-white" id="defcount"></span>
			</a>
			<a href="javascript:void(0);" class="btn btn-success" id="defap">
				<i class="fa fa-plus"></i> 添加参数
			</a>
			<a href="javascript:void(0);" class="btn btn-success" id="deftest">
				<i class="fa fa-align-justify"></i> 测试
			</a>
			<hr class="simple">
			<div class="tab-content padding-10">
				<div class="row">
					<input type="hidden" id="newCreateId" />
					<form id="bizDefForm" method="post" action="${ctx}/sys/biz/save"
						class="smart-form" onsubmit="return bizSubmit(this)">
						<fieldset style="padding-top: 6px">
							<div class="row">
								<section>
									<label class="textarea textarea-resizable">
										<textarea rows="2" class="custom-scroll" name="sysBizPermission.permission_def"
											maxlength="200"	id="sysBizPermission_permission_def"></textarea> 
									</label>
								</section>
								<div class="help-block with-errors"></div>  
							</div>
						</fieldset>
					</form>
				</div>
				<div class="row" id="table">
</div>
			</div>
		</div>
	</div>
</div>
</div>
<div class="modal-footer">
	<a href="#" class="btn" data-dismiss="modal" id="cl">关闭</a>
	<a href="#" class="btn btn-primary" id="bsb">保存</a>
</div>

<div id="addp" style="display: none;">
<script type="text/javascript">
$(document).ready(function() {
	$("#addParam").validator();
})
</script>
	<form  class="smart-form" id="addParam" onsubmit="return false">
		<fieldset>
			<div class="row">
				<section class="col">
					<label class="input"> <i class="icon-append fa fa-umbrella"></i>
						<input type="text" id="paramname" placeholder="名称" required>
					</label>
				</section>
			</div>
			<div class="row">
				<section class="col">
					<label class="input"> <i
						class="icon-append fa fa-rocket"></i> <input type="text"
						id="paramserial" placeholder="序号(数值类型)" required pattern="^([0-9])$">
					</label>
				</section>
			</div>
			<section>
				<label class="select"> 
					<select id="paramtype">
						<option value="" selected="" disabled="">参数类型</option>
						<option value="number">数值</option>
						<option value="string">字符串</option>
						<option value="collection">集合</option>
					</select> <i></i>
				</label>
			</section>
		</fieldset>
		<footer>
			<input type="submit" class="btn btn-primary" id="saveparam" value="保存" onclick="save_param()"/>
		</footer>
	</form>
</div>

<div id="runp" style="display: none;">
<script type="text/javascript">
$(document).ready(function() {
	$("#runParam").validator();
})
</script>
	<form id="runParam" onsubmit="return false">
		<fieldset id="paramset"></fieldset>
		<input type="submit" class="btn btn-primary" value="运行" onclick="testIt()" style="float: right; margin-bottom: 3px;margin-top: -10px"/>
	</form>
</div>