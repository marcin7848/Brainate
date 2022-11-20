import {Component, Input, OnInit} from '@angular/core';
import {Mode} from "../../../model/Mode";
import {Language} from "../../../model/Language";
import {NestedTreeControl} from "@angular/cdk/tree";
import {MatDialog, MatTreeNestedDataSource} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {AppService} from "../../../app.service";
import {ActivatedRoute} from "@angular/router";
import {AppComponent} from "../../../app.component";
import {Category} from "../../../model/Category";
import {LanguageService} from "../../language.service";

interface Node {
  id: number;
  name: string;
  mode: Mode,
  language: Language;
  idParent: number;
  defaultCategory: boolean;
  children?: Node[];
}

let FlatToNested, flatToNested;

declare function require(name: string);

FlatToNested = require('flat-to-nested');

flatToNested = new FlatToNested({
  id: 'id',
  parent: 'idParent',
  children: 'children',
  idParent: 'idParent',
  defaultCategory: 'defaultCategory',
  options: {deleteParent: false}
});

@Component({
  selector: 'app-category-tree',
  templateUrl: './category-tree.component.html',
  styleUrls: ['./category-tree.component.scss']
})
export class CategoryTreeComponent implements OnInit {

  @Input() languageGetter: Language;

  language: Language;
  treeControl = new NestedTreeControl<Node>(node => node.children);
  treeDataSource = new MatTreeNestedDataSource<Node>();
  nodeHasChild = (_: number, node: Node) => !!node.children && node.children.length > 0;
  @Input() checklistSelection = new SelectionModel<Node>(true);
  nodeHasNoContent = (_: number, node: Node) => node.name === '';
  currentMode:Mode;
  tree: Node[] = [];
  Mode = Mode;

  constructor(private appService: AppService,
              private route: ActivatedRoute,
              public dialog: MatDialog,
              private appComponent: AppComponent,
              private languageService: LanguageService) {
    this.currentMode = this.appService.getTreeCounter() ? Mode.DICTIONARY : Mode.EXERCISE;

  }

  ngOnInit() {
    this.language = Object.assign({}, this.languageGetter);
    this.language.categories = this.language.categories.filter(c => c.mode == this.currentMode);

    this.convertCategoryToNode();
  }

  selectNode(node: Node): void {
    this.checklistSelection.toggle(node);
  }

  nestedToFlat(node: Node, flatArray: Category[]) {
    let category: Category = new Category(node.name, node.mode, node.idParent, node.defaultCategory, node.language);
    category.id = node.id;
    flatArray.push(category);
    if (node.children != null && node.children.length > 0) {
      node.children.forEach(ch => {
        this.nestedToFlat(ch, flatArray);
      });
    }
  }

  convertCategoryToNode() {
    let categories: Category[] = JSON.parse(JSON.stringify(this.language.categories));
    let nested = flatToNested.convert(categories);
    if(nested["name"]){
      let test: Node[] = [];
      test.push(nested);
      this.tree = test;
    }
    else if(this.language.categories.length > 0) {
      this.tree = nested["children"];
    }

    this.treeDataSource.data = null;
    this.treeDataSource.data = this.tree;

  }

  findNodeById(id: number): Node {
    let foundNode: Node = null;
    function search(nodeToVisit: Node) {
      if (nodeToVisit && nodeToVisit.id == id) {
        foundNode = nodeToVisit;
      } else if (nodeToVisit && nodeToVisit.children != null && nodeToVisit.children.length > 0) {
        nodeToVisit.children.forEach(n => {
          search(n);
        });
      }
    }

    this.tree.forEach(n => {
      search(n);
    });

    return foundNode;
  }

  expandAllParentNodes(node: Node){
    this.treeControl.expand(node);
    if(node.idParent){
      let parentNode: Node = this.findNodeById(node.idParent);
      this.expandAllParentNodes(parentNode);
    }
  }

  addNewItem(parentNode: Node) {
    if(parentNode.defaultCategory){
      parentNode.id = null;
    }
    let category: Category = new Category("", parentNode.mode, parentNode.id, false, null, null);
    this.language.categories.push(category);
    this.convertCategoryToNode();

    let node: Node = this.findNodeById(parentNode.id);
    if(node){
      this.expandAllParentNodes(node);
    }
  }

  saveNode(node: Node, itemValue: string){
    if(!itemValue || itemValue && itemValue.trim().length == 0){
      this.appService.openSnackBar("Category name is required!");
      return;
    }

    for(let i=0; i < this.language.categories.length; i++){
      let currentCategory: Category = this.language.categories[i];
      if(currentCategory.idParent == node.idParent && currentCategory.name === ''){
        this.language.categories.splice(i, 1);
        break;
      }
    }
    let category: Category = new Category(itemValue, node.mode, node.idParent, false,
      null, null);
    this.languageService.addNewCategory(this.language.id, category).subscribe(
      result => {
        category = result;
        category.language = null;
        category.idParent = category.idParent == 0 ? null : category.idParent;
        this.language.categories.push(category);

        node.id = category.id;
        node.name = itemValue;
        node.defaultCategory = category.defaultCategory;
        node.idParent = category.idParent;
        this.treeDataSource.data = null;
        this.treeDataSource.data = this.tree;
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      }
    );
  }
}
