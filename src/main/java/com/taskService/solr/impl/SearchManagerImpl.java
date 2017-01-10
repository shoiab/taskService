package com.taskService.solr.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.taskService.constants.Constants;
import com.taskService.model.TaskModel;
import com.taskService.model.TaskRecipientModel;
import com.taskService.solr.SearchHandler;

@Service
public class SearchManagerImpl implements SearchHandler {

	@Autowired
	Environment env;
	
	public SolrDocumentList fetchTag(String searchVal, String searchField)
			throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);

		HttpSolrClient server = new HttpSolrClient(solrUrl);

		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(searchVal + "*");
		solrQuery
				.setQuery("(tagValue:(" + "*" + searchVal + "*" + ") AND "
						+ "tagType:(" + searchField + ")) OR " + "(tagName:("
						+ "*" + searchVal + "*" + ") AND " + "tagType:("
						+ searchField + "))");
		solrQuery.setFields("tagName", "tagType", "tagValue");
		//solrQuery.setFields("tagName","tagValue");

		QueryResponse rsp = server.query(solrQuery, METHOD.POST);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

	@Override
	public void deleteTag(String fieldName, String fieldValue)
			throws SolrServerException, IOException {

		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		server.deleteByQuery(fieldName + ":" + fieldValue);
		server.commit();
		server.close();
	}

	@Override
	public void createTag(String tagName, String tagType, String tagValue, String id)
			throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);

		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrInputDocument tagdoc = new SolrInputDocument();

		tagdoc.addField("tagName", tagName);
		tagdoc.addField("tagType", tagType);
		tagdoc.addField("tagValue", tagValue);
		tagdoc.addField(Constants.TAG_TYPE_ID, id);

		server.add(tagdoc);
		server.commit();
		server.close();

	}

	@Override
	public SolrDocumentList getAllUsers() throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_USER + ")");
		//solrQuery.setFields("tagName", "tagType", "tagValue");
		/*solrQuery.setFields("tagValue");*/

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

	@Override
	public SolrDocumentList getAllGroups() throws SolrServerException,
			IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_GROUP + ")");
		//solrQuery.setFields("tagName", "tagType", "tagValue");
		/*solrQuery.setFields("tagValue");*/

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

	@Override
	public void createTask(TaskModel taskModel) throws SolrServerException, IOException {		
		
		List<Map<String, String>> recipientlist = new ArrayList<Map<String, String>>();
		Map<String, String> recipientMap;
		
		if(taskModel.getRecipientList().size() > 0){
			for(TaskRecipientModel recipientmodel : taskModel.getRecipientList()){
				recipientMap = new HashMap<String, String>();
				recipientMap.put("type", recipientmodel.getRecipientType());
				recipientMap.put("recipient", recipientmodel.getRecipient());
				recipientlist.add(recipientMap);
			}
		}		
		
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrInputDocument tagdoc = new SolrInputDocument();
		
		tagdoc.addField("tagName", taskModel.getTaskTitle());
		tagdoc.addField("tagType", Constants.TAG_TYPE_TASK);
		tagdoc.addField("recipients", recipientlist.toString());
		tagdoc.addField("taskDescription", taskModel.getDescription());
		tagdoc.addField(Constants.TAG_TYPE_ID, taskModel.getTaskid());
		tagdoc.addField("taskCreator", taskModel.getTaskCreator());
		tagdoc.addField("taskCreationDate", taskModel.getTaskCreationDate());
		tagdoc.addField("dateOfStart", taskModel.getDateOfStart());
		tagdoc.addField("dateOfCompletion", taskModel.getDateOfCompletion());
		tagdoc.addField("statusOfCompletion", taskModel.getStatusOfCompletion());
		tagdoc.addField("priority", taskModel.getPriority());
		tagdoc.addField("notificationTime", taskModel.getNotificationTime());

		server.add(tagdoc);
		server.commit();
		server.close();
	}

	@Override
	public SolrDocumentList getAllTasks(String taskCreator) throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskCreator:(" + taskCreator + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

	@Override
	public SolrDocumentList createdTasks(String email) throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskCreator:(" + email + ") AND " + "statusOfCompletion:(" + "open" + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

	@Override
	public SolrDocumentList completedTasks(String email) throws SolrServerException, IOException {
		String solrUrl = env.getProperty(Constants.SOLR_URL);
		HttpSolrClient server = new HttpSolrClient(solrUrl);
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskCreator:(" + email + ") AND " + "statusOfCompletion:(" + "closed" + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		System.out.println("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		System.out.println(docsans);

		server.close();
		return docsans;
	}

}
