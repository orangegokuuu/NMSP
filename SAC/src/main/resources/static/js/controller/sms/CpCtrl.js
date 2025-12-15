SAC.controller('Sms.CpCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, SmsService, AuthService, GridUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;
	
	$scope.statusFilter = [
		{ value : 'A', label : $translate('cp.status.A') }, { value : 'I', label : $translate('cp.status.I') } ];

	$scope.flagFilter = [
		{ value : false, label : $translate('cp.flag.false')}, { value : true , label : $translate('cp.flag.true') }];
	
	$scope.cpTypeFilter = [
		{ value : '1', label : $translate('cp.type.1')}, { value : '2' , label : $translate('cp.type.2') }, { value : '3' , label : $translate('cp.type.3') }];
	
	$scope.cpZoneSet = [
		{ value : 0, label : $translate('cp.zone.0')}, { value : 1 , label : $translate('cp.zone.1') }];
	
	$scope.mqCpZoneSet = [
		{ value : 11 , label : $translate('cp.zone.11') }, { value : 12 , label : $translate('cp.zone.12')}, 
		{ value : 13 , label : $translate('cp.zone.13') }, { value : 14 , label : $translate('cp.zone.14')}, 
		{ value : 15 , label : $translate('cp.zone.15') }, { value : 16 , label : $translate('cp.zone.16')}];
	
	$scope.priorityList = [
		{ value : 0, label : $translate('sms.cp.priority.0')}, { value : 1 , label : $translate('sms.cp.priority.1') }, { value : 2 , label : $translate('sms.cp.priority.2') }];
	
	$scope.unitList = [
		 { value : 'm' , label : $translate('cp.time.unit.m') }, { value : 'h' , label : $translate('cp.time.unit.h') }];
	
	
	var columnDefs = header();
		
	function header() {
		return [{ headerName : $translate('sms.cp.field.01'), field : "cpId", tooltipField : "cpId" },
		{ headerName : $translate("sms.cp.field.02"), field : "cpName", tooltipField : "cpName" },
		{ headerName : $translate("sms.cp.field.03"), field : "contactTel", tooltipField : "contactTel" },
		{ headerName : $translate("sms.cp.field.04"), field : "contactEmail", tooltipField : "contactEmail" },
		{ headerName : $translate("sms.cp.field.05"), field : "cpsaMap", cellRenderer : cpsaMapRenderer, suppressSorting : true, suppressFilter : true },
		{ headerName : $translate("sms.cp.field.07"), field : "status", cellRenderer : statusRenderer,
			cellClass : statusStyle, filter : OptionsFilter, filterParams : { options : $scope.statusFilter, apply : true } },
		{ headerName : $translate("sms.cp.field.06"), field : "cpType", tooltipField : "cpType" ,  cellRenderer : typeRenderer,
		    filter : OptionsFilter, filterParams : { options : $scope.cpTypeFilter, apply : true } },
	    { headerName : $translate("sms.cp.field.33"), field : "throughPSA",  cellRenderer : flagRenderer,
			cellClass : flagStyle, filter : BooleanOptionsFilter, filterParams : { options : $scope.flagFilter, apply : true }}, 
		{ headerName : $translate("sms.cp.field.08"), field : "spamCheckFl",  cellRenderer : flagRenderer,
			cellClass : flagStyle, filter : BooleanOptionsFilter, filterParams : { options : $scope.flagFilter, apply : true }}, 
		{ headerName : $translate("sms.cp.field.09"), field : "blacklistCheckFl",  cellRenderer : flagRenderer,
			cellClass : flagStyle, filter : BooleanOptionsFilter, filterParams : { options : $scope.flagFilter, apply : true }},
			{ headerName : $translate("sms.cp.field.30"), field : "drRequestFl",  cellRenderer : flagRenderer,
			cellClass : flagStyle, filter : BooleanOptionsFilter, filterParams : { options : $scope.flagFilter, apply : true }},
		{ headerName : $translate("sms.cp.field.10"), field : "smsLimit", tooltipField : "smsLimit",
			filter : NumberFilter, filterParams : {  apply : true }},
		{ headerName : $translate("sms.cp.field.11"), field : "apiVersion", tooltipField : "apiVersion"}]
	};
	
	function cpsaMapRenderer(cp) {
		var bodyText = null;
//		console.log(cp);
		if (cp.value){
			cp.value.forEach(function(value) {
				if (bodyText != null){
					bodyText = bodyText + ", [" + value.sourceAddress + "]";
				}
				else{
					bodyText = "[" + value.sourceAddress + "]"
				}
			});

			if (bodyText != null && bodyText.length > 40) {
				return bodyText.substring(0,40)+"...";
			} else {
				return bodyText;
			}
		}
		return bodyText;
	}
	
	function statusRenderer(param) {
		return $translate('cp.status.' + param.value);
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
	
	function flagRenderer(param) {
		return $translate('cp.flag.' + param.value);
	}
	
	function flagStyle(param) {
		switch (param.value) {
		case false:
			return 'alert-warning';
			break;
		case true:
			return 'alert-success';
			break;
		default:
			return '';
			break;
		}
	}
	
	function typeRenderer(param) {
		return $translate('cp.type.' + param.value);
	}
	
	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };
	
	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	$scope.enableEdit = false;
	$scope.Error = null;
	$scope.showSA = false;
	$scope.showDA = false;
	$scope.cp = null;
	$scope.unbindedCp = null;	
	
	function clearUnrelatedField(cp){
		switch (cp.cpType){
		case '1': // HTTP
			// remove unselected queue dr limit
			if(cp.queryDrLimitUnit == 'h'){
				cp.queryDrMinLimit = -1;
			} else {
				cp.queryDrHrLimit = -1;
			}
			
			// remove MQ field
			cp.mqReqQName = null;
			cp.mqRespQName = null;
			cp.prepaidFl = false;
			cp.legacy = false;

			console.log(cp);
			break;
		case '2': // MQ
			// remove HTTP field
			cp.daLimit = null;
			cp.pushDrUrl = null;
			cp.deliverSmUrl = null;
			cp.timeTableId = null;
			
			// restrict hiddien field value
			cp.pushDrFl = true;
			cp.moSmsFl = true;
			
			break;
		case '3': // File
			// remove HTTP field
			cp.daLimit = null;
			cp.pushDrUrl = null;
			cp.deliverSmUrl = null;
			cp.timeTableId = null;
			break;
		default:
			break;
		} 
		return cp;
	}
	
	function clearEmptyField(cp){
		if(cp.pushDrUrl == "")
			cp.pushDrUrl = null;
		if(cp.deliverSmUrl == "")
			cp.deliverSmUrl = null;
		return cp;
	}
	
	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual',
		paginationPageSize : pageSize, dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true,
		enableServerSideFilter : true, rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ],
		debug : false,

		onRowDoubleClicked : function(event) {
			SmsService.getCP(event.data.cpId).then(function(response){
				$scope.openUpdateForm(response);
			});
//			$scope.openUpdateForm(event.data);
		}, 
		onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_01:write")) {
				$('#editBtn').prop('disabled', false);
				$scope.enableEdit = true;
			}
			$scope.cp = event.data;
			$scope.unbindedCp = event.data;
			$scope.showSA = true;
			$scope.showDA = true;
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

	var dataSource = { getRows : function(params) {
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		console.log(searchPage);
		console.log('asking for ' + params.startRow + ' to ' + params.endRow);
		SmsService.pageCP(searchPage).then(function(response) {
			console.log(response);
			if (response.status=='200') {
				var pageResult = response.data;
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
		$scope.cp = null;
		$scope.unbindedCp = null;
		$scope.showSA = false;
		$scope.showDA = false;
		$("#refreshBtn").blur();
		
	}
	
	$scope.grid.datasource = dataSource;
	
	function getEmptyCP(){
		var promise = SmsService.getEmptyCP().then(function(response) {
//			console.log(response.data);
			return response.data;			
		});
		return promise;
	}
	
	
	function getTimetableList(){
		$scope.timetableList = null;
		var promise = SmsService.getTimetableList().then(function(response){
			// console.log(response);
			return response;
		}).then(function(data){
//			console.log(data);
			tempData =[];
			for(i=0;i<data.length;i++){
				tempData.push({'timeTableId':data[i][0], 'timeTableName': data[i][1]});
			}
			$scope.timetableList = tempData;
//			console.log($scope.timetableList);
		});
	}	
	getTimetableList();
	
 
	var _timeout;
	$scope.searchTextChange = function() {
//	   if(_timeout){ //if there is already a timeout in process cancel it
//		  $timeout.cancel(_timeout);
//	   }
//	   console.log('Getting Timetable liasdasdasst...');
//	   _timeout = $timeout(function(){
//		   console.log('Getting Timetable list...');
//		   getTimetableList();
//		   _timeout = null;
//	   }, 1000);
  }

    $scope.selectedItemChange = function(item) {
    	if(item!=null){
//	    	console.log('Item changed to ' + JSON.stringify(item.timeTableId));
	    	$scope.cp.timeTableId = item.timeTableId;
    	}
    }
    
    $scope.querySearch = function(query) {
        var results = query ? $scope.timetableList.filter( createFilterFor(query) ) : $scope.timetableList, 
        		deferred;
        return results;
    }
    
 
    
    /**
     * Create filter function for a query string
     */
    function createFilterFor(query) {
        return function filterFn(item) {
          return (item.timeTableId.indexOf(query) === 0)||
          (item.timeTableName.indexOf(query) === 0);
        };

    }

	// set default value
	var emptyCP = null;
	getEmptyCP().then(function(cp){
		cp.daLimit = null;
		cp.smsLimit = null;
		cp.smsLimitUnit = 'h';
		cp.queryDrLimit = null;
		cp.queryDrLimitUnit = 'h';
		cp.queryDrHrLimit = -1;
		cp.queryDrMinLimit = -1;
		emptyCP = cp;
	});
	
	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.showSA = false;
		$scope.showDA = false;
		$scope.Error = null;
		$scope.cp = angular.copy(emptyCP);
		$scope.cp.cpsaMap.push({});
		getTimetableList();
//		console.log($scope.cp);
		
		$mdDialog.show({
			controller : 'Sms.CpCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body),
			targetEvent : event, clickOutsideToClose : true, fullscreen : true });
	};

	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);

	};
	
	$scope.openUpdateForm = function(cp) {
		if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_01:write")) {
			$scope.UpdateForm.$setPristine();
			$scope.Error = null;
			$scope.cp = null;
			$scope.cp = angular.copy(cp.data);
			
			console.log($scope.cp);
			
			getTimetableList();
			$mdDialog.show({
				controller : 'Sms.CpCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true , multiple: true, skipHide:true});
		}
	};

	$scope.openUpdateSAForm = function() {
		$scope.UpdateSAForm.$setPristine();
		$scope.Error = null;
		if ($scope.cp.cpsaMap.length == 0){
			$scope.cp.cpsaMap.push({});
		}

		$mdDialog.show({
			controller : 'Sms.CpCtrl', contentElement : '#UpdateSADialog', parent : angular.element(document.body),
			clickOutsideToClose : true, fullscreen : true , multiple: true , skipHide:true,  
			onRemoving: function (event, removePromise) {
				$scope.closeSADialog($scope.cp);
	        }
		});
 
	};
	
	$scope.openUpdateDAForm = function() {
		$scope.UpdateDAForm.$setPristine();
		$scope.Error = null;
		if ($scope.cp.cpdaMap.length == 0){
			$scope.cp.cpdaMap.push({});
		}

		$mdDialog.show({
			controller : 'Sms.CpCtrl', contentElement : '#UpdateDADialog', parent : angular.element(document.body),
			clickOutsideToClose : true, fullscreen : true , multiple: true , skipHide:true,  
			onRemoving: function (event, removePromise) {
				$scope.closeDADialog($scope.cp);
	        }
		});
 
	};
	
	$scope.addSourceAddr = function(cp) {
		if ($scope.cp.cpsaMap.length <= 49){
			$scope.cp.cpsaMap.push({});
		}
	};
	
	$scope.delSourceAddr = function(index) {
		$scope.cp.cpsaMap.splice(index, 1);
		if ($scope.cp.cpsaMap.length < 1){
			$scope.cp.cpsaMap.push({});
		}
	};
	
	$scope.addDestinationAddr = function(cp) {
		if ($scope.cp.cpdaMap.length <= 49){
			$scope.cp.cpdaMap.push({});
		}
	};
	
	$scope.delDestinationAddr = function(index) {
		$scope.cp.cpdaMap.splice(index, 1);
		if ($scope.cp.cpdaMap.length < 1){
			$scope.cp.cpdaMap.push({});
		}
	};
	
	
	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};
	
	$scope.closeSADialog = function(cp) {
		for (var i = cp.cpsaMap.length - 1; i >= 1; i--) {
		    if (!cp.cpsaMap[i].sourceAddress||cp.cpsaMap[i].sourceAddress=='') {
		    	cp.cpsaMap.splice(i, 1);
		    }
		}
	};
	
	$scope.closeDADialog = function(cp) {
		for (var i = cp.cpdaMap.length - 1; i >= 1; i--) {
		    if (!cp.cpdaMap[i].destinationAddress||cp.cpdaMap[i].destinationAddress=='') {
		    	cp.cpdaMap.splice(i, 1);
		    }
		}
	};
	
	$scope.create = function(cp) {
		cp.status = 'A';
		cp = clearUnrelatedField(cp);
		cp = clearEmptyField(cp);
		if(cp.cpType == '2'){
			cp.cpId = $filter('uppercase')(cp.cpId);
		}
		// map cpId
		angular.forEach(cp.cpsaMap, function(value, key){
			cp.cpsaMap[key].cpId = cp.cpId;        	 
	    });
		
		angular.forEach(cp.cpdaMap, function(value, key){
			cp.cpdaMap[key].cpId = cp.cpId;        	 
	    });
		console.log(cp);
		
		SmsService.createCP(cp).then(function(response) {
			if (response.status==201) {
//				console.log($scope.cp);
				console.log("Create CP Success, Refresh Grid");
				$scope.showSA = true;
				$scope.showDA = true;
				$scope.cp = cp; 
				$scope.refreshGrid();
				
				$scope.closeDialog();
				$scope.$digest();
				
				$scope.openUpdateSAForm();
				
			} else {
				// $scope.Error = 'sms.cp.err.' + response.code;
				if(response.code){
					$scope.Error = 'sms.cp.err.' + response.code;					
				}else{
					$scope.Error = 'sms.cp.err.' + response.status;
				}
			}
		})
	};

//	$scope.cpTypeChange = function(cpType) {
////		console.log(cpType);
//		switch(cpType){
//		case '2':
//			$scope.cp.drRequestFl = true;
//			break;
//		case 2:
//			$scope.cp.drRequestFl = true;
//			break;
//		default:
//			break;
//		}
//	}
	
	$scope.cpTypeChange = function(cpType) {
	//	console.log(cpType);
		switch(cpType){
			case '2':
				$scope.cp.apiVersion = '1';
				break;
			case 2:
				$scope.cp.apiVersion = '1';
				break;
			default:
				break;
		}
	}

	$scope.update = function(cp) {
		cp = clearUnrelatedField(cp);
		cp = clearEmptyField(cp);
		
		if(cp.cpType == '2'){
			cp.cpId = $filter('uppercase')(cp.cpId);
		}
		
		// map cpId
		angular.forEach(cp.cpsaMap, function(value, key){
			if(!cp.cpsaMap[key].cpId){
				cp.cpsaMap[key].cpId = cp.cpId;				
			}
	    });
		
		angular.forEach(cp.cpdaMap, function(value, key){
			if(!cp.cpdaMap[key].cpId){
				cp.cpdaMap[key].cpId = cp.cpId;				
			}
	    });
		
		SmsService.updateCP(cp).then(function(response) {
			if (response.status='200') {
				console.log("Update CP Success, Refresh Grid");
				$scope.showSA = true;
				$scope.showDA = true;
				$scope.cp = cp;
				$scope.refreshGrid();
				$scope.closeDialog();
				$scope.$digest();
				
			} else {
				// $scope.Error = 'sms.cp.err.' + response.code;
				if(response.code){
					$scope.Error = 'sms.cp.err.' + response.code;					
				}else{
					$scope.Error = 'sms.cp.err.' + response.status;
				}
			}
		});
	};

	$scope.confirmDelete = function(cp) {
		// Appending dialog to document.body to cover sidenav in
		// docs app
		var confirm = $mdDialog.confirm().title('Confirm ?')
		.textContent("OA(s) aligning with " + cp.cpId + " would also be deleted. Are you sure?").ok('OK').cancel('Cancel');

		$mdDialog.show(confirm).then(function() {
			SmsService.deleteCP(cp).then(function(response) {
				if (response.status==200) {
					$scope.refreshGrid();
				} else {
					// $scope.Error = 'sms.cp.err.' + response.code;
					if(response.code){
						$scope.Error = 'sms.cp.err.' + response.code;					
					}else{
						$scope.Error = 'sms.cp.err.' + response.status;
					}
					$scope.openUpdateForm(cp);
				}
			})
		}, function() {
			$scope.closeDialog();
		});
	};
});