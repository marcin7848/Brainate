import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageAddingComponent } from './language-adding.component';

describe('LanguageAddingComponent', () => {
  let component: LanguageAddingComponent;
  let fixture: ComponentFixture<LanguageAddingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LanguageAddingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageAddingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
