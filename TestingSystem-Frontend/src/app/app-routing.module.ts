import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TestsListComponent} from "./tests-list/tests-list.component";
import {LoginComponent} from "./login/login.component";
import {SignupComponent} from "./signup/signup.component";
import {LeaderboardComponent} from "./leaderboard/leaderboard.component";
import {TestComponent} from "./test/test.component";
import {EditorComponent} from "./editor/editor.component";

const routes: Routes = [
  {path: '', component: TestsListComponent},
  {path: 'leaderboard', component: LeaderboardComponent},
  {path: 'login', component: LoginComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'test', component: TestComponent},
  {path: 'editor', component: EditorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

export const routingComponents = [TestsListComponent, LoginComponent, SignupComponent, LeaderboardComponent,
  TestComponent, EditorComponent]
