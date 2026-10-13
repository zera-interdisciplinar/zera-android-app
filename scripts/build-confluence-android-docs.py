#!/usr/bin/env python3
"""Gera HTML para páginas Confluence do app Android. Uso: python3 scripts/build-confluence-android-docs.py"""
import json
from pathlib import Path

OUT = Path("/tmp/confluence-android")
OUT.mkdir(exist_ok=True)

TOC = (
    '<div data-type="extension" data-extension-key="toc" '
    'data-extension-type="com.atlassian.confluence.macro.core" '
    'data-parameters=\'{"macroParams":{"maxLevel":{"value":"3"}},'
    '"macroMetadata":{"schemaVersion":{"value":"1"},"title":"Table of Contents"}}\'></div>'
)


def esc(s: str) -> str:
    return (
        s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
    )


def h2(t): return f"<h2>{esc(t)}</h2>"
def h3(t): return f"<h3>{esc(t)}</h3>"
def h4(t): return f"<h4>{esc(t)}</h4>"
def p(t): return f"<p>{t}</p>"
def ul(items): return "<ul>" + "".join(f"<li>{x}</li>" for x in items) + "</ul>"


def screen_block(title, meta: dict):
    """meta: route, screen, vm, scaffold, api, ui[], actions[], empty, errors, notes"""
    rows = ""
    for label, val in [
        ("Rota", f"<code>{meta['route']}</code>"),
        ("Composable", f"<code>{meta['screen']}</code>"),
        ("ViewModel", f"<code>{meta.get('vm', '—')}</code>"),
        ("Shell / layout", meta.get("scaffold", "—")),
        ("APIs principais", meta.get("api", "—")),
    ]:
        rows += f"<tr><th>{label}</th><td>{val}</td></tr>"
    body = f"<h3>{esc(title)}</h3><table><tbody>{rows}</tbody></table>"
    if meta.get("ui"):
        body += "<h4>O que aparece na tela</h4><ul>" + "".join(f"<li>{x}</li>" for x in meta["ui"]) + "</ul>"
    if meta.get("actions"):
        body += "<h4>Ações do usuário</h4><ul>" + "".join(f"<li>{x}</li>" for x in meta["actions"]) + "</ul>"
    if meta.get("empty"):
        body += "<h4>Estados vazios / falha</h4><ul>" + "".join(f"<li>{x}</li>" for x in meta["empty"]) + "</ul>"
    if meta.get("notes"):
        body += f"<div data-type=\"panel-note\"><p>{meta['notes']}</p></div>"
    return body


