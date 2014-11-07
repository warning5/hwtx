<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/datetimepicker.jsp" %>
<script type="text/javascript">
    $(document).ready(function () {
        pageSetUp();
    });
</script>
<script>
    function delAction(action) {
        var url = $(action).attr("url");
        gDialog.fConfirm('确认执行', '你确定执行这个操作么？',
                function (rs) {
                    if (rs) {
                        $.ajax({
                            type: 'post',
                            dataType: "json",
                            url: url,
                            cache: false,
                            success: function (content) {
                                HWTX.refreshTable($('#fuserTable'));
                                message_box.show(content.message, content.code);
                            },
                            error: function (content) {
                                message_box.show(content.message, content.code);
                            }
                        });
                    }
                }
        );
    }

    function editAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('修改用户', url, {}, 400, 300);
    }

    function highlightRow() {
        $("#userTable tbody tr").click(function (e) {
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            } else {
                $(this).addClass('row_selected');
            }
        });
    }

    $(document).ready(function () {
        initTablePipeline();
        $('#fuserTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "bFilter": false,
            "sPaginationType": "bootstrap_full",
            "sAjaxSource": "${ctx}/app/financial/data",
            "sServerMethod": "POST",
            "iDisplayLength": 25,
            "aLengthMenu": [10, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "fnServerParams": function (aoData) {
                aoData.push({"name": "searchSysUser.userName", "value": $("#userName").val()});
                aoData.push({"name": "searchSysUser.startDate", "value": $("#startdate").val()});
                aoData.push({"name": "searchSysUser.finishDate", "value": $("#finishdate").val()});
            },
            "fnDrawCallback": function (oSettings) {
                highlightRow();
            },
            "aoColumns": [
                { "mData": "num", "bSortable": false },
                { "mData": "userName"},
                { "mData": "orgName"},
                { "mData": "login_date"},
                { "mData": "userId", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="editAction(this)" href="#" url="${ctx}/app/financial/showEditUser?id=' +
                                full.userId + '&userOrg=' + full.orgId + '">修改</a>&nbsp;&nbsp;' +
                                '<a onclick="delAction(this)" href="#" url="${ctx}/app/financial/deleteUser?ids=' +
                                full.userId + '&userOrg=' + full.orgId + '">删除</a>';
                    }
                }
            ]
        });

        $("#userSubmit").click(function () {
            HWTX.refreshTable($('#fuserTable'));
        });

        $('#startdate').datetimepicker({
            format: 'yyyy-mm-dd hh:ii:ss',
            autoclose: true,
            todayBtn: true,
            language: "zh-CN"
        });

        $('#finishdate').datetimepicker({
            format: 'yyyy-mm-dd hh:ii:ss',
            autoclose: true,
            todayBtn: true,
            language: "zh-CN"
        });

        $("#clearstart").click(function () {
            $("#startdate").val("");
        });
        $("#clearend").click(function () {
            $("#finishdate").val("");
        });

        $("#startdate").on("", function (e) {
            $('#finishdate').data("DateTimePicker").setMinDate(e.date);
        });

        $('#startdate').datetimepicker().on("changeDate", function (e) {
            $('#finishdate').datetimepicker("setStartDate", e.date);
        });

        $("#finishdate").datetimepicker().on("changeDate", function (e) {
            $('#startdate').datetimepicker("setEndDate", e.date);
        });

        /*$("#delBat").click(function () {
         var ids = new Array();
         var trs = new Array();

         var length = $("#userTable tbody tr.row_selected").length;
         if (length == 0) {
         alert("请选择一个用户");
         return;
         }

         $("#userTable tbody tr.row_selected").each(function () {
         ids.push($(this).children('td').eq(1).html());
         trs.push($(this));
         });
         del(prefix + "/app/financial/deleteUser?ids=" + ids.toString(), trs);
         });*/

        $("#usera").click(function () {
            HWTX.gDialogCreate('添加用户', '${ctx}/app/financial/showAddUser', {}, 400, 280);
        });
    });
</script>
<section id="widget-grid">
    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0"
         data-widget-editbutton="false">
        <header>
            <span class="widget-icon"> <i class="fa fa-table"></i> </span>

            <h2>列表</h2>
        </header>
        <div>
            <!-- widget edit box -->
            <div class="jarviswidget-editbox">
                <!-- This area used as dropdown edit box -->

            </div>
            <!-- end widget edit box -->

            <!-- widget content -->
            <div class="widget-body no-padding">
                <div class="widget-body-toolbar">
                    <form action="${ctx}/app/financial/user" method="post" id="userForm"
                          onsubmit="return false" class="form-inline" role="form">
                        <fieldset style="top: 3px">
                            <a class="btn btn-primary" id="usera" href="#"><i class="fa fa-plus"></i> 添加</a>
                            <%-- <a class="btn btn-primary" id="delBat" href="#"><i class="fa fa-minus"></i> 删除</a>--%>
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-user"></i>
                                    <input type="text" placeholder="用户名" id="userName" class="form-control"
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
                       class="table table-bordered table-hover" id="fuserTable">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>用户名</th>
                        <th>所属机构</th>
                        <th>登录时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>