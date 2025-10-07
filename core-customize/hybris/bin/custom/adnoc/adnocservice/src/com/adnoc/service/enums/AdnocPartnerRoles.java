/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adnoc.service.enums;

/**
 * Partner roles for sales order processing as defined in SAP ERP. Corresponds to SAP defined entries of table TPAR
 */
public enum AdnocPartnerRoles
{
    PAYER_TO("RG");

    private final String code;

    private AdnocPartnerRoles(final String code)
    {
        this.code = code;
    }


    public String getCode()
    {
        return code;
    }
}
