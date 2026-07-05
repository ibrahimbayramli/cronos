import { Table, Tag, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { Link } from 'react-router-dom'
import type { JobSummary } from '../api/types'
import { StatusTag } from './StatusTag'
import { formatDateTime, formatDuration, formatRelative } from '../utils/format'

interface JobTableProps {
  jobs: JobSummary[]
  loading?: boolean
}

export function JobTable({ jobs, loading }: JobTableProps) {
  const columns: ColumnsType<JobSummary> = [
    {
      title: 'Job',
      key: 'name',
      render: (_, job) => (
        <div>
          <Typography.Text strong>{job.name}</Typography.Text>
          <br />
          <Typography.Text type="secondary" style={{ fontSize: 12, fontFamily: 'monospace' }}>
            {job.beanName}.{job.methodOrClass}
          </Typography.Text>
        </div>
      ),
    },
    {
      title: 'Kaynak',
      dataIndex: 'sourceType',
      key: 'sourceType',
      render: (value: string) => <Tag>{value.replace('_', ' ')}</Tag>,
    },
    {
      title: 'Durum',
      key: 'status',
      render: (_, job) => <StatusTag status={job.lastStatus} />,
    },
    {
      title: 'Son Çalışma',
      key: 'lastRunAt',
      render: (_, job) => (
        <div>
          <div>{formatDateTime(job.lastRunAt)}</div>
          <Typography.Text type="secondary" style={{ fontSize: 12 }}>
            {formatRelative(job.lastRunAt)}
          </Typography.Text>
        </div>
      ),
    },
    {
      title: 'Süre',
      key: 'duration',
      render: (_, job) => (
        <Typography.Text code>{formatDuration(job.lastDurationMs)}</Typography.Text>
      ),
    },
    {
      title: 'Sonraki',
      key: 'nextRunAt',
      render: (_, job) => (
        <div>
          <div>{formatDateTime(job.nextRunAt)}</div>
          <Typography.Text type="secondary" style={{ fontSize: 12 }}>
            {formatRelative(job.nextRunAt)}
          </Typography.Text>
        </div>
      ),
    },
    {
      title: '',
      key: 'action',
      width: 80,
      render: (_, job) => <Link to={`/jobs/${job.id}`}>Detay</Link>,
    },
  ]

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={jobs}
      loading={loading}
      pagination={false}
      scroll={{ x: 900 }}
    />
  )
}
