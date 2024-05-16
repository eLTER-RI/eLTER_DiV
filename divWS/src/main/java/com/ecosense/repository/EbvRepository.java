package com.ecosense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecosense.entity.Ebv;

public interface EbvRepository extends JpaRepository<Ebv, Integer>, EbvRepo {

	Optional<Ebv> findEbvByEbvId(Integer ebvId);
	
	@Query("SELECT e.creatorName FROM Ebv e ORDER BY e.creatorName")
	List<String> findAllCreators();

}
