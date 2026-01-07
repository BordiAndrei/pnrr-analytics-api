-- ====================================================================================
-- STRUCTURĂ BAZĂ DE DATE PNRR (Versiunea Actualizată - Production Ready)
-- Dialect: PostgreSQL
-- Include: Resetare automată (DROP), Suport pentru >100%, Fără erori de overflow
-- ====================================================================================

-- 0. CURĂȚENIE GENERALĂ (RESET TOTAL)
-- Ștergem tabelele în ordine inversă a dependențelor pentru a evita erorile
DROP TABLE IF EXISTS proiecte CASCADE;
DROP TABLE IF EXISTS locatii CASCADE;
DROP TABLE IF EXISTS beneficiari CASCADE;
DROP TABLE IF EXISTS masuri CASCADE;
DROP TABLE IF EXISTS componente CASCADE;
DROP TABLE IF EXISTS institutii CASCADE;

-- 1. Tabela INSTITUTII (CRI - Coordonatori de Reforme și Investiții)
CREATE TABLE institutii (
    id SERIAL PRIMARY KEY,
    cod VARCHAR(50) NOT NULL UNIQUE,  -- Ex: 'MIPE', 'MTI'
    denumire VARCHAR(255) NOT NULL,   -- Ex: 'Ministerul Investițiilor și Proiectelor Europene'
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE institutii IS 'Nomenclatorul instituțiilor responsabile (CRI)';

-- 2. Tabela COMPONENTE
CREATE TABLE componente (
    id SERIAL PRIMARY KEY,
    denumire VARCHAR(255) NOT NULL UNIQUE, -- Ex: 'Managementul apei'
    cod VARCHAR(50),                       -- Ex: 'C1'
    descriere TEXT,
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE componente IS 'Componentele majore ale PNRR';

-- 3. Tabela MASURI (Investiții și Reforme)
CREATE TABLE masuri (
    id SERIAL PRIMARY KEY,
    id_componenta INTEGER NOT NULL REFERENCES componente(id),
    cod VARCHAR(50) NOT NULL,        -- Ex: 'I1', 'R2'
    denumire TEXT,                   -- Descrierea completă
    tip VARCHAR(20) CHECK (tip IN ('INVESTITIE', 'REFORMA', 'NECUNOSCUT')) DEFAULT 'NECUNOSCUT',
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_componenta, cod)
);

COMMENT ON TABLE masuri IS 'Măsurile specifice asociate unei componente';

-- 4. Tabela BENEFICIARI
CREATE TABLE beneficiari (
    id BIGSERIAL PRIMARY KEY,
    cui VARCHAR(50) UNIQUE,           -- Cod Unic de Înregistrare
    nume TEXT NOT NULL,               -- Numele beneficiarului
    tip VARCHAR(100),                 -- Ex: UAT, SRL, Companie Națională (Inferat din date)
    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE beneficiari IS 'Beneficiarii proiectelor (identificați prin CUI)';

-- 5. Tabela LOCATII
CREATE TABLE locatii (
    id SERIAL PRIMARY KEY,
    judet VARCHAR(100) NOT NULL,      -- Ex: 'Alba'
    localitate VARCHAR(100) NOT NULL, -- Ex: 'Alba Iulia'
    regiune VARCHAR(100),             -- Ex: 'Centru' (Inferat din date)
    UNIQUE (judet, localitate)
);

COMMENT ON TABLE locatii IS 'Nomenclator pentru locațiile de implementare a proiectelor';

-- 6. Tabela PROIECTE (Tabela de Fapte - Actualizată)
CREATE TABLE proiecte (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titlu TEXT NOT NULL,

    -- Relații (Foreign Keys)
    id_beneficiar BIGINT NOT NULL REFERENCES beneficiari(id),
    id_masura INTEGER NOT NULL REFERENCES masuri(id),
    id_institutie INTEGER NOT NULL REFERENCES institutii(id),
    id_locatie INTEGER REFERENCES locatii(id),

    -- Date Financiare
    sursa_finantare VARCHAR(100),                  -- Ex: 'Finanțare nerambursabilă'
    valoare_eur NUMERIC(18, 2) DEFAULT 0,          -- Valoare totală
    absorbtie_financiara_eur NUMERIC(18, 2) DEFAULT 0,

    -- Progres (Actualizat: NUMERIC(10,2) și fără limită superioară)
    progres_tehnic NUMERIC(10, 2) DEFAULT 0 CHECK (progres_tehnic >= 0),
    progres_financiar NUMERIC(10, 2) DEFAULT 0 CHECK (progres_financiar >= 0),

    diferenta_tehnic_financiar NUMERIC(10, 2),     -- Calculat: Tehnic - Financiar

    -- Raportare (Dimensiunea Timp)
    luna_raportare VARCHAR(20),      -- Ex: 'ianuarie'
    trimestru_raportare VARCHAR(10), -- Ex: 'T1'
    an_raportare INT DEFAULT 2025,

    data_creare TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_actualizare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE proiecte IS 'Tabela centrală cu proiectele PNRR';
COMMENT ON COLUMN proiecte.progres_financiar IS 'Procentul poate depăși 100% în cazuri de plăți în avans, actualizări de prețuri sau costuri suplimentare acceptate.';

-- ====================================================================================
-- INDECȘI PENTRU OPTIMIZARE (Performanță API)
-- ====================================================================================

CREATE INDEX idx_proiecte_beneficiar ON proiecte(id_beneficiar);
CREATE INDEX idx_proiecte_institutie ON proiecte(id_institutie);
CREATE INDEX idx_proiecte_masura ON proiecte(id_masura);
CREATE INDEX idx_proiecte_valoare ON proiecte(valoare_eur DESC);
CREATE INDEX idx_locatii_judet ON locatii(judet);