package com.taskService.service.data;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;

public interface TagService {

	SolrDocumentList fetchTag(String searchVal, String searchField) throws SolrServerException, IOException;

	public SolrDocumentList getAllUsers() throws SolrServerException, IOException;

	public SolrDocumentList getAllGroups() throws SolrServerException, IOException;

	void createTag(String tagName, String tagType, String tagValue, String id)
			throws SolrServerException, IOException;

}
