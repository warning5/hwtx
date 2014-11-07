var prefix = $("#prefix").html();

	function highlightRow(){
		$("#userTable tbody tr").click( function( e ) {
	        if ($(this).hasClass('row_selected')) {
	            $(this).removeClass('row_selected');
	        }else {
	            $(this).addClass('row_selected');
	        }
	        
	        var length = $("#userTable tbody tr.row_selected").length;
			if( length == 0){
				if(!$("#deluser").hasClass("disabled")){
					$("#deluser").addClass("disabled")
				}
			}else{
				if($("#deluser").hasClass("disabled")){
					$("#deluser").removeClass("disabled")
				}
			}
	    });
	}
	
	function ajaxInvoke(url){
		
		var ids = new Array();
		var trs = new Array();

		$("#userTable tbody tr.row_selected").each(function(){
			var td = $(this).children('td:eq(0)');
			ids.push(td.find("div").html());
			trs.push($(this));
		});
		var oTable = $('#userTable').dataTable();
		var orgId = $("#uorgId").html();
		$.ajax({
			type : 'post',
			dataType : "json",
			url: url,
			data: {userIds : ids.toString(),orgId : orgId},
			cache : false,
			success : function(content) {
			    if(trs != null) {
			       for(tr in trs){
				       oTable.fnDeleteRow(tr,null,false);
			       }
                    HWTX.refreshTable($('#userTable'));
			    }
				message_box.show(content.message,content.code);
				HWTX.gDialogClose();
				updateTree(orgId,content.userCount);
			},
			error : function(content){
				message_box.show(content.message,content.code);
			}
		});
	}
	
	function updateTree(orgId,ucount){
		$('#orgcontainer li[id="'+orgId+'"] span').html(ucount);
	}
	
	$(document).ready(function() {
        initTablePipeline();
	    $('#userTable').dataTable({
	        "bProcessing": true,
	        "bServerSide": true,
	        "bFilter": false,
	        "sPaginationType" : "bootstrap_full",
	        "sAjaxSource": prefix+"/sys/user/data",
	        "sServerMethod": "POST",
	        "iDisplayLength": 10,
	        "aLengthMenu": [5,10, 25, 50],
	        "fnServerData": fnDataTablesPipeline,
	        "fnServerParams": function ( aoData ) {
	            aoData.push({"name":"searchSysUser.userName","value":$("#userName").val()});
	            aoData.push({"name":"searchSysUser.startDate","value":$("#startdate").val()});
	            aoData.push({"name":"searchSysUser.finishDate","value":$("#finishdate").val()});
	            aoData.push({"name":"searchSysUser.orgId","value":$("#searchSysUserOrgId").val()});
	            aoData.push({"name":"searchSysUser.includeUser","value":$("#includeUser").val()});
	        },
	        "fnDrawCallback": function( oSettings ) {
	        	highlightRow();
	        },
 	        "aoColumns": [
	                      { "mData": "num","bSortable": false,
	                    	"mRender": function ( data, type, full ) {
                    	        return '<div class=\"hide\">'+full.userId+'</div>'+data;
                    	      }
	                      },
	                      { "mData": "name"},
	                      { "mData": "login_date"}
	                  ] 
	    });
	    
	    document.onkeydown = function(e){
	        var ev = document.all ? window.event : e;
	        if(ev.keyCode==13) {
	        	$("#userSubmit").click();
	         }
	    }
	    
	    $("#osb").click(function(){
			ajaxInvoke(prefix+"/sys/org/assignUser");
	    });
	    
	    $("#userSubmit").click(function(){
            HWTX.refreshTable($('#userTable'));
	    });
	    
	    $("#deluser").click(function(){
	    	ajaxInvoke(prefix+"/sys/org/delAssignedUser");
	    });
	    
		$('#startdate').datetimepicker({
			format: 'yyyy-mm-dd hh:ii:ss',
			autoclose: true,
			todayBtn: true,
			language : "zh-CN"
		});
		
		$('#finishdate').datetimepicker({
			format: 'yyyy-mm-dd hh:ii:ss',
			autoclose: true,
			todayBtn: true,
			language : "zh-CN"
		});
		
		$("#clearstart").click(function(){
			$("#startdate").val("");
		});
		$("#clearend").click(function(){
			$("#finishdate").val("");
		});
		
		$("#startdate").on("",function (e) {
            $('#finishdate').data("DateTimePicker").setMinDate(e.date);
        });
		
		$('#startdate').datetimepicker().on("changeDate", function(e){
			$('#finishdate').datetimepicker("setStartDate",e.date);
		});
        
		$("#finishdate").datetimepicker().on("changeDate",function (e) {
            $('#startdate').datetimepicker("setEndDate",e.date);
        });
	});