import {Answer} from "./Answer";

export interface Question {
  id: number | null
  text: string
  maxScore: number
  answers: Answer[]
}
