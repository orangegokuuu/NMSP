SAC.directive('validatepwd', function($rootScope, AuthService) {

//	AuthService.getPwdConfig().then(function(response) {
//		console.log(response);
//		$rootScope.pwdConfig = response.data;
//		console.log($rootScope.pwdConfig);
//	});
//	
	
	// inject from application.properties file
	var pwd_minL = $rootScope.pwdConfig.pwd_minL;
	var pwd_maxL = $rootScope.pwdConfig.pwd_maxL;
	var lowercase_req = $rootScope.pwdConfig.lowercase_req;
	var uppercase_req = $rootScope.pwdConfig.uppercase_req;
	var special_req = $rootScope.pwdConfig.special_req;
	var num_req = $rootScope.pwdConfig.num_req;

	var lowercase_regExp = new RegExp("^(?=(.*[a-z]){" + lowercase_req + "}).+");
	var uppercase_regExp = new RegExp("^(?=(.*[A-Z]){" + uppercase_req + "}).+");
	var special_regExp = new RegExp("^(?=(.*[^a-zA-Z0-9]){" + special_req + "}).+");
	var num_regExp = new RegExp("^(?=(.*[0-9]){" + num_req + "}).+");

	// console.log(lowercase_regExp, uppercase_regExp, special_regExp,
	// num_regExp);

	return {
		restrict : 'A',
		require : 'ngModel',
		link : function(scope, element, attr, ctrl) {
			function customValidator(ngModelValue) {

				// check if contains lowercase
				// if it does contain lowercase, set our custom
				// `lowercaseValidator` to valid/true
				// otherwise set it to non-valid/false
				if (lowercase_regExp.test(ngModelValue)) {
					ctrl.$setValidity('lowercaseValidator', true);
				} else {
					ctrl.$setValidity('lowercaseValidator', false);
				}

				// check if contains uppercase
				// if it does contain uppercase, set our custom
				// `uppercaseValidator` to valid/true
				// otherwise set it to non-valid/false
				if (uppercase_regExp.test(ngModelValue)) {
					ctrl.$setValidity('uppercaseValidator', true);
				} else {
					ctrl.$setValidity('uppercaseValidator', false);
				}

				// check if contains special characters
				// if it does contain special characters, set our custom
				// `specialValidator` to valid/true
				// otherwise set it to non-valid/false
//				console.log("special_regExp = "	+ special_regExp.test(ngModelValue));
				if (special_regExp.test(ngModelValue)) {
					ctrl.$setValidity('specialValidator', true);
				} else {
					ctrl.$setValidity('specialValidator', false);
				}

				// check if contains number
				// if it does contain number, set our custom `numberValidator`
				// to valid/true
				// otherwise set it to non-valid/false
				if (num_regExp.test(ngModelValue)) {
					ctrl.$setValidity('numberValidator', true);
				} else {
					ctrl.$setValidity('numberValidator', false);
				}

				if (ngModelValue.length >= pwd_minL
						&& ngModelValue.length <= pwd_maxL) {
					ctrl.$setValidity('lengthValidator', true);
				} else {
					ctrl.$setValidity('lengthValidator', false);
				}

				// we need to return our ngModelValue, to be displayed to the
				// user(value of the input)
				return ngModelValue;
			}
			ctrl.$parsers.push(customValidator);
		}
	};
});


