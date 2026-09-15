-- Changes VW_MEMORY_OBJECTS (see V1_7 through V2_5) in one way, and adds one index; no table is altered otherwise.
--
--   * The PUBL branch of SEARCH_TEXT gains XMLTEXT — the digitised body of the publication. Without it the combined
--     search only ever saw titles and comments, so a word that appears solely inside a scanned page was invisible:
--     "Drunkningsolycka" returned one hit where the source holds four, and three of those four carry the word only in
--     XMLTEXT. The per-type /publications search already read the column (PublicationSpecification), so the two
--     searches disagreed on the same data.
--
--     TEXT is deliberately left alone: its XMLTEXT is empty across all 3 942 rows, so adding it would widen the scan
--     for nothing.
--
--     Cost: SEARCH_TEXT for PUBL now concatenates roughly 65 MB of longtext across 11 705 rows, and the combined
--     search matches it with LIKE, which no index can serve. Measure this once it is live; if it hurts, the fix is to
--     stop matching a computed view column at all (MariaDB refuses MATCH on one — error 1210) and give the combined
--     search a real indexed table to search.
--
--   * TBL_NODES gains a FULLTEXT index over (NAME, DESCRIPTION). MATCH requires an index whose column list is exactly
--     the one being matched, and the existing ft_tbl_nodes_description covers DESCRIPTION alone, which the node search
--     cannot use because it searches the name as well.
--
-- Unlike the legacy tables this view is ours. Flyway is off in the real environments, so both statements have to be
-- applied by hand there before the code that depends on them ships. Both are written to survive a re-run.

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
       U_J_ID                 AS CREATOR_LEGAL_ENTITY_ID,
       NODEID                 AS NODE_ID
FROM FOTO
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS, NULL, FILM_U_E_ID, FILM_U_J_ID,
       NODEID
FROM FILM
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS, NULL, LJUD_U_E_ID, LJUD_U_J_ID,
       NODEID
FROM LJUD
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS, NULL, U_E_ID, U_J_ID,
       NODEID
FROM TEXT
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL, XMLTEXT),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS, NULL, U_E_ID, U_J_ID,
       NODEID
FROM PUBL
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('person-', P_ID), P_ID, 'Person',
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', NULLIF(FNAMN, ''), NULLIF(ENAMN, '')),
       CONCAT_WS(' ', FNAMN, ENAMN, YRKEE, FODFRS, KOMMENT_PERS),
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFRS,
       CASE
           WHEN LOWER(KON) IN ('man', '1') THEN 'Man'
           WHEN LOWER(KON) IN ('kvinna', '2') THEN 'Kvinna'
           ELSE 'Okänt'
       END,
       NULL, NULL, NULL
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', NULLIF(JURPERS, ''), NULLIF(ALTNAMN, '')),
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS, NULL, NULL, NULL, NULL
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
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS, NULL, NULL, NULL, NULL
FROM SJOMAN
UNION ALL
SELECT CONCAT('mantal-', KALLA, '-', ID), ID, 'Mantal',
       CONCAT_WS(' ', NULLIF(MNMNF, ''), NULLIF(MNMNE, '')),
       CONCAT_WS(' ', NULLIF(MNMNF, ''), NULLIF(MNMNE, '')),
       CONCAT_WS(' ', MNMNF, MNMNE, YRKREL, MFSTNR1, FSTDEL1, MFSTNR2, FSTDEL2, MFSTNR3, FSTDEL3, ANM),
       NULLIF(CAST(LEFT(NULLIF(FODAR, ''), 4) AS UNSIGNED), 0), NULL, NULL,
       CASE
           WHEN LOWER(KON) IN ('man', '1') THEN 'Man'
           WHEN LOWER(KON) IN ('kvinna', '2') THEN 'Kvinna'
           ELSE 'Okänt'
       END,
       NULL, NULL, NULL
FROM MANTAL;

ALTER TABLE TBL_NODES ADD FULLTEXT INDEX IF NOT EXISTS ft_tbl_nodes_name_description (NAME, DESCRIPTION);
