var prefix = $("#prefix").html();

$(document).ready(function () {

    $('#bizcontainer').jstree({
        "core": {
            "animation": 2,
            "check_callback": true,
            "themes": {
                "variant": "large",
                "dots": true
            },
            'data': {
                'url': prefix + '/sys/biz/treeData',
                'data': function (node) {
                    return {
                        'id': node.id
                    };
                }
            }
        },
        "contextmenu": {
            'items': contextMenu
        },
        "plugins": [ "contextmenu", "search", "sort", "dnd",
            "unique" ]
    }).on("create_node.jstree", function (e, data) {
        $("#save_type").val("INSERT");
        $("#newCreateId").val(data.node.id);
        $('#bizcontainer').jstree(true).deselect_node(
            data.node.parent);
        $('#bizcontainer').jstree(true).select_node(
            data.node.id);
    }).on("move_node.jstree", function (e, data) {
        $.ajax({
            type: 'post',
            dataType: "json",
            url: prefix + '/sys/biz/move',
            data: {
                '_old_parent': data.old_parent,
                '_new_parent': data.node.parent,
                '_moved_id': data.node.id
            },
            cache: false,
            success: function (content) {
                message_box.show(content.message, content.code);
            },
            error: function (content) {
                message_box.show(content.message, content.code);
            }
        });
    }).on("delete_node.jstree", function (e, data) {

    }).on("select_node.jstree", function (e, data) {

        if (data.node.id == 'top') {
            if (!$("#bizd").hasClass("disabled")) {
                $("#bizd").addClass("disabled");
            }
            $("bizdef").hide();
        } else {
            $("#bizd").removeClass("disabled");
        }

        var createId = $("#newCreateId").val();
        if (createId != "" && data.node.id != createId) {
            alert("请先保存新建权限!");
            jstree.select_node(createId);
            jstree.deselect_node(data.node.id);
            return;
        }

        $("#sysBizPermission_permissionId").val("");
        $("#sysBizPermission_pid").val("");
        $("#sysBizPermission_name").val();
        $("#sysBizPermission_description").val("");
        $("#sysBizPermission_permissionId").val(data.node.id);
        $("#sysBizPermission_pid").val(data.node.parent);
        var text = data.node.text;
        var start = text.indexOf("<bb>");
        if (start != -1) {
            var end = text.indexOf("</bb>");
            text = text.substring(start + 4, end);
        }
        $("#sysBizPermission_name").val(text);
        var save_type = $("#save_type").val();
        if (data.node.id != 'top') {

            if (save_type == "" || save_type != "INSERT") {
                $.getJSON(prefix + '/sys/biz/detail?bizId=' + data.node.id, function (result) {
                    if (result != null) {

                        if (result.type == "0") {
                            $("#bizdef").hide();
                        } else {
                            $("#bizdef").show();
                        }

                        $("#sysBizPermission_description").val(result.description);
                        $("#sysBizPermission_permission_def").val(result.permission_def);
                        $("input:radio[name='sysBizPermission.type']:nth(" + result.type + ")").prop("checked", true);
                        $("input:radio[name='sysBizPermission.show']:nth(" + result.show + ")").prop("checked", true);
                    }
                });
                $("#save_type").val("UPDATE");
            }
        }
        var ptext = $('#bizcontainer').jstree(true).get_text(
            data.node.parent);
        if (ptext == false) {
            $("#btnSubmit").hide();
        } else {
            var start = ptext.indexOf("<bb>");
            if (start != -1) {
                var end = ptext.indexOf("</bb>");
                ptext = ptext.substring(start + 4, end);
            }
            $("#btnSubmit").show();
            $("#sysBizPermission_pname").val(ptext);
        }
    });

    $("#biza").click(function () {

        var selected = jstree.get_selected(true);
        if (selected.length == 0 || selected.length > 1) {
            alert("请选择上级分类");
            return;
        }
        var newId = jstree.create_node(selected[0], {'text': '新数据权限'}, "last");
        $("#newCreateId").val(newId);
    });

    $("#bizd").click(function () {

        var selected = jstree.get_selected(true);

        if (selected.length == 0 || selected.length > 1) {
            alert("请选择一个权限");
            return;
        }

        if ($("#save_type").val() == "INSERT") {
            jstree.delete_node(selected);
            $("#save_type").val("");
            $("#newCreateId").val("")
            return;
        }

        var id = selected[0].id
        $.ajax({
            type: 'post',
            dataType: "json",
            url: prefix + '/sys/biz/delete',
            data: {id: id},
            cache: false,
            success: function (content) {
                if (content.code == "success") {
                    message_box.show(content.message, content.code);
                    jstree.delete_node(selected);
                } else {
                    var ids = content.message.split(",");
                    var mm = "";
                    for (i in ids) {
                        mm += jstree.get_node(ids[i]).text;
                    }
                    message_box.show("无法删除" + mm, content.code);
                }
            },
            error: function (content) {
                message_box.show(content.message, content.code);
            }
        });
    });

    function gcreate(title, url, width, height) {
        gDialog.fCreate({
            title: title,
            url: url,
            width: width || 880,
            height: height || 400
        }).show();
    }

    $("#permissionDef").click(function () {
        var id = $("#sysBizPermission_permissionId").val();
        HWTX.gDialogCreate("数据权限表达式", prefix + "/sys/biz/showDef?id=" + id, {}, 1000, 400);
    });
});

