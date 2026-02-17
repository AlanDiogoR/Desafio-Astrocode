import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { updateProfile } from '~/services/user/updateProfile'

const nameSchema = z.string().min(1, 'Campo obrigatório').min(3, 'Nome deve ter no mínimo 3 caracteres').max(100, 'Nome deve ter no máximo 100 caracteres')
const passwordSchema = z.string().min(1, 'Campo obrigatório').min(8, 'A senha deve ter pelo menos 8 caracteres')

const editProfileSchema = z.object({
  name: nameSchema,
  currentPassword: z.string().optional(),
  newPassword: z.string().optional(),
  confirmPassword: z.string().optional(),
})
  .refine((data) => !data.newPassword || (data.currentPassword && data.currentPassword.length > 0), {
    message: 'Senha atual é obrigatória para alterar',
    path: ['currentPassword'],
  })
  .refine((data) => !data.newPassword || (data.newPassword?.length ?? 0) >= 8, {
    message: 'Nova senha deve ter no mínimo 8 caracteres',
    path: ['newPassword'],
  })
  .refine((data) => !data.newPassword || data.newPassword === data.confirmPassword, {
    message: 'As senhas não coincidem',
    path: ['confirmPassword'],
  })

export function useEditProfileModalController() {
  const authStore = useAuthStore()
  const { refetch } = useUser()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const name = ref('')
  const currentPassword = ref('')
  const newPassword = ref('')
  const confirmPassword = ref('')
  const errors = reactive<Record<string, string>>({})
  const touched = reactive<Record<string, boolean>>({})
  const submittedOnce = ref(false)
  const isLoading = ref(false)

  function initForm() {
    name.value = authStore.user?.name ?? ''
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  function markTouched(field: string) {
    touched[field] = true
  }

  function shouldShowError(field: string): boolean {
    return !!(touched[field] || submittedOnce.value) && !!errors[field]
  }

  async function handleSubmit(closeModal: () => void) {
    submittedOnce.value = true
    Object.keys(errors).forEach((key) => delete errors[key])

    const wantsToChangePassword = !!newPassword.value.trim()

    const result = editProfileSchema.safeParse({
      name: name.value.trim(),
      currentPassword: wantsToChangePassword ? currentPassword.value : undefined,
      newPassword: wantsToChangePassword ? newPassword.value : undefined,
      confirmPassword: wantsToChangePassword ? confirmPassword.value : undefined,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) {
            const key = path === 'confirmPassword' ? 'confirmPassword' : path.toString()
            errors[key] = issue.message
          }
        })
      }
      return
    }

    isLoading.value = true
    try {
      const payload: { name?: string; currentPassword?: string; newPassword?: string } = {}
      if (result.data.name) payload.name = result.data.name
      if (wantsToChangePassword && result.data.currentPassword && result.data.newPassword) {
        payload.currentPassword = result.data.currentPassword
        payload.newPassword = result.data.newPassword
      }

      const updated = await updateProfile(payload)
      authStore.setUser({
        id: updated.id,
        name: updated.name,
        email: updated.email,
        createdAt: updated.createdAt,
        updatedAt: updated.updatedAt,
      })
      toast.success('Perfil atualizado com sucesso!')
      refetch()
      closeModal()
      initForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao atualizar perfil. Tente novamente.'))
      const axiosErr = err as { response?: { data?: { message?: string } } }
      const msg = axiosErr?.response?.data?.message
      if (msg && typeof msg === 'string' && msg.toLowerCase().includes('senha')) {
        errors.currentPassword = msg
      }
    } finally {
      isLoading.value = false
    }
  }

  return {
    name,
    currentPassword,
    newPassword,
    confirmPassword,
    errors,
    markTouched,
    shouldShowError,
    isLoading,
    initForm,
    handleSubmit,
  }
}
