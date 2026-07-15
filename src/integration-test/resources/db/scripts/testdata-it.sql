--
-- TOPOGRAFI (place lookup)
--
INSERT INTO TOPOGRAFI (T_ID, TOPNAMN, TOPKOD, PLATS, LAND)
VALUES (1, 'Sundsvall', 'SUN', 'Sundsvalls kommun', 'Sverige'),
       (2, 'Timrå', 'TIM', 'Timrå kommun', 'Sverige'),
       (4, 'Sundsvalls kommun', 'SUNK', 'Sundsvalls kommun', 'Sverige'),
       (16, 'Sundsvall stad', 'SUNS', 'Sundsvalls kommun', 'Sverige');

--
-- FILM
--
INSERT INTO FILM (FILM_ID, FILNAMN, FILM_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, FILM_T_ID, FILM_OPLATS, FILM_O_ID,
                  FILM_U_E_ID, FILM_U_J_ID, KOMMENT_FILM, FILM_MIME_TYPE, ASV, NODEID, `OPTIONS`, DELETEDDATE)
-- OPTIONS = 6 (bit 4 published + bit 2 set) — exercises the bitwise check, must still match `(OPTIONS & 4) = 4`
VALUES (1, 'midsommar1985.mp4', '/media/film/midsommar1985.mp4', 'VIDEO', '1985-06-21',
        'Midsommarfirande i Sundsvall 1985', 1, 'Sundsvall', 1, 0, 1, 'Film om midsommarfirande på Stora torget',
        'video/mp4', 'ASV001', 100, 6, null);

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
        -- OPTIONS = 6 (bit 4 published + bit 2 set) — exercises the bitwise check
        'PUBL.id_207_fil_xtra.jpeg', 18407, 6, 'text', null);

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

--
-- FOTO
--
INSERT INTO FOTO (F_ID, DOKTITEL, KOMMENT_FF, TIDIG, SENAST, F_OPLATS, OBJTYP, SVVITFARG, MATERIAL, TEKNIK,
                  TILLSKAT, TILLSTAND, FIL_LITEN, FIL_STOR, FIL_ORIGINAL, NODEID, `OPTIONS`)
-- OPTIONS = 6 (bit 4 published + bit 2 set) — exercises the bitwise check
VALUES (1001, 'Stadsvy från Norra berget', 'Panorama över Sundsvall från Norra berget', '1920', '1925',
        'Sundsvall', 'Foto', 'Svartvit', 'Glas', 'Negativ', 'Bra', 'Inga skador', 'FOTO.id_1001_fil_liten.jpg',
        'FOTO.id_1001_fil_stor.jpg', 'FOTO.id_1001_fil_original.jpg', 19000, 6);

INSERT INTO FOTO (F_ID, DOKTITEL, KOMMENT_FF, TIDIG, SENAST, F_OPLATS, OBJTYP, SVVITFARG, FIL_LITEN, FIL_STOR,
                  FIL_ORIGINAL, NODEID, `OPTIONS`)
VALUES (1002, 'Hamnen i Sundsvall 1950', 'Sundsvalls hamn på 1950-talet med båtar', '1950', '1955',
        'Sundsvall', 'Foto', 'Färg', 'FOTO.id_1002_fil_liten.jpg', 'FOTO.id_1002_fil_stor.jpg',
        'FOTO.id_1002_fil_original.jpg', 19001, 4);

INSERT INTO FOTO (F_ID, DOKTITEL, KOMMENT_FF, TIDIG, SENAST, F_OPLATS, OBJTYP, F_T_ID, FIL_LITEN, FIL_STOR, FIL_ORIGINAL,
                  NODEID, `OPTIONS`)
VALUES (1003, 'Industribild från Timrå', 'Sågverk i Timrå', '1960', '1962', 'Timrå', 'Foto', 2,
        'FOTO.id_1003_fil_liten.jpg', 'FOTO.id_1003_fil_stor.jpg', 'FOTO.id_1003_fil_original.jpg', 19002, 4);

