import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {finalize} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  title = 'Demo';
  authenticated = false;
  greeting = {} as any;

  constructor(private http: HttpClient) {
    this.authenticate();
  }

  authenticate() {

    this.http.get('user').subscribe(response => {
      // @ts-ignore
      if (response['name']) {
        this.authenticated = true;
        this.http.get('resource_server').subscribe(data => this.greeting = data);
      } else {
        this.authenticated = false;
      }
    }, () => {
      this.authenticated = false;
    });

  }

  logout() {
    this.http.post('logout', {}).pipe(finalize(() => {
      this.authenticated = false;
    })).subscribe();
  }

}
