-- Changes VW_NODE_CONTEXT and VW_MEMORY_OBJECTS (see V2_8) in one way each; no table is altered.
--
--   * VW_NODE_CONTEXT also gives every node the arkivbildare of the archive it sits in, when that arkivbildare is a
--     legal entity. The walk goes up from the node itself through its four nearest parents and stops at the first node
--     that names any arkivbildare in TBL_NODEATTRIBUTES: FIELD1 for a legal entity, FIELD2 for a person. A legal entity
--     there is the answer. A person stops the walk and gives none, since the filter offers legal entities only, so a
--     series that names a person does not take the legal entity of the archive above it. JURPERS 1 and PERSON 0 are
--     the blank sentinel rows and name no one. In the real data only the archive nodes carry FIELD1 or FIELD2 (checked
--     against sm-test, October 2026). The walk stops at a soft-deleted node, as it does for the institution.
--
--   * VW_MEMORY_OBJECTS gains ARCHIVE_CREATOR_LEGAL_ENTITY_ID, which the archiveCreatorLegalEntityId filter reads. It
--     names the arkivbildare of the archive a row sits in, which is not the row's upphovsman
--     (CREATOR_LEGAL_ENTITY_ID):
--       - FOTO, FILM, LJUD, TEXT and PUBL: the arkivbildare of the archive they sit in.
--       - The registers (PERSON, JURPERS, SJOMAN, MANTAL) are not placed in the tree and emit NULL, so the filter
--         excludes them, the way institutionId does.
--     The new column is the last one in every branch: UNION ALL pairs the columns by position, not by name.
--
-- Flyway is off in the real environments, so this has to be applied by hand there, after V2_8 and before the code that
-- reads ARCHIVE_CREATOR_LEGAL_ENTITY_ID ships: the entity maps the column, so Hibernate selects it in every query
-- against the view. It is written to survive a re-run.

CREATE OR REPLACE VIEW VW_NODE_CONTEXT AS
SELECT n0.ID AS NODE_ID,
       COALESCE(CASE WHEN a0.FIELD3 > 1 THEN a0.FIELD3 END,
                CASE WHEN a1.FIELD3 > 1 THEN a1.FIELD3 END,
                CASE WHEN a2.FIELD3 > 1 THEN a2.FIELD3 END,
                CASE WHEN a3.FIELD3 > 1 THEN a3.FIELD3 END,
                CASE WHEN a4.FIELD3 > 1 THEN a4.FIELD3 END) AS INSTITUTION_ID,
       CASE WHEN a0.FIELD1 > 1 THEN a0.FIELD1 WHEN a0.FIELD2 > 0 THEN NULL
            WHEN a1.FIELD1 > 1 THEN a1.FIELD1 WHEN a1.FIELD2 > 0 THEN NULL
            WHEN a2.FIELD1 > 1 THEN a2.FIELD1 WHEN a2.FIELD2 > 0 THEN NULL
            WHEN a3.FIELD1 > 1 THEN a3.FIELD1 WHEN a3.FIELD2 > 0 THEN NULL
            WHEN a4.FIELD1 > 1 THEN a4.FIELD1 END AS ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM TBL_NODES n0
         LEFT JOIN TBL_NODES n1 ON n1.ID = n0.PARENTID AND n1.DELETEDDATE IS NULL
         LEFT JOIN TBL_NODES n2 ON n2.ID = n1.PARENTID AND n2.DELETEDDATE IS NULL
         LEFT JOIN TBL_NODES n3 ON n3.ID = n2.PARENTID AND n3.DELETEDDATE IS NULL
         LEFT JOIN TBL_NODES n4 ON n4.ID = n3.PARENTID AND n4.DELETEDDATE IS NULL
         LEFT JOIN TBL_NODEATTRIBUTES a0 ON a0.NODEID = n0.ID
         LEFT JOIN TBL_NODEATTRIBUTES a1 ON a1.NODEID = n1.ID
         LEFT JOIN TBL_NODEATTRIBUTES a2 ON a2.NODEID = n2.ID
         LEFT JOIN TBL_NODEATTRIBUTES a3 ON a3.NODEID = n3.ID
         LEFT JOIN TBL_NODEATTRIBUTES a4 ON a4.NODEID = n4.ID
