CREATE TABLE FILM
(
    FILM_ID        bigint NOT NULL PRIMARY KEY,
    FILNAMN        varchar(256),
    FILM_OBJ_FIL   varchar(256),
    OBJTYP         varchar(8),
    DATUM          varchar(256),
    DOKTITEL       varchar(2256),
    FILM_T_ID      bigint DEFAULT 1,
    FILM_OPLATS    varchar(64),
    FILM_O_ID      bigint DEFAULT 1,
    FILM_U_E_ID    bigint DEFAULT 0,
    FILM_U_J_ID    bigint DEFAULT 1,
    KOMMENT_FILM   varchar(4000),
    FILM_MIME_TYPE varchar(50),
    ASV            varchar(20),
    NODEID         bigint,
    `OPTIONS`      bigint DEFAULT 0,
    DELETEDDATE    date
) ENGINE = InnoDB;

CREATE TABLE PUBL_TYP
(
    ID        int NOT NULL PRIMARY KEY,
    PUBLIKTYP varchar(256)
) ENGINE = InnoDB;

CREATE TABLE PUBL
(
    P_ID          bigint NOT NULL PRIMARY KEY,
    FILNAMN       varchar(256),
    PUBLIKTYP     varchar(40),
    DATUM         varchar(10),
    TIDTITEL      varchar(128),
    TIDNR         varchar(5),
    TIDSIDA       varchar(3),
    BF_J_ID       bigint     DEFAULT 1,
    FORLAG_T_ID   bigint     DEFAULT 1,
    FORLAG_OPLATS varchar(64),
    DOKDATUM      varchar(10),
    DOKTITEL      varchar(256),
    F_E_ID        bigint     DEFAULT 0,
    R_E_ID        bigint     DEFAULT 0,
    U_J_ID        bigint     DEFAULT 1,
    U_E_ID        bigint     DEFAULT 0,
    P_T_ID        bigint     DEFAULT 1,
    P_OPLATS      varchar(64),
    ME_O_ID       bigint     DEFAULT 1,
    KOMMENT_PUBL  varchar(4000),
    FIL_LITEN     varchar(256),
    FIL_STOR      varchar(256),
    FIL_ORIGINAL  varchar(256),
    FIL_TXT       varchar(256),
    XMLTEXT       longtext,
    FIL_XTRA      varchar(256),
    NODEID        bigint,
    `OPTIONS`     bigint     DEFAULT 0,
    FIL_FORMAT    varchar(4) DEFAULT 'text',
    DELETEDDATE   date,
    FULLTEXT INDEX ft_publ_doktitel_komment_xmltext (DOKTITEL, KOMMENT_PUBL, XMLTEXT)
) ENGINE = InnoDB;


ALTER TABLE FILM
    ADD FULLTEXT INDEX ft_film_doktitel_komment (DOKTITEL, KOMMENT_FILM);
