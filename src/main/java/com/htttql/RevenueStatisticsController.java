package com.htttql;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.htttql.entity.RevenueStatistics;
import com.htttql.service.AbstractDAO;
import com.htttql.service.BillDAO;
import com.htttql.service.ReceiptDAO;
import com.htttql.service.RevenueStatisticsDAO;
import com.htttql.service.SalaryDAO;

@Controller
public class RevenueStatisticsController {
	
	@Autowired
	private RevenueStatisticsDAO reveDAO;
	
	@Autowired
	private BillDAO billDAO;
	
	@Autowired
	private SalaryDAO salaryDAO;
	
	@Autowired
	private AbstractDAO abDao;
	@Autowired
	private ReceiptDAO receiptDAO;
	
	@RequestMapping(value = "/reve",method = RequestMethod.GET)
	public String findAll(Model model) {
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findAll();
		model.addAttribute("reves", reves);
		return "revenueStatistics/display";
	}
	
	@RequestMapping(value = "/reve/revenow",method = RequestMethod.GET)
	public String createNewReve(Model model) {
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findAll();
		LocalDate todaydate = LocalDate.now();
		LocalDate fisrtdate = todaydate.withDayOfMonth(1);
		Date enddate = Date.from(todaydate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date startdate = Date.from(fisrtdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		double saleprice = 0;
		double salary = 0;
		double importprice = 0;
		
		if(reveDAO.countByDate() != 0) {
			for (RevenueStatistics reve : reves) {
				if(((reve.getCreateDate().compareTo(startdate) == 1) && (reve.getCreateDate().compareTo(enddate) == -1)) || (reve.getCreateDate().compareTo(startdate) == 0) || (reve.getCreateDate().compareTo(enddate) == 0)) {
					saleprice = billDAO.revenueByMonthNow();
					salary = salaryDAO.getTotalSalaryOfMonthNow();
					importprice = receiptDAO.findTotalPriceImportOfMonthNow();
					double totalPrice = 0;
					totalPrice = saleprice - salary - importprice;
					reve.setTotal(totalPrice);
					reve.setCreateDate(enddate);
					reveDAO.save(reve);
				}
				
			}
		}
		else {
			RevenueStatistics reve1 = new RevenueStatistics();
			saleprice = billDAO.revenueByMonthNow();
			salary = salaryDAO.getTotalSalaryOfMonthNow();
			importprice = receiptDAO.findTotalPriceImportOfMonthNow();
			double totalPrice = 0;
			totalPrice = saleprice - salary - importprice;
			reve1.setTotal(totalPrice);
			reve1.setCreateDate(enddate);
			reve1.setCreateBy(abDao.getAccountant());
			reveDAO.save(reve1);
		}
		return "redirect:/reve";
	}

	@RequestMapping(value = "/reve/month",method = RequestMethod.GET)
	public String createReveByMonthAndYear(@RequestParam("month") String month,@RequestParam("year") String year) {
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findAll();
		int dem = 0;
		double saleprice = 0;
		double salary = 0;
		double importprice = 0;
		for (RevenueStatistics reve : reves) {
			if(reve.getCreateDate().getYear() + 1900 == Integer.parseInt(year)) {
				if(reve.getCreateDate().getMonth()+1 == Integer.parseInt(month)) {
					dem ++;
				}
			}	
		}
		if(dem != 0) {
			return "redirect:/reve";
		}
		else {
			for (RevenueStatistics reve : reves) {
				if(reve.getCreateDate().getYear() + 1900 == Integer.parseInt(year)) {
					if(reve.getCreateDate().getMonth()+1 == Integer.parseInt(month)) {
						saleprice = billDAO.salePriceByMonth(Integer.parseInt(month), Integer.parseInt(year));
						double totalPrice = 0;
						totalPrice = saleprice - salary - importprice;
					}
				}	
			}
			
		}
	
		
		return "redirect:/reve";
	}
}
