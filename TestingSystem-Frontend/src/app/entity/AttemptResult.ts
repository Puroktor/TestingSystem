export interface AttemptResult {
  id: number
  testId: number
  testName: string
  dateTime: Date
  score: number
  hasPassed: boolean
}
