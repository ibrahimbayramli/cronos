import { useMemo } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { ClockCircleOutlined, DashboardOutlined, UnorderedListOutlined } from '@ant-design/icons'
import { Layout as AntLayout, Menu, Typography } from 'antd'

const { Header, Sider, Content } = AntLayout

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: 'Genel Bakış' },
  { key: '/jobs', icon: <UnorderedListOutlined />, label: 'Joblar' },
]

export function Layout() {
  const location = useLocation()
  const navigate = useNavigate()

  const selectedKey = useMemo(() => {
    if (location.pathname.startsWith('/jobs')) return '/jobs'
    return '/'
  }, [location.pathname])

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider
        breakpoint="lg"
        collapsedWidth={0}
        width={240}
        style={{
          borderRight: '1px solid rgba(255,255,255,0.08)',
        }}
      >
        <div style={{ padding: '20px 16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div
              style={{
                width: 40,
                height: 40,
                borderRadius: 10,
                background: 'rgba(19, 194, 194, 0.15)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <ClockCircleOutlined style={{ fontSize: 20, color: '#13c2c2' }} />
            </div>
            <div>
              <Typography.Title level={4} style={{ margin: 0, color: '#fff' }}>
                Cronos
              </Typography.Title>
              <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                Job Observability
              </Typography.Text>
            </div>
          </div>
        </div>

        <Menu
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          style={{ borderInlineEnd: 'none' }}
        />

        <div style={{ padding: 16, marginTop: 'auto' }}>
          <Typography.Text type="secondary" style={{ fontSize: 12 }}>
            Cronos Dashboard v0.1.0
          </Typography.Text>
        </div>
      </Sider>

      <AntLayout>
        <Header
          style={{
            padding: '0 24px',
            background: 'transparent',
            borderBottom: '1px solid rgba(255,255,255,0.08)',
            display: 'flex',
            alignItems: 'center',
          }}
        >
          <Typography.Title level={5} style={{ margin: 0 }}>
            {selectedKey === '/' ? 'Genel Bakış' : 'Joblar'}
          </Typography.Title>
        </Header>

        <Content style={{ padding: 24, maxWidth: 1400, margin: '0 auto', width: '100%' }}>
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  )
}
