package com.ecosense.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.entity.Activity;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.ActivityRepo;

@Repository
@Transactional
public class ActivityRepoImpl implements ActivityRepo {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Activity insertActivity(Activity act) {
		em.persist(act);
		return act;
	}
	
	@Override
	public Activity findActivity(String actName) throws SimpleException {
		if (actName == null || actName.isEmpty()) {
			throw new SimpleException(SimpleResponseDTO.DATA_NOT_COMPLETE);
		}
		
		List<Activity> activity = new ArrayList<>();
		
		activity = em.createQuery("SELECT act FROM Activity act WHERE act.activityName LIKE :actName", Activity.class)
				.setParameter("actName", actName)
				.getResultList();
		
		return activity.isEmpty() ? null : activity.get(0);
	}
	
	@Override
	public List<Activity> getAllActivities() {
		
		List<Activity> activities = new ArrayList<>();
		activities = em.createNamedQuery("Activity.findAllOrderByActivityName", Activity.class).getResultList();
		
		return activities;
	}

}
