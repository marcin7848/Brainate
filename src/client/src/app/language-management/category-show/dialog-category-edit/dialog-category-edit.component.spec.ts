import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCategoryEditComponent } from './dialog-category-edit.component';

describe('DialogCategoryEditComponent', () => {
  let component: DialogCategoryEditComponent;
  let fixture: ComponentFixture<DialogCategoryEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogCategoryEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogCategoryEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
