import {TestCard} from "./TestCard";
import {Question} from "./Question";

export interface FullTest {
  testInfoDto: TestCard
  questionList: Question[]
}
