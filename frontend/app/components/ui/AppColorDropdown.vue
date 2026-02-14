<script setup lang="ts">
import { CheckIcon, ChevronDownIcon, CrossCircledIcon } from '@radix-icons/vue'
import {
  DropdownMenuContent,
  DropdownMenuPortal,
  DropdownMenuRoot,
  DropdownMenuTrigger,
} from 'radix-vue'
import { BANK_COLORS } from '~/utils/colors'

interface Props {
  modelValue?: string | null
  label?: string
  errorText?: string
}

withDefaults(defineProps<Props>(), {
  label: 'Cor',
  errorText: '',
})

const modelValue = defineModel<string | null>()
const isOpen = ref(false)

const selectedColor = computed(() =>
  BANK_COLORS.find((c) => c.color === modelValue.value)
)

const hasValue = computed(() => !!modelValue.value)

function selectColor(color: string) {
  modelValue.value = color
  isOpen.value = false
}
</script>

<template>
  <div class="app-color-dropdown">
    <DropdownMenuRoot v-model:open="isOpen">
      <DropdownMenuTrigger as-child>
        <button
          type="button"
          class="app-color-dropdown__trigger"
          :class="{
            'app-color-dropdown__trigger--filled': hasValue,
            'app-color-dropdown__trigger--empty': !hasValue,
            'app-color-dropdown__trigger--error': !!errorText,
          }"
        >
          <span
            :class="{
              'app-color-dropdown__label': true,
              'app-color-dropdown__label--float': hasValue || isOpen,
            }"
          >
            {{ label }}
          </span>
          <div class="app-color-dropdown__value" />
          <div class="app-color-dropdown__right-icon">
            <span
              v-if="selectedColor"
              class="app-color-dropdown__circle"
              :style="{
                backgroundColor: selectedColor.color,
                borderColor: selectedColor.color,
              }"
            />
            <ChevronDownIcon
              v-else
              class="app-color-dropdown__chevron"
              aria-hidden="true"
            />
          </div>
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuPortal>
        <DropdownMenuContent
          class="app-color-dropdown__content"
          align="start"
          :side-offset="5"
          :style="{ zIndex: 2500 }"
        >
          <div class="app-color-dropdown__grid">
            <button
              v-for="opt in BANK_COLORS"
              :key="opt.color"
              type="button"
              class="app-color-dropdown__item"
              :class="{ 'app-color-dropdown__item--selected': modelValue === opt.color }"
              :aria-label="`Cor ${opt.color}`"
              @click="selectColor(opt.color)"
            >
              <span
                class="app-color-dropdown__item-circle"
                :style="{ backgroundColor: opt.color }"
              />
              <CheckIcon
                v-if="modelValue === opt.color"
                class="app-color-dropdown__check"
              />
            </button>
          </div>
        </DropdownMenuContent>
      </DropdownMenuPortal>
    </DropdownMenuRoot>
    <div v-if="errorText" class="app-color-dropdown__error">
      <CrossCircledIcon class="app-color-dropdown__error-icon" />
      <span class="app-color-dropdown__error-text">{{ errorText }}</span>
    </div>
  </div>
</template>

<style scoped>
.app-color-dropdown__trigger {
  position: relative;
  width: 100%;
  height: 56px;
  background: white;
  border: 1px solid #495057;
  border-radius: 16px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: left;
  cursor: pointer;
  outline: none;
}

.app-color-dropdown__trigger:focus,
.app-color-dropdown__trigger:focus-visible {
  outline: none;
}

.app-color-dropdown__trigger[data-state="open"] {
  border-color: #087f5b;
}

.app-color-dropdown__label--float {
  top: -8px;
  font-size: 12px;
  color: #087f5b;
  left: 14px;
  transform: none;
}

.app-color-dropdown__label {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #6b7280;
  pointer-events: none;
  transition: all 0.2s ease;
  padding: 0 4px;
  background: white;
}

.app-color-dropdown__value {
  flex: 1;
  min-width: 0;
}

.app-color-dropdown__right-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.app-color-dropdown__circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid;
}

.app-color-dropdown__chevron {
  width: 20px;
  height: 20px;
  color: #6b7280;
}

.app-color-dropdown__trigger--error {
  border-color: #e03131;
}

.app-color-dropdown__error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 6px;
  color: #e03131;
}

.app-color-dropdown__error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.app-color-dropdown__error-text {
  font-size: 14px;
  font-weight: 500;
}

.app-color-dropdown__content {
  background: white;
  border-radius: 16px;
  border: 1px solid #f3f4f6;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  z-index: 2500;
  padding: 12px;
}

.app-color-dropdown__grid {
  background: white;
  border-radius: 16px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);

}

.app-color-dropdown__item {
  position: relative;
  width: 40px;
  height: 40px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: background-color 0.2s ease;
}

.app-color-dropdown__item:hover {
  background: #f9fafb; /* gray-50 */
}

.app-color-dropdown__item-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.app-color-dropdown__item--selected .app-color-dropdown__item-circle {
  box-shadow: 0 0 0 2px white, 0 0 0 4px currentColor;
}

.app-color-dropdown__check {
  position: absolute;
  width: 18px;
  height: 18px;
  color: white;
  filter: drop-shadow(0 1px 1px rgba(0, 0, 0, 0.3));
}
</style>
