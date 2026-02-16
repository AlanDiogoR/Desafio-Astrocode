<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import { ChevronLeftIcon, ChevronRightIcon } from '@radix-icons/vue'
import type { TransactionFiltersState } from '~/composables/useTransactions'

const props = defineProps<{
  modelValue: boolean
  filters: TransactionFiltersState
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'apply': [filters: TransactionFiltersState]
}>()

const { accounts } = useBankAccounts()

const selectedAccountId = ref<string | undefined>(undefined)
const selectedYear = ref(new Date().getFullYear())

function prevYear() {
  selectedYear.value -= 1
}

function nextYear() {
  selectedYear.value += 1
}

function applyFilters() {
  emit('apply', {
    bankAccountId: selectedAccountId.value,
    year: selectedYear.value,
  })
  emit('update:modelValue', false)
}

watch(
  () => props.filters,
  (f) => {
    if (f) {
      selectedAccountId.value = f.bankAccountId
      if (f.year) selectedYear.value = f.year
    }
  },
  { immediate: true, deep: true },
)
</script>

<template>
  <AppModal
    title="Filtros"
    :open="modelValue"
    @update:open="(v: boolean) => emit('update:modelValue', v)"
  >
    <div class="filters-content">
      <section class="filters-section">
        <h3 class="filters-section__title">Conta</h3>
        <div class="filters-section__accounts">
          <button
            type="button"
            class="account-btn"
            :class="{ 'account-btn--selected': selectedAccountId === undefined }"
            @click="selectedAccountId = undefined"
          >
            Todas
          </button>
          <button
            v-for="acc in accounts"
            :key="acc.id"
            type="button"
            class="account-btn"
            :class="{ 'account-btn--selected': selectedAccountId === acc.id }"
            @click="selectedAccountId = acc.id"
          >
            {{ acc.name }}
          </button>
        </div>
      </section>

      <section class="filters-section filters-section--year">
        <h3 class="filters-section__title">Ano</h3>
        <div class="year-selector">
          <button
            type="button"
            class="year-nav"
            aria-label="Ano anterior"
            @click="prevYear"
          >
            <ChevronLeftIcon width="36" height="36" />
          </button>
          <span class="year-value">{{ selectedYear }}</span>
          <button
            type="button"
            class="year-nav"
            aria-label="Próximo ano"
            @click="nextYear"
          >
            <ChevronRightIcon width="36" height="36" />
          </button>
        </div>
      </section>

      <v-btn
        color="primary"
        block
        rounded="pill"
        height="48"
        class="filters-apply"
        @click="applyFilters"
      >
        Aplicar filtros
      </v-btn>
    </div>
  </AppModal>
</template>

<style scoped>
.filters-content {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.filters-section {
  margin-top: 0;
}

.filters-section:first-child {
  margin-top: 0;
}

.filters-section__title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #1f2937;
  margin: 0;
}

.filters-section__accounts {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-btn {
  width: 100%;
  text-align: left;
  padding: 8px 16px;
  border-radius: 16px;
  border: none;
  background-color: #f3f4f6;
  color: #1f2937;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.account-btn:hover {
  background-color: #f9fafb;
}

.account-btn--selected {
  background-color: #e5e7eb;
  font-weight: 600;
}

.filters-section--year {
  margin-top: 40px;
}

.year-selector {
  margin-top: 8px;
  width: 210px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.year-nav {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  color: #1f2937;
  border-radius: 8px;
}

.year-nav:hover {
  background-color: #f3f4f6;
}

.year-value {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  flex: 1;
}

.filters-apply {
  margin-top: 40px;
}
</style>
