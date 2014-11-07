<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>HwTx admin</title>
    <meta content="" name="description">
    <meta content="" name="author">

    <link href="/static/resource/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" media="screen" href="/static/resource/css/font-awesome.min.css">

    <link rel="stylesheet" type="text/css" media="screen" href="/static/resource/css/smartadmin-production.css">
    <link rel="stylesheet" type="text/css" media="screen" href="/static/resource/css/smartadmin-skins.css">

    <!-- Demo purpose only: goes with demo.js, you can delete this css when designing your own WebApp -->
    <link rel="stylesheet" type="text/css" media="screen" href="/static/resource/css/demo.css">

    <!-- FAVICONS -->
    <link rel="shortcut icon" href="/static/resource/img/favicon/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/resource/img/favicon/favicon.ico" type="image/x-icon">

    <script src="/static/jquery/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="/static/bootstrap/3.1.1/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/static/resource/js/ui/dwz.ui.js"></script>
    <script src="/static/resource/js/hwtx.js"></script>
    <script src="/static/resource/js/ui/dwz.combox.js"></script>
    <%@ include file="/WEB-INF/views/include/jquery_validator.jsp" %>
    <%@ include file="/WEB-INF/views/include/jshow.jsp" %>
    <%@ include file="/WEB-INF/views/include/table.jsp" %>
    <%@ include file="/WEB-INF/views/include/select2.jsp" %>
</head>
<body class="">
<!-- possible classes: minified, fixed-ribbon, fixed-header, fixed-width-->

<!-- HEADER -->
<div id="meu_all_url" class="hide">${ctx}/sys/menu/all</div>
<header id="header">
    <div id="logo-group">

        <!-- PLACE YOUR LOGO HERE -->
        <span id="logo"> <img src="../static/resource/img/logo.png" alt="SmartAdmin"> </span>
        <!-- END LOGO PLACEHOLDER -->
    </div>

    <!-- pulled right: nav area -->
    <div class="pull-right">

        <!-- collapse menu button -->
        <div id="hide-menu" class="btn-header pull-right">
            <span> <a href="javascript:void(0);" title="Collapse Menu"><i class="fa fa-bars"></i></a> </span>
        </div>
        <!-- end collapse menu -->

        <!-- logout button -->
        <div id="logout" class="btn-header transparent pull-right">
            <span> <a href="${ctx}/logout" title="Sign Out"><i class="fa fa-sign-out"></i></a> </span>
        </div>
        <!-- end logout button -->

        <!-- search mobile button (this is hidden till mobile view port) -->
        <div id="search-mobile" class="btn-header transparent pull-right">
            <span> <a href="javascript:void(0)" title="Search"><i class="fa fa-search"></i></a> </span>
        </div>
        <!-- end search mobile button -->

        <!-- input: search field -->
        <form action="#search.html" class="header-search pull-right">
            <input type="text" placeholder="Find reports and more" id="search-fld">
            <button type="submit">
                <i class="fa fa-search"></i>
            </button>
            <a href="javascript:void(0);" id="cancel-search-js" title="Cancel Search"><i class="fa fa-times"></i></a>
        </form>
        <!-- end input: search field -->

        <!-- multiple lang dropdown : find all flags in the image folder -->
        <%-- 		<ul class="header-dropdown-list hidden-xs">
                    <li>
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown"> <img alt="" src="/static/resource/img/flags/us.png"> <span> US </span> <i class="fa fa-angle-down"></i> </a>
                        <ul class="dropdown-menu pull-right">
                            <li class="active">
                                <a href="javascript:void(0);"><img alt="" src="/static/resource/img/flags/us.png"> US</a>
                            </li>
                            <li>
                                <a href="javascript:void(0);"><img alt="" src="/static/resource/img/flags/es.png"> Spanish</a>
                            </li>
                            <li>
                                <a href="javascript:void(0);"><img alt="" src="/static/resource/img/flags/de.png"> German</a>
                            </li>
                        </ul>
                    </li>
                </ul> --%>
        <!-- end multiple lang -->

    </div>
    <!-- end pulled right: nav area -->

