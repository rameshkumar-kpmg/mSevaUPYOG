
package org.egov.pt.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.egov.pt.models.AllotmentCriteria;
import org.egov.pt.models.AllotmentDetails;
import org.egov.pt.models.AllotmentRequest;
import org.egov.pt.repository.builder.AllotmentQueryBuilder;
import org.egov.pt.repository.rowmapper.AllotmentRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AllotmentRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AllotmentQueryBuilder queryBuilder;

	@Autowired
	private AllotmentRowMapper rowMapper;
    
	
	public AllotmentDetails getAllotmentByIds(AllotmentCriteria criterias) {

		List<Object> preparedStmtList = new ArrayList<>();
		
		String query = queryBuilder.getAllotmentSearchById(criterias, preparedStmtList);

        log.info("Executing Query: {}", query);
        log.info("With Parameters: {}", preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
//      AllotmentRequest result =  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
//      result.setRequestInfo(null);
//      return result;

	}

}
