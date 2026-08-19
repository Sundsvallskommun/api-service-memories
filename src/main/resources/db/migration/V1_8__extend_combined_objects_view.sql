-- Extends VW_MEMORY_OBJECTS (see V1_7) with the three register tables — PERSON, JURPERS and SJOMAN — so that every
-- base item a user can search for is reachable from the combined /objects endpoint, not just the six media/document
-- object types. The five original branches are unchanged.
--
-- Register-specific notes:
--   * TITLE is the composed name (given + family name, or the legal entity's name); SEARCH_TEXT adds the fields a
--     free-text search is expected to hit (alternative names, occupation, parishes, ship, comment).
--   * SORT_YEAR is the birth year (PERSON/SJOMAN) or the start of the activity period (JURPERS). The per-type
--     /legal-entities search treats STARTDATUM–SLUTDATUM as a period and keeps overlapping rows; the combined view
--     collapses it to the start year, the same simplification the media branches already make.
--   * PERSON and SJOMAN have no topography reference, so TOPOGRAPHY_ID is NULL and LOCATION_TEXT carries the birth
--     parish — that keeps the location filter meaningful for them. The remaining places (home parish, birthplace,
--     home port) stay searchable through SEARCH_TEXT.
--   * The placeholder rows PERSON.P_ID = 0 ("ingen person") and JURPERS.J_ID = 1 ("ingen") are sentinels other tables
--     point at, not archive records, and are excluded exactly as the per-type searches exclude them. Both are flagged
--     published, so the OPTIONS bit alone would not remove them.
--   * SJOMAN has no OPTIONS column, so no publish filter is applied — matching the per-type /seamen search.
CREATE OR REPLACE VIEW VW_MEMORY_OBJECTS AS
SELECT CONCAT('foto-', F_ID)  AS OBJECT_KEY,
       F_ID                   AS SOURCE_ID,
       OBJTYP                 AS OBJECT_TYPE,
       DOKTITEL               AS TITLE,
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FF) AS SEARCH_TEXT,
       NULLIF(CAST(LEFT(NULLIF(TIDIG, ''), 4) AS UNSIGNED), 0) AS SORT_YEAR,
       F_T_ID                 AS TOPOGRAPHY_ID,
       F_OPLATS               AS LOCATION_TEXT
FROM FOTO
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS
FROM FILM
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS
FROM LJUD
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS
FROM TEXT
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS
FROM PUBL
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('person-', P_ID), P_ID, 'Person',
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', FNAMN, ENAMN, YRKEE, FODFRS, KOMMENT_PERS),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFRS
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS
FROM JURPERS
WHERE (`OPTIONS` & 4) = 4
  AND J_ID <> 1
UNION ALL
SELECT CONCAT('sjoman-', POSTNR), POSTNR, 'Sjöman',
       CONCAT_WS(' ', NULLIF(FORNAMN, ''), NULLIF(EFTERNAMN1, '')),
       CONCAT_WS(' ', FORNAMN, EFTERNAMN1, EFTERNAMN2, FODFORS, FODPLATS, HEMFORS, HEMPLATS, BEFATTN, FARTYG),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS
FROM SJOMAN;