</header>
<!-- END HEADER -->

<!-- Left panel : Navigation area -->
<!-- Note: This width of the aside area can be adjusted through LESS variables -->
<aside id="left-panel">

    <!-- User info -->
    <div class="login-info">
				<span> <!-- User image size is adjusted inside CSS, it should stay as it --> 
					
					<a href="javascript:void(0);" id="show-shortcut">
                        <img src="/static/resource/img/avatars/sunny.png" alt="me" class="online"/>
						<span>
                            ${userName}
                        </span>
                        <i class="fa fa-angle-down"></i>
                    </a>
					
				</span>
    </div>
    <!-- end user info -->

    <!-- NAVIGATION : This navigation is also responsive

    To make this navigation dynamic please make sure to link the node
    (the reference to the nav > ul) after page load. Or the navigation
    will not initialize.
    -->
    <nav>
        <ul id="menu"></ul>
    </nav>
    <span class="minifyme"> <i class="fa fa-arrow-circle-left hit"></i> </span>

</aside>
<!-- END NAVIGATION -->

<!-- MAIN PANEL -->
<div id="main" role="main">

    <!-- RIBBON -->
    <div id="ribbon">
        <span class="ribbon-button-alignment">
            <span id="refresh" class="btn btn-ribbon" data-title="refresh" rel="tooltip" data-placement="bottom"
                  data-original-title="<i class='text-warning fa fa-warning'></i> Warning! This will reset all your widget settings."
                  data-html="true"><i class="fa fa-refresh"></i>
            </span>
        </span>
        <!-- breadcrumb -->
        <ol class="breadcrumb">
            <li>主页</li>
        </ol>
        <!-- end breadcrumb -->

        <!-- You can also add more buttons to the
        ribbon for further usability

        Example below:

        <span class="ribbon-button-alignment pull-right">
        <span id="search" class="btn btn-ribbon hidden-xs" data-title="search"><i class="fa-grid"></i> Change Grid</span>
        <span id="add" class="btn btn-ribbon hidden-xs" data-title="add"><i class="fa-plus"></i> Add</span>
        <span id="search" class="btn btn-ribbon" data-title="search"><i class="fa-search"></i> <span class="hidden-mobile">Search</span></span>
        </span> -->

    </div>
    <!-- END RIBBON -->
    <div id="content">
    </div>

</div>
<!-- END MAIN PANEL -->
<script src="/static/resource/js/notification/SmartNotification.min.js"></script>
<script src="/static/resource/js/smartwidgets/jarvis.widget.min.js"></script>
<script src="/static/resource/js/app.js"></script>
<script src="/static/resource/js/demo.js"></script>
<script>
    $(document).ready(function () {
        pageSetUp();
        $("nav li a").each(function () {
            var url = $(this).attr("url");
            var sa = $(this);
            $(this).click(function () {
                if (url != "#") {
                    $.ajax({
                        type: 'get',
                        dataType: "text",
                        url: url,
                        cache: false,
                        success: function (content) {
                            $("#content").html(content);
                        }
                    });
                }

                if (url != "#") {
                    $('nav li.active').removeClass("active");
                    $('nav li:has(a[url="' + url + '"])').addClass("active");
                    var title = ($('nav a[url="' + url + '"]').attr('title'))
                    document.title = (title || document.title);
                }

                var menu = new Array();
                sa.parents("li").each(function () {
                    menu.push($(this).children("a").attr("title"));
                });

                mb = "<li>主页</li>";
                menu = menu.reverse();
                for (i in menu) {
                    mb += "<li>" + menu[i] + "</li>";
                }
                $(".breadcrumb").html(mb);
            });
        })
    });
</script>
</body>
</html>