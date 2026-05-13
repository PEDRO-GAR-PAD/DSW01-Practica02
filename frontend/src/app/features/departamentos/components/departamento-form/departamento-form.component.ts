import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  inject
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DepartamentoFormValue } from '../../models/departamento.models';

@Component({
  selector: 'app-departamento-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './departamento-form.component.html',
  styleUrl: './departamento-form.component.css'
})
export class DepartamentoFormComponent implements OnChanges {
  private readonly formBuilder = inject(FormBuilder);

  @Input() mode: 'create' | 'edit' = 'create';
  @Input() loading = false;
  @Input() submitLabel = 'Guardar';
  @Input() initialValue: Partial<DepartamentoFormValue> | null = null;
  @Input() fieldErrors: Record<string, string> | null = null;

  @Output() submitted = new EventEmitter<DepartamentoFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]]
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initialValue'] && this.initialValue) {
      this.form.patchValue({
        nombre: this.initialValue.nombre ?? ''
      });
    }
  }

  protected onSubmit(): void {
    if (this.form.invalid || this.loading) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted.emit(this.form.getRawValue());
  }
}