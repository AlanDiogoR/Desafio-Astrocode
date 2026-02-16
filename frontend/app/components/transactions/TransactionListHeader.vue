<script setup lang="ts">
import AppDropdown from '~/components/ui/AppDropdown.vue'
import { DropdownMenuItem } from 'radix-vue'
import type { TransactionTypeOption } from '~/constants/transactions'

const props = defineProps<{
  transactionTypes: TransactionTypeOption[]
  selectedType: TransactionTypeOption
  onTypeSelect: (type: TransactionTypeOption) => void
}>()

const emit = defineEmits<{
  openFilters: []
}>()
</script>

<template>
  <div class="transaction-list__header d-flex align-center justify-space-between mb-3">
    <AppDropdown content-max-width="279px">
      <template #trigger>
        <div class="transactions-dropdown-trigger d-flex align-center gap-2">
          <img
            :src="selectedType.icon"
            :alt="selectedType.label"
            class="transactions-dropdown-icon"
            :style="{ filter: selectedType.filter }"
          >
          <h3 class="section-title text-h6 font-weight-medium mb-0">
            {{ selectedType.label }}
          </h3>
          <v-icon icon="mdi-chevron-down" size="20" class="transactions-dropdown-chevron" />
        </div>
      </template>
      <template #content>
        <DropdownMenuItem
          v-for="type in transactionTypes"
          :key="type.value"
          class="dropdown-item"
          :class="{
            'dropdown-item--income': type.colorClass === 'income',
            'dropdown-item--expense': type.colorClass === 'expense',
          }"
          :text-value="type.label"
          @select="onTypeSelect(type)"
        >
          <div class="d-flex align-center dropdown-item__row" style="gap: 8px; width: 100%">
            <img
              :src="type.icon"
              alt=""
              width="20"
              height="20"
              :style="{ filter: type.filter, objectFit: 'contain' }"
              class="transition-all"
            >
            <span
              class="text-body-2"
              :class="{
                'text-green-darken-2': type.value === 'income',
                'text-red-darken-2': type.value === 'expense',
                'text-grey-darken-3': type.value === 'all',
              }"
            >
              {{ type.label }}
            </span>
          </div>
        </DropdownMenuItem>
      </template>
    </AppDropdown>
    <v-btn
      variant="text"
      icon
      size="small"
      class="section-filter"
      @click="emit('openFilters')"
    >
      <v-icon icon="mdi-filter-outline" size="22" class="filter-icon" />
    </v-btn>
  </div>
</template>

<style scoped>
.transactions-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 8px;
}

.transactions-dropdown-trigger:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.transactions-dropdown-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
  filter: brightness(0.3);
}

.transactions-dropdown-chevron {
  color: #495057;
}

.section-title {
  color: #212529;
}

.section-filter {
  min-width: 0;
  color: #495057;
}

.filter-icon {
  color: #495057;
}
</style>
