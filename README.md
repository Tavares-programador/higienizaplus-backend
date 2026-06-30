# Higieniza+ Backend

Backend em **Java 21 + Spring Boot 3** para o site da Higieniza+. Recebe pedidos de orçamento do formulário (`contact.html`), salva no PostgreSQL, gera PDF da lista de preços, e oferece um painel admin (login + listagem de pedidos) protegido por JWT.

---

## 1. Pré-requisitos na sua máquina

- Java 21 (`java -version`)
- Maven (`mvn -version`)
- PostgreSQL já rodando localmente

## 2. Configurar o banco de dados

Rode uma vez, como superusuário do Postgres:

```bash
sudo -u postgres psql -f setup-database.sql
```

Isso cria o banco `higienizaplus` e o usuário `higienizaplus_user`. **Troque a senha** tanto no script quanto em `application.properties` antes de rodar em produção.

As tabelas (`orcamentos`, `admins`, `servicos_preco`) são criadas automaticamente pelo Flyway no primeiro boot da aplicação — não precisa criar nada manualmente além do banco/usuário.

## 3. Configurar `application.properties`

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.password=troque_esta_senha          # mesma senha do passo 2
app.jwt.secret=...                                     # troque por uma string longa e aleatória
app.admin.default-password=MudeEstaSenha123!           # senha do admin criado no primeiro boot
```

## 4. Rodar o backend

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

No primeiro boot, um usuário admin é criado automaticamente com as credenciais de `app.admin.default-username` / `app.admin.default-password`. **Troque a senha depois do primeiro login** (não há endpoint de troca de senha nesta versão — se quiser, gere um novo hash bcrypt e atualize direto no banco, ou me peça esse endpoint depois).

## 5. Acessar o painel admin

Abra no navegador:

```
http://localhost:8080/admin/index.html
```

Faça login com o usuário/senha definidos no passo 3. Você será redirecionado para `dashboard.html`, com a lista de pedidos, filtro por status, e ações de atualizar status / excluir.

---

## Endpoints da API

### Públicos (sem autenticação)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/orcamentos` | Cria um novo pedido de orçamento (usado pelo formulário do site) |
| `GET` | `/api/orcamentos/pdf` | Baixa o PDF com a lista de preços de todos os serviços |
| `GET` | `/api/precos` | Lista de preços em JSON |
| `POST` | `/api/auth/login` | Login do admin — devolve um token JWT |

### Protegidos (precisam de `Authorization: Bearer <token>`, role ADMIN)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/orcamentos` | Lista pedidos (paginado, filtro opcional `?status=NOVO`) |
| `GET` | `/api/orcamentos/{id}` | Detalhe de um pedido |
| `PATCH` | `/api/orcamentos/{id}/status?status=CONCLUIDO` | Atualiza o status (`NOVO`, `EM_ANDAMENTO`, `CONCLUIDO`, `CANCELADO`) |
| `DELETE` | `/api/orcamentos/{id}` | Remove um pedido |

---

## Exemplo de payload — `POST /api/orcamentos`

```json
{
  "nome": "Maria João",
  "email": "maria@email.com",
  "whatsapp": "+244923456789",
  "servico": "Lavagem de Sofás",
  "mensagem": "Tenho um sofá de 3 lugares, preciso de orçamento."
}
```

Resposta (`201 Created`):

```json
{
  "id": 1,
  "nome": "Maria João",
  "email": "maria@email.com",
  "whatsapp": "+244923456789",
  "servico": "Lavagem de Sofás",
  "mensagem": "Tenho um sofá de 3 lugares, preciso de orçamento.",
  "status": "NOVO",
  "criadoEm": "2026-06-28T10:00:00",
  "linkWhatsapp": "https://wa.me/244949943236?text=..."
}
```

O frontend pode usar `linkWhatsapp` para abrir o WhatsApp da empresa automaticamente, como já fazia antes — só que agora o pedido também fica salvo no banco e visível no painel admin.

---

## Integrando com o frontend atual (`contact.html`)

No `<script>` do `contact.html`, troque o handler do submit para enviar ao backend **e depois** abrir o WhatsApp (mantendo o comportamento atual):

```javascript
document.getElementById('orcamento-form').addEventListener('submit', async function (e) {
    e.preventDefault();

    const nome = document.getElementById('nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const whatsapp = document.getElementById('whatsapp').value.trim();
    const servico = document.getElementById('servico').value;
    const mensagem = document.getElementById('mensagem').value.trim();

    try {
        const response = await fetch('http://localhost:8080/api/orcamentos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, whatsapp, servico, mensagem })
        });

        const data = await response.json();

        // abre o WhatsApp usando o link já pronto que o backend devolveu
        window.open(data.linkWhatsapp, '_blank');

    } catch (err) {
        // fallback: se o backend estiver fora do ar, ainda assim abre o WhatsApp
        const texto =
            `Olá! Gostaria de solicitar um orçamento.%0A%0A` +
            `*Nome:* ${nome}%0A*E-mail:* ${email}%0A*WhatsApp:* ${whatsapp}%0A` +
            `*Serviço:* ${servico}%0A*Mensagem:* ${mensagem}`;
        window.open(`https://wa.me/244949943236?text=${texto}`, '_blank');
    }
});
```

Para o **botão de baixar o PDF de preços**, basta um link/botão no site:

```html
<a href="http://localhost:8080/api/orcamentos/pdf" target="_blank" class="btn btn-ghost">
    Ver lista de preços (PDF)
</a>
```

> Em produção, troque `http://localhost:8080` pela URL real do backend, e ajuste `app.cors.allowed-origins` em `application.properties` para incluir o domínio real do site.

---

## Estrutura do projeto

```
src/main/java/com/higienizaplus/backend/
├── config/          SecurityConfig, DataInitializer
├── controller/       AuthController, OrcamentoController, PrecoController
├── dto/              Records de request/response
├── exception/        ResourceNotFoundException, GlobalExceptionHandler
├── model/             Entidades JPA (Orcamento, Admin, ServicoPreco)
├── repository/        Interfaces Spring Data JPA
├── security/           JWT (provider, filtro, UserDetailsService)
└── service/            Regras de negócio (Orcamento, Auth, PDF, Preços)

src/main/resources/
├── application.properties
├── db/migration/        Scripts Flyway (schema + dados iniciais de preços)
└── static/admin/        Painel admin (HTML/CSS/JS puro, sem build)
```

## Próximos passos sugeridos (não incluídos nesta versão)

- Endpoint para o admin trocar a própria senha
- Envio de e-mail de notificação ao receber um novo orçamento
- Deploy (Docker, variáveis de ambiente em vez de senha hardcoded no `application.properties`)
