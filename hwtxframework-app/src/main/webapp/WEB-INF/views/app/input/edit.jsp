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
            <form action="${ctx}/app/input/update" method="post" id="inputForm" class="smart-form">
                <input type="hidden" value="${type}" name="type"/>
                <input type="hidden" name="statistic" value="${statistic}"/>
                <input type="hidden" name="startdate" value="${date}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">数据年份</label>
                            <label class="input state-disabled">
                                <input type="text" placeholder="年份"
                                       class="form-control" name="startdate" value="${date}" disabled="disabled"/>
                            </label>
                        </section>
                    </div>
                    <c:forEach var="item" items="${defValueInidata}" varStatus="status">
                        <c:set var="type" value=""/>
                        <c:set var="dvalue" value="${item.value}"/>
                        <c:if test="${item.defInidata.diniDataType==5||item.defInidata.diniDataType==2}">
                            <c:choose>
                                <c:when test="${item.defInidata.diniFractionDigits == 0}">
                                    <c:set value="number digits" var="type"/>
                                    <fmt:parseNumber value="${item.value}" var="dvalue" integerOnly="true"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set value="number rate${item.defInidata.diniFractionDigits}" var="type"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <c:if test="${status.index%3==0}">
                            <div class="row">
                        </c:if>
                        <section class="col col-4">
                            <label class="control-label">${item.defInidata.diniDataLabel}:</label>
                            <label class="input <c:if test="${readOnly}">state-disabled</c:if>">
                                <i class="icon-append" style="width: 33px">
                                        ${fns:getDictLabel(item.defInidata.diniDataUnit,"dict_dataUnit","")}
                                </i>
                                <input type="text" class="${type}" name="${item.defInidata.diniDataName}"
                                       <c:if test="${item.defInidata.diniDataRequired==1}">required</c:if>
                                       <c:if test="${!empty item.defInidata.diniDataMaxLength}">maxlength="${item.defInidata.diniDataMaxLength}"</c:if>
                                       <c:if test="${!empty item.defInidata.diniDataMinLength}">minlength="${item.defInidata.diniDataMinLength}"</c:if>
                                       <c:if test="${!empty item.defInidata.diniDataMinValue}">min="${item.defInidata.diniDataMinValue}"</c:if>
                                       <c:if test="${!empty item.defInidata.diniDataMaxValue}">max="${item.defInidata.diniDataMaxValue}"</c:if>
                                       value="${dvalue}" <c:if test="${readOnly}">disabled="disabled"</c:if>/>
                            </label>
                        </section>
                        <c:if test="${status.count%3==0||status.last}">
                            </div>
                        </c:if>
                    </c:forEach>
                </fieldset>
                <footer>
                    <c:if test="${!readOnly}">
                        <input id="btnSubmit" class="btn btn-primary" type="submit"
                               value="保 存"/>&nbsp;
                    </c:if>
                    <input id="btngoback" class="btn btn-primary" type="button"
                           value="取 消" onclick="HWTX.gDialogClose()"/>
                </footer>
            </form>
        </div>
    </div>
</div>