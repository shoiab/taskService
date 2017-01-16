package com.taskService.solrService;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;

import com.taskService.model.TaskModel;

public interface SearchHandler {

	SolrDocumentList fetchTag(String searchVal, String searchField) throws SolrServerException, IOException;

	void deleteTag(String fieldName, String fieldValue) throws SolrServerException, IOException;

	void createTag(String tagName, String tagType, String tagValue, String id) throws SolrServerException, IOException;

	SolrDocumentList getAllUsers() throws SolrServerException, IOException;

	SolrDocumentList getAllGroups() throws SolrServerException, IOException;

	void createTask(TaskModel taskModel) throws SolrServerException, IOException;

	SolrDocumentList getAllTasks(String userEmail) throws SolrServerException, IOException;

	JSONObject getCreatedTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedCreatedTasks(String email) throws SolrServerException, IOException;

	JSONObject getPendingTasks(String email) throws SolrServerException, IOException;

	JSONObject getCompletedTasks(String email) throws SolrServerException, IOException;

}
