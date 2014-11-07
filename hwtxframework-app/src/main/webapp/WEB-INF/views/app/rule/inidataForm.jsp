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

    function saveCallback(json) {
        if (json.code == 'success') {
            message_box.show(json.message, 'success');
            HWTX.gDialogClose();
            HWTX.refreshTable($('#iniDataTable'));
        } else {
            message_box.show(json.message, 'error');
        }
        return true;
    }
    $(document).ready(function () {
        $("#inputForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#inputForm"), 'json', saveCallback);
            }
        });
        $("[name='defInidata.diniDataType']").change(function () {
            if ($(this).val() == 5) {
                $("[name='defInidata.diniDataMaxValue']").val(100);
                $("[name='defInidata.diniDataMinValue']").val(-100);
            }
        });
    });
</script>

<div class="jarviswidget" id="wid-id-8" data-widget-editbutton="false"
     data-widget-custombutton="false">
    <div>
        <div class="jarviswidget-editbox">
            <!-- This area used as dropdown edit box -->
        </div>
        <!-- end widget edit box -->

        <!-- widget content -->
        <div class="widget-body no-padding">
            <form action="${ctx}/app/inidata/save" method="post" id="inputForm" class="smart-form">
                <input type="hidden" name="defInidata.diniDataId" value="${defInidata.diniDataId}"/>
                <input type="hidden" name="defInidata.dkpiId" value="${defInidata.dkpiId}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">显示名:</label> <label class="input">
                            <input type="text" name="defInidata.diniDataLabel" value="${defInidata.diniDataLabel}"
                                   maxlength="45" required/>
                        </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">名称:</label>
                            <label class="input">
                                <input type="text" name="defInidata.diniDataName" required
                                       maxlength="100" value="${defInidata.diniDataName}"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">分类:</label>
                            <label class="select">
                                <select name="defInidata.submitOrgRole" required>
                                    <c:forEach items="${orgRoles}" var="orgRole">
                                        <c:choose>
                                            <c:when test="${orgRole.key!=null && orgRole.key eq defInidata.submitOrgRole}">
                                                <option value="${orgRole.key}"
                                                        selected="selected">${orgRole.value}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${orgRole.key}">${orgRole.value}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select> <i></i>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">数据类型:</label>
                            <label class="select">
                                <select name="defInidata.diniDataType" required>
                                    <option value="">请选择</option>
                                    <c:forEach items="${fns:getDictList('dict_dataType')}" var="dict"
                                               varStatus="status">
                                        <c:choose>
                                            <c:when test="${dict.value!=null && dict.value eq
                                                     defInidata.diniDataType}">
                                                <option value="${dict.value}"
                                                        selected="selected">${dict.label}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${dict.value}">${dict.label}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select> <i></i>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">最大值:</label>
                            <label class="input">
                                <input type="number" name="defInidata.diniDataMaxValue"
                                       value="${defInidata.diniDataMaxValue}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">最小值:</label>
                            <label class="input">
                                <input type="number" name="defInidata.diniDataMinValue"
                                       value="${defInidata.diniDataMinValue}"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">最大长度:</label>
                            <label class="input">
                                <input type="number" name="defInidata.diniDataMaxLength"
                                       value="${defInidata.diniDataMaxLength}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">最小长度:</label>
                            <label class="input">
                                <input type="number" name="defInidata.diniDataMinLength"
                                       value="${defInidata.diniDataMinLength}"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-3">
                            <label class="control-label">是否必须:</label>

                            <div class="inline-group">
                                <label class="radio">
                                    <input id="need" type="radio"
                                           name="defInidata.diniDataRequired" value="1"
                                           <c:if test="${defInidata.diniDataRequired eq 1}">checked="checked"</c:if>
                                           required/>
                                    <i></i>是
                                </label>
                                <label class="radio">
                                    <input id="no_need" type="radio"
                                           name="defInidata.diniDataRequired" value="0"
                                           <c:if test="${defInidata.diniDataRequired eq 0}">checked="checked"</c:if>
                                           required/>
                                    <i></i>否
                                </label>
                            </div>
                        </section>
                        <section class="col col-3">
                            <label class="control-label">是否需要统计:</label>

                            <div class="inline-group">
                                <label class="radio">
                                    <input type="radio"
                                           name="defInidata.isStaticMark" value="1"
                                           <c:if test="${defInidata.isStaticMark eq 1}">checked="checked"</c:if>
                                           required/>
                                    <i></i>是
                                </label>
                                <label class="radio">
                                    <input type="radio"
                                           name="defInidata.isStaticMark" value="0"
                                           <c:if test="${defInidata.isStaticMark eq 0}">checked="checked"</c:if>
                                           required/>
                                    <i></i>否
                                </label>
                            </div>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">单位:</label>
                            <label class="select">
                                <select name="defInidata.diniDataUnit" required>
                                    <c:forEach items="${fns:getDictList('dict_dataUnit')}" var="dict">
                                        <c:choose>
                                            <c:when test="${dict.value!=null && dict.value eq defInidata.diniDataUnit}">
                                                <option value="${dict.value}"
                                                        selected="selected">${dict.label}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${dict.value}">${dict.label}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select> <i></i>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">小数位数:</label>
                            <label class="input">
                                <input type="number" name="defInidata.diniFractionDigits"
                                       value="${defInidata.diniFractionDigits}" max="4" min="0"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">说明:</label>
                            <label class="input">
                                <textarea name="defInidata.diniDataRemark"
                                          cols="55" rows="3">${defInidata.diniDataRemark}</textarea>
                            </label>
                        </section>
                    </div>
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