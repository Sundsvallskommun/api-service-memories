-- Extends VW_MEMORY_OBJECTS (see V1_7 through V2_0) in two ways; everything else is unchanged.
--
--   * GENDER is new: KON for the branches that record one (PERSON, MANTAL), NULL everywhere else. Backs the gender
--     filter and the genderCounts counters on the combined search.
--   * The MANTAL branch is new, so census records are searchable as the object type 'Mantal'. Like SJOMAN the table
--     has neither an OPTIONS nor a DELETEDDATE column, so no guard applies — matching the per-type /censusrecords
--     search. The names are composed first-name-first like the other registers, and the property designations join
--     the search text as identifying fields.
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
       NULL                   AS GENDER,
       U_E_ID                 AS CREATOR_PERSON_ID,
       U_J_ID                 AS CREATOR_LEGAL_ENTITY_ID
FROM FOTO
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS, NULL, FILM_U_E_ID, FILM_U_J_ID
FROM FILM
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS, NULL, LJUD_U_E_ID, LJUD_U_J_ID
FROM LJUD
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS, NULL, U_E_ID, U_J_ID
FROM TEXT
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS, NULL, U_E_ID, U_J_ID
FROM PUBL
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('person-', P_ID), P_ID, 'Person',
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', FNAMN, ENAMN, YRKEE, FODFRS, KOMMENT_PERS),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFRS, KON, NULL, NULL
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', NULLIF(JURPERS, ''), NULLIF(ALTNAMN, '')),
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS, NULL, NULL, NULL
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
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS, NULL, NULL, NULL
FROM SJOMAN
UNION ALL
SELECT CONCAT('mantal-', ID), ID, 'Mantal',
       CONCAT_WS(' ', NULLIF(MNMNF, ''), NULLIF(MNMNE, '')),
       CONCAT_WS(' ', NULLIF(MNMNF, ''), NULLIF(MNMNE, '')),
       CONCAT_WS(' ', MNMNF, MNMNE, YRKREL, MFSTNR1, FSTDEL1, MFSTNR2, FSTDEL2, MFSTNR3, FSTDEL3, ANM),
       NULLIF(CAST(LEFT(NULLIF(FODAR, ''), 4) AS UNSIGNED), 0), NULL, NULL, KON, NULL, NULL
FROM MANTAL;
