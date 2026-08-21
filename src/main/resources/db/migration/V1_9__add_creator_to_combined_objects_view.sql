-- Adds the upphovsman (originator) to VW_MEMORY_OBJECTS so the combined /objects search can filter on it the same way
-- the per-type searches do. The object branches carry U_E_ID and U_J_ID; the register branches have no originator of
-- their own -- a person is not created by anyone -- and emit NULL, which keeps them out of an originator-filtered
-- result without a special case in the query.
--
-- Both columns default to a sentinel rather than to NULL (0 = "ingen person", 1 = "ingen"), so the ids come through as
-- they are and the placeholders are excluded where the filter is applied, not here.
CREATE OR REPLACE VIEW VW_MEMORY_OBJECTS AS
SELECT CONCAT('foto-', F_ID)  AS OBJECT_KEY,
       F_ID                   AS SOURCE_ID,
       OBJTYP                 AS OBJECT_TYPE,
       DOKTITEL               AS TITLE,
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FF) AS SEARCH_TEXT,
       NULLIF(CAST(LEFT(NULLIF(TIDIG, ''), 4) AS UNSIGNED), 0) AS SORT_YEAR,
       F_T_ID                 AS TOPOGRAPHY_ID,
       F_OPLATS               AS LOCATION_TEXT,
       U_E_ID                 AS CREATOR_PERSON_ID,
       U_J_ID                 AS CREATOR_LEGAL_ENTITY_ID
FROM FOTO
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS, FILM_U_E_ID, FILM_U_J_ID
FROM FILM
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS, LJUD_U_E_ID, LJUD_U_J_ID
FROM LJUD
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS, U_E_ID, U_J_ID
FROM TEXT
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL, CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS, U_E_ID, U_J_ID
FROM PUBL
WHERE (`OPTIONS` & 4) = 4
UNION ALL
SELECT CONCAT('person-', P_ID), P_ID, 'Person',
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', FNAMN, ENAMN, YRKEE, FODFRS, KOMMENT_PERS),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFRS, NULL, NULL
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS, NULL, NULL
FROM JURPERS
WHERE (`OPTIONS` & 4) = 4
  AND J_ID <> 1
UNION ALL
SELECT CONCAT('sjoman-', POSTNR), POSTNR, 'Sjöman',
       CONCAT_WS(' ', NULLIF(FORNAMN, ''), NULLIF(EFTERNAMN1, '')),
       CONCAT_WS(' ', FORNAMN, EFTERNAMN1, EFTERNAMN2, FODFORS, FODPLATS, HEMFORS, HEMPLATS, BEFATTN, FARTYG),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS, NULL, NULL
FROM SJOMAN;
