import { Table, Tag, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { JobExecution } from '../api/types'
import { StatusTag } from './StatusTag'
import { formatDateTime, formatDuration } from '../utils/format'

interface ExecutionTableProps {
  executions: JobExecution[]
  loading?: boolean
}

export function ExecutionTable({ executions, loading }: ExecutionTableProps) {
  const columns: ColumnsType<JobExecution> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      render: (id: number) => <Typography.Text code>#{id}</Typography.Text>,
    },
    {
      title: 'Durum',
      key: 'status',
      render: (_, row) => <StatusTag status={row.status} />,
    },
    {
      title: 'Başlangıç',
      dataIndex: 'startedAt',
      key: 'startedAt',
      render: (value: string) => formatDateTime(value),
    },
    {
      title: 'Bitiş',
      dataIndex: 'finishedAt',
      key: 'finishedAt',
      render: (value: string | null) => formatDateTime(value),
    },
    {
      title: 'Süre',
      key: 'duration',
      render: (_, row) => (
        <Typography.Text code>{formatDuration(row.durationMs)}</Typography.Text>
      ),
    },
    {
      title: 'Tetikleyici',
      dataIndex: 'triggerSource',
      key: 'triggerSource',
      render: (value: string) => (
        <Tag color={value === 'MANUAL' ? 'purple' : 'default'}>
          {value === 'MANUAL' ? 'Manuel' : 'Otomatik'}
        </Tag>
      ),
    },
    {
      title: 'Hata',
      dataIndex: 'errorMessage',
      key: 'errorMessage',
      ellipsis: true,
      render: (value: string | null) =>
        value ? (
          <Typography.Text type="danger" style={{ fontSize: 12 }}>
            {value}
          </Typography.Text>
        ) : (
          '—'
        ),
    },
  ]

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={executions}
      loading={loading}
      pagination={false}
      scroll={{ x: 800 }}
    />
  )
}
