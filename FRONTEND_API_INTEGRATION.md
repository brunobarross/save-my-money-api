# Front-End API Integration Guide

Welcome to the **Save My Money API** integration guide for front-end developers. This document provides technical specifications, schema definitions, authentication guidelines, endpoint documentation, and code examples for integrating web or mobile applications with the Java Spring Boot backend.

---

## 1. Overview & Core Specifications

### Base URLs
- **Local Development**: `http://localhost:8080`
- **Docker Compose**: `http://localhost:8080`

### Content Type & Data Formats
- **Request & Response Format**: `application/json`
- **Date Format**: ISO 8601 date format `YYYY-MM-DD` (e.g., `"2026-08-05"`)
- **Currency / Amounts**: Numbers or Decimal Strings representing `BigDecimal` (e.g., `150.50`)
- **Identifiers**: `UUID` v4 formatted string (e.g., `"3fa85f64-5717-4562-b3fc-2c963f66afa6"`)

### CORS Configuration
The backend explicitly enables CORS for the following origins:
- `http://localhost:9000`
- `https://finance-dashboard-amber-six.vercel.app`

Allowed HTTP Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `PATCH`  
Allowed Headers: `Authorization`, `Content-Type`, `Accept`, `X-Requested-With`

---

## 2. Authentication & Authorization

The API uses **JWT (JSON Web Token)** authentication based on RSA key pair signing.

### Headers Required for Authenticated Endpoints
Except for registration (`POST /api/users`) and login (`POST /api/authenticate`), all requests require the `Authorization` header:

```http
Authorization: Bearer <your_access_token>
```

### Token Lifecycle & Expiry
- **Expiration**: Token is valid for 180,000 seconds (~50 hours).
- **Roles / Claims**: Encoded in the token `roles` claim (`ROLE_USER` or `ROLE_ADMIN`).

---

## 3. TypeScript Type Definitions

Copy these TypeScript interfaces into your front-end codebase (`src/types/api.ts`):

```typescript
// Roles
export type RoleName = 'ROLE_ADMIN' | 'ROLE_USER';

// Enums
export type TransactionType = 'INCOME' | 'EXPENSE';

/**
 * IconType serializes to Material Design Icon string names in JSON responses.
 */
export type IconType =
  | 'account_balance_wallet' // WALLET
  | 'credit_card'             // CARD
  | 'account_balance'         // BANK
  | 'payments'                // PAYMENTS
  | 'savings';                // SAVINGS

// DTOs
export interface LoginRequestDTO {
  username: string;
  password: string;
}

export interface LoginResponseDTO {
  user: {
    username: string;
    authorities: Array<{ authority: string }>;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
    enabled: boolean;
  };
  accessToken: string;
}

export interface UserDTO {
  id?: string; // UUID
  name: string;
  password?: string;
  role?: RoleName;
}

export interface WalletDTO {
  id?: string; // UUID
  name: string;
  color: string;
  userId?: string; // UUID
  icon: IconType;
  amount?: number; // Calculated dynamic balance
}

export interface TransactionDTO {
  id?: string; // UUID
  name: string;
  value: number;
  description?: string;
  date: string; // Format: "YYYY-MM-DD"
  installment?: string; // e.g. "1/12" or "Single"
  type: TransactionType;
  walletId?: string; // UUID
  userId?: string; // UUID
  recurrenceGroupId?: string; // UUID (groups multi-installment transactions)
  installmentCount?: number; // Used in POST requests to generate N monthly installments
}

export interface TransactionsPageDTO {
  transactions: TransactionDTO[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface FinancialSummaryDTO {
  totalReceipts: number;
  totalExpenses: number;
  currentBalance: number;
}
```

---

## 4. API Endpoints Reference

### 4.1 Authentication (`/api/authenticate`)

#### Log In User
`POST /api/authenticate`
- **Auth Required**: No
- **Request Body**: `LoginRequestDTO`
```json
{
  "username": "john_doe",
  "password": "securepassword"
}
```
- **Response**: `200 OK` -> `LoginResponseDTO`
```json
{
  "user": {
    "username": "john_doe",
    "enabled": true
  },
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
- **Error Response**: `401 Unauthorized` (`"Usuário ou senha incorretos!"`)

---

### 4.2 User Management (`/api/users`)

#### Register New User
`POST /api/users`
- **Auth Required**: No
- **Request Body**: `UserDTO`
```json
{
  "name": "john_doe",
  "password": "securepassword",
  "role": "ROLE_USER"
}
```
- **Response**: `200 OK` -> `UserDTO`

#### Get Current Logged-in User
`GET /api/users/me`
- **Auth Required**: Yes (`ROLE_USER` or `ROLE_ADMIN`)
- **Response**: `200 OK` -> `UserDTO`
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "john_doe",
  "role": "ROLE_USER"
}
```

