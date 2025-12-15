SAC.factory('GridUtil', function ($filter) {
	var gridUtil = {};

	/*
	 * Map AG-Grid
	 */
	var opers = {};
	opers['equals'] = 'eq';
	opers['notEquals'] = 'ne';
	opers['lessThan'] = 'lt';
	opers['greaterThan'] = 'gt';
	opers['contains'] = 'cn';

	function getOper(key) {
		if (opers[key] != null) {
			return opers[key];
		} else {
			return key;
		}
	}
	/*
	 * Build sorting object interchange with com.ws.pojo.Paging
	 * 
	 * com.ws.database.SortOrder contain private Direction direction =
	 * Direction.ASC; private String field = null;
	 * 
	 */
	gridUtil.makeSorting = function (gridSort) {
		var sorting = {};
		var sortCriteria = [];

		gridSort.forEach(function (item) {
			var sortOrder = {};
			sortOrder.field = item.colId;
			sortOrder.direction = item.sort.toUpperCase();

			sortCriteria.push(sortOrder);
			//			console.log(item);
		});
		sorting.orders = sortCriteria;

		return sorting;
	}

	gridUtil.makeSearch = function (gridFilter) {
		var searchRule = [];
		var searchCommand = {}

		for (var key in gridFilter) {
			var filter = gridFilter[key];
			var rule = {};
			// Handle date filter
			if (filter.dateFrom != null) {
				if (filter.type == 'inRange') {
					rule = {};
					rule.field = key;
					rule.op = 'ge';
					rule.date = $filter('date')(new Date(filter.dateFrom + " 00:00:00"), 'yyyy-MM-dd HH:mm:ss', '+0800');
					searchRule.push(rule);

					rule = {};
					rule.field = key;
					rule.op = 'le';
					rule.date = $filter('date')(new Date(filter.dateTo + " 23:59:59"), 'yyyy-MM-dd HH:mm:ss', '+0800');
					searchRule.push(rule);
				} else {
					rule.field = key;
					rule.op = getOper(filter.type);
					rule.date = $filter('date')(new Date(filter.dateFrom + " 00:00:00"), 'yyyy-MM-dd HH:mm:ss', '+0800');
					console.log(rule);
					searchRule.push(rule);
				}
			} else {
				rule.field = key;
				rule.op = getOper(filter.type);
				console.log(filter);
				rule.value = filter.filter;
				searchRule.push(rule);
			}
		}

		searchCommand.groupOp = "AND";
		searchCommand.rules = searchRule;
		return searchCommand;
	}

	gridUtil.makePageSearch = function (gridFilter, gridSort, pageSize, offset) {
		var searchPage = {};
		var searchGroup = {};
		searchGroup.commands = [];

		var searchCommand = gridUtil.makeSearch(gridFilter);
		searchGroup.join = "AND";
		searchGroup.commands.push(searchCommand);
		searchPage.searchGroup = searchGroup;

		var sorting = gridUtil.makeSorting(gridSort);
		var pageNum = (offset / pageSize) + 1;

		searchPage.sorting = sorting;
		searchPage.pageSize = pageSize;
		searchPage.pageNum = pageNum;

		return searchPage;
	}

	return gridUtil;
});

/**
 * Date Transfer 
 */
