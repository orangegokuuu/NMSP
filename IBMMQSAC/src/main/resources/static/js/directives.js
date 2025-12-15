MQSAC.directive('validatesa', function($rootScope) {

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

MQSAC.directive('formAutofillFix', function() {
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

MQSAC.directive('match', function() {
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

MQSAC.directive('privilege', function() {
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

MQSAC.directive('uppercased', function() {
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