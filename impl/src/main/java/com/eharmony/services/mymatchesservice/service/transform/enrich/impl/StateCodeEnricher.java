package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.enrich.AbstractProfileEnricher;

public class StateCodeEnricher extends AbstractProfileEnricher {
    private static final Logger logger = LoggerFactory.getLogger(StateCodeEnricher.class);

    @Override
    protected boolean processMatchSection(Map<String, Object> profile,
        MatchFeedRequestContext context) {

    	String stateCode = (String) profile.get(MatchFeedModel.PROFILE.STATE_CODE);
    	
    	if(StringUtils.isBlank(stateCode)){
    		logger.warn("State code for userId {} is blank.", profile.get(MatchFeedModel.PROFILE.USERID));							
    	}else if(stateCode.contains("???")){
    		logger.warn("State code for userId {} is unresolved: \"{}\".", profile.get(MatchFeedModel.PROFILE.USERID), stateCode);							
    	}
    	
    	return true;
    }

}
