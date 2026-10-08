import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { Building2 } from 'lucide-react';

export const AuthLayout = () => {
  const { isAuthenticated } = useAuthStore();
  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  return (
    <div className="min-h-screen bg-bg flex">
      {/* Left branding panel */}
      <div
        className="hidden lg:flex lg:w-[45%] flex-col justify-between p-12 text-white relative overflow-hidden"
        style={{ background: 'linear-gradient(150deg, #3B6FF5 0%, #2454D8 100%)' }}
      >
        {/* decorative circles */}
        <div className="absolute -right-20 -top-20 w-72 h-72 bg-white/10 rounded-full" />
        <div className="absolute -left-10 bottom-32 w-52 h-52 bg-white/5 rounded-full" />
        <div className="absolute right-10 bottom-10 w-36 h-36 bg-primary-dark/40 rounded-full" />

        {/* Logo */}
        <div className="relative z-10 flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-white/20 backdrop-blur-sm flex items-center justify-center">
            <Building2 className="h-5 w-5 text-white" strokeWidth={1.8} />
          </div>
          <span className="text-2xl font-bold tracking-tight">TransMoney</span>
        </div>

        {/* Center copy */}
        <div className="relative z-10">
          <h2 className="text-4xl font-bold leading-tight mb-4">
            Banking made <br />simple & secure.
          </h2>
          <p className="text-white/75 text-base leading-relaxed max-w-sm">
            Transfer money, track transactions, and manage your accounts — all in one beautiful platform.
          </p>
        </div>

        {/* Stats row */}
        <div className="relative z-10 flex gap-8">
          {[['256-bit', 'Encryption'], ['99.9%', 'Uptime'], ['24/7', 'Support']].map(([val, label]) => (
            <div key={label}>
              <p className="text-2xl font-bold">{val}</p>
              <p className="text-white/65 text-sm">{label}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Right form panel */}
      <div className="flex-1 flex flex-col items-center justify-center px-6 py-12">
        {/* Mobile logo */}
        <div className="lg:hidden flex items-center gap-2 mb-8">
          <div className="w-9 h-9 rounded-xl bg-primary flex items-center justify-center">
            <Building2 className="h-5 w-5 text-white" strokeWidth={1.8} />
          </div>
          <span className="text-xl font-bold text-navy">TransMoney</span>
        </div>

        <div className="w-full max-w-[400px]">
          <Outlet />
        </div>
      </div>
    </div>
  );
};
