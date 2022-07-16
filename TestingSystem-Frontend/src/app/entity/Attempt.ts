import {FullTest} from "./FullTest";

export interface Attempt {
  nickname: string
  score: number
  test: FullTest
  answerToSubmittedValueMap: any
}
