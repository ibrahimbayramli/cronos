import type { LucideIcon } from 'lucide-react'

interface StatCardProps {
  title: string
  value: string | number
  subtitle?: string
  icon: LucideIcon
  accent?: 'cyan' | 'emerald' | 'amber' | 'rose'
}

const accentStyles = {
  cyan: 'text-cyan-400 bg-cyan-500/10 border-cyan-500/20',
  emerald: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/20',
  amber: 'text-amber-400 bg-amber-500/10 border-amber-500/20',
  rose: 'text-rose-400 bg-rose-500/10 border-rose-500/20',
}

export function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  accent = 'cyan',
}: StatCardProps) {
  return (
    <div className="glass group rounded-2xl p-5 transition-colors hover:border-slate-700">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-slate-400">{title}</p>
          <p className="mt-2 text-3xl font-semibold tracking-tight text-white">{value}</p>
          {subtitle && <p className="mt-1 text-xs text-slate-500">{subtitle}</p>}
        </div>
        <div
          className={`flex h-10 w-10 items-center justify-center rounded-xl border ${accentStyles[accent]}`}
        >
          <Icon className="h-5 w-5" />
        </div>
      </div>
    </div>
  )
}
