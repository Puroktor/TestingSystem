import {FullTest} from "./FullTest";

export interface Attempt {
  nickname: string
  score: number
  dateTime: Date
  test: FullTest
  answerToSubmittedValueMap: any
}