SAC.factory('DateUtil', function ($filter) {
	var dateUtil = {};


	dateUtil.transLocalDateTime = function (dateArray) {
		var year = dateArray[0];
		var month = dateArray[1];
		var day = dateArray[2];
		var hours = dateArray[3];
		var minutes = dateArray[4];
		var seconds = dateArray[5];

		var date = new Date(year, month - 1, day, hours, minutes, seconds);

		var formattedDate = moment(date).format('YYYY/MM/DD, hh:mm:ss A');

		return formattedDate;
	}

	dateUtil.transDate = function (date, format) {
		var padZero = function (value) {
			return value < 10 ? "0" + value : value;
		};

		var year = date.getFullYear();
		var month = padZero(date.getMonth() + 1);
		var day = padZero(date.getDate());
		var hour = padZero(date.getHours());
		var minute = padZero(date.getMinutes());
		var second = padZero(date.getSeconds());

		format = format.replace("yyyy", year);
		format = format.replace("MM", month);
		format = format.replace("dd", day);
		format = format.replace("hh", hour);
		format = format.replace("mm", minute);
		format = format.replace("ss", second);

		return format;
	}

	dateUtil.parserToTimestamp = function (dateString) {
		const parts = dateString.split(/[/, :, ]/);
  
		const date = new Date(
		  parts[0],  // year
		  parts[1] - 1,  // month (-1，JavaScript)
		  parts[2],  // day
		  parts[3],  // hour
		  parts[4],  // min
		  parts[5]  // sec
		);
		
		// get timestamp (millisecond)
		const timestamp = date.getTime();
		
		return timestamp;
	}

	dateUtil.parserToLocalDateTime = function (dateString) {
		const parts = dateString.split(/[/, :, ]/);
  
		const year = parseInt(parts[0], 10);
		const month = parseInt(parts[1], 10) - 1;
		const day = parseInt(parts[2], 10);
		// const hour = parseInt(parts[3], 10);
		// const minute = parseInt(parts[4], 10);
		// const second = parseInt(parts[5], 10);
		// const millisecond = parseInt(parts[6], 10) || 0;
		const hour = parseInt(parts[4], 10);
		const minute = parseInt(parts[5], 10);
		const second = parseInt(parts[6], 10);
	  
		const customFormat = [
		  year,
		  month,
		  day,
		  hour,
		  minute,
		  second
		//   millisecond
		];
		
		return customFormat;
	}

	return dateUtil;
});

/**
 * Authentication Handling
 */
SAC.factory('DashbaordService', function ($http) {
	var dashbaordService = {};

	dashbaordService.getVersion = function () {
		return $http.get(context + '/dashbaord/version').then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	};

	dashbaordService.getMonit = function () {
		return $http.get(context + '/dashbaord/monit').then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	};

	return dashbaordService;
});

/**
 * Authentication Handling
 */
SAC.factory('AuthService', function ($http, authService) {
	var sacAuthService = {};

	sacAuthService.login = function (credentials) {
		return $http.post(context + '/login', credentials).then(function (response) {
			authService.loginConfirmed();
			return response.status;
		}, function (response) {
			return response.status;
		});
	};

	sacAuthService.updatePassword = function (credentials) {
		return $http.post(context + '/password', credentials).then(function (response) {
			authService.loginConfirmed();
			return response;
		}, function (response) {
			return response;
		});
	};
	sacAuthService.changePassword = function (credentials) {
		return $http.post(context + '/profile/password', credentials).then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	}

	sacAuthService.getSession = function () {
		return $http.get(context + '/session').then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	};

	sacAuthService.getPwdConfig = function () {
		return $http.post(context + '/passwordconfig').then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	};

	sacAuthService.expireSession = function () {
		return $http.get(context + '/expired').then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return JSOG.decode(response.data);
		});
	};

	sacAuthService.havePrivilege = function (rights, required) {
		var privilege = "";
		var level = "";
		if (required.indexOf(":") > 0) {
			privilege = required.split(":")[0];
			level = required.split(":")[1];
		} else {
			privilege = required;
			level = "read";
		}
		switch (level) {
			case 'write':
				if (rights['SUPER'].writeEnable || rights[privilege].writeEnable) {
					return true;
				}
				return false;
				break;
			case 'other':
				if (rights['SUPER'].otherEnable || rights[privilege].otherEnable) {
					return true;
				}
				return false;
				break;
			default:
				if (rights['SUPER'].readEnable || rights[privilege].readEnable) {
					return true;
				}
				return false;
				break;
		}
	}
	return sacAuthService;
});

var RestService = function ($http, apiUrl) {
	if (!apiUrl.endsWith('/')) {
		apiUrl = apiUrl + '/';
	}
	this.list = function() {
		return $http.get(apiUrl).then(function(response) {
			return JSOG.decode(response);
		}, function (response) {
			return JSOG.decode(response);
		});
	};

	this.page = function(searchPage) {
		return $http.post(apiUrl + 'page', searchPage).then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return JSOG.decode(response);
		});
	};
	
	this.get = function(id) {
		return $http.get(apiUrl + id +"/").then(function(response) {
			return JSOG.decode(response);
		}, function(response) {
			return JSOG.decode(response);
		});
	};

	this.create = function (entity) {
		return $http.post(apiUrl, entity).then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return JSOG.decode(response);
		});
	}

	this.update = function(id, entity) {
		return $http.put(apiUrl + id +"/", entity).then(function(response) {
			return JSOG.decode(response);
		}, function (response) {
			return JSOG.decode(response);
		});
	}
	
	this.delete = function(id) {
		return $http.delete(apiUrl + id +"/").then(function(response) {
			return JSOG.decode(response);
		}, function (response) {
			return JSOG.decode(response);
		});
	}
}

