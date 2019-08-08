package com.broadcom.nbiapps.constants;

public enum BuildConstants {
	BUILD_SUCCESS(0, "Build Success"), BUILD_FAILED(1, "Build Failed");
	private BuildConstants(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	private int status;
	private String desc;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
