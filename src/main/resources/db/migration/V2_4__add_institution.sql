-- INSTITUTION: the institution (arkivinstitution) that holds an archive or collection, reached from an archive node
-- through TBL_NODEATTRIBUTES.FIELD3. The legacy table carries some forty contact columns; only the ones the API reads
-- are mirrored here.
--
-- Like every table in this schema it already exists in the real Sundsvallsminnen database, which Flyway never touches.
-- This migration only rebuilds it in the throwaway test database the integration tests start.
CREATE TABLE INSTITUTION
(
    I_ID        bigint NOT NULL PRIMARY KEY,
    INSTNAMN    varchar(60),
    INSTKOD     varchar(10),
    BESKRIVNING varchar(256),
    URL         varchar(200),
    EPOST       varchar(200)
) ENGINE = InnoDB;
