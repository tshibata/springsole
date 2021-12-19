package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app")
public class AppProperties {

	private boolean openEntry = false;

	public void setOpenEntry(boolean value) {
		this.openEntry = value;
	}

	public boolean getOpenEntry() {
		return openEntry;
	}

	private long adminId = 1;

	public void setAdminId(long value) {
		this.adminId = value;
	}

	public long getAdminId() {
		return adminId;
	}
}

