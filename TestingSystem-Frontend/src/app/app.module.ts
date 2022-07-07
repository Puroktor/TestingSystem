import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule, routingComponents } from './app-routing.module';
import { AppComponent } from './app.component';
import { TestCardComponent } from './tests-list/test-card/test-card.component';
import { HeaderComponent } from './header/header.component';
import { HttpClientModule } from '@angular/common/http';
import {QuestionComponent} from "./test/question/question.component";
import {AnswerComponent} from "./test/answer/answer.component";
import {EditorAnswerComponent} from "./editor/editor-answer/editor-answer.component";
import {EditorQuestionComponent} from "./editor/editor-question/editor-question.component";


@NgModule({
  declarations: [
    AppComponent,
    TestCardComponent,
    HeaderComponent,
    QuestionComponent,
    AnswerComponent,
    EditorQuestionComponent,
    EditorAnswerComponent,
    routingComponents,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
