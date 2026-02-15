export interface ColorOption {
  color: string
  bg: string
  icon: string
}

export type Color = ColorOption

const colorIcon = (name: string) => `/images/${encodeURIComponent(`Color Name=${name}.png`)}`

export const BANK_COLORS: ColorOption[] = [
  { color: '#868E96', bg: '#F8F9FA', icon: colorIcon('Gray') },
  { color: '#FA5252', bg: '#FFF5F5', icon: colorIcon('Red') },
  { color: '#E64980', bg: '#FFF0F6', icon: colorIcon('Pink') },
  { color: '#BE4BDB', bg: '#F8F0FC', icon: colorIcon('Grape') },
  { color: '#7950F2', bg: '#F3F0FF', icon: colorIcon('Purple') },
  { color: '#4C6EF5', bg: '#EDF2FF', icon: colorIcon('Indigo') },
  { color: '#228BE6', bg: '#E7F5FF', icon: colorIcon('Blue') },
  { color: '#15AABF', bg: '#E3FAFC', icon: colorIcon('Cyan') },
  { color: '#12B886', bg: '#E6FCF5', icon: colorIcon('Teal') },
  { color: '#40C057', bg: '#EBFBEE', icon: colorIcon('Green') },
  { color: '#82C91E', bg: '#F4FCE3', icon: colorIcon('Lime') },
  { color: '#FAB005', bg: '#FFF9DB', icon: colorIcon('Yellow') },
  { color: '#FD7E14', bg: '#FFF4E6', icon: colorIcon('Orange') },
  { color: '#212529', bg: '#F8F9FA', icon: colorIcon('Black') },
]
