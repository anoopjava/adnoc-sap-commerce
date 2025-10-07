import { DOCUMENT } from '@angular/common';
import { Component, Inject } from '@angular/core';

@Component({
  selector: 'adnoc-user-registration-success',
  templateUrl: './adnoc-user-registration-success.component.html',
  styleUrl: './adnoc-user-registration-success.component.scss',
  standalone: false,
})
export class AdnocUserRegistrationSuccessComponent {
  responseData: any;
  constructor(@Inject(DOCUMENT) private document: Document) {}

  ngOnInit(): void {
    this.document.body.classList.add('registartionPage', 'hide-header-footer');
    const stored = sessionStorage.getItem('registrationResponse');
    if (stored) {
      this.responseData = JSON.parse(stored);
      sessionStorage.removeItem('registrationResponse');
    }
  }

  ngOnDestroy(): void {
    this.document.body.classList.remove(
      'registartionPage',
      'hide-header-footer'
    );
  }
}
