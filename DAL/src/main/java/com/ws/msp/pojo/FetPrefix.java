package com.ws.msp.pojo;

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
@Table(name = "FET_PREFIX", uniqueConstraints = @UniqueConstraint(columnNames = "PREFIX", name = "PK_FET_PREFIX"))
public class FetPrefix extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3432629014983788355L;

	@Id
	@Column(name = "PREFIX", nullable = false, length = 10)
	private String Prefix;
}
