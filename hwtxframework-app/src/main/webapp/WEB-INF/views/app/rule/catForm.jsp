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
            if($('#ruleContainer').jstree().is_loaded("#")){
                $('#ruleContainer').jstree().load_node("#");
            }
        } else {
            message_box.show(json.message, 'error');
        }
        return true;
    }
    $(document).ready(function () {
        $("#inputForm").validate({
            submitHandler: function() {
                ajaxSubmit($("#inputForm"),'json',saveCallback);
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
            <form action="${ctx}/app/rule/saveCat" method="post" id="inputForm" class="smart-form">
                <input type="hidden" name="defCat.dcatId" value="${defCat.dcatId}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">类别名称:</label>
                            <label class="input">
                                <input type="text" name="defCat.dcatName" required
                                       maxlength="100" value="${defCat.dcatName}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">分值:</label>
                            <label class="input">
                                <input type="number" name="defCat.dcatScore" required value="${defCat.dcatScore}"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">说明:</label>
                            <label class="input">
                                <input type="text" name="defCat.dcatRemark" maxlength="100" value="${defCat.dcatRemark}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">顺序:</label>
                            <label class="input">
                                <input type="number" name="defCat.dcatSquence" required value="${defCat.dcatSquence}"/>
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