# --- Catálogo gestor + auth ---
auth_gestor = [
    screen_block(
        "Splash",
        {
            "route": "Route.Splash",
            "screen": "SplashScreen",
            "vm": "SplashViewModel",
            "scaffold": "Tela cheia, logo com shared transition",
            "api": "<code>Boot</code> → Scrapy <code>POST /v1/boot</code>; <code>RestoreSession</code> se houver tokens",
            "ui": [
                "Logo Zera animado",
                "Sem inputs — carregamento automático",
            ],
            "actions": [
                "Após boot: se sessão válida → home do <code>role</code>; senão → <code>Welcome</code>",
                "Falha de boot: permanece ou redireciona conforme implementação do ViewModel",
            ],
        },
    ),
    screen_block(
        "Boas-vindas",
        {
            "route": "Route.Welcome",
            "screen": "WelcomeScreen",
            "vm": "(sem ViewModel — exceção arquitetural)",
            "scaffold": "Coluna central, botões primários",
            "api": "Nenhuma",
            "ui": ["Logo", "Botão Entrar", "Botão Criar conta"],
            "actions": [
                "Entrar → <code>SignIn</code>",
                "Criar conta → <code>SignUp</code>",
            ],
        },
    ),
    screen_block(
        "Login",
        {
            "route": "Route.SignIn",
            "screen": "SignInScreen",
            "vm": "SingInViewModel",
            "scaffold": "Auth layout + shared transition logo",
            "api": "<code>SingIn</code> → <code>POST auth/login</code>, <code>GetSelfUser</code>, <code>LoadFlags</code>, <code>RegisterDeviceToken</code>",
            "ui": [
                "Campo e-mail",
                "Campo senha",
                "Botão Entrar (desabilitado durante loading)",
                "Mensagem de erro abaixo dos campos",
            ],
            "actions": [
                "Entrar com credenciais → <code>pushAndPopAll(ManagerHome|EmployeeHome)</code> + <code>consumePendingAlertDeepLink()</code>",
                "Role desconhecido → limpa sessão, erro \"Perfil inválido…\"",
                "Campos vazios → \"Email and password cannot be empty\" sem API",
            ],
            "empty": ["Erro de rede/servidor: texto da exceção na UI"],
        },
    ),
    screen_block(
        "Cadastro por convite",
        {
            "route": "Route.SignUp",
            "screen": "SignUpScreen",
            "vm": "SignUpViewModel",
            "api": "<code>InvitationUseCase</code> → <code>POST invitations/redeem</code>; depois login automático",
            "ui": ["Código convite", "Nome", "E-mail", "Senha", "Confirmar senha"],
            "actions": ["Sucesso → mesma pilha limpa que login"],
            "notes": "Não existe <code>POST /users</code> no app; entrada de usuário novo só por convite.",
        },
    ),
    screen_block(
        "Home do gestor",
        {
            "route": "Route.ManagerHome",
            "screen": "ManagerHomeScreen",
            "vm": "ManagerHomeViewModel",
            "scaffold": "ManagerScaffold — UpperNavBar \"Visão geral\", bottom nav, FAB (TODO chatbot), sidebar",
            "api": (
                "<code>GET dashboard/home</code> (ocupação, activeItems, change%, recentItems); "
                "<code>GET users/{id}</code>; <code>GET notifications/alerts?status=OPEN</code>; "
                "<code>GET users/count-by-manager</code> (equipe ativa)"
            ),
            "ui": [
                "Nome + cargo (Gestor)",
                "StockOccupationCard — contagem ativos + % ocupação",
                "Seção \"Resumo geral\": ShortcutCard Itens (total + seta % variação verde/vermelha)",
                "ShortcutCard Funcionários (contagem formatada pt-BR)",
                "Seção \"Alertas importantes\": NotificationList (descrição, cor por kind/severity)",
                "Seção \"Últimos Itens\": ProductList + link \"Ver Todos\"",
                "CaptionText vermelho se dashboard/home falhar",
            ],
            "actions": [
                "Itens / Ver todos → <code>Route.Itens()</code>",
                "Funcionários → <code>Route.Employees</code>",
                "Toque alerta → rota por <code>AlertKind</code> (Itens, Recycling, filtro pendente)",
                "Toque último item → <code>ItemDetails(itemId)</code>",
                "Menu hamburger → sidebar (Perfil, Colaboradores, Categorias, …)",
            ],
            "empty": [
                "Sem alertas OPEN → \"Nenhuma notificação\"",
                "Sem recentItems → \"Nenhum produto encontrado\"",
                "Falha GetSelfUser → nome vazio; resto continua",
                "Falha alertas → lista vazia silenciosa",
            ],
        },
    ),
    screen_block(
        "Indicadores (gestor)",
        {
            "route": "Route.Indexes",
            "screen": "IndexesScreen",
            "vm": "IndexesViewModel",
            "scaffold": "ManagerScaffold, aba Indicadores",
            "api": "<code>GET dashboard/indicators</code> (from/to opcional yyyy-MM-dd)",
            "ui": ["Filtro/período quando implementado na UI", "Cards/gráficos de indicadores de inventário"],
            "actions": ["Navegação via bottom nav"],
            "notes": "Ver spec <code>docs/07-especificacoes/indexes.md</code>.",
        },
    ),
    screen_block(
        "Colaboradores",
        {
            "route": "Route.Employees",
            "screen": "EmployeesScreen",
            "vm": "EmployeesViewModel",
            "scaffold": "ManagerScaffold, voltar ativo",
            "api": "ListTeamEmployees, ListPendingInvitations, CreateInvitation, CountActiveEmployees",
            "ui": [
                "ZeraBox azul: \"Equipe ativa\" + contagem + botão Novo",
                "CreateInviteForm (quando aberto)",
                "InviteCard convites pendentes (código copiável)",
                "EmployeeList — colaboradores ativos",
            ],
            "actions": [
                "Novo → exibe formulário convite",
                "Voltar → goBack",
                "Copiar código convite → clipboard",
            ],
            "empty": ["errorMessage em CaptionText se falhar carga/convite"],
        },
    ),
    screen_block(
        "Lista de itens (gestor)",
        {
            "route": "Route.Itens(status?)",
            "screen": "ItensScreen",
            "vm": "ItensViewModel",
            "scaffold": "ManagerScaffold, scrollable=false",
            "api": "<code>GET items</code> paginado + <code>GET categories</code>; query status/categoryId/q",
            "ui": [
                "ZeraSearchInput — placeholder \"Pesquisar por ID ou Nome…\"",
                "Ícone filtros → BottomSheet",
                "ZeraChipsGroup chips de filtros aplicados",
                "Contagem total formatada",
                "ProductList com chip de status colorido por ItemStatus",
                "Scroll infinito (loadNextPage)",
            ],
            "actions": [
                "Buscar → reload page 0",
                "Filtro status (labels UI → API enum) e categorias",
                "Toque item → <code>ItemDetails</code>",
            ],
            "empty": ["Lista vazia → mensagem padrão ProductList", "Erro → errorMessage"],
            "notes": "Labels de status incluem \"Em manutenção\", \"Pendente\", etc. (ver ItensViewModel.STATUS_API_VALUES).",
        },
    ),
    screen_block(
        "Detalhe do item (PDI)",
        {
            "route": "Route.ItemDetails(itemId)",
            "screen": "ItemDetailsScreen",
            "vm": "ItemDetailsViewModel",
            "scaffold": "Scaffold standalone + UpperNavBar \"Detalhes do Item\"",
            "api": "GET item; PATCH name/condition/serial/notes; POST approve/reject",
            "ui": [
                "ItemSummaryCard — nome, subtítulo (modelo), chip status",
                "Linhas: categoria, material, condição, serial, observações, cadastrado por, data",
                "EditableFieldRow com lápis onde permitido",
                "Bottom: Recusar + Aprovar (se canReview); Editar (popup)",
                "Popups: editar campos, motivo recusa",
            ],
            "actions": [
                "Aprovar → ItemApproved + pushAndPop",
                "Recusar → motivo obrigatório ≤500 chars",
                "Editar → PATCH parcial",
                "Voltar → goBack",
            ],
            "empty": ["404 → errorMessage", "Loading desabilita ações"],
            "notes": (
                "<code>canReview</code> = gestor E status <code>PENDING_APPROVAL</code> ou "
                "<code>AWAITING_EVALUATION</code>. Operário vê tela mas sem aprovar/recusar."
            ),
        },
    ),
    screen_block(
        "Item aprovado (confirmação)",
        {
            "route": "Route.ItemApproved(...)",
            "screen": "ItemApprovedScreen",
            "vm": "ItemApprovedViewModel",
            "ui": ["Confirmação visual pós-aprovação", "Dados do item na rota"],
            "actions": ["Continuar / voltar conforme botões da tela"],
        },
    ),
    screen_block(
        "Reciclagem — mapa",
        {
            "route": "Route.Recycling",
            "screen": "RecyclingScreen",
            "vm": "RecyclingViewModel / NearbyRecyclingPlacesViewModel",
            "scaffold": "ManagerScaffold ou EmployeeScaffold conforme pilha",
            "api": "GetNearbyRecyclingPlaces (adm-core + Places)",
            "ui": ["Mapa", "Pins de recicladoras", "Lista/card de locais"],
            "actions": ["Selecionar local → ItensSelection com parâmetros do place"],
        },
    ),
    screen_block(
        "Descarte — seleção e confirmação",
        {
            "route": "ItensSelection → RecyclingResume → SchedulingSuccess",
            "screen": "ItensSelectionScreen, RecyclingResumeScreen, SchedulingSuccessScreen",
            "vm": "ItensSelectionViewModel, RecyclingResumeViewModel, SchedulingSuccessViewModel",
            "api": "<code>POST /disposals</code>; AI <code>POST /reports</code> para PDF",
            "ui": [
                "Lista itens elegíveis para descarte",
                "Resumo place + itens + pesos",
                "Tela sucesso com dados do descarte e contato recicladora",
            ],
            "actions": [
                "Confirmar descarte → registro com disposedAt ≤ hoje",
                "Gerar PDF quando disponível",
            ],
            "notes": "Descarte é registro de coleta já feita, não agendamento futuro.",
        },
    ),
    screen_block(
        "Histórico de descartes",
        {
            "route": "Route.ScheduledDisposals / SchedulingDetails",
            "screen": "ScheduledDisposalsScreen, SchedulingDetailsScreen",
            "vm": "ScheduledDisposalsViewModel, SchedulingDetailsViewModel",
            "api": "ListDisposalSchedules, GetDisposalSchedule, CancelDisposalSchedule (gestor)",
            "ui": ["Lista de descartes", "Detalhe com itens incluídos"],
            "actions": ["Gestor cancela → PATCH cancel; operário 403"],
        },
    ),
    screen_block(
        "Categorias e modelos (gestor)",
        {
            "route": "Categories, CategoryCreation, Models, ModelCreation, ModelDetails, ModelItems",
            "screen": "CategoriesScreen, ModelsScreen, …",
            "vm": "CategoriesViewModel, ModelsViewModel, ModelDetailsViewModel, …",
            "api": "CRUD categories/models; GET model items; approve/reject em cascata no modelo",
            "ui": [
                "Listagens com busca/paginação",
                "Formulários criação categoria/modelo",
                "Detalhe modelo + itens pendentes no modelo",
            ],
            "actions": [
                "Modelo criado por operário nasce PENDING; por gestor APPROVED",
                "PATCH name, manufacturer, materials no modelo",
            ],
            "notes": "Ver specs modelos.md, model-items.md.",
        },
    ),
]

