package com.ws.msp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ws.logging.LogAction;
import com.ws.logging.LogEvent;
import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpDestinationAddress;
import com.ws.msp.pojo.CpSourceAddress;
import com.ws.msp.sac.subsac.pojo.MqCpRequestBody;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.TimeTableManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;
import com.ws.sac.constant.SacConstant;

@RestController
@RequestMapping("/sms/cp")
public class ContentProviderController extends AbstractRestController<ContentProvider, String> {
	private static final Logger logger = LogManager.getLogger(ContentProviderController.class);

	@Autowired
	private ContentProviderManager contentProviderManager = null;

	@Autowired
	private TimeTableManager timeTableManager = null;

	public void setTimeTableManager(TimeTableManager timeTableManager) {
		this.timeTableManager = timeTableManager;
	}

	@Autowired
	public ContentProviderController(ContentProviderManager contentProviderManager) {
		super(contentProviderManager);
		// this.setSuccessView("/success");
	}

	@Override
	public void requiredPrivilege() {
		super.setPrivilege("SMS_SERVICE_01");
	}

	@CrossOrigin
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	// @LogAction(type = SacConstant.LIS_CP, message = "List Content Provider")
	// @LogEvent(type = SacConstant.LIS_CP, message = "List Content Provider")
	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<ContentProvider> page(
			@RequestBody SearchablePaging page) {
		return super.page(page);
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.READ)
	@RequestMapping(value = "/getEmptyCP", method = RequestMethod.GET)
	public @ResponseBody RestResult<ContentProvider> getEmptyCP() {
		RestResult<ContentProvider> result = new RestResult<ContentProvider>();
		ContentProvider cp = new ContentProvider();
		List<CpSourceAddress> saList = new ArrayList<CpSourceAddress>();
		List<CpDestinationAddress> daList = new ArrayList<CpDestinationAddress>();
		cp.setCpsaMap(saList);
		cp.setCpdaMap(daList);
		result.setData(cp);
		return result;
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.READ)
	@RequestMapping(value = "/testCP", method = RequestMethod.GET)
	public @ResponseBody RestResult<ContentProvider> gettestCP() {
		RestResult<ContentProvider> result = new RestResult<ContentProvider>();
		ContentProvider cp = contentProviderManager.get(ContentProvider.class, "TEST1");
		result.setData(cp);
		return result;
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@RequestMapping(value = "/getTimetableList", method = RequestMethod.GET)
	public @ResponseBody List<Object[]> getTimetableList() {
		List<Object[]> tableList = timeTableManager.getTimeTableList();
		return tableList;
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@RequestMapping(method = RequestMethod.POST)
	@LogAction(type = SacConstant.ADD_CP, message = "Add Content Provider[{1.cpId}]")
	@LogEvent(type = SacConstant.ADD_CP, message = "Add Content Provider[{1.cpId}]",
			id1 = "{1.cpId}")
	public @ResponseBody ContentProvider create(@RequestBody ContentProvider cp) {
		cp.setCreateBy(userSession.getUserId());
		cp.setCreateDate(LocalDateTime.now());
		return super.create(cp);
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@RequestMapping(value = "/{id}/", method = RequestMethod.PUT)
	@LogAction(type = SacConstant.UPD_CP, message = "Update Content Provider[{1}]")
	@LogEvent(type = SacConstant.UPD_CP, message = "Update Content Provider[{1}]", id1 = "{1}")
	public @ResponseBody ContentProvider update(@PathVariable String id,
			@RequestBody ContentProvider cp) {
		cp.setUpdateBy(userSession.getUserId());
		cp.setUpdateDate(LocalDateTime.now());
		return super.update(id, cp);
	}

	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@RequestMapping(value = "/{id}/", method = RequestMethod.DELETE)
	@LogAction(type = SacConstant.DEL_CP, message = "Delete Content Provider[{1}]")
	@LogEvent(type = SacConstant.DEL_CP, message = "Delete Content Provider[{1}]", id1 = "{1}")
	public @ResponseBody ContentProvider delete(@PathVariable String id) {
		return super.delete(id);
	}

	/* MQ-SAC API */
	// @RequestMapping(value = "/getCpIdList", method = RequestMethod.GET)
	// public RestResult<List<ContentProvider>> getCpIdList(){
	// RestResult<List<ContentProvider>> result = new
	// RestResult<List<ContentProvider>>();
	// List<ContentProvider> cps = contentProviderManager.getAllMQCP();
	// result.setData(cps);
	// return result;
	// }

	@CrossOrigin
	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.READ)
	@RequestMapping(value = "/subconsole/getCp/{id}/", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ContentProvider> getMQCp(@PathVariable String id) {
		id = id.toUpperCase();
		ContentProvider cp = contentProviderManager.get(ContentProvider.class, id);
		if (cp == null) {
			return ResponseEntity.notFound().build();
		} else if (!cp.getCpType().equals(ContentProvider.CP_TYPE_MQ)) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		} else {
			return ResponseEntity.ok(cp);
		}
	}

	@CrossOrigin
	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@RequestMapping(value = "/subconsole/updateCp/", method = RequestMethod.PUT)
	@LogEvent(type = SacConstant.UPD_CP, message = "Update Content Provider[{1.cpId}]",
			id1 = "{1.cpId}")
	public @ResponseBody ResponseEntity<ContentProvider> updateCp(@RequestBody ContentProvider cp) {
		cp.setUpdateBy(userSession.getUserId());
		cp.setUpdateDate(LocalDateTime.now());

		ContentProvider targetCp = contentProviderManager.get(ContentProvider.class, cp.getCpId());
		if (targetCp == null) {
			return ResponseEntity.notFound().build();
		}
		try {
			logger.debug("merged entity: {}", cp);
			ContentProvider updated = contentProviderManager.merge(ContentProvider.class, cp);
			logger.debug("updated enitity: {}", updated);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@CrossOrigin
	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@LogEvent(type = SacConstant.ADD_CP, message = "Add Content Provider[{1.cpId}]",
			id1 = "{1.cpId}")
	@RequestMapping(value = "/subconsole/createCp/", method = RequestMethod.POST,
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody ResponseEntity<ContentProvider> createMQCP(@RequestBody MqCpRequestBody mqCp) {
		RestResult<ContentProvider> result = new RestResult<ContentProvider>();

		mqCp.setCpId(mqCp.getCpId().toUpperCase());

		// Check if Cp exist
		if (contentProviderManager.get(ContentProvider.class, mqCp.getCpId()) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		ContentProvider cp = new ContentProvider();
		cp.setCpId(mqCp.getCpId());
		cp.setSmsLimit(mqCp.getWaterLevel());
		cp.setCpName(mqCp.getCpName());
		cp.setCpType(ContentProvider.CP_TYPE_MQ);
		cp.setStatus(ContentProvider.STATUS_ACTIVE);
		cp.setDaLimit(-1);
		cp.setCpZone(LegacyConstant.QM_MAP.get(mqCp.getMqManagerName()));
		cp.setDrRequestFl(true);
		cp.setPrepaidFl(false);
		cp.setApiVersion("1");
		cp.setLegacy(mqCp.isLegacy());
		cp.setBlacklistCheckFl(false);
		cp.setSpamCheckFl(mqCp.isSpamCheck());
		cp.setMqReqQName("SMS." + mqCp.getCpId() + ".REQ.Q");
		cp.setMqRespQName("SMS." + mqCp.getCpId() + ".PLY.Q");

		cp.setPushDrFl(true);
		cp.setMoSmsFl(true);

		cp.setCreateBy(userSession.getUserId());
		cp.setCreateDate(LocalDateTime.now());

		CpSourceAddress sa = new CpSourceAddress();
		sa.setCpId(mqCp.getCpId());
		sa.setSourceAddress(mqCp.getSourceAddress());

		CpDestinationAddress da = new CpDestinationAddress();
		da.setCpId(mqCp.getCpId());
		da.setDestinationAddress(mqCp.getDestinationAddress());

		List<CpSourceAddress> cpsaMap = new ArrayList<CpSourceAddress>();
		cpsaMap.add(sa);
		cp.setCpsaMap(cpsaMap);

		List<CpDestinationAddress> cpdaMap = new ArrayList<CpDestinationAddress>();
		cpdaMap.add(da);
		cp.setCpdaMap(cpdaMap);

		try {
			return ResponseEntity.ok(contentProviderManager.save(ContentProvider.class, cp));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@CrossOrigin
	@Permission(id = "SMS_SERVICE_01", level = PrivilegeLevel.WRITE)
	@LogEvent(type = SacConstant.UPD_CP, message = "Update Content Provider[{1}]", id1 = "{1}")
	@RequestMapping(value = "/updateSmsLimit/{id}/{smsLimit}/", method = RequestMethod.PUT)
	public @ResponseBody RestResult<ContentProvider> updateWaterLevel(@PathVariable String id,
			@PathVariable int smsLimit) {
		RestResult<ContentProvider> result = new RestResult<ContentProvider>();
		try {
			result.setData(contentProviderManager.updateSmsLimit(id, smsLimit));
			result.setSuccess(true);
		} catch (Exception e) {
			result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			result.setMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}

}