SAC.directive('validatesa', function($rootScope) {

	var regExp01 = new RegExp("^0123456[0-9]{13}$");
	var regExp02 = new RegExp("^01234567890123456[0-7]{1}02$");
	var regExp03 = new RegExp("^55111$");
	var regExp04 = new RegExp("^01234567890123456700$");
	var regExp05 = new RegExp("^886936123456$");
	var regExp06 = new RegExp("^0123456789012345678(0|2)$");
	var regExp07 = new RegExp("^01234[0-9]{1}0936123456[0-9]{2}(00|02|03|09)$");

	var regExpList = [regExp01, regExp02, regExp03, regExp04, regExp05, regExp06,
			regExp07 ];
	
	return {
		restrict : 'A',
		require : 'ngModel',
		link : function(scope, element, attr, ctrl) {
			function customValidator(ngModelValue) {

				// check if it match regExp
				// if it does match regExp, set our custom `SAValidator` to
				// valid/true
				// otherwise set it to non-valid/false
				if (regExpList[0].test(ngModelValue)){
					if(regExpList[1].test(ngModelValue)){
						ctrl.$setValidity('SAValidator', true);
						console.log("validate = true");
						return ngModelValue;	
					}
					if(regExpList[3].test(ngModelValue)){
						ctrl.$setValidity('SAValidator', true);
						console.log("validate = true");
						return ngModelValue;	
					}
				}
				
				if(regExpList[2].test(ngModelValue)){
					ctrl.$setValidity('SAValidator', true);
					console.log("validate = true");
					return ngModelValue;	
				}
				if(regExpList[4].test(ngModelValue)){
					ctrl.$setValidity('SAValidator', true);
					console.log("validate = true");
					return ngModelValue;	
				}
				if(regExpList[5].test(ngModelValue)){
					ctrl.$setValidity('SAValidator', true);
					console.log("validate = true");
					return ngModelValue;	
				}
				if(regExpList[6].test(ngModelValue)){
					ctrl.$setValidity('SAValidator', true);
					console.log("validate = true");
					return ngModelValue;	
				}
				ctrl.$setValidity('SAValidator', false);
				console.log("validate = false");
				return ngModelValue;
			}
			ctrl.$parsers.push(customValidator);
		}
	};
});

SAC.directive('formAutofillFix', function() {
	return function(scope, element, attrs) {
		element.prop('method', 'post');
		if (attrs.ngSubmit) {
			element.off('submit').on(
					'submit',
					function(event) {
						event.preventDefault();
						element.find('input, textarea, select').triggerHandler(
								'input').triggerHandler('change')
								.triggerHandler('keydown');
						// element.find('input, textarea,
						// select').trigger('input').trigger('change').trigger('keydown');
						scope.$apply(attrs.ngSubmit);
					});
		}
	};
});

SAC.directive('match', function() {
	return {
		restrict : 'A',
		scope : true,
		require : 'ngModel',
		link : function(scope, elem, attrs, control) {
			var checker = function() {
				// get the value of the first password
				var e1 = scope.$eval(attrs.ngModel);
				// get the value of the other password
				var e2 = scope.$eval(attrs.match);
				return e1 == e2;
			};
			scope.$watch(checker, function(n) {
				// set the form control to valid if both
				// passwords are the same, else invalid
				control.$setValidity("match", n);
			});
		}
	};
});

SAC.directive('privilege', function() {
	return function(scope, element, attrs) {
		var privilege = "";
		var level = "";
		if (attrs.privilege.indexOf(":") > 0) {
			privilege = attrs.privilege.split(":")[0];
			level = attrs.privilege.split(":")[1];
		} else {
			privilege = attrs.privilege;
			level = "read";
		}
		switch (level) {
		case 'write':
			if (scope.userSession.rights['SUPER'] == null
					&& scope.userSession.rights[privilege] == null) {
				element.prop('disabled', true);
			} else if (!scope.userSession.rights['SUPER'].writeEnable
					&& !scope.userSession.rights[privilege].writeEnable) {
				console.log("required " + privilege + " "
						+ scope.userSession.rights[privilege].writeEnable);
				element.prop('disabled', true);
			}
			break;
		case 'other':
			if (scope.userSession.rights['SUPER'] == null
					&& scope.userSession.rights[privilege] == null) {
				element.prop('disabled', true);
			} else if (!scope.userSession.rights['SUPER'].otherEnable
					&& !scope.userSession.rights[privilege].otherEnable) {
				element.prop('disabled', true);
			}
			break;
		default:
			if (scope.userSession.rights['SUPER'] == null
					&& scope.userSession.rights[privilege] == null) {
				element.prop('disabled', true);
			} else if (!scope.userSession.rights['SUPER'].readEnable
					&& !scope.userSession.rights[privilege].readEnable) {
				element.prop('disabled', true);
			}
			break;
		}
	};
});

SAC.directive('uppercased', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, modelCtrl) {
            modelCtrl.$parsers.push(function(input) {
                return input ? input.toUpperCase() : "";
            });
            element.css("text-transform","uppercase");
        }
    };
})