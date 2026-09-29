# Especificação do Sistema de Agendamento de Espaços (Eventos/Calendário)

## 1. Visão Geral da Solução
A abordagem ideal para lidar com recorrências e exceções (como um evento que ocorre toda semana, mas que não ocorre em julho ou que muda de dia em um feriado) é adotar o padrão de **Série de Eventos e Ocorrências**:
- O líder cria a "Série" (o Evento base e a regra de recorrência).
- O sistema gera as "Ocorrências" (instâncias individuais no calendário para o próximo período, ex: 1 ano).
- O líder (ou administrador) pode visualizar o calendário e alterar/cancelar ocorrências individuais livremente, o que cria as "exceções" de forma simples.

## 2. Banco de Dados e Entidades (Migração `V8__Adicionar_Agendamento_Espacos.sql`)

Precisaremos criar 3 novas entidades principais:

1. **`Espaco` (Tabela `espacos`)**
   - `id` (PK)
   - `nome` (String - ex: "Auditório Principal", "Sala 2")
   - `capacidade` (Integer)
   - `descricao` (String)

2. **`Evento` (Tabela `eventos` - A definição da série)**
   - `id` (PK)
   - `titulo` (String)
   - `descricao` (Text)
   - `ministerio_id` (FK para `ministerios`)
   - `is_recorrente` (Boolean)
   - `regra_recorrencia` (String - Ex: formato padrão Cron ou RRULE do iCal)
   - `apoio_necessario` (Text - Equipamentos, suporte de mídia, etc.)
   - `estimativa_participantes` (Integer)

3. **`EventoOcorrencia` (Tabela `evento_ocorrencias` - As instâncias no calendário)**
   - `id` (PK)
   - `evento_id` (FK para `eventos`)
   - `espaco_id` (FK para `espacos`) *(Permite que uma ocorrência específica mude de sala se houver conflito)*
   - `data_inicio` (Timestamp)
   - `data_fim` (Timestamp)
   - `status` (Enum: `PENDENTE`, `APROVADO`, `CONFLITO`, `CANCELADO`)
   - `observacao` (String - Motivo de alteração pontual, etc.)

## 3. Lógica de Back-end (Services e Controllers)

1. **`AgendamentoService`**:
   - **Geração de Ocorrências**: Ao salvar um `Evento`, o service lê a data inicial e a regra de recorrência, gerando e salvando registros na tabela `evento_ocorrencias`.
   - **Verificação de Conflitos**: Um método que busca `EventoOcorrencia` com `status != CANCELADO`, checando interseção de `data_inicio` e `data_fim` para o mesmo `espaco_id`. Se houver choque, a nova ocorrência (ou a menos prioritária) entra no status `CONFLITO`.

2. **`GCalExportService`**:
   - Uma nova funcionalidade para exportar um feed `.ics` estático acessível por um link que o Google Calendar pode assinar, apenas com ocorrências de status `APROVADO`.

3. **Controllers (Rotas)**:
   - `GET /admin/espacos` (CRUD de Espaços - apenas Admin)
   - `GET /admin/agendamentos/calendario` (View principal com a grade do calendário)
   - `GET /admin/agendamentos/novo` (Formulário de criação)
   - `GET /admin/agendamentos/conflitos` (Dashboard de gestão para Pastores/Admins)
   - `POST /admin/agendamentos/resolver-conflito` (Endpoint para aceitar, mudar sala, ou mudar horário de um evento conflitante)

## 4. Front-end e UX (Aproveitando Layout e Thymeleaf)

Integraremos tudo na área logada (aproveitando o `fragments.html` e os estilos CSS existentes baseados na paleta do projeto).

- **Menu de Navegação**: Adicionar a nova opção "📅 Agendamentos" no fragmento de navbar.
- **Formulário de Agendamento (`agendamentos-form.html`)**:
  - Seleção do `Ministerio` do líder logado.
  - Campos de Título, Espaço, Datas Iniciais.
  - Configuração de Recorrência (Não repete, Diário, Semanal, Mensal).
- **Visão de Calendário (`agendamentos-calendario.html`)**:
  - Integração com a biblioteca JavaScript **FullCalendar** (gratuita e leve).
  - O líder vê os eventos gerados na grade mensal/semanal.
  - Clicando em um evento gerado (ex: o de Julho), abre uma modal para **"Cancelar esta ocorrência"** ou **"Editar apenas esta ocorrência"** (mudando para quinta-feira no caso de um feriado).
- **Tela de Resolução de Conflitos (`agendamentos-conflitos.html` - Restrita para Admin/Pastores)**:
  - Uma tabela que lista lado a lado os eventos sobrepostos.
  - Botões de ação rápida para cada linha: *[Mudar Espaço]*, *[Mudar Horário]*, *[Manter e Ignorar]* (caso sejam eventos compatíveis no mesmo grande espaço) ou *[Rejeitar]*.
