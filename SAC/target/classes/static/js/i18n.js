SAC.factory('UrlLanguageStorage', [ '$location', function($location) {
	return { put : function(name, value) {
	}, get : function(name) {
		return $location.search()['lang']
	} };
} ]);

SAC.factory('LangService', function($http) {
	var langService = {};

	langService.changeLang = function(locale) {
		$http.get(context + '/lang/'+locale)
	};
	
	return langService;
});

SAC.config(function($translateProvider) {
	$translateProvider.useSanitizeValueStrategy('escapeParameters');
	$translateProvider.useUrlLoader(context + '/messageSource');
	$translateProvider.useStorage('UrlLanguageStorage');
	$translateProvider.useLoaderCache(true);
	$translateProvider.registerAvailableLanguageKeys(['en','zh-TW']);
	
	$translateProvider.preferredLanguage('en');
	$translateProvider.fallbackLanguage('en');
});

SAC.controller('LangCtrl', function($scope, $rootScope, $translate, $location, $timeout, LangService) {
	$scope.changeLang = function(langKey) {
		LangService.changeLang(langKey);
		$location.search('lang', langKey);
		$translate.use(langKey);
		$translate.onReady(function() {
			$timeout(function() {
				$rootScope.$broadcast('Event:LangChange', langKey);
			},100);
		});
	};
});
