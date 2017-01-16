package com.taskService.service.data.impl;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskService.service.data.TagService;
import com.taskService.solrService.SearchHandler;

@Service
public class TagServiceImpl implements TagService{
	
	@Autowired
	private SearchHandler solrService;
	
	
	@Override
	public SolrDocumentList fetchTag(String searchVal, String searchField) throws SolrServerException, IOException {
		return solrService.fetchTag(searchVal, searchField);
	}

	@Override
	public void createTag(String tagName, String tagType, String tagValue, String id)
			throws SolrServerException, IOException {
		solrService.createTag(tagName, tagType, tagValue, id);
		
	}

	@Override
	public SolrDocumentList getAllUsers() throws SolrServerException, IOException {
		return solrService.getAllUsers();
	}

	@Override
	public SolrDocumentList getAllGroups() throws SolrServerException, IOException {
		return solrService.getAllGroups();
	}

}
