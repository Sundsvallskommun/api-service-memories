CREATE TABLE TEXT
(
    ID              bigint NOT NULL PRIMARY KEY,
    DOKDATUM        varchar(10),
    DOKDATUM_SLUT   varchar(10),
    DOKTITEL        varchar(256),
    U_E_ID          bigint     DEFAULT 0,
    U_T_ID          bigint     DEFAULT 1,
    D_T_ID          bigint     DEFAULT 1,
    D_OPLATS        varchar(64),
    KOMMENT_DOC     varchar(4000),
    FILNAMN         varchar(256),
    FIL_LITEN       varchar(256),
    FIL_STOR        varchar(256),
    FIL_ORIGINAL    varchar(256),
    FIL_TXT         varchar(256),
    XMLTEXT         longtext,
    FIL_XTRA        varchar(256),
    NODEID          bigint,
    `OPTIONS`       bigint     DEFAULT 0,
    FIL_FORMAT      varchar(4) DEFAULT 'text',
    DELETEDDATE     date,
    FULLTEXT INDEX ft_text_doktitel_komment_xmltext (DOKTITEL, KOMMENT_DOC, XMLTEXT)
) ENGINE = InnoDB;

CREATE TABLE FOTO_FOTO
(
    ID    bigint NOT NULL PRIMARY KEY,
    F_ID1 bigint NOT NULL,
    F_ID2 bigint NOT NULL,
    INDEX ix_foto_foto_f_id1 (F_ID1),
    INDEX ix_foto_foto_f_id2 (F_ID2)
) ENGINE = InnoDB;

CREATE TABLE TEXT_MULTI
(
    ID           bigint NOT NULL PRIMARY KEY,
    MED          bigint NOT NULL,
    FIL_LITEN    varchar(256),
    FIL_STOR     varchar(256),
    FIL_ORIGINAL varchar(256),
    INDEX ix_text_multi_med (MED)
) ENGINE = InnoDB;

CREATE TABLE FOTO_OCM
(
    ID   bigint NOT NULL PRIMARY KEY,
    F_ID bigint NOT NULL,
    O_ID bigint NOT NULL,
    INDEX ix_foto_ocm_f_id (F_ID),
    INDEX ix_foto_ocm_o_id (O_ID)
) ENGINE = InnoDB;

CREATE TABLE PUBL_TYP
(
    ID        bigint NOT NULL PRIMARY KEY,
    PUBLIKTYP varchar(40)
) ENGINE = InnoDB;
