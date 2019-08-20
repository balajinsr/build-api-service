package com.broadcom.nbiapps.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.broadcom.nbiapps.model.ModuleData;
import com.broadcom.nbiapps.util.CoreUtils;

/**
 * @author Balaji N
 *
 */
@Converter
public class ModuleDataConverter implements AttributeConverter<ModuleData, String> {
	 @Override
	 public ModuleData convertToEntityAttribute(String data) {
	  return CoreUtils.convertToObject(ModuleData.class, data);
	}

	
	@Override
	public String convertToDatabaseColumn(ModuleData moduleData) {
		return CoreUtils.convertToJsonString(moduleData);
	}
}
