import { Navigate, Outlet, Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import {
  LayoutDashboard, Wallet, Send, Receipt, CreditCard,
  BarChart2, Headphones, Settings, LogOut,
  ChevronDown, Building2
} from 'lucide-react';

const navigation = [
  { name: 'Dashboard',   href: '/dashboard',     icon: LayoutDashboard },
  { name: 'Accounts',    href: '/profile',       icon: Wallet },
  { name: 'Transfer',    href: '/transfer',      icon: Send },
  { name: 'Transactions',href: '/transactions',  icon: Receipt },
  { name: 'Cards',       href: '#',              icon: CreditCard },
  { name: 'Investments', href: '/notifications',   icon: BarChart2 },
  { name: 'Support',     href: '#',                icon: Headphones },
  { name: 'Settings',    href: '#',                icon: Settings },
];

export const ProtectedLayout = () => {
  const { isAuthenticated, logout, user } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return (
    <div className="flex min-h-screen bg-bg font-sans">

      {/* ── SIDEBAR ──────────────────────────────────────────── */}
      <aside className="hidden md:flex flex-col fixed inset-y-0 left-0 w-[260px] bg-white border-r border-border z-20">

        {/* Logo */}
        <div className="flex items-center gap-3 px-6 pt-7 pb-6">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-primary-dark flex items-center justify-center shadow-md">
            <Building2 className="h-5 w-5 text-white" strokeWidth={1.8} />
          </div>
          <span className="text-xl font-bold text-navy tracking-tight">TransMoney</span>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-4 space-y-1 overflow-y-auto">
          {navigation.map((item) => {
            const active = location.pathname.startsWith(item.href) && item.href !== '#';
            return (
              <Link
                key={item.name}
                to={item.href}
                className={`nav-item ${active ? 'nav-item-active' : 'nav-item-inactive'}`}
              >
                <item.icon
                  className={`h-[18px] w-[18px] flex-shrink-0 ${active ? 'text-primary' : 'text-[#68758B]'}`}
                  strokeWidth={active ? 2 : 1.8}
                />
                <span>{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* User profile */}
        <div className="px-4 py-5 border-t border-border">
          <div className="flex items-center gap-3 px-3 py-3 rounded-xl hover:bg-[#F3F6FC] cursor-pointer transition-colors">
            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-primary to-primary-dark flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
              {user?.email?.charAt(0).toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-navy truncate">{user?.email?.split('@')[0]}</p>
              <p className="text-xs text-muted">Premium Customer</p>
            </div>
            <ChevronDown className="h-4 w-4 text-muted flex-shrink-0" />
          </div>
          <button
            onClick={logout}
            className="mt-2 w-full flex items-center gap-2 px-3 py-2.5 text-sm text-body hover:text-error hover:bg-red-50 rounded-xl transition-colors"
          >
            <LogOut className="h-4 w-4" />
            Sign out
          </button>
        </div>
      </aside>

      {/* ── MOBILE HEADER ────────────────────────────────────── */}
      <header className="md:hidden fixed top-0 left-0 right-0 bg-white border-b border-border z-10 flex items-center justify-between px-4 py-4">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-primary to-primary-dark flex items-center justify-center">
            <Building2 className="h-4 w-4 text-white" strokeWidth={1.8} />
          </div>
          <span className="text-lg font-bold text-navy">TransMoney</span>
        </div>
        <button onClick={logout} className="p-2 text-body hover:text-error rounded-lg">
          <LogOut className="h-5 w-5" />
        </button>
      </header>

      {/* ── MAIN ─────────────────────────────────────────────── */}
      <main className="flex-1 md:ml-[260px] mt-[60px] md:mt-0 overflow-y-auto">
        <div className="px-6 py-8 md:px-10 md:py-10 max-w-[1280px] mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