operario_shared = [
    screen_block(
        "Home do operário",
        {
            "route": "Route.EmployeeHome",
            "screen": "EmployeeHomeScreen",
            "vm": "EmployeeHomeViewModel",
            "scaffold": "EmployeeScaffold",
            "api": "GetSelfUser; GetEmployeeHome (dashboard/home); GetEmployeePendencies (work-center)",
            "ui": [
                "Nome + cargo (Operário)",
                "StockOccupationCard",
                "Botões: Escanear (amarelo), Adicionar Modelo (verde)",
                "Atalhos: Manutenção (count inMaintenance), Descartes (disposalsInWindow)",
                "Pendências — cards agregados por bucket (não item a item)",
                "Últimos itens + Ver todos",
            ],
            "actions": [
                "Escanear → Scan",
                "Adicionar Modelo → Models",
                "Manutenção → EmployeeItems(status Em manutenção)",
                "Descartes → ScheduledDisposals",
                "Pendência → WorkingCentral",
                "Item recente → ItemDetails",
            ],
            "empty": ["Buckets vazios → Nenhuma notificação", "Falha home → zeros e lista vazia"],
        },
    ),
    screen_block(
        "Central de Trabalho",
        {
            "route": "Route.WorkingCentral",
            "screen": "WorkingCentralScreen",
            "vm": "WorkingCentralViewModel",
            "api": "<code>GET dashboard/work-center</code> (mesmo payload resumido na home)",
            "ui": [
                "SummaryCard \"Resumo do turno\" — \"N pendências · M em manutenção\"",
                "NotificationList item a item",
                "Ordem: drafts (amarelo), rejected (vermelho), damaged (azul)",
                "TODO: seção Em manutenção",
            ],
            "actions": [
                "Toque card resolvível → ItemDetails(itemId)",
                "Cards danificados sem destino: resolvable=false (sem navegação)",
            ],
            "notes": "Texto rascunho usa missingFields da API (foto não obrigatória ZERA-236).",
        },
    ),
    screen_block(
        "Scan",
        {
            "route": "Route.Scan",
            "screen": "ScanScreen",
            "vm": "ScanViewModel",
            "api": "<code>GET items/by-barcode/{code}</code>",
            "ui": [
                "ScanArea — preview câmera ou botão permissão",
                "Atalhos cadastro manual / modelo",
            ],
            "actions": [
                "200 → ItemDetails",
                "404 → ManualRegister(barcode=…)",
                "Debounce entre leituras",
            ],
            "notes": "Spec scan-item.md",
        },
    ),
    screen_block(
        "Cadastro manual",
        {
            "route": "Route.ManualRegister(modelName?, barcode?)",
            "screen": "ManualRegisterScreen",
            "vm": "ManualRegisterViewModel",
            "api": "GetModels; CreateItem → POST items (DRAFT)",
            "ui": [
                "Dropdown modelo (GET models)",
                "Barcode, serial, ano fab., data aquisição",
                "Condição, slider intensidade uso",
                "Possui danos? + tipo dano",
                "Observações opcional",
                "Concluir cadastro",
            ],
            "actions": [
                "canSubmit exige modelId + campos obrigatórios",
                "Sucesso → navegação de retorno / detalhe conforme fluxo",
            ],
            "notes": "Submit para fila pode ser passo separado (SubmitItem) conforme tela.",
        },
    ),
    screen_block(
        "Itens (operário)",
        {
            "route": "Route.EmployeeItems(status?)",
            "screen": "EmployeeItemsScreen",
            "vm": "Reutiliza padrão ItensViewModel / lista operário",
            "api": "GET items com filtros",
            "ui": ["Similar gestor com EmployeeScaffold", "Adicionar novo item → ManualRegister"],
            "actions": ["Filtros, busca, detalhe"],
        },
    ),
    screen_block(
        "Perfil (compartilhado)",
        {
            "route": "Route.Profile",
            "screen": "ProfileScreen",
            "vm": "ProfileViewModel",
            "api": "GetProfile, RenameUser, UpdateUserEmail, UploadAvatar, SaveTelephone, GetTelephone",
            "ui": ["Avatar", "Nome", "E-mail", "Telefone", "Ações editar"],
            "actions": ["Upload foto", "Salvar alterações adm-core"],
            "notes": "Acessível sidebar gestor e operário.",
        },
    ),
]

