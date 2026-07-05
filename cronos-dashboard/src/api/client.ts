import type {
  HealthResponse,
  JobDetail,
  JobExecution,
  JobSummary,
  PageResponse,
  TriggerResponse,
} from './types'

const API_BASE = '/cronos/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      Accept: 'application/json',
      ...init?.headers,
    },
    ...init,
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Request failed (${response.status})`)
  }

  return response.json() as Promise<T>
}

export const api = {
  getHealth: () => request<HealthResponse>('/health'),

  listJobs: () => request<JobSummary[]>('/jobs'),

  getJob: (id: number) => request<JobDetail>(`/jobs/${id}`),

  getExecutions: (id: number, page = 0, size = 20) =>
    request<PageResponse<JobExecution>>(
      `/jobs/${id}/executions?page=${page}&size=${size}&sort=startedAt,desc`,
    ),

  triggerJob: (id: number) =>
    request<TriggerResponse>(`/jobs/${id}/trigger`, { method: 'POST' }),
}
