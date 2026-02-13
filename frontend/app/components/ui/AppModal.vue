<script setup lang="ts">
import {
  DialogClose,
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogRoot,
  DialogTitle,
} from 'radix-vue'
import { Cross2Icon } from '@radix-icons/vue'

interface Props {
  title: string
  modelValue: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

function handleOpenChange(value: boolean) {
  emit('update:modelValue', value)
}
</script>

<template>
  <DialogRoot
    :open="modelValue"
    :modal="true"
    @update:open="handleOpenChange"
  >
    <DialogPortal>
      <DialogOverlay class="modal-overlay" />
      <DialogContent class="modal-content">
        <header class="modal-header">
          <DialogClose as-child>
            <button
              type="button"
              class="modal-close"
              aria-label="Fechar"
            >
              <Cross2Icon width="24" height="24" />
            </button>
          </DialogClose>
          <DialogTitle class="modal-title">
            {{ title }}
          </DialogTitle>
          <div class="modal-spacer" />
        </header>
        <slot />
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(2px);
  z-index: 2000;
  animation: overlayShow 150ms cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-content {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 11px 20px 0 rgba(0, 0, 0, 0.1);
  z-index: 2001;
  outline: none;
  display: flex;
  flex-direction: column;
  gap: 40px;
  animation: contentShow 150ms cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 48px;
}

.modal-close {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  color: #1f2937;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.modal-close:hover {
  background-color: #f3f4f6;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #1f2937;
  text-align: center;
}

.modal-spacer {
  width: 48px;
  height: 48px;
}

@keyframes overlayShow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes contentShow {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}
</style>
