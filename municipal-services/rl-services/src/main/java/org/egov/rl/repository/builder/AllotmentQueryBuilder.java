
package org.egov.rl.repository.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.egov.rl.models.AllotmentCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AllotmentQueryBuilder {
	
	private static final String BASE_QUERY ="SELECT\r\n"
			+ "    al.*,\r\n"
			+ "    ap.*,\r\n"
			+ "    doc.*,\r\n"
			+ "    doc_count.documentCount,\r\n"
			+ "    ap_count.applicantCount\r\n"
			+ "FROM eg_rl_allotment al\r\n"
			+ "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id\r\n"
			+ "INNER JOIN eg_rl_document doc ON al.id = doc.allotment_id\r\n"
			+ "LEFT JOIN (\r\n"
			+ "    SELECT allotment_id, COUNT(DISTINCT id) AS documentCount\r\n"
			+ "    FROM eg_rl_document\r\n"
			+ "    GROUP BY allotment_id\r\n"
			+ ") doc_count ON doc_count.allotment_id = al.id\r\n"
			+ "LEFT JOIN (\r\n"
			+ "    SELECT allotment_id, COUNT(DISTINCT id) AS applicantCount\r\n"
			+ "    FROM eg_rl_applicant\r\n"
			+ "    GROUP BY allotment_id\r\n"
			+ ") ap_count ON ap_count.allotment_id = al.id ";
//		    "SELECT al.*, ap.*, count(ap.*) as applicantCount,doc.*,count(doc.*) as documentCount FROM eg_rl_allotment al " +
//		    "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id INNER JOIN eg_rl_document doc ON al.id = doc.allotment_id ";
	
//	private final String GROUPBY_QUERY = " GROUP BY al.id, ap.id , doc.id;";

	public String getAllotmentSearchById(AllotmentCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder subQuery = new StringBuilder("");
		List<Object> subQueryParams = new ArrayList<>();


		if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" al.tenant_id = ? ");
			subQueryParams.add(criteria.getTenantId());
		}
		if (!CollectionUtils.isEmpty(criteria.getAllotmentIds())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" al.id IN (").append(createQuery(criteria.getAllotmentIds())).append(" ) ");
			addToPreparedStatement(subQueryParams, criteria.getAllotmentIds());
		}
//		
		// Now build the main query
		StringBuilder mainQuery = new StringBuilder(BASE_QUERY);
		mainQuery.append(subQuery);
//
//		// Add WHERE clause with subquery
////		mainQuery.append(" WHERE ptr.id IN (       al.tenant_id = ? AND ");
//		mainQuery.append(" WHERE al.id IN (");
//		mainQuery.append(subQuery);
//		mainQuery.append(" ) ");

		// Add all subquery parameters to the main prepared statement list
		preparedStmtList.addAll(subQueryParams);

		// Order the final result
//		mainQuery.append(GROUPBY_QUERY);

		return mainQuery.toString();
	}

	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
		if (preparedStmtList.isEmpty()) {
			query.append("WHERE");
		} else {
			query.append("AND");
		}
    }


	private String createQuery(Set<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, Set<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}
}

////    private static final String ALLOTMENT_DEFAULTER_SEARCH = "SELECT al.*, ap.*, doc.* FROM eg_rl_allotment al "
////            + "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id "
////            + "INNER JOIN eg_rl_document doc ON al.id = doc.allotment_id "
////            + "WHERE al.tenant_id = ? AND al.id IN (%s)";
////    private static final String ALLOTMENT_DEFAULTER_SEARCH =
////    	    "SELECT al.*, ap.* FROM eg_rl_allotment al " +
////    	    "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id " +
//////    	    "INNER JOIN eg_rl_document doc ON al.id = doc.allotment_id " +
////    	    "WHERE al.tenant_id = ? AND al.id IN (%s)";
////    private static final String ALLOTMENT_DEFAULTER_SEARCH ="SELECT al.*,ap.* FROM eg_rl_allotment al INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id where al.id='233be143-a73e-40f7-9ec9-4d9216699b3e'";
//	private static final String ALLOTMENT_DEFAULTER_SEARCH =
//		    "SELECT al.*, ap.* FROM eg_rl_allotment al " +
//		    "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id " +
//		    "WHERE al.tenant_id = ? AND al.id IN (%s)";
//
//		public String getAllotmentDetailsById(AllotmentCriteria criteria, List<Object> preparedStmtList) {
//		    Set<String> allotmentIds = criteria.getAllotmentIds();
//
//		    if (CollectionUtils.isEmpty(allotmentIds)) {
//		        throw new IllegalArgumentException("Allotment IDs cannot be empty");
//		    }
//
//		    preparedStmtList.add(criteria.getTenantId());
//
//		    StringJoiner joiner = new StringJoiner(", ");
//		    for (String id : allotmentIds) {
//		        joiner.add("?");
//		        preparedStmtList.add(id);
//		    }
//
//		    String query = String.format(ALLOTMENT_DEFAULTER_SEARCH, joiner.toString());
//
//		    return query;
//		}
//
//}


