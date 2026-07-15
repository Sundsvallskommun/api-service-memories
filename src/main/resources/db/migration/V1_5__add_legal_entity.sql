CREATE TABLE KATEGORI
(
    KAT_ID  bigint NOT NULL PRIMARY KEY,
    KATKOD  varchar(7),
    KATNAMN varchar(60)
) ENGINE = InnoDB;

CREATE TABLE JURPERS
(
    J_ID            bigint NOT NULL PRIMARY KEY,
    JURPERS         varchar(256),
    ALTNAMN         varchar(256),
    T_ID            bigint DEFAULT 1,
    OPLATS          varchar(64),
    STARTDATUM      varchar(10),
    SLUTDATUM       varchar(10),
    HUVUDMAN        varchar(256),
    KOMMENT_JURPERS varchar(4000),
    HISTORIA        varchar(256),
    KAT_ID          bigint DEFAULT 1,
    `OPTIONS`       bigint DEFAULT 0,
    DELETEDDATE     date,
    INDEX IDX_JURPERS_NAMN (JURPERS(191)),
    INDEX IDX_JURPERS_KAT (KAT_ID)
) ENGINE = InnoDB;
