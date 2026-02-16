<script setup lang="ts">
import { PlusIcon } from '@radix-icons/vue'
import AppDropdown from '~/components/ui/AppDropdown.vue'
import { DropdownMenuItem } from 'radix-vue'
import { TRANSACTION_FAB_OPTIONS } from '~/constants/transactions'

const emit = defineEmits<{
  action: [action: string]
}>()

function handleSelect(action: string) {
  emit('action', action)
}
</script>

<template>
  <div class="fab-wrapper">
    <AppDropdown
      class="fab-dropdown"
      content-side="top"
      content-align="end"
    >
      <template #trigger>
        <v-btn
          color="primary"
          icon
          width="48"
          height="48"
          min-width="48"
          rounded="circle"
          elevation="4"
          class="fab-trigger d-flex align-center justify-center"
        >
          <PlusIcon width="32" height="32" class="plus-icon transition-transform text-white" />
        </v-btn>
      </template>
      <template #content>
        <DropdownMenuItem
          v-for="(opt, i) in TRANSACTION_FAB_OPTIONS"
          :key="i"
          class="dropdown-item"
          :text-value="opt.label"
          @select="handleSelect(opt.action)"
        >
          <div class="d-flex align-center w-100">
            <div
              class="d-flex align-center justify-center mr-3"
              style="width: 40px; height: 40px; flex-shrink: 0"
            >
              <img
                :src="opt.icon"
                alt=""
                :width="opt.size ?? 24"
                :height="opt.size ?? 24"
                class="transition-opacity"
                style="object-fit: contain"
                :style="opt.filter ? { filter: opt.filter } : {}"
              >
            </div>
            <span class="text-body-2 font-weight-medium">{{ opt.label }}</span>
          </div>
        </DropdownMenuItem>
      </template>
    </AppDropdown>
  </div>
</template>

<style scoped>
.fab-wrapper {
  position: absolute !important;
  bottom: 16px !important;
  right: 16px !important;
  z-index: 100;
}

@media (max-width: 959px) {
  .fab-wrapper {
    position: fixed !important;
    bottom: calc(24px + env(safe-area-inset-bottom, 0px)) !important;
    right: calc(24px + env(safe-area-inset-right, 0px)) !important;
    left: auto !important;
  }
}

.plus-icon {
  color: white;
  transition: transform 0.3s ease;
}

.fab-trigger[data-state="open"] .plus-icon {
  transform: rotate(135deg);
}
</style>
