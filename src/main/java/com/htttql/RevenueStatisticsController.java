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
import org.thymeleaf.templateparser.markup.decoupled.DecoupledInjectedAttribute;

import com.htttql.dto.DetailReveDTO;
import com.htttql.entity.Accountant;
import com.htttql.entity.RevenueStatistics;
import com.htttql.repository.AccountRepository;
import com.htttql.repository.AccountantRepository;
import com.htttql.service.AbstractDAO;
import com.htttql.service.BillDAO;
import com.htttql.service.DateDAO;
import com.htttql.service.ExpenseDAO;
import com.htttql.service.ReceiptDAO;
import com.htttql.service.RevenueStatisticsDAO;
import com.htttql.service.SalaryDAO;
import com.htttql.service.TaxStatisticDAO;

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
	@Autowired
	private ExpenseDAO exDAO;
	@Autowired
	private DateDAO dateDAO;
	
	
	@Autowired
	private AccountantRepository accountantRepository;
	@Autowired
	private TaxStatisticDAO taxDAO;
	
	@RequestMapping(value = "/reve",method = RequestMethod.GET)
	public String findAll(Model model) {
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findAll();
		model.addAttribute("reves", reves);
		return "revenueStatistics/display";
	}
	
	@RequestMapping(value = "/reve/revenow",method = RequestMethod.GET)
	public String createNewReve(Model model) {
		LocalDate todaydate = LocalDate.now();
		LocalDate fisrtdate = todaydate.withDayOfMonth(1);
		LocalDate endDate = todaydate.withDayOfMonth(todaydate.lengthOfMonth());
		Date enddate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date startdate = Date.from(fisrtdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date todate = Date.from(todaydate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		double saleprice = 0;
		double salary = 0;
		double importprice = 0;
		double otherfee=0;
		double vat = 0;
		double tncn = 0;
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findByCreateDateBetween(startdate, enddate);
		
		if(reves.size() != 0) {
			for (RevenueStatistics reve : reves) {
				if(((reve.getCreateDate().compareTo(startdate) == 1) && (reve.getCreateDate().compareTo(enddate) == -1)) || (reve.getCreateDate().compareTo(startdate) == 0) || (reve.getCreateDate().compareTo(enddate) == 0)) {
					saleprice = billDAO.revenueByMonthNow();
					salary = salaryDAO.getTotalSalaryOfMonthNow();
					importprice = receiptDAO.findTotalPriceImportOfMonthNow();
					otherfee=exDAO.getTotalFeeByMonthAndYear(todate.getMonth()+1,todate.getYear()+1900);
					vat = taxDAO.vatCal(todate.getMonth()+1,todate.getYear()+1900);
					tncn = taxDAO.tncnCal(todate.getMonth()+1,todate.getYear()+1900);
					double totalPrice = 0;
					totalPrice = saleprice - salary - importprice - vat - tncn-otherfee;
					reve.setTotal(totalPrice);
					reve.setCreateDate(todate);
					reveDAO.save(reve);
				}
				
			}
		}
		else {
			RevenueStatistics reve1 = new RevenueStatistics();
			saleprice = billDAO.revenueByMonthNow();
			salary = salaryDAO.getTotalSalaryOfMonthNow();
			importprice = receiptDAO.findTotalPriceImportOfMonthNow();
			otherfee=exDAO.getTotalFeeByMonthAndYear(todate.getMonth()+1,todate.getYear()+1900);
			vat = taxDAO.vatCal(todate.getMonth()+1,todate.getYear()+1900);
			tncn = taxDAO.tncnCal(todate.getMonth()+1,todate.getYear()+1900);
			double totalPrice = 0;
			totalPrice = saleprice - salary - importprice - vat - tncn - otherfee;
			reve1.setTotal(totalPrice);
			reve1.setCreateDate(todate);
			reve1.setCreateBy(abDao.getAccountant());
			reveDAO.save(reve1);
		}
		return "redirect:/reve";
	}

	@RequestMapping(value = "/reve/month",method = RequestMethod.GET)
	public String createReveByMonthAndYear(@RequestParam("month") String month,@RequestParam("year") String year) {
		if(month =="" || year == "") {
			return "redirect:/reve";
		}
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		reves = reveDAO.findAll();
		int dem = 0;
		double saleprice = 0;
		double salary = 0;
		double importprice = 0;
		double otherfee=0;
		double vat = 0;
		double tncn = 0;
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
			RevenueStatistics reve = new RevenueStatistics();
			
						saleprice = billDAO.salePriceByMonth(Integer.parseInt(month), Integer.parseInt(year));
						salary = exDAO.getTotalSalaryHistoryByMonthAndYear(Integer.parseInt(month), Integer.parseInt(year));
						importprice = exDAO.getTotalReceiptByMonthAndYear(Integer.parseInt(month), Integer.parseInt(year));
						otherfee=exDAO.getTotalFeeByMonthAndYear(Integer.parseInt(month), Integer.parseInt(year));
						vat = taxDAO.vatCal(Integer.parseInt(month), Integer.parseInt(year));
						tncn = taxDAO.tncnCal(Integer.parseInt(month), Integer.parseInt(year));
						double totalPrice = 0;
						totalPrice = saleprice - salary - importprice - vat - tncn-otherfee;
						reve.setTotal(totalPrice);
						LocalDate initial = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 15);
						LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
						Date enddate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());
						reve.setCreateDate(enddate);
						reve.setCreateBy(abDao.getAccountant());
						reveDAO.save(reve);
						
	
			}
		return "redirect:/reve";
	}
	
	@RequestMapping(value = "/reve/delete",method = RequestMethod.GET)
	public String deleteReve(@RequestParam("id") String id) {
		reveDAO.delete(Integer.parseInt(id));
		return "redirect:/reve";
	}
	
	@RequestMapping(value = "/search/reve",method = RequestMethod.GET)
	public String searchReve(@RequestParam("name") String name,Model model) {
		if(name == "") {
			return "redirect:/reve";
		}
		List<Accountant> accountants = new ArrayList<Accountant>();
		accountants = accountantRepository.findByName(name);
		List<RevenueStatistics> reves = new ArrayList<RevenueStatistics>();
		for (Accountant acc : accountants) {
			List<RevenueStatistics> reves1 = new ArrayList<RevenueStatistics>();
			reves1 = reveDAO.findByCreateBy(acc);
			for (RevenueStatistics reve : reves1) {
				reves.add(reve);
			}
		}
		
		model.addAttribute("reves", reves);
		
		return "revenueStatistics/display";
	}
	
	@RequestMapping(value = "/reve/detail",method = RequestMethod.GET)
	public String detailReve(@RequestParam("id") String id,Model model) {
		RevenueStatistics reve = reveDAO.findOneById(Integer.parseInt(id));
		Date date = reve.getCreateDate();
		double salePrice = 0;
		double importPrice = 0;
		double salaryPrice = 0;
		double vat = 0;
		double tncn = 0;
		double psPrice = 0;
		double total = 0;
		salePrice = billDAO.salePriceByMonth(date.getMonth()+1, date.getYear()+1900);
		importPrice = exDAO.getTotalReceiptByMonthAndYear(date.getMonth()+1, date.getYear()+1900);
		salaryPrice = exDAO.getTotalSalaryHistoryByMonthAndYear(date.getMonth()+1, date.getYear()+1900);
		vat = taxDAO.vatCal(date.getMonth()+1, date.getYear()+1900);
		tncn = taxDAO.tncnCal(date.getMonth()+1, date.getYear()+1900);
		psPrice = exDAO.getTotalFeeByMonthAndYear(date.getMonth()+1, date.getYear()+1900);
		total = reve.getTotal();
		String createBy = reve.getCreateBy().getName();
		int month = date.getMonth()+1;
		int year = date.getYear()+1900;
		DetailReveDTO detail = new DetailReveDTO();
		detail.setId(Integer.parseInt(id));
		detail.setCreateBy(reve.getCreateBy().getName());
		detail.setCreateDate(date);
		detail.setSalePrice(salePrice);
		detail.setImportPrice(importPrice);
		detail.setSalaryPrice(salaryPrice);
		detail.setVat(vat);
		detail.setTncn(tncn);
		detail.setPsPrice(psPrice);
		detail.setTotalPrice(total);
		detail.setMonth(month);
		detail.setYear(year);
		String totalprice = String.valueOf((int)Math.round(total));
		ArrayList<String> kq = billDAO.readNum(totalprice);
		String money = "";
        for (int i = 0; i < kq.size(); i++) {
        	money += kq.get(i) + " ";
            System.out.print(kq.get(i)+ " ");
        }
        System.out.print(total);
        model.addAttribute("totalprice", money);
		model.addAttribute("detail", detail);
		return "revenueStatistics/detail";
	}
}