//package org.egov.pt.repository.builder;
//
//import java.time.Instant;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.time.Instant;
//import org.egov.pt.config.PropertyConfiguration;
//import org.egov.pt.consumer.NotificationConsumer;
//import org.egov.pt.models.AllotmentCriteria;
//import org.egov.pt.models.Property;
//import org.egov.pt.models.PropertyCriteria;
//import org.egov.pt.models.enums.Status;
//import org.egov.pt.web.contracts.PropertyRequest;
//import org.egov.tracer.model.CustomException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.ObjectUtils;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//public class AllotmentQueryBuilder {
//
//	@Autowired
//	private PropertyConfiguration config;
//
//	private static final String SELECT = "SELECT ";
//	private static final String INNER_JOIN = "INNER JOIN";
//	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
//	private static final String AND_QUERY = " AND ";
//
//	private static final String ALLOTMENT_DEFAULTER_SEARCH = "select *,al.* as user,doc.* as docList FROM eg_rl_allotment al "
//			+ "INNER JOIN eg_rl_applicant ap ON al.id = ap.allotment_id "
//			+ "INNER JOIN eg_rl_document doc ON al.id = doc.allotment_id "
//			+ "where al.tenant_id=? and al.id = ?";
////			"select * from (SELECT property.id as pid, property.propertyid, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.createdtime as pcreatedtime, property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder, address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails, owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus,owner.additionaldetails as oadditionaldetails, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime, unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime  FROM EG_PT_PROPERTY property INNER JOIN EG_PT_ADDRESS address         ON property.id = address.propertyid INNER JOIN EG_PT_OWNER owner             ON property.id = owner.propertyid  LEFT OUTER JOIN EG_PT_UNIT unit ON property.id =  unit.propertyid  WHERE  property.tenantId= ?  AND property.usagecategory like ? AND address.locality = ? AND owner.status = ? AND property.status = ?) as propertydata"
////			+ " LEFT OUTER JOIN (select consumercode,sum(taxdue) as taxDue,STRING_AGG(year || '(Rs.' || taxdue || ')',',')  as taxDueYear from ( select d.consumercode, (to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY')) as year ,  sum(dd.taxamount)-sum(dd.collectionamount) as taxdue from egbs_demanddetail_v1 dd, egbs_demand_v1 d "
////			+ " where dd.demandid=d.id and d.status!='CANCELLED' and dd.tenantid='pg.citya' and d.tenantid='pg.citya' and (to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY')) != ? group by d.consumercode,(to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY'))) tax where taxDue>0 group by tax.consumercode) as taxdata on propertydata.propertyid=taxdata.consumercode";
//
//	
//	public String getAllotmentDetailsById(AllotmentCriteria criteria, List<Object> preparedStmtList) {
//
//		StringBuilder builder = new StringBuilder(ALLOTMENT_DEFAULTER_SEARCH);
//		preparedStmtList.add(criteria.getTenantId());
////		if (criteria.getPropertyType().equals("ALL"))
////			preparedStmtList.add("%%");
////		else
////			preparedStmtList.add("%" + criteria.getPropertyType().toUpperCase() + "%");
//		preparedStmtList.add(criteria.getAllotmentIds());
////		preparedStmtList.add(Status.ACTIVE.toString());
////		preparedStmtList.add(currYearS);
//
//		return builder.toString();
//	}
//}
