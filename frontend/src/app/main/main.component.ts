import { User } from './../user';
import { Alerts } from './../alerts';
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponseBase } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthJWTManagementService } from './../auth-jwtmanagement.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from '@angular/material/snack-bar';

declare var $:any;

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    FormsModule, 
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule
  ],
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  title = 'Spring Boot Rest Api App ';
  baseUrl = 'http://127.0.0.1:8080/SpringBootRestApi/api/user/';
  userList!: User[];
  user!: User | null;

  visibleAlert: Boolean = false;
  message: String = '';
  success: Boolean = false;
  info: Boolean = false;
  warning: Boolean = false;
  danger: Boolean = false;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient, 
    private authManagement: AuthJWTManagementService ) {}

  ngOnInit(): void {
    this.obtainAllUsers();
  }

  cancel() {
    this.user = null;
  }

  checkAuth(){
    if(!this.authManagement.isAuthenticated() ){
      console.log('UnAuthenticated User');
      this.router.navigate(['/login']);
    }
  }

  save() {
    if (this.user && this.user.id) {
        this.updateUser();
    } else if (this.user) {
      this.createUser();
    }
  }

  obtainAllUsers() {
    this.checkAuth();

    var data = {
        headers: new HttpHeaders()
          .set(this.authManagement.AUTH_TOKEN_HEADER, this.authManagement.getToken())
          .set('Content-Type', 'application/json')
          .set('Accept', 'application/json') 
        ,withCredentials: true
      };
    this.http.get<User[]>(this.baseUrl, data).subscribe({
      next: (data: User[]) => {
        // console.log(data);
        this.userList = data;
      },
      error: (err: HttpResponseBase) => {
        console.log(err);
      }
    });
  }

  newUser() {
    this.user = new User();
  }

  createUser() {
    // console.log('createUser');

    this.http.post<User>(this.baseUrl, this.user).subscribe((data: User) => {
      this.obtainAllUsers();
      this.cancel();
      this.showAlert('User ' + data.name + ' Created successfully', Alerts.ALERT_TYPE_SUCCESS);
    });
  }

  deleteUser(user: User) {
    this.http.delete<User>(this.baseUrl + user.id).subscribe((data: User) => {
      this.obtainAllUsers();
      this.showAlert('User ' + user.name + ' Deleted successfully', Alerts.ALERT_TYPE_INFO);
    });
  }

  updateUser() {
    this.http.put<User>(this.baseUrl + this.user!.id, this.user).subscribe((data: User) => {
      console.log(data);
      this.obtainAllUsers();
      this.cancel();
      this.showAlert('User ' + data.name + ' Updated successfully', Alerts.ALERT_TYPE_SUCCESS);
    });
  }

  viewUser(user: User) {
    this.checkAuth();
    
    var data = {
        headers: new HttpHeaders()
          .set(this.authManagement.AUTH_TOKEN_HEADER, this.authManagement.getToken())
          .set('Content-Type', 'application/json')
          .set('Accept', 'application/json') 
        ,withCredentials: true
      };

    this.http.get<User>(this.baseUrl + user.id, data).subscribe((data: User) => {
      console.log(data);
      this.user = data;
    });
  }

  showAlert(message: string, typeAlert: Alerts) {
    this.message = message;
    this.visibleAlert = true;

    if (typeAlert === Alerts.ALERT_TYPE_SUCCESS) {
      this.success = true;
    }else if (typeAlert === Alerts.ALERT_TYPE_INFO) {
      this.info = true;
    }else if (typeAlert === Alerts.ALERT_TYPE_WARNING) {
      this.warning = true;
    }else if (typeAlert === Alerts.ALERT_TYPE_DANGER) {
      this.danger = true;
    }

    setTimeout(() => {
      this.visibleAlert = false;
    }, 5000);
  }
}
