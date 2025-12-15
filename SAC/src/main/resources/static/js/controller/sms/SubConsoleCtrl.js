SAC.controller('Sms.SubConsoleCtrl', function($scope, $rootScope, $http,
		$filter, $mdDialog, SmsService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	$scope.user = null;

	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	$scope.statusFilter = [ {
		value : 'A',
		label : $translate('user.status.A')
	}, {
		value : 'S',
		label : $translate('user.status.S')
	}, {
		value : 'E',
		label : $translate('user.status.E')
	} ];

	var columnDefs = header();

	function header() {
		return [ {
			headerName : $translate('sms.subconsole.field.01'),
			field : "userId",
			tooltipField : "userId"
		}, {
			headerName : $translate("sms.subconsole.field.02"),
			field : "userName",
			tooltipField : "userName"
		}, {
			headerName : $translate("sms.subconsole.field.03"),
			field : "status",
			cellRenderer : statusRenderer,
			cellClass : statusStyle,
			filter : OptionsFilter,
			filterParams : {
				options : $scope.statusFilter,
				apply : true
			}
		},
		// { headerName : $translate("sms.subconsole.field.04"), field :
		// "lastLogin", tooltipField : "lastLogin", filter : 'date'},
		{
			headerName : $translate("user.user.msg.08"),
			field : "createDate",
			tooltipField : "createDate",
			filter : 'date'
		}, {
			headerName : $translate("user.user.msg.13"),
			field : "createBy",
			tooltipField : "createBy"
		}, {
			headerName : $translate("user.user.msg.09"),
			field : "updateDate",
			tooltipField : "updateDate",
			filter : 'date'
		}, {
			headerName : $translate("user.user.msg.14"),
			field : "updateBy",
			tooltipField : "updateBy"
		} ];
	}
	;

	var defaultColDef = {
		minWidth : 70,
		filter : StringFilter,
		filterParams : {
			newRowsAction : 'keep',
			apply : true
		}
	};

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

	$scope.grid = {
		columnDefs : columnDefs,
		enableColResize : true,
		enableFilter : true,
		rowModelType : 'virtual',
		paginationPageSize : pageSize,
		dataSource : dataSource,
		defaultColDef : defaultColDef,
		enableServerSideSorting : true,
		enableServerSideFilter : true,
		rowSelection : 'single',
		animateRows : true,
		sortingOrder : [ 'desc', 'asc' ],
		debug : false,

		onRowDoubleClicked : function(event) {
			$scope.openUpdateForm(event.data);
		},
		onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights,
					"USER_07:write")) {
				$scope.enableEdit = true;
			}
			$scope.$digest();
		}
	};

	$scope.autoFit = function() {
		var allColumnIds = [];
		columnDefs.forEach(function(columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);
	}

	var dataSource = {
		getRows : function(params) {
			var searchPage = GridUtil.makePageSearch(params.filterModel,
					params.sortModel, pageSize, params.startRow);
			console.log(searchPage);
			console.log('asking for ' + params.startRow + ' to '
					+ params.endRow);
			SmsService.pageSubUser(searchPage).then(
					function(response) {
						console.log(response);
						if (response.status=='200') {
							var pageResult = response.data;
							pageResult.data.forEach(function(item) {
								if(item.createDate != null){
									var formattedDate = DateUtil.transLocalDateTime(item.createDate);
									item.createDate = formattedDate;
								}

								if(item.updateDate != null){
									var formattedDate = DateUtil.transLocalDateTime(item.updateDate);
									item.updateDate = formattedDate;
								}
							});
							if (pageResult.totalSize > 0) {
								params.successCallback(pageResult.data,
										pageResult.totalSize);
							}
							$scope.autoFit();
						}
					});
		}
	};

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
		$scope.user = null;
		$mdDialog.show({
			controller : 'Sms.SubConsoleCtrl',
			contentElement : '#CreateDialog',
			parent : angular.element(document.body),
			targetEvent : event,
			clickOutsideToClose : true,
			fullscreen : true
		});
	};

	$scope.showUpdateForm = function(event) {
		var selectedUser = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selectedUser);
	};

	$scope.openUpdateForm = function(user) {
		if (AuthService.havePrivilege($scope.userSession.rights,
				"USER_07:write")) {
			if (user.userId == 'WSAdmin') {
				$scope.allowStatusEdit = false;
			} else {
				$scope.allowStatusEdit = true;
			}
			$scope.UpdateForm.$setPristine();
			$scope.user = user;

			$scope.user.password = '';
			$mdDialog.show({
				controller : 'Sms.SubConsoleCtrl',
				contentElement : '#UpdateDialog',
				parent : angular.element(document.body),
				clickOutsideToClose : true,
				fullscreen : true
			});
		}
	};
	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};

	$scope.create = function(user) {
		user.status = 'A';
		SmsService.createSubUser(user).then(function(response) {
			console.log(response);
			if (response.status==201) {
				console.log("Create Sub User Success, Refresh Grid");
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
			tempTime = DateUtil.parserToLocalDateTime(user.updateDate);
			user.updateDate = tempTime;
		}
		SmsService.updateSubUser(user).then(function(response) {
			if (response.status==200) {
				console.log("Update Sub User Success, Refresh Grid");
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
		var confirm = $mdDialog.confirm().title('Confirm ?').ok('OK').cancel(
				'Cancel');

		$mdDialog.show(confirm).then(function() {
			if(user.createDate!=null)
			{
				var tempTime = DateUtil.parserToLocalDateTime(user.createDate);
				user.createDate = tempTime;
			}
			if(user.updateDate!=null)
			{
				tempTime = DateUtil.parserToLocalDateTime(user.updateDate);
				user.updateDate = tempTime;
			}
			SmsService.deleteSubUser(user).then(function(response) {
				if (response.status==200) {
					$scope.refreshGrid();
				} else {
					// $scope.Error = 'user.user.err.' + response.code;
					if(response.code){
						$scope.Error = 'user.user.err.' + response.code;					
					}else{
						$scope.Error = 'user.user.err.' + response.status;
					}
					$scope.openUpdateForm(user);
				}
			})
		}, function() {
			$scope.closeDialog();
		});
	};

});