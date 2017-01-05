package com.taskService.service.data.impl;

import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.taskService.constants.Constants;
import com.taskService.dbOperation.DbOperationService;
import com.taskService.model.GroupModel;
import com.taskService.service.data.CSVGroupDataService;
import com.taskService.solr.SearchHandler;

@Service
public class CSVGroupDataServiceImpl implements CSVGroupDataService{
	
	@Autowired
	DbOperationService dbservice;
	
	@Autowired
	private SearchHandler solrService;

	@Override
	public HttpStatus createGroup(String emails, String groupName) throws SolrServerException, IOException {
		GroupModel groupmodel = new GroupModel();
		groupmodel.setDateOfCreation(new Date());
		groupmodel.setGroupName(groupName);
		groupmodel.setGroupMailList(emails);
		JSONObject groupobj = dbservice.createGroup(groupmodel);
		if(groupobj.get("HTTPStatus") == HttpStatus.OK){
			solrService.createTag(groupName, Constants.TAG_TYPE_GROUP, emails, groupobj.get("id").toString());
			dbservice.createTag(groupName, Constants.TAG_TYPE_GROUP, emails);
		}
		return (HttpStatus) groupobj.get("HTTPStatus");
	}

}
