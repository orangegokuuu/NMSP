MQSAC.controller('MainCtrl', function($scope, $rootScope, $mdMedia, $mdDialog, $mdBottomSheet, $state, AuthService) {
	
	$scope.lockLeft = true;
	$scope.mainMenu = function() {
		if ($mdMedia('gt-sm')) {
			$scope.lockLeft = !$scope.lockLeft;
		} else {
			$mdSidenav('left').toggle();
		}
	};
	
	var loginEvent = false;
	$scope.$on('$viewContentLoaded', function() {
		AuthService.getSession().then(function(response) {
			console.log(response);
			$rootScope.userSession = response.data;
		});
	});



	$scope.updatePassword = function(credentials) {
		credentials.username = $rootScope.userSession.userId;
//		console.log(credentials);
		AuthService.changePassword(credentials).then(function(response) {
			if (response.stauts == 200) {
				$scope.Message = "Password Updated";
				$scope.Error = false;

			} else {
				console.log(response);
				$scope.Message = false;
				$scope.Error = "Fail to Update password. Response Code : "+response.code;
				
			}
		})
	};

	$scope.showLoginForm = function() {
		$mdDialog.show({
			controller : 'AuthCtrl', templateUrl : context + "/views/auth/login.html", 
			parent : angular.element(document.body),
			scope : $scope, clickOutsideToClose : false });
	};

	$scope.$on('event:auth-loginRequired', function() {
		if (!$rootScope.loginProgress) {
			$rootScope.loginProgress = true;
			$scope.showLoginForm();
		}
	});

	$scope.$on('event:auth-loginConfirmed', function() {
		$mdDialog.cancel();
		$rootScope.loginProgress = false;
	});
	
	$scope.showFooterMenu = function() {
		$mdBottomSheet.show({
            templateUrl: context + "/views/main/footer.html", 
            scope : $scope, 
            clickOutsideToClose : true,
            preserveScope : true,
            disableBackdrop : false,
            disableParentScroll : true
         });
	}
	
});