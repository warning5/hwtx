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
        $("#statisticForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#statisticForm"), 'json', saveCallback);
            }
        });

        $("#compute").click(function () {
            $("#statisticForm").submit();
        });

        $('#startdate').datetimepicker({format: "yyyy", startView: 4, minView: 4,
            maxView: 4, language: "zh-CN" });
    });

    var intervalId;

    function saveCallback(json) {
        $("#compute_content").html(json.message);
        intervalId = setInterval(getInfo, 1000);

    }

    function getInfo() {
        $.get("${ctx}/app/statistic/getStatisticInfo", null, function (json) {
            if (json.over && json.error == "") {
                clearInterval(intervalId);
                $("#compute_content").html("计算完成");
            } else if (json.error != "") {
                $("#compute_content").html(json.error);
                clearInterval(intervalId);
            } else if (json.error_region != "") {
                $("#compute_content").html(json.error_region);
                clearInterval(intervalId);
            } else {
                $("#compute_content").html("正在标准化区域" + json.running);
            }
        }, "json");

    }
</script>

<div class="jarviswidget well">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body">
            <hr class="simple">
            <ul class="nav nav-tabs bordered" id="mtab">
                <li class="active" id="ml1">
                    <a data-toggle="tab" href="#s1"><i
                            class="fa fa-fw fa-lg fa-list-alt"></i>数据计算</a>
                </li>
            </ul>

            <div class="tab-content padding-10">
                <div id="s1" class="tab-pane fade in active">
                    <form action="${ctx}/app/statistic/doIt" method="post" id="statisticForm"
                          class="form-inline"
                          role="form">
                        <input type="hidden" value="${survey}" name="survey"/>
                        <fieldset style="top: 3px">
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-calendar"></i>
                                    <input type="text" id="startdate" placeholder="数据年份"
                                           class="form-control" name="startdate" required
                                           value="${date}">
                                </div>
                            </div>
                            <a href="javascript:void(0);" class="btn btn-primary" id="compute">
                                <i class="fa fa-fire"></i> 计算
                            </a>
                        </fieldset>
                    </form>
                    <hr class="simple">
                    <div id="compute_content"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>