/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.adnoc.backoffice.widgets;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.util.DefaultWidgetController;

import com.adnoc.backoffice.services.AdnocbackofficeService;


public class AdnocbackofficeController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private Label label;

	@WireVariable
	private transient AdnocbackofficeService adnocbackofficeService;

	@Override
	public void initialize(final Component comp)
	{
		super.initialize(comp);
		label.setValue(adnocbackofficeService.getHello() + " AdnocbackofficeController");
	}
}
