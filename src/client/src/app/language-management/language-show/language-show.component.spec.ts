import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageShowComponent } from './language-show.component';

describe('LanguageShowComponent', () => {
  let component: LanguageShowComponent;
  let fixture: ComponentFixture<LanguageShowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LanguageShowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageShowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
