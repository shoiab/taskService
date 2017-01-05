package com.taskService.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taskService.service.data.TagService;

@RestController
public class TagController {

	@Autowired
	TagService tagservice;

	@RequestMapping(value= "/fetchTag", method = RequestMethod.GET)
	public @ResponseBody SolrDocumentList fetchTag(
			@RequestHeader(value = "auth_key") String auth_key,
			@RequestParam(value = "search") String searchVal,
			@RequestParam(value = "field") String searchField)
			throws NoSuchAlgorithmException, SolrServerException, IOException {

		return tagservice.fetchTag(searchVal, searchField);

	}
	
	@RequestMapping(value= "/getAllUsers", method = RequestMethod.GET)
	public @ResponseBody SolrDocumentList getAllUsers(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {

		return tagservice.getAllUsers();

	}
	
	@RequestMapping(value= "/getAllGroups", method = RequestMethod.GET)
	public @ResponseBody SolrDocumentList getAllGroups(
			@RequestHeader(value = "auth_key") String auth_key)
			throws NoSuchAlgorithmException, SolrServerException, IOException {

		return tagservice.getAllGroups();

	}
}
