import {Answer} from "./Answer";

export interface Question {
  id: number | null
  text: string
  maxScore: number
  questionTemplateIndex: number|null
  answers: Answer[]
}
