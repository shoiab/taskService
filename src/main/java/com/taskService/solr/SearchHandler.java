package com.taskService.solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;

import com.taskService.model.TaskModel;

public interface SearchHandler {

	SolrDocumentList fetchTag(String searchVal, String searchField) throws SolrServerException, IOException;

	void deleteTag(String fieldName, String fieldValue) throws SolrServerException, IOException;

	void createTag(String tagName, String tagType, String tagValue, String id) throws SolrServerException, IOException;

	SolrDocumentList getAllUsers() throws SolrServerException, IOException;

	SolrDocumentList getAllGroups() throws SolrServerException, IOException;

	void createTask(TaskModel taskModel) throws SolrServerException, IOException;

	SolrDocumentList getAllTasks(String userEmail) throws SolrServerException, IOException;

	SolrDocumentList createdTasks(String email) throws SolrServerException, IOException;

	SolrDocumentList completedTasks(String email) throws SolrServerException, IOException;

}
