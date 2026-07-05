import { useCallback, useEffect, useMemo, useState } from 'react'
import { Search, ListTodo } from 'lucide-react'
import { api } from '../api/client'
import type { JobSummary } from '../api/types'
import { JobTable } from '../components/JobTable'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { EmptyState } from '../components/EmptyState'
import { StatusBadge } from '../components/StatusBadge'

export function JobsPage() {
  const [jobs, setJobs] = useState<JobSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState<string>('ALL')

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await api.listJobs()
      setJobs(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Joblar yüklenemedi')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const filteredJobs = useMemo(() => {
    const query = search.trim().toLowerCase()
    return jobs.filter((job) => {
      const matchesSearch =
        !query ||
        job.name.toLowerCase().includes(query) ||
        job.beanName.toLowerCase().includes(query) ||
        job.methodOrClass.toLowerCase().includes(query)

      const matchesStatus =
        statusFilter === 'ALL' ||
        (statusFilter === 'PENDING' && !job.lastStatus) ||
        job.lastStatus === statusFilter

      return matchesSearch && matchesStatus
    })
  }, [jobs, search, statusFilter])

  if (loading) {
    return <LoadingSpinner label="Joblar yükleniyor..." />
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Joblar</h1>
        <p className="mt-1 text-sm text-slate-400">
          Keşfedilen tüm scheduled job&apos;lar ({jobs.length})
        </p>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
          <input
            type="text"
            placeholder="Job adı, bean veya metot ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/60 py-2.5 pl-10 pr-4 text-sm text-slate-200 placeholder:text-slate-500 focus:border-cyan-500/50 focus:outline-none focus:ring-1 focus:ring-cyan-500/30"
          />
        </div>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-xl border border-slate-800 bg-slate-900/60 px-4 py-2.5 text-sm text-slate-200 focus:border-cyan-500/50 focus:outline-none"
        >
          <option value="ALL">Tüm durumlar</option>
          <option value="SUCCESS">Başarılı</option>
          <option value="FAILED">Hatalı</option>
          <option value="RUNNING">Çalışıyor</option>
          <option value="PENDING">Bekleniyor</option>
        </select>
      </div>

      {error && (
        <div className="rounded-xl border border-rose-500/20 bg-rose-500/5 px-4 py-3 text-sm text-rose-300">
          {error}
        </div>
      )}

      {filteredJobs.length === 0 ? (
        <EmptyState
          icon={ListTodo}
          title="Job bulunamadı"
          description={
            jobs.length === 0
              ? 'Henüz keşfedilmiş job yok. Uygulamanızda @Scheduled metotlar tanımlayın.'
              : 'Arama veya filtre kriterlerinize uygun job bulunamadı.'
          }
        />
      ) : (
        <JobTable jobs={filteredJobs} />
      )}

      <div className="flex flex-wrap gap-2">
        {(['SUCCESS', 'FAILED', 'RUNNING'] as const).map((status) => (
          <div key={status} className="flex items-center gap-2 text-xs text-slate-500">
            <StatusBadge status={status} size="sm" />
          </div>
        ))}
      </div>
    </div>
  )
}
