MQSAC.controller('mqCtrl', function ($scope, $mdDialog, $filter, CpService, MqService, InitService) {

	// not used
	$scope.qManagerList = [{ value: 'DMZ.QM1' },
	{ value: 'DMZ.QM2' },
	{ value: 'DMZ.QM3' },
	{ value: 'DMZ.QM4' },
	{ value: 'DMZ.QM5' }
	];

	$scope.result = null;
	$scope.result2 = null;
	$scope.result3 = null;
	$scope.error = null;
	$scope.error2 = null;
	$scope.error3 = null;

	$scope.cpFlag = true;
	$scope.mqFlag = true;
	$scope.userFlag = true;

	//	MQ Management variable
	$scope.mqManagerName = null;
	function getMqmName() {
		var p = InitService.getMqmName().then(function (p) {
			console.log(p);
			$scope.mqManagerName = p.mqmName;
		});
	}
	getMqmName();

	function createEmtryMQCp() {
		var mqCp = { cpId: null, cpName: null, sourceAddress: null, waterLevel: null, mqManagerName: $scope.mqManagerName, legacy: true, spamCheck: true };
		return mqCp;
	}
	$scope.mqCp = createEmtryMQCp();

	//  CP Update variable
	$scope.tempCpId = null;
	$scope.cp = null;
	$scope.cpsaList = [];
	$scope.cpdaList = [];
	$scope.sacRootContext = null;

	function getRootContext() {
		var p = InitService.getSacPath().then(function (p) {
			console.log(p);
			$scope.sacRootContext = p.sacpath;
		});
	}
	getRootContext();

	$scope.createMQ = function (mqManagerName, mqCp) {
		//		//to uppercase
		//		mqCp.cpId = $filter('uppercase')(mqCp.cpId);

		$scope.result = null;
		$scope.result2 = null;
		$scope.result3 = null;
		$scope.error = null;
		$scope.error2 = null;
		$scope.error3 = null;

		//create CP
		mqCp.mqManagerName = mqManagerName;
		console.log(mqCp);

		if ($scope.cpFlag) {

			// disable smslimit check for legacy mq
			if (mqCp.legacy) {
				mqCp.waterLevel = -1;
			}

			CpService.createCP($scope.sacRootContext, mqCp).then(function (response) {
				console.log(response);
				if (response.status = 200) {
					$scope.result = "Create CP Success.";
				} else {
					$scope.error = 'Fail to Create CP:' + mqCp.cpId + ". Response message: " + response.message;
				}
			});
		}


		//create OS user
		if ($scope.userFlag) {
			MqService.createOsUser(mqManagerName, mqCp.cpId).then(function (response) {
				console.log(response);
				if (response.status == 200) {
					$scope.result2 = "Create Os User Success.";
				} else {
					$scope.error2 = 'Fail to Create Os User with CP : ' + $scope.mqCp.cpId + ". Response message: " + response.message;
				}
			});
		}


		//create MQ 
		if ($scope.mqFlag) {
			MqService.createMQ(mqManagerName, mqCp.cpId).then(function (response) {
				console.log(response);
				if (response.status == 200) {
					$scope.result3 = "Create Queue Success.";
				} else {
					$scope.error3 = 'Fail to Create Queue with CP : ' + $scope.mqCp.cpId + ". Response message: " + response.message;
				}
			});
		}


	}

	//	$scope.deleteMQ = function(mqManagerName, mqCp){
	//		//check cp exist or not
	//		CpService.getCP($scope.sacRootContext, mqCp.cpId).then(function(response){
	////			console.log(response);
	//			if(response.success){
	//				//create MQ 
	//				MqService.deleteMQ(mqManagerName, mqCp.cpId).then(function(result){
	//					console.log(result);
	//					if(!result){
	//						$scope.error = 'Fail to delete Queue with CP : ' + $scope.mqCp.cpId;
	//					}
	//				});
	//			}else {
	//				$scope.error = 'Fail to get CP : ' + $scope.mqCp.cpId;
	//			}
	//		});
	//	}

	//	$scope.confirmDeleteMQ = function(mqManagerName, mqCp) {
	//		// Appending dialog to document.body to cover sidenav in
	//		// docs app
	//		var confirm = $mdDialog.confirm().title('Confirm ?')
	//		.textContent("Confirm to delete this Queue?").ok('OK').cancel('Cancel');
	//
	//		$mdDialog.show(confirm).then(function() {
	//			$scope.deleteMQ(mqManagerName, mqCp);
	//		}, function() {
	//			$scope.closeDialog();
	//		});
	//	};


	this.saveCp = function (cp) {
		for (var i = cp.cpsaMap.length - 1; i >= 1; i--) {
			if (!cp.cpsaMap[i].sourceAddress || cp.cpsaMap[i].sourceAddress == '') {
				cp.cpsaMap.splice(i, 1);
			}
		}
		for (var i = cp.cpdaMap.length - 1; i >= 1; i--) {
			if (!cp.cpdaMap[i].destinationAddress || cp.cpdaMap[i].destinationAddress == '') {
				cp.cpdaMap.splice(i, 1);
			}
		}

		angular.forEach(cp.cpsaMap, function (value, key) {
			if (!cp.cpsaMap[key].cpId) {
				cp.cpsaMap[key].cpId = cp.cpId;
			}
		});
		angular.forEach(cp.cpdaMap, function (value, key) {
			if (!cp.cpdaMap[key].cpId) {
				cp.cpdaMap[key].cpId = cp.cpId;
			}
		});

		console.log(cp);

		CpService.updateCP($scope.sacRootContext, cp).then(function (response) {
			console.log(response);
			if (response.status == 200) {
				$scope.result = "Update CP Success.";
				$scope.error = null;
				$scope.closeDialog();
				$scope.$digest();
			} else {
				$scope.result = null;
				$scope.error = 'Fail to update CP : ' + cp.cpId + ". Response message: " + response.message;
			}
		});
	}

	$scope.openUpdateCpDialog = function (tempCpId) {
		$scope.error = null;
		$scope.error2 = null;
		$scope.error3 = null;
		$scope.result = null;
		$scope.result2 = null;
		$scope.result3 = null;

		this.resp = CpService.getCP($scope.sacRootContext, tempCpId).then(function (response) {
			console.log(response);
			if (response.status == 200) {
				$scope.cp = response.data;
				// Record Existing Source Address
				angular.forEach(response.data.cpsaMap, function (item) {
					$scope.cpsaList.push(item.sourceAddress);
				});
				angular.forEach(response.data.cpdaMap, function (item) {
					$scope.cpdaList.push(item.destinationAddress);
				});
				// Open Update Form
				$scope.UpdateCpForm.$setPristine();
				$mdDialog.show({
					controller: 'mqCtrl', contentElement: '#UpdateCpDialog', parent: angular.element(document.body),
					clickOutsideToClose: true, fullscreen: true, multiple: true,
					onRemoving: function (event, removePromise) {
						$scope.closeDialog($scope.cp);
					}
				});
			}
			else if (response.status == 406) {
				$scope.error = tempCpId + ' NOT a MQ CP';
			} else {
				$scope.error = 'Fail to get CP : ' + tempCpId + ". Response message: " + response.message;
				$scope.error2 = null;
				$scope.error3 = null;
				$scope.result = null;
				$scope.result2 = null;
				$scope.result3 = null;
			}
		});
	};

	$scope.addSourceAddr = function (cp) {
		if ($scope.cp.cpsaMap.length <= 49) {
			$scope.cp.cpsaMap.push({});
		}
	};

	$scope.delSourceAddr = function (index) {
		$scope.cp.cpsaMap.splice(index, 1);
		if ($scope.cp.cpsaMap.length < 1) {
			$scope.cp.cpsaMap.push({});
		}
	};

	$scope.addDestinationAddr = function (cp) {
		if ($scope.cp.cpdaMap.length <= 49) {
			$scope.cp.cpdaMap.push({});
		}
	};

	$scope.delDestinationAddr = function (index) {
		$scope.cp.cpdaMap.splice(index, 1);
		if ($scope.cp.cpdaMap.length < 1) {
			$scope.cp.cpdaMap.push({});
		}
	};

	$scope.closeDialog = function (cp) {
		$mdDialog.cancel();
		$scope.cpsaList = [];
		$scope.cpdaList = [];
		cp = $scope.cp;
		for (var i = cp.cpsaMap.length - 1; i >= 1; i--) {
			if (!cp.cpsaMap[i].sourceAddress || cp.cpsaMap[i].sourceAddress == '') {
				cp.cpsaMap.splice(i, 1);
			}
		}
		for (var i = $scope.cp.cpdaMap.length - 1; i >= 1; i--) {
			if (!cp.cpdaMap[i].destinationAddress || cp.cpdaMap[i].destinationAddress == '') {
				cp.cpdaMap.splice(i, 1);
			}
		}
	};


	$scope.refreshMQForm = function () {
		$scope.error = null;
		$scope.error2 = null;
		$scope.error3 = null;
		$scope.result = null;
		$scope.result2 = null;
		$scope.result3 = null;
		$scope.mqCp = createEmtryMQCp();
		$scope.CreateMQForm.$setPristine();
	}

	$scope.refreshCpForm = function () {
		$scope.error = null;
		$scope.error2 = null;
		$scope.error3 = null;
		$scope.result = null;
		$scope.result2 = null;
		$scope.result3 = null;
		$scope.tempCpId = null;
		$scope.UpdateCpForm.$setPristine();
	}

	$scope.isNewCpsa = function (sa) {
		if ($scope.cpsaList.indexOf(sa) !== -1) {
			return true;
		}
		return false;
	}

	$scope.isNewCpda = function (da) {
		if ($scope.cpdaList.indexOf(da) !== -1) {
			return true;
		}
		return false;
	}

	/* Ag-grid config */
	//	var pageSize = 50;
	//	var columnDefs = header();
	//	
	//	$scope.statusFilter = [
	//		{ value : 'A', label : "Active" }, { value : 'I', label : "Inactive" } ];
	//	
	//	function header() {
	//		return [{ headerName : "Cp Id" , field : "cpId", tooltipField : "cpId" },
	//		{ headerName : "Cp Name", field : "cpName", tooltipField : "cpName" },
	//		{ headerName : "Source Address", field : "cpsaMap", cellRenderer : cpsaMapRenderer, suppressSorting : true, suppressFilter : true },
	//		{ headerName : "Status", field : "status", cellRenderer : statusRenderer,
	//			cellClass : statusStyle, filter : OptionsFilter, filterParams : { options : $scope.statusFilter, apply : true } },
	//		{ headerName : "Water Level", field : "smsLimit", tooltipField : "smsLimit"}];
	//	};
	//	
	//	function cpsaMapRenderer(cp) {
	//		var bodyText = null;
	//		if (cp.value.length != 0){
	//			cp.value.forEach(function(value) {
	//				if (bodyText != null){
	//					bodyText = bodyText + ", [" + value.sourceAddress + "]";
	//				}
	//				else{
	//					bodyText = "[" + value.sourceAddress + "]"
	//				}
	//			});
	//			
	//			if (bodyText.length > 40) {
	//				return bodyText.substring(0,40)+"...";
	//			} else {
	//				return bodyText;
	//			}
	//		}
	//		return bodyText;
	//	}
	//	
	//	function statusRenderer(param) {
	//		switch (param.value) {
	//		case 'A':
	//			return 'Active';
	//			break;
	//		case 'I':
	//			return 'Inactive';
	//			break;
	//		default:
	//			return '';
	//			break;
	//		}
	//	}
	//	function statusStyle(param) {
	//		switch (param.value) {
	//		case 'A':
	//			return 'alert-success';
	//			break;
	//		case 'I':
	//			return 'alert-warning';
	//			break;
	//		default:
	//			return '';
	//			break;
	//		}
	//	}
	//	
	//	function flagRenderer(param) {
	//		switch (param.value) {
	//		case false:
	//			return 'false';
	//			break;
	//		case true:
	//			return 'true';
	//			break;
	//		default:
	//			return '';
	//			break;
	//		}
	//	}
	//	
	//	function flagStyle(param) {
	//		switch (param.value) {
	//		case false:
	//			return 'alert-warning';
	//			break;
	//		case true:
	//			return 'alert-success';
	//			break;
	//		default:
	//			return '';
	//			break;
	//		}
	//	}
	//	
	//	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };
	//	
	//	$scope.grid = {
	//		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual',
	//		paginationPageSize : pageSize, dataSource : null, defaultColDef : defaultColDef, enableServerSideSorting : true,
	//		enableServerSideFilter : true, rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ],
	//		debug : false,
	//
	//		onRowClicked : function(event) {
	//			$scope.tempCpId = event.data;
	//			$scope.mqCp.cpId = event.data;
	//		}
	//	};
	//	
	//	$scope.autoFit = function() {
	//		var allColumnIds = [];
	//		columnDefs.forEach(function(columnDef) {
	//			allColumnIds.push(columnDef.field);
	//		});
	//		$scope.grid.columnApi.autoSizeColumns(allColumnIds);
	//	}
	//	
	//	var dataSource = { getRows : function(params) {
	//		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
	//		console.log('asking for ' + params.startRow + ' to ' + params.endRow);
	//		CpService.pageCP(searchPage).then(function(response) {
	//			if (response.success) {
	//				var pageResult = response.data;
	//				if (pageResult.totalSize > 0) {
	//					params.successCallback(pageResult.data, pageResult.totalSize);
	//				}
	//				$scope.autoFit();
	//			}
	//		});
	//	} };
	//
	//	$scope.refreshGrid = function() {
	//		columnDefs = header();
	//		$scope.grid.api.setColumnDefs(columnDefs);
	//		$scope.grid.api.setDatasource(dataSource);
	//		$scope.grid.api.refreshHeader();
	//		$scope.error = null;
	//		$scope.cp = null;
	//		$("#refreshBtn").blur();
	//		
	//	}
	//	
	//	$scope.grid.datasource = dataSource;
	//	
});

