<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>MQSAC - Error 401</title>
<link rel='shortcut icon' type='image/x-icon' href="<c:url value="/favicon.ico"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/angular-material/angular-material.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/font-awesome/css/font-awesome.css"/>" />
<link rel="stylesheet" href="<c:url value="/webjars/bootstrap/css/bootstrap.css"/>" />
<link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
</head>
<body>
  <div class="well">
    <div class="vertical-center">
      <div class="container" style="width:250px">
        <div class="panel panel-danger">
          <div class="panel-heading">Error - ${detail.status}</div>
          <div class="panel-body" style="min-height : 200px">
            <span>${detail.message}</span>
          </div>
          <div class="panel-footer">
            <a href="<c:url value="/login"/>">Go back to Login</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
