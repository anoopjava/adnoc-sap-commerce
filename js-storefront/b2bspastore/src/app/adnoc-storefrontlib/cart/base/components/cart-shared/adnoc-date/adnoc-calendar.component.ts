import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDateFormats } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import * as _moment from 'moment';
// tslint:disable-next-line:no-duplicate-imports
import { default as _rollupMoment } from 'moment';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
const moment = _rollupMoment || _moment;
export const CUSTOM_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'DD/MM/YYYY', // Format for input parsing
  },
  display: {
    dateInput: 'DD/MM/YYYY', // Format shown in the input
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
export interface ICalendar{
  event: Date | any,
  groupKey: string
};
@Component({
    selector: 'adnoc-calendar',
    templateUrl: './adnoc-calendar.component.html',
    styleUrl: './adnoc-calendar.component.scss',
    providers: [provideMomentDateAdapter(CUSTOM_DATE_FORMATS)],
    standalone: false
})

export class AdnocCalendarComponent {
  todayDate: Date = new Date();
  maxDate!: Date;
  // configurable value for number of days to disable
  @Input() mindaysToDisable = 3;
  @Input() maxdaysToEnable = 365;
  @Input() groupKey!:string;
  @Input() selectedDate!: Date | null;
  @Output() dateSelected=new EventEmitter<ICalendar>();
  
  constructor() {
    this.maxDate = new Date(
      this.todayDate.getFullYear(),
      this.todayDate.getMonth(),
      this.todayDate.getDate() + this.maxdaysToEnable
    );
  }

  // On date selection
  onDateChange(event: MatDatepickerInputEvent<Date>,i:string){
    let emitValue ={
      event: event.value,
      groupKey: i
    }
      this.dateSelected.emit(emitValue); 
  }

  // Filter function to disable the configurable days
  dateFilter = (date: Date | null): boolean => {
    if (!date) return false;
    const currentDate = new Date(date);
    const today = this.todayDate;
    const configDaysFromNow = new Date(today);
    configDaysFromNow.setDate(today.getDate() + this.mindaysToDisable);

    return currentDate > configDaysFromNow;
  };

  // Function to disable dates
  disableDates = (date: Date | null) => {
    const today = new Date();
    const dayDifference =
      ((date ? date!.getTime() : 0) - today.getTime()) / (1000 * 60 * 60 * 24);

    // Disable dates within the specified days from today
    return dayDifference >= this.mindaysToDisable;
  };
}
