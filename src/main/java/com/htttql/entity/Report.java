package com.htttql.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Lob
	private String content;
	@ManyToOne
	@JoinColumn(name="type")
	private SampleReport type;
	private int month;
	private int year;
	private Date createDate;
	private String category;
	@ManyToOne
	@JoinColumn(name = "create_by")
	private Accountant createBy;
	
}
