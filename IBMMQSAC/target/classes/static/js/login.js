MQSAC.config(function($mdThemingProvider, $urlRouterProvider) {
	$mdThemingProvider.theme('default').primaryPalette('indigo').accentPalette(
			'blue').warnPalette("red").backgroundPalette("grey").dark();
	
	$urlRouterProvider.otherwise('/login');
});
