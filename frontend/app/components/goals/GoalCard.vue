<script setup lang="ts">
import { formatCurrency, formatDate } from '~/utils/format'
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

const isExpired = computed(() => {
  const endDate = props.goal.endDate
  if (!endDate) return false
  return new Date(endDate) < new Date()
})

const badgeLabel = computed(() => {
  if (isExpired.value) return 'Encerrada'
  if (isCompleted.value) return 'Concluída'
  return ''
})

const formattedDeadline = computed(() => {
  const endDate = props.goal.endDate
  if (!endDate) return ''
  return formatDate(new Date(endDate), 'd MMM yyyy')
})

const canAdd = computed(() => {
  const g = props.goal
  return g.currentAmount < g.targetAmount && g.status !== 'COMPLETED' && !isExpired.value
})

function handleClick() {
  if (isExpired.value) return
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
    class="goal-card d-flex align-center ga-3 pa-4 rounded-xl"
    :class="{
      'goal-card--completed': isCompleted,
      'goal-card--expired': isExpired,
    }"
    role="button"
    tabindex="0"
    @click="handleClick"
    @keydown.enter="handleClick"
    @keydown.space.prevent="handleClick"
  >
    <div v-if="badgeLabel" class="goal-card__badge" :class="{ 'goal-card__badge--expired': isExpired }">
      {{ badgeLabel }}
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
      <div v-if="!(isCompleted && !isExpired)" class="goal-card__percentage">
        {{ Math.round(goal.progressPercentage) }}%
      </div>
      <div v-if="isCompleted && !isExpired" class="goal-card__check">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="20 6 9 17 4 12" />
        </svg>
      </div>
    </div>
    <div class="goal-card__body flex-grow-1 min-width-0 d-flex flex-column ga-1">
      <div class="d-flex flex-column">
        <span class="goal-card__title">{{ goal.name }}</span>
        <span class="goal-card__amount">
          {{ showPrivacy ? formatCurrency(goal.currentAmount) : '••••' }}
        </span>
      </div>
      <span v-if="formattedDeadline" class="goal-card__deadline">
        {{ formattedDeadline }}
      </span>
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
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
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

.goal-card--expired {
  border-color: #adb5bd;
  background: #f1f3f5;
  cursor: default;
  opacity: 0.9;
}

.goal-card--expired:hover {
  background: #e9ecef;
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

.goal-card__badge--expired {
  color: #495057;
  background: #dee2e6;
}

.goal-card__progress {
  flex-shrink: 0;
  position: relative;
}

.goal-card__percentage {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #212529;
}

.goal-card--completed .goal-card__percentage {
  color: #b45309;
}

.goal-card--expired .goal-card__percentage {
  color: #495057;
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

.goal-card--expired .goal-card__ring-bg {
  stroke: rgba(173, 181, 189, 0.3);
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

.goal-card__deadline {
  font-size: 11px;
  color: #868e96;
}

.goal-card__target {
  font-size: 12px;
  color: #868e96;
  align-self: flex-end;
}
</style>
