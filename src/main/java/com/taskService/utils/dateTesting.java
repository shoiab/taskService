package com.taskService.utils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class dateTesting {
	public static void main(String[] args){
		long addMinuteTime = 15*60*1000;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		String currentDate = simpleDateFormat.format(new Date());
		Date date = new Date();
		System.out.println(date.getTime());
		System.out.println(date.getTime()-addMinuteTime);
		String date1 = simpleDateFormat.format(new Date(System.currentTimeMillis() - addMinuteTime));
		System.out.println(currentDate);
		System.out.println(date1);
		
		System.out.println(LocalTime.MIDNIGHT.getSecond());
		String changedDate = simpleDateFormat.format(new Date(/*System.currentTimeMillis()*/0 - addMinuteTime));
		System.out.println(changedDate);
		/*
		int addMinuteTime = 15*60*1000;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		String currentDate = simpleDateFormat.format(new Date());
		System.out.println(currentDate);
		String defaultAddedDate = simpleDateFormat.format(new Date(System.currentTimeMillis()-addMinuteTime));
		System.out.println(defaultAddedDate);
		String date = simpleDateFormat.format(new Date());
*/	}
	

}
