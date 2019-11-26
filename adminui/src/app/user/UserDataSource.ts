import UserDto = namespace.UserDto;
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable, of} from "rxjs";
import {UserService} from "./user.service";
import {catchError, finalize} from "rxjs/operators";

export class UserDataSource implements DataSource<UserDto> {

  private usersSubject = new BehaviorSubject<UserDto[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(private userService: UserService) {
  }


  connect(collectionViewer: CollectionViewer): Observable<namespace.UserDto[] | ReadonlyArray<namespace.UserDto>> {
    return this.usersSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.usersSubject.complete();
    this.loadingSubject.complete();
  }

  public loadUsers() {
    this.loadingSubject.next(true);
    this.userService.loadData().pipe(
      catchError(()=>of([])),
      finalize(() => this.loadingSubject.next(false))
    ).subscribe(users=>this.usersSubject.next(users));
  }
}
