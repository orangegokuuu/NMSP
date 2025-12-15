SAC.controller('System.EventLogCtrl', function($scope, $rootScope, $filter, SystemService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var statusFilter = [
		{ value : 'SUCCESS', label : $translate('log.status.SUCCESS') }, 
		{ value : 'FAIL',    label : $translate('log.status.FAIL') },
		{ value : 'FAULT',   label : $translate('log.status.FAULT') }];

	var columnDefs = header();
	
	function header(){
		return [{ headerName : $translate('sys.eventlog.msg.02'), field : "eventDate", tooltipField : "eventDate", sort : 'desc', filter:'date' },
		{ headerName : $translate("sys.eventlog.msg.03"), field : "eventType", tooltipField : "eventType" },
		{ headerName : $translate("sys.eventlog.msg.05"), field : "status", tooltipField : "status", cellClass : statusStyle, 
			filter : OptionsFilter, filterParams : { options : statusFilter, apply : true } },
		{ headerName : $translate("sys.eventlog.msg.04"), field : "message", tooltipField : "message", suppressSorting : true,
			suppressFilter : true }, 
		{ headerName : $translate("sys.eventlog.msg.13"), field : "id1", tooltipField : "id1" },
		{ headerName : $translate("sys.eventlog.msg.14"), field : "id2", tooltipField : "id2" } ];
	}

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
		} 
	};

	$scope.autoFit = function() {
		var allColumnIds = [];
		columnDefs.forEach(function(columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);
	}

	var dataSource = { getRows : function(params) {
		// Add default sorting
		if (params.sortModel.length == 0) {
			var sort = { colId : "eventDate", sort : "desc" };
			params.sortModel.push(sort);
		}
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		SystemService.pageEventLog(searchPage).then(function(response) {
			if (response.status=='200') {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.eventDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.eventDate);
						item.eventDate = formattedDate;
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
		SystemService.pageEventLog(searchPage).then(function(response) {
			if (response.status==200) {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.eventDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.eventDate);
						item.eventDate = formattedDate;
					}
				});
				alasql('SELECT eventId, eventDate, eventType, status, message, id1, id2 INTO csv("eventlog.csv",{separator:","}) FROM ?', [pageResult.data]);
			}
		});
		
		$("#exportBtn").blur();
	}

});