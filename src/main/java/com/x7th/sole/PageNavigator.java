package com.x7th.sole;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageNavigator {

	public SortedSet navigation(Page current) {
		SortedSet<Integer> set = new TreeSet<Integer>();
		if (! current.isEmpty()) {
			set.add(0);
			set.add(current.previousOrFirstPageable().getPageNumber());
			set.add(current.getNumber());
			set.add(current.nextOrLastPageable().getPageNumber());
			set.add(current.getTotalPages() - 1);
		}
		return set;
	}
}
