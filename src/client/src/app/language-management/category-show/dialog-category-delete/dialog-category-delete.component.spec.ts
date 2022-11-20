import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCategoryDeleteComponent } from './dialog-category-delete.component';

describe('DialogCategoryDeleteComponent', () => {
  let component: DialogCategoryDeleteComponent;
  let fixture: ComponentFixture<DialogCategoryDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogCategoryDeleteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogCategoryDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
