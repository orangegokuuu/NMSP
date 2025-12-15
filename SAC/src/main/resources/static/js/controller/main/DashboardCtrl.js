SAC.controller('DashboardCtrl', function($scope, $interval, $mdMedia, DashbaordService) {
	$scope.versionMap = {};
	$scope.monit = [];
	$scope.sysMem = {};
	$scope.sysCPU = {};
	$scope.colors = ['#0000ff', '#ff0000', '#00ff00'];
	var ratio = 1;
	
	var lineChartOption = {
		title : { display : true, position : 'top' }, 
		legend : { display : true, position : 'bottom' },
		maintainAspectRatio: true,
		aspectRatio : 2,
		responsive: true,
		scales : { yAxes : [ { stacked : false, ticks : { max : 100, min : 0, stepSize : 10, } } ] } 
	};
	$scope.memChartOption = JSON.parse(JSON.stringify(lineChartOption));
	$scope.memChartOption.title.text = "RAM(%)";
	$scope.cpuChartOption = JSON.parse(JSON.stringify(lineChartOption));
	$scope.cpuChartOption.title.text = "CPU(%)";
	
	if ($mdMedia('gt-sm')) {
		$scope.largeScreen = true;
	} else {
		$scope.largeScreen = false;
	}
	
	DashbaordService.getVersion().then(function(response) {
		$scope.versionMap = response.data;
	});
	
	this.drawGraph = function() {
		DashbaordService.getMonit().then(function(response) {
			$scope.monit = response.data;
			
			$scope.monit.forEach(function(m) {
				if ($scope.sysMem[m.system.name]) {
					if ($scope.sysMem[m.system.name].labels.length >= 20) {
						$scope.sysMem[m.system.name].labels.shift();
					}
					$scope.sysMem[m.system.name].labels.push(m.procMemLabel);
					var i = 0;
					m.procMemSeries.forEach(function(){
						if ($scope.sysMem[m.system.name].data[i].length >= 20) {
							$scope.sysMem[m.system.name].data[i].shift();
						}
						$scope.sysMem[m.system.name].data[i].push(m.procMemLoad[i]);
						i++;
					});
				} else {
					$scope.sysMem[m.system.name] = {};
					$scope.sysMem[m.system.name].data = [[]];
					$scope.sysMem[m.system.name].labels = [m.procMemLabel];
					$scope.sysMem[m.system.name].series = m.procMemSeries;
					var i = 0;
					m.procMemSeries.forEach(function(){
						$scope.sysMem[m.system.name].data[i] = [m.procMemLoad[i]];
						i++;
					})
				}
				
				if ($scope.sysCPU[m.system.name]) {
					if ($scope.sysCPU[m.system.name].labels.length >= 20) {
						$scope.sysCPU[m.system.name].labels.shift();
					}
					$scope.sysCPU[m.system.name].labels.push(m.procCpuLabel);
					var i = 0;
					m.procCpuSeries.forEach(function(){
						if ($scope.sysCPU[m.system.name].data[i].length >= 20) {
							$scope.sysCPU[m.system.name].data[i].shift();
						}
						$scope.sysCPU[m.system.name].data[i].push(m.procCpuLoad[i]);
						i++;
					});
				} else {
					$scope.sysCPU[m.system.name] = {};
					$scope.sysCPU[m.system.name].data = [[]];
					$scope.sysCPU[m.system.name].labels = [m.procCpuLabel];
					$scope.sysCPU[m.system.name].series = m.procCpuSeries;
					var i = 0;
					m.procCpuSeries.forEach(function(){
						$scope.sysCPU[m.system.name].data[i] = [m.procCpuLoad[i]];
						i++;
					})
				}
			});
		});
	};
	
	// Timer for draw graph
	var drawTimer = $interval(function() {
		this.drawGraph();
	}.bind(this), 30000);    
	
	$scope.$on('$destroy', function () {
        $interval.cancel(drawTimer);
    });

	this.drawGraph();
});