import { useState, useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/client';
import { transactionApi } from '../../api/transactionApi';
import { generateUUID } from '../../utils/uuid';
import { ArrowRight, CheckCircle, AlertCircle } from 'lucide-react';

const transferSchema = z.object({
  receiverAccountNumber: z.string().min(12, 'Must be exactly 12 digits').max(12, 'Must be exactly 12 digits').regex(/^\d+$/, 'Must contain only digits'),
  amount: z.number().positive('Amount must be positive').min(1, 'Minimum amount is Rs. 1'),
  description: z.string().max(100, 'Description too long').optional(),
});

type TransferForm = z.infer<typeof transferSchema>;

export const TransferPage = () => {
  const [account, setAccount] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [apiError, setApiError] = useState<string | null>(null);
  const [confirmationStep, setConfirmationStep] = useState(false);
  const [formData, setFormData] = useState<TransferForm | null>(null);
  
  // Idempotency key must remain the same for retries
  const idempotencyKey = useRef(generateUUID());
  
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    trigger,
    getValues,
    formState: { errors, isSubmitting },
  } = useForm<TransferForm>({
    resolver: zodResolver(transferSchema),
  });

  useEffect(() => {
    fetchAccount();
  }, []);

  const fetchAccount = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get('/api/v1/accounts/me');
      setAccount(response.data);
    } catch (err: any) {
      setApiError('Could not fetch your account details. Please ensure you have an active account.');
    } finally {
      setLoading(false);
    }
  };

  const onReview = async () => {
    const isValid = await trigger();
    if (isValid) {
      setFormData(getValues());
      setConfirmationStep(true);
    }
  };

  const onSubmitTransfer = async () => {
    if (!formData || !account) return;
    setApiError(null);
    try {
      const response = await transactionApi.transfer({
        senderAccountNumber: account.accountNumber,
        receiverAccountNumber: formData.receiverAccountNumber,
        amount: formData.amount,
        description: formData.description,
      }, idempotencyKey.current);
      
      // Navigate to transaction success or detail page
      navigate(`/transactions/${response.id}`);
    } catch (err: any) {
      setApiError(err.response?.data?.message || err.message || 'Transfer failed. Please try again.');
    }
  };

  if (loading) {
    return <div className="animate-pulse space-y-4">
      <div className="h-32 bg-border rounded-card"></div>
    </div>;
  }

  if (!account) {
    return (
      <div className="text-center py-12">
        <h2 className="text-xl font-bold text-navy mb-2">No Account Found</h2>
        <p className="text-body">You need an active account to transfer money.</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <h1 className="text-3xl font-bold text-navy tracking-tight">Transfer Money</h1>

      <div className="card p-8">
        <div className="mb-8 bg-bg rounded-card p-5 border border-border flex justify-between items-center">
          <div>
            <p className="text-sm font-semibold text-muted mb-1">From Account</p>
            <p className="font-medium text-navy">{account.accountNumber.replace(/(.{4})/g, '$1 ').trim()}</p>
          </div>
          <div className="text-right">
            <p className="text-sm font-semibold text-muted mb-1">Available Balance</p>
            <p className="font-bold text-primary">
              Rs. {account.balance.toLocaleString('en-PK', { minimumFractionDigits: 2 })}
            </p>
          </div>
        </div>

        {!confirmationStep ? (
          <form className="space-y-6">
            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Send To (Account Number)</label>
              <input
                {...register('receiverAccountNumber')}
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.receiverAccountNumber ? 'border-error' : 'border-border'
                }`}
                placeholder="0000 0000 0000"
                maxLength={12}
              />
              {errors.receiverAccountNumber && <p className="mt-1.5 text-xs text-error font-medium">{errors.receiverAccountNumber.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Amount (Rs)</label>
              <input
                type="number"
                {...register('amount', { valueAsNumber: true })}
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all text-lg ${
                  errors.amount ? 'border-error' : 'border-border'
                }`}
                placeholder="0.00"
              />
              {errors.amount && <p className="mt-1.5 text-xs text-error font-medium">{errors.amount.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Description (Optional)</label>
              <input
                {...register('description')}
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.description ? 'border-error' : 'border-border'
                }`}
                placeholder="e.g. Rent Payment"
              />
              {errors.description && <p className="mt-1.5 text-xs text-error font-medium">{errors.description.message}</p>}
            </div>

            <button
              type="button"
              onClick={onReview}
              className="w-full py-3 px-4 rounded-btn text-sm font-semibold text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 transition-all duration-150 shadow-sm flex items-center justify-center gap-2"
            >
              Review Transfer <ArrowRight className="h-5 w-5" />
            </button>
          </form>
        ) : (
          <div className="space-y-6">
            <div className="bg-bg p-6 border border-border rounded-card">
              <h3 className="text-lg font-medium text-navy mb-4">Confirm Transfer</h3>
              
              <dl className="space-y-4 text-sm">
                <div className="flex justify-between">
                  <dt className="text-muted">Receiver Account</dt>
                  <dd className="font-medium text-navy">{formData?.receiverAccountNumber}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-muted">Amount</dt>
                  <dd className="font-bold text-navy text-lg">Rs. {formData?.amount.toLocaleString('en-PK', { minimumFractionDigits: 2 })}</dd>
                </div>
                {formData?.description && (
                  <div className="flex justify-between">
                    <dt className="text-muted">Description</dt>
                    <dd className="font-medium text-navy">{formData.description}</dd>
                  </div>
                )}
              </dl>
            </div>

            {apiError && (
              <div className="p-3.5 text-sm text-error bg-red-50 rounded-btn border border-red-100 font-medium flex items-start gap-2">
                <AlertCircle className="h-5 w-5 flex-shrink-0" />
                <p>{apiError}</p>
              </div>
            )}

            <div className="flex gap-4">
              <button
                type="button"
                onClick={() => setConfirmationStep(false)}
                disabled={isSubmitting}
                className="flex-1 py-3 px-4 border border-border rounded-btn text-sm font-semibold text-dark bg-white hover:bg-bg transition-colors disabled:opacity-50"
              >
                Back to Edit
              </button>
              <button
                type="button"
                onClick={handleSubmit(onSubmitTransfer)}
                disabled={isSubmitting}
                className="flex-1 py-3 px-4 rounded-btn text-sm font-semibold text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 transition-all duration-150 shadow-sm flex items-center justify-center gap-2"
              >
                {isSubmitting ? 'Processing...' : (
                  <>Confirm & Send <CheckCircle className="h-5 w-5" /></>
                )}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
