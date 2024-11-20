import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Inject, Output } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { DialogField } from "../../models/dialog-field";
import { MatOptionModule } from "@angular/material/core";
import { MatSelectModule } from "@angular/material/select";

@Component({
  selector: "app-dialog-form",
  standalone: true,
  imports: [
    MatDialogModule,
    MatInputModule,
    MatDatepickerModule,
    MatCheckboxModule,
    CommonModule,
    MatButtonModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatOptionModule,
  ],
  templateUrl: "./dialog-form.component.html",
  styleUrl: "./dialog-form.component.css",
})
export class DialogFormComponent {
  form: FormGroup;

  @Output() submitEvent = new EventEmitter<any>();

  constructor(
    public dialogRef: MatDialogRef<DialogFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogField[],
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({});
    this.createForm(data);
  }

  createForm(fields: DialogField[]): void {
    fields.forEach((field) => {
      const control = this.fb.control(
        field.value,
        field.type === "checkbox" || field.type === "select"
          ? []
          : Validators.required
      );
      this.form.addControl(field.name, control);
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.submitEvent.emit(this.form.value);
      this.dialogRef.close(this.form.value);
    }
  }
  onCancel(): void {
    this.dialogRef.close();
  }
}
