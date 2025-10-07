/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { AdnocOccEndpoints } from '../adnoc-occ-endpoint-model/adnoc-occ-endpoint-model';
import { Config, OccConfig } from '@spartacus/core';
import { LoadingScopes } from './loading-scopes-config';

export interface BackendConfig{
  occ?: {
    baseUrl?: string;
    prefix?: string;
    /**
     * Indicates whether or not cross-site Access-Control requests should be made
     * using credentials such as cookies, authorization headers or TLS client certificates
     */
    useWithCredentials?: boolean;

    endpoints?: AdnocOccEndpoints;
  };
  media?: {
    /**
     * Media URLs are typically relative, so that the host can be configured.
     * Configurable media baseURLs are useful for SEO, multi-site,
     * switching environments, etc.
     */
    baseUrl?: string;
  };
  loadingScopes?: LoadingScopes;
}

@Injectable({
  providedIn: 'root',
  useExisting: Config,
})

export abstract class AdnocOccConfig extends OccConfig {
  declare backend?: BackendConfig;
}

