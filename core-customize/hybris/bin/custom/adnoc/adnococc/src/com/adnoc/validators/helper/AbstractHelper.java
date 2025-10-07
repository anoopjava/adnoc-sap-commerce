/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.validators.helper;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public abstract class AbstractHelper
{
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	protected PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
	{
		final PageableData pageable = new PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);
		return pageable;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
