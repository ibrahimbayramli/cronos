import { format, formatDistanceToNow, isValid, parseISO } from 'date-fns'
import { tr } from 'date-fns/locale'

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return '—'
  const date = parseISO(value)
  if (!isValid(date)) return '—'
  return format(date, 'dd MMM yyyy HH:mm:ss', { locale: tr })
}

export function formatRelative(value: string | null | undefined): string {
  if (!value) return '—'
  const date = parseISO(value)
  if (!isValid(date)) return '—'
  return formatDistanceToNow(date, { addSuffix: true, locale: tr })
}

export function formatDuration(ms: number | null | undefined): string {
  if (ms == null) return '—'
  if (ms < 1000) return `${ms}ms`
  if (ms < 60_000) return `${(ms / 1000).toFixed(1)}s`
  const minutes = Math.floor(ms / 60_000)
  const seconds = Math.floor((ms % 60_000) / 1000)
  return `${minutes}m ${seconds}s`
}

export function formatSourceType(source: string): string {
  return source.replace(/_/g, ' ')
}
