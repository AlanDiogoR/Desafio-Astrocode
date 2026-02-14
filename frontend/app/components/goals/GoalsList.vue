<script setup lang="ts">
import { formatCurrency } from '~/utils/format'
import { useGoals } from '~/composables/useGoals'

defineProps<{
  showPrivacy: boolean
}>()

const { goals } = useGoals()
const { openNewGoalModal } = useDashboard()

const hasGoals = computed(() => (goals.value ?? []).length > 0)

function progressStrokeDasharray(percentage: number): string {
  const circumference = 2 * Math.PI * 36
  const drawn = (Math.min(100, Math.max(0, percentage)) / 100) * circumference
  return `${drawn} ${circumference - drawn}`
}
</script>

<template>
  <section class="goals-section">
    <div class="goals-header d-flex align-center justify-space-between">
      <h3 class="goals-title">
        Minhas Metas
      </h3>
      <button
        type="button"
        class="goals-add-btn"
        aria-label="Adicionar meta"
        @click="openNewGoalModal()"
      >
        <span class="goals-add-btn__icon">+</span>
        <span class="goals-add-btn__text">Adicionar</span>
      </button>
    </div>
    <div
      v-if="hasGoals"
      class="goals-list"
    >
      <div
        v-for="goal in goals"
        :key="goal.id"
        class="goal-card"
      >
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
    </div>
  </section>
</template>

<style scoped>
.goals-section {
  flex-shrink: 0;
  margin-bottom: 16px;
}

.goals-header {
  margin-bottom: 12px;
}

.goals-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  opacity: 0.95;
  color: white;
}

.goals-add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 8px;
  background: transparent;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s;
}

.goals-add-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: white;
}

.goals-add-btn__icon {
  font-size: 18px;
  line-height: 1;
}

.goals-list {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 8px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.goals-list::-webkit-scrollbar {
  height: 6px;
}

.goals-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

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
}

.goal-card__progress {
  flex-shrink: 0;
}

.goal-card__ring {
  display: block;
}

.goal-card__ring-bg {
  stroke: rgba(8, 127, 91, 0.2);
}

.goal-card__ring-fill {
  transition: stroke-dasharray 0.3s ease;
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

