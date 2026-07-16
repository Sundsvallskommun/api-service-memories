CREATE TABLE PERSON
(
    P_ID         bigint NOT NULL PRIMARY KEY,
    PNR          varchar(10),
    ENAMN        varchar(510),
    FNAMN        varchar(510),
    KON          varchar(510),
    FODDAT       varchar(10),
    FODFRS       varchar(510),
    DODDAT       varchar(10),
    YRKEE        varchar(510),
    YAGARE       varchar(510),
    YRKER        varchar(100),
    FRNFRS       varchar(510),
    TILFRS       varchar(510),
    KALLA        varchar(100),
    KOMMENT_PERS varchar(4000),
    BIOGRAFI     varchar(256),
    `OPTIONS`    bigint DEFAULT 0,
    DELETEDDATE  date,
    INDEX IDX_PERSON_ENAMN (ENAMN(191)),
    INDEX IDX_PERSON_FNAMN (FNAMN(191)),
    INDEX IDX_PERSON_FODDAT (FODDAT)
) ENGINE = InnoDB;
