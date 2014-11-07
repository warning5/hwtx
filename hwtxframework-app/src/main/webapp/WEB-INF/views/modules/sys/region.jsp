<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<script>
    $(document).ready(function () {
        initUI();
        if ($.fn.select2) {
            $('.select2').each(function() {
                var $this = $(this);
                var width = $this.attr('data-select-width') || '100%';
                $this.select2({
                    placeholder: "选择区域",
                    allowClear : true,
                    width : width
                })
            })
        }
    });
</script>
<style>
    .col-select {
        box-sizing: border-box;
        float: left;
        min-height: 1px;
    }
</style>
<div id="wid-id-o3" class="jarviswidget well">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body">
            <hr class="simple">
            <h4>全部区域:</h4>
            <form class="smart-form">
                <div class="row">
                    <section class="col-select col-5" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" ref="w_combox_city" refUrl="${ctx}/region/city"
                                    ajaxUrl="${ctx}/region/province" multiple>
                                <option value="">请选择</option>
                            </select> <i></i>
                        </label>
                    </section>
                    <section class="col-select col-6" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" id="w_combox_city" ref="w_combox_area"
                                    refUrl="${ctx}/region/area" multiple>
                                <option value="">所有城市</option>
                            </select> <i></i>
                        </label>
                    </section>
                </div>
                <div class="row">
                    <section class="col-select col-6" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" id="w_combox_area" multiple>
                                <option value="">所有区县</option>
                            </select> <i></i>
                        </label>
                    </section>
                </div>
            </form>
            <hr class="simple">
            <h4>已选择区域:</h4>
            <form class="smart-form">
                <div class="row">
                    <section class="col-select col-3" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" ref="w_combox_city_1" refUrl="${ctx}/region/city"
                                    ajaxUrl="${ctx}/region/province">
                                <option value="">请选择</option>
                            </select> <i></i>
                        </label>
                    </section>
                    <section class="col-select col-3" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" id="w_combox_city_1" ref="w_combox_area_1"
                                    refUrl="${ctx}/region/area">
                                <option value="">所有城市</option>
                            </select> <i></i>
                        </label>
                    </section>
                    <section class="col-select col-3" style="margin-left: 15px">
                        <label class="control-label"></label>
                        <label class="select">
                            <select class="combox select2" id="w_combox_area_1">
                                <option value="">所有区县</option>
                            </select> <i></i>
                        </label>
                    </section>
                </div>
            </form>
        </div>
    </div>
</div>