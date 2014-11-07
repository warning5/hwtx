<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<style>
    .row_selected {
        background-color: #B0BED9;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        pageSetUp();
        initTablePipeline();

        if ($.fn.select2) {
            $('.select2').each(function () {
                var $this = $(this);
                var width = $this.attr('data-select-width') || '100%';
                $this.select2({
                    maximumSelectionSize: 2,
                    placeholder: "选择类型",
                    allowClear: true,
                    width: width
                })
            })
        }

        $('#iniDataTable').dataTable({
            "bProcessing": true,
            "bServerSide": true,
            "bFilter": false,
            "sPaginationType": "bootstrap_full",
            "sAjaxSource": "${ctx}/app/inidata/data",
            "sServerMethod": "POST",
            "iDisplayLength": 15,
            "aLengthMenu": [15, 25, 50, 100],
            "fnServerData": fnDataTablesPipeline,
            "fnServerParams": function (aoData) {
                aoData.push({"name": "defInidata.diniDataLabel", "value": $("#diniDataLabel").val()});
                aoData.push({"name": "defInidata.submitOrgRole", "value": $("#submitOrgRole").val()});
            },
            "fnDrawCallback": function (oSettings) {
//                highlightRow();
                editRow();
            },
            "aoColumns": [
                { "mData": "diniDataId", "bSortable": false, "sWidth": "5%",
                    "mRender": function (data, type, full) {

                        return    '<div class=\"smart-form\">' +
                                '<label class=\"checkbox\">' +
                                '<input type=\"checkbox\" vv=\"' + full.diniDataId + '\"/><i></i>' +
                                '</label>' +
                                '</div>'
                    }
                },
                { "mData": "diniDataLabel"},
                { "mData": "diniDataName"},
                { "mData": "showDiniDataType","sWidth": "9%"},
                { "mData": "orgRole","sWidth": "10%"},
                { "mData": "diniDataRequired", "sWidth": "7%","bVisible": false,
                    "mRender": function (data, type, full) {
                        if (full.diniDataRequired == "1") {
                            return "是";
                        } else {
                            return "否";
                        }
                    }
                },
                { "mData": "diniDataMaxLength", "sWidth": "5%","bVisible": false},
                { "mData": "diniDataMinLength", "sWidth": "5%","bVisible": false},
                { "mData": "update_time","sWidth": "11%","bVisible": false},
                { "mData": "actions", "bSortable": false,
                    "mRender": function (data, type, full) {
                        return '<a onclick="editAction(this)" href="#" url="${ctx}/app/inidata/showEditInidata?id=' +
                                full.diniDataId + '">修改</a>&nbsp;&nbsp;' +
                                '<a onclick="delAction(this)" href="#" url="${ctx}/app/inidata/deleteInidata?ids=' +
                                full.diniDataId + '">删除</a><div id=\'ini_' + full.diniDataId + '\'class=\'hide\'>' +
                                full.diniDataId + '</div>';
                    }
                }
            ]
        });

        $("#add").click(function () {
            HWTX.gDialogCreate("添加原始数据", "${ctx}/app/inidata/showAddInidata");
        });

        $("#checkAll").click(function () {
            if ($("#checkAll").is(":checked")) {
                $(":checkbox").not("#checkAll").prop("checked", "checked");
            } else {
                $(":checkbox").not("#checkAll").prop("checked", "");
            }
        });
    });

    function editAction(action) {
        var url = $(action).attr("url");
        HWTX.gDialogCreate("编辑原始数据", url);
    }

    function delAction(action) {
        var url = $(action).attr("url");
        var tr = $(action).parent().parent();
        var trs = []
        trs.push(tr);
        del(url, trs);
    }

    function del(url, trs) {
        var oTable = $('#iniDataTable').dataTable();
        gDialog.fConfirm('确认执行', '确定执行这个操作?',
                function (rs) {
                    if (rs) {
                        $.ajax({
                            type: 'post',
                            dataType: "json",
                            url: url,
                            cache: false,
                            success: function (content) {
                                if (trs != null) {
                                    for (tr in trs) {
                                        oTable.fnDeleteRow(tr, null, false);
                                    }
                                    HWTX.refreshTable($('#iniDataTable'));
                                }
                                message_box.show(content.message, content.code);
                            },
                            error: function (content) {
                                message_box.show(content.message, content.code);
                            }
                        });
                    }
                }
        );
    }
    function editRow() {
        $("#iniDataTable tbody tr").dblclick(function (e) {
            HWTX.gDialogCreate("编辑原始数据", "${ctx}/app/inidata/showEditInidata?id=" + $(this).find("div[id^='ini']").html());
        });
    }
</script>
<section id="widget-grid">
    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0"
         data-widget-editbutton="false">
        <header>
            <span class="widget-icon"> <i class="fa fa-table"></i> </span>
            <h2>原始数据列表</h2>
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
                    <form action="${ctx}/app/inidata/list" method="post" id="userForm"
                          onsubmit="return false" class="form-inline" role="form">
                        <fieldset style="top: 3px">
                            <a href="javascript:void(0);" class="btn btn-primary" id="add"><i class="fa fa-plus"></i>
                                添加</a>
                            <a href="javascript:void(0);" class="btn btn-primary" id="delete"><i
                                    class="fa fa-minus"></i>
                                删除</a>

                            <div class="form-group">
                                <div class="input-icon-right">
                                    <i class="fa fa-tint"></i>
                                    <input type="text" placeholder="名称" id="diniDataLabel" class="form-control"
                                           name="defInidata.diniDataLabel">
                                </div>
                            </div>
                            <div class="form-group">
                                <select class="select2" data-select-width="250px" id="submitOrgRole" multiple>
                                    <c:forEach items="${orgRoles}" var="orgRole">
                                        <c:choose>
                                            <c:when test="${orgRole.key!=null && orgRole.key eq defInidata.submitOrgRole}">
                                                <option value="${orgRole.key}"
                                                        selected="selected">${orgRole.value}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${orgRole.key}">${orgRole.value}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                            <button class="btn btn-primary" id="userSubmit"
                                    onclick="HWTX.refreshTable($('#iniDataTable'));">查询
                            </button>
                        </fieldset>
                    </form>
                </div>
                <table cellpadding="0" cellspacing="0" border="0"
                       class="table table-bordered table-hover" id="iniDataTable">
                    <thead>
                    <tr>
                        <th class="smart-form">
                            <label class="checkbox" style="margin-bottom: 3px">
                                <input type="checkbox" id="checkAll"><i></i>
                            </label>
                        </th>
                        <th>显示名</th>
                        <th>名称</th>
                        <th>数据类型</th>
                        <th>分类</th>
                        <th>是否必须</th>
                        <th>最大长度</th>
                        <th>最短长度</th>
                        <th>更新时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>
