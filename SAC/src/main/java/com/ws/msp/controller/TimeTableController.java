package com.ws.msp.controller;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.ws.logging.LogAction;
import com.ws.logging.LogEvent;
import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PermissionAdvice;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.pojo.TimeSlotData;
import com.ws.msp.pojo.TimeSlotDataPk;
import com.ws.msp.pojo.TimeTable;
import com.ws.msp.sac.pojo.TimetableBody;
import com.ws.msp.sac.pojo.TimetableContainer;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.TimeTableManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;
import com.ws.sac.constant.SacConstant;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/sms/timetable")
@Log4j2
public class TimeTableController extends AbstractRestController<TimeTable, String> {
	private static final String[] dayTitle =
			{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	@Autowired
	private TimeTableManager timeTableManager = null;

	@Autowired
	private ContentProviderManager contentProviderManager = null;

	public void setTimetableManager(TimeTableManager timeTableManager) {
		this.timeTableManager = timeTableManager;
	}

	public void setContentProviderManager(ContentProviderManager contentProviderManager) {
		this.contentProviderManager = contentProviderManager;
	}

	@Autowired
	public TimeTableController(TimeTableManager timeTableManager) {
		super(timeTableManager);
		// this.setSuccessView("/success");
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	// @LogAction(type = SacConstant.LIS_TT, message = "List Timetable record")
	// @LogEvent(type = SacConstant.LIS_TT, message = "List Timetable record")
	@Permission(id = "SMS_SERVICE_05", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<TimeTable> page(@RequestBody SearchablePaging page) {
		return super.page(page);
	}

	@RequestMapping(value = "/getTimeslotDatas/{id}", method = RequestMethod.GET)
	@Permission(id = "SMS_SERVICE_05", level = PrivilegeLevel.READ)
	public @ResponseBody TimetableBody listAllTimeslot(@PathVariable String id) {
		TimeTable timeTable = timeTableManager.get(TimeTable.class, id);
		if (timeTable == null) {
			throw new EntityNotFoundException(String.format("Timetable[%s] not found", id));
		}

		// List<TimeSlotData> timeSlotDatas = timeTable.getTimeData(); // Sun to Mon timeslotDatas
		List<TimeSlotData> timeSlotDatas = timeTableManager.getOrderedTimeSlotDatas(id); // Sun to
																							// Mon
																							// timeslotDatas

		TimetableBody tableBody = new TimetableBody();
		tableBody.setData(timeSlotDatas);

		log.debug("getting timeSlotDatas: [{}]", Arrays.deepToString(tableBody.getTableBody()));
		// RestResult<TimetableBody> result = new RestResult<TimetableBody>();
		// result.setData(tableBody);
		return tableBody;
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {}

	@Override
	public void requiredPrivilege() {
		// TODO Auto-generated method stub
		super.setPrivilege("SMS_SERVICE_05");
	}

	private String getDefTimeSlotData(String defTimeData) { // 0 or 1
		String timeData = defTimeData;

		for (int i = 0; i < 47; i++) {
			timeData = timeData + "," + defTimeData;
		}
		return timeData;
	}

	// @RequestMapping(method = RequestMethod.POST)
	// @LogAction(type = SacConstant.ADD_TT, message = "Add Timetable[{1.timeTableId}]")
	// @LogEvent(type = SacConstant.ADD_TT, message = "Add Timetable[{1.timeTableId}]", id1 =
	// "{1.timeTableId}")
	// @Permission(id = "SMS_SERVICE_05", level = PrivilegeLevel.WRITE)
	// public @ResponseBody RestResult<TimeTable> create(@RequestBody TimeTable timeTable) {
	// timeTable.setCreateBy(userSession.getUserId());
	// timeTable.setCreateDate(new Date());
	//
	// List<TimeSlotData> timeSlotDatas = new ArrayList<TimeSlotData>();
	//
	// for(int i = 0; i < 7; i++) {
	// TimeSlotData ts = new TimeSlotData();
	// ts.setDayDesc(dayTitle[i]);
	// ts.setPk(new TimeSlotDataPk(timeTable.getTimeTableId(), i));
	// ts.setSendTimeData(getDefTimeSlotData("0"));
	// timeSlotDatas.add(ts);
	// }
	//
	// timeTable.setTimeData(timeSlotDatas);
	//
	// //log.debug("Print timetable id = " + timeTable.getTimeTableId());
	//
	// return super.create(timeTable);
	// }

	@RequestMapping(value = "/createTimeTable/{defaultTimetableValue}", method = RequestMethod.POST)
	@LogAction(type = SacConstant.ADD_TT, message = "Add Timetable[{1.timeTableId}]")
	@LogEvent(type = SacConstant.ADD_TT, message = "Add Timetable[{1.timeTableId}]",
			id1 = "{1.timeTableId}")
	public @ResponseBody TimeTable createTimetable(@RequestBody TimeTable timeTable,
			@PathVariable String defaultTimetableValue) {

		PermissionAdvice.checkPermission(this.getUserSession(), "SMS_SERVICE_05",
				PrivilegeLevel.WRITE);

		timeTable.setCreateBy(userSession.getUserId());
		timeTable.setCreateDate(LocalDateTime.now());

		List<TimeSlotData> timeSlotDatas = new ArrayList<TimeSlotData>();

		for (int i = 0; i < 7; i++) {
			TimeSlotData ts = new TimeSlotData();
			ts.setDayDesc(dayTitle[i]);
			ts.setPk(new TimeSlotDataPk(timeTable.getTimeTableId(), i));
			ts.setSendTimeData(getDefTimeSlotData(defaultTimetableValue));
			timeSlotDatas.add(ts);
		}

		timeTable.setTimeData(timeSlotDatas);


		/* This Method is not found in any class !! */
		// timeTable = beforeCreate(timeTable);


		log.debug("create() with body {} of type {}", timeTable, timeTable.getClass());

		TimeTable created = timeTableManager.save(TimeTable.class, timeTable);

		return created;
	}

	// not used
	// @RequestMapping(value = "/{id}/", method = RequestMethod.PUT)
	// @LogAction(type = SacConstant.UPD_TT, message = "Update Timetable")
	// @LogEvent(type = SacConstant.UPD_TT, message = "Update Timetable")
	// @Permission(id = "SMS_SERVICE_05", level = PrivilegeLevel.WRITE)
	// public @ResponseBody RestResult<TimeTable> update(@PathVariable String timetableId,
	// @RequestBody
	// TimetableContainer timetableContainer) {
	// TimeTable timeTable = timetableContainer.getTimeTable();
	// timeTable.setUpdateBy(userSession.getUserId());
	// timeTable.setUpdateDate(new Date());
	// if(timetableContainer.getTimetableBody()!=null){
	// TimetableBody timetableBody = timetableContainer.getTimetableBody();
	// String[][] table = timetableBody.getTableBody();
	// log.debug(Arrays.deepToString(table));
	// }
	// return super.update(timetableId, timeTable);
	// }

	@RequestMapping(value = "/updateTimetable", method = RequestMethod.PUT)
	@LogAction(type = SacConstant.UPD_TT, message = "Update Timetable[{1.timeTable.timeTableId}]")
	@LogEvent(type = SacConstant.UPD_TT, message = "Update Timetable[{1.timeTable.timeTableId}]",
			id1 = "{1.timeTable.timeTableId}")
	public @ResponseBody TimetableContainer updateTimetable(
			@RequestBody TimetableContainer timetableContainer) {
		PermissionAdvice.checkPermission(userSession, "SMS_SERVICE_05", PrivilegeLevel.WRITE);

		TimeTable timeTable = timetableContainer.getTimeTable();
		log.debug("update() of id#{} with body {}", timeTable.getTimeTableId(), timeTable);
		log.debug("T json is of type {}", timetableContainer.getClass());

		TimeTable oldTt = timeTableManager.get(TimeTable.class, timeTable.getTimeTableId());
		if (oldTt == null) {
			throw new EntityNotFoundException(
					String.format("Entity[{}] not found", timeTable.getTimeTableId()));
		}

		RestResult<TimetableContainer> result = new RestResult<TimetableContainer>();

		if (contentProviderManager.checkTimetableLinkage(timeTable.getTimeTableId())
				&& timeTable.getStatus().equals(TimeTable.STATUS_INACTIVE)) {
			log.info("Cannot change status to \"inactive\" because this timetable is still in use");

			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
					"Cannot change status to \"inactive\" because this timetable is still in use");
		}

		TimetableContainer tContainer = new TimetableContainer();

		String[][] table = null;

		if (timetableContainer.getTimetableBody() != null) {
			TimetableBody timetableBody = timetableContainer.getTimetableBody();
			table = timetableBody.getTableBody();
			log.debug("New Timeslot data:" + Arrays.deepToString(table));

			// Transpose
			String[][] transposedTable = new String[8][48];
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 48; j++) {
					transposedTable[i][j] = table[j][i];
				}
			}

			List<TimeSlotData> timeSlotDatas = new ArrayList<TimeSlotData>();

			for (int i = 1; i < 8; i++) { // ignore rowHeader
				TimeSlotData ts = new TimeSlotData();
				ts.setDayDesc(dayTitle[i - 1]);
				ts.setPk(new TimeSlotDataPk(timeTable.getTimeTableId(), i - 1));
				String data = StringUtils.join(transposedTable[i], ","); // concat string
				log.debug("Setting SendTimeData: [{}] for TimeSlotData: [{}], pk [{}]", data,
						ts.getDayDesc(), i - 1);
				ts.setSendTimeData(data);
				timeSlotDatas.add(ts);
			}

			timeTable.setTimeData(timeSlotDatas);

			// //for debug
			// TimetableBody tb = new TimetableBody();
			// tb.setData(timeSlotDatas);
			// String[][] data = tb.getTableBody();
			// log.debug("debug for timeslotdata: [{}]", Arrays.deepToString(data));
			// tContainer.setTimetableBody(tb);
		}

		try {
			BeanUtils.copyProperties(oldTt, timeTable);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.warn("while copying properties", e);
		}

		log.debug("merged entity: {}", timeTable);

		timeTable.setUpdateBy(userSession.getUserId());
		timeTable.setUpdateDate(LocalDateTime.now());

		TimeTable updated = timeTableManager.merge(TimeTable.class, timeTable);
		log.debug("updated enitity: {}", updated);

		tContainer.setTimeTable(updated);
		// result.setSuccess(true);
		// result.setData(tContainer);
		return tContainer;
	}

	@RequestMapping(value = "/{timetableId}/", method = RequestMethod.DELETE)
	@LogAction(type = SacConstant.DEL_TT, message = "Delete Timetable[{1}]")
	@LogEvent(type = SacConstant.DEL_TT, message = "Delete Timetable[{1}]", id1 = "{1}")
	public @ResponseBody TimeTable delete(@PathVariable String timetableId) {

		log.debug("delete() of id#{}", timetableId);
		PermissionAdvice.checkPermission(this.getUserSession(), "SMS_SERVICE_05",
				PrivilegeLevel.WRITE);

		// check if timetable used by cp
		if (contentProviderManager.checkTimetableLinkage(timetableId) == true) {
			log.info("Cannot delete timetable because this timetable is still in use");
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
					"Cannot delete timetable because this timetable is still in use");
		}

		return super.delete(timetableId);
	}


}
