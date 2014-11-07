<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en-us">
<head>
    <title>${fns:getConfig('productName')}登录</title>

    <link href="/static/bootstrap/3.1.1/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" media="screen"
          href="/static/resource/css/font-awesome.min.css">

    <link rel="stylesheet" type="text/css" media="screen"
          href="/static/resource/css/smartadmin-production.css">
    <link rel="stylesheet" type="text/css" media="screen"
          href="/static/resource/css/smartadmin-skins.css">

    <link rel="shortcut icon" href="/static/resource/img/favicon/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/resource/img/favicon/favicon.ico" type="image/x-icon">

    <script src="/static/jquery/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script src="/static/bootstrap/3.1.1/js/bootstrap.min.js" type="text/javascript"></script>
    <%@ include file="/WEB-INF/views/include/jquery_validator.jsp" %>
</head>
<body id="login" class="animated fadeInDown">
<header id="header">
    <div id="logo-group">
        <span id="logo"> <img src="/static/resource/img/logo.png" alt="HwTx"> </span>
    </div>
</header>
<div id="main" role="main">
    <div id="content" class="container">
        <div class="page-header">
            <%
                String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
            %>
            <div class="alert alert-danger alert-dismissable <%=error == null ? "hide" : ""%>">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                <label id="loginError" class="error"><%=error == null ? ""
                        : "com.thinkgem.jeesite.modules.sys.security.CaptchaException".equals(error) ? "验证码错误, 请重试."
                        : "用户或密码错误, 请重试."%></label>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-5 col-lg-4" style="margin: 0 auto
                auto 400px;">
                <div class="well no-padding">
                    <form action="${ctx}/login" id="login-form" class="smart-form client-form"
                          method="post">
                        <header>登 录</header>
                        <fieldset>
                            <section>
                                <label class="label">用户名</label>
                                <label class="input">
                                    <i class="icon-append fa fa-user"></i>
                                    <input type="text" name="username">
                                    <b class="tooltip tooltip-top-right">
                                        <i class="fa fa-user txt-color-teal"></i>
                                        请输入用户名
                                    </b>
                                </label>
                            </section>

                            <section>
                                <label class="label">密码</label>
                                <label class="input">
                                    <i class="icon-append fa fa-lock"></i>
                                    <input type="password" name="password">
                                    <b class="tooltip tooltip-top-right">
                                        <i class="fa fa-lock txt-color-teal"></i>
                                        请输入密码
                                    </b>
                                </label>
                                <div class="note">
                                    <a href="forgotpassword.html">忘记密码?</a>
                                </div>
                            </section>
                            <section>
                                <label class="checkbox" for="remember" title="下次无需登录">
                                    <input type="checkbox" name="rememberMe" checked="" id="remember">
                                    <i></i>记住我
                                </label>
                            </section>
                            <c:if test="${isValidateCodeLogin}">
                                <section>
                                    <div class="validateCode">
                                        <tags:validateCode name="validateCode" inputCssStyle="margin-bottom:0;" />
                                    </div>
                                </section>
                            </c:if>
                        </fieldset>
                        <footer>
                            <button type="submit" class="btn btn-primary">
                                登 录
                            </button>
                        </footer>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<footer class="white navbar-fixed-bottom" style="text-align: center">
    Copyright &copy; 2012-${fns:getConfig('copyrightYear')} <a
        href="${pageContext.request.contextPath}${fns:getFrontPath()}">${fns:getConfig('productName')}</a>
    - Powered By <a href="#"
                    target="_blank">HwTxSite</a> ${fns:getConfig('version')}
</footer>

<script type="text/javascript">

    $(function () {
        // Validation
        $("#login-form").validate({
            // Rules for form validation
            rules: {
                username: {
                    required: true,
                    minlength: 3,
                    maxlength: 20
                },
                password: {
                    required: true,
                    minlength: 3,
                    maxlength: 20
                }
            },

            // Messages for form validation
            messages: {
                username: {
                    required: '请输入用户名'
                },
                password: {
                    required: '请输入用户密码'
                }
            },

            // Do not change code below
            errorPlacement: function (error, element) {
                error.insertAfter(element.parent());
            }
        });
    });
</script>

</body>
</html>