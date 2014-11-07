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
        $('#inputTable${type}').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "sPaginationType": "bootstrap_full",
            "bFilter": false,
            "sAjaxSource": "${ctx}/app/input/data?paginate=false&type=${type}&statistic=${statistic}&orgId=${orgId}",
            "sServerMethod": "POST",
            "iDisplayLength": 20,
            "aLengthMenu": [10, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "fnServerParams": function (aoData) {
                aoData.push({"name": "name", "value": $("#orgName").val()});
            },
            "fnDrawCallback": function (oSettings) {
//                highlightRow();
            },
            "aoColumns": [
                { "mData": "num", "bSortable": false },
                { "mData": "year"},
                { "mData": "status", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return "${typeName}";
                    }
                },
                { "mData": "statusShow"},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        if (full.status == 1) {
                            return '<a onclick="viewAction(this)" href="#" url="${ctx}/app/input/view?orgId=${orgId}&type=${type}&' +
                                    'statistic=${statistic}&date=' + full.year + '">查看</a>';
                        } else {
                            var back = full.status == 2 ? '&nbsp;&nbsp;<a onclick="viewBackAction(this)" href="#" ' +
                                    'url="${ctx}/app/financialData/showBack?view=true&orgId=${orgId}&statistic=${statistic}&date=' + full.year + '">查看原因</a>' : "";

                            return '<a onclick="editAction(this)" href="#" url="${ctx}/app/input/edit?orgId=${orgId}&type=${type}&statistic=${statistic}&date=' +
                                    full.year + '&status=' + full.status + '">修改</a>&nbsp;&nbsp;' +
                                    '<a onclick="delAction(this)" href="#" url="${ctx}/app/input/delete?type=${type}&statistic=${statistic}&date=' +
                                    full.year + '">删除</a>' + back;
                        }
                    }
                }
            ]
        });
        $("#inputa").click(function () {
            HWTX.gDialogCreate("新增数据", "${ctx}/app/input/show", {statistic: '${statistic}', type: '${type}'});
        });
    });
    function viewAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('原始数据', url);
    }
    function viewBackAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('退回原因', url);
    }

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
                                HWTX.refreshTable($('#inputTable${type}'));
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
        HWTX.gDialogCreate('修改原始数据', url);
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
                <div class="form-inline">
                    <fieldset style="top: 3px">
                        <a href="javascript:void(0);" class="btn btn-primary" id="inputa"><i class="fa fa-plus"></i>
                            添加</a>
                    </fieldset>
                </div>
            </div>
            <table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-hover"
                   id="inputTable${type}">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>年份</th>
                    <th>机构</th>
                    <th>状态</th>
                    <th>操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>