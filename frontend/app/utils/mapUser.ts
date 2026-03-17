import type { PlanType } from '~/stores/auth'

export interface ApiUserResponse {
  id: string
  name: string
  email: string
  plan?: string
  isPro?: boolean
  isElite?: boolean
  planExpiresAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export function mapApiUserToStoreUser(data: ApiUserResponse) {
  return {
    id: data.id,
    name: data.name,
    email: data.email,
    plan: (data.plan ?? 'FREE') as PlanType,
    isPro: data.isPro ?? false,
    isElite: data.isElite ?? false,
    planExpiresAt: data.planExpiresAt ?? null,
    createdAt: data.createdAt ? new Date(data.createdAt) : undefined,
    updatedAt: data.updatedAt ? new Date(data.updatedAt) : undefined,
  }
}
