-- Read-only view unifying the five object tables (FOTO incl. Föremål, FILM, LJUD, TEXT, PUBL) into one
-- SORT_YEAR is guarded with NULLIF(CAST(...), 0): the source date columns are dirty free text, and a
-- non-numeric value (e.g. 'okänt') casts to 0 — which would both satisfy every yearTo bound and surface
-- as a bogus year 0 in the API. Normalising to NULL keeps such rows out of year filters.
-- projection, so the combined /objects endpoint can search, sort and paginate globally on the server side
-- instead of the BFF fetching every source and merging in memory. Only published rows ((OPTIONS & 4) = 4).
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
WHERE (`OPTIONS` & 4) = 4;