flows_deep = (
    h2("Fluxos passo a passo")
    + h3("Autenticação e sessão")
    + p(
        "Sequência completa desde cold start até home autenticada, incluindo refresh silencioso."
    )
    + """<ol>
<li><strong>Splash</strong> — <code>Boot.execute()</code> chama Scrapy <code>POST /v1/boot</code>. Resposta grava <code>ms-adm-core-url</code>, keys e <code>ms-inventory-url</code> em <code>AppConfig</code>; inicializa <code>AdmCoreClient</code> e <code>InventoryClient</code>.</li>
<li><strong>Sessão existente</strong> — <code>RestoreSession</code> lê SQLite (<code>zera.db</code>): access/refresh token, userId, unitId. Se válido, pula Welcome e vai direto para home do role persistido.</li>
<li><strong>Login</strong> — usuário em <code>SignInScreen</code> → <code>POST auth/login</code> → persiste tokens → <code>GET users/{userId}</code> para <code>role</code> e unidade → <code>POST /v1/flags</code> com attrs (user_id, role, unit_id, app_version) → <code>POST users/me/device-tokens</code> (FCM).</li>
<li><strong>Pós-login</strong> — <code>pushAndPopAll</code> para <code>ManagerHome</code> ou <code>EmployeeHome</code>. Se push tinha <code>kind</code> de alerta, <code>consumePendingAlertDeepLink()</code> empilha rota alvo.</li>
<li><strong>401 em qualquer call autenticada</strong> — <code>TokenRefreshAuthenticator</code> usa refresh token; falha → logout implícito / erro na tela.</li>
</ol>"""
    + h3("Operário — completar cadastro incompleto")
    + """<ol>
<li>Back-end coloca item em <code>DRAFT</code> com <code>missingFields</code> após tentativa de submit incompleto, ou item criado e não submetido.</li>
<li><code>work-center.drafts</code> lista o item; home mostra contagem agregada; Central lista card \"Completar cadastro\" com texto \"Nome · faltam campo1, campo2\".</li>
<li>Operário toca card na Central → <code>ItemDetails</code> (desde fix recente: <code>onNotificationClick</code> navega se resolvable).</li>
<li>Operário edita campos via PDI ou fluxos de edição; chama <code>POST /items/{id}/submit</code> quando UI expõe (use case <code>SubmitItem</code> existe no model).</li>
<li>Submit operário → <code>PENDING_APPROVAL</code>; gestor vê na home (contador) e alertas OPEN.</li>
</ol>"""
    + h3("Gestor — aprovar cadastro de operário")
    + """<ol>
<li>Alerta <code>ITEM_PENDING_APPROVAL</code> ou lista filtrada → PDI.</li>
<li>PDI carrega <code>GET /items/{id}</code>; chip \"Pendente\"; botões Recusar/Aprovar visíveis (<code>canReview</code>).</li>
<li>Aprovar → <code>POST .../approve</code> → navega <code>ItemApprovedScreen</code> → item <code>IN_STOCK</code>.</li>
<li>Recusar → motivo → <code>POST .../reject</code> → operário vê em rejected no work-center.</li>
</ol>"""
    + h3("Descarte na recicladora")
    + """<ol>
<li>Usuário abre Reciclagem (aba bottom nav).</li>
<li>Mapa carrega locais próximos; seleciona pin.</li>
<li>Seleciona itens em estoque elegíveis; revisa resumo (place, endereço, nomes).</li>
<li>Confirma → transação inventory: itens → <code>DISPOSED</code>, eventos, vínculo disposal.</li>
<li> opcional PDF comprovante via AI core.</li>
<li>Histórico em Descartes agendados; gestor pode cancelar descarte (itens revertem estado anterior).</li>
</ol>"""
    + h3("Mapeamento status UI ↔ backend")
    + p("Chips usam <code>ItemStatus</code> em <code>view/components/outros/ItemStatus.kt</code>:")
    + """<table><thead><tr><th>Backend</th><th>Label UI</th><th>Cor família</th></tr></thead><tbody>
<tr><td>DRAFT</td><td>Rascunho</td><td>Blue</td></tr>
<tr><td>PENDING_APPROVAL</td><td>Pendente</td><td>Yellow</td></tr>
<tr><td>REJECTED</td><td>Recusado</td><td>Red</td></tr>
<tr><td>IN_STOCK</td><td>Aprovado</td><td>Green</td></tr>
<tr><td>IN_MAINTENANCE</td><td>Em manutenção</td><td>Yellow</td></tr>
<tr><td>AWAITING_EVALUATION</td><td>Em aprovação</td><td>Yellow</td></tr>
<tr><td>DISPOSED</td><td>Descartado</td><td>Red</td></tr>
</tbody></table>"""
    + h3("Alertas na home do gestor")
    + p("Origem: <code>GET /notifications/alerts?status=OPEN</code>. Toque usa mesmo mapa que push FCM:")
    + """<table><thead><tr><th>AlertKind</th><th>Destino</th></tr></thead><tbody>
<tr><td>STOCK_QUANTITY_LIMIT, TIME_IN_STOCK_LIMIT, STALE_ITEM, WARRANTY_EXPIRATION, LIFESPAN_EXPIRATION, USAGE_INTENSITY_LIMIT, PREDICTED_FAILURE</td><td>Lista Itens</td></tr>
<tr><td>RECYCLABLE_TO_LANDFILL</td><td>Reciclagem</td></tr>
<tr><td>ITEM_PENDING_APPROVAL</td><td>Itens filtro Pendente</td></tr>
<tr><td>ITEM_APPROVED, ITEM_REJECTED</td><td>Cor na lista; rota opcional null</td></tr>
</tbody></table>"""
    + p("Cores: ver <code>ManagerHomeViewModel.colorFamilyForAlert</code> — verde aprovação, amarelo pendência/tempo, azul estoque parado, vermelho rejeição/reciclável/predicted failure.")
)

