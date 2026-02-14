<script setup lang="ts">
import type { Component } from 'vue'
import {
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuPortal,
  DropdownMenuRoot,
  DropdownMenuTrigger,
} from 'radix-vue'

interface DropdownItem {
  label: string
  icon?: Component
  action?: () => void
  danger?: boolean
}

type DropdownSide = 'top' | 'right' | 'bottom' | 'left'
type DropdownAlign = 'start' | 'center' | 'end'

interface Props {
  items?: DropdownItem[]
  contentMaxWidth?: string
  contentSide?: DropdownSide
  contentAlign?: DropdownAlign
  contentZIndex?: number
}

const props = withDefaults(defineProps<Props>(), {
  items: () => [],
  contentMaxWidth: undefined,
  contentSide: 'bottom',
  contentAlign: 'end',
  contentZIndex: undefined,
})

const contentStyle = computed(() => {
  const style: Record<string, string> = {}
  if (props.contentMaxWidth) style.maxWidth = props.contentMaxWidth
  if (props.contentZIndex != null) style.zIndex = String(props.contentZIndex)
  return Object.keys(style).length ? style : undefined
})

function handleSelect(item: DropdownItem) {
  item.action?.()
}
</script>

<template>
  <DropdownMenuRoot :modal="false">
    <DropdownMenuTrigger as-child>
      <slot name="trigger" />
    </DropdownMenuTrigger>
    <DropdownMenuPortal>
      <DropdownMenuContent
        class="dropdown-content"
        :class="{ 'dropdown-content--top': contentSide === 'top' }"
        :style="contentStyle"
        :side="contentSide"
        :align="contentAlign"
        :side-offset="5"
      >
        <template v-if="items.length">
          <DropdownMenuItem
            v-for="(item, index) in items"
            :key="index"
            class="dropdown-item"
            :class="{ 'dropdown-item--danger': item.danger }"
            :text-value="item.label"
            @select="handleSelect(item)"
          >
            <span class="dropdown-item__label">{{ item.label }}</span>
            <component
              :is="item.icon"
              v-if="item.icon"
              class="dropdown-item__icon"
            />
          </DropdownMenuItem>
        </template>
        <template v-else>
          <slot name="content" />
        </template>
      </DropdownMenuContent>
    </DropdownMenuPortal>
  </DropdownMenuRoot>
</template>

<style>
.dropdown-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 8px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 11px 20px 0 rgba(0, 0, 0, 0.1);
  z-index: 2000;
  outline: none;
  will-change: transform, opacity;
  transform-origin: var(--dropdown-transform-origin, top center);
}

.dropdown-content[data-state="open"] {
  animation: slideUpAndFade 400ms cubic-bezier(0.16, 1, 0.3, 1);
}

.dropdown-content[data-state="closed"] {
  animation: slideDownAndFade 400ms cubic-bezier(0.16, 1, 0.3, 1);
}

.dropdown-content--top {
  --dropdown-transform-origin: bottom center;
}

.dropdown-content--top[data-state="open"] {
  animation: slideDownAndFadeIn 400ms cubic-bezier(0.16, 1, 0.3, 1);
}

.dropdown-content--top[data-state="closed"] {
  animation: slideUpAndFadeOut 400ms cubic-bezier(0.16, 1, 0.3, 1);
}

.dropdown-item {
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2px 4px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 400;
  color: #1f2937;
  cursor: pointer;
  outline: none;
  transition: background-color 0.2s ease;
}

.dropdown-item:hover,
.dropdown-item[data-highlighted] {
  background-color: #f9fafb;
}

.dropdown-item--danger {
  color: #dc2626;
}

.dropdown-item--danger[data-highlighted] {
  background-color: #fef2f2;
}

.dropdown-item--income {
  color: #12b886;
}

.dropdown-item--income[data-highlighted] {
  background-color: #e6fcf5;
}

.dropdown-item--expense {
  color: #ff6b6b;
}

.dropdown-item--expense[data-highlighted] {
  background-color: #fff5f5;
}

.dropdown-item__row {
  display: flex;
  align-items: center;
  gap: 2px;
  flex: 1;
  min-width: 0;
}

.dropdown-item__img {
  flex-shrink: 0;
  object-fit: contain;
}

.dropdown-item__label {
  flex: 1;
}

.dropdown-item__icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  margin-left: 8px;
  color: currentColor;
}

@keyframes slideUpAndFade {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDownAndFade {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(8px);
  }
}

@keyframes slideDownAndFadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUpAndFadeOut {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-8px);
  }
}
</style>
