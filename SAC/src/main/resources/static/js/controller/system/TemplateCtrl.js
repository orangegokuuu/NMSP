SAC.controller('System.TemplateCtrl', function($scope, $rootScope, $filter, $mdDialog, $state, SystemService, AuthService, GridUtil) {
	var $translate = $filter('translate');
	var pageSize = 50;

	var columnDefs = header();
	
	function header() {
		return [{ headerName : $translate('sys.param.msg.03'), field : "messageType", tooltipField : "messageType"},
		{ headerName : $translate("sys.param.msg.02"), field : "messageId", tooltipField : "messageId" },
		{ headerName : $translate("sys.param.msg.04"), field : "messageBody", cellRenderer : titleRenderer, suppressSorting : true, suppressFilter : true  },
		{ headerName : $translate("sys.param.msg.05"), field : "messageBody", cellRenderer : bodyRenderer, suppressSorting : true, suppressFilter : true}, 
		{ headerName : $translate("sys.param.msg.07"), field : "createDate", tooltipField : "createDate", filter : 'date' },
		{ headerName : $translate("sys.param.msg.06"), field : "createBy", tooltipField : "createBy" },
		{ headerName : $translate("sys.param.msg.09"), field : "updateDate", tooltipField : "updateDate", filter : 'date' },
		{ headerName : $translate("sys.param.msg.08"), field : "updateBy", tooltipField : "updateBy" }];
	}

	var defaultColDef = { minWidth : 70, filter : StringFilter, filterParams : { newRowsAction : 'keep', apply : true }, editable : false };

	function titleRenderer(param) {
//		console.log(param);
		return param.value[$rootScope.userSession.locale].name;
	}
	function bodyRenderer(param) {
		var bodyText = param.value[$rootScope.userSession.locale].bodyText;
		
		if (bodyText != null && bodyText.length > 50) {
			return bodyText.substring(0,50)+"...";
		} else {
			return bodyText;
		}
	}
	
	$scope.$on('Event:LangChange', function(event, lang) {
		$scope.refreshGrid();
	});
	
	$scope.grid = {
		columnDefs : columnDefs, enableColResize : true, enableFilter : true, rowModelType : 'virtual', paginationPageSize : pageSize,
		dataSource : dataSource, defaultColDef : defaultColDef, enableServerSideSorting : true, enableServerSideFilter : true,
		rowSelection : 'single', animateRows : true, sortingOrder : [ 'desc', 'asc' ], debug : false,

		onRowDoubleClicked : function(event) {
			$scope.openUpdateForm(event.data);
		}, onRowClicked : function(event) {
			if (AuthService.havePrivilege($scope.userSession.rights, "SYSTEM_02:write")) {
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
	
	$scope.mTypes = [{ code : 'SMS', text : 'SMS' }, { code : 'EMAIL', text : 'EMAIL' } ];
	$scope.toolbarSmall = [['h1','h2','h3'],['bold','italics','underline'],['undo','redo','insertLink']];
	$scope.toolbarSMS = [['charcount','wordcount']];
	
	$scope.showCreateForm = function(event) {
		$scope.CreateForm.$setPristine();
		$scope.template = {};
		var messageBody = {'en' : {pk:{locale : 'en', messageId : null}}, 'zh-TW' : {pk:{locale : 'zh-TW', messageId : null}} };
		$scope.template.messageBody = messageBody;
		
		$mdDialog.show({
			controller : 'System.TemplateCtrl', contentElement : '#CreateDialog', parent : angular.element(document.body), targetEvent : event,
			clickOutsideToClose : true, fullscreen : true });
	};
	
	$scope.showUpdateForm = function(event) {
		var selected = $scope.grid.api.getSelectedRows()[0];
		$scope.openUpdateForm(selected);
	};
	$scope.openUpdateForm = function(param) {
		if (AuthService.havePrivilege($scope.userSession.rights, "SYSTEM_02:write")) {
			console.log(param);
			$scope.template = param;
			$mdDialog.show({
				controller : 'System.TemplateCtrl', contentElement : '#UpdateDialog', parent : angular.element(document.body), 
				clickOutsideToClose : true, fullscreen : true });
		}
	};
	
	function decodeHtml(html) {
	    var txt = document.createElement("textarea");
	    txt.innerHTML = html;
//	    console.log(txt.value);
	    return txt.value;
	}
	
	
	$scope.create = function(template) {
		template.messageBody['en'].pk.messageId = template.messageId;
		template.messageBody['zh-TW'].pk.messageId = template.messageId;
//		console.log(template);
		SystemService.createTemplate(template).then(function(response) {
			if (response.status==200) {
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'sys.msg.err.' + response.code;
				if(response.code){
					$scope.Error = 'sys.msg.err.' + response.code;					
				}else{
					$scope.Error = 'sys.msg.err.' + response.status;
				}
			}
		})
	};
	
	$scope.update = function(template) {
		SystemService.updateTemplate(template).then(function(response) {
			if (response.status==200) {
				$scope.refreshGrid();
				$scope.closeDialog();
			} else {
				// $scope.Error = 'sys.msg.err.' + response.code;
				if(response.code){
					$scope.Error = 'sys.msg.err.' + response.code;					
				}else{
					$scope.Error = 'sys.msg.err.' + response.status;
				}
			}
		});
	};

	$scope.closeDialog = function() {
		$mdDialog.cancel();
		$scope.Error = null;
	};
	
	var dataSource = { getRows : function(params) {
		var searchPage = GridUtil.makePageSearch(params.filterModel, params.sortModel, pageSize, params.startRow);
		SystemService.pageTemplate(searchPage).then(function(response) {
			if (response.status=='200') {
				var pageResult = response.data;
				console.log(pageResult);
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

});