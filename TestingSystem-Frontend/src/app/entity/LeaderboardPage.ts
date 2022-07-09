import {BoardTestRecord} from "./BoardTestRecord";
import {Page} from "./Page";
import {BoardUserRecord} from "./BoardUserRecord";

export interface LeaderboardPage {
  testRecords: BoardTestRecord[]
  userRecords: Page<BoardUserRecord>
}
