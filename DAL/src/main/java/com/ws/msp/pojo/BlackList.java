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
@Table(name = "BLACK_LIST", uniqueConstraints = @UniqueConstraint(columnNames = "DEST_NUMBER", name = "PK_BLACK_LIST"))
public class BlackList extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8888014697645379816L;

	@Id
	@Column(name = "DEST_NUMBER", nullable = false, length = 20)
	private String destNumber = null;
	
	@Column(name = "CREATE_BY", nullable = false, length = 255)
	private String createBy = null;
	
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate = null;
	
}
