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
            if ($('#ruleContainer').jstree().is_loaded('${id}')) {
                $('#ruleContainer').jstree().load_node('${id}');
            }
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

        $("#extend").click(function () {
            $("#one").slideToggle(200);
            $("#two").slideToggle(200);
            $("#comparisonText").focus();
            if ($(this).hasClass("fa-chevron-down")) {
                $(this).removeClass("fa-chevron-down")
                $(this).addClass("fa-chevron-up")
            } else {
                $(this).removeClass("fa-chevron-up")
                $(this).addClass("fa-chevron-down")
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
            <form action="${ctx}/app/rule/saveKpi" method="post" id="inputForm" class="smart-form">
                <input type="hidden" name="defKpi.dkpiId" value="${defKpi.dkpiId}"/>
                <input type="hidden" name="defKpi.dclassId" value="${defKpi.dclassId}"/>
                <input type="hidden" name="defKpi.pid" value="${defKpi.pid}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">指标名称:</label>
                            <label class="input">
                                <input type="text" name="defKpi.dkpiName" required
                                       maxlength="100" value="${defKpi.dkpiName}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">分值:</label>
                            <label class="input">
                                <input type="number" name="defKpi.dkpiscore" required value="${defKpi.dkpiscore}"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">顺序:</label>
                            <label class="input">
                                <input type="number" name="defKpi.dkpiSquence" required value="${defKpi.dkpiSquence}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">说明:</label>
                            <label class="input">
                                <input type="text" name="defKpi.dkpiRemark" value="${defKpi.dkpiRemark}"
                                       maxlength="100"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-4">
                            <label class="control-label">是否复合指标:</label>

                            <div class="inline-group">
                                <label class="radio">
                                    <input type="radio" name="defKpi.complex" value="1"
                                           <c:if test="${defKpi.complex==1}">checked="checked"</c:if>
                                           required/>
                                    <i></i>是
                                </label>
                                <label class="radio">
                                    <input type="radio" name="defKpi.complex" value="0"
                                           <c:if test="${defKpi.complex==0}">checked="checked"</c:if>
                                           required/>
                                    <i></i>否
                                </label>
                            </div>
                        </section>
                        <section class="col col-7">
                            <label class="control-label">计算类型:</label>

                            <div class="inline-group">
                                <c:forEach var="item" items="${fns:getDictList('dict_computeType')}">
                                    <label class="radio">
                                        <input type="radio" name="defKpi.dkpiStandardizeType" value="${item.value}"
                                                <c:if test="${defKpi.dkpiStandardizeType==item.value}"> checked="checked"</c:if>/>
                                        <i></i>${item.label}
                                    </label>
                                </c:forEach>
                            </div>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-2">
                            <div id="one" style="display: none">
                                <label class="control-label">附加计算:</label>
                                <label class="select">
                                    <select name="comparisonOp">
                                        <option value="">选择</option>
                                        <option value="&lt;"
                                                <c:if test="${defKpi.comparisonOp == '<'}"> selected="selected
                                                "</c:if>>&lt;</option>
                                        <option value="&gt;"
                                                <c:if test="${defKpi.comparisonOp == '>'}"> selected="selected
                                                "</c:if>>&gt;</option>
                                        <option value="="
                                                <c:if test="${defKpi.comparisonOp == '='}"> selected="selected
                                                "</c:if>>=</option>
                                        <option value="&lt;="
                                                <c:if test="${defKpi.comparisonOp == '<='}"> selected="selected
                                                "</c:if>>&lt;=</option>
                                        <option value="&gt;="
                                                <c:if test="${defKpi.comparisonOp == '>='}"> selected="selected
                                                "</c:if>>&gt;=</option>
                                    </select> <i></i>
                                </label>
                            </div>
                        </section>
                        <section class="col col-5">
                            <div id="two" style="display: none">
                                <label class="control-label"></label>
                                <label class="input">
                                    <input type="text" name="comparisonText" value="${defKpi.comparisonText}"
                                           id="comparisonText"/>
                                </label>
                            </div>
                        </section>
                        <section class="col col-5">
                            <div class="fa fa-fw fa-chevron-down" title="计算说明"
                                 id="extend" style="cursor:pointer;float: right;"></div>
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