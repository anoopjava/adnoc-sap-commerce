/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ProductScope } from '@spartacus/core';
import { Observable } from 'rxjs';
import { CurrentProductService } from '../../current-product.service';
import { FileDownloadService } from '@spartacus/storefront';
import { Product } from '../../../../core/src/model/product.model';

@Component({
  selector: 'adnoc-product-attributes',
  templateUrl: './adnoc-product-attributes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class ProductAttributesComponent {
  product$: Observable<Product | null>;

  constructor(
    protected currentProductService: CurrentProductService,
    protected downloadService: FileDownloadService
  ) {
    this.product$ = this.currentProductService.getProduct(
      ProductScope.DETAILS,
    );
  }
  getpdf(code: string) {
    this.currentProductService
      .getProductAttachment(code)
      .subscribe((response: any) => {
        // fallback for when the response is not have a headers
        let fileName = 'ProductDetail.pdf';
        let blob: Blob;

        if (response?.body && response?.headers) {
          const contentDisposition = response.headers.get(
            'content-disposition'
          );
          if (contentDisposition) {
            const match = contentDisposition.match(/filename="?([^"]+)"?/);
            if (match && match[1]) {
              fileName = match[1];
            }
          }
          blob = response.body;
        } else {
          blob = response;
        }

        const blobUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(blobUrl);
      });
  }
}
