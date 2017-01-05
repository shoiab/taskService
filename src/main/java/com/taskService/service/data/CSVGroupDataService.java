package com.taskService.service.data;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;

public interface CSVGroupDataService {

	HttpStatus createGroup(String emaillist, String groupName) throws SolrServerException, IOException;

}
