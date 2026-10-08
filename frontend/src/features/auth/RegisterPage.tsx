import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { Eye, EyeOff } from 'lucide-react';

const registerSchema = z
  .object({
    email: z.string().min(1, 'Email is required').email('Invalid email format'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    confirmPassword: z.string().min(1, 'Confirm your password'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  });

type RegisterForm = z.infer<typeof registerSchema>;

export const RegisterPage = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterForm) => {
    setApiError(null);
    try {
      await authApi.register({ email: data.email, password: data.password });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      if (err.response?.status === 409 || err.response?.status === 400) {
        setApiError(err.response?.data?.message || 'Registration failed. The email might already exist.');
      } else {
        setApiError('Something went wrong. Please try again later.');
      }
    }
  };

  if (success) {
    return (
      <div className="text-center py-8">
        <h3 className="text-2xl font-bold text-green-success mb-3">Registration Successful</h3>
        <p className="text-body font-medium">You can now sign in. Redirecting to login...</p>
      </div>
    );
  }

  return (
    <div>
      <div className="text-center mb-8">
        <h3 className="text-2xl font-bold text-navy mb-2">Create an account</h3>
        <p className="text-body text-sm">Join TransMoney to manage your finances</p>
      </div>
      
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <div>
          <label className="block text-sm font-semibold text-dark mb-1.5">Email address</label>
          <input
            {...register('email')}
            type="email"
            placeholder="name@example.com"
            className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
              errors.email ? 'border-error' : 'border-border'
            }`}
          />
          {errors.email && <p className="mt-1.5 text-xs text-error font-medium">{errors.email.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-semibold text-dark mb-1.5">Password</label>
          <div className="relative">
            <input
              {...register('password')}
              type={showPassword ? 'text' : 'password'}
              placeholder="Create a password"
              className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                errors.password ? 'border-error' : 'border-border'
              }`}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-muted hover:text-dark transition-colors"
            >
              {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
          {errors.password && <p className="mt-1.5 text-xs text-error font-medium">{errors.password.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-semibold text-dark mb-1.5">Confirm Password</label>
          <input
            {...register('confirmPassword')}
            type={showPassword ? 'text' : 'password'}
            placeholder="Confirm your password"
            className={`block w-full px-4 py-3 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
              errors.confirmPassword ? 'border-error' : 'border-border'
            }`}
          />
          {errors.confirmPassword && <p className="mt-1.5 text-xs text-error font-medium">{errors.confirmPassword.message}</p>}
        </div>

        {apiError && (
          <div className="p-3.5 text-sm text-error bg-red-50 rounded-btn border border-red-100 font-medium">
            {apiError}
          </div>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full py-3 px-4 rounded-btn text-sm font-semibold text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 transition-all duration-150 shadow-sm mt-2"
        >
          {isSubmitting ? 'Creating account...' : 'Create account'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-body">
        Already have an account?{' '}
        <Link to="/login" className="font-semibold text-primary hover:text-primary-dark transition-colors">
          Sign in
        </Link>
      </p>
    </div>
  );
};
