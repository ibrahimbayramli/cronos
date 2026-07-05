import { Link } from 'react-router-dom'
import { ChevronRight } from 'lucide-react'
import type { JobSummary } from '../api/types'
import { StatusBadge } from './StatusBadge'
import { formatDateTime, formatDuration, formatRelative } from '../utils/format'

interface JobTableProps {
  jobs: JobSummary[]
}

export function JobTable({ jobs }: JobTableProps) {
  return (
    <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/40">
      <div className="overflow-x-auto">
        <table className="w-full min-w-[900px] text-left text-sm">
          <thead>
            <tr className="border-b border-slate-800 bg-slate-900/80 text-xs uppercase tracking-wider text-slate-500">
              <th className="px-5 py-3.5 font-medium">Job</th>
              <th className="px-5 py-3.5 font-medium">Kaynak</th>
              <th className="px-5 py-3.5 font-medium">Durum</th>
              <th className="px-5 py-3.5 font-medium">Son Çalışma</th>
              <th className="px-5 py-3.5 font-medium">Süre</th>
              <th className="px-5 py-3.5 font-medium">Sonraki</th>
              <th className="px-5 py-3.5 font-medium" />
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/80">
            {jobs.map((job) => (
              <tr
                key={job.id}
                className="group transition-colors hover:bg-slate-800/30"
              >
                <td className="px-5 py-4">
                  <div>
                    <p className="font-medium text-slate-100">{job.name}</p>
                    <p className="mt-0.5 font-mono text-xs text-slate-500">
                      {job.beanName}.{job.methodOrClass}
                    </p>
                  </div>
                </td>
                <td className="px-5 py-4">
                  <span className="rounded-md bg-slate-800 px-2 py-1 text-xs text-slate-300">
                    {job.sourceType.replace('_', ' ')}
                  </span>
                </td>
                <td className="px-5 py-4">
                  <StatusBadge status={job.lastStatus} size="sm" />
                </td>
                <td className="px-5 py-4 text-slate-400">
                  <div>
                    <p>{formatDateTime(job.lastRunAt)}</p>
                    <p className="text-xs text-slate-500">{formatRelative(job.lastRunAt)}</p>
                  </div>
                </td>
                <td className="px-5 py-4 font-mono text-slate-400">
                  {formatDuration(job.lastDurationMs)}
                </td>
                <td className="px-5 py-4 text-slate-400">
                  <div>
                    <p>{formatDateTime(job.nextRunAt)}</p>
                    <p className="text-xs text-slate-500">{formatRelative(job.nextRunAt)}</p>
                  </div>
                </td>
                <td className="px-5 py-4 text-right">
                  <Link
                    to={`/jobs/${job.id}`}
                    className="inline-flex items-center gap-1 rounded-lg px-2 py-1 text-cyan-400 opacity-0 transition group-hover:opacity-100 hover:bg-cyan-500/10"
                  >
                    Detay
                    <ChevronRight className="h-4 w-4" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
