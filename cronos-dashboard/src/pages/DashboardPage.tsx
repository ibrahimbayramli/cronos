import { useCallback, useEffect, useState } from 'react'
import { Activity, AlertTriangle, CheckCircle2, Clock3 } from 'lucide-react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { HealthResponse, JobSummary } from '../api/types'
import { StatCard } from '../components/StatCard'
import { JobTable } from '../components/JobTable'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { EmptyState } from '../components/EmptyState'
import { formatRelative } from '../utils/format'

export function DashboardPage() {
  const [health, setHealth] = useState<HealthResponse | null>(null)
  const [jobs, setJobs] = useState<JobSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [healthData, jobsData] = await Promise.all([api.getHealth(), api.listJobs()])
      setHealth(healthData)
      setJobs(jobsData)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Veri yüklenemedi')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
    const interval = setInterval(load, 15_000)
    return () => clearInterval(interval)
  }, [load])

  if (loading && !health) {
    return <LoadingSpinner label="Dashboard yükleniyor..." />
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-500/20 bg-rose-500/5 p-6 text-center">
        <AlertTriangle className="mx-auto h-8 w-8 text-rose-400" />
        <h2 className="mt-3 font-medium text-rose-300">API bağlantısı kurulamadı</h2>
        <p className="mt-2 text-sm text-slate-400">{error}</p>
        <p className="mt-4 text-xs text-slate-500">
          Spring Boot uygulamanızın çalıştığından ve Cronos API&apos;nin{' '}
          <code className="rounded bg-slate-800 px-1.5 py-0.5">/cronos/api</code> altında
          erişilebilir olduğundan emin olun.
        </p>
        <button
          type="button"
          onClick={load}
          className="mt-4 rounded-xl bg-slate-800 px-4 py-2 text-sm text-slate-200 hover:bg-slate-700"
        >
          Tekrar Dene
        </button>
      </div>
    )
  }

  const successCount = jobs.filter((j) => j.lastStatus === 'SUCCESS').length
  const failedCount = jobs.filter((j) => j.lastStatus === 'FAILED').length
  const runningCount = jobs.filter((j) => j.lastStatus === 'RUNNING').length
  const recentJobs = [...jobs]
    .sort((a, b) => {
      const aTime = a.lastRunAt ? new Date(a.lastRunAt).getTime() : 0
      const bTime = b.lastRunAt ? new Date(b.lastRunAt).getTime() : 0
      return bTime - aTime
    })
    .slice(0, 5)

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Genel Bakış</h1>
        <p className="mt-1 text-sm text-slate-400">
          Keşfedilen scheduled job&apos;ların anlık durumu
          {health && (
            <span className="ml-2 text-slate-500">
              · Son güncelleme {formatRelative(health.timestamp)}
            </span>
          )}
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard
          title="Keşfedilen Job"
          value={health?.discoveredJobs ?? 0}
          subtitle={`Sistem durumu: ${health?.status ?? '—'}`}
          icon={Activity}
          accent="cyan"
        />
        <StatCard
          title="Başarılı"
          value={successCount}
          subtitle="Son çalışmada başarılı"
          icon={CheckCircle2}
          accent="emerald"
        />
        <StatCard
          title="Hatalı"
          value={failedCount}
          subtitle="Son çalışmada hata"
          icon={AlertTriangle}
          accent="rose"
        />
        <StatCard
          title="Çalışıyor"
          value={runningCount}
          subtitle="Aktif execution"
          icon={Clock3}
          accent="amber"
        />
      </div>

      <section>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-medium text-white">Son Çalışan Joblar</h2>
          <Link
            to="/jobs"
            className="text-sm text-cyan-400 transition hover:text-cyan-300"
          >
            Tümünü gör →
          </Link>
        </div>

        {recentJobs.length === 0 ? (
          <EmptyState
            icon={Activity}
            title="Henüz job bulunamadı"
            description="@Scheduled ile tanımlı job'lar uygulama başladığında otomatik keşfedilir."
          />
        ) : (
          <JobTable jobs={recentJobs} />
        )}
      </section>
    </div>
  )
}
