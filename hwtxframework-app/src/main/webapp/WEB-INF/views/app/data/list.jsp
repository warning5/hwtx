<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .jarviswidget > div {
        border-width: 0;
    }

    .smart-form footer {
        background: none;
    }

    div.dataTables_length {
        top: -45px;
    }
</style>

<script>
    $(document).ready(function () {
        initTablePipeline();
        $('#kpiTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "bFilter": false,
            "sPaginationType": "bootstrap_full",
            "sAjaxSource": "${ctx}/app/compute/data",
            "sServerMethod": "POST",
            "iDisplayLength": 10,
            "aLengthMenu": [10, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "aoColumns": [
                { "mData": "date"},
                { "mData": "statusShow"},
                { "mData": "vkpiSubmitTime"},
                { "mData": "vkpiHandleTime"},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="viewAction(this)" href="#" url="${ctx}/app/pbc/viewKpi?orgId=${orgId}&region=${region}&date=' + full.date + '">查看</a>&nbsp;&nbsp;' +
                                (full.vkpiStatus == 2 ?
                                        '<a onclick="viewBackAction(this)" href="#" url="${ctx}/app/pbc/showBack?view=1&date=' + full.date + '">查看原因</a>' : "");
                    }
                }
            ]
        });
    });

    function viewAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('查看数据', url, {}, 880, 450);
    }

    function viewBackAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('退回原因', url);
    }
</script>

<table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-hover"
       id="kpiTable">
    <thead>
    <tr>
        <th>年份</th>
        <th>状态</th>
        <th>提交时间</th>
        <th>处理时间</th>
        <th>操作</th>
    </tr>
    </thead>
</table>