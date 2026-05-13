import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-empleado-delete-dialog',
  imports: [CommonModule],
  templateUrl: './empleado-delete-dialog.component.html',
  styleUrl: './empleado-delete-dialog.component.css'
})
export class EmpleadoDeleteDialogComponent {
  @Input() visible = false;
  @Input() empleadoNombre = '';
  @Input() loading = false;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
