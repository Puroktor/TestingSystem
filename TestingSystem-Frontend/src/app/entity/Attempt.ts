import {FullTest} from "./FullTest";

export interface Attempt {
  userId: number
  nickname: string
  score: number
  dateTime: Date
  test: FullTest
  answerToSubmittedValueMap: any
}
