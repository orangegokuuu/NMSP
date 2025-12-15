<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html ng-app="SAC">
<head>
<base href="<%=request.getContextPath()%>" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>SAC</title>
<link rel="apple-touch-icon" sizes="180x180" href="<c:url value="/favicon/apple-touch-icon.png"/>">
<link rel="icon" type="image/png" href="<c:url value="/favicon/favicon-32x32.png"/>" sizes="32x32">
<link rel="icon" type="image/png" href="<c:url value="/favicon/favicon-16x16.png"/>" sizes="16x16">
<link rel="manifest" href="<c:url value="/favicon/manifest.json"/>">
<link rel="mask-icon" href="<c:url value="/favicon/safari-pinned-tab.svg"/>" color="#5bbad5">
<meta name="theme-color" content="#ffffff">

<link rel="stylesheet" href="<c:url value="/webjars/angular-material/angular-material.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/angular-loading-bar/loading-bar.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/font-awesome/css/font-awesome.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/css/bootstrap.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/metisMenu/metisMenu.css"/>"/>
<link rel="stylesheet" href="<c:url value="/webjars/ag-grid/dist/styles/ag-grid.css"/>"/>
<link rel="stylesheet" href="<c:url value="/webjars/ag-grid/dist/styles/theme-fresh.css"/>"/>
<link rel="stylesheet" href="<c:url value="/webjars/json-formatter/dist/json-formatter.min.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/flag-icon-css/css/flag-icon.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/textAngular/textAngular.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/menu.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/bootstrap-submenu.css"/>" />

<script language="javascript">
var context = "<%=request.getContextPath()%>";
</script>

<script src="<c:url value="/webjars/angularjs/angular.min.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-route.min.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-resource.min.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-animate.min.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-aria.min.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-messages.min.js"/>"></script>

<script src="<c:url value="/webjars/angular-sanitize/angular-sanitize.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-material/angular-material.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-ui-router/angular-ui-router.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate/angular-translate.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-loader-url/angular-translate-loader-url.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-storage-local/angular-translate-storage-local.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-http-auth/http-auth-interceptor.js"/>"></script>
<script src="<c:url value="/webjars/angular-loading-bar/loading-bar.min.js"/>"></script>
<script src="<c:url value="/webjars/json-formatter/dist/json-formatter.min.js"/>"></script>
<script src="<c:url value="/webjars/ag-grid/dist/ag-grid.min.js"/>"></script>
<script src="<c:url value="/webjars/textAngular/textAngular.min.js"/>"></script>
<script src="<c:url value="/webjars/textAngular/textAngular-rangy.min.js"/>"></script>
<script src="<c:url value="/webjars/textAngular/textAngular-sanitize.min.js"/>"></script>
<script src="<c:url value="/webjars/textAngular/textAngularSetup.js"/>"></script>
<script src="<c:url value="/webjars/alasql/dist/alasql.min.js"/>"></script>
<script src="<c:url value="/webjars/chartjs/Chart.min.js"/>"></script>
<script src="<c:url value="/webjars/angular-chart.js/angular-chart.min.js"/>"></script>
<script src="<c:url value="/webjars/momentjs/min/moment.min.js"/>"></script>

<script src="<c:url value="/webjars/jquery/jquery.min.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap/js/bootstrap.min.js"/>"></script>

<script src="<c:url value="/webjars/metisMenu/metisMenu.min.js"/>"></script>

<script src="<c:url value="/js/lib/ag-grid-addons.js"/>"></script>
<script src="<c:url value="/js/lib/JSOG.js"/>"></script>

<script src="<c:url value="/js/app.js"/>"></script>
<script src="<c:url value="/js/i18n.js"/>"></script>
<script src="<c:url value="/js/directives.js"/>"></script> 
<script src="<c:url value="/js/services.js"/>"></script> 

<script src="<c:url value="/js/controller/main/MainCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/main/MenuCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/main/DashboardCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/auth/AuthCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/user/UserCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/user/RoleCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/user/GroupCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/system/EventLogCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/user/ActionLogCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/system/ParamCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/system/TemplateCtrl.js"/>"></script> 

