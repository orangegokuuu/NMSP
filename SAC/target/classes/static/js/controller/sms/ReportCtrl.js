SAC.controller('Sms.ReportCtrl', function($scope, $rootScope, $http, $filter, $mdDialog, $location, $sce, $state, SmsService, AuthService) {
	var $translate = $filter('translate');
	
	$scope.datePeriodList = [
		{ code : 'H', text : $translate('sms.report.dp.' + 'H') }, 
		{ code : 'D', text : $translate('sms.report.dp.' + 'D') }];
	// 	{ code : 'M', text : $translate('sms.report.dp.' + 'M') } 
	
	$scope.reportTypeList = [
		{ code : 'clientMtSummary', text : $translate('sms.report.type.' + 'MtSummary') }, 
		{ code : 'clientMtDetail', text : $translate('sms.report.type.' + 'MtDetail') }, 
		{ code : 'clientMtSmsFailure', text : $translate('sms.report.type.' + 'MtSmsFailure') }, 
		{ code : 'clientMoSummary', text : $translate('sms.report.type.' + 'MoSummary') }, 
		{ code : 'clientDrSummary', text : $translate('sms.report.type.' + 'DrSummary') } ];
	
	$scope.reportFormat = [
		{ code : 'html', text : 'HTML' },
		{ code : 'pdf', text : 'PDF' },
		{ code : 'csv', text : 'CSV' },
		{ code : 'xlsx', text : 'XLSX' }
	]
	
	$scope.criteriaList = [
		{ code : 'interface', text : 'All Interface' },
		{ code : 'sysId', text : 'All SysId' }
	]
	
	$scope.downloadFormat = "pdf";
	$scope.today = new Date();
	$scope.Error = null;
	$scope.reportReady = false;
	var endDate = new Date();
	var startDate = new Date(
			endDate.getFullYear(),
			endDate.getMonth()-1,
			endDate.getDate()
		 ); 
	
	$scope.searchRequest = {reportType : 'clientMtSummary', datePeriod : 'D', reportFormat: 'html', 
			sysId : 'ALL', sourceId : 'ALL', start : startDate, end : endDate, criteria: 'interface'};
	
	$scope.rptHtmlUrl = null;
	$scope.setRptUrl= function(){
		
		var request = angular.copy($scope.searchRequest); // create asyn. copy for search request
		
		request.start = moment(request.start).startOf('day').format('YYYY-MM-DD');
		request.end = moment(request.end).endOf('day').format('YYYY-MM-DD');
		
		// for monthly MT Details
		if(request.reportType == 'clientMtDetail' && request.datePeriod == 'M'){
			$scope.rptHtmlUrl = context + '/sms/report/getReportMTDetailM/'
			+ request.criteria +'/'
			+ request.reportType +'/'+ request.reportFormat +'/' 
			+ request.start+ '/' + request.end;		
		}
		else{ // for other types report
			$scope.rptHtmlUrl = context + '/sms/report/getReport/'
			+ request.sysId +'/'+ request.sourceId +'/'
			+ request.reportType +'/'+ request.datePeriod +'/'+ request.reportFormat +'/' 
			+ request.start+ '/' + request.end;			
		}
		
		// add timestamp to avoid templateCache
		var timestamp = new Date().getTime()
		$scope.rptHtmlUrl = $scope.rptHtmlUrl + '?' +　timestamp;
//		console.log($scope.rptHtmlUrl);
	};
	
	$scope.showReportDialog = function() {	
		if (AuthService.havePrivilege($scope.userSession.rights, "SMS_SERVICE_06:read")) {
			$scope.Error = null;
			$scope.setRptUrl();
			$scope.reportReady = false;
			$mdDialog.show({
				controller: 'Sms.ReportCtrl',
				contentElement : '#ReportDialog',
				parent: angular.element(document.body),
				clickOutsideToClose : true, 
				fullscreen : true
			});
		}
	}
	
	$scope.closeDialog = function() {
		$mdDialog.cancel();
//		$scope.Error = null;
	};
	
	$scope.finishLoading = function() {
		$scope.reportReady = true;
		console.log($scope.reportReady);
	}
	
	$scope.refresh = function() {
		$state.reload();
	}
	
	$scope.dateMstart = {
		opened: false
	};
	$scope.dateMend = {
		opened: false
	};
	$scope.open1 = function() {
		$scope.dateMstart.opened = true;
	};
	$scope.open2 = function() {
		$scope.dateMend.opened = true;
	};
	
	/* Event handle  */
	$scope.showSourceId = true;
	$scope.rptTypeChange = function(){
		var tempPeriodList = [];
		
		tempPeriodList.push({ code : 'D', text : $translate('sms.report.dp.' + 'D') });
		if($scope.searchRequest.reportType === "clientMtDetail"){			
			tempPeriodList.push({ code : 'M', text : $translate('sms.report.dp.' + 'M') });
			// hide sourceId
			$scope.searchRequest.sourceId = 'ALL';
			$scope.showSourceId = false;
			
		}else{
			tempPeriodList.push({ code : 'H', text : $translate('sms.report.dp.' + 'H') });
			$scope.showSourceId = true;
		}
		$scope.datePeriodList = tempPeriodList;
	}

	$scope.showSelectionbox = false;
	$scope.datePeriodChange = function(){
		if($scope.searchRequest.datePeriod == 'M'){
			 //hide sysId, show selectionbox = {ALL_interface, ALL_sysID} 
			$scope.searchRequest.sysId = 'ALL';
			$scope.showSelectionbox = true;
		}
		else {
			$scope.showSelectionbox = false;
		}
	}
	
	// for daily and hourly
	$scope.setDateRange = function(){
		var today = new Date();
		var endDate = new Date(
			$scope.searchRequest.start.getFullYear(),
			$scope.searchRequest.start.getMonth() + 1,
			$scope.searchRequest.start.getDate()
		); 
		function getMaxDate(){
			if(today < endDate){
				return today;
			}else{
				return endDate;
			}
		};
		
		if($scope.searchRequest.end > endDate){
			$scope.searchRequest.end = endDate;
		}
		
		if($scope.searchRequest.start > $scope.searchRequest.end){
			$scope.searchRequest.end = $scope.searchRequest.start;
		}
		
		$scope.startDateOptions = {
				maxDate: $scope.today
		}
		$scope.endDateOptions = {
				minDate: $scope.searchRequest.start,
				maxDate: getMaxDate()
		}		
	}
	$scope.setDateRange();
	
	// for monthly
	$scope.setMDateRange = function(){
		var today = new Date();
		$scope.startDateMonthOptions = {
				maxMode: 'year',
				minMode: 'month',
				datepickerMode: 'month',
				maxDate: $scope.today
				
		}
		$scope.endDateMonthOptions = {
				maxMode: 'year',
				minMode: 'month',
				datepickerMode: 'month',
				minDate: $scope.searchRequest.start,
				maxDate: today
		}
		
		if($scope.searchRequest.end > endDate){
			$scope.searchRequest.end = endDate;
		}
		
		if($scope.searchRequest.start > $scope.searchRequest.end){
			$scope.searchRequest.end = $scope.searchRequest.start;
		}
	}
	$scope.setMDateRange();
	
	$scope.downloadRpt = function(){
		var request = angular.copy($scope.searchRequest); // create asyn. copy for search request
				
		request.start = moment(request.start).startOf('day').format('YYYY-MM-DD');
		request.end = moment(request.end).endOf('day').format('YYYY-MM-DD');
		
		// for monthly MT Details
		var rptDownloadUrl = null;
		if(request.reportType == 'clientMtDetail' && request.datePeriod == 'M'){
			rptDownloadUrl = context + '/sms/report/getReportMTDetailM/'
			+ request.criteria +'/'
			+ request.reportType +'/'+ $scope.downloadFormat +'/' 
			+ request.start+ '/' + request.end;		
		}
		else{ // for other types report
			rptDownloadUrl = context + '/sms/report/getReport/'
			+ request.sysId +'/'+ request.sourceId +'/'
			+ request.reportType +'/'+ request.datePeriod +'/'+ $scope.downloadFormat +'/' 
			+ request.start+ '/' + request.end;			
		}
		
		
		var link = document.createElement('a');
        link.href = rptDownloadUrl;
		
        if (link.download !== undefined){
        	link.download = request.reportType +"_"+request.start+"_"+request.end ;
        	if($scope.downloadFormat == 'csv'){
        		link.download = link.download + ".csv";
        	}
        }
 
        if (document.createEvent) {
            var e = document.createEvent('MouseEvents');
            e.initEvent('click' ,true ,true);
            link.dispatchEvent(e);
            return true;
        }
        
	    window.open(downloadPath, '_self', '');
	}
	
});