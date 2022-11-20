import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogWordDeleteComponent } from './dialog-word-delete.component';

describe('DialogWordDeleteComponent', () => {
  let component: DialogWordDeleteComponent;
  let fixture: ComponentFixture<DialogWordDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogWordDeleteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogWordDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
