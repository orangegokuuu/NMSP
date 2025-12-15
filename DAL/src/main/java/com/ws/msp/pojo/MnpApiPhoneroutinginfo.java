package com.ws.msp.pojo;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.BaseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "MNP_API_PHONEROUTINGINFO", uniqueConstraints = @UniqueConstraint(columnNames = "PHONE_NUMBER", name = "PK_MNP_API_PHONEROUTINGINFO"))
public class MnpApiPhoneroutinginfo extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3008482456404855882L;
	
	@Id
	@Column(name = "PHONE_NUMBER", nullable = false, length = 20)
	private String phoneNumber;
	
	@Column(name = "SS7RN", length = 6)
	private String ss7Rn;
	
	@Column(name = "SS7DN", length = 11)
	private String ss7Dn;
	
	@Column(name = "VOIPRN", length = 20)
	private String voiprn;
	
	@Column(name = "SMRI", length = 10)
	private String smri;
	
	@Column(name = "MRI", length = 20)
	private String mri;
	
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "OPERATION_TYPE", length = 1)
	private String operationType;

}
