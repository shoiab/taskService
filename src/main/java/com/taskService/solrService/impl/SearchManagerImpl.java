package com.taskService.solrService.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.taskService.config.SolrConfig;
import com.taskService.constants.Constants;
import com.taskService.model.TaskAssigneeModel;
import com.taskService.model.TaskModel;
import com.taskService.solrService.SearchHandler;

@Service
public class SearchManagerImpl implements SearchHandler {
	
	static Logger logger = Logger.getLogger(SearchManagerImpl.class.getName());

	@Autowired
	Environment env;
	
	@Autowired
	private SolrConfig solrconfig;
	
	public SolrDocumentList fetchTag(String searchVal, String searchField)
			throws SolrServerException, IOException {
		/*String solrUrl = env.getProperty(Constants.SOLR_URL);

		HttpSolrClient server = new HttpSolrClient(solrUrl);*/
		HttpSolrClient server = solrconfig.getSolrClient();

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
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);

		//server.close();
		return docsans;
	}

	@Override
	public void deleteTag(String fieldName, String fieldValue)
			throws SolrServerException, IOException {

		HttpSolrClient server = solrconfig.getSolrClient();
		server.deleteByQuery(fieldName + ":" + fieldValue);
		server.commit();
	}

	@Override
	public void createTag(String tagName, String tagType, String tagValue, String id)
			throws SolrServerException, IOException {

		HttpSolrClient server = solrconfig.getSolrClient();
		SolrInputDocument tagdoc = new SolrInputDocument();

		tagdoc.addField("tagName", tagName);
		tagdoc.addField("tagType", tagType);
		tagdoc.addField("tagValue", tagValue);
		tagdoc.addField(Constants.TAG_TYPE_ID, id);

		server.add(tagdoc);
		server.commit();
	}

	@Override
	public SolrDocumentList getAllUsers() throws SolrServerException, IOException {

		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_USER + ")");
		//solrQuery.setFields("tagName", "tagType", "tagValue");
		/*solrQuery.setFields("tagValue");*/

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);

		return docsans;
	}

	@Override
	public SolrDocumentList getAllGroups() throws SolrServerException,
			IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_GROUP + ")");
		//solrQuery.setFields("tagName", "tagType", "tagValue");
		/*solrQuery.setFields("tagValue");*/

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);

		return docsans;
	}

	@Override
	public void createTask(TaskModel taskModel) throws SolrServerException, IOException {	
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		List<Map<String, String>> assigneelist = new ArrayList<Map<String, String>>();
		Map<String, String> assigneeMap;
		
		if(taskModel.getAssigneeList().size() > 0){
			for(TaskAssigneeModel recipientmodel : taskModel.getAssigneeList()){
				assigneeMap = new HashMap<String, String>();
				assigneeMap.put("type", recipientmodel.getAssigneeType());
				assigneeMap.put("recipient", recipientmodel.getAssignee());
				assigneelist.add(assigneeMap);
			}
		}		
		
		SolrInputDocument tagdoc = new SolrInputDocument();
		
		tagdoc.addField("tagName", taskModel.getTaskTitle());
		tagdoc.addField("tagType", Constants.TAG_TYPE_TASK);
		tagdoc.addField("assignees", assigneelist.toString());
		tagdoc.addField("taskDescription", taskModel.getDescription());
		tagdoc.addField(Constants.TAG_TYPE_ID, taskModel.getTaskId());
		tagdoc.addField("taskAssigner", taskModel.getTaskAssigner());
		tagdoc.addField("taskCreationDate", taskModel.getTaskCreationDate());
		tagdoc.addField("startDate", taskModel.getStartDate());
		tagdoc.addField("endDate", taskModel.getEndDate());
		tagdoc.addField("taskStatus", taskModel.getTaskStatus());
		tagdoc.addField("priority", taskModel.getPriority());
		tagdoc.addField("notificationTime", taskModel.getNotificationTime());

		server.add(tagdoc);
		server.commit();
	}

	@Override
	public SolrDocumentList getAllTasks(String taskCreator) throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskAssigner:(" + taskCreator + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);

		return docsans;
	}

	/*@Override
	public JSONObject getCreatedTasks(String email) throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskCreator:(" + email + ") AND " + "statusOfCompletion:(" + Constants.TASK_STATUS_OPEN + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);
		
		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		return resultobj;
	}*/

	/*@Override
	public JSONObject getCompletedCreatedTasks(String email) throws SolrServerException, IOException {

		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskCreator:(" + email + ") AND " + "statusOfCompletion:(" + Constants.TASK_STATUS_CLOSED + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);

		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		
		return resultobj;
	}

	@Override
	public JSONObject getPendingTasks(String email) throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "recipients:(" + "*" + email + "*" +  ") AND " + "statusOfCompletion:(" + Constants.TASK_STATUS_OPEN + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);
		
		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		
		return resultobj;
	}*/

	/*@Override
	public JSONObject getCompletedTasks(String email)
			throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "recipients:(" + "*" + email + "*" +  ") AND " + "statusOfCompletion:(" + Constants.TASK_STATUS_OPEN + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);
		
		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		
		return resultobj;
	}*/

	@Override
	public void updateTaskStatus(String email, String taskId, String taskStatus) throws SolrServerException, IOException {
		HttpSolrClient server = solrconfig.getSolrClient();	
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery
		.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
				+ "taskId:(" + taskId +  ")");
		solrQuery.setFields(Constants.SOLR_TAG_NAME, Constants.SOLR_TAG_TYPE,
				Constants.SOLR_TAG_VALUE, "id");

		logger.info("query = " + solrQuery.toString());
		QueryResponse rsp = server.query(solrQuery, METHOD.POST);
		
		
		if (rsp != null && rsp.getResults().size() > 0) {
			SolrDocumentList docsans = rsp.getResults();

			for (SolrDocument userdoc : docsans) {

				String id = userdoc.get("id").toString();
				server.deleteById(id);
				server.commit();
			}	
		}
		
	}

	@Override
	public JSONObject getTasksForStatus(String email, String taskStatus) throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "assignees:(" + "*" + email + "*" +  ") AND " + "taskStatus:(" + taskStatus + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);
		
		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		
		return resultobj;
	}

	@Override
	public JSONObject getAssignedTasksForStatus(String email, String taskStatus) throws SolrServerException, IOException {
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "taskAssigner:(" + email + ") AND " + "taskStatus:(" + taskStatus + ")");

		QueryResponse rsp = server.query(solrQuery, METHOD.GET);
		logger.info("query = " + solrQuery.toString());
		docsans = rsp.getResults();
		logger.info(docsans);
		
		JSONObject resultobj = new JSONObject();
		resultobj.put("taskCount", docsans.size());
		resultobj.put("taskList", docsans);
		return resultobj;
	}

}
