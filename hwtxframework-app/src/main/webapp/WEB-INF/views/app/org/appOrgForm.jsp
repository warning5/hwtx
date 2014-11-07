<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<script>
    $(document).ready(function () {
        $("#appPbcInfoForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#appPbcInfoForm"), 'json', saveCallback);
            }
        });
        $("#checkUser").click(function () {

            if ($("#newCreateId").val()) {
                alert("请保存新建机构");
                return;
            }

            var selected = $('#orgcontainer').jstree(true).get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择一个机构");
                return;
            }
            HWTX.gDialogCreate("用户列表", prefix + "/sys/user/lookup?includeUser=1", {"orgId": selected[0].id});
        });
    });
</script>
<form id="appPbcInfoForm" method="post" action="${ctx}/app/org/save" class="smart-form">
    <fieldset>
        <input type="hidden" name="sysOrg.orgId" id="sysOrg_orgId" value="${appPbcInfo.sysOrg.orgId}"/>
        <input type="hidden" name="sysOrg.pid" id="sysOrg_pid" value="${appPbcInfo.sysOrg.pid}"/>
        <input type="hidden" name="appPbcInfo.id" id="appPbcInfo_id" value="${appPbcInfo.id}"/>

        <div class="row">
            <section class="col col-6">
                <label class="control-label">上级机构:</label>
                <label class="input state-disabled">
                    <input type="text" id="appPbcInfo_pname" disabled value="${appPbcInfo.pname}"/>
                </label>
            </section>
            <section class="col col-6">
                <label class="control-label">名称:</label> <label class="input">
                <input type="text" name="sysOrg.name" maxlength="50"
                       class="required" id="sysOrg_name" required value="${appPbcInfo.sysOrg.name}"/>
            </label>
            </section>
        </div>
        <div class="row">
            <section class="col">
                <tags:region provinces="${provinces}" cities="${cities}" areas="${areas}"
                             province_name="appPbcInfo.province" city_name="appPbcInfo.city"
                             area_name="appPbcInfo.area"
                             province_value="${appPbcInfo.province}"
                             city_value="${appPbcInfo.city}"
                             area_value="${appPbcInfo.area}"
                             province_required="false"
                             area_required="false"
                             city_required="false"/>
            </section>
        </div>
        <div class="row">
            <section class="col col-6">
                <label class="control-label">描述:</label> <label class="input">
                <input type="text" name="sysOrg.description" maxlength="50"
                       id="sysOrg_description" value="${appPbcInfo.sysOrg.description}"/>
            </label>
            </section>
            <section class="col col-6">
                <label class="control-label">已分配用户:</label>
                <label class="input">
                    <a href="#" class="btn btn-success btn-sm" id="checkUser"><i
                            class="fa fa-user"></i> 查看</a>
                </label>
            </section>
        </div>
    </fieldset>
    <footer>
        <input id="btnSubmit" class="btn btn-primary" type="submit"
               value="保存"/>
    </footer>
</form>