#### Get All Users (Admin Only)
`GET /api/users`
- **Auth Required**: Yes (`ROLE_ADMIN`)
- **Response**: `200 OK` -> `Array<UserDTO>`

---

### 4.3 Wallets (`/api/wallets`)

> **Note on Wallet Balance**: When calling `GET /api/wallets`, passing `month` and `year` dynamically populates the `amount` field with calculated balances for that specific period.

#### Get All Wallets
`GET /api/wallets`
- **Auth Required**: Yes (`ROLE_USER` or `ROLE_ADMIN`)
- **Query Parameters**:
  - `month` *(optional, integer)*: Month number (1-12)
  - `year` *(optional, integer)*: Year (e.g., 2026)
- **Response**: `200 OK` -> `Array<WalletDTO>`
```json
[
  {
    "id": "b1a2c3d4-5678-90ab-cdef-1234567890ab",
    "name": "Main Bank Account",
    "color": "#4F46E5",
    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "icon": "account_balance",
    "amount": 2540.75
  }
]
```

#### Get Wallet by ID
`GET /api/wallets/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Response**: `200 OK` -> `WalletDTO`

#### Create Wallet
`POST /api/wallets`
- **Auth Required**: Yes
- **Request Body**: `WalletDTO`
```json
{
  "name": "Savings Account",
  "color": "#10B981",
  "icon": "savings"
}
```
- **Response**: `201 Created` -> `WalletDTO`

#### Update Wallet
`PUT /api/wallets/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Request Body**: `WalletDTO`
```json
{
  "name": "Primary Checking Account",
  "color": "#3B82F6",
  "icon": "account_balance_wallet"
}
```
- **Response**: `200 OK` -> `WalletDTO`

