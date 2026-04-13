<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

useSeoMeta({
  title: 'Política de Privacidade — Grivy',
  description: 'Como a Grivy coleta, usa e protege seus dados pessoais, conforme a LGPD.',
  ogTitle: 'Política de Privacidade — Grivy',
  ogDescription: 'Transparência total sobre como tratamos seus dados.',
})
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

      <span class="legal-chip">Documento Legal</span>

      <h1>Política de Privacidade</h1>
      <p class="legal-subtitle">
        Última atualização: <strong>Janeiro de 2025</strong> · Versão 1.0
      </p>

      <nav class="legal-toc">
        <h3>Índice</h3>
        <ol>
          <li><a href="#quem-somos">Quem somos</a></li>
          <li><a href="#dados-coletados">Dados que coletamos</a></li>
          <li><a href="#como-usamos">Como usamos seus dados</a></li>
          <li><a href="#base-legal">Base legal (LGPD)</a></li>
          <li><a href="#whatsapp">Integração com WhatsApp</a></li>
          <li><a href="#compartilhamento">Compartilhamento de dados</a></li>
          <li><a href="#retencao">Retenção de dados</a></li>
          <li><a href="#direitos">Seus direitos</a></li>
          <li><a href="#seguranca">Segurança</a></li>
          <li><a href="#contato">Contato</a></li>
        </ol>
      </nav>

      <section id="quem-somos">
        <h2>1. Quem somos</h2>
        <p>
          A <strong>Grivy</strong> é um serviço de controle financeiro pessoal
          disponível em <a href="https://grivy.netlify.app">grivy.netlify.app</a>.
          Para fins desta política, a Grivy atua como <strong>controladora</strong>
          dos dados pessoais dos usuários, conforme definido pela LGPD.
        </p>
      </section>

      <section id="dados-coletados">
        <h2>2. Dados que coletamos</h2>
        <table class="legal-table">
          <thead>
            <tr>
              <th>Categoria</th>
              <th>Exemplos</th>
              <th>Quando coletamos</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><strong>Cadastro</strong></td>
              <td>Nome, e-mail, senha (hash)</td>
              <td>Ao criar conta</td>
            </tr>
            <tr>
              <td><strong>Financeiros</strong></td>
              <td>Transações, categorias, valores, datas</td>
              <td>Ao usar o app</td>
            </tr>
            <tr>
              <td><strong>WhatsApp</strong></td>
              <td>Número de telefone, mensagens enviadas</td>
              <td>Ao vincular o número</td>
            </tr>
            <tr>
              <td><strong>Uso</strong></td>
              <td>IP, browser, páginas acessadas</td>
              <td>Automaticamente</td>
            </tr>
          </tbody>
        </table>
        <p>
          Dados de cartão de crédito ou informações bancárias completas
          <strong>não são armazenados</strong> em nossos servidores.
        </p>
      </section>

      <section id="como-usamos">
        <h2>3. Como usamos seus dados</h2>
        <ul>
          <li>Prestar o serviço de controle financeiro pessoal</li>
          <li>Autenticar seu acesso e manter a segurança da conta</li>
          <li>Enviar e-mails transacionais (confirmação, recuperação de senha)</li>
          <li>Enviar comunicações de marketing, quando você consentir</li>
          <li>Processar mensagens enviadas via WhatsApp para registrar transações</li>
          <li>Melhorar a plataforma com analytics anonimizados</li>
          <li>Cumprir obrigações legais</li>
        </ul>
      </section>

      <section id="base-legal">
        <h2>4. Base legal (LGPD)</h2>
        <table class="legal-table">
          <thead>
            <tr>
              <th>Finalidade</th>
              <th>Base legal</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Prestação do serviço</td>
              <td>Art. 7º, V — execução de contrato</td>
            </tr>
            <tr>
              <td>E-mails transacionais</td>
              <td>Art. 7º, V — execução de contrato</td>
            </tr>
            <tr>
              <td>E-mails de marketing</td>
              <td>Art. 7º, I — consentimento</td>
            </tr>
            <tr>
              <td>Analytics</td>
              <td>Art. 7º, IX — legítimo interesse</td>
            </tr>
            <tr>
              <td>Segurança</td>
              <td>Art. 7º, IX — legítimo interesse</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section id="whatsapp">
        <h2>5. Integração com WhatsApp</h2>
        <p>Ao vincular seu número de WhatsApp à conta Grivy, você autoriza que:</p>
        <ul>
          <li>Seu número seja armazenado e associado à sua conta</li>
          <li>As mensagens enviadas ao bot Grivy sejam processadas para extrair dados financeiros</li>
          <li>O conteúdo passe pela API do WhatsApp Business (Meta) e, quando necessário, pela OpenAI</li>
          <li>Os dados financeiros extraídos sejam registrados em sua conta</li>
        </ul>
        <div class="legal-warning">
          Não envie senhas ou PINs bancários pelo WhatsApp. A integração é destinada apenas ao registro de transações.
        </div>
      </section>

      <section id="compartilhamento">
        <h2>6. Compartilhamento de dados</h2>
        <table class="legal-table">
          <thead>
            <tr>
              <th>Fornecedor</th>
              <th>Finalidade</th>
              <th>País</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Railway / Netlify</td>
              <td>Hospedagem</td>
              <td>EUA</td>
            </tr>
            <tr>
              <td>Meta Platforms</td>
              <td>WhatsApp API</td>
              <td>EUA</td>
            </tr>
            <tr>
              <td>OpenAI</td>
              <td>Extração de dados (fallback)</td>
              <td>EUA</td>
            </tr>
            <tr>
              <td>Resend / Brevo</td>
              <td>Envio de e-mails</td>
              <td>EUA/França</td>
            </tr>
          </tbody>
        </table>
        <p><strong>Não vendemos, alugamos ou cedemos seus dados pessoais a terceiros.</strong></p>
      </section>

      <section id="retencao">
        <h2>7. Retenção de dados</h2>
        <ul>
          <li><strong>Conta ativa:</strong> mantemos seus dados enquanto você usar a Grivy</li>
          <li><strong>Após exclusão:</strong> dados financeiros excluídos em até 30 dias</li>
          <li><strong>Logs de segurança:</strong> mantidos por até 12 meses por obrigação legal</li>
          <li><strong>Mensagens WhatsApp:</strong> conteúdo bruto por 90 dias; dados extraídos enquanto a conta existir</li>
        </ul>
      </section>

      <section id="direitos">
        <h2>8. Seus direitos (LGPD)</h2>
        <ul>
          <li><strong>Acesso</strong> — solicitar cópia dos seus dados</li>
          <li><strong>Correção</strong> — corrigir dados incorretos</li>
          <li><strong>Eliminação</strong> — solicitar exclusão dos seus dados</li>
          <li><strong>Portabilidade</strong> — receber seus dados em CSV/JSON</li>
          <li><strong>Revogação</strong> — cancelar e-mails de marketing</li>
          <li><strong>Oposição</strong> — opor-se a tratamentos por legítimo interesse</li>
        </ul>
        <p>
          Envie solicitações para
          <a href="mailto:privacidade@grivy.app">privacidade@grivy.app</a>.
          Respondemos em até <strong>15 dias úteis</strong>.
        </p>
      </section>

      <section id="seguranca">
        <h2>9. Segurança</h2>
        <ul>
          <li>Senhas armazenadas com hash (bcrypt)</li>
          <li>Comunicação via HTTPS/TLS em todas as requisições</li>
          <li>Tokens de autenticação com expiração</li>
          <li>Rate limiting em endpoints sensíveis</li>
          <li>Assinatura HMAC-SHA256 validada nos webhooks do WhatsApp</li>
          <li>Row Level Security no banco de dados</li>
        </ul>
      </section>

      <section id="contato">
        <h2>10. Contato</h2>
        <p>
          Dúvidas sobre privacidade:<br>
          <a href="mailto:privacidade@grivy.app">privacidade@grivy.app</a>
        </p>
      </section>
    </div>
  </div>
</template>
