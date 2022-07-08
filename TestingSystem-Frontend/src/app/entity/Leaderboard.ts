import {BoardUserRecord} from "./BoardUserRecord";
import {BoardTestRecord} from "./BoardTestRecord";

export interface Leaderboard {
  testRecords: BoardTestRecord[]
  userRecords: BoardUserRecord[]
}
