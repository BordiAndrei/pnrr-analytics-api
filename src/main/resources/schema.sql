-- ====================================================================================
-- STRUCTURĂ BAZĂ DE DATE PNRR (Versiunea în Limba Română)
-- Dialect: PostgreSQL
-- ====================================================================================

-- 1. Tabela INSTITUTII (CRI - Coordonatori de Reforme și Investiții)
-- Corespunde fișierului CRI.csv
CREATE TABLE IF NOT EXISTS institutii (
    id SERIAL PRIMARY KEY,
    cod VARCHAR(50) NOT NULL UNIQUE,  -- Ex: 'MIPE', 'MTI'
    denumire VARCHAR(255) NOT NULL,   -- Ex: 'Ministerul Investițiilor și Proiectelor Europene'
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE institutii IS 'Nomenclatorul instituțiilor responsabile (CRI)';

-- 2. Tabela COMPONENTE
-- Nivelul 1 din ierarhia PNRR (ex: C1, C2)
CREATE TABLE IF NOT EXISTS componente (
    id SERIAL PRIMARY KEY,
    denumire VARCHAR(255) NOT NULL UNIQUE, -- Ex: 'Managementul apei'
    cod VARCHAR(50),                       -- Ex: 'C1'
    descriere TEXT,
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE componente IS 'Componentele majore ale PNRR';

-- 3. Tabela MASURI (Investiții și Reforme)
-- Nivelul 2 din ierarhia PNRR (Investiții/Reforme legate de o Componentă)
CREATE TABLE IF NOT EXISTS masuri (
    id SERIAL PRIMARY KEY,
    id_componenta INTEGER NOT NULL REFERENCES componente(id),
    cod VARCHAR(50) NOT NULL,        -- Ex: 'I1', 'R2'
    denumire TEXT,                   -- Descrierea completă
    tip VARCHAR(20) CHECK (tip IN ('INVESTITIE', 'REFORMA', 'NECUNOSCUT')) DEFAULT 'NECUNOSCUT',
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_componenta, cod)      -- Un cod (ex: I1) este unic doar în cadrul unei componente
);

COMMENT ON TABLE masuri IS 'Măsurile specifice (Investiții sau Reforme) asociate unei componente';

-- 4. Tabela BENEFICIARI
-- Entitățile care primesc finanțarea
CREATE TABLE IF NOT EXISTS beneficiari (
    id BIGSERIAL PRIMARY KEY,
    cui VARCHAR(50) UNIQUE,           -- Cod Unic de Înregistrare
    nume TEXT NOT NULL,               -- Numele beneficiarului
    tip VARCHAR(100),                 -- Ex: UAT, SRL, Companie Națională
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE beneficiari IS 'Beneficiarii proiectelor (identificați prin CUI)';

-- 5. Tabela LOCATII
-- Standardizarea geografică
CREATE TABLE IF NOT EXISTS locatii (
    id SERIAL PRIMARY KEY,
    judet VARCHAR(100) NOT NULL,      -- Ex: 'Alba'
    localitate VARCHAR(100) NOT NULL, -- Ex: 'Alba Iulia'
    regiune VARCHAR(100),             -- Opțional
    UNIQUE (judet, localitate)        -- Previne duplicatele
);

COMMENT ON TABLE locatii IS 'Nomenclator pentru locațiile de implementare a proiectelor';

-- 6. Tabela PROIECTE (Tabela de Fapte)
-- Conține datele tranzacționale din fișierul principal
CREATE TABLE IF NOT EXISTS proiecte (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titlu TEXT NOT NULL,

    -- Relații (Foreign Keys)
    id_beneficiar BIGINT NOT NULL REFERENCES beneficiari(id),
    id_masura INTEGER NOT NULL REFERENCES masuri(id),
    id_institutie INTEGER NOT NULL REFERENCES institutii(id),
    id_locatie INTEGER REFERENCES locatii(id), -- Poate fi NULL pt proiecte naționale

    -- Date Financiare
    sursa_finantare VARCHAR(100),                  -- Ex: 'Finanțare nerambursabilă'
    valoare_eur NUMERIC(18, 2) DEFAULT 0,          -- Valoare totală
    absorbtie_financiara_eur NUMERIC(18, 2) DEFAULT 0,

    -- Progres (0.00 - 100.00%)
    progres_tehnic NUMERIC(5, 2) DEFAULT 0 CHECK (progres_tehnic >= 0 AND progres_tehnic <= 100),
    progres_financiar NUMERIC(5, 2) DEFAULT 0 CHECK (progres_financiar >= 0 AND progres_financiar <= 100),
    diferenta_tehnic_financiar NUMERIC(5, 2),      -- Calculat: Tehnic - Financiar

    -- Raportare (Dimensiunea Timp)
    luna_raportare VARCHAR(20),    -- Ex: 'ianuarie'
    trimestru_raportare VARCHAR(10), -- Ex: 'T1'
    an_raportare INT DEFAULT 2025,

    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_actualizare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE proiecte IS 'Tabela centrală cu proiectele PNRR';

-- ====================================================================================
-- INDECȘI PENTRU OPTIMIZARE (Performanță API)
-- ====================================================================================

CREATE INDEX idx_proiecte_beneficiar ON proiecte(id_beneficiar);
CREATE INDEX idx_proiecte_institutie ON proiecte(id_institutie);
CREATE INDEX idx_proiecte_masura ON proiecte(id_masura);
CREATE INDEX idx_proiecte_valoare ON proiecte(valoare_eur DESC);
CREATE INDEX idx_locatii_judet ON locatii(judet);