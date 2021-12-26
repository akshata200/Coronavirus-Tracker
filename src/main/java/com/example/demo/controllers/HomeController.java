package com.example.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.services.CoronaVirusDataServices;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataServices coronaVirusDataServices;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("locationStats",coronaVirusDataServices.getAllStats());
		model.addAttribute("totalReportedCasesTillNow",coronaVirusDataServices.getTotalReportedCasesTillNow());
		model.addAttribute("totalReportedCasesToday",coronaVirusDataServices.getTotalReportedCasesToday());
		return "home";
	}
	
}
