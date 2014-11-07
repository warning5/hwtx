<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/treetable.jsp" %>
<style>
    .jarviswidget > div {
        border-width: 0;
    }

    .smart-form footer {
        background: none;
    }

    #treeTable {
        width: 90%;
        margin-left: 38px;
        margin-right: 10px;
        margin-bottom: 18px;
        margin-top: 18px;
    }
</style>

<script>
    $(document).ready(function () {
        $("#treeTable").treeTable({
            expandLevel: 2
        });
        $("[id^=mtn]").hover(function(){
            var e=$(this);
            $.get(e.data('poload'),function(d) {
                e.popover({
                    trigger:"hover",
                    html:true,
                    title:"<i class='fa fa-fw fa-pencil'></i> 原始数据",
                    content: d}).popover('show');
            });
        });
    });
</script>
<div style="overflow-y:scroll; height: 450px">
    <table id="treeTable" class="table table-striped table-bordered table-condensed">
        <tr>
            <th width="500px">名称</th>
            <th>指标值</th>
        </tr>
        <c:forEach items="${result}" var="item" varStatus="varStatus">
            <tr id="${item.id}" pId="${item.pid}">
                <td>
                    <c:choose>
                        <c:when test="${item.kpi}">
                            <a href="#" id="mtn${varStatus.index}"
                               data-poload="${ctx}/app/pbc/viewIniData?kpiId=${item.id}&region=${region}&date=${date}"
                                    >${item.name}</a>
                        </c:when>
                        <c:otherwise>
                            ${item.name}
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${item.value}</td>
            </tr>
        </c:forEach>
    </table>
</div>
<div class="modal-footer">
    <a href="#" class="btn btn-primary" data-dismiss="modal" onclick="HWTX.gDialogClose()">关闭</a>
</div>