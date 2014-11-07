<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/datetimepicker.jsp" %>
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
        $("#inputForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#inputForm"), 'json', saveCallback);
            }
        });

        $('#startdate').datetimepicker({format: "yyyy", startView: 4, minView: 4,
            maxView: 4, language: "zh-CN" });
    });

    function saveCallback(json) {
        if (json.code == 'success') {
            message_box.show(json.message, 'success');
            HWTX.gDialogClose();
            HWTX.refreshTable($("#inputTable${type}"));
        } else {
            message_box.show(json.message, 'error');
        }
        return true;
    }
</script>

<div class="jarviswidget" id="wid-id-3" data-widget-editbutton="false"
     data-widget-custombutton="false">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body no-padding">
            <form action="${ctx}/app/input/save" method="post" id="inputForm" class="smart-form">
                <input type="hidden" value="${type}" name="type"/>
                <input type="hidden" name="statistic" value="${statistic}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">数据年份</label>
                            <label class="input">
                                <input type="text" id="startdate" placeholder="数据年份"
                                       class="form-control" name="startdate" value="${startdate}" required>
                            </label>
                        </section>
                    </div>
                    <c:forEach var="item" items="${definidata}" varStatus="status">
                        <c:set var="type" value=""/>
                        <c:if test="${item.diniDataType==5||item.diniDataType==2}">
                            <c:choose>
                                <c:when test="${item.diniFractionDigits == 0}">
                                    <c:set value="number digits" var="type"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set value="number rate${item.diniFractionDigits}" var="type"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <c:if test="${status.index%3==0}">
                            <div class="row">
                        </c:if>
                        <section class="col col-4">
                            <label class="control-label">${item.diniDataLabel}:</label>
                            <label class="input">
                                <i class="icon-append" style="width: 33px">
                                        ${fns:getDictLabel(item.diniDataUnit,"dict_dataUnit","")}
                                </i>
                                <input type="text" class="${type}" name="${item.diniDataName}"
                                       <c:if test="${item.diniDataRequired==1}">required</c:if>
                                       <c:if test="${!empty item.diniDataMaxLength}">maxlength="${item.diniDataMaxLength}"</c:if>
                                       <c:if test="${!empty item.diniDataMinLength}">minlength="${item.diniDataMinLength}"</c:if>
                                       <c:if test="${!empty item.diniDataMinValue}">min="${item.diniDataMinValue}"</c:if>
                                       <c:if test="${!empty item.diniDataMaxValue}">max="${item.diniDataMaxValue}"</c:if>
                                       value="12.00"/>
                            </label>
                        </section>
                        <c:if test="${status.count%3==0||status.last}">
                            </div>
                        </c:if>
                    </c:forEach>
                </fieldset>
                <footer>
                    <input id="btnSubmit" class="btn btn-primary" type="submit"
                           value="保 存"/>&nbsp;
                    <input id="btngoback" class="btn btn-primary" type="button"
                           value="取 消" onclick="HWTX.gDialogClose()"/>
                </footer>
            </form>
        </div>
    </div>
</div>