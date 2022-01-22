package com.x7th.sole;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

@Configuration
@ConfigurationProperties("app")
public class AppProperties {

	@Autowired
	MessageSource messageSource;

	private int openEntryInterval = - 1;

	private LocalDateTime lastEntry = LocalDateTime.now();

	public void setOpenEntryInterval(int value) {
		openEntryInterval = value;
	}

	public void updateOpenEntry() {
		lastEntry = LocalDateTime.now();
	}

	public boolean getOpenEntry() {
		if (openEntryInterval < 0) {
			return false;
		}
		if (LocalDateTime.now().isBefore(lastEntry.plusSeconds(openEntryInterval))) {
			return false;
		}
		return true;
	}

	private long adminId = 1;

	public void setAdminId(long value) {
		adminId = value;
	}

	public long getAdminId() {
		return adminId;
	}

	private HashMap<String, TreeMap<String, Pattern>> prohibitedPatterns;

	public void setProhibitedPatterns(Map<String, Map<String, String>> value) {
		prohibitedPatterns = new HashMap<String, TreeMap<String, Pattern>>();
		for (String type: value.keySet()) {
			TreeMap<String, Pattern> patterns = new TreeMap<String, Pattern>();
			for (Map.Entry<String, String> entry: value.get(type).entrySet()) {
				patterns.put(entry.getKey(), Pattern.compile(entry.getValue()));
			}
			prohibitedPatterns.put(type, patterns);
		}
	}

	public String checkProhibitedPatterns(String type, String value) {
		if (prohibitedPatterns != null) {
			TreeMap<String, Pattern> patterns = prohibitedPatterns.get(type);
			if (patterns != null) {
				for (String name: patterns.navigableKeySet()) {
					Pattern pattern = patterns.get(name);
					Matcher matcher = pattern.matcher(value);
					if (matcher.find()) {
						List<String> list = new ArrayList<String>();
						list.add(matcher.group());
						for (int i = 1; i <= matcher.groupCount(); i++) {
							list.add(matcher.group(i));
						}
						return messageSource.getMessage(name, list.toArray(), LocaleContextHolder.getLocale());
					}
				}
			}
		}
		return null;
	}
}

