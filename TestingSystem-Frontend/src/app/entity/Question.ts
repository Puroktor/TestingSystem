import {Answer} from "./Answer";

export interface Question {
  id: number
  text: string
  maxScore: number
  answers: Answer[]
}
