<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script src="/static/select2/select2.js"></script>
<script>
    $(document).ready(function () {
        if ($.fn.select2) {
            $('.select2').each(function () {
                var $this = $(this);
                var width = $this.attr('data-select-width') || '100%';
                $this.select2({
                    maximumSelectionSize: 3,
                    placeholder: "选择类型",
                    allowClear: true,
                    width: width
                })
            })
            $("#inidata").change(function (e) {
                if (e.added.id != "") {
                    var oldVal = $("#defFormula_calRuleShow").val();
                    $("#defFormula_calRuleShow").val(oldVal + e.added.text);
                    var sv = $("#defFormula_calRule").val();
                    $("#defFormula_calRule").val(sv + e.added.id);
                    var inidatas = $("#defFormula_inidata").val();
                    $("#defFormula_inidata").val(inidatas != "" ? inidatas + "," + e.added.id : e.added.id);
                }
            });
            $("#kpi").change(function (e) {
                if (e.added.id != "") {
                    var oldVal = $("#defFormula_calRuleShow").val();
                    $("#defFormula_calRuleShow").val(oldVal + e.added.text);
                    var sv = $("#defFormula_calRule").val();
                    $("#defFormula_calRule").val(sv + e.added.id);
                    var inidatas = $("#defFormula_inidata").val();
                    $("#defFormula_inidata").val(inidatas != "" ? inidatas + "," + e.added.id : e.added.id);
                }
            });
            $("#operator").change(function (e) {
                if (e.added.id != "") {
                    var oldVal = $("#defFormula_calRuleShow").val();
                    $("#defFormula_calRuleShow").val(oldVal + e.added.text);
                    var sv = $("#defFormula_calRule").val();
                    $("#defFormula_calRule").val(sv + e.added.id);
                }
            });
            $("#addParam").click(function () {
                var val = $("#param").val();
                var oldVal = $("#defFormula_calRuleShow").val();
                $("#defFormula_calRuleShow").val(oldVal + val);
                var sv = $("#defFormula_calRule").val();
                $("#defFormula_calRule").val(sv + val);
            });
            $("#clear").click(function () {
                $("#defFormula_calRuleShow").val("");
                $("#defFormula_calRule").val("");
                $("#defFormula_inidata").val("");
            });
            $("#btnSubmit").click(function () {
                $.ajax({
                    type: "post",
                    url: "${ctx}/app/rule/saveFormula",
                    data: $("#formula_form").serializeArray()
                }).done(function (msg) {
                    message_box.show(msg.message, msg.code);
                });
            });
        }
    });
</script>
<form class="smart-form" id="formula_form">
    <fieldset>
        <div class="row">
            <section>
                <label class="control-label">原始数据:</label>
                <label class="select">
                    <select class="select2" id="inidata" data-select-width="350px">
                        <option value="">请选择</option>
                        <c:forEach items="${inidata}" var="item">
                            <option value="${inidata_prefix}${item.diniDataId}">${item.diniDataLabel}</option>
                        </c:forEach>
                    </select>
                </label>
            </section>
        </div>
        <div class="row">
            <section>
                <label class="control-label">符号:</label>
                <label class="select">
                    <select class="select2" id="operator" data-select-width="350px">
                        <option value="">请选择</option>
                        <c:forEach items="${fns:getDictList('dict_operator')}" var="dict"
                                   varStatus="status">
                            <option value="${dict.value}">${dict.label}</option>
                        </c:forEach>
                    </select>
                </label>
            </section>
        </div>
        <div class="row">
            <section>
                <label class="control-label">指标:</label>
                <label class="select">
                    <select class="select2" id="kpi" data-select-width="350px">
                        <option value="">请选择</option>
                        <c:forEach items="${defClasses}" var="cls">
                            <optgroup label="${cls.defClass.dclassName}">
                                <c:forEach var="kpi" items="${cls.defKpis}">
                                    <option value="${kpi_prefix}${kpi.dkpiId}">${kpi.dkpiName}</option>
                                </c:forEach>
                            </optgroup>
                        </c:forEach>
                    </select>
                </label>
                <div class="note">
                    <strong>提示:</strong> 用于复合指标计算
                </div>
            </section>
        </div>
        <div class="row">
            <section>
                <label class="control-label">参数:</label>
                <label class="input">
                    <input type="text" id="param" style="width:350px;float: left;margin-right: 10px">
                    <a href="#" class="btn btn-success btn-sm" id="addParam">添加</a>
                </label>
            </section>
        </div>
        <div class="row">
            <section>
                <label class="control-label">计算表达式:</label>
                <label class="textarea state-disabled"> <i class="icon-prepend fa fa-eraser" id="clear"></i>
                    <textarea rows="5" cols="47" class="custom-scroll" disabled="disabled" id="defFormula_calRuleShow"
                              name="defFormula.calRuleShow">${defFormula.calRuleShow}</textarea>
                    <b class="tooltip tooltip-top-left"> <i class="fa fa-warning txt-color-teal"></i>
                        清除</b>
                </label>

                <div class="note">
                    <strong>提示:</strong> 计算表达式.
                </div>
            </section>
        </div>
        <input type="hidden" id="defFormula_calRule" name="defFormula.calRule" value="${defFormula.calRule}">
        <input type="hidden" id="defFormula_calRuleId" name="defFormula.calRuleId" value="${defFormula.calRuleId}">
        <input type="hidden" id="defFormula_inidata" name="defFormula.inidatas" value="${defFormula.inidatas}">
        <input type="hidden" id="kpiId" name="kpiId" value="${kpiId}">
    </fieldset>
    <footer>
        <a id="btnSubmit" class="btn btn-primary" href="javascript:void(0);">保存</a>
    </footer>
</form>