<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

useSeoMeta({
  title: 'Exclusão de Dados — Grivy',
  description: 'Como solicitar a exclusão dos seus dados pessoais da plataforma Grivy.',
  ogTitle: 'Exclusão de Dados — Grivy',
  ogDescription: 'Solicite a remoção dos seus dados conforme a LGPD.',
})

const route = useRoute()
const confirmationCode = computed(() => route.query.code as string | undefined)
const statusMessage = ref('Verificando status da solicitação...')
const statusIcon = ref('⏳')

if (confirmationCode.value) {
  const config = useRuntimeConfig()
  try {
    const data = await $fetch<{ status: string }>(
      `/api/webhooks/meta/data-deletion/status/${confirmationCode.value}`,
      { baseURL: config.public.apiBase as string },
    )
    if (data?.status === 'completed') {
      statusMessage.value = 'Seus dados foram excluídos com sucesso.'
      statusIcon.value = '✅'
    } else {
      statusMessage.value = 'Solicitação recebida e em processamento. Você receberá uma confirmação por e-mail.'
      statusIcon.value = '⏳'
    }
  } catch {
    statusMessage.value = 'Solicitação registrada. Seus dados serão excluídos em até 30 dias. Guarde o código para referência.'
    statusIcon.value = '✅'
  }
}
</script>

<template>
  <div class="legal-page">
    <div class="legal-container">
      <div class="legal-nav">
        <v-btn variant="text" color="primary" to="/" size="small">
          <v-icon start>mdi-arrow-left</v-icon>
          Voltar
        </v-btn>
      </div>

      <span class="legal-chip legal-chip--danger">Exclusão de Dados</span>

      <h1>Solicitar Exclusão de Dados</h1>
      <p class="legal-subtitle">
        Conforme a LGPD e os requisitos da API do WhatsApp (Meta Platforms).
      </p>

      <div class="deletion-card">
        <span class="method-label">Método 1 — Recomendado</span>
        <h2>Excluir pelo aplicativo</h2>
        <p>A forma mais rápida. Dados apagados em até 30 dias.</p>
        <ol>
          <li>Acesse <a href="https://grivy.netlify.app">grivy.netlify.app</a> e faça login</li>
          <li>Clique no seu avatar → <strong>Configurações</strong></li>
          <li>Role até a seção <strong>"Conta"</strong></li>
          <li>Clique em <strong>"Excluir minha conta e todos os dados"</strong></li>
          <li>Confirme digitando seu e-mail</li>
        </ol>
        <div class="legal-warning">
          Esta ação é irreversível. Todos os dados financeiros e configurações serão permanentemente excluídos.
        </div>
      </div>

      <div class="deletion-card">
        <span class="method-label">Método 2 — Via E-mail</span>
        <h2>Solicitar por e-mail</h2>
        <p>Se não conseguir acessar sua conta:</p>
        <div class="email-info">
          <strong>Enviar para:</strong>
          <a href="mailto:privacidade@grivy.app">privacidade@grivy.app</a>
        </div>
        <div class="email-info">
          <strong>Assunto:</strong>
          Solicitação de Exclusão de Dados — [seu e-mail cadastrado]
        </div>
        <p>
          Inclua no corpo: e-mail cadastrado, nome completo e número de WhatsApp
          vinculado (se houver).
        </p>
        <p>Respondemos em até <strong>15 dias úteis</strong>.</p>
      </div>

      <div v-if="confirmationCode" class="deletion-card deletion-card--status">
        <h2>{{ statusIcon }} Status da sua solicitação</h2>
        <p>{{ statusMessage }}</p>
        <p style="color: var(--color-text-muted); font-size: 13px;">
          Código de confirmação: <code>{{ confirmationCode }}</code>
        </p>
      </div>

      <div class="deletion-card">
        <h2>O que acontece após a solicitação</h2>
        <ul>
          <li>Informações de cadastro (nome, e-mail, senha)</li>
          <li>Todas as transações financeiras</li>
          <li>Número de WhatsApp e histórico de mensagens</li>
          <li>Preferências e configurações</li>
          <li>Dados de uso vinculados ao perfil</li>
        </ul>
        <div class="legal-warning">
          Logs de segurança são mantidos por até 12 meses por obrigação legal.
        </div>
        <p>Exclusão completa em até <strong>30 dias</strong> após a confirmação.</p>
      </div>
    </div>
  </div>
</template>
