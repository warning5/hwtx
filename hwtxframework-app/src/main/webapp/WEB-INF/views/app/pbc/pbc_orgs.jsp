<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .row_selected {
        background-color: #B0BED9;
    }
</style>
<script>
    $(document).ready(function () {
        initTablePipeline();
        $('#pbcOrgTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "bFilter": false,
            "sPaginationType": "bootstrap_full",
            "sAjaxSource": "${ctx}/app/pbc/data",
            "sServerMethod": "POST",
            "iDisplayLength": 10,
            "aLengthMenu": [10, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "fnServerParams": function (aoData) {
                aoData.push({"name": "name", "value": $("#pbcOrgName").val()});
            },
            "fnDrawCallback": function (oSettings) {
                //highlightRow();
            },
            "aoColumns": [
                { "mData": "num", "bSortable": false },
                { "mData": "name"},
                { "mData": "region"},
                { "mData": "contact"},
                { "mData": "phone"},
                { "mData": "address"},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="editAction(this)" href="#" url="${ctx}/app/pbc/show?id=' +
                                full.id + '">修改</a>&nbsp;&nbsp;' +
                                '<a onclick="delAction(this)" href="#" url="${ctx}/app/pbc/delete?ids=' +
                                full.orgId + '">删除</a>';
                    }
                }
            ]
        });
        $("#adda").click(function () {
            HWTX.gDialogCreate("添加", "${ctx}/app/pbc/add?roleId=${roleId}");
            initUI();
        });
        $("#orgSubmit").click(function () {
            HWTX.refreshTable($('#pbcOrgTable'));
        });
    });

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
                                HWTX.refreshTable($('#pbcOrgTable'));
                                message_box.show(content.message, content.code);
                            },
                            error: function (content) {
                                message_box.show(content.message, content.code);
                            }
                        });
                    }
                });
    }

    function editAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('修改', url);
    }
</script>
<div class="jarviswidget jarviswidget-color-darken" id="wid-id-0" data-widget-editbutton="false">
    <div>
        <!-- widget edit box -->
        <div class="jarviswidget-editbox">
            <!-- This area used as dropdown edit box -->

        </div>
        <!-- end widget edit box -->

        <!-- widget content -->
        <div class="widget-body no-padding">
            <div class="widget-body-toolbar">
                <form action="${ctx}/app/pbc/city/orgs" onsubmit="return false" method="post" id="pbcOrgForm"
                      class="form-inline" role="form">
                    <fieldset style="top: 3px">
                        <a href="javascript:void(0);" class="btn btn-primary" id="adda"><i class="fa fa-plus"></i>
                            添加</a>

                        <div class="form-group">
                            <div class="input-icon-right">
                                <i class="fa fa-asterisk"></i> <input type="text" placeholder="名称"
                                                                      id="pbcOrgName" class="form-control" name="name">
                            </div>
                        </div>
                        <button class="btn btn-primary" id="orgSubmit">查询</button>
                    </fieldset>
                </form>
            </div>
            <table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-hover"
                   id="pbcOrgTable">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>名称</th>
                    <th>区域</th>
                    <th>联系人</th>
                    <th>电话</th>
                    <th>地址</th>
                    <th>操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>