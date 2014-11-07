var prefix = $("#prefix").html();

function delAction(action) {
    var url = $(action).attr("url");
    var tr = $(action).parent().parent();
    var trs = []
    trs.push(tr);
    del(url, trs);
}

function editAction(action) {
    var url = $(action).attr("url");
    HWTX.gDialogCreate('修改用户', url, {}, 400, 204);
}

function del(url, trs) {
    var oTable = $('#userTable').dataTable();
    gDialog.fConfirm('确认执行', '你确定执行这个操作么？',
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
                            HWTX.refreshTable($('#userTable'));
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

function highlightRow() {
    $("#userTable tbody tr").click(function (e) {
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        } else {
            $(this).addClass('row_selected');
        }
    });
}

$(document).ready(function () {

    initTablePipeline();
    $('#userTable').dataTable({
        "bProcessing": true,
        "bServerSide": true,
        "bFilter": false,
        "sPaginationType": "bootstrap_full",
        "sAjaxSource": prefix + "/sys/user/data",
        "sServerMethod": "POST",
        "iDisplayLength": 25,
        "aLengthMenu": [10, 25, 50, 100],
        "fnServerData": fnDataTablesPipeline,
        "fnServerParams": function (aoData) {
            aoData.push({"name": "searchSysUser.userName", "value": $("#userName").val()});
            aoData.push({"name": "searchSysUser.startDate", "value": $("#startdate").val()});
            aoData.push({"name": "searchSysUser.finishDate", "value": $("#finishdate").val()});
        },
        "fnDrawCallback": function (oSettings) {
            highlightRow();
        },
        "aoColumns": [
            { "mData": "num", "bSortable": false },
            { "mData": "userId"},
            { "mData": "name"},
            { "mData": "login_ip"},
            { "mData": "login_date"},
            { "mData": "actions", "bSortable": false,
                "mRender": function (data, type, full) {
                    return '<a onclick="editAction(this)" href="#" url="' + prefix + '/sys/user/showEdit?id=' + full.userId + '">修改</a>&nbsp;&nbsp;' +
                        '<a onclick="delAction(this)" href="#" url="' + prefix + '/sys/user/d?ids=' + full.userId + '">删除</a>';
                }
            }
        ]
    });

    document.onkeydown = function (e) {
        var ev = document.all ? window.event : e;
        if (ev.keyCode == 13) {
            $("#userSubmit").click();
        }
    }

    $("#userSubmit").click(function () {
        HWTX.refreshTable($('#userTable'));
    });

    $('#startdate').datetimepicker({
        format: 'yyyy-mm-dd hh:ii:ss',
        autoclose: true,
        todayBtn: true,
        language: "zh-CN"
    });

    $('#finishdate').datetimepicker({
        format: 'yyyy-mm-dd hh:ii:ss',
        autoclose: true,
        todayBtn: true,
        language: "zh-CN"
    });

    $("#clearstart").click(function () {
        $("#startdate").val("");
    });
    $("#clearend").click(function () {
        $("#finishdate").val("");
    });

    $("#startdate").on("", function (e) {
        $('#finishdate').data("DateTimePicker").setMinDate(e.date);
    });

    $('#startdate').datetimepicker().on("changeDate", function (e) {
        $('#finishdate').datetimepicker("setStartDate", e.date);
    });

    $("#finishdate").datetimepicker().on("changeDate", function (e) {
        $('#startdate').datetimepicker("setEndDate", e.date);
    });

    $("#delBat").click(function () {
        var ids = new Array();
        var trs = new Array();

        var length = $("#userTable tbody tr.row_selected").length;
        if (length == 0) {
            alert("请选择一个用户");
            return;
        }

        $("#userTable tbody tr.row_selected").each(function () {
            ids.push($(this).children('td').eq(1).html());
            trs.push($(this));
        });
        del(prefix + "/sys/user/d?ids=" + ids.toString(), trs);
    });

    $("#usera").click(function () {
        HWTX.gDialogCreate('添加用户', prefix + '/sys/user/showAdd', {}, 400, 204);
    });

    $("#userm").click(function () {
        var id = getSelectedTr();
        if (id != "") {
            HWTX.gDialogCreate('修改用户', prefix + '/sys/user/showEdit?id=' + id, {}, 400, 204);
        }
    });

    $("#userAssignRole").click(function () {
        var id = getSelectedTr();
        if (id != "") {
            HWTX.gDialogCreate('分配角色', prefix + '/sys/user/showAssignRole?id=' + id, {}, 550, 350);
        }
    });
});

function getSelectedTr() {
    var length = $("#userTable tbody tr.row_selected").length;
    if (length == 0 || length > 1) {
        alert("请选择一个用户");
        return "";
    }
    return $("#userTable tbody tr.row_selected").eq(0).children('td').eq(1).html();
}