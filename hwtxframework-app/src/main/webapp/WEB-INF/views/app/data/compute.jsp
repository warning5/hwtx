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
        $("#computeForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#computeForm"), 'html', saveCallback);
            }
        });

        $("#compute").click(function () {
            $("#computeForm").submit();
        });

        $('#startdate').datetimepicker({format: "yyyy", startView: 4, minView: 4,
            maxView: 4, language: "zh-CN" });

        $("#submit").click(function () {
            gDialog.fConfirm('确认执行', '上报后无法修改!',
                    function (rs) {
                        if (rs) {
                            $.ajax({
                                type: 'post',
                                dataType: "html",
                                url: "${ctx}/app/compute/submit",
                                data: {"startdate": $("#startdate").val()},
                                cache: false,
                                success: function (content) {
                                    message_box.show(content,"success");
                                },
                                error: function (content) {
                                    message_box.show(content,"error");
                                }
                            });
                        }
                    });
        });

        $("#ml2").click(function () {
            $.ajax({
                type: "get",
                async: false,
                url: "${ctx}/app/compute/list"
            }).done(function (msg) {
                $('#s2').html(msg);
            });
        });
    });

    function saveCallback(data) {
        $("#compute_content").html(data);
    }
</script>

<div class="jarviswidget well">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body">
            <hr class="simple">
            <ul class="nav nav-tabs bordered" id="mtab">
                <li class="active" id="ml1">
                    <a data-toggle="tab" href="#s1"><i class="fa fa-fw fa-lg fa-list-alt"></i>数据计算</a>
                </li>
                <li id="ml2"><a data-toggle="tab" href="#s2"><i
                        class="fa fa-fw fa-lg fa-gear"></i>提交数据列表</a>
                </li>
            </ul>

            <div class="tab-content padding-10">
                <div id="s1" class="tab-pane fade in active">
                    <form action="${ctx}/app/compute/doIt" method="post" id="computeForm" class="form-inline"
                          role="form">
                        <input type="hidden" value="${survey}" name="survey"/>
                        <fieldset style="top: 3px">
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-calendar"></i>
                                    <input type="text" id="startdate" placeholder="数据年份"
                                           class="form-control" name="startdate" required value="${date}">
                                </div>
                            </div>
                            <a href="javascript:void(0);" class="btn btn-primary" id="compute">
                                <i class="fa fa-fire"></i> 计算
                            </a>
                            <a href="javascript:void(0);" class="btn btn-primary" id="submit">
                                <i class="fa fa-angle-up"></i> 数据上报
                            </a>
                        </fieldset>
                    </form>
                    <hr class="simple">
                    <div id="compute_content"></div>
                </div>
                <div id="s2" class="tab-pane fade"></div>
            </div>
        </div>
    </div>
</div>
</div>