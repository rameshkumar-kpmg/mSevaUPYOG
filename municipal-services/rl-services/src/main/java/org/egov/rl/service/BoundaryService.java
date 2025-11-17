package org.egov.rl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.rl.config.RentLeaseConfiguration;
import org.egov.rl.models.AllotmentRequest;
import org.egov.rl.repository.ServiceRequestRepository;
import org.egov.rl.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@Service
public class BoundaryService {

	@Value("${egov.location.host}")
	private String locationHost;

	@Value("${egov.location.context.path}")
	private String locationContextPath;

	@Value("${egov.location.endpoint}")
	private String locationEndpoint;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private RentLeaseConfiguration configs;

	@Autowired
	RestTemplate restTemplate;// = new RestTemplate();

	
		
	public JsonNode loadPropertyData(AllotmentRequest allotementRequest) {
		String propertyId = Optional.ofNullable(allotementRequest.getAllotment().getPropertyId()).orElse(null);
		String tenantId = Optional.ofNullable(allotementRequest.getAllotment().getTenantId()).orElse(null);
		JsonNode body = null;
		try {
//System.out.println("-----"+allotementRequest.getRequestInfo());
			MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
			mdmsCriteriaReq.setRequestInfo(allotementRequest.getRequestInfo()); // from your context
			MdmsCriteria mdmsCriteria = new MdmsCriteria();
			mdmsCriteria.setTenantId(tenantId);
			ModuleDetail moduleDetail = new ModuleDetail();
			moduleDetail.setModuleName("rentAndLease");
			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName("RLProperty");
			moduleDetail.setMasterDetails(Arrays.asList(masterDetail));
			mdmsCriteria.setModuleDetails(Arrays.asList(moduleDetail));
			mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);

			String mdmsUrl = configs.getMdmsHost() + configs.getMdmsEndpoint();// "http://<mdms-host>/egov-mdms-service/v1/_search";
			ResponseEntity<JsonNode> response = restTemplate.postForEntity(mdmsUrl, mdmsCriteriaReq, JsonNode.class);
			body =response.getBody();


			if (body.isArray()) {
	            for (JsonNode node : body) {
	                if (node.has("propertyId") && node.get("propertyId").asText().equals(propertyId)) {
	                	body = node; // return only the matched property
	                break;
	                }
	            }
			}
			if (body.isEmpty()) {
				throw new CustomException("PROPERTY ID TENANT ID INFO ERROR",
						"startDate cannot be wrong, please provide the valid propertyId and tenentId information");
			}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CustomException("PROPERTY LOADING ERROR",
						"property loading error from mdms");
			
			}
		return body;
	}


}
