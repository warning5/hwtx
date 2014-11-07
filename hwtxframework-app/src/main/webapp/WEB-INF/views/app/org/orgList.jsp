<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@include file="/WEB-INF/views/include/jstree.jsp" %>
<style>
    .smart-form footer {
        background: none;
    }

    .u-tree {
        /*border-right: 1px solid #DDDDDD !important;*/
        float: left;
      /*  height: 350px;*/
        overflow-y: auto;
        position: relative;
    }

    .u-text {
        /*border-right: 1px solid #DDDDDD !important;*/
        float: left;
       /* height: 326px;*/
        overflow-y: auto;
        position: relative;
        margin-left: 20px;
    }
</style>

<div id="prefix" class="hide">${ctx}</div>
<script src="/static/resource/js/appOrg.js"></script>
<div data-widget-sortable="false" data-widget-custombutton="false"
     data-widget-fullscreenbutton="false" data-widget-deletebutton="false"
     data-widget-togglebutton="false" data-widget-editbutton="false"
     data-widget-colorbutton="false" id="wid-id-o3" class="jarviswidget well">
    <div>
        <div class="jarviswidget-editbox"></div>
        <div class="widget-body">
            <a href="javascript:void(0);" class="btn btn-primary" id="orga"><i class="fa fa-plus"></i> 添加</a>
            <a href="javascript:void(0);" class="btn btn-primary" id="orgd"><i class="fa fa-minus"></i> 删除</a>
            <a href="javascript:void(0);" class="btn btn-primary" id="usera"><i class="fa fa-user"></i> 分配用户</a>
            <a href="javascript:void(0);" class="btn btn-primary" id="rolea"><i class="fa fa-magic"></i> 分配角色</a>
            <hr class="simple">
            <div class="tab-content padding-10" id="myTabContent1">
                <input type="hidden" id="newCreateId" />
                <div class="row">
                    <div class="col-xs-12 col-sm-4 col-md-4 col-lg-4 u-tree">
                        <div id="orgcontainer"></div>
                    </div>
                    <div class="col-xs-12 col-sm-7 col-md-7 col-lg-6 u-text" id="appOrgContent">

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>