SAC.factory('UserService', function ($http) {
	var userService = {};
	var userRest = new RestService($http, context + '/api/user/user/');
	var roleRest = new RestService($http, context + '/api/user/role/');
	var groupRest = new RestService($http, context + '/api/user/group/');
	var actionLogRest = new RestService($http, context + '/api/user/actionlog/');



	userService.listUser = function () {
		return userRest.list();
	}

	userService.pageUser = function (searchPage) {
		return userRest.page(searchPage);
	};

	userService.getUser = function (userId) {
		return userRest.get(userId);
	};

	userService.createUser = function (user) {
		return userRest.create(user);
	}

	userService.updateUser = function (user) {
		return userRest.update(user.userId, user);
	}

	userService.deleteUser = function (user) {
		return userRest.delete(user.userId);
	}

	userService.listRole = function () {
		return roleRest.list();
	};

	userService.listPrivilege = function (id) {
		return $http.get(context + '/api/user/role/privilege').then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return response;
		});
	}

	userService.listPrivilegeType = function (id) {
		return $http.get(context + '/api/user/role/privilegeType').then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return response;
		});
	}

	userService.pageRole = function (searchPage) {
		return roleRest.page(searchPage);
	};

	userService.createRole = function (role) {
		return roleRest.create(role);
	}

	userService.updateRole = function (role) {
		return roleRest.update(role.roleId, role);
	}

	userService.deleteRole = function (role) {
		console.log("Delete role " + role.roleId);
		return roleRest.delete(role.roleId);
	}

	userService.listGroup = function () {
		return groupRest.list();
	};

	userService.pageGroup = function (searchPage) {
		return groupRest.page(searchPage);
	};

	userService.createGroup = function (group) {
		return groupRest.create(group);
	}

	userService.updateGroup = function (group) {
		return groupRest.update(group.groupId, group);
	}

	userService.deleteGroup = function (group) {
		return groupRest.delete(group.groupId);
	}

	userService.pageActionLog = function (searchPage) {
		return actionLogRest.page(searchPage);
	}
	return userService;
});

SAC.factory('SystemService', function ($http) {
	var systemService = {};
	var eventRest = new RestService($http, context + '/api/system/event/');
	var paramRest = new RestService($http, context + '/api/system/param/');
	var templateRest = new RestService($http, context + '/api/system/template/');
	var agentRest = new RestService($http, context + '/api/system/agent/');

	systemService.pageEventLog = function (searchPage) {
		return eventRest.page(searchPage);
	}

	systemService.listParam = function () {
		return paramRest.list();
	}
	systemService.pageParam = function (searchPage) {
		return paramRest.page(searchPage);
	}
	systemService.updateParam = function (param) {
		return paramRest.update(param.paramId, param);
	}

	systemService.listTemplate = function () {
		return templateRest.list();
	}
	systemService.pageTemplate = function (searchPage) {
		return templateRest.page(searchPage);
	}
	systemService.createTemplate = function (template) {
		return templateRest.create(template);
	}
	systemService.updateTemplate = function (template) {
		return templateRest.update(template.messageId, template);
	}

	return systemService;
});

