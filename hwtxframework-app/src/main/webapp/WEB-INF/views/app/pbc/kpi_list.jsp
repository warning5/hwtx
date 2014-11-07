<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .jarviswidget > div {
        border-width: 0;
    }

    .smart-form footer {
        background: none;
    }
</style>

<script>
    $(document).ready(function () {
        initTablePipeline();
        $('#pbcKpiTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "bFilter": false,
            "sPaginationType": "bootstrap_full",
            "sAjaxSource": "${ctx}/app/pbc/kpi_data",
            "sServerMethod": "POST",
            "iDisplayLength": 10,
            "aLengthMenu": [10, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "aoColumns": [
                { "mData": "date"},
                { "mData": "statusShow"},
                { "mData": "orgName"},
                { "mData": "vkpiSubmitTime"},
                { "mData": "vkpiHandleTime"},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="viewAction(this)" href="#" url="${ctx}/app/pbc/viewKpi?region=' +
                                full.vkpiRegion + '&date=' + full.date + '&orgId=' + full.orgId + '">查看</a>&nbsp;&nbsp;' +
                                (full.vkpiStatus == 1 ?
                                        '<a onclick="backAction(this)" href="#" url="${ctx}/app/pbc/showBack?region=' +
                                        full.vkpiRegion + '&date=' + full.date + '&orgId=' + full.orgId + '">退回</a>' : "");
                    }
                }
            ]
        });
    });

    function viewAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('查看数据', url, {}, 880, 450);
    }
    function backAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('指标退回', url);
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

            </div>
            <table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-hover"
                   id="pbcKpiTable">
                <thead>
                <tr>
                    <th>年份</th>
                    <th>状态</th>
                    <th>机构</th>
                    <th>提交时间</th>
                    <th>处理时间</th>
                    <th>操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>