package com.example.demo.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.models.LocationStats;

@Service
public class CoronaVirusDataServices {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	private int totalReportedCasesTillNow;
	private int totalReportedCasesToday;
	
	public int getTotalReportedCasesTillNow() {
		return totalReportedCasesTillNow;
	}
	
	public int getTotalReportedCasesToday() {
		return totalReportedCasesToday;
	}


	public List<LocationStats> getAllStats() {
		return allStats;
	}


	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		
		totalReportedCasesTillNow = 0;
		totalReportedCasesToday = 0;
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		//System.out.println("Service  start");
		
		
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
			if(record.get("Province/State").equals("")) {
				locationStat.setState("NA");
			}	
			else {
				locationStat.setState(record.get("Province/State"));
			}
			locationStat.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size()-1));
			int prevDayCases = Integer.parseInt(record.get(record.size()-2));
			locationStat.setLatestTotalCases(latestCases);
			locationStat.setDiffFromPrevDay(latestCases-prevDayCases);
			totalReportedCasesToday += (latestCases-prevDayCases);
			totalReportedCasesTillNow += latestCases;
		    //String state = record.get("Province/State");
			
			//System.out.println(locationStat);
			newStats.add(locationStat);
		}
		this.allStats = newStats;
	}
}