SAC.factory('SmsService', function ($http) {
	var smsService = {};
	var cpRest = new RestService($http, context + '/sms/cp/');
	var smsRest = new RestService($http, context + '/sms/record/');
	var bkListRest = new RestService($http, context + '/sms/blacklist/');
	var spamKeyRest = new RestService($http, context + '/sms/spamkeyword/');
	var TimetableRest = new RestService($http, context + '/sms/timetable/');
	var subConsoleRest = new RestService($http, context + '/sms/subconsole/');

	//cp
	smsService.getCP = function (cpId) {
		return cpRest.get(cpId);
	}

	smsService.listCP = function () {
		return cpRest.list();
	}

	smsService.pageCP = function (searchPage) {
		return cpRest.page(searchPage);
	};

	smsService.createCP = function (cp) {
		return cpRest.create(cp);
	}

	smsService.updateCP = function (cp) {
		return cpRest.update(cp.cpId, cp);
	}

	smsService.deleteCP = function (cp) {
		return cpRest.delete(cp.cpId);
	}

	smsService.getEmptyCP = function () {
		var contextPath = context + '/sms/cp/getEmptyCP';
		return $http.get(contextPath).then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return response.data;
		});
	}

	smsService.getTimetableList = function () {
		var contextPath = context + '/sms/cp/getTimetableList';
		return $http.get(contextPath).then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return response.data;
		});
	}

	// record
	smsService.listSmsRecord = function () {
		return smsRest.list();
	}

	smsService.pageSmsRecord = function (searchPage) {
		return smsRest.page(searchPage);
	};

	smsService.addSampleSmsRecord = function () {
		var contextPath = context + '/sms/record/addSampleSmsRecord';
		return $http.get(contextPath).then(function (response) {
			return JSOG.decode(response.data);
		}, function (response) {
			return response.data;
		});
	}

	//blackList
	smsService.listBlackList = function () {
		return bkListRest.list();
	}

	smsService.pageBlackList = function (searchPage) {
		return bkListRest.page(searchPage);
	};

	//spam keyword
	smsService.listSpamKey = function () {
		return spamKeyRest.list();
	}

	smsService.pageSpamKey = function (searchPage) {
		return spamKeyRest.page(searchPage);
	};

	//TimeTable
	smsService.listTimeTable = function () {
		return TimetableRest.list();
	}

	smsService.pageTimeTable = function (searchPage) {
		return TimetableRest.page(searchPage);
	};

	//	smsService.createTimeTable = function(timetable) {
	//		return TimetableRest.create(timetable);
	//	}

	//	smsService.updateTimeTable = function(timetableContainer) {
	//		return TimetableRest.update(timetableContainer.timeTable.timeTableId, timetableContainer);
	//	}
	//	


	smsService.deleteTimeTable = function (timetable) {
		return TimetableRest.delete(timetable.timeTableId);
	}

	smsService.createTimeTableWithDefaultValue = function (timetable, defValue) {
		var contextPath = context + '/sms/timetable/createTimeTable/' + defValue;
		return $http.post(contextPath, timetable).then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return response;
		});
	}

	smsService.getTimeslotDatas = function (ttId) {
		var contextPath = context + '/sms/timetable/getTimeslotDatas/' + ttId;
		return $http.get(contextPath).then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return response;
		});
	}

	smsService.updateTimeTable = function (timetableContainer) {
		var contextPath = context + '/sms/timetable/updateTimetable/';
		//		console.log(contextPath);
		return $http.put(contextPath, timetableContainer).then(function (response) {
			return JSOG.decode(response);
		}, function (response) {
			return JSOG.decode(response);
		});
	};

	//	//report
	//	smsService.generateReport = function(request) {
	//		var requestTemp = angular.copy(request);
	//		requestTemp.start = moment(requestTemp.start).startOf('day').format('YYYY-MM-DD HH:mm:ss');
	//		requestTemp.end = moment(requestTemp.end).endOf('day').format('YYYY-MM-DD HH:mm:ss');
	//		return $http.post(context + '/sms/report/getReport/', requestTemp);
	//	};
	//	
	//	smsService.getReportHtml = function(request) {
	//		var requestTemp = angular.copy(request);
	//		requestTemp.start = moment(requestTemp.start).startOf('day').format('YYYY-MM-DD HH:mm:ss');
	//		requestTemp.end = moment(requestTemp.end).endOf('day').format('YYYY-MM-DD HH:mm:ss');
	//		return $http.get(context + '/sms/report/getReportHtml/');
	//	};
	//	

	smsService.getSubUser = function (id) {
		return subConsoleRest.get(id);
	}

	smsService.listSubUser = function () {
		return subConsoleRest.list();
	}

	smsService.pageSubUser = function (searchPage) {
		return subConsoleRest.page(searchPage);
	};

	smsService.createSubUser = function (subUser) {
		return subConsoleRest.create(subUser);
	}

	smsService.updateSubUser = function (subUser) {
		return subConsoleRest.update(subUser.userId, subUser);
	}

	smsService.deleteSubUser = function (subUser) {
		return subConsoleRest.delete(subUser.userId);
	}
	return smsService;
});