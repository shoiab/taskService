package com.taskService.solrService.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
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
import org.springframework.http.HttpStatus;
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
	public JSONObject getAllUsers() throws SolrServerException, IOException {

		HttpSolrClient server = solrconfig.getSolrClient();
		JSONObject statusobj = new JSONObject();
		
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
		
		statusobj.put("userCount", docsans.size());
		statusobj.put("users", docsans);

		return statusobj;
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
	public void createTask(TaskModel taskModel) throws SolrServerException, IOException, ParseException {	
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		List<Map<String, String>> assigneelist = new ArrayList<Map<String, String>>();
		Map<String, String> assigneeMap;
		
		//Map<String, String> taskmap = (Map) taskModel;
		
		if(taskModel.getAssigneeList().size() > 0){
			for(TaskAssigneeModel recipientmodel : taskModel.getAssigneeList()){
				assigneeMap = new HashMap<String, String>();
				assigneeMap.put("type", recipientmodel.getAssigneeType());
				assigneeMap.put("recipient", recipientmodel.getAssignee());
				assigneelist.add(assigneeMap);
			}
		}		
		
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd kk:mm:ss");

		String taskCreationDate = taskModel.getTaskCreationDate();
		Date now = simpleDateFormat.parse(taskCreationDate);
		taskModel.setTaskCreationDate(org.apache.solr.common.util.DateUtil.
                getThreadLocalDateFormat().format(now));
		
		if(!taskModel.getEndDate().isEmpty()){
			String taskEndDate = taskModel.getEndDate();
			Date endDate = simpleDateFormat.parse(taskEndDate);
			taskModel.setEndDate(org.apache.solr.common.util.DateUtil.
	                getThreadLocalDateFormat().format(endDate));
		}
		
		if(!taskModel.getNotificationTime().isEmpty()){
			String taskNotificationTime = taskModel.getNotificationTime();
			Date notificationTime = simpleDateFormat.parse(taskNotificationTime);
			taskModel.setNotificationTime(org.apache.solr.common.util.DateUtil.
	                getThreadLocalDateFormat().format(notificationTime));
		}
		
		SolrInputDocument tagdoc = new SolrInputDocument();
		
		tagdoc.addField("tagName", taskModel.getTaskTitle());
		tagdoc.addField("tagType", Constants.TAG_TYPE_TASK);
		tagdoc.addField("assignees", assigneelist.toString());
		tagdoc.addField("taskDescription", taskModel.getDescription());
		tagdoc.addField(Constants.TAG_TYPE_ID, taskModel.getTaskId());
		tagdoc.addField("taskAssigner", taskModel.getTaskAssigner());
		tagdoc.addField("taskCreationDate", taskModel.getTaskCreationDate());
		//tagdoc.addField("startDate", taskModel.getStartDate());
		tagdoc.addField("endDate", taskModel.getEndDate());
		tagdoc.addField("taskStatus", taskModel.getTaskStatus());
		tagdoc.addField("priority", taskModel.getPriority());
		tagdoc.addField("notificationTime", taskModel.getNotificationTime());

		server.add(tagdoc);
		server.commit();
	}

	@Override
	public SolrDocumentList getAllTasks(String email) throws SolrServerException, IOException {
		
		HttpSolrClient server = solrconfig.getSolrClient();
		
		SolrDocumentList docsans = new SolrDocumentList();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "assignees:(" + "*" + email + "*" + ")");

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
	public JSONObject getTasksForStatusv2(String email, String taskStatus) throws SolrServerException, IOException {
		
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
	
	@Override
	public JSONObject getTasksCountv2(String email) throws SolrServerException, IOException, ParseException {
		
		int overduecount = getOverdueTaskCount(email);
		int newTaskCount = getOpenTaskCount(email);
		int closedCount = getClosedCount(email);
		int todayCount = getTodayTaskCount(email);

		JSONObject resultobj = new JSONObject();
		resultobj.put("status", HttpStatus.OK.value());
		resultobj.put("message", "success");
		resultobj.put("overdue", overduecount);
		resultobj.put("new", newTaskCount);
		resultobj.put("closed", closedCount);
		resultobj.put("today", todayCount);
		
		return resultobj;
	}

	private int getTodayTaskCount(String email) throws SolrServerException, IOException, ParseException {
		HttpSolrClient server = solrconfig.getSolrClient();
		SolrDocumentList todayTaskDoc = new SolrDocumentList();
		SolrQuery todayTaskSolrQuery = new SolrQuery();
		
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd 00:00:00");

		String currentDate = simpleDateFormat.format(new Date());
		Date today = simpleDateFormat.parse(currentDate);
		
		Date tomDate = DateUtils.addDays(new Date(), 1);
		String tomoDate = simpleDateFormat.format(tomDate);
		Date tomorrow = simpleDateFormat.parse(tomoDate);
		
		String fromDate = org.apache.solr.common.util.DateUtil.
                getThreadLocalDateFormat().format(today);
		
		String toDate = org.apache.solr.common.util.DateUtil.
                getThreadLocalDateFormat().format(tomorrow);
				
		todayTaskSolrQuery
		.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
				+ "assignees:(" + "*"+email+"*" + ") AND " + "taskStatus:(" + Constants.TASK_STATUS_OPEN + ") AND " + "endDate:[" + fromDate  + " TO " +  toDate + "]");
		
		QueryResponse rsp = server.query(todayTaskSolrQuery, METHOD.GET);
		logger.info("query = " + todayTaskSolrQuery.toString());
		todayTaskDoc = rsp.getResults();
		logger.info(todayTaskDoc);
		
		return todayTaskDoc.size();
	}

	private int getClosedCount(String email) throws SolrServerException, IOException {
		HttpSolrClient server = solrconfig.getSolrClient();
		SolrDocumentList closedTaskDoc = new SolrDocumentList();
		SolrQuery closedTaskSolrQuery = new SolrQuery();
		closedTaskSolrQuery
		.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
				+ "assignees:(" + "*"+email+"*" + ") AND " + "taskStatus:(" + Constants.TASK_STATUS_CLOSED + ")");
		
		QueryResponse rsp = server.query(closedTaskSolrQuery, METHOD.GET);
		logger.info("query = " + closedTaskSolrQuery.toString());
		closedTaskDoc = rsp.getResults();
		logger.info(closedTaskDoc);
		
		return closedTaskDoc.size();
	}

	private int getOpenTaskCount(String email) throws SolrServerException, IOException, ParseException {
		HttpSolrClient server = solrconfig.getSolrClient();
		SolrDocumentList openTaskDoc = new SolrDocumentList();
		SolrQuery openTaskSolrQuery = new SolrQuery();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");
		String currentDate = simpleDateFormat.format(DateUtils.addDays(new Date(), 1));
		Date now = simpleDateFormat.parse(currentDate);
		
		String toDate = org.apache.solr.common.util.DateUtil.
                getThreadLocalDateFormat().format(now);
		
		openTaskSolrQuery
		.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
				+ "assignees:(" + "*"+email+ "*" + ") AND " + "taskStatus:(" + Constants.TASK_STATUS_OPEN + ")");
		
		QueryResponse rsp = server.query(openTaskSolrQuery, METHOD.GET);
		logger.info("query = " + openTaskSolrQuery.toString());
		openTaskDoc = rsp.getResults();
		logger.info(openTaskDoc);
		
		return openTaskDoc.size();
	}

	private int getOverdueTaskCount(String email) throws ParseException, SolrServerException, IOException {
		HttpSolrClient server = solrconfig.getSolrClient();
		SolrDocumentList overduedoc = new SolrDocumentList();
		SolrQuery overduesolrQuery = new SolrQuery();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd kk:mm:ss");
		String currentDate = simpleDateFormat.format(new Date());
		Date now = simpleDateFormat.parse(currentDate);
		
		String toDate = org.apache.solr.common.util.DateUtil.
                getThreadLocalDateFormat().format(now);
		overduesolrQuery
				.setQuery("tagType:(" + Constants.TAG_TYPE_TASK + ") AND "
						+ "assignees:(" + "*"+email+"*" + ") AND " + "endDate:[*" + " TO " + toDate + "] AND taskStatus:(" + Constants.TASK_STATUS_OPEN + ")");

		QueryResponse rsp = server.query(overduesolrQuery, METHOD.GET);
		logger.info("query = " + overduesolrQuery.toString());
		overduedoc = rsp.getResults();
		logger.info(overduedoc);
		
		return overduedoc.size();
	}

}
