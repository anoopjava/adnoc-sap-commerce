import { Component, OnInit } from '@angular/core';
import { useFeatureStyles } from '@spartacus/core';
import { User, UserAccountFacade } from '@spartacus/user/account/root';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AdnocAuthService } from '../../../core/src/auth/user-auth/facade/adnoc-auth.service';

@Component({
  selector: 'adnoc-login',
  templateUrl: './adnoc-login.component.html',
  styleUrl: './adnoc-login.component.scss',
  standalone: false,
})
export class AdnocLoginComponent implements OnInit {
  user$!: Observable<User | undefined>;

  constructor(
    private auth: AdnocAuthService,
    private userAccount: UserAccountFacade
  ) {
    useFeatureStyles('a11yMyAccountLinkOutline');
  }
  displayName: any;
  showMenu: boolean = false;
  ngOnInit(): void {
    this.user$ = this.auth.isUserLoggedIn().pipe(
      switchMap((isUserLoggedIn) => {
        if (isUserLoggedIn) {
          return this.userAccount.get();
        } else {
          return of(undefined);
        }
      })
    );
    this.user$.subscribe((user) => {
      this.displayName = this.getDisplayName(
        user?.firstName ?? '',
        user?.lastName ?? ''
      );
    });
  }

  getDisplayName(firstName: string, lastName: string): string {
    let displayName = '';

    if (firstName && firstName.trim()) {
      displayName += firstName.charAt(0).toUpperCase();
    }

    if (lastName && lastName.trim()) {
      displayName += lastName.charAt(0).toUpperCase();
    }

    return displayName.trim(); // To remove any leading or trailing spaces
  }
}
