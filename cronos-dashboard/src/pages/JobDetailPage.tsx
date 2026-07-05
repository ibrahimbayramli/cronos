import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ArrowLeft, ChevronLeft, ChevronRight, History } from 'lucide-react'
import { api } from '../api/client'
import type { JobDetail, JobExecution } from '../api/types'
import { StatusBadge } from '../components/StatusBadge'
import { ExecutionTable } from '../components/ExecutionTable'
import { TriggerButton } from '../components/TriggerButton'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { EmptyState } from '../components/EmptyState'
import {
  formatDateTime,
  formatDuration,
  formatRelative,
  formatSourceType,
} from '../utils/format'

export function JobDetailPage() {
  const { id } = useParams<{ id: string }>()
  const jobId = Number(id)

  const [job, setJob] = useState<JobDetail | null>(null)
  const [executions, setExecutions] = useState<JobExecution[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    if (!jobId || Number.isNaN(jobId)) return

    setLoading(true)
    setError(null)
    try {
      const [jobData, executionsData] = await Promise.all([
        api.getJob(jobId),
        api.getExecutions(jobId, page),
      ])
      setJob(jobData)
      setExecutions(executionsData.content)
      setTotalPages(executionsData.totalPages)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Job detayı yüklenemedi')
    } finally {
      setLoading(false)
    }
  }, [jobId, page])

  useEffect(() => {
    load()
  }, [load])

  if (loading && !job) {
    return <LoadingSpinner label="Job detayı yükleniyor..." />
  }

  if (error || !job) {
    return (
      <div className="space-y-4">
        <Link
          to="/jobs"
          className="inline-flex items-center gap-2 text-sm text-slate-400 hover:text-slate-200"
        >
          <ArrowLeft className="h-4 w-4" />
          Joblara dön
        </Link>
        <div className="rounded-2xl border border-rose-500/20 bg-rose-500/5 p-6 text-center text-rose-300">
          {error || 'Job bulunamadı'}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      <div>
        <Link
          to="/jobs"
          className="inline-flex items-center gap-2 text-sm text-slate-400 transition hover:text-slate-200"
        >
          <ArrowLeft className="h-4 w-4" />
          Joblara dön
        </Link>

        <div className="mt-4 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-semibold tracking-tight text-white">{job.name}</h1>
              <StatusBadge status={job.lastStatus} />
            </div>
            <p className="mt-2 font-mono text-sm text-slate-500">
              {job.beanName}.{job.methodOrClass}
            </p>
          </div>
          <TriggerButton jobId={job.id} onTriggered={load} />
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {[
          { label: 'Kaynak', value: formatSourceType(job.sourceType) },
          { label: 'Trigger', value: job.triggerInfo },
          { label: 'Toplam Çalışma', value: job.totalExecutions.toString() },
          { label: 'Son Süre', value: formatDuration(job.lastDurationMs) },
        ].map((item) => (
          <div key={item.label} className="glass rounded-2xl p-4">
            <p className="text-xs font-medium uppercase tracking-wider text-slate-500">
              {item.label}
            </p>
            <p className="mt-2 text-sm font-medium text-slate-200">{item.value}</p>
          </div>
        ))}
      </div>

      <div className="grid gap-4 lg:grid-cols-3">
        <div className="glass rounded-2xl p-5 lg:col-span-1">
          <h3 className="text-sm font-medium text-slate-400">Zamanlama</h3>
          <dl className="mt-4 space-y-4 text-sm">
            <div>
              <dt className="text-slate-500">Keşfedilme</dt>
              <dd className="mt-1 text-slate-200">{formatDateTime(job.discoveredAt)}</dd>
            </div>
            <div>
              <dt className="text-slate-500">Son Çalışma</dt>
              <dd className="mt-1 text-slate-200">
                {formatDateTime(job.lastRunAt)}
                <span className="ml-2 text-xs text-slate-500">
                  {formatRelative(job.lastRunAt)}
                </span>
              </dd>
            </div>
            <div>
              <dt className="text-slate-500">Sonraki Çalışma</dt>
              <dd className="mt-1 text-slate-200">
                {formatDateTime(job.nextRunAt)}
                <span className="ml-2 text-xs text-slate-500">
                  {formatRelative(job.nextRunAt)}
                </span>
              </dd>
            </div>
            <div>
              <dt className="text-slate-500">Durum</dt>
              <dd className="mt-1">
                <span
                  className={`inline-flex rounded-md px-2 py-1 text-xs ${
                    job.enabled
                      ? 'bg-emerald-500/10 text-emerald-400'
                      : 'bg-slate-800 text-slate-500'
                  }`}
                >
                  {job.enabled ? 'Aktif' : 'Pasif'}
                </span>
              </dd>
            </div>
          </dl>
        </div>

        <div className="lg:col-span-2">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="flex items-center gap-2 text-lg font-medium text-white">
              <History className="h-5 w-5 text-slate-400" />
              Çalışma Geçmişi
            </h2>
            {totalPages > 1 && (
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="rounded-lg border border-slate-700 p-1.5 text-slate-400 hover:text-white disabled:opacity-40"
                >
                  <ChevronLeft className="h-4 w-4" />
                </button>
                <span className="text-xs text-slate-500">
                  Sayfa {page + 1} / {totalPages}
                </span>
                <button
                  type="button"
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                  className="rounded-lg border border-slate-700 p-1.5 text-slate-400 hover:text-white disabled:opacity-40"
                >
                  <ChevronRight className="h-4 w-4" />
                </button>
              </div>
            )}
          </div>

          {executions.length === 0 ? (
            <EmptyState
              icon={History}
              title="Henüz çalışma kaydı yok"
              description="Job ilk çalıştığında veya manuel tetiklendiğinde kayıtlar burada görünecek."
            />
          ) : (
            <ExecutionTable executions={executions} />
          )}
        </div>
      </div>
    </div>
  )
}
