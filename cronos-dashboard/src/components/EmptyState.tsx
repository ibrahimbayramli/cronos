import type { LucideIcon } from 'lucide-react'

interface EmptyStateProps {
  icon: LucideIcon
  title: string
  description: string
}

export function EmptyState({ icon: Icon, title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 rounded-2xl border border-dashed border-slate-800 bg-slate-900/30 px-6 py-16 text-center">
      <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-slate-800/80">
        <Icon className="h-6 w-6 text-slate-500" />
      </div>
      <div>
        <h3 className="font-medium text-slate-200">{title}</h3>
        <p className="mt-1 max-w-sm text-sm text-slate-500">{description}</p>
      </div>
    </div>
  )
}
