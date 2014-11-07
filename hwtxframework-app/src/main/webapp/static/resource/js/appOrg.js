var prefix = $("#prefix").html();

$(document).ready(
    function () {
        $('#orgcontainer').jstree(
            {
                "core": {
                    "animation": 2,
                    "check_callback": true,
                    "themes": {
                        "variant": "large",
                        "dots": true
                    },
                    'data': {
                        'url': prefix + '/sys/org/treeData',
                        'data': function (node) {
                            return {
                                "id": node.id
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
                $("#newCreateId").val(data.node.id);
                $('#orgcontainer').jstree(true).deselect_node(
                    data.node.parent);
                $('#orgcontainer').jstree(true).select_node(
                    data.node.id);
            }).on("move_node.jstree", function (e, data) {
                $.ajax({
                    type: 'post',
                    dataType: "json",
                    url: prefix + '/app/org/move',
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

                if (data.node.id == '-1') {
                    if (!$("#orgd").hasClass("disabled")) {
                        $("#orgd").addClass("disabled");
                    }
                } else {
                    $("#orgd").removeClass("disabled");
                }

                var createId = $("#newCreateId").val();
                if (createId != "" && data.node.id != createId) {
                    alert("请先保存新建机构!");
                    jstree.select_node(createId);
                    jstree.deselect_node(data.node.id);
                    return;
                }
                if (data.node.id != '-1') {
                    var text = data.node.text;
                    var start = text.indexOf("<bb>");
                    if (start != -1) {
                        var end = text.indexOf("</bb>");
                        text = text.substring(start + 4, end);
                    }

                    var orgId = data.node.id;
                    if (createId != "") {
                        orgId = "";
                    }

                    $.ajax({
                        type: "post",
                        dataType: "html",
                        url: prefix + '/app/org/detail',
                        data: {orgId: orgId,
                            type: data.node.li_attr.type,
                            pid: data.node.parent,
                            text: text}
                    }).done(function (msg) {
                        $("#appOrgContent").html(msg);
                        initUI();
                    });
                }
            });

        var jstree = $('#orgcontainer').jstree(true);

        $("#orga").click(function () {

            var selected = jstree.get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择上级机构");
                return;
            }
            var newId = jstree.create_node(selected[0], {'text': '新机构'}, "last");
            $("#newCreateId").val(newId);
        });

        $("#orgd").click(function () {

            var selected = jstree.get_selected(true);

            var ids = "";
            for (i in selected) {
                ids += selected[i].id;
                ids += ",";
            }
            $.ajax({
                type: 'post',
                dataType: "json",
                url: prefix + '/sys/org/delete',
                data: {ids: ids, type: selected[0].li_attr.type},
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

        $("#usera").click(function () {
            if ($("#newCreateId").val()) {
                alert("请保存新建机构");
                return;
            }
            var selected = jstree.get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择一个机构");
                return;
            }
            HWTX.gDialogCreate("用户列表", prefix + "/sys/user/lookup", {"orgId": selected[0].id});
        });

        $("#rolea").click(function () {

            if ($("#newCreateId").val()) {
                alert("请保存新建机构");
                return;
            }

            var selected = jstree.get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择一个机构");
                return;
            }
            HWTX.gDialogCreate('分配角色', prefix + '/sys/org/showAssignRole', {"orgId": selected[0].id}, 550, 350);
        });
    });

function saveCallback(json) {
    if (json.code == 'success') {
        message_box.show(json.message, 'success');
        /*$('#orgcontainer').jstree(true).set_text(json.id, json.name);
         if (json.type == '1') {
         $('#orgcontainer').jstree(true).hide_icon(json.id);
         } else if (json.type == '0') {
         $('#orgcontainer').jstree(true).show_icon(json.id);
         }*/
    } else {
        message_box.show(json.message, 'error');
    }
    $("#newCreateId").val("");
    return true;
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