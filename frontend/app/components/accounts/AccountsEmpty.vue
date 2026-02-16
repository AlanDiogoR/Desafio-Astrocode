<script setup lang="ts">
import { PlusIcon } from '@radix-icons/vue'

defineEmits<{
  click: []
}>()
</script>

<template>
  <div
    class="accounts-empty"
    role="button"
    tabindex="0"
    @click="$emit('click')"
    @keydown.enter="$emit('click')"
    @keydown.space.prevent="$emit('click')"
  >
    <div class="accounts-empty__icon-wrapper">
      <PlusIcon class="accounts-empty__icon" />
    </div>
    <span class="accounts-empty__text">Cadastrar uma nova conta</span>
  </div>
</template>

<style scoped>
.accounts-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 32px;
  border: 2px dashed #ffffff;
  border-radius: 16px;
  cursor: pointer;
  min-height: 120px;
  background: transparent;
}

.accounts-empty__icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px dashed #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.accounts-empty__icon {
  width: 24px;
  height: 24px;
  color: #ffffff;
}

.accounts-empty__text {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  text-align: center;
}
</style>
