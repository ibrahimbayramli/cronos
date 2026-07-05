export type ExecutionStatus = 'RUNNING' | 'SUCCESS' | 'FAILED'
export type JobSourceType = 'SPRING_SCHEDULED' | 'QUARTZ'
export type TriggerSource = 'AUTO' | 'MANUAL'

export interface JobSummary {
  id: number
  name: string
  sourceType: JobSourceType
  beanName: string
  methodOrClass: string
  triggerInfo: string
  discoveredAt: string
  enabled: boolean
  lastRunAt: string | null
  lastStatus: ExecutionStatus | null
  lastDurationMs: number | null
  nextRunAt: string | null
}

export interface JobDetail extends JobSummary {
  totalExecutions: number
}

export interface JobExecution {
  id: number
  jobId: number
  status: ExecutionStatus
  startedAt: string
  finishedAt: string | null
  durationMs: number | null
  errorMessage: string | null
  triggerSource: TriggerSource
}

export interface HealthResponse {
  status: string
  timestamp: string
  discoveredJobs: number
}

export interface TriggerResponse {
  status: string
  jobId: number
  jobName: string
  executionId: number | null
  message: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}
