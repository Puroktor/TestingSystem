import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TestsListComponent} from "./tests-list/tests-list.component";
import {LoginComponent} from "./login/login.component";
import {SignupComponent} from "./signup/signup.component";
import {LeaderboardComponent} from "./leaderboard/leaderboard.component";
import {TestComponent} from "./test/test.component";
import {EditorComponent} from "./editor/editor.component";
import {InfoComponent} from "./info/info.component";
import {AttemptComponent} from "./attempt/attempt.component";
import {UserComponent} from "./user/user.component";

const routes: Routes = [
  {path: '', component: InfoComponent},
  {path: 'tests-list', component: TestsListComponent},
  {path: 'leaderboard', component: LeaderboardComponent},
  {path: 'login', component: LoginComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'test', component: TestComponent},
  {path: 'editor', component: EditorComponent},
  {path: 'attempt', component: AttemptComponent},
  {path: 'user', component: UserComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

export const routingComponents = [InfoComponent, TestsListComponent, LoginComponent, SignupComponent,
  LeaderboardComponent, TestComponent, EditorComponent, AttemptComponent, UserComponent]
