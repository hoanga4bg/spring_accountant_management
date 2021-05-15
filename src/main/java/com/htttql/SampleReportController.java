package com.htttql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.htttql.entity.SampleReport;
import com.htttql.repository.SampleRepository;

@Controller
public class SampleReportController {
	@Autowired
	private SampleRepository samRepository;
	
	@RequestMapping(value = "/samplereport",method = RequestMethod.GET)
	public String findAll(Model model) {
		List<SampleReport> samples = new ArrayList<SampleReport>();
		samples = samRepository.findAll();
		model.addAttribute("samples", samples);
		return "samplereport/display";
	}
	
	@RequestMapping(value = "/samplereport/create",method = RequestMethod.GET)
	public String createNewSample(Model model) {
		model.addAttribute("sample", new SampleReport());
		model.addAttribute("status", 1);
		return "samplereport/insert";
	}
	
	@RequestMapping(value = "/samplereport/save",method = RequestMethod.POST)
	public String saveNewSample(SampleReport sample) {
		sample.setStatus(false);
		samRepository.save(sample);
		return "redirect:/samplereport";
	}
	
	@RequestMapping(value = "/samplereport/delete",method = RequestMethod.GET)
	public String deleteSample(@RequestParam("id") String id) {
		samRepository.delete(samRepository.findOneById(Integer.parseInt(id)));
		return "redirect:/samplereport";
	}
	
	@RequestMapping(value = "/samplereport/edit",method = RequestMethod.GET)
	public String edit(@RequestParam("id") String id, Model model) {
		SampleReport sample = new SampleReport();
		sample = samRepository.findOneById(Integer.parseInt(id));
		model.addAttribute("sample", sample);
		model.addAttribute("status", 0);
		return "samplereport/insert";
	}
	
	@RequestMapping(value = "/samplereport/detail",method = RequestMethod.GET)
	public String detail(@RequestParam("id") String id, Model model) {
		SampleReport sample = new SampleReport();
		sample = samRepository.findOneById(Integer.parseInt(id));
		model.addAttribute("sample", sample);
		
		return "samplereport/detail";
	}

}
