package com.taskService.utils;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

@Service
public class UUIDGeneratorForUser {

	public UUID generateUUID(){
		TimeBasedGenerator gen = Generators.timeBasedGenerator();
		UUID uuid = gen.generate();
		return uuid;
	}
}
