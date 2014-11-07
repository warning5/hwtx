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
            HWTX.refreshTable($("#pbcOrgTable"));
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
            <form action="${ctx}/app/pbc/save" method="post" id="inputForm" class="smart-form">
                <input type="hidden" name="appPbcInfo.id" value="${appPbcInfo.id}"/>
                <input type="hidden" name="appPbcInfo.orgId" value="${appPbcInfo.orgId}"/>
                <input type="hidden" name="roleId" value="${roleId}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">名称:</label>
                            <label class="input">
                                <input type="text" name="name" value="${name}" maxlength="50" required/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <tags:region provinces="${provinces}" cities="${cities}" areas="${areas}"
                                         province_name="appPbcInfo.province" city_name="appPbcInfo.city"
                                         area_name="appPbcInfo.area" province_value="${appPbcInfo.province}"
                                         city_value="${appPbcInfo.city}" area_value="${appPbcInfo.area}"
                                         province_disabled="true" city_disabled="true"/>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">电话:</label>
                            <label class="input">
                                <input type="text" name="appPbcInfo.phone"
                                       value="${appPbcInfo.phone}" maxlength="11"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">地址:</label>
                            <label class="input">
                                <input type="text" name="appPbcInfo.address"
                                       value="${appPbcInfo.address}" maxlength="50"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">联系人:</label>
                            <label class="input">
                                <input type="text" name="appPbcInfo.contact"
                                       value="${appPbcInfo.contact}" maxlength="20"/>
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