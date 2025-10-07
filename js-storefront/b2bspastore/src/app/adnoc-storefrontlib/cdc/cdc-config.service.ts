import { Injectable } from '@angular/core';
import { BaseSite, BaseSiteService } from '@spartacus/core';

export interface GigyaConfigData {
  cdcApiKey: string;
  cdcUserKey: string;
  cdcUserSecret: string;
  dataCenter: string;
  include: string;
  loginMode: string;
  sessionExpiration: number;
}

export interface ExtendedBaseSite extends BaseSite {
  gigyaConfigData?: GigyaConfigData;
}

@Injectable({
  providedIn: 'root',
})
export class CdcConfigService {
  private cdcApiKey: string = '';
  private dataCenter: string = '';
  private includeParam: string = '';
  private sessionExpiration: number = 0;

  constructor(protected baseSiteService: BaseSiteService) {}

  load(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.baseSiteService.getAll().subscribe({
        next: (data: ExtendedBaseSite[]) => {
          const site = data[0];
          if (site?.gigyaConfigData) {
            this.cdcApiKey = site.gigyaConfigData.cdcApiKey;
            this.dataCenter = site.gigyaConfigData.dataCenter;
            this.includeParam = site.gigyaConfigData.include;
            this.sessionExpiration = +site.gigyaConfigData.sessionExpiration;
            resolve();
          } else {
            console.warn('gigyaConfigData not found in site');
            resolve();
          }
        },
        error: (err) => {
          console.error('Failed to load base site data:', err);
          reject(err);
        },
      });
    });
  }

  getApiKey(): string {
    return this.cdcApiKey;
  }

  getDataCenter(): string {
    return this.dataCenter;
  }

  getSessionExpiration(): number {
    return this.sessionExpiration;
  }

  getIncludeParams(): string {
    return this.includeParam;
  }
}
