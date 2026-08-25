-- Refines VW_MEMORY_OBJECTS (see V1_7 through V1_9) in two ways; everything else is unchanged.
--
--   * NAME_TEXT is new: the document title for the object types, the full name for the registers, including the name
--     columns TITLE leaves out (JURPERS.ALTNAMN, SJOMAN.EFTERNAMN2). The combined search ranks a hit here above one
--     that only occurred in SEARCH_TEXT. It is a subset of SEARCH_TEXT, so it changes the order, not which rows match.
--     Columns are wrapped in NULLIF(..., '') because CONCAT_WS skips NULL but not the empty string.
--   * The soft-delete guard is new: deletion sets DELETEDDATE but leaves the published bit set, so the OPTIONS check
--     alone never hid a deleted row. SJOMAN has neither column and is left as it was.
CREATE OR REPLACE VIEW VW_MEMORY_OBJECTS AS
SELECT CONCAT('foto-', F_ID)  AS OBJECT_KEY,
       F_ID                   AS SOURCE_ID,
       OBJTYP                 AS OBJECT_TYPE,
       DOKTITEL               AS TITLE,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')) AS NAME_TEXT,
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FF) AS SEARCH_TEXT,
       NULLIF(CAST(LEFT(NULLIF(TIDIG, ''), 4) AS UNSIGNED), 0) AS SORT_YEAR,
       F_T_ID                 AS TOPOGRAPHY_ID,
       F_OPLATS               AS LOCATION_TEXT,
       U_E_ID                 AS CREATOR_PERSON_ID,
       U_J_ID                 AS CREATOR_LEGAL_ENTITY_ID
FROM FOTO
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS, FILM_U_E_ID, FILM_U_J_ID
FROM FILM
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS, LJUD_U_E_ID, LJUD_U_J_ID
FROM LJUD
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS, U_E_ID, U_J_ID
FROM TEXT
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS, U_E_ID, U_J_ID
FROM PUBL
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('person-', P_ID), P_ID, 'Person',
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', FNAMN, ENAMN, YRKEE, FODFRS, KOMMENT_PERS),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFRS, NULL, NULL
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', NULLIF(JURPERS, ''), NULLIF(ALTNAMN, '')),
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS, NULL, NULL
FROM JURPERS
WHERE (`OPTIONS` & 4) = 4
  AND J_ID <> 1
  AND DELETEDDATE IS NULL
UNION ALL
-- SJOMAN has neither an OPTIONS nor a DELETEDDATE column, so neither guard applies — matching the per-type /seamen
-- search.
SELECT CONCAT('sjoman-', POSTNR), POSTNR, 'Sjöman',
       CONCAT_WS(' ', NULLIF(FORNAMN, ''), NULLIF(EFTERNAMN1, '')),
       CONCAT_WS(' ', NULLIF(FORNAMN, ''), NULLIF(EFTERNAMN1, ''), NULLIF(EFTERNAMN2, '')),
       CONCAT_WS(' ', FORNAMN, EFTERNAMN1, EFTERNAMN2, FODFORS, FODPLATS, HEMFORS, HEMPLATS, BEFATTN, FARTYG),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS, NULL, NULL
FROM SJOMAN;
