import { Component } from '@angular/core';
import { CmsNavigationComponent } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  CmsComponentData,
  NavigationService,
  NavigationNode,
} from '@spartacus/storefront';

@Component({
  selector: 'adnoc-footer',
  templateUrl: './adnoc-footer.component.html',
  styleUrl: './adnoc-footer.component.scss',
  standalone: false,
})
export class AdnocFooterComponent {
  constructor(
    protected componentData: CmsComponentData<CmsNavigationComponent>,
    protected service: NavigationService
  ) {
    this.node$ = this.service.getNavigationNode(this.componentData.data$);
  }
  node$: Observable<NavigationNode>;
}
