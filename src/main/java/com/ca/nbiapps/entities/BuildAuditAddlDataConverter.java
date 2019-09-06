/**
 * 
 */
package com.ca.nbiapps.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ca.nbiapps.model.BuildAuditAddlData;
import com.ca.nbiapps.util.CoreUtils;

/**
 * @author Balaji N
 *
 */
@Converter
public class BuildAuditAddlDataConverter implements AttributeConverter<BuildAuditAddlData, String> {
	 
	@Override
	 public BuildAuditAddlData convertToEntityAttribute(String data) {
	  return CoreUtils.convertToObject(BuildAuditAddlData.class, data);
	}

	
	@Override
	public String convertToDatabaseColumn(BuildAuditAddlData buildAuditAddlData) {
		return CoreUtils.convertToJsonString(buildAuditAddlData);
	}
}
