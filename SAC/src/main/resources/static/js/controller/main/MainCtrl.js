SAC.controller('MainCtrl', function ($scope, $rootScope, $mdSidenav, $mdMedia, $mdDialog, $mdBottomSheet, $state, AuthService, UserService, DateUtil) {
	var loginEvent = false;
	$scope.$on('$viewContentLoaded', function () {
		AuthService.getSession().then(function (response) {
			console.log(response);
			$rootScope.userSession = response.data;
			$rootScope.locale = $rootScope.userSession.locale;

			if ($rootScope.userSession.user.createDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.createDate);
				$rootScope.userSession.user.createDate = formattedDate;
			}
			if ($rootScope.userSession.logonDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.logonDate);
				$rootScope.userSession.logonDate = formattedDate;
			}
			if ($rootScope.userSession.user.updateDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.updateDate);
				$rootScope.userSession.user.updateDate = formattedDate;
			}
			if ($rootScope.userSession.user.role.createDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.role.createDate);
				$rootScope.userSession.user.role.createDate = formattedDate;
			}
			if ($rootScope.userSession.user.role.updateDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.role.updateDate);
				$rootScope.userSession.user.role.updateDate = formattedDate;
			}
			if ($rootScope.userSession.role.createDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.role.createDate);
				$rootScope.userSession.role.createDate = formattedDate;
			}
			if ($rootScope.userSession.role.updateDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.role.updateDate);
				$rootScope.userSession.role.updateDate = formattedDate;
			}
			if ($rootScope.userSession.user.group.createDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.group.createDate);
				$rootScope.userSession.user.group.createDate = formattedDate;
			}
			if ($rootScope.userSession.user.group.updateDate != null) {
				var formattedDate = DateUtil.transLocalDateTime($rootScope.userSession.user.group.updateDate);
				$rootScope.userSession.user.group.updateDate = formattedDate;
			}
		});

		UserService.listPrivilege().then(function (result) {
			if (result.status == 200) {
				$scope.privilege = result.data;
			}
		});
		UserService.listPrivilegeType().then(function (result) {
			if (result.status == 200) {
				$scope.privilegeType = result.data;
			}
		});
	});

	$scope.lockLeft = true;
	$scope.mainMenu = function () {
		if ($mdMedia('gt-sm')) {
			$scope.lockLeft = !$scope.lockLeft;
		} else {
			$mdSidenav('left').toggle();
		}
	};


	$scope.userMenu = function ($mdOpenMenu, ev) {
		$mdOpenMenu(ev);
	};

	$scope.testExpired = function () {
		AuthService.expireSession().then(function (response) {
			console.log(response);
		});
	}

	$scope.updatePassword = function (credentials) {
		credentials.username = $rootScope.userSession.userId;
		//		console.log(credentials);
		AuthService.changePassword(credentials).then(function (response) {
			if (response.success) {
				$scope.Message = "auth.pass.msg.01";
				$scope.Error = false;

			} else {
				console.log(response);
				$scope.Message = false;
				$scope.Error = "auth.pass.err." + response.code;

			}
		})
	};

	$scope.showLoginForm = function () {
		$mdDialog.show({
			controller: 'AuthCtrl', templateUrl: context + "/views/auth/login.html",
			parent: angular.element(document.body),
			scope: $scope, clickOutsideToClose: false
		});
	};

	$scope.$on('event:auth-loginRequired', function () {
		if (!$rootScope.loginProgress) {
			$rootScope.loginProgress = true;

			$scope.showLoginForm();
		}

	});

	$scope.$on('event:auth-loginConfirmed', function () {
		$mdDialog.cancel();
		$rootScope.loginProgress = false;
	});

	$scope.showFooterMenu = function () {
		$mdBottomSheet.show({
			templateUrl: context + "/views/main/footer.html",
			scope: $scope,
			clickOutsideToClose: true,
			preserveScope: true,
			disableBackdrop: false,
			disableParentScroll: true
		});
	}

	if (!$rootScope.pwdConfig) {
		AuthService.getPwdConfig().then(function (response) {
			$rootScope.pwdConfig = response.data;
			//			console.log($rootScope.pwdConfig);
		});
	}

});