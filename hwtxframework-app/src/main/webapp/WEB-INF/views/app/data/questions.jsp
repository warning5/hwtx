<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    $(document).ready(function () {
    });
</script>
<style>
    .stretchbox {
        margin-left: -30px;
        margin-right: -30px;
    }

    .out-survey {
        border-bottom: 1px dashed #c8c8c8;
        overflow: hidden;
        padding-left: 45px;
        padding-right: 48px;
    }

    .surveybox {
        color: #444;
        float: left;
        margin-right: 20px;
        padding-bottom: 35px;
        padding-top: 35px;
        width: 48%;
    }

    .surveybox dt, .surveybox dd {
        line-height: 30px;
    }

    .surveybox dt {
        font-weight: bold;
    }

    .surveybox dd {
        padding-left: 18px;
    }
</style>
<div class="wizard" id="question_wizard">
    <ul class="steps">
        <c:forEach begin="1" end="${steps}" var="step">
            <li data-target="#step${step}" <c:if test="${step==1}">class="active"</c:if>>
                <span class="badge badge-info">${step}</span>第${step}页<span class="chevron"></span>
            </li>
        </c:forEach>
    </ul>
    <div class="actions">
        <button type="button" class="btn btn-sm btn-primary btn-prev">
            <i class="fa fa-arrow-left"></i>上一页
        </button>
        <button id="next" type="button" class="btn btn-sm btn-success btn-next" data-last="完成">
            下一页<i class="fa fa-arrow-right"></i>
        </button>
    </div>
</div>
<div class="step-content">
    <form class="smart-form" id="fuelux_wizard" method="post">
        <input type="hidden" name="surveyId" value="${surveyId}"/>
        <c:set var="count" value="1"/>
        <c:forEach var="question" items="${questions}" varStatus="status">
            <c:if test="${status.count%2!=0}">
                <div class="step-pane <c:if test="${count==1}">active</c:if>" id="step${count}">
                <c:set value="${count+1}" var="count"/>
                <fieldset>
                <div class="stretchbox">
                <div class="out-survey" <c:if test="${steps==(count-1)}">last="1"</c:if>>
            </c:if>
            <dl class="surveybox">
                <dt>${status.count}、${question.questionDesc}</dt>
                <dd>
                    <ul class="list-surev">
                        <section>
                            <c:forEach items="${question.options}" var="option">
                                <label class="radio">
                                    <input type="radio" name="${question.questionId}" value="${option.key}">
                                    <i></i>${option.value}
                                </label>
                            </c:forEach>
                        </section>
                    </ul>
                </dd>
            </dl>
            <c:if test="${status.count%2==0}">
                </div>
                </div>
                </fieldset>
                </div>
            </c:if>
        </c:forEach>
    </form>
</div>

<!-- end widget content -->

<script src="/static/resource/js/plugin/fuelux/wizard/wizard.js"></script>
<script>
    $(document).ready(function () {

        var wizard = $('.wizard').wizard();

        wizard.on('finished', function (e, data) {
            var length = $("input:radio:checked").length;
            if (length <${fn:length(questions)}) {
                alert("请填写完成后提交!");
                return;
            }
            $.post("${ctx}/app/survey/submit", $("#fuelux_wizard").serialize(), function () {
            }, "json")
                    .done(function (data) {
                        if (data.code == 'error') {
                            message_box.show(data.message, data.code);
                            return;
                        }
                        $("#next").prop("disabled", "disabled");
                        var count = $("#" +${surveyId}).html();
                        $("#" +${surveyId}).html((count - 0) + 1);
                        $.smallBox({
                            title: "完成",
                            content: "<i class='fa fa-clock-o'></i> <i>答卷完成</i>",
                            color: "#5F895F",
                            iconSmall: "fa fa-check bounce animated",
                            timeout: 3000
                        });
                    }).fail(function (jqxhr) {
                        message_box.show(jqxhr.responseJSON.message, 'error');
                    });
        });

        $(".surveybox .radio").click(function () {
                    var parent = $(this).parents(".out-survey")
                    var outradio = parent.find("input:radio:checked").length;
                    if (outradio == 2) {
                        if (!parent.attr("last")) {
                            $("#question_wizard").wizard('next');
                        }
                    }
                }
        )
    })
</script>