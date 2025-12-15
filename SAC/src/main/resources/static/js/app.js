agGrid.initialiseAgGridWithAngular1(angular);

var SAC = angular.module('SAC', [
	'ngMaterial', 'ngAnimate', 'ngMessages', 'ui.router', 'pascalprecht.translate', 'agGrid', 'http-auth-interceptor', 
	'angular-loading-bar', 'jsonFormatter','textAngular','chart.js', 'ngSanitize', 'ui.bootstrap'  ]);

SAC.config(function($mdThemingProvider, cfpLoadingBarProvider, $compileProvider) {
	$mdThemingProvider.theme('default').primaryPalette('indigo').accentPalette('blue').warnPalette("red").backgroundPalette("grey");
	$compileProvider.preAssignBindingsEnabled(true);
});

SAC.config(function($httpProvider) {
	  $httpProvider.defaults.headers.delete = { 'Content-Type' : 'application/json' };
});

SAC.config(function($qProvider){
	$qProvider.errorOnUnhandledRejections(false);
});

SAC.config(function($stateProvider, $urlRouterProvider) {
	var mainState = { name : 'main', url : '/main', templateUrl : context + '/views/main/dashboard.html' }
	$stateProvider.state(mainState);

	var loginState = { name : 'login', url : '/login', templateUrl : context + '/views/auth/login.html' }
	$stateProvider.state(loginState);
	
	var passwordState = { name : 'password', url : '/password', templateUrl : context + '/views/auth/password.html' }
	$stateProvider.state(passwordState);

	var profileState = { name : 'profile', url : '/profile', templateUrl : context + '/views/main/profile.html' }
	$stateProvider.state(profileState);
	
	$stateProvider.state('user', { url : '/api/user', templateUrl : context + '/views/user/title.html' })
		.state('user.user', { url : '/user', templateUrl : context + '/views/user/user.html' })
		.state('user.role',	{ url : '/role', templateUrl : context + '/views/user/role.html' })
		.state('user.group',{ url : '/group', templateUrl : context + '/views/user/group.html' })
		.state('user.actionlog', { url : '/actionlog', templateUrl : context + '/views/user/actionlog.html' });

	$stateProvider.state('system', { url : '/api/system',   templateUrl : context + '/views/system/title.html' })
		.state('system.param',     { url : '/param',    templateUrl : context + '/views/system/param.html' })
		.state('system.template',  { url : '/template', templateUrl : context + '/views/system/template.html' })
		.state('system.eventlog',  { url : '/eventlog', templateUrl : context + '/views/system/eventlog.html' });

	$stateProvider.state('sms', { url : '/api/sms', templateUrl : context + '/views/sms/title.html' })
		.state('sms.cp', { url : '/cp', templateUrl : context + '/views/sms/cp.html' })
		.state('sms.record', { url : '/record', templateUrl : context + '/views/sms/record.html' })
		.state('sms.blacklist', { url : '/blacklist', templateUrl : context + '/views/sms/blacklist.html' })
		.state('sms.spamkeyword', { url : '/spamkeyword', templateUrl : context + '/views/sms/spamkeyword.html' })
		.state('sms.timetable', { url : '/timetable', templateUrl : context + '/views/sms/timetable.html' })
		.state('sms.report', { url : '/report', templateUrl : context + '/views/sms/report.html' })
		.state('sms.subconsole', { url : '/subconsole', templateUrl : context + '/views/sms/subconsole.html' });
	
	$urlRouterProvider.otherwise('/main');
});
