var prefix = $("#prefix").html();

	function delAction(action){
    	var url = $(action).attr("url");
    	var tr = $(action).parent().parent();
    	var trs = []
    	trs.push(tr); 
    	del(url,trs);
	} 
	 
	function editAction(action){
		var url = $(action).attr("url");
		gcreate('修改用户', url);
	}
	
	function del(url,trs){
		var oTable = $('#userTable').dataTable();
		gDialog.fConfirm('确认执行','你确定执行这个操作么？',
			function(rs){
				if(rs){
					$.ajax({
						type : 'post',
						dataType : "json",
						url: url,
						cache : false,
						success : function(content) {
						    if(trs != null) {
						       for(tr in trs){
							       oTable.fnDeleteRow(tr,null,false);
						       }
						       refreshTable();
						    }
							message_box.show(content.message,content.code);
						},
						error : function(content){
							message_box.show(content.message,content.code);
						}
					});
				}
			}
		);
	}
	
	function highlightRow(){
		$("#bondOrgTable tbody tr").click( function( e ) {
	        if ($(this).hasClass('row_selected')) {
	            $(this).removeClass('row_selected');
	        }else {
	            $(this).addClass('row_selected');
	        }
	    });
	}
	
	function refreshTable(){
		var oTable = $('#bondOrgTable').dataTable();
    	oCache.iCacheLower = -1;
    	oTable.fnDraw(false);
	}
	
	$(document).ready(function() {
        initTablePipeline();
	    $('#bondOrgTable').dataTable({
	        "bProcessing": true,
	        "bServerSide": true,
	        "bFilter": false,
	        "sPaginationType" : "bootstrap_full",
	        "sAjaxSource": prefix+"/bond/org/data",
	        "sServerMethod": "POST",
	        "iDisplayLength": 10,
	        "aLengthMenu": [10,25, 50, 100],
	        "fnServerData": fnDataTablesPipeline,
	        "fnServerParams": function ( aoData ) {
	            aoData.push({"name":"searchSysUser.userName","value":$("#userName").val()});
	            aoData.push({"name":"searchSysUser.startDate","value":$("#startdate").val()});
	            aoData.push({"name":"searchSysUser.finishDate","value":$("#finishdate").val()});
	        },
	        "fnDrawCallback": function( oSettings ) {
	        	highlightRow();
	        },
 	        "aoColumns": [
	                      { "mData": "num","bSortable": false },
	                      { "mData": "name"},
	                      { "mData": "orgId"},
	                      { "mData": "credit_code"},
	                      { "mData": "legal_person"},
	                      { "mData": "actions","bSortable": false,
                    	  	"mRender": function ( data, type, full ) {
                    	        return '<a onclick="editAction(this)" href="#" url="'+prefix+'/bond/org/showEdit?id='+full.id+'">修改</a>&nbsp;&nbsp;'+
                    	        	   '<a onclick="delAction(this)" href="#" url="'+prefix+'/sys/user/d?ids='+full.id+'">删除</a>';
                    	      }  
	                      }
	                  ] 
	    });
	    
	    document.onkeydown = function(e){
	        var ev = document.all ? window.event : e;
	        if(ev.keyCode==13) {
	        	$("#userSubmit").click();
	         }
	    }
	    
	    $("#userSubmit").click(function(){
	    	refreshTable();
	    });
	    
		$("#delBat").click(function(){
			var ids = new Array();
			var trs = new Array();

			var length = $("#userTable tbody tr.row_selected").length;
			if( length == 0){
				alert("请选择一个用户");
				return;
			}
			
			$("#userTable tbody tr.row_selected").each(function(){
				ids.push($(this).children('td').eq(1).html());
				trs.push($(this));
			});
			del(prefix+"/sys/user/d?ids="+ids.toString(),trs);			
		});
		
		$("#bonda").click(function(){
			gcreate('添加担保机构',  prefix+'/bond/org/showAdd',550,300);
			initUI();
		}); 
		
		$("#userm").click(function(){
			var id = getSelectedTr();
			if(id != ""){
				gcreate('修改用户',prefix+'/sys/user/showEdit?id='+id);
			}
		});
		
		$("#userAssignRole").click(function(){
			var id = getSelectedTr();
			if(id != ""){
				gcreate('分配角色', prefix+'/sys/user/showAssignRole?id='+id,550,350);
			}
		});
	});
	
	function getSelectedTr(){
		var length = $("#userTable tbody tr.row_selected").length;
		if( length == 0 || length > 1){
			alert("请选择一个用户");
			return "";
		}
		return $("#userTable tbody tr.row_selected").eq(0).children('td').eq(1).html();
	}