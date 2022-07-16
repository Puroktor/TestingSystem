import {Question} from "./Question";

export interface FullTest {
  id: number | null
  programmingLang: string
  name: string
  questionsCount: number
  testType: string
  questions: Question[]
}
