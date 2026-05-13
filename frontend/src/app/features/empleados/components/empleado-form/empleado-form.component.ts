import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmpleadoFormValue } from '../../models/empleado.models';

interface DepartamentoOption {
  clave: string;
  nombre: string;
}

@Component({
  selector: 'app-empleado-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './empleado-form.component.html',
  styleUrl: './empleado-form.component.css'
})
export class EmpleadoFormComponent implements OnChanges {
  private readonly formBuilder = inject(FormBuilder);

  @Input() mode: 'create' | 'edit' = 'create';
  @Input() loading = false;
  @Input() submitLabel = 'Guardar';
  @Input() initialValue: Partial<EmpleadoFormValue> | null = null;
  @Input() fieldErrors: Record<string, string> | null = null;
  @Input() departamentoOptions: DepartamentoOption[] = [];

  @Output() submitted = new EventEmitter<EmpleadoFormValue>();

  protected readonly form = this.formBuilder.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    departamentoClave: [
      '',
      [Validators.required, Validators.pattern(/^D-[0-9]{4}$/)]
    ],
    version: [0, [Validators.required, Validators.min(0)]]
  });
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mode']) {
      this.configureVersionControl();
    }

    if (changes['initialValue'] && this.initialValue) {
      this.form.patchValue({
        nombre: this.initialValue.nombre ?? '',
        direccion: this.initialValue.direccion ?? '',
        telefono: this.initialValue.telefono ?? '',
        departamentoClave: this.initialValue.departamentoClave ?? '',
        version: this.initialValue.version ?? 0
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

  private configureVersionControl(): void {
    const versionControl = this.form.controls.version;

    if (this.mode === 'create') {
      versionControl.disable({ emitEvent: false });
      versionControl.clearValidators();
      versionControl.setValue(0, { emitEvent: false });
      versionControl.updateValueAndValidity({ emitEvent: false });
      return;
    }

    versionControl.setValidators([Validators.required, Validators.min(0)]);
    versionControl.enable({ emitEvent: false });
    versionControl.updateValueAndValidity({ emitEvent: false });
  }
}
