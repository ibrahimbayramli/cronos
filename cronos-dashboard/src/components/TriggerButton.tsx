import { useState } from 'react'
import { Play, Loader2 } from 'lucide-react'
import { api } from '../api/client'

interface TriggerButtonProps {
  jobId: number
  onTriggered?: () => void
}

export function TriggerButton({ jobId, onTriggered }: TriggerButtonProps) {
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  async function handleTrigger() {
    setLoading(true)
    setMessage(null)
    setError(null)

    try {
      const result = await api.triggerJob(jobId)
      setMessage(result.message || 'Job tetiklendi')
      onTriggered?.()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Tetikleme başarısız')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex flex-col items-end gap-2">
      <button
        type="button"
        onClick={handleTrigger}
        disabled={loading}
        className="inline-flex items-center gap-2 rounded-xl bg-cyan-500 px-4 py-2.5 text-sm font-medium text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {loading ? (
          <Loader2 className="h-4 w-4 animate-spin" />
        ) : (
          <Play className="h-4 w-4" />
        )}
        Manuel Tetikle
      </button>
      {message && <p className="text-xs text-emerald-400">{message}</p>}
      {error && <p className="text-xs text-rose-400">{error}</p>}
    </div>
  )
}
