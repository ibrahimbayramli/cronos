import { NavLink, Outlet } from 'react-router-dom'
import { Clock, LayoutDashboard, ListTodo, RefreshCw } from 'lucide-react'

interface LayoutProps {
  onRefresh?: () => void
  refreshing?: boolean
}

const navItems = [
  { to: '/', label: 'Genel Bakış', icon: LayoutDashboard, end: true },
  { to: '/jobs', label: 'Joblar', icon: ListTodo, end: false },
]

export function Layout({ onRefresh, refreshing }: LayoutProps) {
  return (
    <div className="min-h-screen bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-slate-900 via-slate-950 to-slate-950">
      <div className="mx-auto flex min-h-screen max-w-7xl">
        <aside className="hidden w-64 shrink-0 flex-col border-r border-slate-800/80 bg-slate-950/50 p-6 lg:flex">
          <div className="mb-10 flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-cyan-500/10 glow-cyan">
              <Clock className="h-5 w-5 text-cyan-400" />
            </div>
            <div>
              <h1 className="text-lg font-semibold tracking-tight text-white">Cronos</h1>
              <p className="text-xs text-slate-500">Job Observability</p>
            </div>
          </div>

          <nav className="space-y-1">
            {navItems.map(({ to, label, icon: Icon, end }) => (
              <NavLink
                key={to}
                to={to}
                end={end}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition ${
                    isActive
                      ? 'bg-cyan-500/10 text-cyan-300'
                      : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200'
                  }`
                }
              >
                <Icon className="h-4 w-4" />
                {label}
              </NavLink>
            ))}
          </nav>

          <div className="mt-auto rounded-xl border border-slate-800 bg-slate-900/50 p-4">
            <p className="text-xs font-medium text-slate-400">Cronos Dashboard</p>
            <p className="mt-1 text-xs text-slate-600">v0.1.0 MVP</p>
          </div>
        </aside>

        <div className="flex min-w-0 flex-1 flex-col">
          <header className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-800/80 bg-slate-950/80 px-6 py-4 backdrop-blur-xl">
            <div className="flex items-center gap-3 lg:hidden">
              <Clock className="h-5 w-5 text-cyan-400" />
              <span className="font-semibold text-white">Cronos</span>
            </div>

            {onRefresh && (
              <button
                type="button"
                onClick={onRefresh}
                disabled={refreshing}
                className="ml-auto inline-flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-300 transition hover:border-slate-600 hover:text-white disabled:opacity-60"
              >
                <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                Yenile
              </button>
            )}
          </header>

          <main className="flex-1 px-6 py-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  )
}
