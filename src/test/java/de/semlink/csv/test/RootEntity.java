package de.semlink.csv.test;

import de.semlink.csv.annotation.CSVItem;

public class RootEntity {

	@CSVItem
	protected Long rootId;

	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}

}
