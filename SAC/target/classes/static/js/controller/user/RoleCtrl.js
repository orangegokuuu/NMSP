SAC.controller('User.RoleCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, UserService, AuthService, GridUtil, DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;
	$scope.enableEdit = false;
	$scope.Error = null;

	$scope.privilege = [];
	$scope.privilegeType = [];

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	UserService.listPrivilege().then(function(result) {
		if (result.status==200) {
			$scope.privilege = result.data;
		}
	});
	UserService.listPrivilegeType().then(function(result) {
		if (result.status==200) {
			$scope.privilegeType = result.data;
		}
	});

	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate('user.role.msg.03'), field : "roleId", tooltipField : "roleId" },
		{ headerName : $translate("user.role.msg.04"), field : "roleName", tooltipField : "roleName" },
		{ headerName : $translate("user.role.msg.05"), field : "createDate", tooltipField : "createDate", filter : 'date' },
		{ headerName : $translate("user.role.msg.12"), field : "createBy", tooltipField : "createBy" },
		{ headerName : $translate("user.role.msg.06"), field : "updateDate", tooltipField : "updateDate", filter : 'date' },
		{ headerName : $translate("user.role.msg.13"), field : "updateBy", tooltipField : "updateBy" } ];
	};	

	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };

	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual', paginationPageSize : pageSize,
		dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true, enableServerSideFilter : true,
		rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ], debug : false,

		onRowDoubleClicked : function(event) {
			$scope.openUpdateForm(event.data);
		}, onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "USER_02:write")) {
				$scope.enableEdit = true;
			}
			$scope.$digest();
		} };

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
		UserService.pageRole(searchPage).then(function(response) {
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
		$scope.Error = null;
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.role = {};
		$scope.role.rmap = {};
		$scope.privilege.forEach(function(r) {
			$scope.role.rmap[r.pk.privilegeId] = r;
		});
		$mdDialog.show({
			controller : 'User.roleCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body), targetEvent : event,
			clickOutsideToClose : true, fullscreen : true });
	};

	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);
	};
	$scope.openUpdateForm = function(role) {
		if (AuthService.havePrivilege($scope.userSession.rights, "USER_02:write")) {
			$scope.UpdateForm.$setPristine();
			$scope.role = role;
			console.log($scope.role);
			$mdDialog.show({
				controller : 'User.roleCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true });
		}
	};

	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};

	$scope.create = function(role) {
		console.log(role);
		role.rights = [];
		for ( var key in role.rmap) {
			var right = role.rmap[key];
			console.log(right);
			right.pk.roleId = role.roleId;
			right.pk.privilegeId = key;
			role.rights.push(role.rmap[key]);
		}
		UserService.createRole(role).then(function(response) {
			if (response.status==201) {
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'user.role.err.' + response.code;
				if(response.code){
					$scope.Error = 'user.role.err.' + response.code;					
				}else{
					$scope.Error = 'user.role.err.' + response.status;
				}
			}
		})
	};

	$scope.update = function(role) {
		role.rights = [];
//		console.log(role);
		for ( var key in role.rmap) {
			var right = role.rmap[key];
			role.rights.push(role.rmap[key]);
		}

		if(role.createDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(role.createDate);
			role.createDate = tempTime;
		}
		if(role.updateDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(role.updateDate);
			role.updateDate = tempTime;
		}

		UserService.updateRole(role).then(function(response) {
			if (response.status==200) {
				console.log("Update role Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'user.role.err.' + response.code;
				if(response.code){
					$scope.Error = 'user.role.err.' + response.code;					
				}else{
					$scope.Error = 'user.role.err.' + response.status;
				}
			}
		})
	};

	$scope.confirmDelete = function(role) {
		var confirm = $mdDialog.confirm().title('Confirm ?').ok('OK').cancel('Cancel');

		$mdDialog.show(confirm).then(function() {
			if(role.roleId=='ROOT'){
				$scope.Error = 'user.user.err.sys';
				$scope.openUpdateForm(role);
			} else{
				if(role.createDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(role.createDate);
					role.createDate = tempTime;
				}
				if(role.updateDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(role.updateDate);
					role.updateDate = tempTime;
				}
				UserService.deleteRole(role).then(function(response) {
					if (response.status==200) {
						$scope.refreshGrid();
					} else {
						// $scope.Error = 'user.role.err.' + response.code;
						if(response.code){
							$scope.Error = 'user.role.err.' + response.code;					
						}else{
							$scope.Error = 'user.role.err.' + response.status;
						}
						$scope.openUpdateForm(role);
					}
				})
			}
		}, function() {
			$scope.closeDialog();
		});
	};
});