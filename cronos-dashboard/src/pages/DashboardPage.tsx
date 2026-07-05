import { useCallback, useEffect, useState } from 'react'
import {
  Alert,
  Button,
  Card,
  Col,
  Empty,
  Row,
  Spin,
  Statistic,
  Typography,
} from 'antd'
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  DashboardOutlined,
  WarningOutlined,
} from '@ant-design/icons'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { HealthResponse, JobSummary } from '../api/types'
import { JobTable } from '../components/JobTable'
import { formatRelative } from '../utils/format'

export function DashboardPage() {
  const [health, setHealth] = useState<HealthResponse | null>(null)
  const [jobs, setJobs] = useState<JobSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [healthData, jobsData] = await Promise.all([api.getHealth(), api.listJobs()])
      setHealth(healthData)
      setJobs(jobsData)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Veri yüklenemedi')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
    const interval = setInterval(load, 15_000)
    return () => clearInterval(interval)
  }, [load])

  if (loading && !health) {
    return (
      <div style={{ textAlign: 'center', padding: 80 }}>
        <Spin size="large" tip="Dashboard yükleniyor..." />
      </div>
    )
  }

  if (error) {
    return (
      <Alert
        type="error"
        showIcon
        message="API bağlantısı kurulamadı"
        description={
          <>
            <Typography.Paragraph style={{ marginBottom: 8 }}>{error}</Typography.Paragraph>
            <Typography.Text type="secondary">
              Spring Boot uygulamanızın çalıştığından ve Cronos API&apos;nin{' '}
              <Typography.Text code>/cronos/api</Typography.Text> altında erişilebilir
              olduğundan emin olun.
            </Typography.Text>
          </>
        }
        action={
          <Button size="small" onClick={load}>
            Tekrar Dene
          </Button>
        }
      />
    )
  }

  const successCount = jobs.filter((j) => j.lastStatus === 'SUCCESS').length
  const failedCount = jobs.filter((j) => j.lastStatus === 'FAILED').length
  const runningCount = jobs.filter((j) => j.lastStatus === 'RUNNING').length
  const recentJobs = [...jobs]
    .sort((a, b) => {
      const aTime = a.lastRunAt ? new Date(a.lastRunAt).getTime() : 0
      const bTime = b.lastRunAt ? new Date(b.lastRunAt).getTime() : 0
      return bTime - aTime
    })
    .slice(0, 5)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <Typography.Title level={3} style={{ margin: 0 }}>
          Genel Bakış
        </Typography.Title>
        <Typography.Text type="secondary">
          Keşfedilen scheduled job&apos;ların anlık durumu
          {health && <> · Son güncelleme {formatRelative(health.timestamp)}</>}
        </Typography.Text>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} xl={6}>
          <Card>
            <Statistic
              title="Keşfedilen Job"
              value={health?.discoveredJobs ?? 0}
              prefix={<DashboardOutlined />}
              suffix={
                <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                  {health?.status}
                </Typography.Text>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} xl={6}>
          <Card>
            <Statistic
              title="Başarılı"
              value={successCount}
              valueStyle={{ color: '#52c41a' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} xl={6}>
          <Card>
            <Statistic
              title="Hatalı"
              value={failedCount}
              valueStyle={{ color: '#ff4d4f' }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} xl={6}>
          <Card>
            <Statistic
              title="Çalışıyor"
              value={runningCount}
              valueStyle={{ color: '#faad14' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card
        title="Son Çalışan Joblar"
        extra={<Link to="/jobs">Tümünü gör →</Link>}
      >
        {recentJobs.length === 0 ? (
          <Empty
            description="@Scheduled ile tanımlı job'lar uygulama başladığında otomatik keşfedilir."
          />
        ) : (
          <JobTable jobs={recentJobs} />
        )}
      </Card>
    </div>
  )
}
