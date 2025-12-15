SAC.controller('User.UserCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, UserService, AuthService, GridUtil, DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var statusFilter = [
		{ value : 'A', label : $translate('user.status.A') }, { value : 'S', label : $translate('user.status.S') },
		{ value : 'E', label : $translate('user.status.E') } ];

	var columnDefs = header();
		
	function header() {
		var cols = [
		{ headerName : $translate('user.user.msg.03'), field : "userId", tooltipField : "userId" },
		{ headerName : $translate("user.user.msg.04"), field : "userName", tooltipField : "userName" },
		{
		  headerName : $translate("user.user.msg.06"), field : "status", cellRenderer : statusRenderer, cellClass : statusStyle,
			filter : OptionsFilter, filterParams : { options : statusFilter, apply : true } },
		{ headerName : $translate("user.user.msg.05"), field : "role.roleName", tooltipField : "roleId", suppressSorting : true },
		{ headerName : $translate("user.user.msg.19"), field : "group.groupName", tooltipField : "groupId", suppressSorting : true },
		{ headerName : $translate("user.user.msg.08"), field : "createDate", tooltipField : "createDate", filter : 'date' },
		{ headerName : $translate("user.user.msg.13"), field : "createBy", tooltipField : "createBy" },
		{ headerName : $translate("user.user.msg.09"), field : "updateDate", tooltipField : "updateDate", filter : 'date' },
		{ headerName : $translate("user.user.msg.14"), field : "updateBy", tooltipField : "updateBy" } ];
		
		return cols;
	};	

	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };

	$scope.enableEdit = false;
	$scope.Error = null;

	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual', paginationPageSize : pageSize,
		dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true, enableServerSideFilter : true,
		rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ], debug : false,

		onRowDoubleClicked : function(event) {
			$scope.openUpdateForm(event.data);
		}, onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "USER_01:write")) {
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
		UserService.pageUser(searchPage).then(function(response) {
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

	function statusRenderer(param) {
		return $translate('user.status.' + param.value);
	}
	function statusStyle(param) {
		switch (param.value) {
		case 'A':
			return 'alert-success';
			break;
		case 'S':
			return 'alert-danger';
			break;
		case 'E':
			return 'alert-warning';
			break;
		default:
			return '';
			break;
		}
	}

	$scope.grid.datasource = dataSource;

	$scope.refreshGrid = function() {
		columnDefs = header();
		$scope.grid.api.setColumnDefs(columnDefs);
		$scope.grid.api.setDatasource(dataSource);
		$scope.grid.api.refreshHeader();
		$scope.Error = null;
		$("#refreshBtn").blur();
	}

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});
	
	UserService.listRole().then(function(result) {
		$scope.roles = [];
		if (result.status=='200') {
			$scope.roles = result.data;
		}
	});
	UserService.listGroup().then(function(result) {
		$scope.groups = [];
		if (result.status=='200') {
			$scope.groups = result.data;
		}
	});

	$scope.statuses = [
		{ code : 'A', text : $translate('user.status.' + 'A') }, { code : 'S', text : $translate('user.status.' + 'S') },
		{ code : 'E', text : $translate('user.status.' + 'E') } ];

	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.user = null;
		$mdDialog.show({
			controller : 'User.UserCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body), targetEvent : event,
			clickOutsideToClose : true, fullscreen : true });
	};
	
	$scope.allowStatusEdit = true;
	$scope.showUpdateForm = function(event) {
		var selectedUser = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selectedUser);
	};
	$scope.openUpdateForm = function(user) {
		if (AuthService.havePrivilege($scope.userSession.rights, "USER_01:write")) {
			if(user.userId=='WSAdmin'){
				$scope.allowStatusEdit = false;
			}else{
				$scope.allowStatusEdit = true;
			}
			$scope.UpdateForm.$setPristine();
			$scope.user = user;

			// Temporary Fix for Angular ngOptions
			// http://stackoverflow.com/questions/12654631/why-does-angularjs-include-an-empty-option-in-select
			$scope.roles.forEach(function(item) {
				if (user.role.roleId == item.roleId) {
					$scope.user.role = item;
				}
			});
			$scope.groups.forEach(function(item) {
				if (user.group.groupId == item.groupId) {
					$scope.user.group = item;
				}
			});

			$scope.user.password = '';
			$mdDialog.show({
				controller : 'User.UserCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true });
		}
	};
	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};

	$scope.create = function(user) {
		user.status = 'A';
		// Temporary fix @see
		// https://github.com/FasterXML/jackson-databind/issues/935
		// user.role.rights = null;
		UserService.createUser(user).then(function(response) {
			console.log(response);
			if (response.status==201) {
				console.log("Create User Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'user.user.err.' + response.code;
				if(response.code){
					$scope.Error = 'user.user.err.' + response.code;					
				}else{
					$scope.Error = 'user.user.err.' + response.status;
				}
			}
		})
	};

	$scope.update = function(user) {
		if(user.createDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(user.createDate);
			user.createDate = tempTime;
		}
		if(user.updateDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(user.updateDate);
			user.updateDate = tempTime;
		}

		UserService.updateUser(user).then(function(response) {
			if (response.status==200) {
				console.log("Update User Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'user.user.err.' + response.code;
				if(response.code){
					$scope.Error = 'user.user.err.' + response.code;					
				}else{
					$scope.Error = 'user.user.err.' + response.status;
				}
			}
		})
	};

	$scope.confirmDelete = function(user) {
		// Appending dialog to document.body to cover sidenav in
		// docs app
		var confirm = $mdDialog.confirm().title('Confirm ?').ok('OK').cancel('Cancel');

		$mdDialog.show(confirm).then(function() {
			if(user.userId=='WSAdmin'){
				$scope.Error = 'user.user.err.sys';
				$scope.openUpdateForm(user);
			}
			else if(user.userId==$rootScope.userSession.userId){
				$scope.Error = 'user.user.err.self'
				$scope.openUpdateForm(user);
			}
			else{
				if(user.createDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(user.createDate);
					user.createDate = tempTime;
				}
				if(user.updateDate!=null)
				{
					var tempTime = DateUtil.parserToLocalDateTime(user.updateDate);
					user.updateDate = tempTime;
				}
				UserService.deleteUser(user).then(function(response) {
					if (response.status==200) {
						$scope.refreshGrid();
					} else {
						$scope.Error = 'user.user.err.' + response.code;
						$scope.openUpdateForm(user);
					}
				})
			}
		}, function() {
			$scope.closeDialog();
		});
	};
});