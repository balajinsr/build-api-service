/**
 * 
 */
package com.broadcom.nbiapps.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.broadcom.nbiapps.model.PullRequest;
import com.broadcom.nbiapps.util.CoreUtils;

/**
 * @author Balaji N
 *
 */
@Converter
public class PullRequestConverter implements AttributeConverter<PullRequest, String> {
	 @Override
	 public PullRequest convertToEntityAttribute(String data) {
	  return CoreUtils.convertToObject(PullRequest.class, data);
	}

	
	@Override
	public String convertToDatabaseColumn(PullRequest pullRequest) {
		return CoreUtils.convertToJsonString(pullRequest);
	}
}
