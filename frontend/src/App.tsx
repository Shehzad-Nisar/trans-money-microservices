import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthLayout } from './layouts/AuthLayout';
import { ProtectedLayout } from './layouts/ProtectedLayout';
import { LoginPage } from './features/auth/LoginPage';
import { RegisterPage } from './features/auth/RegisterPage';

import { DashboardPage } from './features/dashboard/DashboardPage';
import { CreateAccountPage } from './features/account/CreateAccountPage';
import { TransferPage } from './features/transfers/TransferPage';
import { TransactionDetailPage } from './features/transactions/TransactionDetailPage';
import { TransactionHistoryPage } from './features/transactions/TransactionHistoryPage';
import { OTPVerificationPage } from './features/transactions/OTPVerificationPage';
import { NotificationsPage } from './features/notifications/NotificationsPage';
import { ProfilePage } from './features/profile/ProfilePage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public / Auth Routes */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Protected Routes */}
        <Route element={<ProtectedLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/create-account" element={<CreateAccountPage />} />
          <Route path="/transfer" element={<TransferPage />} />
          <Route path="/transactions" element={<TransactionHistoryPage />} />
          <Route path="/transactions/:id" element={<TransactionDetailPage />} />
          <Route path="/verify/:transactionId" element={<OTPVerificationPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
