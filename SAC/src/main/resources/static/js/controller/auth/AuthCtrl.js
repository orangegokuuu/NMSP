// Login page
SAC.controller('AuthCtrl', function ($scope, $rootScope, $window, $state, AuthService) {
	$scope.credentials = { username: '', password: '' };
	if ($rootScope.userSession != null) {
		$scope.credentials.username = $rootScope.userSession.userId;
	}

	//	console.log($rootScope.pwdConfig);
	if (!$rootScope.pwdConfig) {
		AuthService.getPwdConfig().then(function (response) {
			$rootScope.pwdConfig = response.data;
			//			console.log($rootScope.pwdConfig);
		});
	}


	$scope.login = function (credentials) {
		console.log("credentials = " + credentials);
		AuthService.login(credentials).then(function (status) {
			console.log("Login return " + status);
			if (status == 200) {
				console.log($window.location.href);
				if ($window.location.href.indexOf('login') > 0) {
					$window.location.href = context + '/home';
				} else {
					location.reload();
				}
			} else if (status == 408) {
				$state.go('password');
				$scope.passwordError = "auth.login.err." + status;
			} else {
				$scope.loginError = "auth.login.err." + status;
			}
		});
	};

	$scope.logout = function () {
		console.log("Logout now");
		$window.location.href = context + '/logout';
	}

	$scope.updatePassword = function (credentials) {
		AuthService.updatePassword(credentials).then(function (response) {
			if (response.status==200) {
				if ($window.location.href.indexOf('login') > 0) {
					$window.location.href = context + '/home';
				}
			}else {
				$scope.passwordError = response.data.error;
			}
		});
	};



});