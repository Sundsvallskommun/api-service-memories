-- Mirrors six SJOMAN columns the legacy seaman page shows and the API now returns: FSCBKOD (SCB code of the birth
-- parish), HYRA, BETTID and VALUTA (the wage), ORSAK (why the seaman signed off) and REGNR (the ship's registration
-- number). Sizes follow the longest value in the delivered data.
--
-- Like every table in this schema, SJOMAN already exists in the real Sundsvallsminnen database with these columns, and
-- Flyway never touches it. This only brings the throwaway test database the integration tests start up to the real
-- table. IF NOT EXISTS keeps it harmless if it is ever run against the real one.
ALTER TABLE SJOMAN
    ADD COLUMN IF NOT EXISTS FSCBKOD varchar(6),
    ADD COLUMN IF NOT EXISTS HYRA    varchar(15),
    ADD COLUMN IF NOT EXISTS BETTID  varchar(10),
    ADD COLUMN IF NOT EXISTS VALUTA  varchar(10),
    ADD COLUMN IF NOT EXISTS ORSAK   varchar(1),
    ADD COLUMN IF NOT EXISTS REGNR   varchar(6);
