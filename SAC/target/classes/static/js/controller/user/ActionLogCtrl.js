SAC.controller('User.ActionLogCtrl', function($scope, $rootScope, $filter, UserService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var statusFilter = [
		{ value : 'SUCCESS', label : $translate('log.status.SUCCESS') }, 
		{ value : 'FAIL', label : $translate('log.status.FAIL') },
		{ value : 'FAULT', label : $translate('log.status.FAULT') }];

	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate("user.actionlog.msg.08"), field : "actionType", tooltipField : "actionType" },
		{ headerName : $translate('user.actionlog.msg.07'), field : "actionDate", tooltipField : "actionDate", sort : 'desc',  filter : 'date'},
		{ headerName : $translate("user.actionlog.msg.06"), field : "session.sessionId", tooltipField : "session.sessionId", width : 80, suppressFilter : true },
		{ headerName : $translate("user.actionlog.msg.01"), field : "doneBy", tooptipField : "doneBy" },
		{ headerName : $translate("user.actionlog.msg.10"), field : "status", tooltipField : "status", cellClass : statusStyle,
			filter : OptionsFilter, filterParams : { options : statusFilter, apply : true } },
		{ headerName : $translate("user.actionlog.msg.09"), field : "message", tooltipField : "message", 
			suppressSorting : true, suppressFilter : true } ];
	};

	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	function statusRenderer(param) {
		return $translate('group.status.' + param.value);
	}
	function statusStyle(param) {
		switch (param.value) {
		case 'SUCCESS':
			return 'alert-success';
			break;
		default:
			return 'alert-danger';
			break;
		}
	}

	$scope.updateDetail = function(data) {
		$scope.detail = data;
		var i = 0;
		$scope.detail.params.forEach(function(p) {
			try {
				$scope.detail.params[i].value = angular.fromJson(p.value);
			} catch (e) {
			}
			i++;
		})
		$scope.$digest();
		console.log($scope.detail);
	}
	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual', paginationPageSize : pageSize,
		dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true, enableServerSideFilter : true,
		rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ], debug : false,

		onRowClicked : function(event) {
			$scope.updateDetail(event.data);
		} };

	$scope.autoFit = function() {
		var allColumnIds = [];
		columnDefs.forEach(function(columnDef) {
			if (columnDef.field != 'session.sessionId') {
				allColumnIds.push(columnDef.field);
			}
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);
	}

	var dataSource = { getRows : function(params) {
		// Add default sorting
		if (params.sortModel.length == 0) {
			var sort = { colId : "actionDate", sort : "desc" };
			params.sortModel.push(sort);
		}
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		UserService.pageActionLog(searchPage).then(function(response) {
			if (response.status=='200') {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.actionDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.actionDate);
						item.actionDate = formattedDate;
					}
					if(item.session.lastAccess!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.session.lastAccess);
						item.session.lastAccess = formattedDate;
					}
					if(item.session.logonDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.session.logonDate);
						item.session.logonDate = formattedDate;
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
		UserService.pageActionLog(searchPage).then(function(response) {
			if (response.status==200) {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.actionDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.actionDate);
						item.actionDate = formattedDate;
					}
					if(item.session.lastAccess!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.session.lastAccess);
						item.session.lastAccess = formattedDate;
					}
					if(item.session.logonDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.session.logonDate);
						item.session.logonDate = formattedDate;
					}
				});
				alasql('SELECT actionId, actionType, actionDate, session->sessionId as sessionId, doneBy, status, message INTO csv("actionlog.csv",{separator:","}) FROM ?', [pageResult.data]);
			}
		});
		
		$("#exportBtn").blur();
	}
});