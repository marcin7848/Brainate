import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryShowComponent } from './category-show.component';

describe('CategoryShowComponent', () => {
  let component: CategoryShowComponent;
  let fixture: ComponentFixture<CategoryShowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoryShowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryShowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
