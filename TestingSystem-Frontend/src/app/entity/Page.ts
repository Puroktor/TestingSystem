import {TestCard} from "./TestCard";

export interface Page {
  content: TestCard[]
  pageable?: any
  last: boolean
  totalPages?: number
  totalElements?: number
  size?: number
  number: number
  sort?: any
  first: boolean
  numberOfElements?: number
  empty?: boolean
}
