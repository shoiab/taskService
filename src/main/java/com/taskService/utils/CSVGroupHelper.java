package com.taskService.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CSVGroupHelper {
	
	static Logger logger = Logger.getLogger(CSVGroupHelper.class.getName());
	
	@Autowired
	Environment env;
	
	public String convertCSVToGroupList(){
		String csvFile = env.getProperty("groupLocation");
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		List<String> emaillist = new ArrayList<String>();

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] user = line.split(cvsSplitBy);

				logger.info("User [username= " + user[0] + " , emailid="
						+ user[1] + "password= " + user[2] + "]");
				
				emaillist.add(user[1]);
				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String emailListString = StringUtils.collectionToCommaDelimitedString(emaillist);
		return emailListString;

	}
}