-- Unpublished photo that must never appear in any response
INSERT INTO FOTO (F_ID, DOKTITEL, KOMMENT_FF, TIDIG, FIL_LITEN, NODEID, `OPTIONS`)
VALUES (1099, 'Draft unpublished photo', 'Stadsvy not yet published', '2024', null, 19099, 0);

--
-- OCM (subject / ämne lookup)
--
INSERT INTO OCM (O_ID, OCMTEXT, OCMKOD, OCMDESC)
VALUES (1, 'Allmänt', 'ALM', 'Allmänt ämne'),
       (10, 'Intervju', 'INT', 'Ljudupptagning av intervju'),
       (20, 'Musik', 'MUS', 'Musikinspelning');

--
-- LJUD
--
INSERT INTO LJUD (LJUD_ID, FILNAMN, LJUD_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, LJUD_T_ID, LJUD_OPLATS, LJUD_O_ID,
                  LJUD_U_E_ID, LJUD_U_J_ID, KOMMENT_LJUD, LJUD_MIME_TYPE, NODEID, `OPTIONS`, DELETEDDATE)
-- OPTIONS = 6 (bit 4 published + bit 2 set) — exercises the bitwise check
VALUES (1, 'intervju_borgmastare_1980.mp3', '/media/ljud/intervju_borgmastare_1980.mp3', 'LJUD', '1980-04-12',
        'Intervju med borgmästaren 1980', 1, 'Sundsvall', 10, 0, 1,
        'Ljudupptagning av intervju med dåvarande borgmästaren',
        'audio/mpeg', 200, 6, null);

