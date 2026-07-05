import type { JobExecution } from '../api/types'
import { StatusBadge } from './StatusBadge'
import { formatDateTime, formatDuration } from '../utils/format'

interface ExecutionTableProps {
  executions: JobExecution[]
}

export function ExecutionTable({ executions }: ExecutionTableProps) {
  return (
    <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/40">
      <div className="overflow-x-auto">
        <table className="w-full min-w-[800px] text-left text-sm">
          <thead>
            <tr className="border-b border-slate-800 bg-slate-900/80 text-xs uppercase tracking-wider text-slate-500">
              <th className="px-5 py-3.5 font-medium">ID</th>
              <th className="px-5 py-3.5 font-medium">Durum</th>
              <th className="px-5 py-3.5 font-medium">Başlangıç</th>
              <th className="px-5 py-3.5 font-medium">Bitiş</th>
              <th className="px-5 py-3.5 font-medium">Süre</th>
              <th className="px-5 py-3.5 font-medium">Tetikleyici</th>
              <th className="px-5 py-3.5 font-medium">Hata</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/80">
            {executions.map((execution) => (
              <tr key={execution.id} className="hover:bg-slate-800/30">
                <td className="px-5 py-4 font-mono text-slate-400">#{execution.id}</td>
                <td className="px-5 py-4">
                  <StatusBadge status={execution.status} size="sm" />
                </td>
                <td className="px-5 py-4 text-slate-400">
                  {formatDateTime(execution.startedAt)}
                </td>
                <td className="px-5 py-4 text-slate-400">
                  {formatDateTime(execution.finishedAt)}
                </td>
                <td className="px-5 py-4 font-mono text-slate-400">
                  {formatDuration(execution.durationMs)}
                </td>
                <td className="px-5 py-4">
                  <span
                    className={`rounded-md px-2 py-1 text-xs ${
                      execution.triggerSource === 'MANUAL'
                        ? 'bg-violet-500/10 text-violet-400'
                        : 'bg-slate-800 text-slate-400'
                    }`}
                  >
                    {execution.triggerSource === 'MANUAL' ? 'Manuel' : 'Otomatik'}
                  </span>
                </td>
                <td className="max-w-xs truncate px-5 py-4 text-xs text-rose-400">
                  {execution.errorMessage || '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
