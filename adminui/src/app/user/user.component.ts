import {Component, OnInit} from '@angular/core';
import {UserService} from "./user.service";
import {UserDataSource} from "./UserDataSource";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  dataSource: UserDataSource;
  displayedColumns = ["seqNo", "description", "duration"];

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.dataSource = new UserDataSource(this.userService);
    this.dataSource.loadUsers();
  }

  onRowClicked(row: any) {
    console.log('Row clicked: ', row);
  }

}
