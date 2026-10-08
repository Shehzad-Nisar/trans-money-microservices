import { Bell, ShieldAlert, CheckCircle, RefreshCcw } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';

// MOCK DATA: The backend notification-service currently only consumes Kafka events
// and logs them (simulating email/SMS). It does not expose a REST API or persist them.
// As per instructions, we build the UI ready to be connected later.
const MOCK_NOTIFICATIONS = [
  {
    id: 'n1',
    type: 'TRANSACTION_COMPLETED',
    title: 'Transfer Successful',
    message: 'Your transfer of Rs. 5,000 to account ending in 1234 was completed.',
    isRead: false,
    createdAt: new Date().toISOString(),
    transactionId: 'mock-tx-1'
  },
  {
    id: 'n2',
    type: 'OTP_REQUIRED',
    title: 'Verification Required',
    message: 'Suspicious activity detected. Please verify your recent transfer of Rs. 50,000.',
    isRead: false,
    createdAt: new Date(Date.now() - 3600000).toISOString(),
    transactionId: 'mock-tx-2'
  },
  {
    id: 'n3',
    type: 'FRAUD_DETECTED',
    title: 'Account Alert',
    message: 'Your account was flagged for suspicious activity and has been temporarily restricted.',
    isRead: true,
    createdAt: new Date(Date.now() - 86400000).toISOString(),
  },
];

export const NotificationsPage = () => {
  const [notifications, setNotifications] = useState(MOCK_NOTIFICATIONS);

  const markAsRead = (id: string) => {
    setNotifications(notifications.map(n => 
      n.id === id ? { ...n, isRead: true } : n
    ));
  };

  const markAllAsRead = () => {
    setNotifications(notifications.map(n => ({ ...n, isRead: true })));
  };

  const getIconData = (type: string) => {
    switch(type) {
      case 'TRANSACTION_COMPLETED': return { icon: <CheckCircle className="h-6 w-6 text-green-success" />, bg: 'bg-green-light' };
      case 'FRAUD_DETECTED': return { icon: <ShieldAlert className="h-6 w-6 text-error" />, bg: 'bg-red-50' };
      case 'OTP_REQUIRED': return { icon: <ShieldAlert className="h-6 w-6 text-orange" />, bg: 'bg-orange-light' };
      case 'REFUND_PROCESSED': return { icon: <RefreshCcw className="h-6 w-6 text-primary" />, bg: 'bg-blueLt' };
      default: return { icon: <Bell className="h-6 w-6 text-muted" />, bg: 'bg-bg' };
    }
  };

  if (notifications.length === 0) {
    return (
      <div className="card max-w-3xl mx-auto py-16 text-center">
        <div className="bg-blueLt rounded-full p-4 mx-auto w-fit mb-4">
          <Bell className="h-12 w-12 text-primary" />
        </div>
        <h2 className="text-xl font-bold text-navy mb-2">You're all caught up</h2>
        <p className="text-body">No new notifications to show right now.</p>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-navy tracking-tight">Notifications</h1>
        <button 
          onClick={markAllAsRead}
          className="text-sm font-semibold text-primary hover:text-primary-dark transition-colors"
        >
          Mark all as read
        </button>
      </div>

      <div className="card overflow-hidden">
        <div className="p-4 bg-blueLt border-b border-primary-light text-sm text-primary font-medium">
          <strong>Development Note:</strong> The backend <code>notification-service</code> currently logs alerts to the console. This UI uses mock data until a persistence API is added to the backend.
        </div>
        <ul className="divide-y divide-border">
          {notifications.map((notification) => {
            const { icon, bg } = getIconData(notification.type);
            return (
              <li 
                key={notification.id} 
                className={`p-6 hover:bg-bg transition-colors cursor-pointer ${notification.isRead ? 'opacity-60' : ''}`}
                onClick={() => markAsRead(notification.id)}
              >
                <div className="flex items-start">
                  <div className="flex-shrink-0 mr-4">
                    <div className={`p-2.5 rounded-full ${bg}`}>
                      {icon}
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className={`text-sm ${notification.isRead ? 'text-muted font-semibold' : 'text-navy font-semibold'}`}>
                      {notification.title}
                    </p>
                    <p className="text-sm text-body mt-1">
                      {notification.message}
                    </p>
                    <div className="mt-2 flex items-center justify-between">
                      <span className="text-xs text-muted">{new Date(notification.createdAt).toLocaleString()}</span>
                      {notification.transactionId && (
                        <Link 
                          to={`/transactions/${notification.transactionId}`}
                          className="text-xs font-semibold text-primary hover:text-primary-dark z-10 relative"
                          onClick={(e) => e.stopPropagation()}
                        >
                          View Transaction
                        </Link>
                      )}
                    </div>
                  </div>
                  {!notification.isRead && (
                    <div className="flex-shrink-0 ml-4">
                      <span className="inline-block h-2.5 w-2.5 rounded-full bg-primary"></span>
                    </div>
                  )}
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
};