WHERE n0.DELETEDDATE IS NULL;

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
       NODEID                 AS NODE_ID,
       COALESCE(CASE WHEN M_I_ID > 1 THEN M_I_ID END, c.INSTITUTION_ID) AS INSTITUTION_ID,
       c.ARCHIVE_CREATOR_LEGAL_ENTITY_ID AS ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM FOTO
         LEFT JOIN VW_NODE_CONTEXT c ON c.NODE_ID = FOTO.NODEID
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('film-', FILM_ID), FILM_ID, 'Film', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_FILM),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), FILM_T_ID, FILM_OPLATS, NULL, FILM_U_E_ID, FILM_U_J_ID,
       NODEID, c.INSTITUTION_ID, c.ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM FILM
         LEFT JOIN VW_NODE_CONTEXT c ON c.NODE_ID = FILM.NODEID
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('ljud-', LJUD_ID), LJUD_ID, 'Ljud', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_LJUD),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), LJUD_T_ID, LJUD_OPLATS, NULL, LJUD_U_E_ID, LJUD_U_J_ID,
       NODEID, c.INSTITUTION_ID, c.ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM LJUD
         LEFT JOIN VW_NODE_CONTEXT c ON c.NODE_ID = LJUD.NODEID
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('text-', ID_ID), ID_ID, 'Text', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_DOC),
       NULLIF(CAST(LEFT(NULLIF(DOKDATUM, ''), 4) AS UNSIGNED), 0), D_T_ID, D_OPLATS, NULL, U_E_ID, U_J_ID,
       NODEID, c.INSTITUTION_ID, c.ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM TEXT
         LEFT JOIN VW_NODE_CONTEXT c ON c.NODE_ID = TEXT.NODEID
WHERE (`OPTIONS` & 4) = 4
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('publ-', P_ID), P_ID, 'Publikation', DOKTITEL,
       CONCAT_WS(' ', NULLIF(DOKTITEL, '')),
       CONCAT_WS(' ', DOKTITEL, KOMMENT_PUBL),
       NULLIF(CAST(LEFT(NULLIF(DATUM, ''), 4) AS UNSIGNED), 0), P_T_ID, P_OPLATS, NULL, U_E_ID, U_J_ID,
       NODEID, c.INSTITUTION_ID, c.ARCHIVE_CREATOR_LEGAL_ENTITY_ID
FROM PUBL
         LEFT JOIN VW_NODE_CONTEXT c ON c.NODE_ID = PUBL.NODEID
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
       NULL, NULL, NULL, NULL, NULL
FROM PERSON
WHERE (`OPTIONS` & 4) = 4
  AND P_ID <> 0
  AND DELETEDDATE IS NULL
UNION ALL
SELECT CONCAT('jurpers-', J_ID), J_ID, 'Juridisk person', JURPERS,
       CONCAT_WS(' ', NULLIF(JURPERS, ''), NULLIF(ALTNAMN, '')),
       CONCAT_WS(' ', JURPERS, ALTNAMN, HUVUDMAN, KOMMENT_JURPERS),
       NULLIF(CAST(LEFT(NULLIF(STARTDATUM, ''), 4) AS UNSIGNED), 0), T_ID, OPLATS, NULL, NULL, NULL, NULL, NULL, NULL
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
       NULLIF(CAST(LEFT(NULLIF(FODDAT, ''), 4) AS UNSIGNED), 0), NULL, FODFORS, NULL, NULL, NULL, NULL, NULL, NULL
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
       NULL, NULL, NULL, NULL, NULL
FROM MANTAL;