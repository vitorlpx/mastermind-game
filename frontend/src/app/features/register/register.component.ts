import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  private authService = inject(AuthService);
  private router = inject(Router);

  form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  errorMessage = '';
  isLoading = false;

  onSubmit() {
    if (this.form.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const { name, email, password } = this.form.value;

    this.authService.register(name!, email!, password!).subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => {
        this.errorMessage = 'Erro ao registrar usuário.';
        this.isLoading = false;
      }
    });

  }

}
