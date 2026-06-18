import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthJWTManagementService } from './auth-jwtmanagement.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    RouterLink, 
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatDividerModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  user: string =  'User Api Application';
  currentYear: number = new Date().getFullYear();

  constructor(
    public authManagement: AuthJWTManagementService,
    private router: Router
  ) {
    if (this.authManagement.isAuthenticated() ) {
      this.user = this.authManagement.getUser().username;
    } else {
      this.user = 'User Api Application';
    }
  }

  logout() {
    this.authManagement.logout();
    this.router.navigate(['/login']);
  }
}
