import { Loader2 } from 'lucide-react'

interface LoadingSpinnerProps {
  label?: string
}

export function LoadingSpinner({ label = 'Yükleniyor...' }: LoadingSpinnerProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-slate-400">
      <Loader2 className="h-8 w-8 animate-spin text-cyan-400" />
      <p className="text-sm">{label}</p>
    </div>
  )
}
