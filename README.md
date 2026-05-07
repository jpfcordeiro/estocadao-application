# Estocadão API - Controle de Estoque (KMP)

Este é um servidor REST desenvolvido com Kotlin Multiplatform e Ktor, utilizando Supabase (PostgreSQL) para persistência de dados.

## 🚀 Como configurar e executar

### 1. Requisitos do Banco de Dados (Supabase)
Certifique-se de que as seguintes tabelas e views existam no seu projeto Supabase:

#### Tabelas
- **products**: `id (uuid, PK)`, `name (varchar)`, `description (text)`, `sku (varchar)`, `category (varchar)`, `created_at`, `updated_at`.
- **stock_items**: `id (uuid, PK)`, `product_id (uuid, FK)`, `quantity (integer)`, `unit_price (decimal)`, `location (varchar)`, `updated_at`.

#### View para o Sumário (Obrigatório para o endpoint /stock/summary)
Execute o seguinte SQL no seu editor do Supabase:
```sql
CREATE VIEW stock_summary AS
SELECT 
    p.id as product_id, 
    p.name as product_name, 
    SUM(s.quantity) as total_quantity
FROM products p
LEFT JOIN stock_items s ON p.id = s.product_id
GROUP BY p.id, p.name;
```

### 2. Variáveis de Ambiente
Crie um arquivo `local.properties` na raiz do projeto ou defina as seguintes variáveis no seu sistema:
```properties
SUPABASE_URL=SUA_URL_DO_SUPABASE
SUPABASE_KEY=SUA_CHAVE_ANON_DO_SUPABASE
```

### 3. Executar o Servidor
No terminal, execute:
```shell
./gradlew :server:run
```
O servidor estará disponível em `http://localhost:8080`.

## 🛠 Endpoints da API

### Produtos (`/products`)
- `GET /products`: Lista todos os produtos.
- `GET /products/{id}`: Busca produto por ID.
- `POST /products`: Cadastra novo produto.
- `PUT /products/{id}`: Atualiza um produto.
- `DELETE /products/{id}`: Remove um produto.

### Estoque (`/stock`)
- `GET /stock`: Lista todos os itens de estoque.
- `GET /stock/summary`: Retorna a quantidade total de cada produto (via SQL View).
- `POST /stock`: Adiciona item ao estoque.
- `PUT /stock/{id}`: Atualiza item de estoque.
- `DELETE /stock/{id}`: Remove item de estoque.

---
**Nota:** Ao deletar um produto, os itens de estoque vinculados devem ser tratados via CASCADE no PostgreSQL para manter a integridade.
