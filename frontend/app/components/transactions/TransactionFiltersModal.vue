<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import { ChevronLeftIcon, ChevronRightIcon } from '@radix-icons/vue'

interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const accounts = ['Nubank', 'XP Investimentos', 'Dinheiro']

const selectedAccount = ref('Nubank')
const selectedYear = ref(2026)

function selectAccount(account: string) {
  selectedAccount.value = account
}

function prevYear() {
  selectedYear.value -= 1
}

function nextYear() {
  selectedYear.value += 1
}

function applyFilters() {
  emit('update:modelValue', false)
}
</script>

<template>
  <AppModal
    title="Filtros"
    :model-value="props.modelValue"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div class="filters-content">
      <section class="filters-section">
        <h3 class="filters-section__title">Conta</h3>
        <div class="filters-section__accounts">
          <button
            v-for="account in accounts"
            :key="account"
            type="button"
            class="account-btn"
            :class="{ 'account-btn--selected': selectedAccount === account }"
            @click="selectAccount(account)"
          >
            {{ account }}
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
