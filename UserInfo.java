package com.neimpetu.starter.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "INFOUSER")
public class UserInfo  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "complete_name")
	private String completeName;

	@Column(name = "email")
	private String email;

	@Column(name = "document_number", unique= true)
	private String documentNumber;

	@Column(name = "birthday")
	private Date birthday;

	@Column(name = "address")
	private String address;

	@Column(name = "phone")
	private String phone;

	@Column(name = "cellphone")
	private String cellphone;
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private MasterValue roleId;

	@ManyToOne
	@JoinColumn(name = "gender_id")
	private MasterValue genderId;
	
	

	@ManyToOne
	@JoinColumn(name = "document_type_id")
	private MasterValue documentTypeId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User userId;

	
	
	@ManyToOne
	@JoinColumn(name = "activated_profile_id")
	private Profile activatedProfileId;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creatorId;
	
	@ManyToOne
	@JoinColumn(name = "last_update_id")
	private User lastUpdateId;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	@Column(name = "notification_token")
	private String notificationToken;
	
}