var jstree = $('#bizcontainer').jstree(true);

function saveCallback(json) {
    if (json.code == 'success') {
        message_box.show(json.message, json.code);
        jstree.set_text(json.id, json.name);
        if (json.type == '1') {
            jstree.hide_icon(json.id);
        } else if (json.type == '0') {
            jstree.show_icon(json.id);
        }
    } else {
        message_box.show(json.message, json.code);
        jstree.delete_node($("#newCreateId").val());
    }
    $("#newCreateId").val("");
    return true;
}

function bizSubmit(form) {

    var pid = $("#sysBizPermission_pid").val();
    var p = jstree.get_icon(pid);
    if (!p) {
        alert("保存失败,父节点类型必须是分类");
        jstree.delete_node($("#newCreateId").val());
        $("#newCreateId").val("");
        return false;
    }

    return ajaxSubmit(form, 'json', saveCallback);
}

function contextMenu() {
    return {
        "create": {
            "separator_before": false,
            "separator_after": true,
            "_disabled": false, // (this.check("create_node",
            // data.reference, {}, "last")),
            "label": "Create",
            "action": function (data) {
                var inst = $.jstree.reference(data.reference), obj = inst
                    .get_node(data.reference);
                inst.create_node(obj, {}, "last", function (new_node) {
                    setTimeout(function () {
                        inst.edit(new_node);
                    }, 0);
                });
            }
        },
        "ccp": {
            "separator_before": true,
            "icon": false,
            "separator_after": false,
            "label": "Edit",
            "action": false,
            "submenu": {
                "cut": {
                    "separator_before": false,
                    "separator_after": false,
                    "label": "Cut",
                    "action": function (data) {
                        var inst = $.jstree.reference(data.reference), obj = inst
                            .get_node(data.reference);
                        if (inst.is_selected(obj)) {
                            inst.cut(inst.get_selected());
                        } else {
                            inst.cut(obj);
                        }
                    }
                },
                "copy": {
                    "separator_before": false,
                    "icon": false,
                    "separator_after": false,
                    "label": "Copy",
                    "action": function (data) {
                        var inst = $.jstree.reference(data.reference), obj = inst
                            .get_node(data.reference);
                        if (inst.is_selected(obj)) {
                            inst.copy(inst.get_selected());
                        } else {
                            inst.copy(obj);
                        }
                    }
                },
                "paste": {
                    "separator_before": false,
                    "icon": false,
                    "_disabled": function (data) {
                        return !$.jstree.reference(data.reference).can_paste();
                    },
                    "separator_after": false,
                    "label": "Paste",
                    "action": function (data) {
                        var inst = $.jstree.reference(data.reference), obj = inst
                            .get_node(data.reference);
                        inst.paste(obj);
                    }
                }
            }
        }
    };
}