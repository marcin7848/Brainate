import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteLanguageComponent } from './dialog-delete-language.component';

describe('DialogDeleteLanguageComponent', () => {
  let component: DialogDeleteLanguageComponent;
  let fixture: ComponentFixture<DialogDeleteLanguageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteLanguageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteLanguageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
