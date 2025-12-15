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
<link rel="stylesheet" href="<c:url value="/webjars/json-formatter/dist/json-formatter.min.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
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
<script src="<c:url value="/webjars/angular-translate/angular-translate.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-loader-url/angular-translate-loader-url.js"/>"></script>
<script src="<c:url value="/webjars/angular-translate-storage-local/angular-translate-storage-local.js"/>"></script>
<script src="<c:url value="/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.js"/>"></script>
<script src="<c:url value="/webjars/angular-http-auth/http-auth-interceptor.js"/>"></script>
<script src="<c:url value="/webjars/angular-loading-bar/loading-bar.js"/>"></script>
<script src="<c:url value="/webjars/json-formatter/dist/json-formatter.min.js"/>"></script>
<script src="<c:url value="/webjars/ag-grid/dist/ag-grid.js"/>"></script>
<script src="<c:url value="/webjars/chartjs/Chart.js"/>"></script>
<script src="<c:url value="/webjars/angular-chart.js/angular-chart.js"/>"></script>

<script src="<c:url value="/webjars/jquery/jquery.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap/js/bootstrap.js"/>"></script>
<script src="<c:url value="/webjars/metisMenu/metisMenu.min.js"/>"></script>

<script src="<c:url value="/js/lib/JSOG.js"/>"></script>

<script src="<c:url value="/js/app.js"/>"></script> 
<script src="<c:url value="/js/login.js"/>"></script> 
<script src="<c:url value="/js/directives.js"/>"></script> 
<script src="<c:url value="/js/services.js"/>"></script> 

<script src="<c:url value="/js/controller/MainCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/MqCtrl.js"/>"></script> 
<script src="<c:url value="/js/controller/AuthCtrl.js"/>"></script> 


</head>
<body ng-controller="AuthCtrl" ng-cloak layout="column" >
<md-card flex="100" layout-align="center center" >
  <md-card-content layout="row">
      <ui-view/>
  </md-card-content>  
</md-card>    
</body>
</html>
