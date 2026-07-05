import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import {
  Alert,
  Button,
  Card,
  Col,
  Descriptions,
  Empty,
  message,
  Pagination,
  Row,
  Spin,
  Tag,
  Typography,
} from 'antd'
import { ArrowLeftOutlined, HistoryOutlined, PlayCircleOutlined } from '@ant-design/icons'
import { api } from '../api/client'
import type { JobDetail, JobExecution } from '../api/types'
import { ExecutionTable } from '../components/ExecutionTable'
import { StatusTag } from '../components/StatusTag'
import {
  formatDateTime,
  formatDuration,
  formatRelative,
  formatSourceType,
} from '../utils/format'

export function JobDetailPage() {
  const { id } = useParams<{ id: string }>()
  const jobId = Number(id)

  const [job, setJob] = useState<JobDetail | null>(null)
  const [executions, setExecutions] = useState<JobExecution[]>([])
  const [page, setPage] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [pageSize] = useState(20)
  const [loading, setLoading] = useState(true)
  const [triggering, setTriggering] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    if (!jobId || Number.isNaN(jobId)) return

    setLoading(true)
    setError(null)
    try {
      const [jobData, executionsData] = await Promise.all([
        api.getJob(jobId),
        api.getExecutions(jobId, page),
      ])
      setJob(jobData)
      setExecutions(executionsData.content)
      setTotalElements(executionsData.totalElements)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Job detayı yüklenemedi')
    } finally {
      setLoading(false)
    }
  }, [jobId, page])

  useEffect(() => {
    load()
  }, [load])

  async function handleTrigger() {
    if (!job) return

    setTriggering(true)
    try {
      const result = await api.triggerJob(job.id)
      message.success(result.message || 'Job tetiklendi')
      load()
    } catch (err) {
      message.error(err instanceof Error ? err.message : 'Tetikleme başarısız')
    } finally {
      setTriggering(false)
    }
  }

  if (loading && !job) {
    return (
      <div style={{ textAlign: 'center', padding: 80 }}>
        <Spin size="large" tip="Job detayı yükleniyor..." />
      </div>
    )
  }

  if (error || !job) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        <Link to="/jobs">
          <Button type="link" icon={<ArrowLeftOutlined />} style={{ padding: 0 }}>
            Joblara dön
          </Button>
        </Link>
        <Alert type="error" message={error || 'Job bulunamadı'} showIcon />
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <Link to="/jobs">
          <Button type="link" icon={<ArrowLeftOutlined />} style={{ padding: 0, marginBottom: 8 }}>
            Joblara dön
          </Button>
        </Link>

        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            flexWrap: 'wrap',
            gap: 16,
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <Typography.Title level={3} style={{ margin: 0 }}>
                {job.name}
              </Typography.Title>
              <StatusTag status={job.lastStatus} />
            </div>
            <Typography.Text type="secondary" style={{ fontFamily: 'monospace' }}>
              {job.beanName}.{job.methodOrClass}
            </Typography.Text>
          </div>

          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            loading={triggering}
            onClick={handleTrigger}
          >
            Manuel Tetikle
          </Button>
        </div>
      </div>

      <Row gutter={[16, 16]}>
        {[
          { label: 'Kaynak', value: formatSourceType(job.sourceType) },
          { label: 'Trigger', value: job.triggerInfo },
          { label: 'Toplam Çalışma', value: job.totalExecutions.toString() },
          { label: 'Son Süre', value: formatDuration(job.lastDurationMs) },
        ].map((item) => (
          <Col key={item.label} xs={24} sm={12} lg={6}>
            <Card size="small">
              <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                {item.label}
              </Typography.Text>
              <Typography.Paragraph strong style={{ margin: '4px 0 0' }}>
                {item.value}
              </Typography.Paragraph>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={8}>
          <Card title="Zamanlama" size="small">
            <Descriptions column={1} size="small">
              <Descriptions.Item label="Keşfedilme">
                {formatDateTime(job.discoveredAt)}
              </Descriptions.Item>
              <Descriptions.Item label="Son Çalışma">
                {formatDateTime(job.lastRunAt)}
                <Typography.Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>
                  {formatRelative(job.lastRunAt)}
                </Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label="Sonraki Çalışma">
                {formatDateTime(job.nextRunAt)}
                <Typography.Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>
                  {formatRelative(job.nextRunAt)}
                </Typography.Text>
              </Descriptions.Item>
              <Descriptions.Item label="Durum">
                <Tag color={job.enabled ? 'success' : 'default'}>
                  {job.enabled ? 'Aktif' : 'Pasif'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>

        <Col xs={24} lg={16}>
          <Card
            title={
              <span>
                <HistoryOutlined style={{ marginRight: 8 }} />
                Çalışma Geçmişi
              </span>
            }
          >
            {executions.length === 0 ? (
              <Empty description="Job ilk çalıştığında veya manuel tetiklendiğinde kayıtlar burada görünecek." />
            ) : (
              <>
                <ExecutionTable executions={executions} loading={loading} />
                {totalElements > pageSize && (
                  <div style={{ marginTop: 16, textAlign: 'right' }}>
                    <Pagination
                      current={page + 1}
                      total={totalElements}
                      pageSize={pageSize}
                      showSizeChanger={false}
                      onChange={(p) => setPage(p - 1)}
                    />
                  </div>
                )}
              </>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  )
}
