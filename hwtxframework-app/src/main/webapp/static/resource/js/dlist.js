var prefix = $("#prefix").html();

function ajaxInvoke(url) {

    var ids = new Array();

    $(":checkbox:checked").each(function () {
        ids.push($(this).attr("vv"));
    });
    $.ajax({
        type: 'post',
        dataType: "json",
        url: url,
        data: {ids: ids.toString()},
        cache: false,
        success: function (content) {
            HWTX.refreshTable($('#dictTable'));
            message_box.show(content.message, content.code);
        },
        error: function (content) {
            message_box.show(content.message, content.code);
        }
    });
}

$(document).ready(function () {

    initTablePipeline();
    $('#dictTable').dataTable({
        "bProcessing": true,
        "bServerSide": true,
        "bFilter": false,
        "sPaginationType": "bootstrap_full",
        "sAjaxSource": prefix + "/sys/dict/data",
        "sServerMethod": "POST",
        "iDisplayLength": 20,
        "aLengthMenu": [20,50,100],
        "fnServerData": fnDataTablesPipeline,
        "fnServerParams": function (aoData) {
            aoData.push({"name": "searchDict.types", "value": $("#types").val()});
            aoData.push({"name": "searchDict.label", "value": $("#label").val()});
        },
        "aoColumns": [
            { "mData": "id", "bSortable": false,
                "mRender": function (data, type, full) {

                    return    '<div class=\"smart-form\">' +
                        '<label class=\"checkbox\">' +
                        '<input type=\"checkbox\" vv=\"' + full.id + '\"/><i></i>' +
                        '</label>' +
                        '</div>'
                }
            },
            { "mData": "num", "bSortable": false},
            { "mData": "label"},
            { "mData": "value"},
            { "mData": "type"},
            { "mData": "actions", "bSortable": false,
                "mRender": function (data, type, full) {
                    return '<a onclick="editAction(this)" href="#" url="' + prefix + '/sys/dict/form?id=' + full.id + '">修改</a>&nbsp;&nbsp;' +
                        '<a onclick="delAction(this)" href="#" url="' + prefix + '/sys/dict/delete?ids=' + full.id + '">删除</a>';
                }
            }
        ]
    });

    document.onkeydown = function (e) {
        var ev = document.all ? window.event : e;
        if (ev.keyCode == 13) {
            $("#dictSubmit").click();
        }
    }

    $("#checkAll").click(function () {
        if ($("#checkAll").is(":checked")) {
            $(":checkbox").not("#checkAll").prop("checked", "checked");
        } else {
            $(":checkbox").not("#checkAll").prop("checked", "");
        }
    });

    $("#dictSubmit").click(function () {
        HWTX.refreshTable($('#dictTable'));
    });

    $("#dictd").click(function () {
        ajaxInvoke(prefix + "/sys/dict/delete");
    });

    $("#dicta").click(function () {
        HWTX.gDialogCreate('添加词典', prefix + '/sys/dict/form', {}, 510, 320);
    });
    $("#clearLabel").click(function () {
        $("#label").val("");
    });

    $("#dictm").click(function () {
        var id = getSelectedTr();
        if (id != "") {
            HWTX.gDialogCreate('修改词典', prefix + '/sys/dict/form?id=' + id, {}, 510, 320);
        }
    });
});

function editAction(tr) {
    var url = $(tr).attr("url");
    HWTX.gDialogCreate('修改词典', url, {}, 510, 320);
}

function delAction(tr) {
    var url = $(tr).attr("url");
    ajaxInvoke(url);
}

function getSelectedTr() {
    var length = $(":checkbox:checked").length;
    if (length == 0 || length > 1) {
        alert("请选择一个项目");
        return "";
    }
    return $(":checkbox:checked").attr("vv");
}