import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KashiComponent } from './kashi.component';

describe('KashiComponent', () => {
  let component: KashiComponent;
  let fixture: ComponentFixture<KashiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KashiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KashiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
