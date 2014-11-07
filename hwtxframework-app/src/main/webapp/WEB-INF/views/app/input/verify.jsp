<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .row_selected {
        background-color: #B0BED9;
    }
</style>
<script>
    $(document).ready(function () {
        pageSetUp();
        initTablePipeline();
        $('#inputTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "sPaginationType": "bootstrap_full",
            "bFilter": false,
            "sAjaxSource": "${ctx}/app/financialData/data?type=${type}",
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
                { "mData": "date"},
                { "mData": "orgName"},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="viewAction(this)" href="#" url="${ctx}/app/input/view?type=${type}&' +
                                'statistic=true&date=' + full.date + '&orgId=' + full.orgId + '">查看</a>&nbsp;&nbsp;' +
                                '<a onclick="backAction(this)" href="#" url="${ctx}/app/financialData/showBack?statistic=true&region=' + full.region + '&type=' +
                                full.type + '&date=' + full.date + '&orgId=' + full.orgId + '">退回</a>';

                    }
                }
            ]
        });
    });
    function viewAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('查看数据', url);
    }
    function backAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate('数据退回', url);
    }
</script>
<section id="widget-grid" class="">
    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0" data-widget-editbutton="false">
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
            <div class="widget-body no-padding">
                <div class="widget-body-toolbar"></div>
                <table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-hover"
                       id="inputTable">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>年份</th>
                        <th>机构</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>