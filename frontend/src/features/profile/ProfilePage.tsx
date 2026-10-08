import { useState, useEffect } from 'react';
import { useAuthStore } from '../../store/useAuthStore';
import { apiClient } from '../../api/client';
import { User, Mail, Phone, Hash, Activity, CreditCard, Shield } from 'lucide-react';

export const ProfilePage = () => {
  const { user } = useAuthStore();
  const [account, setAccount] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAccount = async () => {
      try {
        const response = await apiClient.get('/api/v1/accounts/me');
        setAccount(response.data);
      } catch (err) {
        // user might not have an account yet
        console.error("No account found");
      } finally {
        setLoading(false);
      }
    };
    fetchAccount();
  }, []);

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <h1 className="text-3xl font-bold text-navy tracking-tight">Your Profile</h1>

      {/* Security Info Card */}
      <div className="card overflow-hidden">
        <div className="px-7 py-5 border-b border-border bg-bg flex items-center gap-2">
          <User className="h-5 w-5 text-muted" />
          <h3 className="text-base font-bold text-navy">Authentication Details</h3>
        </div>
        <div className="p-7">
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-6">
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-muted flex items-center gap-2">
                <Mail className="h-4 w-4" /> Email
              </dt>
              <dd className="mt-1.5 text-sm font-medium text-navy">{user?.email}</dd>
            </div>
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-muted flex items-center gap-2">
                <Shield className="h-4 w-4" /> Role
              </dt>
              <dd className="mt-1.5 text-sm font-medium text-navy uppercase">{user?.role || 'CUSTOMER'}</dd>
            </div>
          </dl>
        </div>
      </div>

      {/* Banking Account Card */}
      <div className="card overflow-hidden">
        <div className="px-7 py-5 border-b border-border bg-bg flex items-center gap-2">
          <CreditCard className="h-5 w-5 text-muted" />
          <h3 className="text-base font-bold text-navy">Banking Account Details</h3>
        </div>
        
        <div className="p-7">
          {loading ? (
            <div className="animate-pulse space-y-4">
              <div className="h-4 bg-border rounded w-1/4"></div>
              <div className="h-4 bg-border rounded w-1/2"></div>
            </div>
          ) : account ? (
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-6">
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">
                  <User className="h-4 w-4" /> Account Holder
                </dt>
                <dd className="mt-1.5 text-sm font-medium text-navy">{account.accountHolderName}</dd>
              </div>
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">
                  <Hash className="h-4 w-4" /> Account Number
                </dt>
                <dd className="mt-1.5 text-sm font-medium text-navy font-mono">{account.accountNumber.replace(/(.{4})/g, '$1 ').trim()}</dd>
              </div>
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">
                  <Phone className="h-4 w-4" /> Phone Number
                </dt>
                <dd className="mt-1.5 text-sm font-medium text-navy">{account.phone}</dd>
              </div>
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">
                  <Activity className="h-4 w-4" /> Status
                </dt>
                <dd className="mt-1.5 text-sm font-medium text-navy">
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${
                    account.accountStatus === 'ACTIVE' ? 'bg-green-light text-green-success' : 'bg-red-50 text-error'
                  }`}>
                    {account.accountStatus}
                  </span>
                </dd>
              </div>
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">Account Type</dt>
                <dd className="mt-1.5 text-sm font-medium text-navy capitalize">{account.accountType.toLowerCase()}</dd>
              </div>
              <div className="sm:col-span-1">
                <dt className="text-sm font-medium text-muted flex items-center gap-2">Daily Limit</dt>
                <dd className="mt-1.5 text-sm font-medium text-navy">Rs. {account.dailyTransactionLimit?.toLocaleString()}</dd>
              </div>
            </dl>
          ) : (
            <div className="text-center py-8 text-muted text-sm">
              No banking account found associated with this email.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
