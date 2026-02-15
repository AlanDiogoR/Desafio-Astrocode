<script setup lang="ts">
import {
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogRoot,
  DialogTitle,
} from 'radix-vue'
import type { ConfirmDeleteEntityType } from '~/types/confirmDelete'

interface Props {
  isOpen: boolean
  title?: string
  description?: string
  entityType: ConfirmDeleteEntityType
  onConfirm: () => Promise<void>
  onClose: () => void
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Excluir',
})

const confirmTextMap: Record<ConfirmDeleteEntityType, string> = {
  ACCOUNT: 'Tem certeza que deseja excluir esta conta?',
  TRANSACTION: 'Tem certeza que deseja excluir esta transação?',
  GOAL: 'Tem certeza que deseja excluir esta meta?',
}

const accountWarning =
  'Ao excluir a conta, também serão excluídos todos os registros de receita e despesas relacionados.'

const confirmText = computed(() => confirmTextMap[props.entityType])
const showAccountWarning = computed(() => props.entityType === 'ACCOUNT')

const isLoading = ref(false)

async function handleConfirm() {
  isLoading.value = true
  try {
    await props.onConfirm()
    props.onClose()
  } catch {
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <DialogRoot
    :open="isOpen"
    :modal="true"
    @update:open="(v: boolean) => { if (!v) onClose() }"
  >
    <DialogPortal>
      <DialogOverlay class="confirm-delete-overlay" />
      <DialogContent
        class="confirm-delete-content"
        :aria-describedby="undefined"
        @pointer-down-outside="(e) => e.preventDefault()"
      >
        <DialogTitle class="confirm-delete-title">
          {{ title }}
        </DialogTitle>
        <div class="confirm-delete-body">
          <div class="confirm-delete-icon-wrap">
            <img
              src="/images/Nome=Deletar.svg"
              alt=""
              class="confirm-delete-icon"
            >
          </div>
          <p class="confirm-delete-text">
            {{ confirmText }}
          </p>
          <p
            v-if="showAccountWarning"
            class="confirm-delete-warning"
          >
            {{ accountWarning }}
          </p>
        </div>
        <div class="confirm-delete-actions">
          <button
            type="button"
            class="confirm-delete-btn confirm-delete-btn--confirm"
            :disabled="isLoading"
            @click="handleConfirm"
          >
            Sim, desejo excluir
          </button>
          <button
            type="button"
            class="confirm-delete-btn confirm-delete-btn--cancel"
            :disabled="isLoading"
            @click="onClose"
          >
            Cancelar
          </button>
        </div>
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>

<style scoped>
.confirm-delete-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(2px);
  z-index: 2100;
  animation: overlayShow 150ms cubic-bezier(0.16, 1, 0.3, 1);
}

.confirm-delete-content {
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
  z-index: 2101;
  display: flex;
  flex-direction: column;
  gap: 24px;
  animation: contentShow 150ms cubic-bezier(0.16, 1, 0.3, 1);
}

.confirm-delete-content:focus {
  outline: 2px solid #087f5b;
  outline-offset: 2px;
}

.confirm-delete-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #1f2937;
  text-align: center;
  margin: 0;
}

.confirm-delete-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.confirm-delete-icon-wrap {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #fef2f2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-delete-icon {
  width: 28px;
  height: 28px;
  filter: brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%);
}

.confirm-delete-text {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  text-align: center;
  margin: 0;
}

.confirm-delete-warning {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  text-align: center;
  margin: 0;
}

.confirm-delete-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.confirm-delete-btn {
  width: 100%;
  padding: 14px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  border: none;
}

.confirm-delete-btn--confirm {
  background-color: #dc2626;
  color: white;
}

.confirm-delete-btn--confirm:hover:not(:disabled) {
  background-color: #b91c1c;
}

.confirm-delete-btn--cancel {
  background-color: white;
  color: #1f2937;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.confirm-delete-btn--cancel:hover:not(:disabled) {
  background-color: rgba(31, 41, 55, 0.04);
}

.confirm-delete-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes overlayShow {
  from { opacity: 0; }
  to { opacity: 1; }
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
