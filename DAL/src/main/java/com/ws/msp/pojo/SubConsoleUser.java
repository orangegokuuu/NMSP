package com.ws.msp.pojo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.annotation.SkipToString;
import com.ws.hibernate.pojo.VersionedProfileBean;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Getter
@Setter
@Table(name = "SUB_CONSOLE_USER", uniqueConstraints = @UniqueConstraint(columnNames = "USER_ID", name = "PK_USER_ID"))
public class SubConsoleUser  extends VersionedProfileBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8013945645070143992L;
	public static final String STATUS_ACTIVE = "A";
	public static final String STATUS_SUSPEND = "S";
	public static final String STATUS_EXPIRE = "E";
	
	@Id
	@Column(name = "USER_ID", nullable = false, length = 30)
	private String userId = null;

	@Column(name = "USER_NAME", length = 60)
	private String userName = null;

	@SkipToString
	@Column(name = "PASSWORD", length = 50, nullable = false)
	private String password = null;

	@Column(name = "STATUS", length = 5, nullable = false)
	private String status = null;
	
	@Column(name = "LAST_LOGIN")
	private Date lastLogin = null;
	
}
