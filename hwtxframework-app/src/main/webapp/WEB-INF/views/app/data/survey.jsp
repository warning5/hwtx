<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    $(document).ready(function () {
        if ($.fn.select2) {
            $('.select2').each(function () {
                var $this = $(this);
                var width = $this.attr('data-select-width') || '100%';
                $this.select2({
                    placeholder: "问卷类型",
                    allowClear: true,
                    width: width
                })
            })
        }
        $("#type").change(function () {
            var value = $("#type").val();
            if (value != "") {
                $.get('${ctx}/app/survey/questions?type=' + value, function (data) {
                    $('.fuelux').html(data);
                });
            }
        });
        pageSetUp();
    });
</script>
<section id="widget-grid" class="">
    <div class="jarviswidget" id="wid-id-2" data-widget-editbutton="false" data-widget-deletebutton="false">
        <header>
            <h2>问卷调查</h2>
        </header>
        <div>
            <div class="jarviswidget-editbox"></div>

            <select class="select2" id="type" data-select-width="250px">
                <option value="">请选择</option>
                <c:forEach items="${surveies}" var="survey">
                    <option value="${survey.surveyId}">${survey.surveyName}</option>
                </c:forEach>
            </select>
            <c:forEach items="${surveiesCount}" var="survey">
                ${survey.surveyName}:
                <span class="badge bg-color-red" id="${survey.surveyId}">
                ${survey.count}
                </span>
            </c:forEach>
            <hr class="simple">
            <div class="widget-body fuelux"></div>
        </div>
    </div>
</section>