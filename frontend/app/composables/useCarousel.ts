import { CAROUSEL_CARD_SCROLL_OFFSET } from '~/constants/carousel'

export function useCarousel() {
  const carouselRef = ref<HTMLElement | null>(null)

  function scroll(direction: number) {
    const el = carouselRef.value
    if (!el) return
    el.scrollBy({ left: direction * CAROUSEL_CARD_SCROLL_OFFSET, behavior: 'smooth' })
  }

  return { carouselRef, scroll }
}
