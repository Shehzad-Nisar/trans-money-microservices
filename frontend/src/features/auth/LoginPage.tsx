import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { useAuthStore } from '../../store/useAuthStore';
import { parseJwt } from '../../utils/jwt';
import { Eye, EyeOff } from 'lucide-react';

const loginSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email format'),
  password: z.string().min(1, 'Password is required'),
});

type LoginForm = z.infer<typeof loginSchema>;

export const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginForm) => {
    setApiError(null);
    try {
      const response = await authApi.login(data);
      // Decode JWT to get user info
      const decoded = parseJwt(response.token);
      const user = {
        id: decoded?.sub || decoded?.userId || '',
        email: decoded?.email || data.email,
        firstName: decoded?.firstName || 'User',
        lastName: decoded?.lastName || '',
      };
      
      setAuth(response.token, user);
      navigate('/dashboard');
    } catch (err: any) {
      if (err.response?.status === 401 || err.response?.status === 400) {
        setApiError('Invalid email or password.');
      } else {
        setApiError('Something went wrong. Please try again later.');
      }
    }
  };

  return (
    <div>
      <div className="mb-8">
        <h3 className="text-2xl font-bold text-navy">Sign in</h3>
        <p className="text-body text-sm mt-1">Welcome back! Enter your credentials to continue.</p>
      </div>
      
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <div>
          <label className="block text-sm font-semibold text-dark mb-1.5">Email address</label>
          <input
            {...register('email')}
            type="email"
            placeholder="you@example.com"
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
              placeholder="Enter your password"
              className={`block w-full px-4 py-3 pr-11 border rounded-btn text-sm bg-white placeholder-muted text-navy focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all ${
                errors.password ? 'border-error' : 'border-border'
              }`}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-4 flex items-center text-muted hover:text-body transition-colors"
            >
              {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </button>
          </div>
          {errors.password && <p className="mt-1.5 text-xs text-error font-medium">{errors.password.message}</p>}
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
          {isSubmitting ? 'Signing in...' : 'Sign in'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-body">
        Don't have an account?{' '}
        <Link to="/register" className="font-semibold text-primary hover:text-primary-dark transition-colors">
          Create account
        </Link>
      </p>
    </div>
  );
};
