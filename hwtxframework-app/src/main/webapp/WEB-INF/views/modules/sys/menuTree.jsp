<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>菜单导航</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$(".accordion-heading a").click(function() {
			$('.accordion-toggle i').removeClass('icon-chevron-down');
			$('.accordion-toggle i').addClass('icon-chevron-right');
			if (!$($(this).attr('href')).hasClass('in')) {
				$(this).children('i').removeClass('icon-chevron-right');
				$(this).children('i').addClass('icon-chevron-down');
			}
		});
		$(".accordion-body a").click(function() {
			$("#menu li").removeClass("active");
			$("#menu li i").removeClass("icon-white");
			$(this).parent().addClass("active");
			$(this).children("i").addClass("icon-white");
		});
		$(".accordion-body a:first i").click();
	});
</script>
</head>
<body>
	<div class="accordion" id="menu">
		<c:set var="firstMenu" value="true" />
		<c:forEach items="${menu_List}" var="menu" varStatus="idxStatus">
			<c:if test="${menu.show eq '1'}">
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse"
							data-parent="#menu" href="#collapse${menu.id}"
							title="${menu.description}"><i
							class="icon-chevron-${firstMenu?'down':'right'}"></i>&nbsp;${menu.name}</a>
					</div>
					<div id="collapse${menu.id}"
						class="accordion-body collapse ${firstMenu?'in':''}">
						<div class="accordion-inner">
							<ul class="nav nav-list">
								<c:forEach items="${menu.children}" var="menuChild">
									<c:if
										test="${menuChild.show eq '1'}">
										<li><a
											href="${fn:indexOf(menuChild.url, '://') eq -1?ctx:''}${not empty menuChild.url?menuChild.url:'/404'}"
											target="${not empty menuChild.target?menuChild.target:'mainFrame'}"><i
												class="icon-${not empty menuChild.icon?menuChild.icon:'circle-arrow-right'}"></i>&nbsp;${menuChild.name}</a></li>
										<c:set var="firstMenu" value="false" />
									</c:if>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
			</c:if>
		</c:forEach>
	</div>
</body>
</html>
