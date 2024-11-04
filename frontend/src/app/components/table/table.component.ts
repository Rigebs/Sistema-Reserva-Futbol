import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { TableColumn } from "../../models/table-column";
import { MatRow, MatRowDef, MatTableModule } from "@angular/material/table";
import { CommonModule } from "@angular/common";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

@Component({
  selector: "app-table",
  standalone: true,
  imports: [MatTableModule, CommonModule, MatIconModule, MatButtonModule],
  templateUrl: "./table.component.html",
  styleUrl: "./table.component.css",
})
export class TableComponent implements OnInit {
  @Input() columns: TableColumn[] = [];
  @Input() dataSource: any[] = [];
  @Output() actionClick = new EventEmitter<{ action: string; row: any }>();

  columnKeys: string[] = [];

  ngOnInit() {
    this.columnKeys = this.columns.map((c) => c.key);
  }

  onActionClick(action: string, row: any) {
    this.actionClick.emit({ action, row });
  }
}
