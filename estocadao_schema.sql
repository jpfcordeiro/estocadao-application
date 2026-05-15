-- estocadao_schema.sql

-- Criação da tabela products
CREATE TABLE public.products (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar NOT NULL,
    description text,
    sku varchar UNIQUE NOT NULL,
    category varchar,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
);

-- Criação da tabela stock_items
CREATE TABLE public.stock_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id uuid REFERENCES public.products(id) ON DELETE CASCADE,
    quantity integer NOT NULL DEFAULT 0,
    unit_price decimal NOT NULL,
    location varchar,
    updated_at timestamp with time zone DEFAULT now()
);

-- Criação da View para o endpoint especial /stock/summary
-- Realiza a agregação via query SQL (GROUP BY + SUM)
CREATE OR REPLACE VIEW public.stock_summary AS
SELECT 
    p.id AS product_id,
    p.name AS product_name,
    COALESCE(SUM(s.quantity), 0) AS total_quantity
FROM public.products p
LEFT JOIN public.stock_items s ON p.id = s.product_id
GROUP BY p.id, p.name;
