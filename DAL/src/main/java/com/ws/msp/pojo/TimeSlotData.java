package com.ws.msp.pojo;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.BaseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "TIME_SLOT_DATA", uniqueConstraints = @UniqueConstraint(columnNames = {"TIME_TABLE_ID","DAY_ID"}, name = "PK_TIME_SLOT_DATA"))
public class TimeSlotData extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -993209271351016121L;

	@EmbeddedId
	private TimeSlotDataPk pk = new TimeSlotDataPk();
	
	@Column(name = "DAY_DESC", length = 20)
	private String dayDesc = null;
	
	@Column(name = "SEND_TIME_DATA", length = 100)
	private String sendTimeData;
	
	
}
