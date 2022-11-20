import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AutomaticAddingWordsComponent } from './automatic-adding-words.component';

describe('AutomaticAddingWordsComponent', () => {
  let component: AutomaticAddingWordsComponent;
  let fixture: ComponentFixture<AutomaticAddingWordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AutomaticAddingWordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AutomaticAddingWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
