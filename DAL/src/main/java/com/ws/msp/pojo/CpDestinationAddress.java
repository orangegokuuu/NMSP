package com.ws.msp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.VersionedProfileBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "CP_DEST_ADDRESS", uniqueConstraints = @UniqueConstraint(columnNames = "ADDRESS_ID", name = "PK_CP_DEST_ADDRESS"))
public class CpDestinationAddress extends VersionedProfileBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2421603733798769941L;

	@Id
	@GeneratedValue(generator = "IdGenerator")
	@GenericGenerator(name = "IdGenerator", strategy = "com.ws.hibernate.IdGenerator", parameters = {
	        @Parameter(name = "prefix", value = "CPDA") })
	@Column(name = "ADDRESS_ID", nullable = false, length = 50)
	private String addressId = null;
	
	@Column(name = "CP_ID", length = 30)
	private String cpId = null;

	@Column(name = "DEST_ADDRESS", length = 255, nullable = false)
	private String destinationAddress = null;

	public CpDestinationAddress() {
		super();
	}

}
