package com.ecosense.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ecosense.entity.Activity;
import com.ecosense.exception.SimpleException;

@Repository
public interface ActivityRepo {

	Activity insertActivity(Activity act);
	
	Activity findActivity(String actName) throws SimpleException;
	
	List<Activity> getAllActivities();
}
