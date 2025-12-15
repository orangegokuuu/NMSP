SAC.controller('Sms.RecordCtrl', function ($scope, $rootScope, $http, $filter, $mdDialog, SmsService, AuthService, GridUtil, DateUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var statusFilter = [
		{ value: 'SUCCESS', label: $translate('sms.status.SUCCESS') },
		{ value: 'FAIL', label: $translate('sms.status.FAIL') },
		{ value: 'FAULT', label: $translate('sms.status.FAULT') }];

	var typeFilter = [
		{ value: '1', label: 'HTTP' },
		{ value: '2', label: 'MQ' },
		{ value: '3', label: 'File' }];

	var DRstatusFilter = [
		{ value: 'ENROUTE', label: $translate('sms.status.ENROUTE') },
		{ value: 'DELIVRD', label: $translate('sms.status.DELIVRD') },
		{ value: 'EXPIRED', label: $translate('sms.status.EXPIRED') },
		{ value: 'DELETED', label: $translate('sms.status.DELETED') },
		{ value: 'UNDELIV', label: $translate('sms.status.UNDELIV') },
		{ value: 'ACCEPTD', label: $translate('sms.status.ACCEPTD') },
		{ value: 'UNKNOWN', label: $translate('sms.status.UNKNOWN') },
		{ value: 'REJECTD', label: $translate('sms.status.REJECTD') }];

	var columnDefs = header();

	function header() {
		return [{ headerName: $translate('sms.hist.field.38'), field: "wsMsgId", tooltipField: "wsMsgId" },
		{ headerName: $translate('sms.hist.field.01'), field: "sysId", tooltipField: "sysId" },
		{ headerName: $translate("sms.hist.field.02"), field: "da", tooltipField: "da" },
		{ headerName: $translate("sms.hist.field.03"), field: "oa", tooltipField: "oa" },
		{
			headerName: $translate("sms.hist.field.04"), field: "smsSourceType", cellRenderer: typeRenderer, filter: OptionsFilter,
			filterParams: { options: typeFilter, apply: true }
		},
		{ headerName: $translate("sms.hist.field.05"), field: "acceptDate", tooltipField: "acceptDate", filter: 'date' },
		{
			headerName: $translate("sms.hist.field.06"), field: "acceptStatus", cellRenderer: statusRenderer,
			cellClass: statusStyle
		},
		{
			headerName: $translate("sms.hist.field.11"), field: "totalSeg", tooltipField: "totalSeg",
			filter: NumberFilter, filterParams: { apply: true }
		}];
	};

	var defaultColDef = { minWidth: 70, filter: StringFilter, filterParams: { newRowsAction: 'keep', apply: true } };


	$scope.$on('Event:LangChange', function (event, lang) {
		$scope.refreshGrid();
	});

	$scope.Error = null;

	function typeRenderer(param) {
		//console.log(param);
		if (param.data != null) {
			switch (param.data.smsSourceType) {
				case '1':
					return 'HTTP';
					break;
				case '2':
					return 'MQ';
					break;
				case '3':
					return 'File';
					break;
				default:
					return null;
					break;
			}
		}
	}

	function statusRenderer(param) {
		return $translate('sms.status.' + param.value);
	}

	function statusStyle(param) {
		switch (param.value) {
			case 'SUCCESS':
				return 'alert-success';
				break;
			case '0000':
				return 'alert-success';
				break;
			default:
				return 'alert-danger';
				break;
		}
	}

	//     $scope.addSampleData = function(){
	//    	console.log("adding sample data");
	//		var p = SmsService.addSampleSmsRecord().then(function(response){
	//			p = response.data;
	//			return p;
	//		});
	//		if (p!=null)
	//			console.log(p);
	//	}


	function updateDetail(data) {
		$scope.detail = data;
		$scope.$digest();
		//		console.log($scope.detail);
	}

	$scope.grid = {
		columnDefs: columnDefs, enableColResize: true, enableFilter: true, rowModelType: 'virtual',
		paginationPageSize: pageSize, dataSource: dataSource, defaultColDef: defaultColDef, enableServerSideSorting: true,
		enableServerSideFilter: true, rowSelection: 'single', animateRows: true, sortingOrder: ['desc', 'asc'],
		debug: false, infiniteInitialRowCount: 1, getRowNodeId: function (item) {
			return item.id;
		},

		onRowDoubleClicked: function (event) {
			$scope.openUpdateForm(event.data);
		},
		onRowClicked: function (event) {
			//console.log(event);
			updateDetail(event.data);
			updateDateSource();
		}
	};

	$scope.autoFit = function () {
		var allColumnIds = [];
		columnDefs.forEach(function (columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.grid.columnApi.autoSizeColumns(allColumnIds);

	}

	var dataSource = {
		rowCount: null, // behave as infinite scroll	
		getRows: function (params) {
			console.log(params);
			var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
			console.log(searchPage);
			console.log('asking for ' + params.startRow + ' to ' + params.endRow);
			SmsService.pageSmsRecord(searchPage).then(function (response) {
				console.log(response);
				if (response.status == '200') {
					var pageResult = response.data;
					pageResult.data.forEach(function (item) {
						if(item.acceptDate!=null){
							var formattedDate = DateUtil.transDate(new Date(item.acceptDate), "yyyy/MM/dd hh:mm:ss");
							item.acceptDate = formattedDate;
						}
						if(item.createDate!=null){
							var formattedDate = DateUtil.transLocalDateTime(item.createDate);
							item.createDate = formattedDate;
						}
						if(item.updateDate!=null){
							var formattedDate = DateUtil.transLocalDateTime(item.updateDate);
							item.updateDate = formattedDate;
						}
						
						if(item.subs.length>0)
						{
							if(item.subs[0].submitDate != null);{
								var formattedDate = DateUtil.transDate(new Date(item.subs[0].submitDate), "yyyy/MM/dd hh:mm:ss");
								item.subs[0].submitDate = formattedDate;
							}
							if(item.subs[0].createDate != null);{
								var formattedDate = DateUtil.transDate(new Date(item.subs[0].createDate), "yyyy/MM/dd hh:mm:ss");
								item.subs[0].createDate = formattedDate;
							}
							if(item.subs[0].deliverDate != null);{
								var formattedDate = DateUtil.transDate(new Date(item.subs[0].deliverDate), "yyyy/MM/dd hh:mm:ss");
								item.subs[0].deliverDate = formattedDate;
							}
						}
					});
					if (pageResult.totalSize > 0) {
						params.successCallback(pageResult.data, pageResult.totalSize);
					}
					$scope.autoFit();
				}
			});
		}
	};

	$scope.refreshGrid = function () {
		columnDefs = header();
		$scope.detail = null;
		$scope.grid.api.setColumnDefs(columnDefs);
		$scope.grid.api.setDatasource(dataSource);
		$scope.grid.api.refreshHeader();
		$scope.autoFit();
		$scope.Error = null;
		$("#refreshBtn").blur();
	}

	$scope.grid.datasource = dataSource;

	$scope.exportCSV = function () {
		var searchPage = GridUtil.makePageSearch($scope.grid.api.getFilterModel(), $scope.grid.api.getSortModel(), 0, 0);
		SmsService.pageSmsRecord(searchPage).then(function (response) {
			if (response.status == 200) {
				var pageResult = response.data;
				pageResult.data.forEach(function (item) {
					if(item.acceptDate!=null){
						var formattedDate = DateUtil.transDate(new Date(item.acceptDate), "yyyy/MM/dd hh:mm:ss");
						item.acceptDate = formattedDate;
					}
					if(item.createDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.createDate);
						item.createDate = formattedDate;
					}
					if(item.updateDate!=null){
						var formattedDate = DateUtil.transLocalDateTime(item.updateDate);
						item.updateDate = formattedDate;
					}
					
					if(item.subs.length>0)
					{
						if(item.subs[0].submitDate != null);{
							var formattedDate = DateUtil.transDate(new Date(item.subs[0].submitDate), "yyyy/MM/dd hh:mm:ss");
							item.subs[0].submitDate = formattedDate;
						}
						if(item.subs[0].createDate != null);{
							var formattedDate = DateUtil.transDate(new Date(item.subs[0].createDate), "yyyy/MM/dd hh:mm:ss");
							item.subs[0].createDate = formattedDate;
						}
						if(item.subs[0].deliverDate != null);{
							var formattedDate = DateUtil.transDate(new Date(item.subs[0].deliverDate), "yyyy/MM/dd hh:mm:ss");
							item.subs[0].deliverDate = formattedDate;
						}
					}
				});
				alasql('SELECT * REMOVE subs INTO csv("sms_record.csv",{separator:","}) FROM ?', [pageResult.data]);
			}
		});
		$("#exportBtn").blur();
	}

	$scope.openUpdateForm = function (smsRecord) {
		if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_02:read")) {
			$scope.UpdateForm.$setPristine();
			$scope.smsRecord = smsRecord;
			//console.log($scope.smsRecord);
			$mdDialog.show({
				controller: 'Sms.SmsCtrl', contentElement: '#UpdateDialog', parent: angular.element(document.body),
				clickOutsideToClose: true, fullscreen: true
			});
		}
	};

	$scope.closeDialog = function () {
		$mdDialog.cancel();
		$scope.Error = null;
	};

	/* Grid view for SmsRocordSub  */

	var columnDefsSub = header2();




	function DRstatusRenderer(param) {
		return $translate('sms.status.' + param.value);
	}

	function header2() {
		return [{ headerName: $translate('sms.hist.field.38'), field: "wsMsgId", tooltipField: "wsMsgId", suppressFilter: true },
		{ headerName: $translate("sms.hist.field.39"), field: "segNum", tooltipField: "segNum" },
		{ headerName: $translate("sms.hist.field.40"), field: "submitDate", tooltipField: "submitDate", suppressFilter: true },
		//		{ headerName : $translate("sms.hist.field.41"), field : "submitStatus", cellRenderer : statusRenderer,
		//			cellClass : statusStyle, suppressFilter : true },
		{ headerName: $translate("sms.hist.field.41"), field: "submitStatus", tooltipField: "submitStatus" },
		{ headerName: $translate("sms.hist.field.42"), field: "deliverDate", tooltipField: "acceptDate", suppressFilter: true },
		//		{ headerName : $translate("sms.hist.field.43"), field : "deliverStatus", cellRenderer : DRstatusRenderer,
		//			cellClass : dRStatusStyle, suppressFilter : true },
		{ headerName: $translate("sms.hist.field.43"), field: "deliverStatus", tooltipField: "deliverStatus" }

		];
	};

	var dataSourceSub = [];

	$scope.gridSub = {
		columnDefs: columnDefsSub, enableColResize: true, enableFilter: true,
		rowData: dataSourceSub, defaultColDef: defaultColDef,
		rowSelection: 'single', animateRows: true, sortingOrder: ['desc', 'asc'],
		debug: false,

		onRowDoubleClicked: function (event) {
			//					$scope.openUpdateForm(event.data);
		},
		onRowClicked: function (event) {
			//					//console.log(event);
			//					updateDetail(event.data);
		}
	};

	//	function dRStatusStyle(param) {
	//		switch (param.value) {
	//		case 'DELIVRD':
	//			return 'alert-success';
	//			break;
	//		case 'ACCEPTD':
	//			return 'alert-success';
	//			break;
	//		case 'ENROUTE':
	//			return 'alert-warning';
	//			break;
	//		default:
	//			return 'alert-danger';
	//			break;
	//		}
	//	}


	function updateDateSource() {
		if ($scope.detail) {
			if ($scope.detail.subs) {
				dataSourceSub = $scope.detail.subs;
				$scope.gridSub.api.setRowData(dataSourceSub);
				//				console.log(dataSourceSub);				
			}
		}
		$scope.autoFitSub();
	}


	$scope.autoFitSub = function () {
		var allColumnIds = [];
		columnDefsSub.forEach(function (columnDef) {
			allColumnIds.push(columnDef.field);
		});
		$scope.gridSub.columnApi.autoSizeColumns(allColumnIds);

	}

	$scope.refreshGridSub = function () {
		columnDefsSub = header2();
		$scope.gridSub.api.setColumnDefs(columnDefsSub);
		$scope.gridSub.api.setRowData(dataSourceSub);
		$scope.gridSub.api.refreshHeader();
		$scope.autoFitSub();
		$("#refreshBtn").blur();
	}

});