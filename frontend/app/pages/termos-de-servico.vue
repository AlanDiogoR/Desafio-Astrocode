<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

useSeoMeta({
  title: 'Termos de Serviço — Grivy',
  description: 'Termos e condições de uso da plataforma Grivy.',
  ogTitle: 'Termos de Serviço — Grivy',
  ogDescription: 'Termos e condições de uso da plataforma Grivy.',
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

      <h1>Termos de Serviço</h1>
      <p class="legal-subtitle">
        Última atualização: <strong>Janeiro de 2025</strong> · Versão 1.0
      </p>

      <nav class="legal-toc">
        <h3>Índice</h3>
        <ol>
          <li><a href="#aceitacao">Aceitação dos termos</a></li>
          <li><a href="#descricao">Descrição do serviço</a></li>
          <li><a href="#conta">Criação e segurança da conta</a></li>
          <li><a href="#planos">Planos e pagamentos</a></li>
          <li><a href="#whatsapp">Uso do WhatsApp</a></li>
          <li><a href="#uso-aceitavel">Uso aceitável</a></li>
          <li><a href="#propriedade">Propriedade intelectual</a></li>
          <li><a href="#cancelamento">Cancelamento e rescisão</a></li>
          <li><a href="#limitacao">Limitação de responsabilidade</a></li>
          <li><a href="#lei-aplicavel">Lei aplicável</a></li>
          <li><a href="#contato">Contato</a></li>
        </ol>
      </nav>

      <section id="aceitacao">
        <h2>1. Aceitação dos termos</h2>
        <p>
          Ao criar uma conta na Grivy, você declara ter lido e aceito estes Termos de
          Serviço e a nossa
          <NuxtLink to="/politica-de-privacidade">Política de Privacidade</NuxtLink>.
        </p>
        <p>
          Para usar a Grivy, você deve ter no mínimo <strong>18 anos de idade</strong>
          ou a maioridade legal na sua jurisdição.
        </p>
      </section>

      <section id="descricao">
        <h2>2. Descrição do serviço</h2>
        <p>
          A Grivy é um aplicativo web de <strong>controle financeiro pessoal</strong>.
          Oferecemos ferramentas para registrar transações, categorizar despesas,
          acompanhar metas de economia e gerenciar cartões de crédito.
        </p>
        <div class="legal-warning">
          A Grivy <strong>NÃO é</strong> uma instituição financeira, corretora de
          valores ou empresa de consultoria financeira. Não prestamos assessoria de
          investimentos nem intermediamos operações financeiras.
        </div>
        <p>
          Funcionalidades como integração com WhatsApp e Open Finance estão disponíveis
          em planos específicos, conforme descrito na
          <NuxtLink to="/planos">página de planos</NuxtLink>.
        </p>
      </section>

      <section id="conta">
        <h2>3. Criação e segurança da conta</h2>
        <ul>
          <li>Você é responsável por manter a confidencialidade da sua senha</li>
          <li>É proibido criar múltiplas contas para a mesma pessoa</li>
          <li>Os dados cadastrais informados devem ser verdadeiros e atualizados</li>
          <li>
            Notifique-nos imediatamente em caso de uso não autorizado da sua conta
            pelo e-mail
            <a href="mailto:contato@grivy.app">contato@grivy.app</a>
          </li>
        </ul>
      </section>

      <section id="planos">
        <h2>4. Planos e pagamentos</h2>
        <ul>
          <li>
            O <strong>plano gratuito</strong> oferece funcionalidades básicas com limites
            de uso (contas, transações e metas)
          </li>
          <li>
            Os <strong>planos pagos</strong> (Pro Mensal, Pro Semestral e Elite Anual)
            são cobrados antecipadamente e desbloqueiam funcionalidades ilimitadas
          </li>
          <li>
            Você tem <strong>direito de arrependimento de 7 dias</strong> após a compra,
            conforme o Art. 49 do Código de Defesa do Consumidor (CDC)
          </li>
          <li>
            <strong>Não armazenamos dados de cartão de crédito</strong> — o pagamento é
            processado integralmente por uma processadora terceira (Mercado Pago)
          </li>
          <li>
            Os valores podem ser alterados com aviso prévio de 30 dias; assinaturas
            vigentes mantêm o preço até o vencimento
          </li>
        </ul>
      </section>

      <section id="whatsapp">
        <h2>5. Uso do WhatsApp</h2>
        <p>
          A integração com WhatsApp está disponível em planos selecionados e permite
          registrar transações financeiras por mensagem.
        </p>
        <ul>
          <li>
            O uso é <strong>exclusivo para transações financeiras pessoais</strong>
          </li>
          <li>
            A integração está sujeita aos
            <a href="https://www.whatsapp.com/legal/terms-of-service" target="_blank" rel="noopener">
              Termos de Serviço da Meta Platforms
            </a>
          </li>
          <li>
            Mensagens são processadas pela API do WhatsApp Business e, opcionalmente,
            pela OpenAI para extração de dados financeiros
          </li>
          <li>Não envie dados bancários sensíveis (senhas, PINs, tokens) pelo chat</li>
        </ul>
      </section>

      <section id="uso-aceitavel">
        <h2>6. Uso aceitável (proibições)</h2>
        <p>Ao usar a Grivy, você concorda em <strong>não</strong>:</p>
        <ul>
          <li>Utilizar a plataforma para atividades ilegais ou fraudulentas</li>
          <li>Tentar acessar dados ou contas de outros usuários</li>
          <li>Realizar scraping, DDoS, engenharia reversa ou qualquer ataque ao sistema</li>
          <li>Revender ou redistribuir o acesso à plataforma</li>
          <li>Criar contas automatizadas (bots) sem autorização prévia</li>
          <li>Enviar spam ou conteúdo malicioso pelo WhatsApp</li>
        </ul>
      </section>

      <section id="propriedade">
        <h2>7. Propriedade intelectual</h2>
        <ul>
          <li>
            O código-fonte, design, marca e conteúdo da plataforma são
            <strong>propriedade da Grivy</strong> e estão protegidos pela legislação
            brasileira de propriedade intelectual
          </li>
          <li>
            Os <strong>dados financeiros</strong> que você insere na plataforma
            <strong>pertencem a você</strong> — você pode exportá-los ou solicitar
            sua exclusão a qualquer momento
          </li>
        </ul>
      </section>

      <section id="cancelamento">
        <h2>8. Cancelamento e rescisão</h2>
        <ul>
          <li>
            Você pode <strong>cancelar sua conta a qualquer momento</strong>, sem multas
            ou penalidades
          </li>
          <li>
            Ao cancelar um plano pago, o acesso às funcionalidades Pro continua até o
            final do período já pago
          </li>
          <li>
            A Grivy pode encerrar ou suspender sua conta com aviso prévio de
            <strong>30 dias</strong>, salvo em casos de violação destes termos
          </li>
          <li>
            Para solicitar exclusão completa dos seus dados, acesse a
            <NuxtLink to="/exclusao-de-dados">página de exclusão de dados</NuxtLink>
          </li>
        </ul>
      </section>

      <section id="limitacao">
        <h2>9. Limitação de responsabilidade</h2>
        <ul>
          <li>
            A Grivy é uma ferramenta de <strong>organização financeira</strong> — não nos
            responsabilizamos por decisões financeiras tomadas com base nas informações
            exibidas na plataforma
          </li>
          <li>
            Nossa responsabilidade total é limitada ao <strong>valor pago nos últimos
            12 meses</strong> de assinatura
          </li>
          <li>
            Não garantimos disponibilidade ininterrupta do serviço, embora nos
            esforcemos para manter uptime acima de 99%
          </li>
          <li>
            Integrações com terceiros (Meta, OpenAI, Mercado Pago) estão sujeitas
            aos termos e disponibilidade dos respectivos fornecedores
          </li>
        </ul>
      </section>

      <section id="lei-aplicavel">
        <h2>10. Lei aplicável</h2>
        <p>
          Estes Termos de Serviço são regidos pela <strong>legislação brasileira</strong>.
          Fica eleito o foro da comarca de <strong>Curitiba — PR</strong> para dirimir
          quaisquer controvérsias, com renúncia a qualquer outro, por mais privilegiado
          que seja.
        </p>
      </section>

      <section id="contato">
        <h2>11. Contato</h2>
        <p>
          Dúvidas ou reclamações:<br>
          <a href="mailto:contato@grivy.app">contato@grivy.app</a>
        </p>
      </section>
    </div>
  </div>
</template>
