<script setup lang="ts">
import { formatCurrency } from '~/utils/format'
import type { CreditCard } from '~/types/creditCard'

const props = defineProps<{
  creditCard: CreditCard
  showPrivacy: boolean
}>()

const { openCreditCardBillModal, openEditCreditCardModal } = useDashboard()

const availableLimit = computed(() =>
  (props.creditCard.creditLimit ?? 0) - (props.creditCard.currentBillAmount ?? 0)
)

function getCardColor() {
  return props.creditCard.color ?? '#087f5b'
}
</script>

<template>
  <div
    class="credit-card-card d-flex flex-column pa-4 rounded-xl cursor-pointer"
    role="button"
    tabindex="0"
    :aria-label="`Cartão ${creditCard.name}`"
    :style="{ borderBottomColor: getCardColor() }"
    @click="openCreditCardBillModal(creditCard)"
    @keydown.enter="openCreditCardBillModal(creditCard)"
    @keydown.space.prevent="openCreditCardBillModal(creditCard)"
  >
    <v-btn
      icon
      variant="text"
      density="compact"
      size="small"
      class="credit-card-card__edit-btn"
      aria-label="Editar cartão"
      @click.stop="openEditCreditCardModal(creditCard)"
    >
      <v-icon icon="mdi-pencil-outline" size="18" />
    </v-btn>
    <div class="credit-card-card__icon d-flex align-center justify-center rounded-circle mb-3">
      <v-icon icon="mdi-credit-card-outline" size="24" class="credit-card-card__icon-svg" />
    </div>
    <p class="credit-card-card__name mb-1">
      {{ creditCard.name }}
    </p>
    <p class="credit-card-card__value mb-1">
      {{ showPrivacy ? formatCurrency(creditCard.currentBillAmount ?? 0) : '••••' }}
    </p>
    <p class="credit-card-card__label">
      Fatura atual
    </p>
    <p v-if="showPrivacy" class="credit-card-card__available mt-1 text-caption">
      Limite disponível: {{ formatCurrency(availableLimit) }}
    </p>
    <p v-else class="credit-card-card__available mt-1 text-caption">
      Limite disponível: ••••
    </p>
  </div>

</template>

<style scoped>
.credit-card-card__edit-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #868e96;
  min-width: 32px;
}

.credit-card-card__edit-btn:hover {
  color: #495057;
  background-color: rgba(0, 0, 0, 0.05);
}

.credit-card-card {
  position: relative;
  background-color: white;
  flex-shrink: 0;
  border-bottom: 4px solid transparent;
  color: var(--color-text-primary);
  transition: opacity 0.2s;
  min-height: 140px;
  min-width: 180px;
}

.credit-card-card:hover {
  opacity: 0.95;
}

.credit-card-card__icon {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  background-color: #e9ecef;
}

.credit-card-card__icon-svg {
  color: #495057;
}

.credit-card-card__name {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text-primary);
}

.credit-card-card__value {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #087f5b;
}

.credit-card-card__label {
  font-size: 12px;
  color: #868e96;
  margin: 0;
}

.credit-card-card__available {
  font-size: 11px;
  color: #868e96;
  margin: 0;
}
</style>
