SAC.controller('Sms.SpamKeywordCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, SmsService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;
 
	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate('sms.spamkw.field.01'), field : "key", tooltipField : "key" },
		{ headerName : $translate("sms.spamkw.field.02"), field : "createDate", tooltipField : "createDate" },
		{ headerName : $translate("sms.spamkw.field.03"), field : "createBy", tooltipField : "createBy" }];
	};
	
	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };
	

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});
	
	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual',
		paginationPageSize : pageSize, dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true,
		enableServerSideFilter : true, rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ],
		debug : false,
	};

	$scope.autoFit = function() {
		var allColumnIds = [];
		columnDefs.forEach(function(columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);

	}

	var dataSource = { getRows : function(params) {
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		console.log(searchPage);
		console.log('asking for ' + params.startRow + ' to ' + params.endRow);
		SmsService.pageSpamKey(searchPage).then(function(response) {
			console.log(response);
			if (response.status=='200') {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.createDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.createDate);
						item.createDate = formattedDate;
					}
				});
				if (pageResult.totalSize > 0) {
					params.successCallback(pageResult.data, pageResult.totalSize);
				}
				$scope.autoFit();
			}
		});
	} };

	$scope.refreshGrid = function() {
		columnDefs = header();
		$scope.grid.api.setColumnDefs(columnDefs);
		$scope.grid.api.setDatasource(dataSource);
		$scope.grid.api.refreshHeader();
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

	$scope.exportCSV = function() {
		var searchPage = GridUtil.makePageSearch($scope.grid.api.getFilterModel(), $scope.grid.api.getSortModel(), 0, 0);
		SmsService.pageSpamKey(searchPage).then(function(response) {
			if (response.status==200) {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.createDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.createDate);
						item.createDate = formattedDate;
					}
				});
				alasql('SELECT key, createDate, createBy INTO csv("spamkeyword.csv",{separator:","}) FROM ?', [pageResult.data]);
			}
		});
		$("#exportBtn").blur();
	}
	
});