// Login page
MQSAC.controller('AuthCtrl', function($scope, $rootScope, $window, $state, AuthService) {
	$scope.credentials = { username : '', password : '' };
	if ($rootScope.userSession != null) {
		$scope.credentials.username = $rootScope.userSession.userId;
	}
	
	$scope.login = function(credentials) {
		console.log("credentials = " + credentials);
		AuthService.login(credentials).then(function(status) {
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
				$scope.passwordError = "Password expired";
			} else if (status == 500) {
				$scope.loginError = "Internal Error. Please Contact System Admin.";
			} else if (status == 404){
				$scope.loginError = "User Not Found";
			} else if (status == 406){
				$scope.loginError = "Incorrect Password";
			} else if (status == 423){
				$scope.loginError = "Account suspended";
			} else {
				$scope.loginError = "Unknown Error: " + status;
			}
		});
	};

	$scope.logout = function() {
		console.log("Logout now");
		$window.location.href = context + '/logout';
	}
	
//	$scope.updatePassword = function(credentials) {
//		AuthService.updatePassword(credentials).then(function(response) {
////			console.log(response);
////			console.log("Update Password return " + response.status);
//			if (response.status == 200) {
//				if ($window.location.href.indexOf('login') > 0) {
//					$window.location.href = context + '/home';
//				}
//			} else {
////				console.log(response);
//				$scope.passwordError = response.data.message;
//			}
//		});
//	};
//	


});