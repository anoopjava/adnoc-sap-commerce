import { Injectable } from '@angular/core';
import { CdcConfigService } from './cdc-config.service';
import { LanguageService } from '@spartacus/core';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CdcScriptLoaderService {
  constructor(
    private configService: CdcConfigService,
    private languageService: LanguageService
  ) {}

  async loadCdcScript(): Promise<void> {
    const existingScript = document.getElementById('cdc-sdk-script');
    if (existingScript) {
      return;
    }

    const apiKey = this.configService.getApiKey();
    const dataCenter = this.configService.getDataCenter();
    const lang = await firstValueFrom(this.languageService.getActive());

    const script = document.createElement('script');
    script.id = 'cdc-sdk-script';
    script.type = 'text/javascript';
    script.async = true;
    script.src = `https://cdns.${dataCenter}.gigya.com/js/gigya.js?apikey=${apiKey}&lang=${lang}`;

    document.head.appendChild(script);
  }
}
