<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .popover {
        max-width: 400px;
    }
</style>
<table class="table table-striped table-bordered table-condensed">
    <tr>
        <th>名称</th>
        <th>原始值</th>
        <th>录入部门</th>
    </tr>
    <c:forEach items="${result}" var="item" varStatus="varStatus">
        <tr>
            <td>${item.name}</td>
            <td>${item.value}</td>
            <td>${item.orgName}</td>
        </tr>
    </c:forEach>
</table>