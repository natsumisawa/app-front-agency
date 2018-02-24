import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ReportComponent } from './report/report.component';
import { KashiComponent } from './kashi/kashi.component'

const appRoutes: Routes = [
  {
  path: '',
  component: KashiComponent
}
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
