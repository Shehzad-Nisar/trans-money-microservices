import { apiClient } from './client';
import type { TransferRequest, TransactionResponse } from '../types/transaction';

export const transactionApi = {
  transfer: async (data: TransferRequest, idempotencyKey: string) => {
    const response = await apiClient.post<TransactionResponse>(
      '/api/v1/transactions/transfer',
      data,
      {
        headers: {
          'Idempotency-Key': idempotencyKey,
        },
      }
    );
    return response.data;
  },

  getTransaction: async (transactionId: string) => {
    const response = await apiClient.get<TransactionResponse>(`/api/v1/transactions/${transactionId}`);
    return response.data;
  },

  getTransactionHistory: async (accountNumber: string) => {
    const response = await apiClient.get<TransactionResponse[]>(`/api/v1/transactions/account/${accountNumber}`);
    return response.data;
  },
};
