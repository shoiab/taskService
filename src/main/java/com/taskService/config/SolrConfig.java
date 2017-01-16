package com.taskService.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrConfig {
	
	@Value("${solr.url}")
	private String solrurl;
	
	private HttpSolrClient solrserver;
	
	@PostConstruct
	private void init() throws SolrServerException, IOException {	
		
		solrserver = new HttpSolrClient(this.solrurl);
	}
	
	public HttpSolrClient getSolrClient(){
		return this.solrserver;
	}

}
