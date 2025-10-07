import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdnocUserRegistrationSuccessComponent } from '../components/adnoc-user-registration-success/adnoc-user-registration-success.component';

const routes: Routes = [
  {
    path: 'registration-success',
    component: AdnocUserRegistrationSuccessComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)]
})
export class AdnocRoutingModule {}
