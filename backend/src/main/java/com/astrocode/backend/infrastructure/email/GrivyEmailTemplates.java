package com.astrocode.backend.infrastructure.email;

/**
 * Templates HTML e texto plano para e-mail marketing (design alinhado ao app: Inter, tons primários).
 */
public final class GrivyEmailTemplates {

    private GrivyEmailTemplates() {
    }

    public record HtmlTextPair(String html, String text) {
    }

    private static final String FOOTER = """

            —
            Grivy · grivy.netlify.app
            Para cancelar e-mails de marketing, use as configurações da conta.
            """;

    private static HtmlTextPair pair(String title, String bodyHtml, String bodyText) {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="UTF-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
                <title>%s</title></head>
                <body style="margin:0;font-family:Inter,Segoe UI,Helvetica,Arial,sans-serif;background:#f6f7fb;color:#1a1a1a;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f6f7fb;padding:24px 12px;">
                <tr><td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="max-width:600px;background:#ffffff;border-radius:16px;overflow:hidden;border:1px solid #e8eaf0;">
                <tr><td style="padding:24px 28px;background:linear-gradient(135deg,#1976d2 0%%,#0d47a1 100%%);color:#fff;">
                <div style="font-size:20px;font-weight:700;">Grivy</div>
                <div style="opacity:.9;font-size:13px;margin-top:4px;">Controle financeiro sem complicação</div>
                </td></tr>
                <tr><td style="padding:28px 28px 32px;font-size:15px;line-height:1.6;">
                %s
                </td></tr>
                <tr><td style="padding:16px 28px 24px;font-size:12px;color:#6b7280;border-top:1px solid #eef0f4;">
                Descadastro nas configurações da conta.
                </td></tr>
                </table>
                </td></tr></table>
                </body></html>
                """.formatted(title, bodyHtml);
        return new HtmlTextPair(html, bodyText + FOOTER);
    }

    public static String welcomeSubject(String firstName) {
        return "Bem-vindo(a) à Grivy, " + firstName + "! Seu controle financeiro começa agora";
    }

    public static HtmlTextPair welcome(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, <strong>%s</strong>!</p>
                <p>Sua conta está pronta. Três benefícios:</p>
                <ul style="padding-left:20px;margin:12px 0;">
                <li>📊 Visão clara de receitas, despesas e saldo em um painel único.</li>
                <li>🎯 Metas e categorias sem planilhas complexas.</li>
                <li>🔒 Dados isolados por conta.</li>
                </ul>
                <p style="margin:24px 0;">
                <a href="%s/dashboard" style="display:inline-block;background:#1976d2;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600;">Acessar minha conta</a>
                </p>
                <p style="font-size:14px;color:#4b5563;">Primeiro passo: cadastre <strong>sua primeira transação</strong>.</p>
                <p style="margin-top:16px;text-align:center;"><img src="https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=600&q=80" alt="Finanças" width="100%%" style="max-width:520px;border-radius:12px;" /></p>
                """.formatted(firstName, appUrl);
        String bodyText = """
                Olá, %s! Sua conta está pronta. Acesse %s/dashboard
                Primeiro passo: cadastre sua primeira transação.
                """.formatted(firstName, appUrl);
        return pair("Bem-vindo", bodyHtml, bodyText);
    }

    public static String onboardingD1Subject() {
        return "Você sabia que 78% das pessoas não sabem para onde vai o dinheiro?";
    }

    public static HtmlTextPair onboardingD1(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>78%% das pessoas não sabem para onde vai o dinheiro — comece pelo <strong>dashboard de categorias</strong>.</p>
                <p><a href="%s/dashboard" style="color:#1976d2;font-weight:600;">Abrir Grivy</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Olá, " + firstName + "! Categorias em " + appUrl + "/dashboard";
        return pair("Onboarding dia 1", bodyHtml, bodyText);
    }

    public static String onboardingD3Subject() {
        return "Como criar metas financeiras que você realmente vai cumprir";
    }

    public static HtmlTextPair onboardingD3(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>Metas específicas e mensuráveis funcionam melhor. Use as <strong>metas de economia</strong> no Grivy.</p>
                <p><a href="%s/dashboard" style="color:#1976d2;font-weight:600;">Ir para metas</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Olá, " + firstName + "! Metas em " + appUrl + "/dashboard";
        return pair("Onboarding dia 3", bodyHtml, bodyText);
    }

    public static String onboardingD7Subject() {
        return "Seu resumo da primeira semana na Grivy";
    }

    public static HtmlTextPair onboardingD7(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>Revise saldo e categorias — pequenos ajustes geram grandes resultados.</p>
                <p>Plano Pro com <strong>30%% OFF</strong> para os primeiros usuários (promoção ilustrativa — veja valores atualizados no app).</p>
                <p><a href="%s/dashboard/planos" style="display:inline-block;background:#1976d2;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600;">Ver planos</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Olá, " + firstName + "! Resumo da semana. Planos: " + appUrl + "/dashboard/planos";
        return pair("Onboarding dia 7", bodyHtml, bodyText);
    }

    public static String reactivationSubject(String firstName) {
        return firstName + ", seu dinheiro está te chamando";
    }

    public static HtmlTextPair reactivation(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>Atualize o Grivy e recupere a visão do mês em minutos.</p>
                <p><a href="%s/dashboard" style="display:inline-block;background:#1976d2;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600;">Voltar para a Grivy</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Olá, " + firstName + "! Volte ao app: " + appUrl + "/dashboard";
        return pair("Reativação", bodyHtml, bodyText);
    }

    public static String openFinanceWaitlistSubject() {
        return "Você está na fila VIP do Open Finance da Grivy!";
    }

    public static HtmlTextPair openFinanceWaitlist(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>Confirmamos sua entrada na <strong>lista de espera do Open Finance</strong>.</p>
                <p>Avisaremos por e-mail quando houver disponibilidade (prazo estimado: conforme fila e homologação).</p>
                <p><a href="%s/dashboard/open-finance" style="color:#1976d2;">Open Finance</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Lista VIP Open Finance confirmada. " + appUrl + "/dashboard/open-finance";
        return pair("Lista VIP", bodyHtml, bodyText);
    }

    public static String openFinanceReleasedSubject() {
        return "Chegou a sua vez! Open Finance está disponível para você";
    }

    public static HtmlTextPair openFinanceReleased(String firstName, String appUrl) {
        String bodyHtml = """
                <p>Olá, %s!</p>
                <p>O <strong>Open Finance</strong> foi liberado para você.</p>
                <ol style="padding-left:20px;">
                <li>Abra Open Finance no Grivy.</li>
                <li>Conecte sua instituição com fluxo seguro.</li>
                <li>Aguarde a sincronização.</li>
                </ol>
                <p><a href="%s/dashboard/open-finance" style="display:inline-block;background:#1976d2;color:#fff;text-decoration:none;padding:12px 22px;border-radius:999px;font-weight:600;">Ativar Open Finance agora</a></p>
                """.formatted(firstName, appUrl);
        String bodyText = "Open Finance liberado. " + appUrl + "/dashboard/open-finance";
        return pair("Open Finance", bodyHtml, bodyText);
    }
}
