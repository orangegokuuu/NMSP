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
@Table(name = "TIME_TABLE", uniqueConstraints = @UniqueConstraint(columnNames = "TIME_TABLE_ID", name = "PK_TIME_TABLE"))
public class TimeTable extends VersionedProfileBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 40683617439249353L;

	public static final String STATUS_ACTIVE = "A";
	public static final String STATUS_INACTIVE = "I";
	
	@Id
	@Column(name = "TIME_TABLE_ID", nullable = false, length = 30)
	private String timeTableId = null;
	
	@Column(name = "TIME_TABLE_NAME", nullable = false, length = 200)
	private String timeTableName = null;
	
	@Column(name = "STATUS", nullable = false, length = 5)
	private String status = null;
	
	@JsonProperty(value = "timeData", access = Access.READ_ONLY)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "pk.timeTableId", fetch = FetchType.LAZY)
	private List<TimeSlotData> timeData = new ArrayList<TimeSlotData>();
	
	public List<TimeSlotData> getTimeData() {
		return timeData;
	}

	public void setTimeData(List<TimeSlotData> timeData) {		
		if (this.timeData == null){
			this.timeData = timeData;
		}else{
			this.timeData.clear();
			this.timeData.addAll(timeData);
		}
	}
	
}
