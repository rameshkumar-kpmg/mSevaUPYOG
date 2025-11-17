package org.egov.pt.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.AllotmentCriteria;
import org.egov.pt.models.AllotmentDetails;
import org.egov.pt.models.AllotmentRequest;
import org.egov.pt.producer.PropertyProducer;
//import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.EncryptionDecryptionUtil;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.UnmaskingUtil;
import org.egov.pt.validator.AllotmentValidator;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.workflow.AllotmentWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AllotmentService {
	
	

	@Autowired
	private BillingService billingService;

	@Autowired
	private UnmaskingUtil unmaskingUtil;

	@Autowired
	private PropertyProducer producer;

	@Autowired
	private NotificationService notifService;

	@Autowired
	private PropertyConfiguration config;
	
	@Autowired
	private AllotmentEnrichmentService allotmentEnrichmentService;

	@Autowired
	private PropertyValidator propertyValidator;
	
	@Autowired
	private AllotmentValidator allotmentValidator;

	@Autowired
	private UserService userService;

	@Autowired
	private AllotmentWorkflowService wfService;

	@Autowired
	private PropertyUtil util;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CalculationService calculatorService;

	@Autowired
	private FuzzySearchService fuzzySearchService;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;
	
	@Autowired
	BoundaryService boundaryService;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */

	public AllotmentDetails allotmentCreate(AllotmentRequest allotmentRequest){

		allotmentValidator.validateAllotementRequest(allotmentRequest);
		allotmentEnrichmentService.enrichCreateRequest(allotmentRequest);
		
		if (config.getIsWorkflowEnabled()) {
//			wfService.updateWorkflowStatus(allotmentRequest);
		} else {
			allotmentRequest.getAllotment().setStatus("ACTIVE");
		}
		String previousApplicationNumber=allotmentRequest.getAllotment().getPreviousApplicationNumber();
		if(previousApplicationNumber!=null&&previousApplicationNumber.trim().length()>0){
		    AllotmentDetails allotment=allotmentRequest.getAllotment();
		    allotment.setApplicationType("RENEWAL");
			allotmentRequest.setAllotment(allotment);			
		}else {
			AllotmentDetails allotment=allotmentRequest.getAllotment();
		    allotment.setApplicationType("NEW");
			allotmentRequest.setAllotment(allotment);
		}
		producer.push(config.getSaveRLAllotmentTopic(), allotmentRequest);
		allotmentRequest.getAllotment().setWorkflow(null);
		return allotmentRequest.getAllotment();
	}
	
	public AllotmentDetails allotmentUpdate(AllotmentRequest allotmentRequest){
		
		allotmentValidator.validateAllotementRequest(allotmentRequest);
		allotmentEnrichmentService.enrichUpdateRequest(allotmentRequest);
		AllotmentDetails allotmentDetails=allotmentRequest.getAllotment();
		allotmentRequest.setAllotment(allotmentDetails);
		if (config.getIsWorkflowEnabled()) {
			wfService.updateWorkflowStatus(allotmentRequest);
		} else {
			allotmentRequest.getAllotment().setStatus("ACTIVE");
		}
		
		producer.push(config.getUpdateRLAllotmentTopic(), allotmentRequest);
		allotmentRequest.getAllotment().setWorkflow(null);
		return allotmentRequest.getAllotment();
	}
	
	public AllotmentRequest allotmentSearch(AllotmentRequest allotmentRequest){
		AllotmentCriteria allotmentCriteria=new AllotmentCriteria();
		Set<String> id=new HashSet<>();
		id.add(allotmentRequest.getAllotment().getId());
		allotmentCriteria.setAllotmentIds(id);
		allotmentCriteria.setTenantId("pb.mohali");
		JsonNode additionalDetails=boundaryService.loadPropertyData(allotmentRequest);
		AllotmentDetails allotmentDetails= allotmentEnrichmentService.searchAllotment(allotmentRequest.getRequestInfo(), allotmentCriteria);
		allotmentDetails.setAdditionalDetails(additionalDetails);
		allotmentRequest.setAllotment(allotmentDetails);
	    return allotmentRequest;
	}
}
