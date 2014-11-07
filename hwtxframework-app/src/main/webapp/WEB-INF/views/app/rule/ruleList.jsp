<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/jstree.jsp" %>
<style>
    .smart-form footer {
        background: none;
    }
</style>
<script>
    $(document).ready(function () {
        $('#ruleContainer').jstree({
            "core": {
                "animation": 2,
                "check_callback": true,
                "themes": {
                    "variant": "large",
                    "dots": true
                },
                'data': {
                    'url': '${ctx}/app/rule/treeData',
                    'data': function (node) {
                        var nodeType = "#";
                        if (typeof(node.li_attr) != "undefined") {
                            if (typeof(node.li_attr.type) != "undefined") {
                                nodeType = node.li_attr.type;
                            }
                        }
                        return {
                            'id': node.id,
                            'nodeType': nodeType
                        };
                    }
                }
            },
            "sort": function (a, b) {
                return this.get_node(a).li_attr.squence > this.get_node(b).li_attr.squence ? 1 : -1;
            },
            "plugins": [ "search", "sort", "dnd", "unique" ]
        });

        var jstree = $('#ruleContainer').jstree(false);

        $("#formula").click(function () {

            var selected = jstree.get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择一个节点");
                return;
            }

            $.get('${ctx}/app/rule/formula?id=' + selected[0].id + "&type=" + selected[0].li_attr.type, function
                    (data) {
                $('#right').html(data);
                $("#right").show();
            });
        });

        $('#ruleContainer').on("dblclick.jstree", ".jstree-anchor", $.proxy(function (e) {
            e.preventDefault();
            $(e.currentTarget).focus();
            obj = jstree.get_node(e.currentTarget);
            if (obj.id == "-1") {
                alert("不能编辑根节点.");
                return;
            }

            if (obj.li_attr.type == "defCat") {
                HWTX.gDialogCreate("编辑分类", '${ctx}/app/rule/showEditCat', {"id": obj.id}, 600, 400);
            } else if (obj.li_attr.type == "defClass") {
                HWTX.gDialogCreate("编辑项目", '${ctx}/app/rule/showEditClass', {"id": obj.id}, 600, 400);
            } else if (obj.li_attr.type == "defKpi") {
                HWTX.gDialogCreate("编辑指标", '${ctx}/app/rule/showEditKpi', {"id": obj.id}, 600, 400);
            }
        }, this));

        $("#rulea").click(function () {

            var selected = jstree.get_selected(true);
            if (selected.length == 0 || selected.length > 1) {
                alert("请选择上级");
                return;
            }
            if (selected[0].id == "-1") {
                HWTX.gDialogCreate("添加分类", '${ctx}/app/rule/showAddCat', {}, 600, 400);
            } else if (selected[0].li_attr.type == "defCat") {
                HWTX.gDialogCreate("添加项目", '${ctx}/app/rule/showAddClass', {"id": selected[0].id}, 600, 400);
            } else if (selected[0].li_attr.type == "defClass" ||
                    (selected[0].li_attr.type == "defKpi" && selected[0].li_attr.complex == "1")) {
                HWTX.gDialogCreate("添加指标", '${ctx}/app/rule/showAddKpi',
                        {"id": selected[0].id, "complex": selected[0].li_attr.complex}, 600, 400);
            } else {
                alert("无法添加子节点");
            }
        });
    });

</script>
<div data-widget-sortable="false" data-widget-custombutton="false"
     data-widget-fullscreenbutton="false" data-widget-deletebutton="false"
     data-widget-togglebutton="false" data-widget-editbutton="false"
     data-widget-colorbutton="false" id="wid-id-o3" class="jarviswidget well">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body">
            <a href="javascript:void(0);" class="btn btn-primary" id="rulea">
                <i class="fa fa-plus"></i> 添加
            </a>
            <a href="javascript:void(0);" class="btn btn-primary" id="ruled">
                <i class="fa fa-minus"></i> 删除
            </a>
            <a href="javascript:void(0);" class="btn btn-primary" id="formula">
                <i class="fa fa-fire"></i> 计算公式
            </a>
            <hr class="simple">
            <div class="tab-content padding-10">
                <div class="row">
                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6 orgboard u-tree">
                        <div id="ruleContainer"></div>
                    </div>
                    <div class="col-xs-12 col-sm-6 col-md-6 col-lg-6" id="right" style="display: none">
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>