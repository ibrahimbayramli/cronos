import { Tag } from 'antd'
import type { ExecutionStatus } from '../api/types'

const statusConfig: Record<
  ExecutionStatus,
  { label: string; color: string }
> = {
  SUCCESS: { label: 'Başarılı', color: 'success' },
  FAILED: { label: 'Hatalı', color: 'error' },
  RUNNING: { label: 'Çalışıyor', color: 'processing' },
}

interface StatusTagProps {
  status: ExecutionStatus | null | undefined
}

export function StatusTag({ status }: StatusTagProps) {
  if (!status) {
    return <Tag color="default">Bekleniyor</Tag>
  }

  const config = statusConfig[status]
  return <Tag color={config.color}>{config.label}</Tag>
}
