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
    $(document).ready(function () {
        $("#inputForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#inputForm"), 'json', saveCallback);
            }
        });

        $("#btnsb").click(function () {
            $("#submit").val("true");
            $("#inputForm").submit();
        });
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
            <form action="${ctx}/app/financialData/back" method="post" id="inputForm" class="smart-form">
                <input type="hidden" value="${type}" name="type"/>
                <input type="hidden" value="${region}" name="appDataCheck.checkdRegion"/>
                <input type="hidden" value="${date}" name="date"/>
                <input type="hidden" value="${statistic}" name="statistic"/>
                <input type="hidden" value="${orgId}" name="appDataCheck.checkedOrgId"/>
                <fieldset>
                    <div class="row">
                        <section class="col">
                            <label class="control-label">原因:</label>
                            <label class="textarea">
                                <textarea rows="5" cols="180" class="custom-scroll"
                                          name="appDataCheck.checkNotes" maxlength="255"
                                          required>${checkNotes}</textarea>
                            </label>
                        </section>
                    </div>
                </fieldset>
                <footer>
                    <c:if test="${empty checkNotes}">
                        <input id="btnSubmit" class="btn btn-primary" type="submit"
                               value="退 回"/>&nbsp;
                    </c:if>
                    <input id="btngoback" class="btn btn-primary" type="button"
                           value="取 消" onclick="HWTX.gDialogClose()"/>
                </footer>
            </form>
        </div>
    </div>
</div>