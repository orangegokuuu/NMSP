SAC.controller('User.GroupCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, UserService, AuthService, GridUtil, DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var statusFilter = [
		{ value : 'A', label : $translate('group.status.A') }, { value : 'I', label : $translate('group.status.I') } ];

	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate('user.group.msg.03'), field : "groupId", tooltipField : "groupId" },
		{ headerName : $translate("user.group.msg.04"), field : "groupName", tooltipField : "groupName" },
		{
			headerName : $translate("user.group.msg.11"), field : "groupStatus", cellRenderer : statusRenderer,
			cellClass : statusStyle, filter : OptionsFilter, filterParams : { options : statusFilter, apply : true } },
		{ headerName : $translate("user.group.msg.05"), field : "createDate", tooltipField : "createDate", filter : 'date' },
		{ headerName : $translate("user.group.msg.07"), field : "createBy", tooltipField : "createBy" },
		{ headerName : $translate("user.group.msg.06"), field : "updateDate", tooltipField : "updateDate", filter : 'date' },
		{ headerName : $translate("user.group.msg.08"), field : "updateBy", tooltipField : "updateBy" } ];
	};
	
	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };
	
	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	$scope.enableEdit = false;
	$scope.Error = null;

	function statusRenderer(param) {
		return $translate('group.status.' + param.value);
	}
	function statusStyle(param) {
		switch (param.value) {
		case 'A':
			return 'alert-success';
			break;
		case 'I':
			return 'alert-warning';
			break;
		default:
			return '';
			break;
		}
	}

	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual',
		paginationPageSize : pageSize, dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true,
		enableServerSideFilter : true, rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ],
		debug : false,

		onRowDoubleClicked : function(event) {
			$scope.openUpdateForm(event.data);
		}, onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "USER_03:write")) {
				$scope.enableEdit = true;
				$scope.$digest();
			}
		}};

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
		UserService.pageGroup(searchPage).then(function(response) {
			console.log(response);
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
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.group = null;
		$mdDialog.show({
			controller : 'User.GroupCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body),
			targetEvent : event, clickOutsideToClose : true, fullscreen : true });
	};

	$scope.allowStatusEdit = true;
	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);
	};
	$scope.openUpdateForm = function(group) {
		if (AuthService.havePrivilege($scope.userSession.rights, "USER_03:write")) {
			if(group.groupId=='SYS'){
				$scope.allowStatusEdit = false;
			}else{
				$scope.allowStatusEdit = true;
			}
			$scope.UpdateForm.$setPristine();
			$scope.group = group;
	
			$mdDialog.show({
				controller : 'User.GroupCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true });
		}
	};

	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};

	$scope.create = function(group) {
		group.status = 'A';
		UserService.createGroup(group).then(function(response) {
			if (response.status==201) {
				console.log("Create Group Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				if(response.code){
					$scope.Error = 'user.group.err.' + response.code;					
				}else{
					$scope.Error = 'user.group.err.' + response.status;
				}
			}
		})
	};

	$scope.statuses = [
		{ code : 'A', text : $translate('group.status.' + 'A') }, { code : 'I', text : $translate('group.status.' + 'I') } ];

	$scope.update = function(group) {
		if(group.createDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(group.createDate);
			group.createDate = tempTime;
		}
		if(group.updateDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(group.updateDate);
			group.updateDate = tempTime;
		}
		UserService.updateGroup(group).then(function(response) {
			if (response.status==200) {
				console.log("Update Group Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
//				console.log(response);
				if(response.code){
					$scope.Error = 'user.group.err.' + response.code;					
				}else{
					$scope.Error = 'user.group.err.' + response.status;
				}
			}
		})
	};

	$scope.confirmDelete = function(group) {
		// Appending dialog to document.body to cover sidenav in
		// docs app
		var confirm = $mdDialog.confirm().title('Confirm ?').ok('OK').cancel('Cancel');

		$mdDialog.show(confirm).then(function() {
			if(group.groupId=='SYS'){
				$scope.Error = 'user.user.err.sys';
				$scope.openUpdateForm(group);
			} else{
				if(group.createDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(group.createDate);
					group.createDate = tempTime;
				}
				if(group.updateDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(group.updateDate);
					group.updateDate = tempTime;
				}
				UserService.deleteGroup(group).then(function(response) {
					if (response.status==200) {
						$scope.refreshGrid();
					} else {
						if(response.code){
							$scope.Error = 'user.group.err.' + response.code;					
						}else{
							$scope.Error = 'user.group.err.' + response.status;
						}
						$scope.openUpdateForm(group);
					}
				})
			}
		}, function() {
			$scope.closeDialog();
		});
	};
});
