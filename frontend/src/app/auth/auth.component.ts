import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AuthJWTManagementService } from './../auth-jwtmanagement.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule
  ],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

  user: String = '';
  password: String = '';
  errors: boolean = false;
  error: string = '';

  constructor(private route: ActivatedRoute,
    private router: Router,
    private authManagement: AuthJWTManagementService) { }

  ngOnInit() {
  }

  login(event: Event) {
    event.preventDefault();

    if (this.user === '' || this.password === '') {
      console.log('completa los campos');
      this.error = 'completa los campos';
      this.errors = true;
      return;
    }
    this.errors = false;

    // username: 'admin', password: 'password'
    const credentials = {username: this.user, password: this.password};

    this.authManagement.auth(credentials).then(() => {
      if (this.authManagement.isAuthenticated()) {
        this.goToUserPage();
      }else {
        console.log('credenciales erroneas');
      }
    }).catch((data: HttpErrorResponse) => {
      const error = JSON.parse(data.error);
      if (error.error === 'Unauthorized' || error.status === 401) {
        this.error = error.message;
        this.errors = true;
      }
    });
  }

  goToUserPage() {
    this.router.navigate(['/home']);
  }
}
