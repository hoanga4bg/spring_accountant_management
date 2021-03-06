package com.htttql.service;

import java.util.Date;
import java.util.List;

import javax.swing.ListModel;

import org.jvnet.hk2.annotations.Service;
import org.springframework.context.annotation.Bean;

import com.htttql.entity.Receipt;


public interface ReceiptDAO {
	public List<Receipt> findAll();
	public Receipt findById(Integer id);
	public void save(Receipt receipt);
	public void deleteById(Integer id);
	public double findTotalPriceImportOfMonthNow();
	public List<Receipt> findByCreateDate(Date sDate,Date eDate);
}