INSERT INTO LJUD (LJUD_ID, FILNAMN, LJUD_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, LJUD_T_ID, LJUD_OPLATS, LJUD_O_ID,
                  LJUD_U_E_ID, LJUD_U_J_ID, KOMMENT_LJUD, LJUD_MIME_TYPE, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (2, 'stadskorens_konsert_1985.mp3', '/media/ljud/stadskorens_konsert_1985.mp3', 'LJUD', '1985-06-21',
        'Stadskörens midsommarkonsert 1985', 1, 'Sundsvall', 20, 0, 1,
        'Inspelning av stadskörens midsommarkonsert', 'audio/mpeg', 201, 4, null);

INSERT INTO LJUD (LJUD_ID, FILNAMN, LJUD_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, LJUD_T_ID, LJUD_OPLATS, LJUD_O_ID,
                  LJUD_U_E_ID, LJUD_U_J_ID, KOMMENT_LJUD, LJUD_MIME_TYPE, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (3, 'timra_folkmusik.mp3', '/media/ljud/timra_folkmusik.mp3', 'LJUD', '1975-09-10', 'Folkmusik från Timrå 1975',
        2, 'Timrå', 20, 0, 1, 'Folkmusikinspelning från Timrå', 'audio/mpeg', 202, 4, null);

-- Unpublished audio that should never be returned by search or list endpoints
INSERT INTO LJUD (LJUD_ID, FILNAMN, LJUD_OBJ_FIL, OBJTYP, DATUM, DOKTITEL, LJUD_T_ID, LJUD_OPLATS, LJUD_O_ID,
                  LJUD_U_E_ID, LJUD_U_J_ID, KOMMENT_LJUD, LJUD_MIME_TYPE, NODEID, `OPTIONS`, DELETEDDATE)
VALUES (4, 'unpublished.mp3', '/media/ljud/unpublished.mp3', 'LJUD', '2000-01-01', 'Unpublished audio', 1, 'Sundsvall',
        1, 0, 1, 'This audio is not published', 'audio/mpeg', 203, 0, null);

--
-- TEXT (textarkiv — fjärde mediatypen)
--
INSERT INTO TEXT (ID_ID, DOKDATUM, DOKDATUM_SLUT, DOKTITEL, U_E_ID, U_J_ID, D_T_ID, D_OPLATS, D_O_ID, KOMMENT_DOC, FILNAMN,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
-- OPTIONS = 6 (bit 4 published + bit 2 set) — exercises the bitwise check
VALUES (1001, '1920-01-01', '1925-12-31', 'Minne från Sundsvall stadshus', 0, 1, 16, 'Sundsvall', 20,
        'Handskrivna minnen från stadshuset', 'minne_1920.xml', 'TEXT.id_1001_fil_liten.jpeg',
        'TEXT.id_1001_fil_stor.jpeg', 'TEXT.id_1001_fil_original.jpeg', 'TEXT.id_1001_fil_txt.xml',
        'Detta är ett minne från Sundsvalls stadshus från 1920-talet', 'TEXT.id_1001_fil_xtra.jpeg',
        20001, 6, 'text', null);

INSERT INTO TEXT (ID_ID, DOKDATUM, DOKDATUM_SLUT, DOKTITEL, U_E_ID, U_J_ID, D_T_ID, D_OPLATS, D_O_ID, KOMMENT_DOC, FILNAMN,
                  FIL_LITEN, FIL_STOR, FIL_ORIGINAL, FIL_TXT, XMLTEXT, FIL_XTRA, NODEID, `OPTIONS`, FIL_FORMAT,
                  DELETEDDATE)
VALUES (1002, '1950-06-15', '1950-06-15', 'Brev från Timrå', 0, 1, 2, 'Timrå', 10,
        'Brev från lokal invånare', 'brev_1950.xml', 'TEXT.id_1002_fil_liten.jpeg',
        'TEXT.id_1002_fil_stor.jpeg', null, 'TEXT.id_1002_fil_txt.xml',
        'Brevtext från 1950 om midsommarfirande', null, 20002, 4, 'text', null);

-- Unpublished text that must never appear in any response
INSERT INTO TEXT (ID_ID, DOKTITEL, KOMMENT_DOC, NODEID, `OPTIONS`)
VALUES (1099, 'Draft unpublished text', 'Stadshuset ej publicerad', 20099, 0);

--
-- TEXT_MULTI (extra mediafiler kopplade till TEXT)
--
INSERT INTO TEXT_MULTI (MIID, IID, FIL_LITEN, FIL_STOR, FIL_ORIGINAL)
VALUES (1, 1001, 'TEXT.id_1001.multi_1_fil_liten.jpeg', 'TEXT.id_1001.multi_1_fil_stor.jpeg',
        'TEXT.id_1001.multi_1_fil_original.jpeg'),
       (2, 1001, 'TEXT.id_1001.multi_2_fil_liten.jpeg', 'TEXT.id_1001.multi_2_fil_stor.jpeg', null);

--
-- FOTO_FOTO (foton kopplade till andra foton, t.ex. för-/baksida)
--
INSERT INTO FOTO_FOTO (ID, F_ID1, F_ID2)
VALUES (1, 1001, 1002),
       (2, 1003, 1001);

--
-- FOTO_OCM (kopplar FOTO till OCM-ämne)
--
INSERT INTO FOTO_OCM (ID, F_ID, O_ID)
VALUES (1, 1001, 1),
       (2, 1001, 20);

--
-- MANTAL (mantalslängder) — no OPTIONS/publish column, no parish
--
INSERT INTO MANTAL (ID, OBJEKTSNR, KALLA, MFSTNR1, FSTDEL1, HUSHNR, ORDNR, LOPNR, FNR, YRKREL, RELKOD, MNMNF, MNMNE,
                    KON, FODAR, ANM)
VALUES (1, 'SE/1234', 'MTL', 'Norrmalm 3', '1/2', '3', '1', '12', '7', 'Bonde', 'H', 'Anton', 'Nordin', 'man', '1852',
        'Flyttade in 1875');

INSERT INTO MANTAL (ID, MNMNF, MNMNE, KON, FODAR)
VALUES (2, 'Anna', 'Berg', 'kvinna', '1870');

INSERT INTO MANTAL (ID, MNMNF, MNMNE, KON, FODAR)
VALUES (3, 'Erik', 'Nordin', 'man', '1889');