hub = (
    TOC
    + '<div data-type="panel-info"><p><strong>Documentação do app Android Zera</strong> '
    + "(<code>com.zera.android</code>): produto, telas, fluxos e integração com back-end. "
    + "Complementa "
    + '<a href="https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55803906" data-card-appearance="inline"></a>. '
    + "Espelho no repositório: <code>docs/INDEX.md</code>.</p></div>"
    + p(
        "<strong>Última atualização:</strong> <time datetime=\"2026-10-13\">13 de outubro de 2026</time>. "
        "Esta wiki descreve o comportamento <em>implementado</em>; specs Given/When/Then ficam em <code>docs/07-especificacoes/</code>."
    )
    + h2("Estrutura desta documentação")
    + ul([
        '<strong>Esta página</strong> — visão, papéis, arquitetura, mapa de rotas, fluxos resumidos e links.',
        '<a href="https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/FLUXOS_ID" data-card-appearance="inline"></a> — fluxos passo a passo, estados, alertas.',
        '<a href="https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/CAT_GESTOR_ID" data-card-appearance="inline"></a> — catálogo de telas (auth + gestor).',
        '<a href="https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/CAT_EMP_ID" data-card-appearance="inline"></a> — catálogo (operário + compartilhado).',
        '<a href="https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/64061442" data-card-appearance="inline"></a> — stack, lacunas, matriz permissões, índice docs/.',
    ])
)

