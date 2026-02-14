<script setup lang="ts">
import { PlusIcon, PlusCircledIcon, MinusCircledIcon, Pencil1Icon } from '@radix-icons/vue'
import AppDropdown from '~/components/ui/AppDropdown.vue'
import { DropdownMenuItem } from 'radix-vue'

const { goals } = useGoals()
const { openNewGoalModal, openGoalInteractionModal, openEditGoalModal } = useDashboard()

const goalsList = computed(() => goals.value ?? [])
const hasGoals = computed(() => goalsList.value.length > 0)

const fabItems = computed(() => {
  const items: Array<{
    label: string
    action: () => void
    icon: typeof PlusIcon
    iconClass: string
    visible: boolean
  }> = [
    {
      label: 'Nova Meta',
      action: () => openNewGoalModal(),
      icon: PlusIcon,
      iconClass: 'goals-fab__icon--teal',
      visible: true,
    },
    {
      label: 'Depositar',
      action: () => openGoalInteractionModal('DEPOSIT'),
      icon: PlusCircledIcon,
      iconClass: 'goals-fab__icon--green',
      visible: hasGoals.value,
    },
    {
      label: 'Resgatar',
      action: () => openGoalInteractionModal('WITHDRAW'),
      icon: MinusCircledIcon,
      iconClass: 'goals-fab__icon--red',
      visible: hasGoals.value,
    },
    {
      label: 'Editar Meta',
      action: () => {
        const list = goalsList.value
        openEditGoalModal(list.length === 1 ? list[0] : undefined)
      },
      icon: Pencil1Icon,
      iconClass: 'goals-fab__icon--gray',
      visible: hasGoals.value,
    },
  ]
  return items.filter((i) => i.visible)
})

function handleSelect(action: () => void) {
  action()
}
</script>

<template>
  <AppDropdown
    class="goals-fab-dropdown"
    content-side="top"
    content-align="end"
    :content-z-index="2500"
  >
    <template #trigger>
      <button
        type="button"
        class="fab-minimalist"
        aria-label="Ações de metas"
      >
        <PlusIcon width="24" height="24" class="fab-minimalist__icon" />
      </button>
    </template>
    <template #content>
      <DropdownMenuItem
        v-for="(item, i) in fabItems"
        :key="i"
        class="goals-fab__item"
        :text-value="item.label"
        @select="handleSelect(item.action)"
      >
        <component :is="item.icon" class="goals-fab__icon" :class="item.iconClass" />
        <span class="goals-fab__label">{{ item.label }}</span>
      </DropdownMenuItem>
    </template>
  </AppDropdown>
</template>

<style scoped>
.fab-minimalist {
  background: transparent !important;
  border: none;
  padding: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease;
}

.fab-minimalist__icon {
  color: white;
  transition: transform 0.3s ease;
}

.fab-minimalist[data-state="open"] .fab-minimalist__icon {
  transform: rotate(45deg);
}

.goals-fab__item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
  padding: 8px 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.goals-fab__item:hover,
.goals-fab__item[data-highlighted] {
  background-color: #f9fafb;
}

.goals-fab__icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.goals-fab__icon--teal {
  color: #0f766e;
}

.goals-fab__icon--green {
  color: #12b886;
}

.goals-fab__icon--red {
  color: #ff6b6b;
}

.goals-fab__icon--gray {
  color: #374151;
}

.goals-fab__label {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}
</style>
