<script setup lang="ts">
import GoalsFab from '~/components/goals/GoalsFab.vue'
import GoalCard from '~/components/goals/GoalCard.vue'
import type { SavingsGoal } from '~/composables/useGoals'
import { useCarousel } from '~/composables/useCarousel'

const props = defineProps<{
  goals: SavingsGoal[]
  showPrivacy: boolean
  isLoading?: boolean
}>()

const { carouselRef, scroll } = useCarousel()
const hasGoals = computed(() => (props.goals ?? []).length > 0)
const hasCarousel = computed(() => (props.goals ?? []).length >= 3)
</script>

<template>
  <section class="goals-section">
    <div class="goals-header d-flex align-center justify-space-between">
      <h3 class="goals-title">
        Minhas Metas
      </h3>
      <div class="goals-header__actions d-flex align-center">
        <GoalsFab />
        <template v-if="hasCarousel">
          <v-btn
            icon="mdi-chevron-left"
            variant="text"
            density="compact"
            color="white"
            class="goals-nav__btn"
            @click="scroll(-1)"
          />
          <v-btn
            icon="mdi-chevron-right"
            variant="text"
            density="compact"
            color="white"
            class="goals-nav__btn"
            @click="scroll(1)"
          />
        </template>
      </div>
    </div>
    <div v-if="isLoading" class="goals-skeleton">
      <div class="goals-skeleton__spinner">
        <v-progress-circular
          indeterminate
          color="white"
          size="48"
        />
      </div>
      <v-skeleton-loader
        type="list-item-avatar-two-line"
        class="goals-skeleton__item"
      />
      <v-skeleton-loader
        type="list-item-avatar-two-line"
        class="goals-skeleton__item"
      />
    </div>
    <div
      v-else-if="hasGoals"
      ref="carouselRef"
      class="goals-list"
      :class="{ 'goals-list--carousel': hasCarousel }"
    >
      <GoalCard
        v-for="goal in goals"
        :key="goal.id"
        :goal="goal"
        :show-privacy="showPrivacy"
        class="goals-list__card"
      />
    </div>
    <div v-else class="goals-empty" />
  </section>
</template>

<style scoped>
.goals-section {
  position: relative;
  flex-shrink: 0;
  margin-bottom: 16px;
}

.goals-header {
  margin-bottom: 12px;
}

.goals-header__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.goals-nav__btn :deep(.v-btn),
.goals-nav__btn :deep(.v-icon) {
  color: rgb(255, 255, 255);
}

.goals-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  opacity: 0.95;
  color: white;
}

.goals-skeleton {
  display: flex;
  gap: 16px;
  padding-bottom: 8px;
  position: relative;
}

.goals-skeleton__spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  opacity: 0.95;
}

.goals-skeleton__item {
  flex: 0 0 220px;
  min-width: 220px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
}

.goals-empty {
  min-height: 4px;
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
  scroll-snap-type: x mandatory;
}

.goals-list--carousel .goals-list__card {
  scroll-snap-align: start;
}

.goals-list::-webkit-scrollbar {
  height: 6px;
}

.goals-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}
</style>
