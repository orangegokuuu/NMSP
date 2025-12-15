package com.ws.msp.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.ws.hibernate.pojo.VersionedProfileBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "CONTENT_PROVIDER", uniqueConstraints = @UniqueConstraint(columnNames = "CP_ID", name = "PK_CONTENT_PROVIDER"))
public class ContentProvider extends VersionedProfileBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3726614898718175444L;

	public static final String STATUS_ACTIVE = "A";
	public static final String STATUS_INACTIVE = "I";
	
	public static final String CP_TYPE_HTTP = "1";
	public static final String CP_TYPE_MQ = "2";
	public static final String CP_TYPE_FILE = "3";
	
	public static final int CP_ZONE_INTER = 0;
	public static final int CP_ZONE_INTRA = 1;
	public static final int CP_ZONE_UNKNOWN = 99;
	public static final int CP_ZONE_QM1 = 11;
	public static final int CP_ZONE_QM2 = 12;
	public static final int CP_ZONE_QM3 = 13;
	public static final int CP_ZONE_QM4 = 14;
	public static final int CP_ZONE_QM5 = 15;
	public static final int CP_ZONE_QM6 = 16;

	@Id
	@Column(name = "CP_ID", nullable = false, length = 30)
	private String cpId = null;

	@Column(name = "CP_NAME", length = 60)
	private String cpName = null;

	@Column(name = "CP_TYPE", length = 1)
	private String cpType = null;
	
	@Column(name = "CONTACT_TEL", length = 20)
	private String contactTel = null;
	
	@Column(name = "CONTACT_EMAIL", length = 255)
	private String contactEmail = null;
	
	@Column(name = "TIME_TABLE_ID", length = 30)
	private String timeTableId = null;
		
	@Column(name = "STATUS", length = 5)
	private String status = null;

	@Column(name = "SMS_LIMIT")
	private int smsLimit;
	
	@Column(name = "DA_LIMIT")
	private int daLimit;
	
	@Column(name = "PUSH_DR_URL", length = 100)
	private String pushDrUrl = null;

	@Column(name = "DELIVER_SM_URL", length = 100)
	private String deliverSmUrl = null;
	
	@Column(name = "SPAM_CHECK_FL", length = 5)
	private boolean spamCheckFl =  false;
	
	@Column(name = "BLACKLIST_CHECK_FL", length = 5)
	private boolean blacklistCheckFl = false;
	
	@Column(name = "MQ_REQ_Q_NAME", length = 20)
	private String mqReqQName = null;
	
	@Column(name = "MQ_RESP_Q_NAME", length = 20)
	private String mqRespQName = null;
	
	@Column(name = "PREPAID_FL", length = 5)
	private boolean prepaidFl = false;
	
	@Column(name = "maxWaitingTimeForMO")
	private int maxWaitingTimeForMO;
	
	@Column(name = "maxSizePerReplyMessageForMO")
	private int maxSizePerReplyMessageForMO;
	
	@Column(name = "maxWaitingTimeForDR")
	private int maxWaitingTimeForDR;
	
	@Column(name = "maxSizePerReplyMessageForDR")
	private int maxSizePerReplyMessageForDR;
	
	@Column(name = "maxThread")
	private int maxThread;
	
	@Column(name = "waterLevel", length = 5)
	private String waterLevel = null;
	
	@Column(name = "splitterWaitingTime")
	private int splitterWaitingTime;
	
	@Column(name = "throughPSA", length = 5)
	private boolean throughPSA = false;
	
	@Column(name = "TRUSTED", length = 5)
	private boolean trusted = false;
	
	@Column(name = "LEGACY", length = 5)
	private boolean legacy = false;
	
	@Column(name = "PERIOD", length = 5)
	private String period = null;
	
	@Column(name = "COUNT")
	private int count;
	
	@Column(name = "API_VERSION", length = 1)
	private String apiVersion;
	
	@Column(name = "DR_REQUEST_FL", length = 5)
	private boolean drRequestFl = false;
	
	@Column(name = "CP_ZONE")
	private int cpZone; 
	
	// new column
	@Column(name = "PRIORITY")
	private int priority;
	
	@Column(name = "BLOCK_PROMOTION_FL", length = 5)
	private boolean blockPromotionFl = false;
	
	@Deprecated
	@Column(name = "RETRY_FL", length = 5)
	private boolean retryFl = false;
	
	@Deprecated
	@Column(name = "VALIDITY")
	private int validity;
	
	@Column(name = "SMS_LIMIT_UNIT", length = 1)
	private String smsLimitUnit;
	
	@Deprecated
	@Column(name = "QUERY_DR_LIMIT")
	private int queryDrLimit;
	
	@Column(name = "QUERY_DR_LIMIT_UNIT", length = 1)
	private String queryDrLimitUnit;
	
	@Column(name = "QUERY_DR_HR_LIMIT")
	private int queryDrHrLimit;
	
	@Column(name = "QUERY_DR_MIN_LIMIT")
	private int queryDrMinLimit;
	
	@Column(name = "PUSH_DR_FL", length = 5)
	private boolean pushDrFl = false;
	
	@Column(name = "MO_SMS_FL", length = 5)
	private boolean moSmsFl = false;
	
	@Column(name = "MQ_MANAGER_NAME", length = 10)
	private String mqManagerName = null;
	
