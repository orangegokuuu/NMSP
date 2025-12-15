package com.ws.msp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.BaseProfileBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "SPAM_KEY_WORD", uniqueConstraints = @UniqueConstraint(columnNames = "KEY", name = "PK_SPAM__KEY_WORD"))
public class SpamKeyWord extends BaseProfileBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8176801173801326100L;
	
	public static final String ACTIVE = "A";
	public static final String INACTIVE = "I";

	@Id
	@Column(name = "KEY", nullable = false, length = 50)
	private String key = null;
	
	@Column(name = "STATUS", nullable = false, length = 5)
	private String status = ACTIVE;
	
}
