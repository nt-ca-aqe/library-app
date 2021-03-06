
import {Component, Output} from "@angular/core";
import {UserFormData} from "./user.form.data";
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import {LoginService} from "./login.service";
import {Response} from "@angular/http";
import {Router} from "@angular/router";
import {User} from "./user";

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  userdata: UserFormData = new UserFormData('', '');
  message: string = '';
  private _store: Storage;

  constructor(private _router:Router, private loginService:LoginService) {
    this._store = sessionStorage;
  }

  public login(): void {
    console.info('Logging in');

    this.loginService.login(this.userdata).subscribe(
      (user:User) => {
        console.info(user);
        this._store.setItem("lib-auth", user.username);
        this._router.navigateByUrl('');
      },
      (error: Response) => { console.info(error.statusText); this.message = 'Wrong user/password'}
    );
  }

  public logout(): void {
    console.info('Logging out');
    this.loginService.logout();
    this._store.removeItem('lib-auth');
    this._router.navigateByUrl('login');
  }

}
