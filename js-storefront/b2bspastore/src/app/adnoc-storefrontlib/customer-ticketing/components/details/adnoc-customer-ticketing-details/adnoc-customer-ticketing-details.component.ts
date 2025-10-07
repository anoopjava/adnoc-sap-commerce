import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { CustomerTicketingDetailsComponent } from '@spartacus/customer-ticketing/components';
import {
  TEXT_COLOR_CLASS,
  TicketDetails,
} from '@spartacus/customer-ticketing/root';
import { CustomerTicketStaus } from '../../../root/model/adnoc-customer-ticketing.model';

@Component({
  selector: 'cx-customer-ticketing-details',
  templateUrl: './adnoc-customer-ticketing-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-customer-ticketing-details' },
  standalone: false,
})
export class AdnocCustomerTicketingDetailsComponent extends CustomerTicketingDetailsComponent {
  getCrmCaseId(ticket: TicketDetails | undefined): string | undefined {
    return (ticket as any)?.crmCaseId;
  }

  override getStatusClass(id?: string | undefined): string {
    if (
      id === CustomerTicketStaus.OPEN ||
      id === CustomerTicketStaus.INPROCESS || 
      id === CustomerTicketStaus.INPROGRESS || 
      id === CustomerTicketStaus.AWAITINGCUSTOMERINPUT ||
      id === CustomerTicketStaus.RESOLUTIONINPROGRESS ||
      id === CustomerTicketStaus.RESOLVED
    ) {
      return TEXT_COLOR_CLASS.GREEN;
    } else if (id === CustomerTicketStaus.CLOSED) {
      return TEXT_COLOR_CLASS.GREY;
    }
    return '';
  }
}
