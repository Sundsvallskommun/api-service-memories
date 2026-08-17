CREATE TABLE TBL_NODETYPES
(
    ID          bigint NOT NULL PRIMARY KEY,
    PARENTID    bigint,
    NAME        varchar(100),
    NODETYPEIDS varchar(100)
) ENGINE = InnoDB;

CREATE TABLE TBL_NODES
(
    ID          bigint NOT NULL PRIMARY KEY,
    PARENTID    bigint,
    NAME        varchar(1000),
    NODETYPEID  bigint NOT NULL,
    RUID        bigint,
    STARTYEAR   int,
    STOPYEAR    int,
    `OPTIONS`   bigint DEFAULT 1 NOT NULL,
    SORT        int    DEFAULT 0 NOT NULL,
    DESCRIPTION longtext,
    SUBITEMS    int    DEFAULT 0 NOT NULL,
    SUBITEMS_4  int    DEFAULT 0 NOT NULL,
    DELETEDDATE date,
    FULLTEXT INDEX ft_tbl_nodes_description (DESCRIPTION),
    INDEX IDX_TBL_NODES_PARENT (PARENTID)
) ENGINE = InnoDB;

CREATE TABLE TBL_NODEATTRIBUTES
(
    NODEID        bigint NOT NULL PRIMARY KEY,
    FIELD1        bigint,
    FIELD2        bigint,
    FIELD3        bigint,
    FIELD4        bigint,
    FIELD5        bigint,
    FIELD6        varchar(100),
    FIELD7        decimal(10, 2),
    FIELD8        int,
    FIELD9        bigint,
    FIELD10       varchar(100),
    FIELD11       varchar(100),
    FIELD12       varchar(100),
    FIELD13       varchar(100),
    FIELD14       varchar(100),
    FIELD15       varchar(100),
    SUBITEMS      int     DEFAULT 0 NOT NULL,
    FIELD16       varchar(256),
    FIELD16FORMAT char(4) DEFAULT 'text'
) ENGINE = InnoDB;
