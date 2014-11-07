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
        $("#pbcInputForm").validate({
            submitHandler: function () {
                ajaxSubmit($("#pbcInputForm"), 'json', saveCallback);
            }
        });

        $("#sb").click(function () {
            $("#pbcInputForm").submit();
        })
    });

    function saveCallback(json) {
        if (json.code == 'success') {
            message_box.show(json.message, 'success');
            HWTX.gDialogClose();
            HWTX.refreshTable($("#fuserTable"));
        } else {
            message_box.show(json.message, 'error');
        }
        return true;
    }
</script>
<div class="modal-body" style="margin-left: 20px">
    <div class="jarviswidget" id="wid-id-8" data-widget-editbutton="false"
         data-widget-custombutton="false">
        <div>
            <div class="jarviswidget-editbox">
                <!-- This area used as dropdown edit box -->
            </div>
            <!-- end widget edit box -->

            <!-- widget content -->
            <div class="widget-body no-padding">
                <form action="${ctx}/app/pbc/saveUser" method="post" id="pbcInputForm"
                      class="smart-form">
                    <input type="hidden" name="sysUser.userId" value="${sysUser.userId}"/>

                    <div class="row">
                        <section class="col col-10">
                            <label class="control-label">用户名:</label> <label class="input">
                            <input type="text" name="sysUser.name" value="${sysUser.name}"
                                   maxlength="50" minlength="3" required/>
                        </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-10">
                            <label class="control-label">密码:</label> <label class="input">
                            <input type="password" name="sysUser.pwd" maxlength="15" minlength="3"
                                   <c:if test="${empty sysUser.userId}">required</c:if>/>
                        </label>
                            <c:if test="${!empty sysUser.userId}">
                                <div class="note">若不修改密码，请留空</div>
                            </c:if>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-10">
                            <label class="control-label">机构</label>
                            <label class="select">
                                <select name="userOrg">
                                    <c:if test="${orgs == null }">
                                        <option value="">所有机构</option>
                                    </c:if>
                                    <c:forEach items="${orgs}" var="org">
                                        <c:choose>
                                            <c:when test="${org.orgId!=null && org.orgId eq userOrg}">
                                                <option value="${org.orgId}" selected="selected">${org.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${org.orgId}">${org.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select> <i></i>
                            </label>
                        </section>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<div class="modal-footer">
    <a href="#" class="btn" data-dismiss="modal" onclick="HWTX.gDialogClose()">关闭</a>
    <a href="#" class="btn btn-primary" id="sb">保存</a>
</div>