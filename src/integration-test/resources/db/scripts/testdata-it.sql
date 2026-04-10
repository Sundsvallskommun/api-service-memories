--
-- Reset state between IT classes (Testcontainers shares a single MariaDB instance across IT classes)
--
DELETE
FROM PUBL;
DELETE
FROM FILM;

--
-- FILM
--
INSERT INTO FILM (FILM_ID, FILNAMN, FILM_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, FILM_T_ID, FILM_OPLATS, FILM_O_ID,
                  FILM_U_E_ID, FILM_U_J_ID, KOMMENT_FILM, FILM_MIME_TYPE, ASV, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (1, 'midsommar1985.mp4', '/media/film/midsommar1985.mp4', 'VIDEO', '1985-06-21',
        'Midsommarfirande i Sundsvall 1985', 1, 'Sundsvall', 1, 0, 1, 'Film om midsommarfirande på Stora torget',
        'video/mp4', 'ASV001', 100, 4, null);

INSERT INTO FILM (FILM_ID, FILNAMN, FILM_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, FILM_T_ID, FILM_OPLATS, FILM_O_ID,
                  FILM_U_E_ID, FILM_U_J_ID, KOMMENT_FILM, FILM_MIME_TYPE, ASV, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (2, 'stadsfest1990.mp4', '/media/film/stadsfest1990.mp4', 'VIDEO', '1990-08-15', 'Stadsfesten i Sundsvall 1990',
        1, 'Sundsvall', 1, 0, 1, 'Dokumentation av stadsfesten', 'video/mp4', 'ASV002', 101, 4, null);

INSERT INTO FILM (FILM_ID, FILNAMN, FILM_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, FILM_T_ID, FILM_OPLATS, FILM_O_ID,
                  FILM_U_E_ID, FILM_U_J_ID, KOMMENT_FILM, FILM_MIME_TYPE, ASV, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (3, 'timra_marknad.mp4', '/media/film/timra_marknad.mp4', 'VIDEO', '1975-09-10', 'Timrå marknad 1975', 1,
        'Timrå', 2, 0, 1, 'Marknadsdag i Timrå centrum', 'video/mp4', 'ASV003', 102, 4, null);

-- Unpublished film that should never be returned by search or list endpoints
INSERT INTO FILM (FILM_ID, FILNAMN, FILM_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, FILM_T_ID, FILM_OPLATS, FILM_O_ID,
                  FILM_U_E_ID, FILM_U_J_ID, KOMMENT_FILM, FILM_MIME_TYPE, ASV, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (4, 'unpublished.mp4', '/media/film/unpublished.mp4', 'VIDEO', '2000-01-01', 'Unpublished film', 1, 'Sundsvall',
        1, 0, 1, 'This film is not published', 'video/mp4', 'ASV004', 103, 0, null);
--
-- PUBL
--
INSERT INTO PUBL (P_ID, FILNAMN, PUBLIKTYP, DATUM, TIDTITEL, TIDNR, TIDSIDA, BF_J_ID, FORLAG_T_ID, FORLAG_OPLATS,
                  DOKDATUM, DOKTITEL, F_E_ID, R_E_ID, U_J_ID, U_E_ID, P_T_ID, P_OPLATS, ME_O_ID, KOMMENT_PUBL,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
VALUES (207, 'F21051/1841-02-18_Sida 3 Alfwar och Skämt nr 8 1841.xml', '', '1841-02-18', 'Alfwar och Skämt', '8', '3',
        1, 1, 'Sundsvall', '1841-02-18', 'Sida 3 Alfwar och Skämt nr 8 1841', 0, 0, 1, 0, 16, 'Sundsvall', 1,
        'Tidningsnummer från 1841', 'PUBL.id_207_fil_liten.jpeg', 'PUBL.id_207_fil_stor.jpeg',
        'PUBL.id_207_fil_original.jpeg', 'PUBL.id_207_fil_txt.xml',
        'Alfwar och Skämt No 8 Thorsdagen den 18 februarii 1841 Landsortspressens frihet och Drunkningsolycka i Sundsvall',
        'PUBL.id_207_fil_xtra.jpeg', 18407, 4, 'text', null);

INSERT INTO PUBL (P_ID, FILNAMN, PUBLIKTYP, DATUM, TIDTITEL, TIDNR, TIDSIDA, BF_J_ID, FORLAG_T_ID, FORLAG_OPLATS,
                  DOKDATUM, DOKTITEL, F_E_ID, R_E_ID, U_J_ID, U_E_ID, P_T_ID, P_OPLATS, ME_O_ID, KOMMENT_PUBL,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
VALUES (300, 'broschyr_1950.xml', '', '1950-01-01', null, null, null, 1, 1, 'Sundsvall', '1950-01-01',
        'Kommunal informationsbroschyr 1950', 0, 0, 1, 0, 4, 'Sundsvall', 1, 'Kommunens informationsbroschyr',
        'PUBL.id_300_fil_liten.jpeg', 'PUBL.id_300_fil_stor.jpeg', 'PUBL.id_300_fil_original.pdf',
        'PUBL.id_300_fil_txt.xml', 'Sundsvalls kommun informerar sina invånare om lokala beslut',
        'PUBL.id_300_fil_xtra.jpeg', 18500, 4, 'text', null);

INSERT INTO PUBL (P_ID, FILNAMN, PUBLIKTYP, DATUM, TIDTITEL, TIDNR, TIDSIDA, BF_J_ID, FORLAG_T_ID, FORLAG_OPLATS,
                  DOKDATUM, DOKTITEL, F_E_ID, R_E_ID, U_J_ID, U_E_ID, P_T_ID, P_OPLATS, ME_O_ID, KOMMENT_PUBL,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
VALUES (400, 'affisch_1975.xml', '', '1975-06-01', null, null, null, 1, 1, 'Sundsvall', '1975-06-01',
        'Affisch Sundsvallsstämman 1975', 0, 0, 1, 0, 1, 'Sundsvall', 1, 'Reklamaffisch för årlig stämma',
        'PUBL.id_400_fil_liten.jpeg', 'PUBL.id_400_fil_stor.jpeg', 'PUBL.id_400_fil_original.jpeg',
        'PUBL.id_400_fil_txt.xml', 'Välkommen till Sundsvallsstämman sommaren 1975', 'PUBL.id_400_fil_xtra.jpeg', 18600,
        4, 'text', null);

-- Unpublished publication that must never appear in any response
INSERT INTO PUBL (P_ID, FILNAMN, PUBLIKTYP, DATUM, TIDTITEL, TIDNR, TIDSIDA, BF_J_ID, FORLAG_T_ID, FORLAG_OPLATS,
                  DOKDATUM, DOKTITEL, F_E_ID, R_E_ID, U_J_ID, U_E_ID, P_T_ID, P_OPLATS, ME_O_ID, KOMMENT_PUBL,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
VALUES (500, 'draft.xml', '', '2024-01-01', null, null, null, 1, 1, 'Sundsvall', '2024-01-01',
        'Draft unpublished publication', 0, 0, 1, 0, 4, 'Sundsvall', 1, 'Not yet published', null, null, null, null,
        'Drunkningsolycka draft not published', null, 18700, 0, 'text', null);
