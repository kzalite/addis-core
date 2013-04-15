package org.drugis.trialverse.repositories;

import java.util.UUID;

import org.drugis.trialverse.model.Concept;
import org.drugis.trialverse.model.ConceptType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.repository.annotation.RestResource;

@RestResource(path = "concepts", rel = "concept")
public interface ConceptRepository extends CrudRepository<Concept, UUID>, ConceptRepositoryCustom {
	
	@RestResource(path="type", rel="types")
	public Page<Concept> findByType(@Param("type") ConceptType type, Pageable pageable);
	

}