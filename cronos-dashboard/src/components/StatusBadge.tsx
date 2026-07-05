import type { ExecutionStatus } from '../api/types'

const statusConfig: Record<
  ExecutionStatus,
  { label: string; className: string; dotClassName: string }
> = {
  SUCCESS: {
    label: 'Başarılı',
    className: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
    dotClassName: 'bg-emerald-400',
  },
  FAILED: {
    label: 'Hatalı',
    className: 'bg-rose-500/10 text-rose-400 border-rose-500/20',
    dotClassName: 'bg-rose-400',
  },
  RUNNING: {
    label: 'Çalışıyor',
    className: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
    dotClassName: 'bg-amber-400 animate-pulse',
  },
}

interface StatusBadgeProps {
  status: ExecutionStatus | null | undefined
  size?: 'sm' | 'md'
}

export function StatusBadge({ status, size = 'md' }: StatusBadgeProps) {
  if (!status) {
    return (
      <span
        className={`inline-flex items-center rounded-full border border-slate-700 bg-slate-800/50 text-slate-400 ${
          size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-2.5 py-1 text-xs'
        }`}
      >
        Bekleniyor
      </span>
    )
  }

  const config = statusConfig[status]

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border font-medium ${config.className} ${
        size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-2.5 py-1 text-xs'
      }`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${config.dotClassName}`} />
      {config.label}
    </span>
  )
}
