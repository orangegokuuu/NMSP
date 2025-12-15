MQSAC.factory('InitService', function($http) {
	var initService = {};

	initService.getMqmName = function(){                 
		var contextPath = context +'/getMqmName';
		return $http.get(contextPath).then(function(response) {
			return JSOG.decode(response.data);
		}, function(response) {
			return response.data;
		});
	}
	
	initService.getSacPath = function(){                 
		var contextPath = context +'/getSacPath';
		return $http.get(contextPath).then(function(response) {
			return JSOG.decode(response.data);
		}, function(response) {
			return response.data;
		});
	}
	return initService;
});

/**
 * Authentication Handling
 */
MQSAC.factory('AuthService', function($http, authService) {
	var sacAuthService = {};

	sacAuthService.login = function(credentials) {
		return $http.post(context + '/login', credentials).then(function(response) {
			authService.loginConfirmed();
			return response.status;
		}, function(response) {
			return response.status;
		});
	};

	sacAuthService.updatePassword = function(credentials) {
		return $http.post(context + '/password', credentials).then(function(response) {
			authService.loginConfirmed();
			return response;
		}, function(response) {
			return response;
		});
	};
	
	sacAuthService.getSession = function() {
		return $http.get(context + '/session').then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return JSOG.decode(response);
		});
	};
	
	sacAuthService.expireSession = function() {
		return $http.get(context + '/expired').then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return JSOG.decode(response);
		});
	};

	return sacAuthService;
});


MQSAC.factory('MqService', function($http) {
	var mqService = {};
 	
	mqService.createOsUser = function(managerName, cpId) {
		var contextPath = context +'/createOsUser/'+managerName +'/'+cpId;
		return $http.post(contextPath, cpId).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	
	mqService.deleteOsUser = function(managerName, cpId) {
		var contextPath = context +'/deleteOsUser/'+managerName +'/'+cpId;
		return $http.post(contextPath, cpId).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	
	mqService.createMQ = function(managerName, cpId) {
		var contextPath = context +'/createQueue/'+managerName +'/'+cpId;
		return $http.post(contextPath, cpId).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	
	mqService.deleteMQ = function(managerName, cpId) {
		var contextPath = context +'/deleteQueue/'+managerName +'/'+cpId;
		return $http.post(contextPath, cpId).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	return mqService;
});


MQSAC.factory('CpService', function($http) {
	var sacAuthHeader = { 'AgentSignature': 'WYm1Qsy1sdw' };
	var cpService = {};
	
	//cp
//	cpService.pageCP = function(searchPage) {
//		var contextPath = sacRootContext + '/sms/cp/page';
//		return $http.post(contextPath, { headers: sacAuthHeader }, searchPage).then(function(response) {
//			return JSOG.decode(response.data);
//		}, function(response) {
//			return JSOG.decode(response.data);
//		});
//	};
	
 	cpService.getCP = function(sacRootContext, cpId) {
		var contextPath = sacRootContext + '/sms/cp/subconsole/getCp/'+ cpId + '/';
		console.log(contextPath);
		return $http.get(contextPath, { headers: sacAuthHeader }).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	
	cpService.updateCP = function(sacRootContext, cp) {
		var contextPath = sacRootContext + '/sms/cp/subconsole/updateCp/';
		return $http.put(contextPath, cp, { headers: sacAuthHeader }).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	
	cpService.createCP = function(sacRootContext, mqCp) {
		var contextPath = sacRootContext + '/sms/cp/subconsole/createCp/';
		return $http.post(contextPath, mqCp, { headers: sacAuthHeader }).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return response;
		});
	}
	  
	return cpService;
});


