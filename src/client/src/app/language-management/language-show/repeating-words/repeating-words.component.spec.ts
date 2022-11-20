import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepeatingWordsComponent } from './repeating-words.component';

describe('RepeatingWordsComponent', () => {
  let component: RepeatingWordsComponent;
  let fixture: ComponentFixture<RepeatingWordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepeatingWordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepeatingWordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
