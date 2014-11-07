<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@include file="/WEB-INF/views/include/validator.jsp" %>
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
        $("#inputForm").validator();
    });
</script>

<div class="jarviswidget" id="wid-id-8" data-widget-editbutton="false"
     data-widget-custombutton="false">
    <!-- widget options:
                    usage: <div class="jarviswidget" id="wid-id-0" data-widget-editbutton="false">

                    data-widget-colorbutton="false"
                    data-widget-editbutton="false"
                    data-widget-togglebutton="false"
                    data-widget-deletebutton="false"
                    data-widget-fullscreenbutton="false"
                    data-widget-custombutton="false"
                    data-widget-collapsed="true"
                    data-widget-sortable="false"

                -->
    <div>
        <div class="jarviswidget-editbox">
            <!-- This area used as dropdown edit box -->
        </div>
        <!-- end widget edit box -->

        <!-- widget content -->
        <div class="widget-body no-padding">
            <form action="${ctx}/sys/menu/save" method="post" id="inputForm"
                  class="smart-form" onsubmit="return ajaxSubmit(this)">
                <input type="hidden" name="sysFunctionPermission.permissionId" value="${menu.id}"/>
                <fieldset>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">上级菜单:</label>
                            <label class="input">
                                <tags:treeselect id="menu" name="sysFunctionPermission.pid"
                                                 value="${menu.parent.id}" labelName="parent.name"
                                                 labelValue="${menu.parent.name}" title="菜单" url="/sys/menu/treeData"
                                                 extId="${menu.id}"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">名称:</label>
                            <label class="input">
                                <input type="text" name="sysFunctionPermission.name"
                                       value="${menu.name}" maxlength="50" required/>
                            </label>

                            <div class="help-block with-errors"></div>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">链接:</label>
                            <label class="input">
                                <input type="text" name="sysFunctionPermission.url"
                                       value="${menu.url}" maxlength="200" required/>
                            </label>

                            <div class="help-block with-errors"></div>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">目标:</label>
                            <label class="input">
                                <input type="text" name="sysFunctionPermission.target"
                                       value="${menu.target}" maxlength="10"/>
                            </label>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-6">
                            <label class="control-label">描述:</label>
                            <label class="input">
                                <input type="text" name="sysFunctionPermission.description"
                                       value="${menu.description}" maxlength="50"/>
                            </label>
                        </section>
                        <section class="col col-6">
                            <label class="control-label">排序:</label>
                            <label class="input">
                                <input type="text" name="sysFunctionPermission.sequence"
                                       value="${menu.sequence}" required/>
                            </label>

                            <div class="help-block with-errors"></div>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col col-3">
                            <label class="control-label">图标:</label>
                            <label class="input">
                                <tags:iconselect id="icon" name="sysFunctionPermission.icon"
                                                 value="${menu.icon}"></tags:iconselect>
                            </label>
                        </section>
                        <section class="col col-3">
                            <label class="control-label">可见:</label>

                            <div class="inline-group">
                                <label class="radio">
                                    <input id="show" type="radio"
                                           name="sysFunctionPermission.enable" value="1"
                                           <c:if test="${menu.enable eq 1}">checked="checked"</c:if> required>
                                    <i></i>显示
                                </label>
                                <label class="radio">
                                    <input id="hide" type="radio"
                                           name="sysFunctionPermission.enable" value="0"
                                           <c:if test="${menu.enable eq 0}">checked="checked"</c:if> required>
                                    <i></i>隐藏
                                </label>
                            </div>
                        </section>
                    </div>
                </fieldset>
                <footer>
                    <input id="btnSubmit" class="btn btn-primary" type="submit"
                           value="保 存"/>&nbsp; <input id="btngoback" class="btn btn-primary" type="button"
                                                      value="返 回" onclick="javascript:backTo('${ctx}/sys/menu/')"/>
                </footer>
            </form>
        </div>
    </div>
</div>