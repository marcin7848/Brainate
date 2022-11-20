import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogStartRepeatingComponent } from './dialog-start-repeating.component';

describe('DialogStartRepeatingComponent', () => {
  let component: DialogStartRepeatingComponent;
  let fixture: ComponentFixture<DialogStartRepeatingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogStartRepeatingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogStartRepeatingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
