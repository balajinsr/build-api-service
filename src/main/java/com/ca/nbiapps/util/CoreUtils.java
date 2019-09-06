/**
 * 
 */
package com.ca.nbiapps.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ca.nbiapps.exceptions.JSONException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Balaji N
 *
 */
public class CoreUtils {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Map<String, ObjectReader> objectReaderMap = new HashMap<>();
	private static final Map<String, ObjectWriter> objectWriterMap = new HashMap<>();

	public static String prettyJSONPrinter(String jsonString) throws JSONException {
		try {
			Object json = objectMapper.readValue(jsonString, Object.class);
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new JSONException("Error while formatting input JSON string", e);
		} catch (IOException e) {
			throw new JSONException("Error while formatting input JSON string", e);
		}
	}

	public static <T> T convertToObject(Class<T> clazz, String respJson) throws JSONException {
		T responseObj = null;
		ObjectReader objectReaderJaxb = null;
		try {
			objectReaderJaxb = objectReaderMap.get(clazz.getName());
			responseObj = objectReaderJaxb.readValue(respJson);
		} catch (IOException e) {
			throw new JSONException("Invalid JSON", e);
		} catch (NullPointerException e) {
			if (objectReaderJaxb == null) {
				objectReaderMap.put(clazz.getName(), objectMapper.readerFor(clazz));
				objectWriterMap.put(clazz.getName(), objectMapper.writerFor(clazz));
				return convertToObject(clazz, respJson);
			} else {
				throw e;
			}
		}
		return responseObj;
	}

	public static <R> String convertToJsonString(R jsonObj) throws JSONException {
		String jsonString;
		ObjectWriter objectWriterJaxb = null;
		try {
			objectWriterJaxb = objectWriterMap.get(jsonObj.getClass().getName());
			jsonString = objectWriterJaxb.writeValueAsString(jsonObj);
		} catch (JsonProcessingException e) {
			throw new JSONException(
					String.format("Couldn't convert java object to Json - {%s}", jsonObj.getClass().getName()), e);
		} catch (NullPointerException e) {
			if (objectWriterJaxb == null) {
				objectReaderMap.put(jsonObj.getClass().getName(), objectMapper.readerFor(jsonObj.getClass()));
				objectWriterMap.put(jsonObj.getClass().getName(), objectMapper.writerFor(jsonObj.getClass()));
				return convertToJsonString(jsonObj);
			} else {
				throw e;
			}
		}
		return jsonString;
	}

	public static Map<String, Object> jsontomap(String s) throws JSONException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
			return map;
		} catch (IOException e) {
			throw new JSONException("Invalid JSON", e);
		}
	}

	public static String maptoJson(Map<String, Object> jsonMap) throws JSONException {
		String clientFilterJson = null;
		try {
			clientFilterJson = objectMapper.writeValueAsString(jsonMap);
			return clientFilterJson;
		} catch (IOException e) {
			throw new JSONException(String.format("Couldn't convert java object to Json - {%s}", "java.util.Map"), e);
		}
	}
}
