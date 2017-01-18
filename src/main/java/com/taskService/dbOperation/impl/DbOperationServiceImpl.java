package com.taskService.dbOperation.impl;

import org.apache.log4j.Logger;
import org.bson.Document;
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
	
	public static final Logger logger = Logger.getLogger(DbOperationServiceImpl.class.getName());

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
	public TaskModel fetchTask(String taskid) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.taskCollection"),
				BasicDBObject.class);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("taskid", taskid);

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
		tagmodel.setId(taskModel.getTaskId());
		
		
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

	@Override
	public TaskModel updateTaskStatus(String email, String taskId, String taskStatus) {
		
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.taskCollection"),
				BasicDBObject.class);	
		
		MongoCollection<BasicDBObject> tagcoll = db.getCollection(
				environment.getProperty("mongo.tagCollection"),
				BasicDBObject.class);

		Gson gson = new Gson();
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("taskId", taskId);
		whereQuery.put("assigneeList.assignee", email);

		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		TaskModel taskmodel = new TaskModel();
		if (obj.first() != null) {
			
			BasicDBObject taskobj = new BasicDBObject();
			taskobj = obj.first();
			
			logger.info("object :: "+taskobj);
			String assigner = taskobj.getString("taskAssigner");
			String assignee = taskobj.getString("assigneeList.assignee");
			
			if(!email.equals(assigner)){
				if(taskStatus.equals(Constants.TASK_STATUS_CLOSED) && assignee.equals(email)){
					return null;
				}
			}
			
			taskmodel = (TaskModel) (new Gson()).fromJson(taskobj.toString(),
					TaskModel.class);
			taskmodel.setTaskStatus(taskStatus);
			
			Document newDocument = new Document();
			Document searchQuery = new Document().append("taskId", taskId).append("assigneeList.assignee", email);
			newDocument.put("$set", new BasicDBObject("taskStatus", taskStatus));
			coll.updateOne(searchQuery, newDocument);
			
			Document tagDocument = new Document();
			Document tagSearchQuery = new Document().append("id", taskId);
			tagDocument.put("$set", new BasicDBObject("tagValue", gson.toJson(taskmodel)));
			tagcoll.updateOne(tagSearchQuery, tagDocument);
			
			return taskmodel;
		}
		return null;		
	}

	@Override
	public JSONObject closeTask(String email, String taskId, String taskStatus) {
		MongoDatabase db = mongoClient.getDatabase(environment
				.getProperty("mongo.dataBase"));

		MongoCollection<BasicDBObject> coll = db.getCollection(
				environment.getProperty("mongo.taskCollection"),
				BasicDBObject.class);	
		
		MongoCollection<BasicDBObject> tagcoll = db.getCollection(
				environment.getProperty("mongo.tagCollection"),
				BasicDBObject.class);

		Gson gson = new Gson();
		JSONObject statusobj = new JSONObject();
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("taskId", taskId);
		whereQuery.put("taskAssigner", email);

		FindIterable<BasicDBObject> obj = coll.find(whereQuery);
		TaskModel taskmodel = new TaskModel();
		if (obj.first() != null) {
			
			BasicDBObject taskobj = new BasicDBObject();
			taskobj = obj.first();
			
			String status = taskobj.getString("taskStatus");
			if(!status.equals(Constants.TASK_STATUS_COMPLETED)){
				statusobj.put("status", HttpStatus.CONFLICT.value());
				statusobj.put("message", "Task is not completed yet!");
				statusobj.put("taskobj", null);
				return statusobj;
			}
			
			taskmodel = (TaskModel) (new Gson()).fromJson(obj.first().toString(),
					TaskModel.class);
			taskmodel.setTaskStatus(taskStatus);
			
			Document newDocument = new Document();
			Document searchQuery = new Document().append("taskId", taskId).append("taskAssigner", email).append("taskStatus", Constants.TASK_STATUS_COMPLETED);
			newDocument.put("$set", new BasicDBObject("taskStatus", taskStatus));
			coll.updateOne(searchQuery, newDocument);
			
			Document tagDocument = new Document();
			Document tagSearchQuery = new Document().append("id", taskId);
			tagDocument.put("$set", new BasicDBObject("tagValue", gson.toJson(taskmodel)));
			tagcoll.updateOne(tagSearchQuery, tagDocument);
			
			statusobj.put("status", HttpStatus.OK.value());
			statusobj.put("message", "Task updated successfully");
			statusobj.put("taskobj", taskmodel);
			
			return statusobj;
		}
		
		statusobj.put("status", HttpStatus.NOT_FOUND.value());
		statusobj.put("message", "Assigner can only close a task");
		statusobj.put("taskobj", null);
		return statusobj;
		
	}
}
