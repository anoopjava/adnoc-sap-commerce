import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import {
  B2BUserRole,
  CmsBreadcrumbsComponent,
  FeatureConfigService,
  PageMetaService,
  TranslationService,
  useFeatureStyles,
  User,
} from '@spartacus/core';
import {
  CmsComponentData,
  PageLayoutService,
  PageTitleComponent,
} from '@spartacus/storefront';
import { UserAccountFacade } from '@spartacus/user/account/root';
import _ from 'lodash';
import {
  Observable,
  filter,
  map,
  of,
  combineLatest,
  takeUntil,
  Subject,
} from 'rxjs';

@Component({
  selector: 'cx-breadcrumb',
  templateUrl: './adnoc-breadcrumb.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-breadcrumb' },
  standalone: false,
})
export class AdnocBreadcrumbComponent
  extends PageTitleComponent
  implements OnInit
{
  crumbs$!: Observable<any[]>;
  protected router = inject(Router);
  private featureConfigService = inject(FeatureConfigService);
  protected destroy$ = new Subject<void>();
  templateName = '';
  isAdmin = false;
  displayTitle = true;

  ariaLive$: Observable<boolean> = this.featureConfigService.isEnabled(
    'a11yRepeatedPageTitleFix'
  )
    ? this.router.events.pipe(
        filter((e) => e instanceof NavigationEnd),
        map(() => {
          return document.activeElement !== document.body;
        })
      )
    : of(true);

  constructor(
    public override component: CmsComponentData<CmsBreadcrumbsComponent>,
    protected override pageMetaService: PageMetaService,
    private translation: TranslationService,
    protected pageLayoutService: PageLayoutService,
    protected userAccountFacade: UserAccountFacade
  ) {
    super(component, pageMetaService);
    useFeatureStyles('a11yTruncatedTextForResponsiveView');
    this.userAccountFacade
      .get()
      .pipe(
        filter((user): user is User => !!user && Object.keys(user).length > 0),
        map((user) => (user as User & { roles?: string[] })?.roles),
        map((roles) => {
          this.isAdmin =
            Array.isArray(roles) && roles.includes(B2BUserRole.ADMIN);
        })
      )
      .subscribe();
  }

  override ngOnInit(): void {
    this.pageLayoutService.templateName$
      .pipe(takeUntil(this.destroy$))
      .subscribe((templateName) => {
        this.templateName = templateName;
      });

    super.ngOnInit();
    this.setCrumbs();
  }

  private setCrumbs(): void {
    this.crumbs$ = combineLatest([
      this.pageMetaService.getMeta(),
      this.translation.translate('common.home'),
    ]).pipe(
      map(([meta, textHome]) => {
        let breadcrumbs = meta?.breadcrumbs || [];

        this.displayTitle = !breadcrumbs.some((item: any) =>
          item.link?.includes('support-tickets')
        );

        if (
          this.templateName === 'ProductDetailsPageTemplate' &&
          Array.isArray(breadcrumbs) &&
          breadcrumbs.length === 3
        ) {
          breadcrumbs = breadcrumbs.filter((_, idx) => idx !== 2);
        }

        if (!this.isAdmin) {
          breadcrumbs = breadcrumbs.filter(
            (item) => item.label !== 'Organization'
          );
        }
        const updatedItems =
          this.templateName === 'ProductDetailsPageTemplate'
            ? breadcrumbs.map((item) => {
                if (
                  !item ||
                  typeof item.link !== 'string' ||
                  !item.link.startsWith('/Open-Catalogue/')
                ) {
                  return item;
                }

                const parts = item.link.split('/');
                const category = parts[2] ?? '';
                return {
                  ...item,
                  label: category,
                  link: `/Open-Catalogue/${category}/c/${category.toLocaleLowerCase()}`,
                };
              })
            : meta?.title == 'Account Summary' && !this.isAdmin
            ? [breadcrumbs[0]]
            : breadcrumbs;

        return meta?.breadcrumbs
          ? updatedItems
          : [{ label: textHome, link: '/' }];
      })
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
