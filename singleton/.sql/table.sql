create table ghosts.credit_transactions
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    user_id          bigint                             not null comment '用户ID',
    amount           bigint                             not null comment '积分数量',
    description      varchar(200)                       null comment '描述',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime                           null,
    transaction_type varchar(255)                       null
)
    comment '积分流水表' collate = utf8mb4_unicode_ci;

create index idx_create_time
    on ghosts.credit_transactions (create_time);

create index idx_user_id
    on ghosts.credit_transactions (user_id);

create table ghosts.redemption_codes
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    code            varchar(32)                        not null comment '兑换码',
    credits_amount  bigint                             not null comment '积分数量',
    status          tinyint  default 1                 null comment '状态：1-有效 0-已使用 -1-已禁用',
    used_by_user_id bigint                             null comment '使用者用户ID',
    used_time       datetime                           null comment '使用时间',
    expire_time     datetime                           null comment '过期时间',
    description     varchar(255)                       null comment '描述',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint code
        unique (code)
)
    comment '兑换码表' collate = utf8mb4_unicode_ci;

create index idx_code
    on ghosts.redemption_codes (code);

create index idx_expire_time
    on ghosts.redemption_codes (expire_time);

create index idx_status
    on ghosts.redemption_codes (status);

create table ghosts.user
(
    id          bigint auto_increment
        primary key,
    email       varchar(255)                                     null,
    nickname    varchar(255)                                     null,
    password    varchar(255)                                     not null,
    avatar      varchar(255)                                     null,
    role        enum ('USER', 'ADMIN') default 'USER'            not null,
    create_time datetime               default CURRENT_TIMESTAMP not null,
    update_time datetime               default CURRENT_TIMESTAMP not null
)
    comment '用户表';

create table ghosts.user_credits
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    user_id           bigint                             not null comment '用户ID',
    total_credits     bigint   default 0                 null comment '总积分',
    available_credits bigint   default 0                 null comment '可用积分',
    frozen_credits    bigint   default 0                 null comment '冻结积分',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint user_id
        unique (user_id)
)
    comment '用户积分表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on ghosts.user_credits (user_id);

create table ghosts.workflow
(
    id                   bigint auto_increment
        primary key,
    workflow_category_id int                                not null,
    name                 varchar(100)                       null,
    description          varchar(255)                       not null,
    json                 json                               not null,
    url                  varchar(255)                       not null,
    create_time          datetime default CURRENT_TIMESTAMP not null,
    update_time          datetime default CURRENT_TIMESTAMP not null,
    credits_deducted     int                                null
)
    comment '工作流表';

create table ghosts.workflow_category
(
    id          int auto_increment
        primary key,
    name        varchar(255) not null,
    url         varchar(255) not null,
    create_time datetime     null,
    update_time datetime     null
)
    comment '工作流类别表';

create table ghosts.workflow_form
(
    id          bigint auto_increment
        primary key,
    workflow_id bigint               not null,
    type        varchar(100)         null,
    tips        varchar(255)         null,
    node_key    varchar(100)         null,
    inputs      varchar(100)         not null,
    required    tinyint(1) default 0 null,
    options     json                 null,
    size        int                  null,
    template    varchar(255)         null
)
    comment '工作流表单表';

create table ghosts.workflow_output
(
    id          bigint auto_increment
        primary key,
    workflow_id bigint       null,
    node_key    varchar(100) null,
    type        varchar(255) null
)
    comment '工作流输出节点表';

create table ghosts.workflow_result
(
    id            bigint auto_increment
        primary key,
    task_id       varchar(255)                       not null,
    workflow_name varchar(255)                       not null,
    user_id       bigint                             not null,
    type          varchar(100)                       not null,
    url           varchar(255)                       null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_time   datetime default CURRENT_TIMESTAMP not null
)
    comment '工作流作品表';

