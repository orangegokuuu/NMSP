<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html ng-app="MQSAC">
<head>
<base href="<%=request.getContextPath()%>" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>MQSAC</title>
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
<link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/menu.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/bootstrap-submenu.css"/>" />

<script language="javascript">
var context = "<%=request.getContextPath()%>";
</script>

<script src="<c:url value="/webjars/angularjs/angular.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-route.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-resource.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-animate.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-aria.js"/>"></script>
<script src="<c:url value="/webjars/angularjs/angular-messages.js"/>"></script>

<script src="<c:url value="/webjars/angular-material/angular-material.js"/>"></script>
<script src="<c:url value="/webjars/angular-ui-router/angular-ui-router.js"/>"></script>
<script src="<c:url value="/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate/angular-translate.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-loader-url/angular-translate-loader-url.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-storage-local/angular-translate-storage-local.js"/>"></script>
<script src="<c:url value="/webjars/angular-http-auth/http-auth-interceptor.js"/>"></script>
<script src="<c:url value="/webjars/angular-loading-bar/loading-bar.js"/>"></script>
<script src="<c:url value="/webjars/json-formatter/dist/json-formatter.min.js"/>"></script>
<script src="<c:url value="/webjars/ag-grid/dist/ag-grid.js"/>"></script>
<script src="<c:url value="/webjars/alasql/dist/alasql.min.js"/>"></script>
<script src="<c:url value="/webjars/chartjs/Chart.js"/>"></script>
<script src="<c:url value="/webjars/angular-chart.js/angular-chart.js"/>"></script>
<script src="<c:url value="/webjars/momentjs/moment.js"/>"></script>

<script src="<c:url value="/webjars/jquery/jquery.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap/js/bootstrap.js"/>"></script>

<script src="<c:url value="/webjars/metisMenu/metisMenu.min.js"/>"></script>

<script src="<c:url value="/js/lib/ag-grid-addons.js"/>"></script>
<script src="<c:url value="/js/lib/JSOG.js"/>"></script>

<script src="<c:url value="/js/app.js"/>"></script>
<script src="<c:url value="/js/services.js"/>"></script> 

<script src="<c:url value="/js/controller/MainCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/MenuCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/MqCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/AuthCtrl.js"/>"></script> 


<script src="<c:url value="/js/main.js"/>"></script>

</head>
<body ng-controller="MainCtrl" ng-cloak translate-cloak layout="column">
	<md-toolbar layout="row" layout-padding layout-align="start center">
		<h2>MQ System Admin Console</h2>
		<span flex></span> 
	    <a href="<c:url value="/logout"/>">
           <i class="fa fa-sign-out fa-2x" aria-hidden="true"></i>
	       <md-tooltip>Logout</md-tooltip>
        </a>
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
                          <a class="has-arrow" ui-sref="<c:url value="main"/>" ui-sref-active="active">
                              <i class="fa fa-bookmark fa-fw" aria-hidden="true"></i><span class="lpad10">New SMS Platform</span>
                          </a>
                          <a ui-sref="<c:url value="sada"/>" ui-sref-active="active">
                              <i class="fa fa-user-circle fa-fw" aria-hidden="true"></i><span class="lpad10">Add/Delete Source Address</span>
                          </a>
                          <a ui-sref="<c:url value="waterlevel"/>" ui-sref-active="active">
                              <i class="fa fa-hourglass-half fa-fw" aria-hidden="true"></i><span class="lpad10">Change WaterLevel</span>
                          </a>
                      </li>
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
            <p class="text-muted">Copyright © WiseSpot Company Limited</p>
        </div>
        <div ng-click="showFooterMenu()" layout="row" layout-align="end center">
            <i class="fa fa-phone fa-fw text-muted"></i><div layout-align="end center" class="text-muted" hide-sm hide-xs>Contact Us</div>
        </div>
      </div>
    </footer>
</body>
</html>