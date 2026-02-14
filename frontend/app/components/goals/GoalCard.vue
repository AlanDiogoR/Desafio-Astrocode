<script setup lang="ts">
import { formatCurrency } from '~/utils/format'
import type { SavingsGoal } from '~/composables/useGoals'

const props = defineProps<{
  goal: SavingsGoal
  showPrivacy: boolean
}>()

const { openGoalInteractionModal } = useDashboard()

const isCompleted = computed(() => {
  const g = props.goal
  return g.currentAmount >= g.targetAmount || g.status === 'COMPLETED'
})

const canAdd = computed(() => {
  const g = props.goal
  return g.currentAmount < g.targetAmount && g.status !== 'COMPLETED'
})

function handleClick() {
  const type = canAdd.value ? 'DEPOSIT' : 'WITHDRAW'
  openGoalInteractionModal(type, props.goal)
}

function progressStrokeDasharray(percentage: number): string {
  const circumference = 2 * Math.PI * 36
  const drawn = (Math.min(100, Math.max(0, percentage)) / 100) * circumference
  return `${drawn} ${circumference - drawn}`
}
</script>

<template>
  <div
    class="goal-card"
    :class="{
      'goal-card--completed': isCompleted,
    }"
    role="button"
    tabindex="0"
    @click="handleClick"
    @keydown.enter="handleClick"
    @keydown.space.prevent="handleClick"
  >
    <div v-if="isCompleted" class="goal-card__badge">
      Concluída
    </div>
    <div class="goal-card__progress">
      <svg
        class="goal-card__ring"
        viewBox="0 0 80 80"
        width="72"
        height="72"
      >
        <circle
          class="goal-card__ring-bg"
          cx="40"
          cy="40"
          r="36"
          fill="none"
          stroke-width="6"
        />
        <circle
          class="goal-card__ring-fill"
          cx="40"
          cy="40"
          r="36"
          fill="none"
          stroke-width="6"
          stroke-linecap="round"
          :stroke="goal.color ?? '#087f5b'"
          :stroke-dasharray="progressStrokeDasharray(goal.progressPercentage)"
          stroke-dashoffset="0"
          transform="rotate(-90 40 40)"
        />
      </svg>
      <div v-if="isCompleted" class="goal-card__check">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12" />
        </svg>
      </div>
    </div>
    <div class="goal-card__body">
      <div class="goal-card__content">
        <span class="goal-card__title">{{ goal.name }}</span>
        <span class="goal-card__amount">
          {{ showPrivacy ? formatCurrency(goal.currentAmount) : '••••' }}
        </span>
      </div>
      <span class="goal-card__target">
        {{ showPrivacy ? `${formatCurrency(goal.currentAmount)} / ${formatCurrency(goal.targetAmount)}` : '•••• / ••••' }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.goal-card {
  flex: 0 0 220px;
  min-width: 220px;
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  border: 2px solid transparent;
  position: relative;
}

.goal-card:hover {
  background-color: #f9fafb;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.goal-card--completed {
  border-color: #d4a017;
  background: linear-gradient(to bottom, #fffbeb 0%, white 100%);
}

.goal-card--completed:hover {
  background: linear-gradient(to bottom, #fef3c7 0%, #f9fafb 100%);
}

.goal-card__badge {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 10px;
  font-weight: 700;
  color: #b45309;
  background: #fde68a;
  padding: 2px 8px;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.goal-card__progress {
  flex-shrink: 0;
  position: relative;
}

.goal-card__ring {
  display: block;
}

.goal-card__ring-bg {
  stroke: rgba(8, 127, 91, 0.2);
}

.goal-card--completed .goal-card__ring-bg {
  stroke: rgba(212, 160, 23, 0.3);
}

.goal-card__ring-fill {
  transition: stroke-dasharray 0.3s ease;
}

.goal-card__check {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #087f5b;
}

.goal-card__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.goal-card__content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.goal-card__title {
  font-size: 15px;
  font-weight: 600;
  color: #212529;
}

.goal-card__amount {
  font-size: 16px;
  font-weight: 700;
  color: #212529;
}

.goal-card__target {
  font-size: 12px;
  color: #868e96;
  align-self: flex-end;
}
</style>
