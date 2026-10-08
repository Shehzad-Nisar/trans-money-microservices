import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { apiClient } from '../../api/client';
import { CheckCircle2, ShieldCheck, ArrowLeft } from 'lucide-react';

export const OTPVerificationPage = () => {
  const { transactionId } = useParams<{ transactionId: string }>();
  const [otp, setOtp] = useState('');
  const [verifying, setVerifying] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!transactionId) { navigate('/dashboard'); }
  }, [transactionId, navigate]);

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!transactionId || otp.length < 4) return;
    setVerifying(true);
    setError(null);
    try {
      await apiClient.post(`/api/v1/transactions/verify/${transactionId}?otp=${otp}`);
      setSuccess(true);
      setTimeout(() => { navigate(`/transactions/${transactionId}`); }, 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'OTP Verification failed.');
    } finally {
      setVerifying(false);
    }
  };

  if (success) {
    return (
      <div className="max-w-md mx-auto py-12 text-center">
        <CheckCircle2 className="mx-auto h-16 w-16 text-green-success mb-4" />
        <h2 className="text-2xl font-bold text-navy mb-2">Verification Successful</h2>
        <p className="text-body mb-6">Your transaction is now being processed.</p>
        <p className="text-sm text-muted">Redirecting to transaction details...</p>
      </div>
    );
  }

  return (
    <div className="max-w-md mx-auto space-y-6">
      <Link to={`/transactions/${transactionId}`} className="inline-flex items-center gap-2 text-sm font-semibold text-muted hover:text-navy transition-colors mb-4">
        <ArrowLeft className="h-4 w-4" /> Back to Transaction
      </Link>

      <div className="card p-8 text-center">
        <ShieldCheck className="mx-auto h-12 w-12 text-primary mb-4" />
        <h2 className="text-2xl font-bold text-navy mb-2">Security Verification</h2>
        <p className="text-body mb-8 text-sm">
          Please enter the OTP sent to your registered email to authorize this transaction.
        </p>

        <form onSubmit={handleVerify} className="space-y-6 text-left">
          <div>
            <label className="block text-sm font-semibold text-dark mb-2 text-center">Enter Verification Code</label>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
              maxLength={6}
              className="block w-full px-4 py-4 text-center text-2xl tracking-[0.5em] font-mono border border-border rounded-btn bg-white text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all"
              placeholder="000000"
            />
          </div>

          {error && (
            <div className="p-3.5 text-sm text-error bg-red-50 rounded-btn border border-red-100 font-medium text-center">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={verifying || otp.length < 4}
            className="w-full py-3 px-4 rounded-btn text-sm font-semibold text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 transition-all duration-150 shadow-sm"
          >
            {verifying ? 'Verifying...' : 'Verify Transaction'}
          </button>
        </form>
      </div>
    </div>
  );
};