# Load existing main sections from v2 if present
main_extra = Path("/tmp/cf-main-v2.html")
if main_extra.exists():
    raw = main_extra.read_text(encoding="utf-8")
    # strip old toc/panel from cf-main-v2
    idx = raw.find("<h2>1. Visão do produto</h2>")
    if idx >= 0:
        hub += raw[idx:]
else:
    hub += p("(Regenerar cf-main-v2.html se seções 1–8 ausentes.)")

catalog_g = TOC + h2("Catálogo de telas — Autenticação e Gestor") + "".join(auth_gestor)
catalog_e = TOC + h2("Catálogo de telas — Operário e compartilhado") + "".join(operario_shared)
flows_page = TOC + flows_deep

for name, html in [
    ("hub.html", hub),
    ("catalog-gestor.html", catalog_g),
    ("catalog-operario.html", catalog_e),
    ("flows.html", flows_page),
]:
    (OUT / name).write_text(html, encoding="utf-8")
    print(name, len(html))

manifest = {
    "cloudId": "6084b518-981a-4e1c-afe3-364e2f26cc4e",
    "main": {"contentId": "63537175", "snapshotToken": "v:4", "file": "hub.html"},
    "annex": {"contentId": "64061442", "snapshotToken": "v:4", "file": None},
}
(OUT / "manifest.json").write_text(json.dumps(manifest, indent=2))
print("written to", OUT)
