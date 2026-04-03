# ANS Web Crawler

Sistema de automação desenvolvido em **Groovy** para captura de dados regulatórios da Agência Nacional de Saúde Suplementar (ANS), processamento de tabelas TISS e distribuição de relatórios via e-mail.

 ## Funcionalidades

- **Scraping de Documentos:**  download automático do Padrão TISS (versão Março/2026).
- **Processamento de Dados:** Conversão automática do histórico de componentes (desde 2016) para formato **CSV**.
- **Gestão de Tabelas:** Download da Tabela de Erros de Envio para a ANS.
- **Distribuição:** Sistema de e-mail integrado para envio de pacote `.zip` com os dados coletados.

##  Tecnologias

- **Linguagem:** Groovy
- **Integração HTTP:** HttpBuilder-NG
- **Parser HTML:** JSoup
- **E-mail:** Jakarta Mail
- **Persistência:** JSON (para gestão de e-mails)

##  Configuração e Variáveis de Ambiente

Para manter a segurança da sua conta (especialmente ao usar Gmail), o projeto utiliza variáveis de ambiente para as credenciais, salvas como EMAIL_USER e EMAIL_PASS
para que o sistema de email funcione  elas devem ser configuradas localmente

```bash
# Execute estes comandos no terminal antes de rodar o projeto:
export EMAIL_USER='seu-email@gmail.com'
export EMAIL_PASS='sua-senha-de-app-de-16-digitos'
