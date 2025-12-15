SAC.controller('System.ParamCtrl', function($scope, $rootScope, $filter, $mdDialog, $timeout, SystemService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate('sys.param.msg.03'), field : "type", tooltipField : "type"},
		{ headerName : $translate("sys.param.msg.02"), field : "paramId", tooltipField : "paramId" },
		{ headerName : $translate("sys.param.msg.04"), field : "labels", cellRenderer : labelRenderer, suppressSorting : true, suppressFilter : true  },
		{ headerName : $translate("sys.param.msg.05"), field : "value", tooltipField : "value", suppressSorting : true, suppressFilter : true }, 
		{ headerName : $translate("sys.param.msg.07"), field : "createDate", tooltipField : "createDate", filter : 'date' },
		{ headerName : $translate("sys.param.msg.06"), field : "createBy", tooltipField : "createBy" },
		{ headerName : $translate("sys.param.msg.09"), field : "updateDate", tooltipField : "updateDate", filter : 'date' },
		{ headerName : $translate("sys.param.msg.08"), field : "updateBy", tooltipField : "updateBy" }];
	}

	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true }, editable : false };

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});
	
	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual', paginationPageSize : pageSize,
		dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true, enableServerSideFilter : true,
		rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ], debug : false,

		onRowDoubleClicked : function(event) {
			console.log(event.data);
			$scope.openUpdateForm(event.data);
		}, onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "SYSTEM_01:write")) {
				$scope.enableEdit = true;
				$scope.$digest();
			}
		}
	};

	$scope.autoFit = function() {
		var allColumnIds = [];
		columnDefs.forEach(function(columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);
	}
	function labelRenderer(param) {
		return param.value[$rootScope.userSession.locale].paramName;
	}
	
	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);
	};
	$scope.openUpdateForm = function(param) {
		if (AuthService.havePrivilege($scope.userSession.rights, "SYSTEM_01:write")) {
			$scope.UpdateForm.$setPristine();
			$scope.param = param;
			if (param.valueType == 'INT') {
				param.value = parseInt(param.value);
			}
			$mdDialog.show({
				controller : 'System.ParamCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true });
		}
	};
	
	$scope.update = function(param) {
//		console.log(param);

		if(param.createDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(param.createDate);
			param.createDate = tempTime;
		}
		if(param.updateDate!=null)
		{
			tempTime = DateUtil.parserToLocalDateTime(param.updateDate);
			param.updateDate = tempTime;
		}
		SystemService.updateParam(param).then(function(response) {
			if (response.status==200) {
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'sys.param.err.' + response.code;
				if(response.code){
					$scope.Error = 'sys.param.err.' + response.code;					
				}else{
					$scope.Error = 'sys.param.err.' + response.status;
				}
			}
		})
	};

	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};
	
	var dataSource = { getRows : function(params) {
		// Add default sorting
		if (params.sortModel.length == 0) {
			var sort = { colId : "position", sort : "asc" };
			params.sortModel.push(sort);
		}
		var viewable = {type : 'equals', filter : true};
		params.filterModel.viewable = viewable;
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		SystemService.pageParam(searchPage).then(function(response) {
			if (response.status=='200') {
				var pageResult = response.data;
				pageResult.data.forEach(function(item) {
					if(item.createDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.createDate);
						item.createDate = formattedDate;
					}
					if(item.updateDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.updateDate);
						item.updateDate = formattedDate;
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
		$scope.Error = null;
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

});