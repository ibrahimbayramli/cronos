import { useCallback, useEffect, useMemo, useState } from 'react'
import { Alert, Card, Empty, Input, Select, Spin, Typography } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import { api } from '../api/client'
import type { ExecutionStatus, JobSummary } from '../api/types'
import { JobTable } from '../components/JobTable'

const statusOptions: { value: string; label: string }[] = [
  { value: 'ALL', label: 'Tüm durumlar' },
  { value: 'SUCCESS', label: 'Başarılı' },
  { value: 'FAILED', label: 'Hatalı' },
  { value: 'RUNNING', label: 'Çalışıyor' },
  { value: 'PENDING', label: 'Bekleniyor' },
]

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
        job.lastStatus === (statusFilter as ExecutionStatus)

      return matchesSearch && matchesStatus
    })
  }, [jobs, search, statusFilter])

  if (loading && jobs.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: 80 }}>
        <Spin size="large" tip="Joblar yükleniyor..." />
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <Typography.Title level={3} style={{ margin: 0 }}>
          Joblar
        </Typography.Title>
        <Typography.Text type="secondary">
          Keşfedilen tüm scheduled job&apos;lar ({jobs.length})
        </Typography.Text>
      </div>

      <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
        <Input
          allowClear
          prefix={<SearchOutlined />}
          placeholder="Job adı, bean veya metot ara..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ flex: 1, minWidth: 240 }}
        />
        <Select
          value={statusFilter}
          onChange={setStatusFilter}
          options={statusOptions}
          style={{ width: 180 }}
        />
      </div>

      {error && <Alert type="error" message={error} showIcon />}

      <Card>
        {filteredJobs.length === 0 ? (
          <Empty
            description={
              jobs.length === 0
                ? 'Henüz keşfedilmiş job yok. Uygulamanızda @Scheduled metotlar tanımlayın.'
                : 'Arama veya filtre kriterlerinize uygun job bulunamadı.'
            }
          />
        ) : (
          <JobTable jobs={filteredJobs} loading={loading} />
        )}
      </Card>
    </div>
  )
}