#### Delete Wallet
`DELETE /api/wallets/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Response**: `204 No Content`

---

### 4.4 Transactions (`/api/transactions`)

#### Get Transactions (Paginated & Filtered)
`GET /api/transactions`
- **Auth Required**: Yes (`ROLE_USER` or `ROLE_ADMIN`)
- **Query Parameters**:
  - `walletId` *(optional, UUID)*: Filter by target wallet
  - `month` *(optional, integer)*: Filter by month (1-12)
  - `year` *(optional, integer)*: Filter by year (e.g., 2026)
  - `page` *(optional, integer, default: 0)*: Zero-based page index
  - `pageSize` *(optional, integer, default: 10)*: Number of items per page
- **Response**: `200 OK` -> `TransactionsPageDTO`
```json
{
  "transactions": [
    {
      "id": "e9f8g7h6-5432-10fe-dcba-9876543210fe",
      "name": "Supermarket Purchase",
      "value": 125.40,
      "description": "Weekly grocery shopping",
      "date": "2026-08-05",
      "installment": "1/12",
      "type": "EXPENSE",
      "walletId": "b1a2c3d4-5678-90ab-cdef-1234567890ab",
      "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "recurrenceGroupId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Transaction by ID
`GET /api/transactions/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Response**: `200 OK` -> `TransactionDTO`

#### Get Financial Summary
`GET /api/transactions/summary`
- **Auth Required**: Yes (`ROLE_USER` or `ROLE_ADMIN`)
- **Query Parameters**:
  - `month` *(required, integer)*: e.g., `8`
  - `year` *(required, integer)*: e.g., `2026`
- **Response**: `200 OK` -> `FinancialSummaryDTO`
```json
{
  "totalReceipts": 5000.00,
  "totalExpenses": 1459.20,
  "currentBalance": 3540.80
}
```

#### Create Transaction (Single or Multi-Installments)
`POST /api/transactions`
- **Auth Required**: Yes
- **Request Body**: `TransactionDTO`
  - To create **multi-installment transactions** (e.g. 12x), send `"installmentCount": 12`. The backend automatically generates 12 monthly physical records sharing the same `recurrenceGroupId` with incremented dates and formatted `installment` strings (`"1/12"`, `"2/12"`, ..., `"12/12"`).
  - For single transactions, omit `installmentCount` or set it to `null`/`1`.
```json
{
  "name": "TV 4K",
  "value": 200.00,
  "description": "Electronics purchase",
  "date": "2026-08-10",
  "installmentCount": 12,
  "type": "EXPENSE",
  "walletId": "b1a2c3d4-5678-90ab-cdef-1234567890ab"
}
```
- **Response**: `201 Created` -> `TransactionDTO` (returns the 1st installment DTO created). Note: `installmentCount` is omitted in response payloads (`@JsonInclude(NON_NULL)`).

#### Update Transaction
`PUT /api/transactions/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Request Body**: `TransactionDTO`
- **Response**: `200 OK` -> `TransactionDTO`

#### Delete Transaction
`DELETE /api/transactions/{id}`
- **Auth Required**: Yes
- **Path Parameter**: `id` *(UUID)*
- **Response**: `204 No Content`

---

## 5. Error Handling Reference

| HTTP Status | Trigger Condition | Response Body Example |
| :--- | :--- | :--- |
| `400 Bad Request` | Invalid input or path variable validation failure | `{"timestamp": "...", "status": 400, "error": "Bad Request"}` |
| `401 Unauthorized` | Invalid credentials / Expired JWT token | `"Usuário ou senha incorretos!"` |
| `403 Forbidden` | Accessing resource owned by another user or insufficient role permissions | `"Access denied: You do not own this wallet"` |
| `404 Not Found` | Entity with given UUID does not exist | `"Wallet with id 3fa85f64-5717-4562-b3fc-2c963f66afa6 not found"` |

---

## 6. Front-End Integration Snippets (Axios & React)

### 6.1 Axios Client Setup (`src/services/api.ts`)

```typescript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response Interceptor: Handle Unauthorized Error
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      // Redirect to login page if needed
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

### 6.2 Service Implementation Example (`src/services/financialService.ts`)

```typescript
import { api } from './api';
import {
  LoginRequestDTO,
  LoginResponseDTO,
  UserDTO,
  WalletDTO,
  TransactionDTO,
  TransactionsPageDTO,
  FinancialSummaryDTO,
} from '../types/api';

export const AuthService = {
  login: async (credentials: LoginRequestDTO): Promise<LoginResponseDTO> => {
    const { data } = await api.post<LoginResponseDTO>('/api/authenticate', credentials);
    if (data.accessToken) {
      localStorage.setItem('token', data.accessToken);
    }
    return data;
  },

  register: async (userData: UserDTO): Promise<UserDTO> => {
    const { data } = await api.post<UserDTO>('/api/users', userData);
    return data;
  },

  getCurrentUser: async (): Promise<UserDTO> => {
    const { data } = await api.get<UserDTO>('/api/users/me');
    return data;
  },
};

export const WalletService = {
  getWallets: async (month?: number, year?: number): Promise<WalletDTO[]> => {
    const { data } = await api.get<WalletDTO[]>('/api/wallets', {
      params: { month, year },
    });
    return data;
  },

  createWallet: async (wallet: WalletDTO): Promise<WalletDTO> => {
    const { data } = await api.post<WalletDTO>('/api/wallets', wallet);
    return data;
  },

  updateWallet: async (id: string, wallet: WalletDTO): Promise<WalletDTO> => {
    const { data } = await api.put<WalletDTO>(`/api/wallets/${id}`, wallet);
    return data;
  },

  deleteWallet: async (id: string): Promise<void> => {
    await api.delete(`/api/wallets/${id}`);
  },
};

export const TransactionService = {
  getTransactions: async (params?: {
    walletId?: string;
    month?: number;
    year?: number;
    page?: number;
    pageSize?: number;
  }): Promise<TransactionsPageDTO> => {
    const { data } = await api.get<TransactionsPageDTO>('/api/transactions', { params });
    return data;
  },

  getSummary: async (month: number, year: number): Promise<FinancialSummaryDTO> => {
    const { data } = await api.get<FinancialSummaryDTO>('/api/transactions/summary', {
      params: { month, year },
    });
    return data;
  },

  createTransaction: async (transaction: TransactionDTO): Promise<TransactionDTO> => {
    const { data } = await api.post<TransactionDTO>('/api/transactions', transaction);
    return data;
  },

  deleteTransaction: async (id: string): Promise<void> => {
    await api.delete(`/api/transactions/${id}`);
  },
};
```

---

## 7. Checklist for Front-End Developers

- [ ] Store `accessToken` securely (e.g. `localStorage` or `sessionStorage`).
- [ ] Configure `Authorization: Bearer <token>` in all API calls after login.
- [ ] Ensure dates sent to `POST /api/transactions` format as `"YYYY-MM-DD"`.
- [ ] Handle `401 Unauthorized` responses by resetting the session token and redirecting to Login.
- [ ] Use `IconType` strings matching Material Icons when creating or updating wallets.
