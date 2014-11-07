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
</style>

<script>
    $(document).ready(function () {
        $("#computeTreeTable").treeTable({
            expandLevel: 2
        });
    });
</script>

<table id="computeTreeTable" class="table table-striped table-bordered table-condensed">
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