
package org.egov.pt.web.controllers;

import java.util.Arrays;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.AllotmentDetails;
import org.egov.pt.models.AllotmentRequest;
import org.egov.pt.models.AllotmentResponse;
import org.egov.pt.service.AllotmentService;
import org.egov.pt.service.FuzzySearchService;
import org.egov.pt.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api")
@Slf4j
public class AllotmentController {

   
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    FuzzySearchService fuzzySearchService;

    @Autowired
    AllotmentService allotmentService;

    @PostMapping("/_create")
    public ResponseEntity<AllotmentResponse> create(@Valid @RequestBody AllotmentRequest allotmentRequest) {

        AllotmentDetails allotmentDetails =allotmentService.allotmentCreate(allotmentRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(allotmentRequest.getRequestInfo(), true);
        AllotmentResponse response = AllotmentResponse.builder()
                .allotment(Arrays.asList(allotmentDetails))
                .responseInfo(resInfo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/_update")
    public ResponseEntity<AllotmentResponse> update(@Valid @RequestBody AllotmentRequest allotmentRequest) {
    	AllotmentDetails allotmentDetails =allotmentService.allotmentUpdate(allotmentRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(allotmentRequest.getRequestInfo(), false);
        AllotmentResponse response = AllotmentResponse.builder()
                .allotment(Arrays.asList(allotmentDetails))
                .responseInfo(resInfo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);	
    }
    
    @GetMapping("/_search")
    public ResponseEntity<AllotmentResponse> search(@RequestBody AllotmentRequest allotmentRequest) {
    	ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(allotmentRequest.getRequestInfo(), true);
      allotmentService.allotmentSearch(allotmentRequest);
    	System.out.println();
        AllotmentResponse response = AllotmentResponse.builder()
                .allotment(Arrays.asList(allotmentRequest.getAllotment()))
                .responseInfo(resInfo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);	
    }
    
//    @RequestMapping(value = "/_sendOpenSMS", method = RequestMethod.POST)
//     public ResponseEntity<Integer> sendOpenSMS(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,@RequestParam String msgParam,@RequestParam String mobile_no,@RequestParam String dlt_entity_id,@RequestParam String dlt_template_id)
//    {
//    	try
//    	{
//    		//String msg="Dear OpenSMS, Status for your application no.OpenSMSApp for property OpenPid to OpenCreated property has been changed to OpenActivate. You can track your application on the link given below-https://mseva.lgpunjab.gov.in/employee/user/login Thank you|1301157492438182299|1407162859196626520";
//
//    	String msg=msgParam+"|"+dlt_entity_id+"|"+dlt_template_id;
//    	Map<String, String> mobileNumberToOwner = new HashMap<>();
//    	mobileNumberToOwner.put(mobile_no, "openuser");
//    	List<SMSRequest> smsRequests = notifUtil.createSMSRequest(msg, mobileNumberToOwner);
//		notifUtil.sendSMS(smsRequests);
//
//    	return new ResponseEntity<>(1,HttpStatus.OK);
//    	}
//    	catch(Exception ex)
//    	{
//    		return new ResponseEntity<>(1,HttpStatus.PRECONDITION_FAILED);
//    	}
//
//    }
//
//    @PostMapping("/_search")
//    public ResponseEntity<PropertyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//                                                   @Valid @ModelAttribute PropertyCriteria propertyCriteria) {
//
//        // If inbox search has been disallowed at config level or if inbox search is allowed but the current search is NOT, from inbox service validate the search criteria.
//        if(!configs.getIsInboxSearchAllowed() || !propertyCriteria.getIsInboxSearch()){
//            propertyValidator.validatePropertyCriteria(propertyCriteria, requestInfoWrapper.getRequestInfo());
//        }
//        
//    	List<Property> properties = new ArrayList<Property>();
//    	Integer count = 0;
//        
//        if (propertyCriteria.getIsRequestForCount()) {
//        	count = propertyService.count(requestInfoWrapper.getRequestInfo(), propertyCriteria);
//        	
//        }else {
//        	 properties = propertyService.searchProperty(propertyCriteria,requestInfoWrapper.getRequestInfo());
//        }
//        
//        log.info("Property count after search"+properties.size());
//        
//        PropertyResponse response = PropertyResponse.builder()
//        		.responseInfo(
//                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//        		.properties(properties)
//        		.count(count)
//                .build();
//        
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//
//    @PostMapping("/_migration")
//    public ResponseEntity<?> propertyMigration(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//                                               @Valid @ModelAttribute OldPropertyCriteria propertyCriteria) {
//        long startTime = System.nanoTime();
//        Map<String, String> resultMap = null;
//        Map<String, String> errorMap = new HashMap<>();
//
//        resultMap = migrationService.initiateProcess(requestInfoWrapper,propertyCriteria,errorMap);
//
//        long endtime = System.nanoTime();
//        long elapsetime = endtime - startTime;
//        System.out.println("Elapsed time--->"+elapsetime);
//
//        return new ResponseEntity<>(resultMap, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/_plainsearch", method = RequestMethod.POST)
//    public ResponseEntity<PropertyResponse> plainsearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//                                                        @Valid @ModelAttribute PropertyCriteria propertyCriteria) {
//        List<Property> properties = propertyService.searchPropertyPlainSearch(propertyCriteria, requestInfoWrapper.getRequestInfo());
//        PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
//                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
////	@RequestMapping(value = "/_cancel", method = RequestMethod.POST)
////	public ResponseEntity<PropertyResponse> cancel(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
////												   @Valid @ModelAttribute PropertyCancelCriteria propertyCancelCriteria) {
////
////		List<Property> properties = propertyService.cancelProperty(propertyCancelCriteria,requestInfoWrapper.getRequestInfo());
////		PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
////				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
////				.build();
////		return new ResponseEntity<>(response, HttpStatus.OK);
////	}
//    
//    @PostMapping("/_addAlternateNumber")
//    public ResponseEntity<PropertyResponse> _addAlternateNumber(@Valid @RequestBody PropertyRequest propertyRequest) {    	
//        Property property = propertyService.addAlternateNumber(propertyRequest);
//        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(), true);
//        PropertyResponse response = PropertyResponse.builder()
//                .properties(Arrays.asList(property))
//                .responseInfo(resInfo)
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping("/fuzzy/_search")
//    public ResponseEntity<PropertyResponse> fuzzySearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//                                                      @Valid @ModelAttribute PropertyCriteria fuzzySearchCriteria) {
//
//        List<Property> properties = fuzzySearchService.getProperties(requestInfoWrapper.getRequestInfo(), fuzzySearchCriteria);
//        PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
//                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    /**
//     * Encrypts existing property records
//     *
//     * @param requestInfoWrapper RequestInfoWrapper
//     * @param propertyCriteria PropertyCriteria
//     * @return list of updated encrypted data
//     */
//    /* To be executed only once */
//    @RequestMapping(value = "/_encryptOldData", method = RequestMethod.POST)
//    public ResponseEntity<PropertyResponse> encryptOldData(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//                                                         @Valid @ModelAttribute PropertyCriteria propertyCriteria) {
//
//        throw new CustomException("EG_PT_ENC_OLD_DATA_ERROR", "The encryption of old data is disabled");
//          /* Un-comment the below code to enable Privacy */
//        
////        propertyCriteria.setIsRequestForOldDataEncryption(Boolean.TRUE);
////        List<Property> properties = propertyEncryptionService.updateOldData(propertyCriteria, requestInfoWrapper.getRequestInfo());
////        PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
////                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
////                .build();
////        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
}
