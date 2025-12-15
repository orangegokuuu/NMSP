var MQSAC = angular.module('MQSAC', [ 'ngMaterial', 'ngAnimate', 'ngMessages', 'ui.router', 'http-auth-interceptor', 
	'angular-loading-bar', 'jsonFormatter', 'ui.bootstrap']);

MQSAC.config(function($mdThemingProvider, cfpLoadingBarProvider, $compileProvider) {
	$mdThemingProvider.theme('default').primaryPalette('indigo').accentPalette('blue').warnPalette("red").backgroundPalette("grey");
	$compileProvider.preAssignBindingsEnabled(true);
});

MQSAC.config(function($httpProvider) {
	  $httpProvider.defaults.headers.delete = { 'Content-Type' : 'application/json' };
});

MQSAC.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

//MQSAC.config(function( $routeProvider) {
//	
//	$routeProvider.when('/main', {	templateUrl :  context + '/views/queue-manager.html', controller : 'mqCtrl' })
//	$routeProvider.otherwise({
//		redirectTo : '/main'
//	});
//});

MQSAC.config(function($stateProvider, $urlRouterProvider) {
	
	var loginState = { name : 'login', url : '/login', templateUrl : context + '/views/auth/login.html' }
	$stateProvider.state(loginState);
	
	var mainState = { name : 'main', url : '/main', templateUrl : context + '/views/main/queue-manager.html' }
	$stateProvider.state(mainState);

	var sadaState = { name : 'sada', url : '/sada', templateUrl : context + '/views/main/sada.html' }
	$stateProvider.state(sadaState);
	
	var waterlevelState = { name : 'waterlevel', url : '/waterlevel', templateUrl : context + '/views/main/waterlevel.html' }
	$stateProvider.state(waterlevelState);

	$urlRouterProvider.otherwise('/main');
});
