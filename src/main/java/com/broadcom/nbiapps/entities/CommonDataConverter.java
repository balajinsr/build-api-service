package com.broadcom.nbiapps.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.broadcom.nbiapps.util.CoreUtils;

/**
 * @author Balaji N
 *
 */
@Converter
public class CommonDataConverter implements AttributeConverter<CommonData, String> {
	 @Override
	 public CommonData convertToEntityAttribute(String data) {
	  return CoreUtils.convertToObject(CommonData.class, data);
	}

	
	@Override
	public String convertToDatabaseColumn(CommonData commonData) {
		return CoreUtils.convertToJsonString(commonData);
	}
}
