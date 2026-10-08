import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/client';

const createAccountSchema = z.object({
  accountHolderName: z.string().min(2, 'Name must be at least 2 characters'),
  phone: z.string().min(10, 'Valid phone number is required'),
  accountType: z.enum(['CURRENT', 'SAVING'], { required_error: 'Please select an account type' }),
  initialDeposit: z.number().min(500, 'Minimum initial deposit is Rs. 500'),
});

type CreateAccountForm = z.infer<typeof createAccountSchema>;

export const CreateAccountPage = () => {
  const [apiError, setApiError] = useState<string | null>(null);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<CreateAccountForm>({
    resolver: zodResolver(createAccountSchema),
    defaultValues: { accountType: 'CURRENT', initialDeposit: 1000 },
  });

  const onSubmit = async (data: CreateAccountForm) => {
    setApiError(null);
    try {
      await apiClient.post('/api/v1/accounts', data);
      navigate('/dashboard');
    } catch (err: any) {
      setApiError(err.response?.data?.message || err.message || 'Failed to create account.');
    }
  };

  return (
    <div className="max-w-2xl mx-auto py-4">
      <div className="card p-8">
        <h2 className="text-2xl font-bold text-navy mb-2">Open your TransMoney Account</h2>
        <p className="text-body text-sm mb-8">Please provide your details to open a new digital banking account.</p>
        
        {apiError && (
          <div className="mb-6 p-3.5 text-sm text-error bg-red-50 rounded-btn border border-red-100 font-medium">
            {apiError}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Full Name</label>
              <input
                type="text"
                placeholder="John Doe"
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.accountHolderName ? 'border-error' : 'border-border'
                }`}
                {...register('accountHolderName')}
              />
              {errors.accountHolderName && (
                <p className="mt-1.5 text-xs text-error font-medium">{errors.accountHolderName.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Phone Number</label>
              <input
                type="text"
                placeholder="+1 234 567 890"
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.phone ? 'border-error' : 'border-border'
                }`}
                {...register('phone')}
              />
              {errors.phone && (
                <p className="mt-1.5 text-xs text-error font-medium">{errors.phone.message}</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Account Type</label>
              <select
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.accountType ? 'border-error' : 'border-border'
                }`}
                {...register('accountType')}
              >
                <option value="CURRENT">Current Account</option>
                <option value="SAVING">Savings Account</option>
              </select>
              {errors.accountType && (
                <p className="mt-1.5 text-xs text-error font-medium">{errors.accountType.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-semibold text-dark mb-1.5">Initial Deposit (Rs.)</label>
              <input
                type="number"
                placeholder="1000"
                className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                  errors.initialDeposit ? 'border-error' : 'border-border'
                }`}
                {...register('initialDeposit', { valueAsNumber: true })}
              />
              {errors.initialDeposit && (
                <p className="mt-1.5 text-xs text-error font-medium">{errors.initialDeposit.message}</p>
              )}
            </div>
          </div>

          <div className="pt-4 flex justify-end">
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-8 py-3 rounded-btn text-sm font-semibold text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 transition-all duration-150 shadow-sm"
            >
              {isSubmitting ? 'Creating Account...' : 'Open Account'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
