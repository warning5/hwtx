<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/datetimepicker.jsp" %>
<style>
    .row_selected {
        background-color: #B0BED9;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        pageSetUp();
    });
</script>
<script src="/static/resource/js/ulist.js"></script>
<div id="prefix" class="hide">${ctx}</div>
<section id="widget-grid">
    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0"
         data-widget-editbutton="false">
        <header>
            <span class="widget-icon"> <i class="fa fa-table"></i> </span>
            <h2>用户列表</h2>
        </header>
        <div>
            <!-- widget edit box -->
            <div class="jarviswidget-editbox">
                <!-- This area used as dropdown edit box -->

            </div>
            <!-- end widget edit box -->

            <!-- widget content -->
            <div class="widget-body no-padding">
                <div class="widget-body-toolbar">
                    <form action="${ctx}/sys/user/" method="post" id="userForm"
                          onsubmit="return false" class="form-inline" role="form">
                        <fieldset style="top: 3px">
                            <div class="btn-group">
                                <button class="btn btn-default dropdown-toggle"
                                        data-toggle="dropdown">
                                    操作<span class="caret"></span>
                                </button>
                                <ul class="dropdown-menu" style="min-width: 59px">
                                    <li><a id="usera" href="#"><i class="fa fa-plus"></i> 添加</a></li>
                                    <li><a id="userm" href="#"><i class="fa fa-maxcdn"></i> 修改</a></li>
                                    <li><a id="delBat" href="#"><i class="fa fa-minus"></i> 删除</a></li>
                                    <li class="divider"></li>
                                    <li><a id="userAssignRole" href="#"><i class="fa fa-magnet"></i> 分配角色</a></li>
                                </ul>
                            </div>
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-user"></i> <input type="text" placeholder="用户名"
                                                                      id="userName" class="form-control"
                                                                      name="searchSysUser.userName">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-times" style="cursor: pointer;" id="clearstart">
                                        <i class="fa fa-calendar"></i>
                                    </i> <input type="text" data-format="yyyy-MM-dd HH:mm:ss"
                                                name="searchSysUser.startDate" id="startdate"
                                                placeholder="开始时间" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-times" style="cursor: pointer;" id="clearend">
                                        <i class="fa fa-calendar"></i>
                                    </i> <input type="text" data-format="yyyy-MM-dd" name="
									searchSysUser.finishDate" id="finishdate" placeholder="结束时间"
                                                class="form-control">
                                </div>
                            </div>
                            <button class="btn btn-primary" id="userSubmit">查询</button>
                        </fieldset>
                    </form>
                </div>
                <table cellpadding="0" cellspacing="0" border="0"
                       class="table table-bordered table-hover" id="userTable">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>UserId</th>
                        <th>Name</th>
                        <th>Login_Ip</th>
                        <th>Login_Date</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>