import {TestCard} from "./TestCard";
import {Question} from "./Question";

export interface FullTest {
  testInfo: TestCard
  questions: Question[]
}
