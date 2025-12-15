SAC.controller('Sms.TimetableCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, SmsService, AuthService, GridUtil,DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;
	
	$scope.statusFilter = [
		{ value : 'A', label : $translate('sms.tt.status.A') }, { value : 'I', label : $translate('sms.tt.status.I') } ];

	$scope.onOffFilter = [
		{value : '1', label : $translate('sms.tt.onOff.1') }, { value : '0', label : $translate('sms.tt.onOff.0') }
	];
	
	var columnDefs = header();
		
	function header() {
		return [{ headerName : $translate('sms.tt.field.01'), field : "timeTableId", tooltipField : "timeTableId" },
		{ headerName : $translate("sms.tt.field.02"), field : "timeTableName", tooltipField : "timeTableName" },
		{
			headerName : $translate("sms.tt.field.03"), field : "status", cellRenderer : statusRenderer,
			cellClass : statusStyle, filter : OptionsFilter, filterParams : { options : $scope.statusFilter, apply : true } },
		{ headerName : $translate("sms.tt.field.04"), field : "createBy", tooltipField : "createBy"    }, 
		{ headerName : $translate("sms.tt.field.05"), field : "createDate", tooltipField : "createDate" , filter : 'date' }, 
		{ headerName : $translate("sms.tt.field.06"), field : "updateBy", tooltipField : "updateBy" },
		{ headerName : $translate("sms.tt.field.07"), field : "updateDate", tooltipField : "updateDate" , filter : 'date' },
		{ headerName : $translate("sms.tt.field.08"), field : "version", tooltipField : "version" ,
			filter : NumberFilter, filterParams : {  apply : true } }];
	};
	
	
	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true } };
	
	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});

	$scope.enableEdit = false;
	$scope.Error = null;
	$scope.timetable = null;
	
	function statusRenderer(param) {
		return $translate('sms.tt.status.' + param.value);
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
			if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_05:write")) {
				$('#editBtn').prop('disabled', false);
				$scope.enableEdit = true;
			}
			$scope.$digest();
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
		SmsService.pageTimeTable(searchPage).then(function(response) {
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
		$scope.timetable = null;
		$scope.Error = null;
		$scope.enableEdit = false;
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.Error = null;
		$scope.timetable = null;
		$mdDialog.show({
			controller : 'Sms.TimetableCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body),
			targetEvent : event, clickOutsideToClose : true, fullscreen : true });
	};

	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);
	};
	
	$scope.openUpdateForm = function(timetable) {
		if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_05:write")) {
//			console.log(timetable);
			$scope.UpdateForm.$setPristine();
			$scope.Error = null;
			updateTimetable(timetable);
			getTimeslotDatas();

			$mdDialog.show({
				controller : 'Sms.TimetableCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body),
				clickOutsideToClose : true, fullscreen : true });
		}
	};

	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};
	
	$scope.defValue = '0';
	
	$scope.create = function(timetable) {
		timetable.status = 'A';
		SmsService.createTimeTableWithDefaultValue(timetable, $scope.defValue).then(function(response) {
			if (response.status==200) {
				console.log("Create Timetable Success, Refresh Grid");
				$scope.refreshGrid();
				$scope.closeDialog();
				$scope.$digest();
				
			} else {
				// $scope.Error = 'sms.tt.err.' + response.code;
				if(response.code){
					$scope.Error = 'sms.tt.err.' + response.code;					
				}else{
					$scope.Error = 'sms.tt.err.' + response.status;
				}
			}
		})
	};


	$scope.update = function(tt) {
		if(tt.createDate!=null)
		{
			var tempTime = DateUtil.parserToLocalDateTime(tt.createDate);
			tt.createDate = tempTime;
		}
		if(tt.updateDate!=null)
		{
			tempTime = DateUtil.parserToLocalDateTime(tt.updateDate);
			tt.updateDate = tempTime;
		}
		var timetableContainer ={
				timeTable: tt,
				timetableBody: {tableBody: $scope.tableBody}
		}
//		console.log(timetableContainer);
		
		SmsService.updateTimeTable(timetableContainer).then(function(response) {
			if (response.status==200) {
				console.log("Update Timetable Success, Refresh Grid");
				console.log("Print result");
				console.log(response);
				$scope.refreshGrid();
				$scope.closeDialog();
				$scope.$digest();

			} else {
				// $scope.Error = 'sms.tt.err.' + response.code;
				if(response.code){
					$scope.Error = 'sms.tt.err.' + response.code;					
				}else{
					$scope.Error = 'sms.tt.err.' + response.status;
				}
			}
		})
	};

	$scope.confirmDelete = function(timetable) {
		// Appending dialog to document.body to cover sidenav in
		// docs app
		var confirm = $mdDialog.confirm().title('Confirm ?').ok('OK').cancel('Cancel');

		$mdDialog.show(confirm).then(function() {
			SmsService.deleteTimeTable(timetable).then(function(response) {
				if (response.status==200) {
					$scope.refreshGrid();
				} else {
					$scope.openUpdateForm(timetable);
					// $scope.Error = 'sms.tt.err.' + response.code;
					if(response.code){
						$scope.Error = 'sms.tt.err.' + response.code;					
					}else{
						$scope.Error = 'sms.tt.err.' + response.status;
					}
				}
			})
		}, function() {
			$scope.closeDialog();
		});
	};
	
	/* Timetable grid */
	
	var ttColumnDefs = ttHeader();
	
	
	
	function ttHeader() {
		return [{ headerName : $translate('sms.tt.field.10'), field: "0", width: 90, editable: true, suppressNavigable: true, pinned: 'left', cellEditor: ttRowEditor},
				{ headerName : $translate('sms.tt.field.11'), field: "1", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.12"), field: "2", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.13"), field: "3", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.14"), field: "4", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.15"), field: "5", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.16"), field: "6", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }}, 
				{ headerName : $translate("sms.tt.field.17"), field: "7", width: 90, editable: true, cellRenderer : RefundedCellRenderer, cellEditor: ttCellEditor, 
					cellClassRules: { 'alert-success': function(params) { return params.value === '1'},'alert-warning': function(params) { return params.value === '0'} }},];
	};
	
	//cell editor
	function ttCellEditor() {}
	ttCellEditor.prototype.getGui = function() {
	    return this.eGui;
	};
	ttCellEditor.prototype.getValue = function() {
	    return this.value;
	};
	ttCellEditor.prototype.isPopup = function() {
	    return false;
	};
	ttCellEditor.prototype.init = function(params) {
	    this.value = params.value;
	    tempElement = document.createElement('div');
	    tempElement.innerHTML =
	        '<div>' +
	        	'<button type="button" id="btOn" >On</button>' +
	        	'<button type="button" id="btOff">Off</button>' +
        	'</div>';
	    that = this;
	    ["On","Off"].forEach( function(option) {
	        tempElement.querySelector('#bt'+option).addEventListener('click', function() {
	            if(option == "On"){
	            	that.value = '1';
	            }else if(option == "Off"){
	            	that.value = '0';
	            }
	            params.stopEditing();
	        });
	    });
	    this.eGui = tempElement.firstChild;
	};
	ttCellEditor.prototype.destroy = function() {
	};
	
	//row editor
	function ttRowEditor() {}
	ttRowEditor.prototype.getGui = function() {
	    return this.eGui;
	};
	ttRowEditor.prototype.getValue = function() {
	    return this.value;
	};
	ttRowEditor.prototype.isPopup = function() {
	    return false;
	};
	ttRowEditor.prototype.init = function(params) {
	    this.value = params.value;
	    console.log(params);
	    
	    tempElement = document.createElement('div');
	    tempElement.innerHTML =
	        '<div>' +
	        	'<button type="button" id="btOn" >On</button>' +
	        	'<button type="button" id="btOff">Off</button>' +
        	'</div>';
	    that = this;
	    dataNode = params.node;
	    
	    ["On","Off"].forEach( function(option) {
	        tempElement.querySelector('#bt'+option).addEventListener('click', function() {
	        	console.log($scope.tableBody[dataNode.rowIndex]);
	            if(option == "On"){
	            	//set each data in row = 1
	            	for(i=1;i<=7;i++){
	            		dataNode.setDataValue(i,"1");
	            	}
	            	
	            }else if(option == "Off"){
	            	//set each data in row = 0
	            	for(i=1;i<=7;i++){
		            	dataNode.setDataValue(i,"0");	
	            	}

	            }
	            params.stopEditing();
	        });
	    });
	    this.eGui = tempElement.firstChild;
	};
	ttRowEditor.prototype.destroy = function() {
	};
	
	
	
	function RefundedCellRenderer(params) {
//		console.log(params);
		if (params.value == '0'){
			return "off";
		}
		else{
			return "on";
		}
//		return '<span class="checkbox" ng-true-value="1" ng-false-value="0" ng-model= ' +params.value+ ' ></span>';
		
	}
	
	function RefundedCellStyle(params) {

		switch (params.value) {
		case '1':
//			console.log(params.value);
			return ['alert-success'];
			break;
		case '0':
//			console.log(params.value);
			return ['alert-warning'];
			break;

		default:
			return null;
			break;
		}
	}
	
	$scope.ttGrid = {
			columnDefs : ttColumnDefs, enableColResize : true, enableFilter : false, rowData : null, 
			suppressMovableColumns: true, singleClickEdit: true, stopEditingWhenGridLosesFocus: true,
			enableColResize: false,	suppressTabbing: true, debug: false
	};
	
//	$scope.ttAutoFit = function() {
//		var allColumnIds = [];
//		ttColumnDefs.forEach(function(ttColumnDef) {
//			allColumnIds.push(ttColumnDef.field);
//		});
//		$scope.ttGrid.columnApi.autoSizeColumns(allColumnIds);
//	}
	
	function updateTimetable(tt){
		$scope.timetable = null;
		$scope.timetable = tt;
	}
	
	$scope.tableBody = null;
	function getTimeslotDatas(){
		SmsService.getTimeslotDatas($scope.timetable.timeTableId).then(function(response) {
			if (response.status==200) {
				var result = response.data;
//				console.log(result);
				$scope.tableBody = result.tableBody;
				$scope.ttGrid.api.setRowData($scope.tableBody);
//				$scope.ttAutoFit();
			} 
	
		});
	}  
	
	
	
});