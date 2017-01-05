package com.taskService.dbOperation.impl;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.taskService.constants.Constants;
import com.taskService.dbOperation.DbOperationService;
import com.taskService.model.GroupModel;
import com.taskService.model.TagModel;
import com.taskService.model.TaskModel;

@Service
public class DbOperationServiceImpl implements DbOperationService {

	@Autowired
	private Environment environment;

	@Autowired
	private MongoClient mongoClient;

	@Override
	public void createTag(String name, String tagTypeUser, String email) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.tagCollection"),
				BasicDBObject.class);
		
		TagModel tagmodel = new TagModel();
		tagmodel.setTagName(name);
		tagmodel.setTagType(tagTypeUser);
		tagmodel.setTagValue(email);
		Gson gson = new Gson();
		BasicDBObject basicobj = (BasicDBObject) JSON.parse(gson
				.toJson(tagmodel));
		
		coll.insertOne(basicobj);
		
	}

	@Override
	public JSONObject createGroup(GroupModel groupmodel) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.groupCollection"),
				BasicDBObject.class);

		Gson gson = new Gson();
		
		JSONObject json = new JSONObject();
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("groupName", groupmodel.getGroupName());
		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		
		BasicDBObject basicGroupObj = (BasicDBObject) JSON.parse(gson
				.toJson(groupmodel));
		
		if (obj.first() == null) {
			
			coll.insertOne(basicGroupObj);
			json.put("HTTPStatus", HttpStatus.OK);
			json.put("id", basicGroupObj.get("_id"));
			return json;
		}else{
			json.put("HTTPStatus", HttpStatus.FOUND);
			json.put("id", basicGroupObj.get("_id"));
			return json;
		}
	}

	@Override
	public JSONObject createTask(TaskModel taskModel) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.taskCollection"),
				BasicDBObject.class);

		Gson gson = new Gson();
		
		JSONObject json = new JSONObject();
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("taskTitle", taskModel.getTaskTitle());
		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		
		BasicDBObject basicGroupObj = (BasicDBObject) JSON.parse(gson
				.toJson(taskModel));
		
		if (obj.first() == null) {
			
			coll.insertOne(basicGroupObj);
			json.put("httpStatus", HttpStatus.OK);
			json.put("id", basicGroupObj.get("_id"));
			return json;
		}else{
			json.put("httpStatus", HttpStatus.FOUND);
			json.put("id", basicGroupObj.get("_id"));
			return json;
		}
	}

	@Override
	public TaskModel fetchTask(String taskName) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.taskCollection"),
				BasicDBObject.class);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("taskTitle", taskName);

		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		TaskModel taskModel = new TaskModel();
		if (obj.first() != null) {
			taskModel = (TaskModel) (new Gson()).fromJson(obj.first().toString(),
					TaskModel.class);
		}
		return taskModel;
	}

	@Override
	public void createTaskTag(TaskModel taskModel) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.tagCollection"),
				BasicDBObject.class);

		Gson gson = new Gson();
		JSONObject taskjson = new JSONObject();
		
		
		TagModel tagmodel = new TagModel();
		tagmodel.setTagName(taskModel.getTaskTitle());
		tagmodel.setTagType(Constants.TAG_TYPE_TASK);
		tagmodel.setTagValue(gson.toJson(taskModel));
		
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("tagName", taskModel.getTaskTitle());
		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		
		BasicDBObject basicTaskObj = (BasicDBObject) JSON.parse(gson
				.toJson(tagmodel));
		if (obj.first() == null) {
			
			coll.insertOne(basicTaskObj);
			
			//TaskModel taskmodel = gson.fromJson(basicTaskObj.get("tagValue").toString(), TaskModel.class);
	}
  }
}