//	@Column(name = "MO_SMS_URL", length = 100)
//	private String moSmsUrl = null;
	
	@JsonProperty("cpsaMap")
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "cpId", fetch = FetchType.LAZY)
	//	@JoinColumn(name = "CP_ID", foreignKey = @ForeignKey(name = "FK_CONTENT_PROVIDER_01"))
	//@MapKey(name = "pk.sourceAddress")
	@Cache (usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<CpSourceAddress> cpsaMap = new ArrayList<CpSourceAddress>();
	
	public List<CpSourceAddress> getCpsaMap() {
		return cpsaMap;
	}

	public void setCpsaMap(List<CpSourceAddress> cpsaMap) {
		if (this.cpsaMap == null){
			this.cpsaMap = cpsaMap;
		}else{
			this.cpsaMap.clear();
			this.cpsaMap.addAll(cpsaMap);
		}
	}
	
	@JsonProperty("cpdaMap")
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "cpId", fetch = FetchType.LAZY)
	@Cache (usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<CpDestinationAddress> cpdaMap = new ArrayList<CpDestinationAddress>();
	
	public List<CpDestinationAddress> getCpdaMap() {
		return cpdaMap;
	}

	public void setCpdaMap(List<CpDestinationAddress> cpdaMap) {
		if (this.cpdaMap == null){
			this.cpdaMap = cpdaMap;
		}else{
			this.cpdaMap.clear();
			this.cpdaMap.addAll(cpdaMap);
		}
	}
	
	public String toString() {
		
		String output = super.toString();
		
		try {
			output = output.split("}")[0];
			
				// added oa
				output += ",{\"sourceAddress\":";
				if(cpsaMap != null && cpsaMap.size() > 0) {
					for(CpSourceAddress oa : cpsaMap) {
						output += ",\"" + oa.getSourceAddress() + "\"";
					}
				} else {
					output += "null";
				}
				output += "}";
				
				// added da
				output += ",{\"destinationAddress\":";
				if(cpdaMap != null && cpdaMap.size() > 0) {
					for(CpDestinationAddress da : cpdaMap) {
						output += ",\"" + da.getDestinationAddress() + "\"";
					}
				} else {
					output += "null";
				}	
				output += "}";
				
			output += "}";
		} catch(Exception e) {
			// ignore
		}
		return output;
	}
			
//	public String getCpId() {
//		return cpId;
//	}
//
//	public void setCpId(String cpId) {
//		this.cpId = cpId;
//	}
//
//	public String getCpName() {
//		return cpName;
//	}
//
//	public void setCpName(String cpName) {
//		this.cpName = cpName;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getPushDrUrl() {
//		return pushDrUrl;
//	}
//
//	public void setPushDrUrl(String pushDrUrl) {
//		this.pushDrUrl = pushDrUrl;
//	}
//
//	public String getDeliverSmUrl() {
//		return deliverSmUrl;
//	}
//
//	public void setDeliverSmUrl(String deliverSmUrl) {
//		this.deliverSmUrl = deliverSmUrl;
//	}

}