<script src="<c:url value="/js/controller/sms/BlackListCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/CpCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/ReportCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/RecordCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/SpamKeywordCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/TimetableCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/sms/SubConsoleCtrl.js"/>"></script> 

<script src="<c:url value="/js/main.js"/>"></script>

</head>

<body ng-controller="MainCtrl" ng-cloak translate-cloak layout="column">
	<md-toolbar layout="row" layout-padding layout-align="start center">
	   <h2 translate>console.title</h2>
	   <span flex></span> 
       <div class="dropdown">
          <md-button class="md-icon-button" data-toggle="dropdown">
            <md-tooltip>User Menu</md-tooltip>
            <i class="fa fa-user-circle fa-2x" aria-hidden="true"></i> 
          </md-button>
          <ul class="dropdown-menu dropdown-menu-right" role="menu">
            <li>
                <a ui-sref="profile">
                    <i class="fa fa-user" aria-hidden="true"></i><label class="lpad10">{{userSession.user.userName}}</label>
                </a>
            </li>
            <li class="dropdown-submenu" ng-controller="LangCtrl">
                <a>
                    <i class="fa fa-language" aria-hidden="true"></i><label class="lpad10">Language</label>
                </a>
                <ul class="dropdown-menu" role="menu">
                <!--
                    <li>
                        <a ui-sref="#" ng-click="changeLang('zh-TW')">
                            <span class="flag-icon flag-icon-hk"></span><label class="lpad10">中文</label>
                        </a>
                    </li>
                -->
                    <li>
                        <a ui-sref="#" ng-click="changeLang('en')">
                            <span class="flag-icon flag-icon-us"></span><label class="lpad10">English</label>
                        </a>
                    </li>
                </ul>
            </li>
             <li role="separator" class="divider"></li>
            <li>
                <a href="<c:url value="/logout"/>">
                    <i class="fa fa-close" aria-hidden="true"></i><label class="lpad10">Logout</label>
                </a>
            </li>
          </ul>
       </div> 
	   <md-button class="md-icon-button" ng-click="mainMenu()">
         <md-tooltip>Main Menu</md-tooltip>
	     <i class="fa fa-bars fa-2x" aria-hidden="true"></i> 
	   </md-button> 
	</md-toolbar>
	<div flex layout="row">
		<md-sidenav flex layout-fill md-component-id='left' hide-print md-is-locked-open="lockLeft && $mdMedia('gt-sm')"> 
    		<md-content ng-controller="MenuCtrl">
              <nav class="sidebar-nav">
                  <ul class="metismenu" id="main-menu">
                  <li>
                    <a ui-sref="main" ui-sref-active="active">
                      <i class="fa fa-home fa-fw" aria-hidden="true"></i><span class="lpad10" translate>menu.home</span>
                    </a>
                  </li>
                  <c:forEach items="${menuItems}" var="item">
                      <li>
                          <a class="has-arrow" href="<c:url value="#"/>" aria-expanded="false">
                              <i class="fa <c:out value="${item.icon}"/> fa-fw" aria-hidden="true"></i><span class="lpad10" translate><c:out value="PT.${item.code}"/></span>
                          </a>
                          <ul aria-expanded="false">
                          <c:forEach items="${item.child}" var="c">
                              <li>
                                  <a ui-sref="<c:url value="${c.url}"/>" ui-sref-active="active">
                                      <i class="fa <c:out value="${c.icon}"/> fa-fw" aria-hidden="true"></i><span class="lpad10" translate><c:out value="P.${c.code}"/></span>
                                  </a>
                              </li>
                          </c:forEach>
                          </ul>
                      </li>
                  </c:forEach>
                  </ul>
              </nav>
    	    </md-content> 
	    </md-sidenav>
		<md-content flex layout="column">
            <div id="loading-bar-container"></div>
            <ui-view/>
        </md-content>
	</div>
    <footer id="footer" class="footer">
      <div layout="row" layout-align="space-between">
        <div>
            <p class="text-muted" translate>msg.copyright</p>
        </div>
        <div ng-click="showFooterMenu()" layout="row" layout-align="end center">
            <i class="fa fa-phone fa-fw text-muted"></i><div layout-align="end center" class="text-muted" hide-sm hide-xs>Contact Us</div>
        </div>
      </div>
    </footer>
</body>
</html>